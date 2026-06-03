import axios from "axios";
import { useQuery } from "@tanstack/react-query";
import type { ProductResponse } from "@/features/products/api/productApi";

export interface CatalogPage {
  content: ProductResponse[];
  totalElements: number;
  totalPages: number;
  number: number;   // current page, 0-indexed
  size: number;
  first: boolean;
  last: boolean;
}

const BASE = "/api/products";
const DEFAULT_PAGE_SIZE = 12;

async function fetchCatalog(page: number, size: number): Promise<CatalogPage> {
  const { data } = await axios.get<CatalogPage>(BASE, { params: { page, size } });
  return data;
}

export function useCatalog(page = 0, size = DEFAULT_PAGE_SIZE) {
  return useQuery({
    queryKey: ["catalog", page, size],
    queryFn: () => fetchCatalog(page, size),
    staleTime: 1000 * 60,
  });
}
