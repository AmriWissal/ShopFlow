import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root' // Service global pour les statistiques
})
export class DashboardService {
  private http = inject(HttpClient); // Client HTTP
  private apiUrl = `${environment.apiUrl}/dashboard`; // URL de base : /api/dashboard

  /**
   * Récupère les statistiques globales (Réservé aux administrateurs).
   */
  getAdminStats(): Observable<any> {
    // Appel GET /api/dashboard/admin
    return this.http.get<any>(`${this.apiUrl}/admin`);
  }

  /**
   * Récupère les statistiques du vendeur connecté (Réservé aux vendeurs).
   */
  getSellerStats(): Observable<any> {
    // Appel GET /api/dashboard/seller
    return this.http.get<any>(`${this.apiUrl}/seller`);
  }
}
