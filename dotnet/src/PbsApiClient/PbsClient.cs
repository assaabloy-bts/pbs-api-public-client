using System.Net.Http.Headers;

namespace AssaAbloy.Bts.PbsApiClient;

/// <summary>
/// Unified client for the PBS Public API.
/// </summary>
public class PbsClient
{
    private const string DefaultBaseUrl = "https://public.api.aa-bts.com";

    private readonly HttpClient _httpClient;
    private readonly PbsApiClient _apiClient;

    /// <summary>
    /// Creates a client using the default production URL.
    /// </summary>
    /// <param name="apiKey">The bearer token for authentication.</param>
    public PbsClient(string apiKey) : this(DefaultBaseUrl, apiKey)
    {
    }

    /// <summary>
    /// Creates a client with a custom base URL.
    /// </summary>
    /// <param name="baseUrl">The base URL of the PBS API.</param>
    /// <param name="apiKey">The bearer token for authentication.</param>
    public PbsClient(string baseUrl, string apiKey)
    {
        _httpClient = new HttpClient
        {
            BaseAddress = new Uri(baseUrl)
        };
        _httpClient.DefaultRequestHeaders.Authorization =
            new AuthenticationHeaderValue("Bearer", apiKey);

        _apiClient = new PbsApiClient(_httpClient);
    }

    /// <summary>
    /// Creates a client with a pre-configured HttpClient. For testing purposes.
    /// </summary>
    internal PbsClient(HttpClient httpClient)
    {
        _httpClient = httpClient;
        _apiClient = new PbsApiClient(_httpClient);
    }

    // -- Hardware Attributes --

    /// <summary>Returns a list of hardware attributes.</summary>
    public Task<HardwareAttributes> GetHardwareAttributesAsync(
        string manufacturerId, long? endRow = null, HdwTypeEnum? hardwareType = null,
        string? productLine = null, string? query = null, string? sort = null,
        long? startRow = null, CancellationToken cancellationToken = default)
    {
        return _apiClient.GetHardwareAttributesAsync(manufacturerId, endRow, hardwareType,
            productLine, query, sort, startRow, cancellationToken);
    }

    // -- Hardware Items --

    /// <summary>Returns a list of hardware items.</summary>
    public Task<HardwareItems> GetHardwareItemsAsync(
        string manufacturerId, IEnumerable<long>? attributeXrefs = null, long? xref = null,
        long? endRow = null, HdwTypeEnum? hardwareType = null, string? productLine = null,
        string? query = null, string? sort = null, long? startRow = null,
        CancellationToken cancellationToken = default)
    {
        return _apiClient.GetHardwareItemsAsync(attributeXrefs, manufacturerId, xref, endRow,
            hardwareType, productLine, query, sort, startRow, cancellationToken);
    }

    /// <summary>Returns a single hardware item by xref ID.</summary>
    public Task<HardwareItem> GetHardwareItemByIdAsync(
        long xref, CancellationToken cancellationToken = default)
    {
        return _apiClient.GetHardwareItemByIdAsync(xref, cancellationToken);
    }

    /// <summary>Searches for a part number to find an exact or closest match.</summary>
    public Task<ICollection<HardwareMatch>> LocateHardwareAsync(
        string manufacturerId, string partNumber, int resultQty,
        bool? excludeNoPriceOptions = null, bool? excludeOptions = null,
        bool? includeHardwareItem = null, string? productLine = null,
        string? separatorCharacter = null, CancellationToken cancellationToken = default)
    {
        return _apiClient.LocateHardwareAsync(excludeNoPriceOptions, excludeOptions,
            includeHardwareItem, manufacturerId, partNumber, productLine, resultQty,
            separatorCharacter, cancellationToken);
    }

    // -- Hardware Item Options --

    /// <summary>Returns a list of hardware options for a hardware item.</summary>
    public Task<HardwareOptions> GetHardwareItemsOptionsAsync(
        long xref, long? endRow = null, string? query = null, string? sort = null,
        long? startRow = null, CancellationToken cancellationToken = default)
    {
        return _apiClient.GetHardwareItemsOptionsAsync(xref, endRow, query, sort, startRow,
            cancellationToken);
    }

