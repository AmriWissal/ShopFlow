import type { CartItem } from './cart-item.model';

export interface Cart {
  items: CartItem[];
  totalPrice: number;
}
