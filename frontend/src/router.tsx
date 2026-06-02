import { createBrowserRouter, Navigate } from "react-router-dom";
import { AdminLayout } from "@/components/layout/AdminLayout";
import { ProductListPage } from "@/features/products/pages/ProductListPage";
import { CreateProductPage } from "@/features/products/pages/CreateProductPage";
import { EditProductPage } from "@/features/products/pages/EditProductPage";

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
      { path: "products", element: <ProductListPage /> },
      { path: "products/new", element: <CreateProductPage /> },
      { path: "products/:id", element: <EditProductPage /> },
    ],
  },
]);
