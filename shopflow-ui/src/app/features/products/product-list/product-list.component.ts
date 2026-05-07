import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { Product, Category } from '../../../core/models';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatSliderModule } from '@angular/material/slider';
import { MatSelectModule } from '@angular/material/select';
import { CartService } from '../../../core/services/cart.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, MatIconModule, MatSliderModule, MatSelectModule, MatSnackBarModule],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent implements OnInit {
  productService = inject(ProductService); // Service pour les produits
  cartService = inject(CartService); // Service pour le panier
  router = inject(Router); // Pour la navigation
  snackBar = inject(MatSnackBar); // Pour les messages
  route = inject(ActivatedRoute); // Pour lire les paramètres d'URL

  products: Product[] = []; // Liste des produits à afficher
  categories: Category[] = []; // Liste des catégories pour le filtre
  loading = true; // État de chargement
  view: 'grid' | 'list' = 'grid'; // Mode d'affichage
  totalPages = 0; // Nombre total de pages (pagination backend)
  
  // Subject pour gérer le debounce de la recherche
  private searchSubject = new Subject<string>();

  // Objet regroupant tous les filtres actifs
  filters = {
    query: '', // Recherche textuelle
    categoryId: '', // ID de la catégorie
    minRating: 0, // Note minimale
    promoOnly: false, // Produits en promo uniquement
    page: 0, // Index de la page
    size: 8 // Taille de la page (8 produits = 2 lignes de 4)
  };

  ngOnInit() {
    // Charge les catégories pour le menu de filtrage
    this.productService.getCategories().subscribe(cats => {
      this.categories = cats;
      
      // Une fois les catégories chargées, traite les paramètres d'URL
      this.route.queryParams.subscribe(params => {
        if (params['category']) {
          // Trouve la catégorie par son nom et utilise son ID
          const category = this.categories.find(cat => cat.name === params['category']);
          if (category) {
            this.filters.categoryId = category.id.toString();
          }
        }
        if (params['q']) this.filters.query = params['q']; // Support de la recherche via URL
        this.loadProducts(); // Charge les produits correspondants
      });
    });
    
    // Configure le debounce pour la recherche (500ms)
    this.searchSubject.pipe(
      debounceTime(500),
      distinctUntilChanged()
    ).subscribe(() => {
      this.filters.page = 0; // Réinitialise la page lors d'une nouvelle recherche
      this.loadProducts();
    });
  }

  /**
   * Appelle le service pour charger les produits selon les filtres.
   */
  loadProducts() {
    this.loading = true; // Début chargement
    this.productService.getProducts(this.filters).subscribe({
      next: (res: any) => {
        this.products = res.content || [];
        this.totalPages = res.totalPages || 1;
        this.loading = false; // Fin chargement
      },
      error: (err: any) => {
        console.error('Erreur chargement produits:', err);
        this.products = [];
        this.loading = false;
      }
    });
  }

  /**
   * Déclenche la recherche avec debounce
   */
  onSearchChange() {
    this.searchSubject.next(this.filters.query);
  }

  /**
   * Réinitialise tous les filtres à leurs valeurs par défaut.
   */
  resetFilters() {
    this.filters = {
      query: '',
      categoryId: '',
      minRating: 0,
      promoOnly: false,
      page: 0,
      size: 8
    };
    this.loadProducts(); // Rechargement
  }

  /**
   * Vérifie si un produit est en stock
   */
  isInStock(product: Product): boolean {
    return product.stock > 0;
  }

  /**
   * Retourne le texte du statut de stock
   */
  getStockStatus(product: Product): string {
    if (product.stock === 0) return 'Rupture';
    if (product.stock < 5) return 'Stock limité';
    return 'En Stock';
  }

  /**
   * Retourne la classe CSS pour le statut de stock
   */
  getStockClass(product: Product): string {
    if (product.stock === 0) return 'text-red-500';
    if (product.stock < 5) return 'text-orange-500';
    return 'text-green-500';
  }

  /**
   * Gère le changement de page de la pagination.
   */
  changePage(page: number) {
    this.filters.page = page; // Met à jour l'index
    this.loadProducts(); // Recharge la page correspondante
  }

  /**
   * Vérifie si un produit peut être acheté
   */
  private canPurchase(product: Product): boolean {
    if (!this.isInStock(product)) {
      this.snackBar.open(`❌ Désolé, ${product.name} est actuellement en rupture de stock`, 'Fermer', {
        duration: 4000,
        panelClass: ['error-snackbar'],
        horizontalPosition: 'end',
        verticalPosition: 'top'
      });
      return false;
    }
    return true;
  }

  /**
   * Ajoute un produit au panier directement depuis la liste.
   */
  addToCart(event: Event, product: Product) {
    event.preventDefault(); // Empêche le comportement par défaut du lien
    event.stopPropagation(); // Évite de naviguer vers le détail du produit

    if (!this.canPurchase(product)) return;

    this.cartService.addToCart(product.id, 1).subscribe({
      next: () => {
        this.snackBar.open(`✅ ${product.name} ajouté au panier !`, 'OK', { 
          duration: 3000,
          panelClass: ['success-snackbar'],
          horizontalPosition: 'end',
          verticalPosition: 'top'
        });
      },
      error: (err: any) => {
        // Redirige vers login si non connecté
        this.snackBar.open('⚠️ Veuillez vous connecter pour ajouter au panier', 'Connexion', { 
          duration: 5000,
          panelClass: ['warning-snackbar'],
          horizontalPosition: 'end',
          verticalPosition: 'top'
        })
          .onAction().subscribe(() => this.router.navigate(['/auth/login']));
      }
    });
  }

  /**
   * Ajoute au panier et redirige immédiatement vers le checkout.
   */
  buyNow(event: Event, product: Product) {
    event.preventDefault();
    event.stopPropagation();

    if (!this.canPurchase(product)) return;

    this.cartService.addToCart(product.id, 1).subscribe({
      next: () => {
        this.router.navigate(['/checkout']); // Navigation vers le paiement
      },
      error: (err: any) => {
        this.snackBar.open('⚠️ Veuillez vous connecter pour acheter', 'Connexion', { 
          duration: 5000,
          panelClass: ['warning-snackbar'],
          horizontalPosition: 'end',
          verticalPosition: 'top'
        })
          .onAction().subscribe(() => this.router.navigate(['/auth/login']));
      }
    });
  }

  /**
   * Retourne le nom de la catégorie sélectionnée ou "Toutes les catégories"
   */
  getSelectedCategoryName(): string {
    if (!this.filters.categoryId) {
      return 'Toutes les catégories';
    }
    const selectedCategory = this.categories.find(cat => cat.id.toString() === this.filters.categoryId);
    return selectedCategory ? selectedCategory.name : 'Toutes les catégories';
  }
}