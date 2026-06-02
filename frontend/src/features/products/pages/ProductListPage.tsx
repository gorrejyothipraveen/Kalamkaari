import { Link } from "react-router-dom";
import { PlusCircle } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { useProducts } from "../api/productApi";

function formatPrice(paise: number) {
  return new Intl.NumberFormat("en-IN", { style: "currency", currency: "INR" }).format(paise / 100);
}

export function ProductListPage() {
  const { data: products, isLoading, isError } = useProducts();

  return (
    <div className="py-8 px-4 max-w-5xl mx-auto">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-semibold">Products</h1>
        <Button asChild>
          <Link to="/admin/products/new">
            <PlusCircle className="mr-2 h-4 w-4" />
            Add Product
          </Link>
        </Button>
      </div>

      {isLoading && <p className="text-muted-foreground">Loading products…</p>}
      {isError && <p className="text-destructive">Failed to load products.</p>}

      {!isLoading && !isError && products?.length === 0 && (
        <Card>
          <CardContent className="py-12 text-center">
            <p className="text-muted-foreground mb-4">No products yet.</p>
            <Button asChild variant="outline">
              <Link to="/admin/products/new">Create your first product</Link>
            </Button>
          </CardContent>
        </Card>
      )}

      {products && products.length > 0 && (
        <div className="rounded-xl border overflow-hidden">
          <table className="w-full text-sm">
            <thead className="bg-muted text-muted-foreground">
              <tr>
                <th className="px-4 py-3 text-left font-medium">Name</th>
                <th className="px-4 py-3 text-left font-medium">Price</th>
                <th className="px-4 py-3 text-left font-medium">Category</th>
                <th className="px-4 py-3 text-left font-medium">Created</th>
              </tr>
            </thead>
            <tbody className="divide-y">
              {products.map((p) => (
                <tr key={p.id} className="hover:bg-muted/50 transition-colors">
                  <td className="px-4 py-3 font-medium">{p.name}</td>
                  <td className="px-4 py-3">{formatPrice(p.price)}</td>
                  <td className="px-4 py-3 text-muted-foreground">{p.categoryId ?? "—"}</td>
                  <td className="px-4 py-3 text-muted-foreground">
                    {new Date(p.createdAt).toLocaleDateString("en-IN")}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
