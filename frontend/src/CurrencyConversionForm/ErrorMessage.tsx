import { FC } from "react";
import { CurrencyConversionApiErrorState } from "./useCurrencyConversion";

interface ErrorMessageProps {
  errorState: CurrencyConversionApiErrorState;
}

const ErrorMessage: FC<ErrorMessageProps> = ({ errorState }) => {
  return (
    <div role="alert" className="mt-4 rounded-md border border-red-300 bg-red-100 p-4 text-red-600">
      {errorState.invalidFields?.length && (
        <div className="mb-3">
          <h2 className="text-md">Invalid fields:</h2>
          <ul className="mt-2 list-disc pl-5 text-sm">
            {errorState.invalidFields.map((fieldName) => (
              <li key={fieldName}>
                <span className="font-medium">{fieldName}</span>
              </li>
            ))}
          </ul>
        </div>
      )}
      <div className="flex flex-col text-red-800">
        <h2 className="text-md">Error message:</h2>
        <p className="text-sm">{errorState.message}</p>
      </div>
    </div>
  );
};

export default ErrorMessage;
