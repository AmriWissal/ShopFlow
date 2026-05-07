import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { map, take } from 'rxjs/operators';

export const adminGuard = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.currentUser$.pipe(
    take(1),
    map(user => {
      if (user && authService.hasRole('ADMIN')) {
        return true;
      }
      
      // Redirect to home if not admin
      router.navigate(['/']);
      return false;
    })
  );
};