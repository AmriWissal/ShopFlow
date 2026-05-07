import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { CartService } from '../../../core/services/cart.service';
import { ReviewService } from '../../../core/services/review.service';
import { Product, Review } from '../../../core/models';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-product-details',
  standalone: true,
  imports: [CommonModule, RouterModule, MatIconModule, MatButtonModule, FormsModule, MatSnackBarModule],
  templateUrl: './product-details.component.html',
  styleUrls: ['./product-details.component.css']
})
export class ProductDetailsComponent implements OnInit {
  private route = inject(ActivatedRoute); // Récupère les paramètres de la route (ID)
  private productService = inject(ProductService); // Service pour charger le produit
  private cartService = inject(CartService); // Service pour ajouter au panier
  private reviewService = inject(ReviewService); // Service pour charger les avis
  private snackBar = inject(MatSnackBar); // Notifications utilisateur

  product: Product | null = null; // Données complètes du produit
  reviews: Review[] = []; // Liste des avis clients approuvés
  loading = true; // Indicateur de chargement global
  error = false; // Indicateur d'erreur
  selectedImage: string | null = null; // Image affichée dans la visionneuse
  selectedVariant: any = null; // Variante choisie par l'utilisateur
  quantity = 1; // Quantité sélectionnée par défaut

  // Formulaire d'avis
  newReview = {
    rating: 5,
    comment: ''
  };
  submittingReview = false;

  /**
   * Calcule la note moyenne des avis approuvés
   */
  get averageRating(): number {
    if (this.reviews.length === 0) return 0;
    const sum = this.reviews.reduce((acc, review) => acc + review.note, 0);
    return Math.round((sum / this.reviews.length) * 10) / 10; // Arrondi à 1 décimale
  }

  /**
   * Retourne le nombre d'avis certifiés (approuvés)
   */
  get certifiedReviewsCount(): number {
    return this.reviews.length;
  }

  ngOnInit() {
    // S'abonne aux changements d'ID dans l'URL
    this.route.params.subscribe(params => {
      const id = params['id'];
      if (id) {
        this.loadProductDetails(Number(id)); // Charge les données du produit
      }
    });
  }

  /**
   * Charge les détails du produit et ses avis de manière asynchrone.
   */
  loadProductDetails(id: number) {
    this.loading = true;
    this.error = false;
    
    this.productService.getProductById(id).subscribe({
      next: (prod: Product) => {
        this.setProduct(prod);
        
        // Charge les avis une fois le produit récupéré
        this.reviewService.getProductReviews(id).subscribe((revs: Review[]) => {
          this.reviews = revs;
          this.loading = false;
        });
      },
      error: (err: any) => {
        console.error('Erreur : Produit introuvable', id, err);
        this.error = true;
        this.loading = false;
      }
    });
  }

  private setProduct(prod: Product) {
    this.product = prod;
    // Définit l'image par défaut sur la première de la liste
    this.selectedImage = prod.images && prod.images.length > 0 ? prod.images[0] : null;
    // Sélectionne la première variante par défaut
    if (prod.variants && prod.variants.length > 0) {
      this.selectedVariant = prod.variants[0];
    }
  }

  /**
   * Ajoute le produit au panier avec les options choisies.
   */
  addToCart() {
    if (this.product) {
      // Vérification du stock avant envoi
      if (this.product.stock <= 0) {
        this.snackBar.open(`❌ Désolé, ce produit est en rupture de stock`, 'Fermer', {
          duration: 4000,
          panelClass: ['error-snackbar'],
          horizontalPosition: 'end',
          verticalPosition: 'top'
        });
        return;
      }

      // Vérification de la quantité demandée par rapport au stock
      if (this.quantity > this.product.stock) {
        this.snackBar.open(`❌ Quantité insuffisante en stock (Maximum: ${this.product.stock})`, 'Fermer', {
          duration: 4000,
          panelClass: ['error-snackbar'],
          horizontalPosition: 'end',
          verticalPosition: 'top'
        });
        return;
      }

      // Envoi de la demande d'ajout au panier via le service
      this.cartService.addToCart(this.product.id, this.quantity).subscribe({
        next: () => {
          // Affiche un message de succès avec un bouton d'action
          this.snackBar.open(`✅ ${this.product?.name} ajouté au panier !`, 'Voir Panier', {
            duration: 5000,
            panelClass: ['success-snackbar'],
            horizontalPosition: 'end',
            verticalPosition: 'top'
          });
        },
        error: (err: any) => {
          // Alerte si l'utilisateur n'est pas connecté
          this.snackBar.open('⚠️ Veuillez vous connecter pour ajouter au panier', 'Fermer', { 
            duration: 3000,
            panelClass: ['warning-snackbar'],
            horizontalPosition: 'end',
            verticalPosition: 'top'
          });
        }
      });
    }
  }

  /**
   * Calcule dynamiquement le prix affiché (Prix de base + surcoût variante).
   */
  get displayedPrice() {
    if (!this.product) return 0;
    // Utilise le prix promo si disponible, sinon le prix normal
    let price = this.product.promoPrice || this.product.price;
    // Ajoute le delta de prix de la variante sélectionnée
    if (this.selectedVariant && this.selectedVariant.priceDelta) {
      price += this.selectedVariant.priceDelta;
    }
    return price;
  }

  /**
   * Soumet un nouvel avis client.
   */
  submitReview() {
    if (!this.product || !this.newReview.comment.trim()) return;

    this.submittingReview = true;
    const reviewData = {
      productId: this.product.id,
      note: this.newReview.rating,
      comment: this.newReview.comment
    };

    this.reviewService.addReview(reviewData).subscribe({
      next: (res) => {
        this.snackBar.open('✅ Merci ! Votre avis a été enregistré et sera visible après validation.', 'OK', { 
          duration: 5000,
          panelClass: ['success-snackbar'],
          horizontalPosition: 'end',
          verticalPosition: 'top'
        });
        this.newReview = { rating: 5, comment: '' };
        this.submittingReview = false;
        // On pourrait recharger les avis ici, mais s'ils doivent être approuvés d'abord, on ne verra pas le nouveau tout de suite
      },
      error: (err) => {
        let errorMessage = 'Erreur lors de l\'envoi de l\'avis';
        
        // Gestion des différents types d'erreurs
        if (err.status === 400 && err.error?.message) {
          errorMessage = err.error.message;
        } else if (err.error?.message) {
          errorMessage = err.error.message;
        } else if (err.message) {
          errorMessage = err.message;
        }
        
        this.snackBar.open('❌ ' + errorMessage, 'Fermer', { 
          duration: 5000,
          panelClass: ['error-snackbar'],
          horizontalPosition: 'end',
          verticalPosition: 'top'
        });
        this.submittingReview = false;
      }
    });
  }
}