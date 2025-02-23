import { FC } from "react";
import CurrencyInput from "react-currency-input-field";
import { Controller, useForm } from "react-hook-form";

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
  const { control } = useForm<FormValues>();

  return (
    <form aria-label="Currency-conversion form">
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
                const filteredValue = e.target.value.replace(
                  PERMITTED_CURRENCY_CHARACTERS_REGEXP,
                  ""
                );
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
                const filteredValue = e.target.value.replace(
                  PERMITTED_CURRENCY_CHARACTERS_REGEXP,
                  ""
                );
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
          render={({ field }) => (
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
            />
          )}
        />
      </div>
    </form>
  );
};

export default CurrencyConversionForm;
