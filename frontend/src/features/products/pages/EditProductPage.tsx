import { useParams, useNavigate } from "react-router-dom";
import { toast } from "sonner";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { ProductForm, type ProductFormValues } from "../components/ProductForm";
import { useProductById, useUpdateProduct } from "../api/productApi";

export function EditProductPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const { data: product, isLoading, isError } = useProductById(id!);
  const { mutate, isPending } = useUpdateProduct();

  function handleSubmit(values: ProductFormValues) {
    const priceInPaise = Math.round(Number(values.price) * 100);
    const imageUrls = values.imageUrl ? [values.imageUrl] : [];

    mutate(
      {
        id: id!,
        payload: {
          name: values.name,
          description: values.description,
          price: priceInPaise,
          imageUrls,
          categoryId: values.categoryId || undefined,
        },
      },
      {
        onSuccess: (updated) => {
          toast.success(`"${updated.name}" updated successfully`);
          navigate("/admin/products");
        },
        onError: () => {
          toast.error("Failed to update product. Please try again.");
        },
      }
    );
  }

  if (isLoading) {
    return (
      <div className="max-w-xl mx-auto py-8 px-4">
        <p className="text-muted-foreground">Loading product…</p>
      </div>
    );
  }

  if (isError || !product) {
    return (
      <div className="max-w-xl mx-auto py-8 px-4">
        <p className="text-destructive">Product not found.</p>
      </div>
    );
  }

  const defaultValues: ProductFormValues = {
    name: product.name,
    description: product.description ?? "",
    price: (product.price / 100).toString(),
    imageUrl: product.imageUrls[0] ?? "",
    categoryId: product.categoryId ?? "",
  };

  return (
    <div className="max-w-xl mx-auto py-8 px-4">
      <Card>
        <CardHeader>
          <CardTitle>Edit Product</CardTitle>
          <CardDescription>
            All fields are pre-filled with current values. Update what you need and save.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <ProductForm
            onSubmit={handleSubmit}
            isLoading={isPending}
            defaultValues={defaultValues}
            submitLabel="Update Product"
          />
        </CardContent>
      </Card>
    </div>
  );
}
