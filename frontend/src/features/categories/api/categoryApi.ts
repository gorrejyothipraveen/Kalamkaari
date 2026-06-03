import axios from "axios";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

export interface CategoryResponse {
  id: string;
  name: string;
  createdAt: string;
  updatedAt: string;
}

const BASE = "/api/admin/categories";

async function fetchCategories(): Promise<CategoryResponse[]> {
  const { data } = await axios.get<CategoryResponse[]>(BASE);
  return data;
}

async function createCategory(name: string): Promise<CategoryResponse> {
  const { data } = await axios.post<CategoryResponse>(BASE, { name });
  return data;
}

async function renameCategory(id: string, name: string): Promise<CategoryResponse> {
  const { data } = await axios.put<CategoryResponse>(`${BASE}/${id}`, { name });
  return data;
}

async function deleteCategory(id: string): Promise<void> {
  await axios.delete(`${BASE}/${id}`);
}

export function useCategories() {
  return useQuery({ queryKey: ["categories"], queryFn: fetchCategories });
}

export function useCreateCategory() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: createCategory,
    onSuccess: () => qc.invalidateQueries({ queryKey: ["categories"] }),
  });
}

export function useRenameCategory() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, name }: { id: string; name: string }) => renameCategory(id, name),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["categories"] }),
  });
}

export function useDeleteCategory() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: deleteCategory,
    onSuccess: () => qc.invalidateQueries({ queryKey: ["categories"] }),
  });
}
