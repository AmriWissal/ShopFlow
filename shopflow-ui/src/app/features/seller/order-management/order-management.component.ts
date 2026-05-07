import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrderService } from '../../../core/services/order.service';
import { Order } from '../../../core/models';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-seller-order-management',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatSnackBarModule],
  templateUrl: './order-management.component.html',
  styleUrls: ['./order-management.component.css']
})
export class SellerOrderManagementComponent implements OnInit {
  private orderService = inject(OrderService); // Service des commandes
  private snackBar = inject(MatSnackBar); // Notifications

  orders: Order[] = []; // Liste des commandes reçues
  loading = true; // État de chargement

  ngOnInit() {
    this.loadOrders(); // Charge les commandes à l'initialisation
  }

  /**
   * Charge les commandes du vendeur connecté.
   */
  loadOrders() {
    this.loading = true;
    this.orderService.getSellerOrders().subscribe({
      next: (orders: Order[]) => {
        this.orders = orders; // Stocke les commandes
        this.loading = false; // Fin chargement
      },
      error: (err: any) => {
        console.error('Erreur chargement commandes vendeur', err);
        this.loading = false;
      }
    });
  }

  /**
   * Met à jour le statut d'une commande vers le prochain statut logique.
   */
  updateStatus(order: Order) {
    // Le seller peut seulement faire progresser la commande, pas l'annuler
    let nextStatus: string;
    
    switch (order.status) {
      case 'PENDING':
        nextStatus = 'CONFIRMED';
        break;
      case 'CONFIRMED':
        nextStatus = 'SHIPPED';
        break;
      case 'SHIPPED':
        nextStatus = 'DELIVERED';
        break;
      case 'DELIVERED':
        this.snackBar.open('ℹ️ Cette commande est déjà livrée', 'OK', { 
          duration: 3000,
          panelClass: ['info-snackbar'],
          horizontalPosition: 'end',
          verticalPosition: 'top'
        });
        return;
      case 'CANCELLED':
        this.snackBar.open('ℹ️ Cette commande est annulée', 'OK', { 
          duration: 3000,
          panelClass: ['info-snackbar'],
          horizontalPosition: 'end',
          verticalPosition: 'top'
        });
        return;
      default:
        return;
    }

    this.orderService.updateOrderStatus(order.id, nextStatus).subscribe({
      next: () => {
        const statusLabels: any = {
          'CONFIRMED': 'Confirmée',
          'SHIPPED': 'Expédiée',
          'DELIVERED': 'Livrée'
        };
        this.snackBar.open(`✅ Commande ${statusLabels[nextStatus]}`, 'OK', { 
          duration: 3000,
          panelClass: ['success-snackbar'],
          horizontalPosition: 'end',
          verticalPosition: 'top'
        });
        this.loadOrders(); // Recharge la liste
      },
      error: (err: any) => {
        console.error('Erreur mise à jour statut', err);
        this.snackBar.open('❌ Erreur lors de la mise à jour', 'Fermer', { 
          duration: 3000,
          panelClass: ['error-snackbar'],
          horizontalPosition: 'end',
          verticalPosition: 'top'
        });
      }
    });
  }

  /**
   * Retourne le texte du bouton d'action selon le statut.
   */
  getActionButtonText(status: string): string {
    switch (status) {
      case 'PENDING': return 'Confirmer';
      case 'CONFIRMED': return 'Expédier';
      case 'SHIPPED': return 'Marquer livrée';
      case 'DELIVERED': return 'Livrée';
      case 'CANCELLED': return 'Annulée';
      default: return 'Action';
    }
  }

  /**
   * Vérifie si une action est possible sur la commande.
   */
  canUpdateStatus(status: string): boolean {
    return status !== 'DELIVERED' && status !== 'CANCELLED';
  }

  /**
   * Retourne les classes CSS selon le statut pour l'affichage.
   */
  getStatusClass(status: string) {
    const base = 'px-2 py-1 rounded-full text-xs font-medium ';
    switch (status.toLowerCase()) {
      case 'delivered': return base + 'bg-green-100 text-green-800';
      case 'confirmed': return base + 'bg-blue-100 text-blue-800';
      case 'pending': return base + 'bg-yellow-100 text-yellow-800';
      case 'cancelled': return base + 'bg-red-100 text-red-800';
      default: return base + 'bg-gray-100 text-gray-800';
    }
  }
}
