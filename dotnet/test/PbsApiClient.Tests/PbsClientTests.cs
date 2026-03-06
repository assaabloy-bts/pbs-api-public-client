using AssaAbloy.Bts.PbsApiClient;
using Xunit;

namespace PbsApiClient.Tests;

/// <summary>
/// Integration tests for PbsClient against the live API.
/// Requires the PBS_API_KEY environment variable to be set.
/// Optionally set PBS_API_URL to override the default production URL.
/// </summary>
[TestCaseOrderer("PbsApiClient.Tests.PriorityOrderer", "PbsApiClient.Tests")]
public class PbsClientTests
{
    private static readonly PbsClient? Client;
    private static string? _manufacturerAbbr;
    private static long? _manufacturerNumericId;
    private static long? _hardwareItemXref;
    private static long? _hardwareOptionXref;

    static PbsClientTests()
    {
        var apiKey = Environment.GetEnvironmentVariable("PBS_API_KEY");
        if (string.IsNullOrWhiteSpace(apiKey))
        {
            Client = null;
            return;
        }

        var baseUrl = Environment.GetEnvironmentVariable("PBS_API_URL");
        Client = string.IsNullOrWhiteSpace(baseUrl)
            ? new PbsClient(apiKey)
            : new PbsClient(baseUrl, apiKey);
    }

    private static void SkipIfNoApiKey()
    {
        Skip.If(Client == null, "PBS_API_KEY environment variable is required");
    }

    // -- Manufacturers --

    [SkippableFact]
    [TestPriority(1)]
    public async Task GetManufacturers_ReturnsResults()
    {
        SkipIfNoApiKey();

        var result = await Client!.GetManufacturersAsync(
            manufacturerId: "SA", hasProduct: true, endRow: 20, startRow: 0);

        Assert.NotNull(result);
        Assert.NotNull(result.Data);
        Assert.NotEmpty(result.Data);
        Assert.Equal(0, result.StartRow);
        Assert.True(result.TotalRows > 0);

        var mfg = result.Data[0];
        Assert.NotNull(mfg.Name);
        Assert.NotNull(mfg.Abbr);
        Assert.True(mfg.ManufacturerId > 0);

        _manufacturerAbbr = mfg.Abbr;
        _manufacturerNumericId = mfg.ManufacturerId;
    }

    [SkippableFact]
    [TestPriority(2)]
    public async Task GetManufacturerById_ReturnsSingleManufacturer()
    {
        SkipIfNoApiKey();
        Skip.If(_manufacturerNumericId == null, "Requires GetManufacturers to run first");

        var result = await Client!.GetManufacturerByIdAsync(_manufacturerNumericId!.Value);

        Assert.NotNull(result);
        Assert.Equal(_manufacturerNumericId, result.ManufacturerId);
        Assert.NotNull(result.Name);
        Assert.NotNull(result.Abbr);
        Assert.NotNull(result.Type);
    }

    // -- Product Lines & Subtypes --

    [SkippableFact]
    [TestPriority(3)]
    public async Task GetProductLines_ReturnsResults()
    {
        SkipIfNoApiKey();
        Skip.If(_manufacturerAbbr == null, "Requires GetManufacturers to run first");

        var result = await Client!.GetProductLinesAsync(_manufacturerAbbr!);

        Assert.NotNull(result);
        Assert.NotEmpty(result);
    }

    [SkippableFact]
    [TestPriority(4)]
    public async Task GetSubTypes_ReturnsResults()
    {
        SkipIfNoApiKey();
        Skip.If(_manufacturerAbbr == null, "Requires GetManufacturers to run first");

        var result = await Client!.GetSubTypesAsync(_manufacturerAbbr!);

        Assert.NotNull(result);
        Assert.NotEmpty(result);
        Assert.NotNull(result.First());
    }

    // -- Hardware Items --

