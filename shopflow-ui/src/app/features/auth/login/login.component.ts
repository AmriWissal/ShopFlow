import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, ReactiveFormsModule, MatIconModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);

  loading = false;
  showPassword = false;

  loginForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });

  onSubmit() {
    if (this.loginForm.valid) {
      this.loading = true;
      this.authService.login(this.loginForm.value).subscribe({
        next: (response) => {
          this.snackBar.open('🎉 Bon retour parmi nous !', 'Fermer', { 
            duration: 3000,
            panelClass: ['welcome-snackbar'],
            horizontalPosition: 'end',
            verticalPosition: 'top'
          });
          
          // Redirection selon le rôle de l'utilisateur
          const user = response.user;
          if (user.role === 'ADMIN') {
            this.router.navigate(['/admin/dashboard']);
          } else if (user.role === 'SELLER') {
            this.router.navigate(['/seller/dashboard']);
          } else {
            // Client ou autre rôle -> page home
            this.router.navigate(['/']);
          }
        },
        error: (err) => {
          this.loading = false;
          this.snackBar.open(err.error?.message || 'Connexion échouée', 'Fermer', { 
            duration: 5000,
            panelClass: ['error-snackbar'],
            horizontalPosition: 'end',
            verticalPosition: 'top'
          });
        }
      });
    }
  }
}