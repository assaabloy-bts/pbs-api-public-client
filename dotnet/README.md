# PBS Public API Client - .NET

.NET client library for the [ASSA ABLOY PBS Public API](https://public.api.aa-bts.com).

## Requirements

- .NET 10.0+

## Installation

Packages are published to [GitHub Packages](https://github.com/assaabloy-bts/pbs-api-public-client/packages).

### Add the GitHub Packages NuGet source

```bash
dotnet nuget add source "https://nuget.pkg.github.com/assaabloy-bts/index.json" \
    --name "github-assaabloy-bts" \
    --username YOUR_GITHUB_USERNAME \
    --password YOUR_GITHUB_TOKEN \
    --store-password-in-clear-text
```

To create a token:

1. Go to [GitHub Settings > Developer settings > Personal access tokens > Tokens (classic)](https://github.com/settings/tokens/new)
2. Select the `read:packages` scope
3. Click **Generate token**

Note: Fine-grained tokens do not currently support GitHub Packages.

### Install the package

```bash
dotnet add package AssaAbloy.Bts.PbsApiClient --version 0.0.1
```

Or add to your `.csproj`:

```xml
<PackageReference Include="AssaAbloy.Bts.PbsApiClient" Version="0.0.1" />
```

## Usage

### Creating a Client

```csharp
using AssaAbloy.Bts.PbsApiClient;

// Connect to the default production URL
var client = new PbsClient("your-api-key");

// Or connect to a custom environment
var client = new PbsClient("https://custom-url.example.com", "your-api-key");
```

### Querying Manufacturers

```csharp
// Search manufacturers by abbreviation
var result = await client.GetManufacturersAsync(
    manufacturerId: "SA",
    hasProduct: true,
    endRow: 20,
    startRow: 0);

foreach (var mfg in result.Data)
{
    Console.WriteLine($"{mfg.Name} ({mfg.Abbr})");
}

// Get a single manufacturer by numeric ID
var mfg = await client.GetManufacturerByIdAsync(1);
```

### Browsing Hardware Items

```csharp
// List hardware items for a manufacturer
var items = await client.GetHardwareItemsAsync(
    "SA",
    endRow: 10,
    startRow: 0);

foreach (var item in items.Data)
{
    Console.WriteLine($"{item.PartNumber} - {item.Manufacturer}");
}

// Get a single hardware item by xref
var item = await client.GetHardwareItemByIdAsync(12345);
```

### Searching for Parts

```csharp
// Find a part number match
var matches = await client.LocateHardwareAsync(
    manufacturerId: "SA",
    partNumber: "8204",
    resultQty: 5);

foreach (var match in matches)
{
    Console.WriteLine($"{match.PartNumber,-20} Score: {match.MatchScore}  {match.OrderDescription}");
}
```

### Getting Prices

```csharp
// Get price for a hardware item with selected options
var pricing = await client.GetHardwareItemPriceWithOptionsAsync(xref: 12345);

Console.WriteLine(pricing.PartNumber);
Console.WriteLine(pricing.OrderDescription);
foreach (var p in pricing.Price)
{
    Console.WriteLine($"  {p.PriceBook}: ${p.UnitPrice:F2}");
}
```

### Working with Hardware Options

```csharp
// List options for a hardware item
var itemOptions = await client.GetHardwareItemsOptionsAsync(
    xref: 12345,
    endRow: 10,
    startRow: 0);

// List all options for a manufacturer
var allOptions = await client.GetHardwareOptionsAsync(
    "SA",
    endRow: 10,
    startRow: 0);
```

### Product Lines and Subtypes

```csharp
// Get product lines
var productLines = await client.GetProductLinesAsync("SA");

// Get subtypes
var subTypes = await client.GetSubTypesAsync("SA");
```

### Price Books

```csharp
// List all price books for a manufacturer
var priceBooks = await client.ListPriceBooksAsync("SA");

// Get price books after a specific date
var recentBooks = await client.GetPriceBooksAsync(
    new DateTimeOffset(2024, 1, 1, 0, 0, 0, TimeSpan.Zero),
    manufacturerId: "SA");
```

### Error Handling

API methods throw `ApiException` on failure. The exception includes the HTTP status code:

```csharp
try
{
    var mfg = await client.GetManufacturerByIdAsync(999999);
}
catch (ApiException ex)
{
    Console.Error.WriteLine($"HTTP {ex.StatusCode}: {ex.Message}");
}
```

### Cancellation

All async methods accept an optional `CancellationToken`:

```csharp
using var cts = new CancellationTokenSource(TimeSpan.FromSeconds(10));
var result = await client.GetManufacturersAsync(
    manufacturerId: "SA",
    cancellationToken: cts.Token);
```

## Building

```bash
cd dotnet
dotnet build
```

## Running Tests

Integration tests require a valid API key:

```bash
PBS_API_KEY=your-api-key dotnet test
```

Without the API key, tests are skipped.

---
&#169;ASSA ABLOY
