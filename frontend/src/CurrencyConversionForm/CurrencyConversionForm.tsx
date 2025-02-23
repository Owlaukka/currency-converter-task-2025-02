import { FC } from "react";
import { Controller, useForm } from "react-hook-form";

interface FormValues {
  sourceCurrency: string;
  targetCurrency: string;
  amount?: number;
}

const CurrencyConversionForm: FC = () => {
  const { register, control } = useForm<FormValues>();

  return (
    <form aria-label="Currency-conversion form">
      <div>
        <label htmlFor="source-currency">Source Currency</label>
        <Controller
          name="sourceCurrency"
          control={control}
          render={({ field }) => (
            <input
              {...field}
              id="source-currency"
              type="text"
              onChange={(e) => {
                const filteredValue = e.target.value.replace(/[^a-zA-Z]/g, "");
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
          render={({ field }) => (
            <input
              {...field}
              id="target-currency"
              type="text"
              onChange={(e) => {
                const filteredValue = e.target.value.replace(/[^a-zA-Z]/g, "");
                field.onChange(filteredValue);
              }}
            />
          )}
        />
      </div>

      <div>
        <label htmlFor="amount">Amount</label>
        <input {...register("amount")} type="number" id="amount" />
      </div>
    </form>
  );
};

export default CurrencyConversionForm;
