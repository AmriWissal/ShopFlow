import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Coupon } from '../models';

@Injectable({
  providedIn: 'root'
})
export class CouponService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/coupons`;

  getCoupons(): Observable<Coupon[]> {
    return this.http.get<Coupon[]>(this.apiUrl);
  }

  getCouponById(id: number): Observable<Coupon> {
    return this.http.get<Coupon>(`${this.apiUrl}/${id}`);
  }

  getCouponByCode(code: string): Observable<Coupon> {
    return this.validateCoupon(code);
  }

  createCoupon(coupon: Partial<Coupon>): Observable<Coupon> {
    return this.http.post<Coupon>(this.apiUrl, coupon);
  }

  updateCoupon(id: number, coupon: Partial<Coupon>): Observable<Coupon> {
    return this.http.put<Coupon>(`${this.apiUrl}/${id}`, coupon);
  }

  deleteCoupon(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  toggleCouponStatus(id: number): Observable<Coupon> {
    return this.http.patch<Coupon>(`${this.apiUrl}/${id}/toggle-status`, {});
  }

  activateCoupon(id: number): Observable<Coupon> {
    return this.http.patch<Coupon>(`${this.apiUrl}/${id}/activate`, {});
  }

  deactivateCoupon(id: number): Observable<Coupon> {
    return this.http.patch<Coupon>(`${this.apiUrl}/${id}/deactivate`, {});
  }

  validateCoupon(code: string): Observable<Coupon> {
    return this.http.get<Coupon>(`${this.apiUrl}/validate/${code}`);
  }
}
