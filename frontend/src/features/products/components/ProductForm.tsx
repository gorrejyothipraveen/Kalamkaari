import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";

const schema = z.object({
  name: z.string().min(1, "Product name is required").max(200),
  description: z.string().max(2000).optional(),
  price: z
    .string()
    .min(1, "Price is required")
    .refine((v) => !isNaN(Number(v)) && Number(v) > 0, "Price must be greater than zero"),
  imageUrl: z.string().url("Must be a valid URL").or(z.literal("")).optional(),
  categoryId: z.string().optional(),
});

export type ProductFormValues = z.infer<typeof schema>;

interface ProductFormProps {
  onSubmit: (values: ProductFormValues) => void;
  isLoading: boolean;
  defaultValues?: Partial<ProductFormValues>;
  submitLabel?: string;
}

export function ProductForm({
  onSubmit,
  isLoading,
  defaultValues,
  submitLabel = "Create Product",
}: ProductFormProps) {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ProductFormValues>({
    resolver: zodResolver(schema),
    defaultValues,
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
      {/* Name */}
      <div className="space-y-1.5">
        <Label htmlFor="name">
          Product Name <span className="text-destructive">*</span>
        </Label>
        <Input id="name" placeholder="e.g. Kalamkaari Saree" {...register("name")} />
        {errors.name && <p className="text-xs text-destructive">{errors.name.message}</p>}
      </div>

      {/* Description */}
      <div className="space-y-1.5">
        <Label htmlFor="description">Description</Label>
        <Textarea
          id="description"
          placeholder="Describe the product..."
          rows={4}
          {...register("description")}
        />
        {errors.description && (
          <p className="text-xs text-destructive">{errors.description.message}</p>
        )}
      </div>

      {/* Price */}
      <div className="space-y-1.5">
        <Label htmlFor="price">
          Price (₹) <span className="text-destructive">*</span>
        </Label>
        <Input
          id="price"
          type="number"
          min="0.01"
          step="0.01"
          placeholder="e.g. 2500"
          {...register("price")}
        />
        {errors.price && <p className="text-xs text-destructive">{errors.price.message}</p>}
      </div>

      {/* Image URL */}
      <div className="space-y-1.5">
        <Label htmlFor="imageUrl">Image URL</Label>
        <Input
          id="imageUrl"
          type="url"
          placeholder="https://example.com/product.jpg"
          {...register("imageUrl")}
        />
        {errors.imageUrl && <p className="text-xs text-destructive">{errors.imageUrl.message}</p>}
      </div>

      {/* Category */}
      <div className="space-y-1.5">
        <Label htmlFor="categoryId">Category</Label>
        <Input id="categoryId" placeholder="Category ID (optional)" {...register("categoryId")} />
      </div>

      <Button type="submit" disabled={isLoading} className="w-full">
        {isLoading ? "Saving…" : submitLabel}
      </Button>
    </form>
  );
}
