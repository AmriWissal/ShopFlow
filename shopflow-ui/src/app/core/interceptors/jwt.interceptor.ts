import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { catchError, throwError } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';

/**
 * Intercepteur HTTP pour injecter automatiquement le token JWT dans chaque requête sortante.
 * Gère également les erreurs globales et affiche des messages d'erreur dans des popups.
 */
export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const snackBar = inject(MatSnackBar);
  const token = authService.getToken();

  // On clone la requête pour y ajouter le header Authorization si le token existe
  if (token) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // Message d'erreur par défaut
      let errorMessage = 'Une erreur est survenue';

      // Extraire le message d'erreur du backend si disponible
      if (error.error && error.error.message) {
        errorMessage = error.error.message;
      } else if (error.message) {
        errorMessage = error.message;
      }

      // Gestion spécifique selon le code d'erreur HTTP
      if (error.status === 401) {
        // 401 Non autorisé : déconnexion automatique
        console.warn(`401 Non autorisé : ${req.method} ${req.url}`);
        snackBar.open('Session expirée. Veuillez vous reconnecter.', 'Fermer', {
          duration: 5000,
          panelClass: ['error-snackbar'],
          horizontalPosition: 'center',
          verticalPosition: 'top'
        });
        authService.logout();
      } else if (error.status === 403) {
        // 403 Accès refusé
        console.warn(`ACCES REFUSE : ${req.method} ${req.url}`);
        snackBar.open(`Accès refusé : ${errorMessage}`, 'Fermer', {
          duration: 5000,
          panelClass: ['error-snackbar'],
          horizontalPosition: 'center',
          verticalPosition: 'top'
        });
      } else if (error.status === 400) {
        // 400 Bad Request : erreurs de validation métier
        snackBar.open(errorMessage, 'Fermer', {
          duration: 6000,
          panelClass: ['error-snackbar'],
          horizontalPosition: 'center',
          verticalPosition: 'top'
        });
      } else if (error.status === 404) {
        // 404 Not Found : ressource non trouvée
        snackBar.open(errorMessage, 'Fermer', {
          duration: 5000,
          panelClass: ['error-snackbar'],
          horizontalPosition: 'center',
          verticalPosition: 'top'
        });
      } else if (error.status === 500) {
        // 500 Internal Server Error
        snackBar.open('Erreur serveur : ' + errorMessage, 'Fermer', {
          duration: 5000,
          panelClass: ['error-snackbar'],
          horizontalPosition: 'center',
          verticalPosition: 'top'
        });
      } else if (error.status === 0) {
        // Erreur réseau (serveur inaccessible)
        snackBar.open('Impossible de contacter le serveur. Vérifiez votre connexion.', 'Fermer', {
          duration: 5000,
          panelClass: ['error-snackbar'],
          horizontalPosition: 'center',
          verticalPosition: 'top'
        });
      } else {
        // Autres erreurs
        snackBar.open(errorMessage, 'Fermer', {
          duration: 5000,
          panelClass: ['error-snackbar'],
          horizontalPosition: 'center',
          verticalPosition: 'top'
        });
      }

      return throwError(() => error);
    })
  );
};