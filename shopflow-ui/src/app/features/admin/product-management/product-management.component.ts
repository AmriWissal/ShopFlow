import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../../core/services/product.service';
import { Product, Category } from '../../../core/models';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-product-management',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatDialogModule, FormsModule, ReactiveFormsModule],
  templateUrl: './product-management.component.html',
  styleUrls: ['./product-management.component.css']
})
export class ProductManagementComponent implements OnInit {
  private productService = inject(ProductService);
  private fb = inject(FormBuilder);
  private snackBar = inject(MatSnackBar);

  products: Product[] = [];
  categories: Category[] = [];
  showModal = false;
  editingId: number | null = null;
  selectedFiles: Array<{file: File, preview: string, name: string}> = []; // Fichiers sélectionnés
  showCategoryDropdown = false; // Pour gérer l'affichage du dropdown

  // Pagination
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;
  Math = Math; // Pour utiliser Math.min dans le template

  productForm = this.fb.group({
    name: ['', Validators.required],
    description: ['', Validators.required],
    price: [0, [Validators.required, Validators.min(0)]],
    stock: [0, [Validators.required, Validators.min(0)]],
    active: [true],
    categoryIds: [[] as number[], [Validators.required, Validators.minLength(1)]],
    images: [[] as string[]]
  });

  ngOnInit() {
    this.loadProducts();
    this.productService.getCategories().subscribe(res => this.categories = res);
  }

  loadProducts() {
    this.productService.getSellerProducts(this.currentPage, this.pageSize).subscribe({
      next: (res: any) => {
        // Le backend retourne un objet Page avec content, totalElements, etc.
        if (res.content) {
          this.products = res.content;
          this.totalElements = res.totalElements;
          this.totalPages = res.totalPages;
          this.currentPage = res.number;
        } else {
          // Fallback si c'est une liste simple
          this.products = Array.isArray(res) ? res : [];
        }
      },
      error: (err: any) => console.error('Erreur chargement produits vendeur', err)
    });
  }

