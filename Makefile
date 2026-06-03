# ─── Kalamkaari Dev Stack ─────────────────────────────────────────────────────
# Usage:
#   make dev        → start everything (Colima + MongoDB + backend + frontend)
#   make stop       → stop MongoDB container (Colima keeps running)
#   make logs       → tail backend logs
#   make status     → show what's running

.PHONY: dev stop logs status colima mongodb backend frontend

# Start the full dev stack in one command
dev: colima mongodb
	@echo ""
	@echo "✓ Colima running"
	@echo "✓ MongoDB running on :27017"
	@echo ""
	@echo "Now open two more terminals and run:"
	@echo "  Terminal 2 → make backend"
	@echo "  Terminal 3 → make frontend"

# Start Colima if not already running
colima:
	@colima status > /dev/null 2>&1 || colima start

# Start MongoDB container
mongodb: colima
	@docker compose up mongodb -d

# Start Spring Boot backend (run this in its own terminal)
backend:
	cd backend && TMPDIR=/tmp ./mvnw spring-boot:run

# Start Vite frontend dev server (run this in its own terminal)
frontend:
	cd frontend && npm run dev

# Stop containers (keeps Colima running so it restarts faster next time)
stop:
	@docker compose stop mongodb
	@echo "MongoDB stopped. Colima is still running."

# Kill the background backend process if started with make dev-all
kill-backend:
	@pkill -f "spring-boot:run" 2>/dev/null && echo "Backend stopped" || echo "Backend was not running"

# Tail backend logs (when run via make dev-all)
logs:
	@tail -f /tmp/kalamkaari-backend.log 2>/dev/null || echo "No background log found. Run 'make backend' in a terminal instead."

# Show running status
status:
	@echo "=== Colima ==="
	@colima status 2>&1 | grep -E "running|not running" || echo "unknown"
	@echo ""
	@echo "=== Containers ==="
	@docker compose ps 2>/dev/null || echo "Docker not available"
	@echo ""
	@echo "=== Ports ==="
	@lsof -i :8080 -i :27017 -i :5173 2>/dev/null | grep LISTEN | awk '{print $$9 " (" $$1 ")"}' || echo "none"
