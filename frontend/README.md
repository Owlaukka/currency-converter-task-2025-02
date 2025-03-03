# Currency Converter UI

A React-based currency conversion application that allows users to convert amounts between different currencies using a RESTful API.

## Features

- Currency conversion with source and target currency selection
- Amount input with locale-aware formatting
- Form validation for:
  - 3-letter currency codes
  - Required amount input
- Loading states during API calls
- Error handling for API responses
- Responsive layout
- Accessibility focused implementation
- Comprehensive test coverage

## Development

### Prerequisites

- Node.js 22+
- pnpm
  - npm or yarn might work but not guaranteed

### Setup

```bash
# Install dependencies
pnpm install

# Start development server
pnpm dev
```

### Available Scripts

- `pnpm dev` - Start development server
- `pnpm build` - Build for production
- `pnpm preview` - Preview production build
- `pnpm test` - Run tests
- `pnpm test:watch` - Run tests in watch mode
- `pnpm test:coverage` - Generate test coverage report
- `pnpm test:ui` - Open test UI
- `pnpm lint` - Run ESLint and Prettier check
- `pnpm format` - Format code with Prettier
- `pnpm format:check` - Check code formatting
- `pnpm generate-types` - Generate TypeScript types from OpenAPI spec that is used to integrate to the backend

## Future Improvements

- Add i18n support for UI text
- Implement searchable currency selection using API's currency list endpoint
- Add better error handling for invalid locale configurations
- Add more comprehensive error state handling
- More comprehensive ESLint rules and configuration

## API Integration

The app integrates with a RESTful API that provides:

- Currency conversion endpoint (`/conversion`)
- Supported currencies endpoint (`/currencies`)

API documentation is available in `backend-api.yaml` using OpenAPI 3.0.3 specification.
