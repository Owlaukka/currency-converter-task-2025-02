import { FC } from "react";
import { useForm } from "react-hook-form";

// eslint-disable-next-line @typescript-eslint/no-empty-object-type
interface CurrencyConversionFormProps {
  // Add props here later as needed
}

const CurrencyConversionForm: FC<CurrencyConversionFormProps> = () => {
  const { register } = useForm();

  return (
    <form aria-label="Currency-conversion form" className="w-full max-w-md">
      <div className="mb-4">
        <label htmlFor="source-currency" className="block text-sm font-medium text-gray-700">
          Source Currency
        </label>
        <input
          {...register("sourceCurrency")}
          type="text"
          id="source-currency"
          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
        />
      </div>
    </form>
  );
};

export default CurrencyConversionForm;
