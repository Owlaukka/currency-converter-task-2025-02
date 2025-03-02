import { FC } from "react";
import CurrencyInput from "react-currency-input-field";
import { Controller, useForm } from "react-hook-form";
import useCurrencyConversion from "./useCurrencyConversion";
import clsx from "clsx";
import Button from "../components/Button/Button";
import ConversionResult from "./ConversionResult";

interface FormValues {
  sourceCurrency: string;
  targetCurrency: string;
  amount?: number;
}

const PERMITTED_CURRENCY_CHARACTERS_REGEXP = /[^a-zA-Z]{0,3}/g;

interface CurrencyConversionFormProps {
  // These are just helpful example types
  locale: "fi" | "sv" | "en" | string;
}

const CurrencyConversionForm: FC<CurrencyConversionFormProps> = ({ locale }) => {
  const { control, handleSubmit } = useForm<FormValues>({
    mode: "onBlur",
  });
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
    <form
      aria-label="Currency-conversion form"
      onSubmit={handleSubmit(onSubmit)}
      className="space-y-6"
    >
      <div className="grid gap-6 md:grid-cols-2">
        <div className="space-y-2">
          <label htmlFor="source-currency" className="block text-sm font-medium text-gray-700">
            Source Currency
          </label>
          <Controller
            name="sourceCurrency"
            control={control}
            defaultValue=""
            rules={{
              validate: (value) => {
                if (!value) return "Source currency is required";
                if (value.length !== 3) return "Must be exactly 3 characters long";
                return true;
              },
            }}
            render={({ field, fieldState }) => (
              <div>
                <input
                  {...field}
                  id="source-currency"
                  type="text"
                  className={`focus:ring-primary-600 block w-full rounded-md border-0 px-3 py-2 text-gray-900 ring-1 ring-inset focus:ring-2 focus:ring-inset sm:text-sm ${
                    fieldState.error ? "ring-red-300" : "ring-gray-300"
                  }`}
                  onChange={(e) => {
                    const filteredValue = e.target.value
                      .replace(PERMITTED_CURRENCY_CHARACTERS_REGEXP, "")
                      .toUpperCase();

                    if (filteredValue.length <= 3) {
                      field.onChange(filteredValue);
                    }
                  }}
                  aria-invalid={!!fieldState.error}
                />
                {fieldState.error && (
                  <p className="mt-2 text-sm text-red-600" role="alert">
                    {fieldState.error.message}
                  </p>
                )}
              </div>
            )}
          />
        </div>

        <div className="space-y-2">
          <label htmlFor="target-currency" className="block text-sm font-medium text-gray-700">
            Target Currency
          </label>
          <Controller
            name="targetCurrency"
            control={control}
            defaultValue=""
            rules={{
              validate: (value) => {
                if (!value) return "Target currency is required";
                if (value.length !== 3) return "Must be exactly 3 characters long";
                return true;
              },
            }}
            render={({ field, fieldState }) => (
              <div>
                <input
                  {...field}
                  id="target-currency"
                  type="text"
                  className={`focus:ring-primary-600 block w-full rounded-md border-0 px-3 py-2 text-gray-900 ring-1 ring-inset focus:ring-2 focus:ring-inset sm:text-sm ${
                    fieldState.error ? "ring-red-300" : "ring-gray-300"
                  }`}
                  onChange={(e) => {
                    const filteredValue = e.target.value
                      .replace(PERMITTED_CURRENCY_CHARACTERS_REGEXP, "")
                      .toUpperCase();

                    if (filteredValue.length <= 3) {
                      field.onChange(filteredValue);
                    }
                  }}
                  aria-invalid={!!fieldState.error}
                />
                {fieldState.error && (
                  <p className="mt-2 text-sm text-red-600" role="alert">
                    {fieldState.error.message}
                  </p>
                )}
              </div>
            )}
          />
        </div>
      </div>

      <div className="space-y-2">
        <label htmlFor="amount" className="block text-sm font-medium text-gray-700">
          Amount
        </label>
        <Controller
          name="amount"
          control={control}
          rules={{
            required: "Amount is required",
          }}
          render={({ field, fieldState }) => (
            <div>
              <CurrencyInput
                id={field.name}
                name={field.name}
                className={clsx(
                  "focus:ring-primary-600",
                  "block",
                  "w-full",
                  "rounded-md",
                  "border-0",
                  "px-3",
                  "py-2",
                  "text-gray-900",
                  "ring-1",
                  "ring-inset",
                  "focus:ring-2",
                  "focus:ring-inset",
                  "sm:text-sm",
                  fieldState.error ? "ring-red-300" : "ring-gray-300"
                )}
                decimalScale={2}
                allowNegativeValue={false}
                onValueChange={(val) => {
                  // Needed because the component doesn't replace comma decimal-separators with dots for some reason.
                  field.onChange(val ? val.replace(/,/g, ".") : "");
                }}
                formatValueOnBlur
                intlConfig={{ locale }}
                aria-invalid={!!fieldState.error}
              />
              {fieldState.error && (
                <p className="mt-2 text-sm text-red-600" role="alert">
                  {fieldState.error.message}
                </p>
              )}
            </div>
          )}
        />
      </div>

      <Button type="submit" isLoading={isLoading}>
        {isLoading ? "Converting..." : "Convert"}
      </Button>

      {error && (
        <div role="alert" className="text-red-500">
          {error}
        </div>
      )}

      {result && (
        <ConversionResult
          amount={result.convertedAmount}
          conversionRateDate={result.date}
          locale={locale}
        />
      )}
    </form>
  );
};

export default CurrencyConversionForm;
