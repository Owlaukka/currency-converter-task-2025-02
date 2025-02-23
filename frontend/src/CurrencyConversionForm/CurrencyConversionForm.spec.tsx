import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import CurrencyConversionForm from "./CurrencyConversionForm";

describe("CurrencyConversionForm", () => {
  it("should render a currency-conversion form", () => {
    render(<CurrencyConversionForm locale="fi-FI" />);
    expect(screen.getByRole("form", { name: /currency-conversion form/i })).toBeInTheDocument();
  });

  it("should allow selecting a source currency", async () => {
    // Given
    const user = userEvent.setup();
    render(<CurrencyConversionForm locale="fi-FI" />);

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
    render(<CurrencyConversionForm locale="fi-FI" />);

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
    render(<CurrencyConversionForm locale="fi-FI" />);

    // When
    const amountField = screen.getByRole("textbox", { name: /amount/i });

    const amount = "100";
    await user.type(amountField, amount);
    await user.tab();

    // Then
    expect(amountField).toHaveValue("100,00");
  });

  it("should not allow inputting non-letters as currency selections", async () => {
    // Given
    const user = userEvent.setup();
    render(<CurrencyConversionForm locale="fi-FI" />);

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

  it("should capitalize currency-type selections", async () => {
    // Given
    const user = userEvent.setup();
    render(<CurrencyConversionForm locale="fi-FI" />);

    // When
    const sourceCurrencyField = screen.getByRole("textbox", { name: /source currency/i });
    const targetCurrencyField = screen.getByRole("textbox", { name: /target currency/i });

    const sourceCurrencyInput = "eUr";
    await user.type(sourceCurrencyField, sourceCurrencyInput);

    const targetCurrencyInput = "Usd";
    await user.type(targetCurrencyField, targetCurrencyInput);

    // Then
    expect(sourceCurrencyField).toHaveValue("EUR");
    expect(targetCurrencyField).toHaveValue("USD");
  });

  it.each([
    { locale: "fi-FI", inputValue: "345.12", expectedValue: "34\u00A0512,00" },
    { locale: "fi", inputValue: "345.12", expectedValue: "34\u00A0512,00" },
    { locale: "en-US", inputValue: "345,12", expectedValue: "34,512.00" },
    { locale: "sv-SE", inputValue: "654356,564", expectedValue: "654\u00A0356,56" },
  ])(
    "should correctly format currency-amount for locale $locale",
    async ({ locale, inputValue, expectedValue }) => {
      // Given
      const user = userEvent.setup();
      render(<CurrencyConversionForm locale={locale} />);

      // When
      const amountField = screen.getByRole("textbox", { name: /amount/i });

      await user.type(amountField, inputValue);
      await user.tab();

      // Then
      expect(amountField).toHaveValue(expectedValue);
    }
  );
});
