import { FC } from "react";

// eslint-disable-next-line @typescript-eslint/no-empty-object-type
interface CurrencyConversionFormProps {
  // Add props here later as needed
}

const CurrencyConversionForm: FC<CurrencyConversionFormProps> = () => {
  return (
    <form aria-label="currency-conversion-form" className="w-full max-w-md">
      <div className="space-y-4"></div>
    </form>
  );
};

export default CurrencyConversionForm;
