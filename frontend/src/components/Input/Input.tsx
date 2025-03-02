import { ComponentPropsWithRef } from "react";
import clsx from "clsx";

interface InputProps extends ComponentPropsWithRef<"input"> {
  error?: string;
  label?: string;
}

const Input = ({
  error,
  label,
  id,
  className,
  "aria-invalid": ariaInvalid,
  type = "text",
  ...props
}: InputProps) => {
  return (
    <div className="space-y-2">
      {label && (
        <label htmlFor={id} className="block text-sm font-medium text-gray-700">
          {label}
        </label>
      )}
      <div>
        <input
          {...props}
          id={id}
          type={type}
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
          aria-invalid={ariaInvalid ?? !!error}
        />
        {error && (
          <p className="mt-2 text-sm text-red-600" role="alert">
            {error}
          </p>
        )}
      </div>
    </div>
  );
};

export default Input;
