import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

/**
 * Service pour les utilitaires et données de référence (villes, frais de port, etc.)
 */
@Injectable({
  providedIn: 'root'
})
export class UtilityService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/utilities`;

  /**
   * Récupère la liste des villes tunisiennes
   */
  getTunisianCities(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/cities`);
  }

  /**
   * Calcule les frais de livraison en fonction du montant
   */
  calculateShippingFees(amount: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/shipping-fees`, {
      params: { amount: amount.toString() }
    });
  }

  /**
   * Récupère la configuration du marché tunisien
   */
  getMarketConfig(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/config`);
  }

  /**
   * Formate un montant en dinars tunisiens
   */
  formatPrice(amount: number): string {
    return `${amount.toFixed(3)} DT`;
  }

  /**
   * Formate une date en français tunisien
   */
  formatDate(date: Date | string): string {
    const d = typeof date === 'string' ? new Date(date) : date;
    return d.toLocaleDateString('fr-TN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  /**
   * Formate une date et heure en français tunisien
   */
  formatDateTime(date: Date | string): string {
    const d = typeof date === 'string' ? new Date(date) : date;
    return d.toLocaleDateString('fr-TN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
