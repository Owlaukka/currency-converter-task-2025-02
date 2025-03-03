import { useState } from "react";
import type { components, paths } from "../types/backend-api";

type ConversionResponse =
  paths["/conversion"]["get"]["responses"]["200"]["content"]["application/json"];

type ErrorResponse = components["schemas"]["Error"];

type ValidationErrorResponse =
  paths["/conversion"]["get"]["responses"]["400"]["content"]["application/json"];

interface ConversionParams {
  sourceCurrency: string;
  targetCurrency: string;
  amount: number;
}

export interface CurrencyConversionApiErrorState {
  message: string;
  invalidFields?: string[];
}

const useCurrencyConversion = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<CurrencyConversionApiErrorState>();
  const [result, setResult] = useState<ConversionResponse>();

  const convertCurrency = async (params: ConversionParams) => {
    setIsLoading(true);
    setError(undefined);
    setResult(undefined);

    try {
      const urlParams = new URLSearchParams({
        sourceCurrency: params.sourceCurrency,
        targetCurrency: params.targetCurrency,
        amount: params.amount.toString(),
      });

      const response = await fetch(`/api/conversion?${urlParams}`);

      switch (response.status) {
        case 200: {
          const result = (await response.json()) as ConversionResponse;
          setResult(result);
          break;
        }
        case 400: {
          const errorData = (await response.json()) as ValidationErrorResponse;
          setError({ message: errorData.message, invalidFields: errorData.fields });
          break;
        }
        default: {
          const errorData = (await response.json()) as ErrorResponse;
          setError({ message: errorData.message });
          break;
        }
      }
    } catch (e) {
      // Handle unknown errors
      if (!error)
        setError(e instanceof Error ? { message: e.message } : { message: "Conversion failed" });
    } finally {
      setIsLoading(false);
    }
  };

  return {
    convertCurrency,
    result,
    isLoading,
    error,
  };
};

export default useCurrencyConversion;
