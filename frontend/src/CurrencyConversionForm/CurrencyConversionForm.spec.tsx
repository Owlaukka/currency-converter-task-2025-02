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

    const currencyInput = "EUR";
    await user.type(sourceCurrencyField, currencyInput);

    // Then
    expect(sourceCurrencyField).toHaveValue(currencyInput);
  });

  it("should allow selecting a target currency", async () => {
    // Given
    const user = userEvent.setup();
    render(<CurrencyConversionForm />);

    // When
    const sourceCurrencyField = screen.getByRole("textbox", { name: /target currency/i });

    const currencyInput = "USD";
    await user.type(sourceCurrencyField, currencyInput);

    // Then
    expect(sourceCurrencyField).toHaveValue(currencyInput);
  });

  it("should allow inputting amount to be converted", async () => {
    // Given
    const user = userEvent.setup();
    render(<CurrencyConversionForm />);

    // When
    const amountField = screen.getByRole("spinbutton", { name: /amount/i });

    const amount = "100";
    await user.type(amountField, amount);

    // Then
    expect(amountField).toHaveValue(100);
  });

  it("should not allow inputting non-letters as currency selections", async () => {
    // Given
    const user = userEvent.setup();
    render(<CurrencyConversionForm />);

    // When
    const sourceCurrencyField = screen.getByRole("textbox", { name: /source currency/i });
    const targetCurrencyField = screen.getByRole("textbox", { name: /target currency/i });

    const sourceCurrencyInput = "2@(/& CHF /-";
    await user.type(sourceCurrencyField, sourceCurrencyInput);

    const targetCurrencyInput = "2@(/& EUR /-";
    await user.type(targetCurrencyField, targetCurrencyInput);

    // Then
    expect(sourceCurrencyField).toHaveValue("CHF");
    expect(targetCurrencyField).toHaveValue("EUR");
  });
});
