export interface OrderItem {
  id: number;
  productId: number;
  productName: string;
  productImage?: string; // Première image du produit
  quantity: number;
  unitPrice: number;
  totalPrice?: number;
}
