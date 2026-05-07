import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Cart } from '../models';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private http = inject(HttpClient); // Injection du client HTTP
  private apiUrl = `${environment.apiUrl}/cart`; // URL de base pour le panier

  private cartSubject = new BehaviorSubject<Cart | null>(null); // État réactif du panier
  cart$ = this.cartSubject.asObservable(); // Observable pour s'abonner aux changements du panier

  /**
   * Récupère le panier du client connecté.
   */
  getCart(): Observable<Cart> {
    return this.http.get<Cart>(this.apiUrl).pipe(
      tap(cart => this.cartSubject.next(cart)) // Met à jour l'état local après récupération
    );
  }

  /**
   * Ajoute un produit au panier.
   */
  addToCart(productId: number, quantity: number): Observable<Cart> {
    return this.http.post<Cart>(`${this.apiUrl}/items`, { productId, quantity }).pipe(
      tap(cart => this.cartSubject.next(cart)) // Met à jour l'état local
    );
  }

  /**
   * Modifie la quantité d'un article dans le panier.
   */
  updateQuantity(itemId: number, quantity: number): Observable<Cart> {
    return this.http.put<Cart>(`${this.apiUrl}/items/${itemId}`, null, { params: { quantity: quantity.toString() } }).pipe(
      tap(cart => this.cartSubject.next(cart)) // Met à jour l'état local
    );
  }

  /**
   * Supprime un article du panier.
   */
  removeItem(itemId: number): Observable<Cart> {
    return this.http.delete<Cart>(`${this.apiUrl}/items/${itemId}`).pipe(
      tap(cart => this.cartSubject.next(cart)) // Met à jour l'état local
    );
  }

  /**
   * Applique un code promo au panier.
   */
  applyCoupon(code: string): Observable<Cart> {
    return this.http.post<Cart>(`${this.apiUrl}/coupon`, null, { params: { code } }).pipe(
      tap(cart => this.cartSubject.next(cart)) // Met à jour l'état local avec la remise
    );
  }

  /**
   * Retire le code promo du panier.
   */
  removeCoupon(): Observable<Cart> {
    return this.http.delete<Cart>(`${this.apiUrl}/coupon`).pipe(
      tap(cart => this.cartSubject.next(cart)) // Met à jour l'état local
    );
  }

  /**
   * Vide complètement le panier (utilisé après confirmation de commande).
   */
  clearCart(): void {
    this.cartSubject.next({ items: [], totalPrice: 0 } as Cart);
  }

  /**
   * Rafraîchit le panier depuis le serveur.
   */
  refreshCart(): void {
    this.getCart().subscribe();
  }
}