import { FC, useMemo } from "react";

interface ConversionResultProps {
  amount?: string;
  conversionRateDate?: string;
  locale: string;
}

const ConversionResult: FC<ConversionResultProps> = ({ amount, conversionRateDate, locale }) => {
  const dateFormatter = useMemo(() => new Intl.DateTimeFormat(locale), [locale]);
  const currencyFormatter = useMemo(() => new Intl.NumberFormat(locale), [locale]);

  return (
    <div role="status">
      <p>Converted amount: {amount ? currencyFormatter.format(parseFloat(amount)) : "N/A"}</p>
      <p>
        Rate date: {conversionRateDate ? dateFormatter.format(new Date(conversionRateDate)) : "N/A"}
      </p>
    </div>
  );
};

export default ConversionResult;
