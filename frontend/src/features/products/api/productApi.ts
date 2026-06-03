import axios from "axios";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

export interface ProductResponse {
  id: string;
  name: string;
  description: string | null;
  price: number;
  imageUrls: string[];
  categoryIds: string[];
  stockQuantity: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateProductPayload {
  name: string;
  description?: string;
  price: number;
  imageUrls?: string[];
  categoryIds?: string[];
  stockQuantity?: number;
}

export interface UpdateProductPayload {
  name: string;
  description?: string;
  price: number;
  imageUrls?: string[];
  categoryIds?: string[];
  stockQuantity?: number;
}

const BASE = "/api/admin/products";

async function fetchProducts(): Promise<ProductResponse[]> {
  const { data } = await axios.get<ProductResponse[]>(BASE);
  return data;
}

async function fetchProductById(id: string): Promise<ProductResponse> {
  const { data } = await axios.get<ProductResponse>(`${BASE}/${id}`);
  return data;
}

async function createProduct(payload: CreateProductPayload): Promise<ProductResponse> {
  const { data } = await axios.post<ProductResponse>(BASE, payload);
  return data;
}

async function updateProduct(id: string, payload: UpdateProductPayload): Promise<ProductResponse> {
  const { data } = await axios.put<ProductResponse>(`${BASE}/${id}`, payload);
  return data;
}

async function updateStock(id: string, quantity: number): Promise<ProductResponse> {
  const { data } = await axios.patch<ProductResponse>(`${BASE}/${id}/stock`, { quantity });
  return data;
}

async function deleteProduct(id: string): Promise<void> {
  await axios.delete(`${BASE}/${id}`);
}

export function useProducts() {
  return useQuery({ queryKey: ["products"], queryFn: fetchProducts });
}

export function useProductById(id: string) {
  return useQuery({
    queryKey: ["products", id],
    queryFn: () => fetchProductById(id),
    enabled: !!id,
  });
}

export function useCreateProduct() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: createProduct,
    onSuccess: () => qc.invalidateQueries({ queryKey: ["products"] }),
  });
}

export function useUpdateProduct() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, payload }: { id: string; payload: UpdateProductPayload }) =>
      updateProduct(id, payload),
    onSuccess: (_data, { id }) => {
      qc.invalidateQueries({ queryKey: ["products"] });
      qc.invalidateQueries({ queryKey: ["products", id] });
    },
  });
}

export function useUpdateStock() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, quantity }: { id: string; quantity: number }) => updateStock(id, quantity),
    onSuccess: (_data, { id }) => {
      qc.invalidateQueries({ queryKey: ["products"] });
      qc.invalidateQueries({ queryKey: ["products", id] });
    },
  });
}

export function useDeleteProduct() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: deleteProduct,
    onSuccess: () => qc.invalidateQueries({ queryKey: ["products"] }),
  });
}
