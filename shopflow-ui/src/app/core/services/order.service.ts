import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Order } from '../models';

@Injectable({
  providedIn: 'root' // Service global pour les commandes
})
export class OrderService {
  private http = inject(HttpClient); // Client HTTP pour les requêtes
  private apiUrl = `${environment.apiUrl}/orders`; // URL de base : /api/orders

  /**
   * Passe une nouvelle commande à partir du panier actuel.
   */
  placeOrder(orderData: any): Observable<Order> {
    // Envoi des données de livraison en POST
    return this.http.post<Order>(this.apiUrl, orderData);
  }

  /**
   * Récupère l'historique des commandes de l'utilisateur connecté.
   */
  getMyOrders(): Observable<Order[]> {
    // Appel GET vers /api/orders/my
    return this.http.get<Order[]>(`${this.apiUrl}/my`);
  }

  /**
   * Récupère les détails complets d'une commande par son ID.
   */
  getOrderById(id: number): Observable<Order> {
    // Appel GET /api/orders/{id}
    return this.http.get<Order>(`${this.apiUrl}/${id}`);
  }

  /**
   * Permet au client d'annuler une commande (si éligible, ex: statut PENDING).
   */
  cancelOrder(id: number): Observable<Order> {
    // Appel PUT vers /api/orders/{id}/cancel
    return this.http.put<Order>(`${this.apiUrl}/${id}/cancel`, null);
  }

  /**
   * Récupère la liste de toutes les commandes du système (ADMIN uniquement).
   */
  getAllOrders(): Observable<Order[]> {
    // Appel GET /api/orders
    return this.http.get<Order[]>(this.apiUrl);
  }

  /**
   * Récupère les commandes contenant des produits du vendeur connecté.
   */
  getSellerOrders(): Observable<Order[]> {
    // Appel GET /api/orders/seller
    return this.http.get<Order[]>(`${this.apiUrl}/seller`);
  }

  /**
   * Met à jour le statut d'une commande (ADMIN ou SELLER).
   */
  updateOrderStatus(id: number, status: string): Observable<Order> {
    // Envoi du nouveau statut via paramètre de requête en PUT
    return this.http.put<Order>(`${this.apiUrl}/${id}/status`, null, { params: { status } });
  }
}