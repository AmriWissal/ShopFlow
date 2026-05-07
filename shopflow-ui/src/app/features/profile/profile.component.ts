import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { OrderService } from '../../core/services/order.service';
import { ReviewService } from '../../core/services/review.service';
import { User, Order, Review } from '../../core/models';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, RouterModule, MatIconModule, FormsModule, MatSnackBarModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  authService = inject(AuthService); // Service d'authentification
  orderService = inject(OrderService); // Service des commandes
  reviewService = inject(ReviewService); // Service des avis (Ajouté pour l'espace client)
  snackBar = inject(MatSnackBar);

  user: User | null = null; // Utilisateur connecté
  orders: Order[] = []; // Liste des commandes du client
  myReviews: Review[] = []; // Liste des avis laissés par le client
  loading = true; // État de chargement
  activeTab: 'orders' | 'info' | 'reviews' = 'orders'; // Onglets (Commandes, Infos, Mes Avis)
  
  isEditing = false;
  editForm = {
    firstName: '',
    lastName: ''
  };

  ngOnInit() {
    // S'abonne à l'utilisateur actuel pour afficher son profil
    this.authService.currentUser$.subscribe(u => {
      this.user = u;
      if (u) {
        this.editForm.firstName = u.firstName || '';
        this.editForm.lastName = u.lastName || '';
      }
    });
    this.loadOrders(); // Charge les commandes à l'initialisation
  }

  toggleEdit() {
    this.isEditing = !this.isEditing;
    if (!this.isEditing && this.user) {
      this.editForm.firstName = this.user.firstName || '';
      this.editForm.lastName = this.user.lastName || '';
    }
  }

  updateProfile() {
    if (!this.user) return;
    
    console.log('Envoi des données:', this.editForm);
    
    this.authService.updateProfile(this.editForm).subscribe({
      next: (updatedUser) => {
        console.log('Réponse du backend:', updatedUser);
        this.isEditing = false;
        this.snackBar.open('✅ Profil mis à jour avec succès !', 'OK', { 
          duration: 3000,
          panelClass: ['success-snackbar'],
          horizontalPosition: 'end',
          verticalPosition: 'top'
        });
        // Le service AuthService met déjà à jour currentUserSubject
        // L'interface se mettra à jour automatiquement via l'observable
      },
      error: (err) => {
        console.error('Erreur lors de la mise à jour du profil', err);
        this.snackBar.open('❌ Erreur lors de la mise à jour du profil', 'Fermer', { 
          duration: 5000,
          panelClass: ['error-snackbar'],
          horizontalPosition: 'end',
          verticalPosition: 'top'
        });
      }
    });
  }

  /**
   * Charge l'historique des commandes depuis le backend.
   */
  loadOrders() {
    this.loading = true; // Début chargement
    this.orderService.getMyOrders().subscribe({
      next: (orders: Order[]) => {
        this.orders = orders; // Stocke les commandes récupérées
        this.loading = false; // Fin chargement
      },
      error: (err: any) => {
        console.error('Erreur chargement commandes', err);
        this.loading = false; // Arrêt chargement sur erreur
      }
    });
  }

  /**
   * Change l'onglet actif et charge les données correspondantes.
   */
  switchTab(tab: 'orders' | 'info' | 'reviews') {
    this.activeTab = tab;
    if (tab === 'reviews' && this.myReviews.length === 0) {
      // Logique pour charger les avis du client si nécessaire
      // (Nécessiterait un endpoint spécifique ou un filtrage côté client)
    }
  }

  /**
   * Retourne les classes CSS correspondantes au statut de la commande.
   */
  getStatusClass(status: string) {
    const base = 'bg-white border ';
    switch (status.toLowerCase()) {
      case 'delivered': return base + 'border-green-200 text-green-600'; // Livrée
      case 'confirmed': return base + 'border-blue-200 text-blue-600'; // Confirmée
      case 'pending': return base + 'border-yellow-200 text-yellow-600'; // En attente
      case 'cancelled': return base + 'border-red-200 text-red-600'; // Annulée
      default: return base + 'border-slate-200 text-slate-600'; // Inconnu
    }
  }

  /**
   * Annule une commande en attente.
   */
  cancelOrder(orderId: number) {
    if (!confirm('Êtes-vous sûr de vouloir annuler cette commande ?')) {
      return;
    }

    this.orderService.cancelOrder(orderId).subscribe({
      next: (updatedOrder) => {
        this.snackBar.open('✅ Commande annulée avec succès', 'OK', {
          duration: 3000,
          panelClass: ['success-snackbar'],
          horizontalPosition: 'end',
          verticalPosition: 'top'
        });
        // Recharger les commandes pour afficher le nouveau statut
        this.loadOrders();
      },
      error: (err) => {
        console.error('Erreur lors de l\'annulation de la commande', err);
        const errorMessage = err.error?.message || 'Erreur lors de l\'annulation de la commande';
        this.snackBar.open('❌ ' + errorMessage, 'Fermer', {
          duration: 5000,
          panelClass: ['error-snackbar'],
          horizontalPosition: 'end',
          verticalPosition: 'top'
        });
      }
    });
  }
}