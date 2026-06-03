import { createBrowserRouter, Navigate } from "react-router-dom";
import { AdminLayout } from "@/components/layout/AdminLayout";
import { ProductListPage } from "@/features/products/pages/ProductListPage";
import { CreateProductPage } from "@/features/products/pages/CreateProductPage";
import { EditProductPage } from "@/features/products/pages/EditProductPage";
import { CategoryListPage } from "@/features/categories/pages/CategoryListPage";
import { InventoryPage } from "@/features/inventory/pages/InventoryPage";

export const router = createBrowserRouter([
  {
    path: "/",
    element: <Navigate to="/admin/products" replace />,
  },
  {
    path: "/admin",
    element: <AdminLayout />,
    children: [
      { index: true, element: <Navigate to="/admin/products" replace /> },
      { path: "products",      element: <ProductListPage /> },
      { path: "products/new",  element: <CreateProductPage /> },
      { path: "products/:id",  element: <EditProductPage /> },
      { path: "categories",    element: <CategoryListPage /> },
      { path: "inventory",     element: <InventoryPage /> },
    ],
  },
]);