    [SkippableFact]
    [TestPriority(5)]
    public async Task GetHardwareItems_ReturnsResults()
    {
        SkipIfNoApiKey();
        Skip.If(_manufacturerAbbr == null, "Requires GetManufacturers to run first");

        var result = await Client!.GetHardwareItemsAsync(
            _manufacturerAbbr!, endRow: 5, startRow: 0);

        Assert.NotNull(result);
        Assert.NotNull(result.Data);
        Assert.NotEmpty(result.Data);
        Assert.Equal(0, result.StartRow);
        Assert.True(result.TotalRows > 0);

        var item = result.Data[0];
        Assert.True(item.Xref > 0);
        Assert.NotNull(item.Manufacturer);
        Assert.NotNull(item.PartNumber);

        _hardwareItemXref = item.Xref;
    }

    [SkippableFact]
    [TestPriority(6)]
    public async Task GetHardwareItemById_ReturnsSingleItem()
    {
        SkipIfNoApiKey();
        Skip.If(_hardwareItemXref == null, "Requires GetHardwareItems to run first");

        var result = await Client!.GetHardwareItemByIdAsync(_hardwareItemXref!.Value);

        Assert.NotNull(result);
        Assert.Equal(_hardwareItemXref, result.Xref);
        Assert.NotNull(result.Manufacturer);
        Assert.NotNull(result.PartNumber);
        Assert.NotNull(result.Type);
    }

    [SkippableFact]
    [TestPriority(7)]
    public async Task GetHardwareAttributes_ReturnsResults()
    {
        SkipIfNoApiKey();
        Skip.If(_manufacturerAbbr == null, "Requires GetManufacturers to run first");

        var result = await Client!.GetHardwareAttributesAsync(
            _manufacturerAbbr!, endRow: 5, startRow: 0);

        Assert.NotNull(result);
        Assert.NotNull(result.Data);
        Assert.NotEmpty(result.Data);
        Assert.Equal(0, result.StartRow);
        Assert.True(result.TotalRows > 0);

        var attr = result.Data[0];
        Assert.True(attr.Xref > 0);
        Assert.NotNull(attr.Manufacturer);
        Assert.NotNull(attr.Type);
        Assert.NotNull(attr.PrintCode);
        Assert.NotNull(attr.PrintDescription);
    }

    [SkippableFact]
    [TestPriority(8)]
    public async Task LocateHardware_ReturnsMatches()
    {
        SkipIfNoApiKey();
        Skip.If(_manufacturerAbbr == null || _hardwareItemXref == null,
            "Requires GetManufacturers and GetHardwareItems to run first");

        var item = await Client!.GetHardwareItemByIdAsync(_hardwareItemXref!.Value);

        var result = await Client!.LocateHardwareAsync(
            _manufacturerAbbr!, item.PartNumber, 5);

        Assert.NotNull(result);
        Assert.NotEmpty(result);

        var match = result.First();
        Assert.NotNull(match.PartNumber);
        Assert.NotNull(match.OrderDescription);
        Assert.True(match.MatchScore > 0);
    }

    // -- Hardware Item Options --

    [SkippableFact]
    [TestPriority(9)]
    public async Task GetHardwareItemsOptions_ReturnsResults()
    {
        SkipIfNoApiKey();
        Skip.If(_hardwareItemXref == null, "Requires GetHardwareItems to run first");

        var result = await Client!.GetHardwareItemsOptionsAsync(
            _hardwareItemXref!.Value, endRow: 5, startRow: 0);

        Assert.NotNull(result);
        Assert.NotNull(result.Data);
        Assert.Equal(0, result.StartRow);

        if (result.Data.Count > 0)
        {
            var opt = result.Data[0];
            Assert.True(opt.Xref > 0);
            Assert.NotNull(opt.Manufacturer);
            Assert.NotNull(opt.PrintCode);
            Assert.NotNull(opt.PrintDescription);
            _hardwareOptionXref = opt.Xref;
        }
    }

    [SkippableFact]
    [TestPriority(10)]
    public async Task GetHardwareItemsOptionById_ReturnsSingleOption()
    {
        SkipIfNoApiKey();
        Skip.If(_hardwareOptionXref == null || _hardwareItemXref == null,
            "No options available for the test hardware item");

        var result = await Client!.GetHardwareItemsOptionByIdAsync(
            _hardwareOptionXref!.Value, _hardwareItemXref!.Value);

        Assert.NotNull(result);
        Assert.Equal(_hardwareOptionXref, result.Xref);
        Assert.NotNull(result.Manufacturer);
        Assert.NotNull(result.PrintCode);
    }

