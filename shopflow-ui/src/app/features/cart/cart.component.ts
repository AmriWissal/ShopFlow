import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CartService } from '../../core/services/cart.service';
import { Cart, CartItem } from '../../core/models';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterModule, MatIconModule],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {
  private cartService = inject(CartService); // Service pour gérer le panier
  private snackBar = inject(MatSnackBar); // Pour les notifications

  cart: Cart | null = null; // Données du panier
  loading = true; // État de chargement
  couponCode = ''; // Code promo saisi par l'utilisateur

  ngOnInit() {
    this.loadCart(); // Charge le panier à l'initialisation
  }

  /**
   * Récupère le panier actuel depuis le serveur.
   */
  loadCart() {
    this.loading = true;
    this.cartService.getCart().subscribe({
      next: (c) => {
        this.cart = c;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  /**
   * Met à jour la quantité d'un article.
   */
  updateQuantity(itemId: number, quantity: number) {
    if (quantity > 0) {
      this.cartService.updateQuantity(itemId, quantity).subscribe(c => this.cart = c);
    }
  }

  /**
   * Supprime un article du panier.
   */
  removeItem(itemId: number) {
    this.cartService.removeItem(itemId).subscribe({
      next: (c) => {
        this.cart = c;
        this.snackBar.open('✅ Article retiré du panier', 'OK', { 
          duration: 3000,
          panelClass: ['success-snackbar'],
          horizontalPosition: 'end',
          verticalPosition: 'top'
        });
      }
    });
  }

  /**
   * Gère les erreurs de chargement d'image en affichant un placeholder.
   */
  onImageError(event: Event) {
    const img = event.target as HTMLImageElement;
    img.src = 'https://placehold.co/150x150/f0fdf4/16a34a?text=Produit';
  }

  /**
   * Applique un code promo.
   */
  applyCoupon() {
    if (!this.couponCode) return;
    this.cartService.applyCoupon(this.couponCode).subscribe({
      next: (c) => {
        this.cart = c;
        this.snackBar.open('✅ Coupon appliqué !', 'OK', { 
          duration: 3000,
          panelClass: ['success-snackbar'],
          horizontalPosition: 'end',
          verticalPosition: 'top'
        });
      },
      error: (err) => {
        this.snackBar.open('❌ Code promo invalide ou expiré', 'Erreur', { 
          duration: 3000,
          panelClass: ['error-snackbar'],
          horizontalPosition: 'end',
          verticalPosition: 'top'
        });
      }
    });
  }

  /**
   * Retire le code promo actuel.
   */
  removeCoupon() {
    this.cartService.removeCoupon().subscribe(c => {
      this.cart = c;
      this.couponCode = '';
      this.snackBar.open('ℹ️ Coupon retiré', 'OK', { 
        duration: 3000,
        panelClass: ['info-snackbar'],
        horizontalPosition: 'end',
        verticalPosition: 'top'
      });
    });
  }
}