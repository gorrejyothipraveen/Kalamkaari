import { useState } from "react";
import { ShoppingBag, ChevronLeft, ChevronRight } from "lucide-react";
import { Button } from "@/components/ui/button";
import { ProductCard } from "../components/ProductCard";
import { useCatalog } from "../api/catalogApi";

const PAGE_SIZE = 12;

export function CatalogPage() {
  const [page, setPage] = useState(0);
  const { data, isLoading, isError } = useCatalog(page, PAGE_SIZE);

  return (
    <div className="min-h-screen bg-background">
      {/* Minimal storefront header */}
      <header className="border-b h-14 flex items-center px-6">
        <div className="flex items-center gap-2 font-semibold text-lg">
          <ShoppingBag className="h-5 w-5" />
          Kalamkaari
        </div>
      </header>

      <main className="max-w-6xl mx-auto px-4 py-8">
        <div className="mb-6">
          <h1 className="text-2xl font-semibold">Products</h1>
          {data && (
            <p className="text-sm text-muted-foreground mt-0.5">
              {data.totalElements} product{data.totalElements !== 1 ? "s" : ""} available
            </p>
          )}
        </div>

        {/* Loading */}
        {isLoading && (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {Array.from({ length: PAGE_SIZE }).map((_, i) => (
              <div key={i} className="rounded-xl border bg-muted/30 h-64 animate-pulse" />
            ))}
          </div>
        )}

        {/* Error */}
        {isError && (
          <p className="text-destructive text-center py-16">
            Failed to load products. Please try again later.
          </p>
        )}

        {/* Empty state */}
        {!isLoading && !isError && data?.content.length === 0 && (
          <div className="text-center py-24">
            <ShoppingBag className="h-12 w-12 mx-auto text-muted-foreground mb-4 opacity-40" />
            <h2 className="text-lg font-medium text-muted-foreground">No products available</h2>
            <p className="text-sm text-muted-foreground mt-1">
              Check back soon — new products are on their way.
            </p>
          </div>
        )}

        {/* Product grid */}
        {data && data.content.length > 0 && (
          <>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
              {data.content.map((product) => (
                <ProductCard key={product.id} product={product} />
              ))}
            </div>

            {/* Pagination — only shown when more than one page */}
            {data.totalPages > 1 && (
              <div className="flex items-center justify-center gap-4 mt-10">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setPage((p) => Math.max(0, p - 1))}
                  disabled={data.first}
                >
                  <ChevronLeft className="h-4 w-4 mr-1" />
                  Previous
                </Button>

                <span className="text-sm text-muted-foreground">
                  Page {data.number + 1} of {data.totalPages}
                </span>

                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setPage((p) => Math.min(data.totalPages - 1, p + 1))}
                  disabled={data.last}
                >
                  Next
                  <ChevronRight className="h-4 w-4 ml-1" />
                </Button>
              </div>
            )}
          </>
        )}
      </main>
    </div>
  );
}
