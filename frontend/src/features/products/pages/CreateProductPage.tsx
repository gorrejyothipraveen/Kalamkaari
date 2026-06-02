import { useNavigate } from "react-router-dom";
import { toast } from "sonner";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { ProductForm, type ProductFormValues } from "../components/ProductForm";
import { useCreateProduct } from "../api/productApi";

export function CreateProductPage() {
  const navigate = useNavigate();
  const { mutate, isPending } = useCreateProduct();

  function handleSubmit(values: ProductFormValues) {
    // Convert display price (₹) to storage unit (paise)
    const priceInPaise = Math.round(Number(values.price) * 100);
    const imageUrls = values.imageUrl ? [values.imageUrl] : [];

    mutate(
      {
        name: values.name,
        description: values.description,
        price: priceInPaise,
        imageUrls,
        categoryId: values.categoryId || undefined,
      },
      {
        onSuccess: (product) => {
          toast.success(`"${product.name}" created successfully`);
          navigate("/admin/products");
        },
        onError: () => {
          toast.error("Failed to create product. Please try again.");
        },
      }
    );
  }

  return (
    <div className="max-w-xl mx-auto py-8 px-4">
      <Card>
        <CardHeader>
          <CardTitle>New Product</CardTitle>
          <CardDescription>
            Add a new product to your store. Fields marked with * are required.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <ProductForm onSubmit={handleSubmit} isLoading={isPending} />
        </CardContent>
      </Card>
    </div>
  );
}
