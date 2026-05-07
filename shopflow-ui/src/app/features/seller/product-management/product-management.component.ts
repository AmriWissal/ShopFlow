import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../../core/services/product.service';
import { Product, Category } from '../../../core/models';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-seller-product-management',
  standalone: true,
  imports: [
    CommonModule, 
    MatIconModule, 
    FormsModule, 
    ReactiveFormsModule, 
    MatSelectModule, 
    MatFormFieldModule, 
    MatInputModule
  ],
  templateUrl: './product-management.component.html',
  styleUrls: ['./product-management.component.css']
})
export class SellerProductManagementComponent implements OnInit {
  private productService = inject(ProductService); // Service des produits
  private fb = inject(FormBuilder); // Constructeur de formulaire
  private snackBar = inject(MatSnackBar); // Notifications

  products: Product[] = []; // Liste des produits du vendeur
  categories: Category[] = []; // Liste des catégories
  showModal = false; // État d'affichage de la modale
  editingId: number | null = null; // ID du produit en cours d'édition
  productForm: FormGroup; // Formulaire de produit
  loading = false; // État de chargement
  selectedFiles: Array<{file: File, preview: string, name: string}> = []; // Fichiers sélectionnés
  showCategoryDropdown = false; // Pour gérer l'affichage du dropdown

  // Pagination
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;
  Math = Math; // Pour utiliser Math.min dans le template

  constructor() {
    this.productForm = this.fb.group({
      name: ['', [Validators.required]],
      description: ['', [Validators.required]],
      price: [0, [Validators.required, Validators.min(0)]],
      promoPrice: [null],
      stock: [0, [Validators.required, Validators.min(0)]],
      categoryIds: [[], [Validators.required, Validators.minLength(1)]],
      images: [[]]
    });
  }

  ngOnInit() {
    this.loadProducts(); // Charge les produits à l'initialisation
    this.loadCategories(); // Charge les catégories
  }

  /**
   * Charge les produits appartenant au vendeur.
   */
  loadProducts() {
    this.loading = true;
    this.productService.getSellerProducts(this.currentPage, this.pageSize).subscribe({
      next: (data: any) => {
        // Le backend retourne un objet Page avec content, totalElements, etc.
        if (data.content) {
          this.products = data.content;
          this.totalElements = data.totalElements;
          this.totalPages = data.totalPages;
          this.currentPage = data.number;
        } else {
          // Fallback si c'est une liste simple
          this.products = Array.isArray(data) ? data : [];
        }
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Erreur chargement produits vendeur', err);
        this.loading = false;
      }
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

  /**
   * Charge la liste des catégories disponibles.
   */
  loadCategories() {
    this.productService.getCategories().subscribe(data => {
      this.categories = data;
    });
  }

  onImagesUrlChange(event: any) {
    const value = event.target.value;
    const images = value.split('\n').filter((url: string) => url.trim() !== '');
    this.productForm.patchValue({ images });
  }

  /**
   * Gère la sélection de fichiers images depuis l'ordinateur.
   */
  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const files = Array.from(input.files);
      
      files.forEach(file => {
        // Vérifier que c'est bien une image
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
          
          // Créer une preview
          const reader = new FileReader();
          reader.onload = (e: any) => {
            // Compresser l'image avant de l'ajouter
            this.compressImage(e.target.result, file.name);
          };
          reader.readAsDataURL(file);
        } else {
          this.snackBar.open(`❌ ${file.name} n'est pas une image valide`, 'OK', { 
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
   * Ouvre la modale pour ajouter un nouveau produit.
   */
  openAddModal() {
    this.editingId = null;
    this.selectedFiles = []; // Réinitialise les fichiers sélectionnés
    this.productForm.reset({
      price: 0,
      stock: 0,
      categoryIds: [],
      images: []
    });
    this.showModal = true;
  }

  /**
   * Ouvre la modale pour modifier un produit existant.
   */
  editProduct(product: Product) {
    this.editingId = product.id;
    this.selectedFiles = []; // Réinitialise les fichiers
    
    // Pré-remplir selectedFiles avec les images existantes pour la prévisualisation
    if (product.images && product.images.length > 0) {
      this.selectedFiles = product.images.map(url => ({
        file: null as any,
        preview: url,
        name: 'Image existante'
      }));
    }

    this.productForm.patchValue({
      name: product.name,
      description: product.description,
      price: product.price,
      promoPrice: product.promoPrice,
      stock: product.stock,
      categoryIds: product.categories.map(c => c.id),
      images: product.images
    });
    this.showModal = true;
  }

  /**
   * Soumet le formulaire de création ou de modification.
   */
  onSubmit() {
    if (this.productForm.valid) {
      const productData = this.productForm.value;
      
      // Si des fichiers ont été sélectionnés, convertir en base64 ou URLs
      // Note: Pour une vraie implémentation, il faudrait uploader vers un serveur
      // Pour l'instant, on utilise les previews base64
      if (this.selectedFiles.length > 0) {
        productData.images = this.selectedFiles.map(f => f.preview);
      }
      
      if (this.editingId) {
        this.productService.updateProduct(this.editingId, productData).subscribe({
          next: () => {
            this.handleSuccess('Produit mis à jour');
          },
          error: (err) => this.handleError(err)
        });
      } else {
        this.productService.createProduct(productData).subscribe({
          next: () => {
            this.handleSuccess('Produit ajouté');
          },
          error: (err) => this.handleError(err)
        });
      }
    }
  }

  private handleSuccess(message: string) {
    this.loadProducts();
    this.closeModal();
    this.snackBar.open(`✅ ${message}`, 'OK', { 
      duration: 3000,
      panelClass: ['success-snackbar'],
      horizontalPosition: 'end',
      verticalPosition: 'top'
    });
  }

  private handleError(error: any) {
    console.error(error);
    this.snackBar.open('❌ Une erreur est survenue', 'OK', { 
      duration: 3000,
      panelClass: ['error-snackbar'],
      horizontalPosition: 'end',
      verticalPosition: 'top'
    });
  }

  /**
   * Désactive ou supprime un produit.
   */
  deleteProduct(id: number) {
    if (confirm('Voulez-vous vraiment supprimer ce produit ?')) {
      this.productService.deleteProduct(id).subscribe({
        next: () => {
          this.snackBar.open('✅ Produit supprimé', 'OK', { 
            duration: 3000,
            panelClass: ['success-snackbar'],
            horizontalPosition: 'end',
            verticalPosition: 'top'
          });
          this.loadProducts();
        }
      });
    }
  }

  closeModal() {
    this.showModal = false;
  }

  /**
   * Gère les erreurs de chargement d'image en affichant un placeholder stable.
   */
  onImageError(event: Event) {
    const img = event.target as HTMLImageElement;
    img.src = 'https://placehold.co/100x100/f0fdf4/16a34a?text=Produit';
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
    
    // Si la catégorie est déjà sélectionnée, on vide
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
