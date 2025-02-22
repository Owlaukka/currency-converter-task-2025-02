import { describe, it, expect } from "vitest";
import App from "./App";
import { render, screen } from "@testing-library/react";

describe("App", () => {
  it("displays the correct heading", () => {
    render(<App />);
    expect(screen.getByRole("heading", { name: /Currency-conversion App/i })).toBeInTheDocument();
  });
});
