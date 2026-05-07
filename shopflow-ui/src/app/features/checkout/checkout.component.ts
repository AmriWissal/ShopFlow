import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { CartService } from '../../core/services/cart.service';
import { OrderService } from '../../core/services/order.service';
import { Cart, Coupon } from '../../core/models';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, ReactiveFormsModule, MatIconModule],
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.css']
})
export class CheckoutComponent implements OnInit {
  private fb = inject(FormBuilder); // Constructeur de formulaires réactifs
  private cartService = inject(CartService); // Service pour récupérer le panier
  private orderService = inject(OrderService); // Service pour passer la commande
  private router = inject(Router); // Pour la navigation après succès
  private snackBar = inject(MatSnackBar); // Pour afficher les notifications
  private http = inject(HttpClient); // Client HTTP (utilisé ici pour le coupon en direct)

  cart: Cart | null = null; // Données du panier à payer
  
  // Formulaire de livraison avec validations obligatoires
  checkoutForm = this.fb.group({
    fullName: ['', Validators.required],
    address: ['', Validators.required],
    city: ['', Validators.required],
    zipCode: ['', Validators.required]
  });

  couponCode = ''; // Code promo saisi par l'utilisateur
  applyingCoupon = false; // Indicateur de chargement pendant la validation du coupon
  appliedCoupon: Coupon | null = null; // Détails du coupon validé
  discountAmount = 0; // Montant de la réduction calculé
  loading = false; // Indicateur de chargement général

  // Liste des principales villes de Tunisie
  tunisianCities = [
    'Tunis', 'Ariana', 'Ben Arous', 'Manouba', 'Nabeul', 'Zaghouan', 'Bizerte',
    'Béja', 'Jendouba', 'Le Kef', 'Siliana', 'Sousse', 'Monastir', 'Mahdia',
    'Sfax', 'Kairouan', 'Kasserine', 'Sidi Bouzid', 'Gabès', 'Médenine',
    'Tataouine', 'Gafsa', 'Tozeur', 'Kebili'
  ];

  /**
   * Calcule le montant final à payer (Total panier - Réduction).
   */
  get finalTotal() {
    return this.cart ? Math.max(0, this.cart.totalPrice - this.discountAmount) : 0;
  }

  ngOnInit() {
    // Charge le panier au démarrage de la page de paiement
    this.cartService.getCart().subscribe(c => this.cart = c);
  }

  /**
   * Vérifie et applique un code promo.
   */
  applyCoupon() {
    this.applyingCoupon = true; // Active le spinner
    this.http.get<Coupon>(`${environment.apiUrl}/coupons/validate/${this.couponCode}`).subscribe({
      next: (coupon) => {
        this.appliedCoupon = coupon; // Stocke les infos du coupon
        this.calculateDiscount(); // Calcule la remise immédiate
        this.applyingCoupon = false; // Arrête le spinner
        this.snackBar.open('✅ Coupon appliqué !', 'Fermer', { 
          duration: 3000,
          panelClass: ['success-snackbar'],
          horizontalPosition: 'end',
          verticalPosition: 'top'
        });
      },
      error: () => {
        this.applyingCoupon = false;
        this.snackBar.open('❌ Code promo invalide', 'Fermer', { 
          duration: 3000,
          panelClass: ['error-snackbar'],
          horizontalPosition: 'end',
          verticalPosition: 'top'
        });
      }
    });
  }

  /**
   * Supprime le coupon appliqué.
   */
  removeCoupon() {
    this.appliedCoupon = null;
    this.discountAmount = 0;
    this.couponCode = '';
  }

  /**
   * Calcule le montant de la remise selon le type de coupon.
   */
  calculateDiscount() {
    if (!this.appliedCoupon || !this.cart) return;
    
    // Type 'PERCENT' ou 'FIXED' défini dans l'énoncé
    if (this.appliedCoupon.type === 'PERCENT') {
      this.discountAmount = (this.cart.totalPrice * this.appliedCoupon.value) / 100;
    } else {
      this.discountAmount = this.appliedCoupon.value;
    }
  }

  /**
   * Gère les erreurs de chargement d'image en affichant un placeholder.
   */
  onImageError(event: Event) {
    const img = event.target as HTMLImageElement;
    img.src = 'https://placehold.co/100x100/f0fdf4/16a34a?text=Produit';
  }

  /**
   * Valide le formulaire et crée la commande sur le backend.
   */
  placeOrder() {
    if (this.checkoutForm.valid && this.cart) {
      this.loading = true; // Empêche les clics multiples
      
      // Prépare les données pour l'API /api/orders
      const orderData = {
        shippingAddress: `${this.checkoutForm.value.address}, ${this.checkoutForm.value.city}, ${this.checkoutForm.value.zipCode}`,
        couponCode: this.appliedCoupon?.code
      };

      this.orderService.placeOrder(orderData).subscribe({
        next: (order) => {
          // Vide le panier localement pour mettre à jour le compteur immédiatement
          this.cartService.clearCart();
          
          this.snackBar.open('✅ Commande passée avec succès !', 'Fermer', { 
            duration: 5000,
            panelClass: ['success-snackbar'],
            horizontalPosition: 'end',
            verticalPosition: 'top'
          });
          this.router.navigate(['/profile']); // Redirection vers l'espace client
        },
        error: () => {
          this.loading = false;
          this.snackBar.open('❌ Erreur lors de la création de la commande', 'Fermer', { 
            duration: 5000,
            panelClass: ['error-snackbar'],
            horizontalPosition: 'end',
            verticalPosition: 'top'
          });
        }
      });
    }
  }
}