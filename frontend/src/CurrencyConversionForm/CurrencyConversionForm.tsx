import { FC } from "react";
import CurrencyInput from "react-currency-input-field";
import { Controller, useForm } from "react-hook-form";
import useCurrencyConversion from "../hooks/useCurrencyConversion";

interface FormValues {
  sourceCurrency: string;
  targetCurrency: string;
  amount?: number;
}

const PERMITTED_CURRENCY_CHARACTERS_REGEXP = /[^a-zA-Z]/g;

interface CurrencyConversionFormProps {
  // These are just helpful example types
  locale: "fi" | "sv" | "en" | string;
}

const CurrencyConversionForm: FC<CurrencyConversionFormProps> = ({ locale }) => {
  const { control, handleSubmit } = useForm<FormValues>();
  const { convertCurrency, isLoading, error, result } = useCurrencyConversion();

  const onSubmit = async (data: FormValues) => {
    if (!data.amount) return;

    await convertCurrency({
      sourceCurrency: data.sourceCurrency,
      targetCurrency: data.targetCurrency,
      amount: data.amount,
    });
  };

  return (
    <form aria-label="Currency-conversion form" onSubmit={handleSubmit(onSubmit)}>
      <div>
        <label htmlFor="source-currency">Source Currency</label>
        <Controller
          name="sourceCurrency"
          control={control}
          defaultValue=""
          render={({ field }) => (
            <input
              {...field}
              id="source-currency"
              type="text"
              onChange={(e) => {
                const filteredValue = e.target.value
                  .replace(PERMITTED_CURRENCY_CHARACTERS_REGEXP, "")
                  .toUpperCase();
                field.onChange(filteredValue);
              }}
            />
          )}
        />
      </div>

      <div>
        <label htmlFor="target-currency">Target Currency</label>
        <Controller
          name="targetCurrency"
          control={control}
          defaultValue=""
          render={({ field }) => (
            <input
              {...field}
              id="target-currency"
              type="text"
              onChange={(e) => {
                const filteredValue = e.target.value
                  .replace(PERMITTED_CURRENCY_CHARACTERS_REGEXP, "")
                  .toUpperCase();
                field.onChange(filteredValue);
              }}
            />
          )}
        />
      </div>

      <div>
        <label htmlFor="amount">Amount</label>
        <Controller
          name="amount"
          control={control}
          rules={{
            required: "Amount is required",
          }}
          render={({ field, fieldState }) => (
            <>
              <CurrencyInput
                id={field.name}
                name={field.name}
                value={field.value}
                decimalScale={2}
                allowNegativeValue={false}
                onValueChange={(val) => {
                  field.onChange(val ?? "");
                }}
                formatValueOnBlur
                intlConfig={{ locale }}
                aria-invalid={!!fieldState.error}
              />
              {fieldState.error && <p className="text-red-700">{fieldState.error.message}</p>}
            </>
          )}
        />
      </div>

      <button type="submit" disabled={isLoading}>
        {isLoading ? "Converting..." : "Convert"}
      </button>

      {error && (
        <div role="alert" className="text-red-500">
          {error}
        </div>
      )}

      {result && (
        <div role="status">
          <p>Converted amount: {result.convertedAmount}</p>
          <p>Rate date: {result.date}</p>
        </div>
      )}
    </form>
  );
};

export default CurrencyConversionForm;
