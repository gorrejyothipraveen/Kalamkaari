import { useState } from "react";
import axios from "axios";
import { Tags, Pencil, Check, X, Trash2 } from "lucide-react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import {
  AlertDialog,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog";
import {
  useCategories,
  useCreateCategory,
  useRenameCategory,
  useDeleteCategory,
} from "../api/categoryApi";

export function CategoryListPage() {
  const { data: categories, isLoading, isError } = useCategories();
  const { mutate: createCategory, isPending: isCreating } = useCreateCategory();
  const { mutate: renameCategory, isPending: isRenaming } = useRenameCategory();
  const { mutate: deleteCategory, isPending: isDeleting } = useDeleteCategory();

  const [newName, setNewName] = useState("");
  const [editingId, setEditingId] = useState<string | null>(null);
  const [editingName, setEditingName] = useState("");
  const [deletingId, setDeletingId] = useState<string | null>(null);
  const deletingCategory = categories?.find((c) => c.id === deletingId);

  function handleCreate(e: React.FormEvent) {
    e.preventDefault();
    if (!newName.trim()) return;
    createCategory(newName.trim(), {
      onSuccess: () => {
        toast.success(`Category "${newName.trim()}" created`);
        setNewName("");
      },
      onError: () => toast.error("Failed to create category"),
    });
  }

  function startEditing(id: string, currentName: string) {
    setEditingId(id);
    setEditingName(currentName);
  }

  function handleRename(id: string) {
    if (!editingName.trim()) return;
    const name = editingName.trim();
    renameCategory(
      { id, name },
      {
        onSuccess: () => {
          toast.success(`Renamed to "${name}"`);
          setEditingId(null);
        },
        onError: () => toast.error("Failed to rename category"),
      }
    );
  }

  function handleDeleteConfirm() {
    if (!deletingId) return;
    const categoryName = deletingCategory?.name ?? "Category";
    deleteCategory(deletingId, {
      onSuccess: () => {
        toast.success(`"${categoryName}" deleted`);
        setDeletingId(null);
      },
      onError: (err: unknown) => {
        const isInUse =
          axios.isAxiosError(err) && err.response?.status === 409;
        toast.error(
          isInUse
            ? "This category is assigned to one or more products and cannot be deleted."
            : "Failed to delete category"
        );
        setDeletingId(null);
      },
    });
  }

  return (
    <div className="py-8 px-4 max-w-3xl mx-auto space-y-6">
      {/* Create */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Tags className="h-5 w-5" />
            Categories
          </CardTitle>
          <CardDescription>
            Organise your products by creating and managing categories.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleCreate} className="flex gap-2">
            <Input
              placeholder="New category name…"
              value={newName}
              onChange={(e) => setNewName(e.target.value)}
              maxLength={100}
              className="flex-1"
            />
            <Button type="submit" disabled={isCreating || !newName.trim()}>
              {isCreating ? "Creating…" : "Create"}
            </Button>
          </form>
        </CardContent>
      </Card>

      {/* List */}
      {isLoading && <p className="text-muted-foreground text-sm">Loading categories…</p>}
      {isError && <p className="text-destructive text-sm">Failed to load categories.</p>}

      {!isLoading && !isError && categories?.length === 0 && (
        <p className="text-muted-foreground text-sm">No categories yet. Create one above.</p>
      )}

      {categories && categories.length > 0 && (
        <div className="rounded-xl border overflow-hidden">
          <table className="w-full text-sm">
            <thead className="bg-muted text-muted-foreground">
              <tr>
                <th className="px-4 py-3 text-left font-medium">Name</th>
                <th className="px-4 py-3 text-left font-medium">Created</th>
                <th className="px-4 py-3 text-left font-medium">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y">
              {categories.map((cat) => (
                <tr key={cat.id} className="hover:bg-muted/50 transition-colors">
                  <td className="px-4 py-2">
                    {editingId === cat.id ? (
                      <Input
                        value={editingName}
                        onChange={(e) => setEditingName(e.target.value)}
                        onKeyDown={(e) => {
                          if (e.key === "Enter") handleRename(cat.id);
                          if (e.key === "Escape") setEditingId(null);
                        }}
                        maxLength={100}
                        autoFocus
                        className="h-7 py-0 text-sm"
                      />
                    ) : (
                      <span className="font-medium">{cat.name}</span>
                    )}
                  </td>
                  <td className="px-4 py-2 text-muted-foreground">
                    {new Date(cat.createdAt).toLocaleDateString("en-IN")}
                  </td>
                  <td className="px-4 py-2">
                    {editingId === cat.id ? (
                      <div className="flex items-center gap-1">
                        <Button
                          size="sm"
                          variant="ghost"
                          className="h-7 px-2 text-green-600 hover:text-green-700"
                          disabled={isRenaming}
                          onClick={() => handleRename(cat.id)}
                        >
                          <Check className="h-3.5 w-3.5" />
                        </Button>
                        <Button
                          size="sm"
                          variant="ghost"
                          className="h-7 px-2"
                          onClick={() => setEditingId(null)}
                        >
                          <X className="h-3.5 w-3.5" />
                        </Button>
                      </div>
                    ) : (
                      <div className="flex items-center gap-1">
                        <Button
                          size="sm"
                          variant="ghost"
                          onClick={() => startEditing(cat.id, cat.name)}
                        >
                          <Pencil className="h-3.5 w-3.5 mr-1" />
                          Rename
                        </Button>
                        <Button
                          size="sm"
                          variant="ghost"
                          className="text-destructive hover:text-destructive hover:bg-destructive/10"
                          onClick={() => setDeletingId(cat.id)}
                        >
                          <Trash2 className="h-3.5 w-3.5 mr-1" />
                          Delete
                        </Button>
                      </div>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Delete confirmation dialog */}
      <AlertDialog open={!!deletingId} onOpenChange={(open) => !open && setDeletingId(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Delete category?</AlertDialogTitle>
            <AlertDialogDescription>
              <strong>{deletingCategory?.name}</strong> will be permanently removed.
              This will fail if any products are still assigned to this category.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel disabled={isDeleting}>Cancel</AlertDialogCancel>
            <Button variant="destructive" disabled={isDeleting} onClick={handleDeleteConfirm}>
              {isDeleting ? "Deleting…" : "Delete"}
            </Button>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}
