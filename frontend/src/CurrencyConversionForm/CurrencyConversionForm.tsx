import { FC } from "react";
import { Controller, useForm } from "react-hook-form";
import useCurrencyConversion from "./useCurrencyConversion";
import Button from "../components/Button/Button";
import ConversionResult from "./ConversionResult";
import Input from "../components/Input/Input";
import CurrencyInput from "../components/CurrencyInput/CurrencyInput";

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
            <Input
              {...field}
              id="source-currency"
              type="text"
              label="Source Currency"
              error={fieldState.error?.message}
              onChange={(e) => {
                const filteredValue = e.target.value
                  .replace(PERMITTED_CURRENCY_CHARACTERS_REGEXP, "")
                  .toUpperCase();

                if (filteredValue.length <= 3) {
                  field.onChange(filteredValue);
                }
              }}
            />
          )}
        />

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
            <Input
              {...field}
              id="target-currency"
              type="text"
              label="Target Currency"
              error={fieldState.error?.message}
              onChange={(e) => {
                const filteredValue = e.target.value
                  .replace(PERMITTED_CURRENCY_CHARACTERS_REGEXP, "")
                  .toUpperCase();

                if (filteredValue.length <= 3) {
                  field.onChange(filteredValue);
                }
              }}
            />
          )}
        />
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
            <CurrencyInput
              ref={field.ref}
              id={field.name}
              name={field.name}
              value={field.value}
              onChange={field.onChange}
              error={fieldState.error?.message}
              locale={locale}
            />
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
