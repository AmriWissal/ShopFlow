import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthResponse, User } from '../models';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root' // Service disponible dans toute l'application
})
export class AuthService {
  private static readonly ACCESS_TOKEN_KEY = 'accessToken';
  private static readonly LEGACY_TOKEN_KEY = 'token';
  private static readonly REFRESH_TOKEN_KEY = 'refreshToken';
  private static readonly USER_KEY = 'user';

  private http = inject(HttpClient); // Client HTTP pour les requêtes AJAX
  private router = inject(Router); // Router pour la navigation programmée
  private apiUrl = `${environment.apiUrl}/auth`; // URL de base de l'authentification

  // Sujet pour suivre l'état de l'utilisateur en temps réel
  private currentUserSubject = new BehaviorSubject<User | null>(
    this.normalizeStoredUser(JSON.parse(localStorage.getItem(AuthService.USER_KEY) || 'null'))
  );
  // Observable public pour s'abonner aux changements d'utilisateur
  currentUser$ = this.currentUserSubject.asObservable();

  /**
   * Envoie une requête de connexion au backend.
   */
  login(credentials: any): Observable<AuthResponse> {
    // Appel POST vers /api/auth/login
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        this.persistSession(response);
      })
    );
  }

  /**
   * Enregistre un nouveau compte client ou vendeur.
   */
  register(userData: any): Observable<AuthResponse> {
    // Appel POST vers /api/auth/register
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, userData).pipe(
      tap(response => {
        this.persistSession(response);
      })
    );
  }

  /**
   * Met à jour les informations du profil.
   */
  updateProfile(userData: any): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/profile`, userData).pipe(
      tap(user => {
        // Utiliser directement les données retournées par le backend
        const updatedUser = this.normalizeStoredUser(user);
        localStorage.setItem(AuthService.USER_KEY, JSON.stringify(updatedUser));
        this.currentUserSubject.next(updatedUser);
      })
    );
  }

  /**
   * Demande un nouvel access token via le refresh token.
   */
  refreshToken(): Observable<AuthResponse> {
    const refreshToken = localStorage.getItem(AuthService.REFRESH_TOKEN_KEY); // Récupère le jeton de rafraîchissement
    // Envoie le jeton au backend pour obtenir un nouveau couple
    return this.http.post<AuthResponse>(`${this.apiUrl}/refresh`, { refreshToken }).pipe(
      tap(response => {
        this.saveToken(response.accessToken);
      })
    );
  }

  /**
   * Déconnecte l'utilisateur et nettoie les données locales.
   */
  logout() {
    const refreshToken = localStorage.getItem(AuthService.REFRESH_TOKEN_KEY);
    if (refreshToken) {
      // Notifie le backend de la déconnexion si possible
      this.http.post(`${this.apiUrl}/logout`, { refreshToken }).subscribe();
    }
    // Suppression de toutes les traces de session
    localStorage.removeItem(AuthService.ACCESS_TOKEN_KEY);
    localStorage.removeItem(AuthService.LEGACY_TOKEN_KEY);
    localStorage.removeItem(AuthService.REFRESH_TOKEN_KEY);
    localStorage.removeItem(AuthService.USER_KEY);
    this.currentUserSubject.next(null); // Réinitialise l'utilisateur à null
    this.router.navigate(['/auth/login']); // Redirige vers la page de login
  }

  saveToken(token: string): void {
    const normalizedToken = this.normalizeToken(token);
    localStorage.setItem(AuthService.ACCESS_TOKEN_KEY, normalizedToken);
    // Backward compatibility with old key usage
    localStorage.setItem(AuthService.LEGACY_TOKEN_KEY, normalizedToken);
  }

  /**
   * Accesseur simple pour obtenir l'access token actuel.
   */
  getToken(): string | null {
    const token = localStorage.getItem(AuthService.ACCESS_TOKEN_KEY) ?? localStorage.getItem(AuthService.LEGACY_TOKEN_KEY);
    return token ? this.normalizeToken(token) : null;
  }

  /**
   * Getter pour savoir si l'utilisateur est connecté.
   */
  get isLoggedIn(): boolean {
    return !!this.currentUserSubject.value;
  }

  /**
   * Getter pour savoir si l'utilisateur est admin.
   */
  get isAdmin(): boolean {
    return this.hasRole('ADMIN');
  }

  /**
   * Vérifie si l'utilisateur possède un rôle spécifique.
   */
  hasRole(role: string): boolean {
    const user = this.currentUserSubject.value;
    return this.normalizeRole(user?.role) === this.normalizeRole(role); // Compare le rôle normalisé avec celui demandé
  }

  private persistSession(response: AuthResponse): void {
    const normalizedUser = this.normalizeStoredUser(response.user);
    // Stockage persistant des jetons et de l'utilisateur dans le navigateur
    this.saveToken(response.accessToken);
    localStorage.setItem(AuthService.REFRESH_TOKEN_KEY, response.refreshToken);
    localStorage.setItem(AuthService.USER_KEY, JSON.stringify(normalizedUser));
    // Notification aux abonnés de la nouvelle connexion
    this.currentUserSubject.next(normalizedUser);
  }

  private normalizeRole(role?: string): string | undefined {
    return role?.replace(/^ROLE_/, '').toUpperCase();
  }

  private normalizeStoredUser(user: User | null): User | null {
    if (!user) {
      return null;
    }

    return {
      ...user,
      role: this.normalizeRole(user.role) as User['role']
    };
  }

  private normalizeToken(token: string): string {
    return token.trim().replace(/^Bearer\s+/i, '').replace(/^"|"$/g, '');
  }
}