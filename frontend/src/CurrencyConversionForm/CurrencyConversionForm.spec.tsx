import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import CurrencyConversionForm from "./CurrencyConversionForm";
import { Mock } from "vitest";
import { paths } from "../types/backend-api";

// Mock the fetch function
globalThis.fetch = vi.fn();

describe("CurrencyConversionForm", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

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

  it("should validate that amount is not empty", async () => {
    // Given
    const user = userEvent.setup();
    render(<CurrencyConversionForm locale="fi-FI" />);

    // When
    const amountField = screen.getByRole("textbox", { name: /amount/i });

    // Focus the field and then blur without typing anything
    await user.click(amountField);
    await user.tab(); // Move focus away to trigger validation

    // Try to submit the form
    const submitButton = screen.getByRole("button", { name: /convert/i });
    await user.click(submitButton);

    // Then
    // Check for validation error
    expect(amountField).toBeInvalid();
    expect(screen.getByText(/amount is required/i)).toBeInTheDocument();
  });

  it("should submit a valid form and display the conversion result", async () => {
    // Given
    const mockResponse: paths["/conversion"]["get"]["responses"]["200"]["content"]["application/json"] =
      {
        date: "2023-04-25",
        convertedAmount: "110",
      };

    // Mock the API response
    (globalThis.fetch as Mock).mockResolvedValueOnce({
      ok: true,
      json: async () => mockResponse,
    });

    const user = userEvent.setup();
    render(<CurrencyConversionForm locale="en-US" />);

    // When - Fill in the form with valid data
    const sourceCurrencyField = screen.getByRole("textbox", { name: /source currency/i });
    await user.type(sourceCurrencyField, "EUR");

    const targetCurrencyField = screen.getByRole("textbox", { name: /target currency/i });
    await user.type(targetCurrencyField, "USD");

    const amountField = screen.getByRole("textbox", { name: /amount/i });
    await user.type(amountField, "100");

    // Submit the form
    const submitButton = screen.getByRole("button", { name: /convert/i });
    await user.click(submitButton);

    // Then - Verify the API was called and result is displayed
    await waitFor(() => {
      expect(globalThis.fetch).toHaveBeenCalledTimes(1);
      expect(globalThis.fetch).toHaveBeenCalledWith(
        "/api/conversion?sourceCurrency=EUR&targetCurrency=USD&amount=100.00"
      );

      // Verify the result is displayed
      expect(screen.getByText(/Converted amount: 110/i)).toBeInTheDocument();
      expect(screen.getByText(/Date: 2023-04-25/i)).toBeInTheDocument();
    });
  });

  it("should display an error message when the API call fails", async () => {
    // Given
    const errorMessage = "Currency conversion failed";

    // Mock the API to return an error
    (globalThis.fetch as Mock).mockResolvedValueOnce({
      ok: false,
      status: 500,
      json: async () => ({ message: errorMessage }),
    });

    const user = userEvent.setup();
    render(<CurrencyConversionForm locale="en-US" />);

    // When - Fill in the form with valid data
    const sourceCurrencyField = screen.getByRole("textbox", { name: /source currency/i });
    await user.type(sourceCurrencyField, "EUR");

    const targetCurrencyField = screen.getByRole("textbox", { name: /target currency/i });
    await user.type(targetCurrencyField, "USD");

    const amountField = screen.getByRole("textbox", { name: /amount/i });
    await user.type(amountField, "100");

    // Submit the form
    const submitButton = screen.getByRole("button", { name: /convert/i });
    await user.click(submitButton);

    // Then - Verify error message is displayed
    await waitFor(() => {
      expect(globalThis.fetch).toHaveBeenCalledTimes(1);
      expect(screen.getByText(new RegExp(errorMessage, "i"))).toBeInTheDocument();
    });
  });

  it("should display loading state when API request is in progress", async () => {
    // Given
    // Create a Promise that we can control when it resolves
    let resolveFetchPromise: (value: unknown) => void;
    const fetchPromise = new Promise((resolve) => {
      resolveFetchPromise = resolve;
    });

    const mockResponse = {
      date: "2023-04-25",
      convertedAmount: "110",
    };

    // Mock the API call to use our controllable Promise
    (globalThis.fetch as Mock).mockReturnValueOnce(
      fetchPromise.then(() => ({
        ok: true,
        json: async () => mockResponse,
      }))
    );

    const user = userEvent.setup();
    render(<CurrencyConversionForm locale="en-US" />);

    // When - Fill in form with valid data
    const sourceCurrencyField = screen.getByRole("textbox", { name: /source currency/i });
    await user.type(sourceCurrencyField, "EUR");

    const targetCurrencyField = screen.getByRole("textbox", { name: /target currency/i });
    await user.type(targetCurrencyField, "USD");

    const amountField = screen.getByRole("textbox", { name: /amount/i });
    await user.type(amountField, "100");

    // Submit the form
    const submitButton = screen.getByRole("button", { name: /convert/i });
    await user.click(submitButton);

    // Then - Verify loading state is displayed
    expect(screen.getByText(/converting/i)).toBeInTheDocument();
    expect(submitButton).toBeDisabled();

    // Resolve the API call
    resolveFetchPromise!({});

    // Verify loading state is removed after response
    await waitFor(() => {
      expect(screen.queryByText(/converting/i)).not.toBeInTheDocument();
      expect(submitButton).not.toBeDisabled();
    });
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
