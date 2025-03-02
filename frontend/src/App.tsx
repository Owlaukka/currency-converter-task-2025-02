import CurrencyConversionForm from "./CurrencyConversionForm/CurrencyConversionForm";

function App() {
  return (
    <div className="min-h-screen bg-gray-50 px-4 py-8">
      <div className="mx-auto max-w-md">
        <h1 className="mb-8 text-center text-3xl font-bold text-gray-900">Currency Converter</h1>
        <div className="rounded-lg bg-white p-6 shadow-sm ring-1 ring-gray-900/5">
          <CurrencyConversionForm locale={navigator.language} />
        </div>
      </div>
    </div>
  );
}

export default App;
