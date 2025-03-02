import { ButtonHTMLAttributes, FC } from "react";
import clsx from "clsx";

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  isLoading?: boolean;
}

const Button: FC<ButtonProps> = ({
  isLoading = false,
  children,
  disabled,
  className,
  ...props
}) => {
  return (
    <button
      disabled={isLoading || disabled}
      className={clsx(
        "rounded-2xl",
        "border",
        "border-gray-800",
        "bg-gray-600",
        "px-3",
        "py-1",
        "text-gray-50",
        "transition-colors",
        "hover:cursor-pointer hover:bg-gray-700",
        "disabled:cursor-not-allowed disabled:border-gray-500 disabled:bg-gray-400",
        className
      )}
      {...props}
    >
      {children}
    </button>
  );
};

export default Button;
