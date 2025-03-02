import { FC } from "react";

interface ErrorMessageProps {
  message: string;
}

const ErrorMessage: FC<ErrorMessageProps> = ({ message }) => {
  if (!message) return null;

  return (
    <div role="alert" className="text-red-500">
      {message}
    </div>
  );
};

export default ErrorMessage;
