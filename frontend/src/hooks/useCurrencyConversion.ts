import { useState } from "react";
import type { paths } from "../types/backend-api";

type ConversionResponse =
  paths["/conversion"]["get"]["responses"]["200"]["content"]["application/json"];

type ErrorResponse = paths["/conversion"]["get"]["responses"]["400"]["content"]["application/json"];

interface ConversionParams {
  sourceCurrency: string;
  targetCurrency: string;
  amount: number;
}

const useCurrencyConversion = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string>();
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

      if (!response.ok) {
        const errorData = (await response.json()) as ErrorResponse;
        throw new Error(errorData.message ?? "Conversion failed");
      }

      const result = (await response.json()) as ConversionResponse;
      setResult(result);
    } catch (e) {
      setError(e instanceof Error ? e.message : "Conversion failed");
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
