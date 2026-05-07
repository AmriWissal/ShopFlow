export interface Address {
  id: number;
  street: string;
  city: string;
  zipCode: string;
  country?: string;
  isDefault?: boolean;
}
