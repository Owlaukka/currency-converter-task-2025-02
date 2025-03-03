# Nosto Technical Assignment (Currency Converter)

This project consists of a frontend and backend application. For detailed documentation of each component, see their respective README files:

- [Frontend Documentation](./frontend/README.md)
- [Backend Documentation](./backend/README.md)

## Prerequisites

- Node.js (v22 or newer)
- pnpm
- JDK 21 or newer
- Make

## Getting Started

### Development Setup

1. Clone the repository
2. Build both applications:

```bash
make build
```

### Running the Application

Start both frontend and backend development servers (you need to set SWOP_URL and SWOP_API_KEY to valid values for this to work with the real integration):

```bash
make start ENV="SWOP_URL=https://swop.cx/graphql SWOP_API_KEY=<your-api-key>"
```

Or start them individually:

```bash
# Frontend only
make start-frontend

# Backend only
make start-backend ENV="SWOP_URL=https://swop.cx/graphql SWOP_API_KEY=<your-api-key>"
```

You may also start both frontend and backend individually from inside their directories.

The frontend will be available at `http://localhost:5173`
The backend will be available at `http://localhost:8080`

Both frontend and backend have live reload so most of the time manual restarts are not necessary.

### Development

The project uses a Makefile to orchestrate common tasks. Available commands:

- `make build` - Build both frontend and backend
- `make clean` - Clean build artifacts
- `make start` - Start both development servers
- `make start-frontend` - Start frontend development server
- `make start-backend` - Start backend development server

For detailed development instructions, refer to the respective README files in the frontend and backend directories.
