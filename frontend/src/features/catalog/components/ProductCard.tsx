import { ImageIcon } from "lucide-react";
import type { ProductResponse } from "@/features/products/api/productApi";

interface ProductCardProps {
  product: ProductResponse;
}

function formatPrice(paise: number) {
  return new Intl.NumberFormat("en-IN", { style: "currency", currency: "INR" }).format(paise / 100);
}

export function ProductCard({ product }: ProductCardProps) {
  const primaryImage = product.imageUrls[0];

  return (
    <div className="rounded-xl border bg-background overflow-hidden hover:shadow-md transition-shadow cursor-pointer">
      {/* Image */}
      <div className="w-full h-48 bg-muted flex items-center justify-center overflow-hidden">
        {primaryImage ? (
          <img
            src={primaryImage}
            alt={product.name}
            className="w-full h-full object-cover"
            onError={(e) => {
              e.currentTarget.style.display = "none";
              e.currentTarget.nextElementSibling?.removeAttribute("style");
            }}
          />
        ) : null}
        <div
          className="flex flex-col items-center justify-center text-muted-foreground"
          style={primaryImage ? { display: "none" } : {}}
        >
          <ImageIcon className="h-10 w-10 mb-1 opacity-40" />
          <span className="text-xs">No image</span>
        </div>
      </div>

      {/* Info */}
      <div className="p-4">
        <h3 className="font-medium text-sm leading-tight line-clamp-2">{product.name}</h3>
        <p className="text-base font-semibold mt-2 text-primary">{formatPrice(product.price)}</p>
      </div>
    </div>
  );
}
