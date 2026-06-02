import axios from "axios";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

export interface ProductResponse {
  id: string;
  name: string;
  description: string | null;
  price: number;
  imageUrls: string[];
  categoryId: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CreateProductPayload {
  name: string;
  description?: string;
  price: number;
  imageUrls?: string[];
  categoryId?: string;
}

const BASE = "/api/admin/products";

async function fetchProducts(): Promise<ProductResponse[]> {
  const { data } = await axios.get<ProductResponse[]>(BASE);
  return data;
}

async function createProduct(payload: CreateProductPayload): Promise<ProductResponse> {
  const { data } = await axios.post<ProductResponse>(BASE, payload);
  return data;
}

export function useProducts() {
  return useQuery({ queryKey: ["products"], queryFn: fetchProducts });
}

export function useCreateProduct() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: createProduct,
    onSuccess: () => qc.invalidateQueries({ queryKey: ["products"] }),
  });
}
