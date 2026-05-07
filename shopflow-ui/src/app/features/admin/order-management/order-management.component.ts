import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrderService } from '../../../core/services/order.service';
import { Order } from '../../../core/models';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-order-management',
  standalone: true,
  imports: [CommonModule, MatIconModule],
  templateUrl: './order-management.component.html',
  styleUrls: ['./order-management.component.css']
})
export class OrderManagementComponent implements OnInit {
  private orderService = inject(OrderService);
  private snackBar = inject(MatSnackBar);

  orders: Order[] = [];

  ngOnInit() {
    this.loadOrders();
  }

  loadOrders() {
    this.orderService.getAllOrders().subscribe(res => this.orders = res);
  }

  updateStatus(orderId: number, status: string) {
    this.orderService.updateOrderStatus(orderId, status).subscribe({
      next: () => {
        this.snackBar.open(`✅ Statut de commande mis à jour : ${status}`, 'Fermer', { 
          duration: 3000,
          panelClass: ['success-snackbar'],
          horizontalPosition: 'end',
          verticalPosition: 'top'
        });
        this.loadOrders();
      }
    });
  }

  getStatusClass(status: string) {
    const base = 'appearance-none ';
    switch (status.toLowerCase()) {
      case 'delivered': return base + 'bg-green-50 text-green-600';
      case 'confirmed': return base + 'bg-blue-50 text-blue-600';
      case 'pending': return base + 'bg-yellow-50 text-yellow-600';
      case 'cancelled': return base + 'bg-red-50 text-red-600';
      case 'shipped': return base + 'bg-purple-50 text-purple-600';
      default: return base + 'bg-slate-50 text-slate-600';
    }
  }
}