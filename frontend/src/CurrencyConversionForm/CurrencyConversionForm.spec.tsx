import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import CurrencyConversionForm from "./CurrencyConversionForm";

describe("CurrencyConversionForm", () => {
  it("should render a currency-conversion form", () => {
    render(<CurrencyConversionForm />);
    expect(screen.getByRole("form", { name: /currency-conversion form/i })).toBeInTheDocument();
  });

  it("should allow selecting a source currency", async () => {
    // Given
    const user = userEvent.setup();
    render(<CurrencyConversionForm />);

    // When
    const sourceCurrencyField = screen.getByRole("textbox", { name: /source currency/i });

    await user.type(sourceCurrencyField, "EUR");

    // Then
    expect(sourceCurrencyField).toHaveValue("EUR");
  });
});
