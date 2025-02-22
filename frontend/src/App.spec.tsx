import { describe, it, expect } from "vitest";
import { render, screen } from "./testing/test-utils";
import App from "./App";

describe("App", () => {
  it("displays the correct heading", () => {
    render(<App />);
    expect(screen.getByRole("heading", { name: /Currency-conversion App/i })).toBeInTheDocument();
  });
});
