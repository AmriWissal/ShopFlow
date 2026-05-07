import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Category } from '../models';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {
  private http = inject(HttpClient); // Injection du client HTTP
  private apiUrl = `${environment.apiUrl}/categories`; // URL de base pour les catégories

  /**
   * Récupère l'arbre complet des catégories.
   */
  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(this.apiUrl); // Appel GET vers l'arbre de catégories
  }

  /**
   * Crée une nouvelle catégorie (ADMIN).
   */
  createCategory(category: Partial<Category>): Observable<Category> {
    return this.http.post<Category>(this.apiUrl, category); // Envoi en POST
  }

  /**
   * Met à jour une catégorie existante (ADMIN).
   */
  updateCategory(id: number, category: Partial<Category>): Observable<Category> {
    return this.http.put<Category>(`${this.apiUrl}/${id}`, category); // Envoi en PUT
  }

  /**
   * Supprime une catégorie (ADMIN).
   */
  deleteCategory(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`); // Envoi en DELETE
  }
}
