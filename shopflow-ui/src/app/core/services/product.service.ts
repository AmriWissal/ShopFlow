import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Product, Category } from '../models';

@Injectable({
  providedIn: 'root' // Service accessible globalement
})
export class ProductService {
  private http = inject(HttpClient); // Injecte le client pour les appels AJAX
  private apiUrl = `${environment.apiUrl}/products`; // URL de base : /api/products

  /**
   * Récupère la liste des produits avec filtres (catégorie, prix, promo) et pagination.
   */
  getProducts(filters?: any): Observable<any> {
    let params = new HttpParams(); // Initialise les paramètres d'URL
    if (filters) {
      // Parcourt les filtres pour construire la requête
      Object.keys(filters).forEach(key => {
        const value = filters[key];
        
        // Mappe 'query' vers 'q' pour le backend
        const paramKey = key === 'query' ? 'q' : key;
        
        // Validation spéciale pour categoryId : doit être un nombre valide
        if (key === 'categoryId') {
          // Convertir en nombre et vérifier que c'est valide
          const numValue = Number(value);
          if (!isNaN(numValue) && value !== null && value !== undefined && value !== '') {
            params = params.append(paramKey, numValue.toString());
          }
          // Si ce n'est pas un nombre valide, on ignore ce filtre
        } else {
          // Ajoute seulement les valeurs non nulles et non vides
          if (value !== null && value !== undefined && value !== '') {
            params = params.append(paramKey, value.toString());
          }
        }
      });
    }
    // Appel GET vers le backend avec les paramètres
    return this.http.get<any>(this.apiUrl, { params });
  }

  /**
   * Récupère les produits d'un vendeur spécifique.
   */
  getSellerProducts(page: number = 0, size: number = 10): Observable<any> {
    // Appel GET /api/products/seller avec pagination
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<any>(`${this.apiUrl}/seller`, { params });
  }

  /**
   * Récupère les détails complets d'un produit (inclut variantes, avis, note moyenne).
   */
  getProductById(id: number): Observable<Product> {
    // Appel GET /api/products/{id}
    return this.http.get<Product>(`${this.apiUrl}/${id}`);
  }

  /**
   * Effectue une recherche textuelle sur les produits.
   */
  searchProducts(query: string): Observable<Product[]> {
    // Appel GET /api/products/search?q=...
    return this.http.get<Product[]>(`${this.apiUrl}/search`, {
      params: new HttpParams().set('q', query)
    });
  }

  /**
   * Récupère les 10 meilleures ventes pour la page d'accueil.
   */
  getTopSellingProducts(): Observable<Product[]> {
    // Appel GET /api/products/top-selling
    return this.http.get<Product[]>(`${this.apiUrl}/top-selling`);
  }

  /**
   * Récupère la liste des catégories pour le menu de filtrage.
   */
  getCategories(): Observable<Category[]> {
    // Appel vers l'API des catégories
    return this.http.get<Category[]>(`${environment.apiUrl}/categories`);
  }

  /**
   * Crée un nouveau produit (ADMIN ou SELLER).
   */
  createProduct(product: any): Observable<Product> {
    // Envoi des données en POST
    return this.http.post<Product>(this.apiUrl, product);
  }

  /**
   * Met à jour les informations d'un produit existant.
   */
  updateProduct(id: number, product: any): Observable<Product> {
    // Envoi des modifications en PUT
    return this.http.put<Product>(`${this.apiUrl}/${id}`, product);
  }

  /**
   * Désactive un produit (Suppression logique).
   */
  deleteProduct(id: number): Observable<void> {
    // Envoi de la demande de suppression en DELETE
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}