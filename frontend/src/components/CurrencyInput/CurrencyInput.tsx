import { FC } from "react";
import BaseCurrencyInput from "react-currency-input-field";
import clsx from "clsx";

interface CurrencyInputProps {
  ref: React.Ref<HTMLInputElement>;
  id: string;
  name: string;
  onChange: (value: string) => void;
  error?: string;
  locale: string;
  className?: string;
}

const CurrencyInput: FC<CurrencyInputProps> = ({
  ref,
  id,
  name,
  onChange,
  error,
  locale,
  className,
}) => {
  return (
    <div>
      <BaseCurrencyInput
        ref={ref}
        id={id}
        name={name}
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
          error ? "ring-red-300" : "ring-gray-300",
          className
        )}
        decimalScale={2}
        allowNegativeValue={false}
        onValueChange={(val) => {
          onChange(val ? val.replace(/,/g, ".") : "");
        }}
        formatValueOnBlur
        intlConfig={{ locale }}
        aria-invalid={!!error}
      />
      {error && (
        <p className="mt-2 text-sm text-red-600" role="alert">
          {error}
        </p>
      )}
    </div>
  );
};

export default CurrencyInput;
