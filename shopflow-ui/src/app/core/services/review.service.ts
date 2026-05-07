import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Review } from '../models';

@Injectable({
  providedIn: 'root' // Service global pour les avis
})
export class ReviewService {
  private http = inject(HttpClient); // Client HTTP pour les appels AJAX
  private apiUrl = `${environment.apiUrl}/reviews`; // URL de base : /api/reviews

  /**
   * Envoie un nouvel avis pour un produit.
   * Le backend vérifiera si l'utilisateur a réellement acheté le produit (Achat vérifié).
   */
  addReview(reviewData: any): Observable<Review> {
    // Envoi de la note et du commentaire en POST
    return this.http.post<Review>(this.apiUrl, reviewData);
  }

  /**
   * Récupère la liste des avis approuvés pour un produit spécifique.
   */
  getProductReviews(productId: number): Observable<Review[]> {
    // Appel GET filtré par l'ID du produit : /api/reviews/product/{productId}
    return this.http.get<Review[]>(`${this.apiUrl}/product/${productId}`);
  }

  /**
   * Permet à un administrateur d'approuver un avis client pour qu'il soit public.
   */
  approveReview(id: number): Observable<Review> {
    // Appel PUT vers /api/reviews/{id}/approve
    return this.http.put<Review>(`${this.apiUrl}/${id}/approve`, {});
  }

  /**
   * Récupère tous les avis en attente de validation (Admin).
   */
  getPendingReviews(): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.apiUrl}/pending`);
  }

  /**
   * Récupère tous les avis (Admin).
   */
  getAllReviews(): Observable<Review[]> {
    return this.http.get<Review[]>(this.apiUrl);
  }
}
