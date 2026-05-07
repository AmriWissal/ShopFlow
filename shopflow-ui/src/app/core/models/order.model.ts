import type { Coupon } from './coupon.model';
import type { OrderItem } from './order-item.model';
import type { User } from './user.model';

export interface Order {
  id: number;
  orderNumber: string;
  totalPrice: number;
  status: 'PENDING' | 'CONFIRMED' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';
  shippingAddress: string;
  createdAt: string;
  user: User;
  items: OrderItem[];
  coupon?: Coupon;
}