    [SkippableFact]
    [TestPriority(11)]
    public async Task GetHardwareItemPriceWithOptions_ReturnsPriceInfo()
    {
        SkipIfNoApiKey();
        Skip.If(_hardwareItemXref == null, "Requires GetHardwareItems to run first");

        var result = await Client!.GetHardwareItemPriceWithOptionsAsync(
            _hardwareItemXref!.Value);

        Assert.NotNull(result);
        Assert.NotNull(result.PartNumber);
        Assert.NotNull(result.OrderDescription);
    }

    // -- Hardware Options (global) --

    [SkippableFact]
    [TestPriority(12)]
    public async Task GetHardwareOptions_ReturnsResults()
    {
        SkipIfNoApiKey();
        Skip.If(_manufacturerAbbr == null, "Requires GetManufacturers to run first");

        var result = await Client!.GetHardwareOptionsAsync(
            _manufacturerAbbr!, endRow: 5, startRow: 0);

        Assert.NotNull(result);
        Assert.NotNull(result.Data);
        Assert.NotEmpty(result.Data);
        Assert.Equal(0, result.StartRow);
        Assert.True(result.TotalRows > 0);

        var opt = result.Data[0];
        Assert.True(opt.Xref > 0);
        Assert.NotNull(opt.Manufacturer);
        Assert.NotNull(opt.PrintCode);
        Assert.NotNull(opt.PrintDescription);
    }

    [SkippableFact]
    [TestPriority(13)]
    public async Task GetHardwareOptionById_ReturnsSingleOption()
    {
        SkipIfNoApiKey();
        Skip.If(_manufacturerAbbr == null, "Requires GetManufacturers to run first");

        var options = await Client!.GetHardwareOptionsAsync(
            _manufacturerAbbr!, endRow: 1, startRow: 0);
        Assert.NotEmpty(options.Data);
        var optXref = options.Data[0].Xref;

        var result = await Client!.GetHardwareOptionByIdAsync(optXref);

        Assert.NotNull(result);
        Assert.Equal(optXref, result.Xref);
        Assert.NotNull(result.Manufacturer);
        Assert.NotNull(result.PrintCode);
    }

    // -- Price Books --

    [SkippableFact]
    [TestPriority(14)]
    public async Task GetPriceBookComparisons_Succeeds()
    {
        SkipIfNoApiKey();
        Skip.If(_manufacturerAbbr == null, "Requires GetManufacturers to run first");

        try
        {
            await Client!.GetPriceBookComparisonsAsync(
                _manufacturerAbbr!, futurePriceBook: false);
        }
        catch (ApiException ex) when (ex.StatusCode == 504)
        {
            // 504 Gateway Timeout is acceptable for this long-running comparison
        }
    }

    [SkippableFact]
    [TestPriority(15)]
    public async Task ListPriceBooks_ReturnsResults()
    {
        SkipIfNoApiKey();
        Skip.If(_manufacturerAbbr == null, "Requires GetManufacturers to run first");

        var result = await Client!.ListPriceBooksAsync(_manufacturerAbbr!);

        Assert.NotNull(result);
        Assert.NotEmpty(result);

        var pb = result.First();
        Assert.NotNull(pb.Manufacturer);
        Assert.NotNull(pb.Description);
        Assert.NotNull(pb.EffectiveDate);
    }

    [SkippableFact]
    [TestPriority(16)]
    public async Task GetPriceBooks_ReturnsResults()
    {
        SkipIfNoApiKey();
        Skip.If(_manufacturerAbbr == null, "Requires GetManufacturers to run first");

        var result = await Client!.GetPriceBooksAsync(
            new DateTimeOffset(2000, 1, 1, 0, 0, 0, TimeSpan.Zero),
            _manufacturerAbbr!);

        Assert.NotNull(result);
        Assert.NotEmpty(result);

        var pb = result.First();
        Assert.NotNull(pb.Manufacturer);
        Assert.NotNull(pb.Description);
        Assert.NotNull(pb.EffectiveDate);
    }
}