  goToPage(page: number) {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadProducts();
    }
  }

  nextPage() {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadProducts();
    }
  }

  previousPage() {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadProducts();
    }
  }

  get pageNumbers(): number[] {
    const pages: number[] = [];
    const maxPagesToShow = 5;
    let startPage = Math.max(0, this.currentPage - Math.floor(maxPagesToShow / 2));
    let endPage = Math.min(this.totalPages - 1, startPage + maxPagesToShow - 1);
    
    if (endPage - startPage < maxPagesToShow - 1) {
      startPage = Math.max(0, endPage - maxPagesToShow + 1);
    }
    
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    return pages;
  }

  openAddModal() {
    this.editingId = null;
    this.selectedFiles = []; // Réinitialise les fichiers sélectionnés
    this.productForm.reset({ active: true, price: 0, stock: 0, categoryIds: [], images: [] });
    this.showModal = true;
  }

  editProduct(product: Product) {
    this.editingId = product.id;
    this.selectedFiles = []; // Réinitialise les fichiers (on va les remplir avec les images existantes pour la prévisualisation)
    
    // Pré-remplir selectedFiles avec les images existantes pour l'affichage dans la modale
    if (product.images && product.images.length > 0) {
      this.selectedFiles = product.images.map(url => ({
        file: null as any, // Pas de fichier réel car c'est une URL existante
        preview: url,
        name: 'Image existante'
      }));
    }

    this.productForm.patchValue({
      name: product.name,
      description: product.description,
      price: product.price,
      stock: product.stock,
      active: product.active,
      categoryIds: product.categories.map(c => c.id),
      images: product.images || []
    });
    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
  }

  saveProduct() {
    if (this.productForm.valid) {
      const productData = this.productForm.value;
      
      // Si des fichiers ont été sélectionnés, convertir en base64 ou URLs
      if (this.selectedFiles.length > 0) {
        productData.images = this.selectedFiles.map(f => f.preview);
      }
      
      const obs = this.editingId 
        ? this.productService.updateProduct(this.editingId, productData)
        : this.productService.createProduct(productData);

      obs.subscribe({
        next: () => {
          this.snackBar.open(`✅ Produit ${this.editingId ? 'mis à jour' : 'créé'} !`, 'Fermer', { 
            duration: 3000,
            panelClass: ['success-snackbar'],
            horizontalPosition: 'end',
            verticalPosition: 'top'
          });
          this.loadProducts();
          this.closeModal();
        },
        error: (err: any) => {
          console.error('Erreur sauvegarde produit', err);
          this.snackBar.open('❌ Erreur lors de la sauvegarde', 'Fermer', { 
            duration: 3000,
            panelClass: ['error-snackbar'],
            horizontalPosition: 'end',
            verticalPosition: 'top'
          });
        }
      });
    }
  }

  /**
   * Gère la sélection de fichiers images depuis l'ordinateur.
   */
  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const files = Array.from(input.files);
      
      files.forEach(file => {
        if (file.type.startsWith('image/')) {
          // Vérifier la taille du fichier (max 300MB)
          if (file.size > 300 * 1024 * 1024) {
            this.snackBar.open('❌ ' + file.name + ' est trop volumineux (max 300MB)', 'OK', { 
              duration: 3000,
              panelClass: ['error-snackbar'],
              horizontalPosition: 'end',
              verticalPosition: 'top'
            });
            return;
          }
          
          const reader = new FileReader();
          reader.onload = (e: any) => {
            // Compresser l'image avant de l'ajouter
            this.compressImage(e.target.result, file.name);
          };
          reader.readAsDataURL(file);
        } else {
          this.snackBar.open('❌ ' + file.name + ' n\'est pas une image valide', 'OK', { 
            duration: 3000,
            panelClass: ['error-snackbar'],
            horizontalPosition: 'end',
            verticalPosition: 'top'
          });
        }
      });
    }
  }

  /**
   * Compresse une image pour réduire sa taille.
   */
  compressImage(base64: string, fileName: string) {
    const img = new Image();
    img.onload = () => {
      const canvas = document.createElement('canvas');
      const ctx = canvas.getContext('2d');
      
      // Redimensionner l'image (max 800x800)
      let width = img.width;
      let height = img.height;
      const maxSize = 800;
      
      if (width > height) {
        if (width > maxSize) {
          height = (height * maxSize) / width;
          width = maxSize;
        }
      } else {
        if (height > maxSize) {
          width = (width * maxSize) / height;
          height = maxSize;
        }
      }
      
      canvas.width = width;
      canvas.height = height;
      
      ctx?.drawImage(img, 0, 0, width, height);
      
      // Convertir en base64 avec qualité réduite (0.7 = 70%)
      const compressedBase64 = canvas.toDataURL('image/jpeg', 0.7);
      
      this.selectedFiles.push({
        file: null as any,
        preview: compressedBase64,
        name: fileName
      });
    };
    img.src = base64;
  }

  /**
   * Supprime un fichier de la sélection.
   */
  removeFile(index: number) {
    this.selectedFiles.splice(index, 1);
  }

  /**
   * Gère l'ajout d'URLs d'images.
   */
  onImagesUrlChange(event: any) {
    const value = event.target.value;
    const images = value.split('\n').filter((url: string) => url.trim() !== '');
    this.productForm.patchValue({ images });
  }

  /**
   * Gère les erreurs de chargement d'image en affichant un placeholder stable.
   */
  onImageError(event: Event) {
    const img = event.target as HTMLImageElement;
    img.src = 'https://placehold.co/100x100/f0fdf4/16a34a?text=Produit';
  }

  deleteProduct(id: number) {
    if (confirm('Voulez-vous vraiment supprimer ce produit ?')) {
      this.productService.deleteProduct(id).subscribe({
        next: () => {
          this.snackBar.open('✅ Produit supprimé', 'Fermer', { 
            duration: 3000,
            panelClass: ['success-snackbar'],
            horizontalPosition: 'end',
            verticalPosition: 'top'
          });
          this.loadProducts();
        },
        error: (err: any) => {
          console.error('Erreur suppression produit', err);
          this.snackBar.open('❌ Erreur lors de la suppression', 'Fermer', { 
            duration: 3000,
            panelClass: ['error-snackbar'],
            horizontalPosition: 'end',
            verticalPosition: 'top'
          });
        }
      });
    }
  }

  /**
   * Toggle l'affichage du dropdown des catégories.
   */
  toggleCategoryDropdown() {
    this.showCategoryDropdown = !this.showCategoryDropdown;
  }

  /**
   * Ferme le dropdown quand on clique sur le backdrop du modal.
   */
  onModalBackdropClick(event: Event) {
    this.showCategoryDropdown = false;
  }

  /**
   * Vérifie si une catégorie est sélectionnée.
   */
  isCategorySelected(categoryId: number): boolean {
    const selectedIds = this.productForm.get('categoryIds')?.value || [];
    return selectedIds.includes(categoryId);
  }

  /**
   * Toggle la sélection d'une catégorie (sélection unique demandée).
   */
  toggleCategory(categoryId: number) {
    const currentIds = this.productForm.get('categoryIds')?.value || [];
    
    // Si la catégorie est déjà sélectionnée, on vide (optionnel, ou on garde)
    if (currentIds.includes(categoryId)) {
      this.productForm.patchValue({ categoryIds: [] });
    } else {
      // On remplace par la nouvelle sélection (une seule autorisée)
      this.productForm.patchValue({ categoryIds: [categoryId] });
    }
    
    this.productForm.get('categoryIds')?.markAsDirty();
    // On ferme le dropdown car une seule sélection est possible
    this.showCategoryDropdown = false;
  }

  /**
   * Retourne le texte à afficher dans le bouton de sélection.
   */
  getSelectedCategoriesText(): string {
    const selectedIds = this.productForm.get('categoryIds')?.value || [];
    
    if (selectedIds.length === 0) {
      return 'Sélectionnez une catégorie';
    }
    
    const category = this.categories.find(c => c.id === selectedIds[0]);
    return category ? category.name : 'Sélectionnez une catégorie';
  }
}