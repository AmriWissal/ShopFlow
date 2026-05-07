import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Coupon } from '../../../core/models';
import { CouponService } from '../../../core/services/coupon.service';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-coupon-management',
  standalone: true,
  imports: [CommonModule, MatIconModule, FormsModule, ReactiveFormsModule],
  templateUrl: './coupon-management.component.html',
  styleUrls: ['./coupon-management.component.css']
})
export class CouponManagementComponent implements OnInit {
  private couponService = inject(CouponService);
  private fb = inject(FormBuilder);
  private snackBar = inject(MatSnackBar);

  coupons: Coupon[] = [];
  showModal = false;
  isEditMode = false;
  editingCouponId: number | null = null;

  couponForm = this.fb.group({
    code: ['', [Validators.required, Validators.minLength(3)]],
    value: [0, [Validators.required, Validators.min(1)]],
    type: ['PERCENT', Validators.required],
    expirationDate: ['', Validators.required],
    usageLimit: [100, [Validators.required, Validators.min(1)]],
    active: [true]
  });

  ngOnInit() {
    this.loadCoupons();
  }

  loadCoupons() {
    this.couponService.getCoupons().subscribe(res => this.coupons = res);
  }

  openModal() {
    this.isEditMode = false;
    this.editingCouponId = null;
    this.couponForm.reset({ type: 'PERCENT', active: true, usageLimit: 100 });
    this.showModal = true;
  }

  openEditModal(coupon: Coupon) {
    this.isEditMode = true;
    this.editingCouponId = coupon.id;
    
    // Convertir la date ISO en format yyyy-MM-dd pour l'input date
    const expirationDate = coupon.expirationDate 
      ? new Date(coupon.expirationDate).toISOString().split('T')[0] 
      : '';
    
    this.couponForm.patchValue({
      code: coupon.code,
      value: coupon.value,
      type: coupon.type,
      expirationDate: expirationDate,
      usageLimit: coupon.usageLimit,
      active: coupon.active
    });
    
    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
    this.isEditMode = false;
    this.editingCouponId = null;
  }

  saveCoupon() {
    if (this.couponForm.valid) {
      const formValue = this.couponForm.value;
      const couponRequest = {
        ...formValue,
        // Conversion de la date simple en format ISO attendu par LocalDateTime (Spring)
        expirationDate: formValue.expirationDate ? new Date(formValue.expirationDate).toISOString() : null
      };

      if (this.isEditMode && this.editingCouponId) {
        // Mode édition
        this.couponService.updateCoupon(this.editingCouponId, couponRequest as any).subscribe({
          next: () => {
            this.snackBar.open('✅ Coupon modifié avec succès !', 'Fermer', { 
              duration: 3000,
              panelClass: ['success-snackbar'],
              horizontalPosition: 'end',
              verticalPosition: 'top'
            });
            this.loadCoupons();
            this.closeModal();
          },
          error: (err) => {
            console.error('Erreur lors de la modification du coupon', err);
            this.snackBar.open('❌ Erreur lors de la modification : ' + (err.error?.message || 'Serveur'), 'Fermer', { 
              duration: 5000,
              panelClass: ['error-snackbar'],
              horizontalPosition: 'end',
              verticalPosition: 'top'
            });
          }
        });
      } else {
        // Mode création
        this.couponService.createCoupon(couponRequest as any).subscribe({
          next: () => {
            this.snackBar.open('✅ Coupon créé avec succès !', 'Fermer', { 
              duration: 3000,
              panelClass: ['success-snackbar'],
              horizontalPosition: 'end',
              verticalPosition: 'top'
            });
            this.loadCoupons();
            this.closeModal();
          },
          error: (err) => {
            console.error('Erreur lors de la création du coupon', err);
            this.snackBar.open('❌ Erreur lors de la création : ' + (err.error?.message || 'Serveur'), 'Fermer', { 
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

  deleteCoupon(id: number) {
    if (confirm('Supprimer ce coupon ?')) {
      this.couponService.deleteCoupon(id).subscribe({
        next: () => {
          this.loadCoupons();
          this.snackBar.open('✅ Coupon supprimé', 'Fermer', { 
            duration: 3000,
            panelClass: ['success-snackbar'],
            horizontalPosition: 'end',
            verticalPosition: 'top'
          });
        },
        error: (err) => {
          this.snackBar.open('❌ ' + (err.error?.message || 'Erreur lors de la suppression'), 'Fermer', { 
            duration: 5000,
            panelClass: ['error-snackbar'],
            horizontalPosition: 'end',
            verticalPosition: 'top'
          });
        }
      });
    }
  }

  toggleCouponStatus(coupon: Coupon) {
    const action = coupon.active ? 'désactiver' : 'activer';
    if (confirm(`Voulez-vous ${action} ce coupon ?`)) {
      this.couponService.toggleCouponStatus(coupon.id).subscribe({
        next: () => {
          this.loadCoupons();
          this.snackBar.open(`✅ Coupon ${coupon.active ? 'désactivé' : 'activé'} avec succès`, 'Fermer', { 
            duration: 3000,
            panelClass: ['success-snackbar'],
            horizontalPosition: 'end',
            verticalPosition: 'top'
          });
        },
        error: (err) => {
          this.snackBar.open('❌ ' + (err.error?.message || 'Erreur lors du changement de statut'), 'Fermer', { 
            duration: 5000,
            panelClass: ['error-snackbar'],
            horizontalPosition: 'end',
            verticalPosition: 'top'
          });
        }
      });
    }
  }

  activateCoupon(id: number) {
    this.couponService.activateCoupon(id).subscribe({
      next: () => {
        this.loadCoupons();
        this.snackBar.open('✅ Coupon activé avec succès', 'Fermer', { 
          duration: 3000,
          panelClass: ['success-snackbar'],
          horizontalPosition: 'end',
          verticalPosition: 'top'
        });
      },
      error: (err) => {
        this.snackBar.open('❌ ' + (err.error?.message || 'Impossible d\'activer ce coupon'), 'Fermer', { 
          duration: 5000,
          panelClass: ['error-snackbar'],
          horizontalPosition: 'end',
          verticalPosition: 'top'
        });
      }
    });
  }

  deactivateCoupon(id: number) {
    this.couponService.deactivateCoupon(id).subscribe({
      next: () => {
        this.loadCoupons();
        this.snackBar.open('✅ Coupon désactivé avec succès', 'Fermer', { 
          duration: 3000,
          panelClass: ['success-snackbar'],
          horizontalPosition: 'end',
          verticalPosition: 'top'
        });
      },
      error: (err) => {
        this.snackBar.open('❌ ' + (err.error?.message || 'Erreur lors de la désactivation'), 'Fermer', { 
          duration: 5000,
          panelClass: ['error-snackbar'],
          horizontalPosition: 'end',
          verticalPosition: 'top'
        });
      }
    });
  }

  getActiveCouponsCount(): number {
    return this.coupons.filter(c => c.active).length;
  }

  getExpiredCouponsCount(): number {
    return this.coupons.filter(c => !c.active).length;
  }
}