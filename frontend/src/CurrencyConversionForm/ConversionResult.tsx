import { FC, useMemo } from "react";

interface ConversionResultProps {
  amount: string;
  conversionRateDate: string;
  locale: string;
}

const ConversionResult: FC<ConversionResultProps> = ({ amount, conversionRateDate, locale }) => {
  const dateFormatter = useMemo(() => new Intl.DateTimeFormat(locale), [locale]);
  const currencyFormatter = useMemo(() => new Intl.NumberFormat(locale), [locale]);

  return (
    <div role="status">
      <p>Converted amount: {currencyFormatter.format(parseFloat(amount))}</p>
      <p>Rate date: {dateFormatter.format(new Date(conversionRateDate))}</p>
    </div>
  );
};

export default ConversionResult;
