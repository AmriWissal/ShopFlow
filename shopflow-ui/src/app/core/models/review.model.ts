export interface Review {
  id: number;
  userName: string;
  customerName?: string;
  productName?: string;
  note: number;
  comment: string;
  approved: boolean;
  createdAt: string;
}
