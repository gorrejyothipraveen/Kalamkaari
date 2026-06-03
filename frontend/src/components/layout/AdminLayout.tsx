import { Link, NavLink, Outlet } from "react-router-dom";
import { ShoppingBag, Package, Tags } from "lucide-react";
import { cn } from "@/lib/utils";

const navItems = [
  { to: "/admin/products", label: "Products", icon: Package },
  { to: "/admin/categories", label: "Categories", icon: Tags },
];

export function AdminLayout() {
  return (
    <div className="min-h-screen flex flex-col">
      {/* Top bar */}
      <header className="h-14 border-b flex items-center px-6 bg-background">
        <Link to="/admin/products" className="flex items-center gap-2 font-semibold text-lg">
          <ShoppingBag className="h-5 w-5" />
          Kalamkaari Admin
        </Link>
      </header>

      <div className="flex flex-1">
        {/* Sidebar */}
        <aside className="w-56 border-r bg-muted/40 py-4 px-2 hidden md:block">
          <nav className="space-y-1">
            {navItems.map(({ to, label, icon: Icon }) => (
              <NavLink
                key={to}
                to={to}
                className={({ isActive }) =>
                  cn(
                    "flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium transition-colors",
                    isActive
                      ? "bg-primary text-primary-foreground"
                      : "text-muted-foreground hover:bg-accent hover:text-accent-foreground"
                  )
                }
              >
                <Icon className="h-4 w-4" />
                {label}
              </NavLink>
            ))}
          </nav>
        </aside>

        {/* Main content */}
        <main className="flex-1 overflow-auto">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
