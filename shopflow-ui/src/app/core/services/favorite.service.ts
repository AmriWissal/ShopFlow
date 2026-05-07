import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Favorite } from '../models';

@Injectable({
  providedIn: 'root'
})
export class FavoriteService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/favorites`;

  // Subject pour suivre les IDs des produits favoris
  private favoritesSubject = new BehaviorSubject<Set<number>>(new Set());
  public favorites$ = this.favoritesSubject.asObservable();

  /**
   * Récupère tous les favoris de l'utilisateur
   */
  getFavorites(): Observable<Favorite[]> {
    return this.http.get<Favorite[]>(this.apiUrl).pipe(
      tap(favorites => {
        const favoriteIds = new Set(favorites.map(f => f.product.id));
        this.favoritesSubject.next(favoriteIds);
      })
    );
  }

  /**
   * Vérifie si un produit est favori
   */
  checkFavorite(productId: number): Observable<{ isFavorite: boolean }> {
    return this.http.get<{ isFavorite: boolean }>(`${this.apiUrl}/check/${productId}`);
  }

  /**
   * Toggle un produit dans les favoris
   */
  toggleFavorite(productId: number): Observable<{ isFavorite: boolean }> {
    return this.http.post<{ isFavorite: boolean }>(`${this.apiUrl}/${productId}/toggle`, null).pipe(
      tap(response => {
        const currentFavorites = this.favoritesSubject.value;
        if (response.isFavorite) {
          currentFavorites.add(productId);
        } else {
          currentFavorites.delete(productId);
        }
        this.favoritesSubject.next(new Set(currentFavorites));
      })
    );
  }

  /**
   * Ajoute un produit aux favoris
   */
  addToFavorites(productId: number): Observable<Favorite> {
    return this.http.post<Favorite>(`${this.apiUrl}/${productId}`, null).pipe(
      tap(() => {
        const currentFavorites = this.favoritesSubject.value;
        currentFavorites.add(productId);
        this.favoritesSubject.next(new Set(currentFavorites));
      })
    );
  }

  /**
   * Retire un produit des favoris
   */
  removeFromFavorites(productId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${productId}`).pipe(
      tap(() => {
        const currentFavorites = this.favoritesSubject.value;
        currentFavorites.delete(productId);
        this.favoritesSubject.next(new Set(currentFavorites));
      })
    );
  }

  /**
   * Vérifie si un produit est favori (depuis le cache local)
   */
  isFavorite(productId: number): boolean {
    return this.favoritesSubject.value.has(productId);
  }
}
