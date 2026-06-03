import { useState } from "react";
import { ArrowUpDown, ArrowUp, ArrowDown, Pencil, Check, X } from "lucide-react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent } from "@/components/ui/card";
import { cn } from "@/lib/utils";
import { useInventory, useUpdateStock, type ProductQueryParams } from "@/features/products/api/productApi";

type FilterValue = ProductQueryParams["filter"] | undefined;
type SortValue   = ProductQueryParams["sort"]   | undefined;

const LOW_STOCK_THRESHOLD = 5;

function StockBadge({ qty }: { qty: number }) {
  if (qty === 0)
    return (
      <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-destructive/10 text-destructive">
        Out of stock
      </span>
    );
  if (qty <= LOW_STOCK_THRESHOLD)
    return (
      <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-amber-100 text-amber-700">
        Low stock
      </span>
    );
  return (
    <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-green-100 text-green-700">
      In stock
    </span>
  );
}

const FILTERS: { label: string; value: FilterValue }[] = [
  { label: "All",          value: undefined     },
  { label: "Low Stock",    value: "low_stock"   },
  { label: "Out of Stock", value: "out_of_stock" },
];

export function InventoryPage() {
  const [activeFilter, setActiveFilter] = useState<FilterValue>(undefined);
  const [sortDir, setSortDir]           = useState<SortValue>(undefined);

  const params: ProductQueryParams = {
    ...(activeFilter && { filter: activeFilter }),
    ...(sortDir      && { sort:   sortDir      }),
  };

  const { data: products, isLoading, isError } = useInventory(
    Object.keys(params).length ? params : undefined
  );

  const { mutate: updateStock, isPending: isUpdating } = useUpdateStock();

  const [editingId,  setEditingId]  = useState<string | null>(null);
  const [editingQty, setEditingQty] = useState<string>("");

  function toggleSort() {
    setSortDir((prev) =>
      prev === undefined ? "stock_asc" : prev === "stock_asc" ? "stock_desc" : undefined
    );
  }

  function startEditing(id: string, current: number) {
    setEditingId(id);
    setEditingQty(current.toString());
  }

  function handleSave(productId: string) {
    const qty = parseInt(editingQty, 10);
    if (isNaN(qty) || qty < 0) {
      toast.error("Stock quantity must be 0 or greater");
      return;
    }
    updateStock(
      { id: productId, quantity: qty },
      {
        onSuccess: () => { toast.success("Stock updated"); setEditingId(null); },
        onError:   () => { toast.error("Failed to update stock"); setEditingId(null); },
      }
    );
  }

  const SortIcon =
    sortDir === "stock_asc"  ? ArrowUp :
    sortDir === "stock_desc" ? ArrowDown : ArrowUpDown;

  const outOfStockCount = products?.filter((p) => !p.available).length ?? 0;

  return (
    <div className="py-8 px-4 max-w-5xl mx-auto space-y-5">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-semibold">Inventory</h1>
          <p className="text-sm text-muted-foreground mt-0.5">
            Monitor stock levels across all products.
            {outOfStockCount > 0 && (
              <span className="ml-2 text-destructive font-medium">
                {outOfStockCount} out-of-stock product{outOfStockCount !== 1 ? "s" : ""} need replenishment.
              </span>
            )}
          </p>
        </div>
      </div>

      {/* Filter buttons */}
      <div className="flex gap-2">
        {FILTERS.map(({ label, value }) => (
          <Button
            key={label}
            variant={activeFilter === value ? "default" : "outline"}
            size="sm"
            onClick={() => setActiveFilter(value)}
          >
            {label}
          </Button>
        ))}
      </div>

      {isLoading && <p className="text-muted-foreground text-sm">Loading inventory…</p>}
      {isError   && <p className="text-destructive text-sm">Failed to load inventory.</p>}

      {!isLoading && !isError && products?.length === 0 && (
        <Card>
          <CardContent className="py-10 text-center">
            <p className="text-muted-foreground">
              {activeFilter ? "No products match this filter." : "No products found."}
            </p>
          </CardContent>
        </Card>
      )}

      {products && products.length > 0 && (
        <div className="rounded-xl border overflow-hidden">
          <table className="w-full text-sm">
            <thead className="bg-muted text-muted-foreground">
              <tr>
                <th className="px-4 py-3 text-left font-medium">Product</th>
                <th
                  className="px-4 py-3 text-left font-medium cursor-pointer select-none hover:text-foreground"
                  onClick={toggleSort}
                >
                  <span className="inline-flex items-center gap-1">
                    Stock <SortIcon className="h-3.5 w-3.5" />
                  </span>
                </th>
                <th className="px-4 py-3 text-left font-medium">Status</th>
                <th className="px-4 py-3 text-left font-medium">Price</th>
                <th className="px-4 py-3 text-left font-medium">Update Stock</th>
              </tr>
            </thead>
            <tbody className="divide-y">
              {products.map((p) => (
                <tr
                  key={p.id}
                  className={cn(
                    "transition-colors",
                    !p.available
                      ? "bg-destructive/5 hover:bg-destructive/10"
                      : "hover:bg-muted/50"
                  )}
                >
                  <td className="px-4 py-3 font-medium">{p.name}</td>

                  {/* Stock qty — inline editable */}
                  <td className="px-4 py-3">
                    {editingId === p.id ? (
                      <Input
                        type="number"
                        min="0"
                        step="1"
                        value={editingQty}
                        onChange={(e) => setEditingQty(e.target.value)}
                        onKeyDown={(e) => {
                          if (e.key === "Enter")  handleSave(p.id);
                          if (e.key === "Escape") setEditingId(null);
                        }}
                        autoFocus
                        className="h-7 w-24 py-0 text-sm"
                      />
                    ) : (
                      <span className={cn("font-mono", !p.available && "text-destructive font-semibold")}>
                        {p.stockQuantity}
                      </span>
                    )}
                  </td>

                  <td className="px-4 py-3">
                    <StockBadge qty={p.stockQuantity} />
                  </td>

                  <td className="px-4 py-3 text-muted-foreground">
                    {new Intl.NumberFormat("en-IN", { style: "currency", currency: "INR" })
                      .format(p.price / 100)}
                  </td>

                  {/* Actions */}
                  <td className="px-4 py-3">
                    {editingId === p.id ? (
                      <div className="flex items-center gap-1">
                        <Button
                          size="sm" variant="ghost"
                          className="h-7 px-2 text-green-600 hover:text-green-700"
                          disabled={isUpdating}
                          onClick={() => handleSave(p.id)}
                        >
                          <Check className="h-3.5 w-3.5" />
                        </Button>
                        <Button
                          size="sm" variant="ghost"
                          className="h-7 px-2"
                          onClick={() => setEditingId(null)}
                        >
                          <X className="h-3.5 w-3.5" />
                        </Button>
                      </div>
                    ) : (
                      <Button
                        size="sm" variant="ghost"
                        onClick={() => startEditing(p.id, p.stockQuantity)}
                      >
                        <Pencil className="h-3.5 w-3.5 mr-1" />
                        Edit
                      </Button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Summary footer */}
      {products && products.length > 0 && (
        <p className="text-xs text-muted-foreground">
          Showing {products.length} product{products.length !== 1 ? "s" : ""}.
          {" "}Low-stock threshold: ≤ {LOW_STOCK_THRESHOLD} units.
        </p>
      )}
    </div>
  );
}
