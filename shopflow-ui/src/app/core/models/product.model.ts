import type { Category } from './category.model';
import type { Review } from './review.model';
import type { User } from './user.model';
import type { ProductVariant } from './product-variant.model';

export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  promoPrice?: number;
  stock: number;
  active: boolean;
  createdAt: string;
  categories: Category[];
  images: string[];
  averageNote: number;
  variants?: ProductVariant[];
  reviews?: Review[];
  seller?: User;
}
