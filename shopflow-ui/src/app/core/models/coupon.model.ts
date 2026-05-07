export interface Coupon {
  id: number;
  code: string;
  value: number;
  type: 'PERCENT' | 'FIXED';
  expirationDate: string;
  usageLimit: number;
  currentUsage: number;
  active: boolean;
}
