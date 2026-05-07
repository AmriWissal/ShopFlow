import { Product } from './product.model';

export interface Favorite {
  id: number;
  product: Product;
  createdAt: string;
}
