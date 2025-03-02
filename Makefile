ENV ?=

.PHONY: all clean clean-frontend clean-backend build-frontend build-backend start start-frontend start-backend

# Default target
all: build

# Clean build artifacts
clean: clean-frontend clean-backend

clean-frontend:
	cd frontend && rm -rf dist/

clean-backend:
	cd backend && ./gradlew clean

# Build frontend
build-frontend:
	cd frontend && pnpm install && pnpm build

# Build backend
build-backend:
	cd backend && ./gradlew build

# Build everything
build: build-frontend build-backend

# Start development servers
start-frontend:
	cd frontend && pnpm dev

start-backend:
	cd backend && $(ENV) ./gradlew quarkusDev

start:
	make -j2 start-frontend start-backend
