import CurrencyConversionForm from "./CurrencyConversionForm/CurrencyConversionForm";

function App() {
  return (
    <div className="mx-auto max-w-prose">
      <h1 className="text-2xl">Currency-conversion App</h1>
      <CurrencyConversionForm locale={navigator.language} />
    </div>
  );
}

export default App;
