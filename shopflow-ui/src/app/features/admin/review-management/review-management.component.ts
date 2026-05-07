import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReviewService } from '../../../core/services/review.service';
import { Review } from '../../../core/models';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-review-management',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatSnackBarModule, FormsModule],
  templateUrl: './review-management.component.html',
  styleUrls: ['./review-management.component.css']
})
export class ReviewManagementComponent implements OnInit {
  private reviewService = inject(ReviewService);
  private snackBar = inject(MatSnackBar);

  reviews: Review[] = [];
  filteredReviews: Review[] = [];
  loading = true;
  filterStatus: 'all' | 'pending' | 'approved' = 'pending';

  ngOnInit() {
    this.loadReviews();
  }

  loadReviews() {
    this.loading = true;
    this.reviewService.getAllReviews().subscribe({
      next: (data) => {
        this.reviews = data;
        this.applyFilter();
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur chargement avis:', err);
        this.loading = false;
        this.snackBar.open('❌ Erreur lors du chargement des avis', 'Fermer', {
          duration: 3000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  applyFilter() {
    if (this.filterStatus === 'all') {
      this.filteredReviews = this.reviews;
    } else if (this.filterStatus === 'pending') {
      this.filteredReviews = this.reviews.filter(r => !r.approved);
    } else {
      this.filteredReviews = this.reviews.filter(r => r.approved);
    }
  }

  changeFilter(status: 'all' | 'pending' | 'approved') {
    this.filterStatus = status;
    this.applyFilter();
  }

  approveReview(id: number) {
    this.reviewService.approveReview(id).subscribe({
      next: () => {
        this.snackBar.open('✅ Avis approuvé avec succès', 'Fermer', {
          duration: 3000,
          panelClass: ['success-snackbar'],
          horizontalPosition: 'end',
          verticalPosition: 'top'
        });
        this.loadReviews();
      },
      error: (err) => {
        this.snackBar.open('❌ Erreur lors de l\'approbation', 'Fermer', {
          duration: 3000,
          panelClass: ['error-snackbar'],
          horizontalPosition: 'end',
          verticalPosition: 'top'
        });
      }
    });
  }

  getPendingCount(): number {
    return this.reviews.filter(r => !r.approved).length;
  }

  getApprovedCount(): number {
    return this.reviews.filter(r => r.approved).length;
  }

  getCustomerInitial(review: Review): string {
    const name = review.customerName || review.userName || 'U';
    return name.charAt(0).toUpperCase();
  }

  getCustomerName(review: Review): string {
    return review.customerName || review.userName || 'Client';
  }

  getProductName(review: Review): string {
    return review.productName || 'Produit';
  }
}