    /// <summary>Returns a single hardware option for a hardware item.</summary>
    public Task<HardwareOption> GetHardwareItemsOptionByIdAsync(
        long optionxref, long xref, CancellationToken cancellationToken = default)
    {
        return _apiClient.GetHardwareItemsOptionByIdAsync(optionxref, xref, cancellationToken);
    }

    /// <summary>Gets the price and description of a hardware item with options.</summary>
    public Task<DescriptionAndPrices> GetHardwareItemPriceWithOptionsAsync(
        long xref, string? hand = null, string? height = null, string? length = null,
        IEnumerable<long>? optionxref = null, string? priceBook = null, string? width = null,
        CancellationToken cancellationToken = default)
    {
        return _apiClient.GetHardwareItemPriceWithOptionsAsync(xref, hand, height, length,
            optionxref, priceBook, width, cancellationToken);
    }

    // -- Hardware Options --

    /// <summary>Returns a list of hardware options.</summary>
    public Task<HardwareOptions> GetHardwareOptionsAsync(
        string manufacturerId, long? endRow = null, string? productLine = null,
        string? query = null, string? sort = null, long? startRow = null,
        CancellationToken cancellationToken = default)
    {
        return _apiClient.GetHardwareOptionsAsync(manufacturerId, endRow, productLine, query,
            sort, startRow, cancellationToken);
    }

    /// <summary>Returns a single hardware option by option xref ID.</summary>
    public Task<HardwareOption> GetHardwareOptionByIdAsync(
        long optionxref, CancellationToken cancellationToken = default)
    {
        return _apiClient.GetHardwareOptionByIdAsync(optionxref, cancellationToken);
    }

    // -- Product Lines & Subtypes --

    /// <summary>Returns a list of product lines for a manufacturer.</summary>
    public Task<ICollection<ProductLine>> GetProductLinesAsync(
        string manufacturerId, CancellationToken cancellationToken = default)
    {
        return _apiClient.GetProductLinesAsync(manufacturerId, cancellationToken);
    }

    /// <summary>Lists subtypes by manufacturer.</summary>
    public Task<ICollection<string>> GetSubTypesAsync(
        string manufacturerId, string? productLine = null,
        CancellationToken cancellationToken = default)
    {
        return _apiClient.GetSubTypesAsync(manufacturerId, productLine, cancellationToken);
    }

    // -- Manufacturers --

    /// <summary>Returns a list of manufacturers.</summary>
    public Task<Manufacturers> GetManufacturersAsync(
        bool? hasProduct = null, string? manufacturerId = null, ManufacturerType? type = null,
        long? endRow = null, string? query = null, string? sort = null,
        long? startRow = null, CancellationToken cancellationToken = default)
    {
        return _apiClient.GetManufacturersAsync(hasProduct, manufacturerId, type, endRow, query,
            sort, startRow, cancellationToken);
    }

    /// <summary>Returns a single manufacturer by ID.</summary>
    public Task<Manufacturer> GetManufacturerByIdAsync(
        long manufacturerId, CancellationToken cancellationToken = default)
    {
        return _apiClient.GetManufacturerByIdAsync(manufacturerId, cancellationToken);
    }

    // -- Price Books --

    /// <summary>Returns price book comparison data.</summary>
    public Task GetPriceBookComparisonsAsync(
        string manufacturerId, bool? futurePriceBook = null,
        CancellationToken cancellationToken = default)
    {
        return _apiClient.GetPriceBookComparisonsAsync(futurePriceBook, manufacturerId,
            cancellationToken);
    }

    /// <summary>Retrieves all price books available after given date.</summary>
    public Task<ICollection<PriceBook>> GetPriceBooksAsync(
        DateTimeOffset priceBooksAfterDate, string? manufacturerId = null,
        CancellationToken cancellationToken = default)
    {
        return _apiClient.GetPriceBooksAsync(manufacturerId, priceBooksAfterDate,
            cancellationToken);
    }

    /// <summary>Retrieves all price books available for manufacturer.</summary>
    public Task<ICollection<PriceBook>> ListPriceBooksAsync(
        string manufacturerId, CancellationToken cancellationToken = default)
    {
        return _apiClient.ListPriceBooksAsync(manufacturerId, cancellationToken);
    }
}
