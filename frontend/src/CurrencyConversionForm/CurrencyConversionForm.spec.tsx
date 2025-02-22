import { render, screen } from "@testing-library/react";
import CurrencyConversionForm from "./CurrencyConversionForm";

describe("CurrencyConversionForm", () => {
  it("renders the form", () => {
    render(<CurrencyConversionForm />);
    expect(screen.getByRole("form", { name: /currency-conversion form/i })).toBeInTheDocument();
  });
});
