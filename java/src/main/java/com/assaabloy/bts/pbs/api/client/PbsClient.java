package com.assaabloy.bts.pbs.api.client;

import java.time.LocalDate;
import java.util.List;

import com.assaabloy.bts.pbs.api.client.api.HardwareApi;
import com.assaabloy.bts.pbs.api.client.api.ManufacturersApi;
import com.assaabloy.bts.pbs.api.client.api.PbsApi;
import com.assaabloy.bts.pbs.api.client.model.*;

/**
 * Unified client for the PBS Public API.
 *
 * <p>The {@code manufacturerId} parameter accepted by most methods can be the
 * manufacturer's numeric ID, abbreviation, or name.</p>
 */
public class PbsClient {

    private static final String DEFAULT_BASE_URL = "https://public.api.aa-bts.com";

    private final HardwareApi hardwareApi;
    private final ManufacturersApi manufacturersApi;
    private final PbsApi pbsApi;

    /**
     * Creates a client using the default production URL.
     *
     * @param apiKey the bearer token for authentication
     */
    public PbsClient(String apiKey) {
        this(DEFAULT_BASE_URL, apiKey);
    }

    /**
     * Creates a client with a custom base URL.
     *
     * @param baseUrl the base URL of the PBS API
     * @param apiKey the bearer token for authentication
     */
    public PbsClient(String baseUrl, String apiKey) {
        ApiClient apiClient = new ApiClient();
        apiClient.updateBaseUri(baseUrl);
        apiClient.setRequestInterceptor(builder ->
                builder.header("Authorization", "Bearer " + apiKey));

        hardwareApi = new HardwareApi(apiClient);
        manufacturersApi = new ManufacturersApi(apiClient);
        pbsApi = new PbsApi(apiClient);
    }

    // -- Hardware Attributes --

    /**
     * Returns a list of hardware attributes.
     *
     * @param manufacturerId the manufacturer ID, abbreviation, or name
     */
    public HardwareAttributes getHardwareAttributes(String manufacturerId, Long endRow,
            HdwTypeEnum hardwareType, String productLine, String query, String sort,
            Long startRow) throws ApiException {
        return hardwareApi.getHardwareAttributes(manufacturerId, endRow, hardwareType,
                productLine, query, sort, startRow);
    }

    // -- Hardware Items --

    /**
     * Returns a list of hardware items.
     *
     * @param manufacturerId the manufacturer ID, abbreviation, or name
     */
    public HardwareItems getHardwareItems(String manufacturerId, List<Long> attributeXrefs,
            Long xref, Long endRow, HdwTypeEnum hardwareType, String productLine, String query,
            String sort, Long startRow) throws ApiException {
        return hardwareApi.getHardwareItems(manufacturerId, attributeXrefs, xref, endRow,
                hardwareType, productLine, query, sort, startRow);
    }

    /**
     * Returns a single hardware item by xref ID.
     */
    public HardwareItem getHardwareItemById(long xref) throws ApiException {
        return hardwareApi.getHardwareItemById(xref);
    }

    /**
     * Searches for a part number to find an exact or closest match.
     *
     * @param manufacturerId the manufacturer ID, abbreviation, or name
     */
    public List<HardwareMatch> locateHardware(String manufacturerId, String partNumber,
            int resultQty, Boolean excludeNoPriceOptions, Boolean excludeOptions,
            Boolean includeHardwareItem, String productLine,
            String separatorCharacter) throws ApiException {
        return hardwareApi.locateHardware(manufacturerId, partNumber, resultQty,
                excludeNoPriceOptions, excludeOptions, includeHardwareItem, productLine,
                separatorCharacter);
    }

    // -- Hardware Item Options --

    /**
     * Returns a list of hardware options for a hardware item.
     */
    public HardwareOptions getHardwareItemsOptions(long xref, Long endRow, String query,
            String sort, Long startRow) throws ApiException {
        return hardwareApi.getHardwareItemsOptions(xref, endRow, query, sort, startRow);
    }

    /**
     * Returns a single hardware option for a hardware item.
     */
    public HardwareOption getHardwareItemsOptionById(long optionxref,
            long xref) throws ApiException {
        return hardwareApi.getHardwareItemsOptionById(optionxref, xref);
    }

    /**
     * Gets the price and updated description of a hardware item with options.
     */
    public DescriptionAndPrices getHardwareItemPriceWithOptions(long xref, String hand,
            String height, String length, List<Long> optionxref, String priceBook,
            String width) throws ApiException {
        return hardwareApi.getHardwareItemPriceWithOptions(xref, hand, height, length,
                optionxref, priceBook, width);
    }

    // -- Hardware Options --

    /**
     * Returns a list of hardware options.
     *
     * @param manufacturerId the manufacturer ID, abbreviation, or name
     */
    public HardwareOptions getHardwareOptions(String manufacturerId, Long endRow,
            String productLine, String query, String sort, Long startRow) throws ApiException {
        return hardwareApi.getHardwareOptions(manufacturerId, endRow, productLine, query,
                sort, startRow);
    }

    /**
     * Returns a single hardware option by option xref ID.
     */
    public HardwareOption getHardwareOptionById(long optionxref) throws ApiException {
        return hardwareApi.getHardwareOptionById(optionxref);
    }

    // -- Product Lines & Subtypes --

    /**
     * Returns a list of product lines for a manufacturer.
     *
     * @param manufacturerId the manufacturer ID, abbreviation, or name
     */
    public List<ProductLine> getProductLines(String manufacturerId) throws ApiException {
        return hardwareApi.getProductLines(manufacturerId);
    }

    /**
     * Lists subtypes by manufacturer.
     *
     * @param manufacturerId the manufacturer ID, abbreviation, or name
     */
    public List<String> getSubTypes(String manufacturerId,
            String productLine) throws ApiException {
        return hardwareApi.getSubTypes(manufacturerId, productLine);
    }

    // -- Manufacturers --

    /**
     * Returns a list of manufacturers.
     *
     * @param manufacturerId the manufacturer ID, abbreviation, or name
     */
    public Manufacturers getManufacturers(String manufacturerId, Boolean hasProduct,
            ManufacturerType type, Long endRow, String query, String sort,
            Long startRow) throws ApiException {
        return manufacturersApi.getManufacturers(hasProduct, manufacturerId, type, endRow,
                query, sort, startRow);
    }

    /**
     * Returns a single manufacturer by ID.
     */
    public Manufacturer getManufacturerById(long manufacturerId) throws ApiException {
        return manufacturersApi.getManufacturerById(manufacturerId);
    }

    // -- Price Books --

    /**
     * Returns a list of hdw standards that have been removed and added with their prices.
     *
     * @param manufacturerId the manufacturer ID, abbreviation, or name
     */
    public void getPriceBookComparisons(String manufacturerId,
            Boolean futurePriceBook) throws ApiException {
        pbsApi.getPriceBookComparisons(manufacturerId, futurePriceBook);
    }

    /**
     * Retrieves all price books available after given date.
     *
     * @param manufacturerId the manufacturer ID, abbreviation, or name
     */
    public List<PriceBook> getPriceBooks(LocalDate priceBooksAfterDate,
            String manufacturerId) throws ApiException {
        return pbsApi.getPriceBooks(priceBooksAfterDate, manufacturerId);
    }

    /**
     * Retrieves all price books available for manufacturer.
     *
     * @param manufacturerId the manufacturer ID, abbreviation, or name
     */
    public List<PriceBook> listPriceBooks(String manufacturerId) throws ApiException {
        return pbsApi.listPriceBooks(manufacturerId);
    }
}
