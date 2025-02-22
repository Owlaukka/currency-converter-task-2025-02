import "@testing-library/jest-dom";
import { cleanup } from "@testing-library/react";
import { afterEach, expect } from "vitest";
import * as matchers from "@testing-library/jest-dom/matchers";

expect.extend(matchers);

// Automatically cleanup after each test
afterEach(() => {
  cleanup();
});
