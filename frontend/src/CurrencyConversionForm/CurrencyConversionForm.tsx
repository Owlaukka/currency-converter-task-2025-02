import { FC } from "react";
import { useForm } from "react-hook-form";

const CurrencyConversionForm: FC = () => {
  const { register } = useForm();

  return (
    <form aria-label="Currency-conversion form">
      <div>
        <label htmlFor="source-currency">Source Currency</label>
        <input {...register("sourceCurrency")} type="text" id="source-currency" />
      </div>

      <div>
        <label htmlFor="target-currency">Target Currency</label>
        <input {...register("targetCurrency")} type="text" id="target-currency" />
      </div>

      <div>
        <label htmlFor="amount">Amount</label>
        <input {...register("amount")} type="number" id="amount" />
      </div>
    </form>
  );
};

export default CurrencyConversionForm;
