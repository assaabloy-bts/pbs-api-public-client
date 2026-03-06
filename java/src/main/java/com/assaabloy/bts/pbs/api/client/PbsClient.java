package com.assaabloy.bts.pbs.api.client;

import java.time.LocalDate;
import java.util.List;

import com.assaabloy.bts.pbs.api.client.api.HardwareApi;
import com.assaabloy.bts.pbs.api.client.api.ManufacturersApi;
import com.assaabloy.bts.pbs.api.client.api.PbsApi;
import com.assaabloy.bts.pbs.api.client.model.DescriptionAndPrices;
import com.assaabloy.bts.pbs.api.client.model.HardwareAttributes;
import com.assaabloy.bts.pbs.api.client.model.HardwareItem;
import com.assaabloy.bts.pbs.api.client.model.HardwareItems;
import com.assaabloy.bts.pbs.api.client.model.HardwareMatch;
import com.assaabloy.bts.pbs.api.client.model.HardwareOption;
import com.assaabloy.bts.pbs.api.client.model.HardwareOptions;
import com.assaabloy.bts.pbs.api.client.model.HdwTypeEnum;
import com.assaabloy.bts.pbs.api.client.model.Manufacturer;
import com.assaabloy.bts.pbs.api.client.model.ManufacturerType;
import com.assaabloy.bts.pbs.api.client.model.Manufacturers;
import com.assaabloy.bts.pbs.api.client.model.PriceBook;
import com.assaabloy.bts.pbs.api.client.model.ProductLine;

/**
 * Unified client for the PBS Public API.
 *
 * <p>
 * The {@code manufacturerId} parameter accepted by most methods can be the manufacturer's numeric ID, abbreviation, or name.
 * </p>
 */
public class PbsClient {

    private static final String DEFAULT_BASE_URL = "https://public.api.aa-bts.com";

    private final HardwareApi      hardwareApi;
    private final ManufacturersApi manufacturersApi;
    private final PbsApi           pbsApi;

    /**
     * Creates a client using the default production URL.
     *
     * @param apiKey the bearer token for authentication
     */
    public PbsClient(final String apiKey) {
        this(DEFAULT_BASE_URL, apiKey);
    }

    /**
     * Creates a client with a custom base URL.
     *
     * @param baseUrl the base URL of the PBS API
     * @param apiKey the bearer token for authentication
     */
    public PbsClient(final String baseUrl, final String apiKey) {
        final ApiClient apiClient = new ApiClient();
        apiClient.updateBaseUri(baseUrl);
        apiClient.setRequestInterceptor(builder -> builder.header("Authorization", "Bearer " + apiKey));

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
    public HardwareAttributes getHardwareAttributes(final String manufacturerId, final Long endRow, final HdwTypeEnum hardwareType, final String productLine,
            final String query, final String sort, final Long startRow) throws ApiException {
        return hardwareApi.getHardwareAttributes(manufacturerId, endRow, hardwareType, productLine, query, sort, startRow);
    }

    // -- Hardware Items --

    /**
     * Returns a list of hardware items.
     *
     * @param manufacturerId the manufacturer ID, abbreviation, or name
     */
    public HardwareItems getHardwareItems(final String manufacturerId, final List<Long> attributeXrefs, final Long xref, final Long endRow,
            final HdwTypeEnum hardwareType, final String productLine, final String query, final String sort, final Long startRow) throws ApiException {
        return hardwareApi.getHardwareItems(manufacturerId, attributeXrefs, xref, endRow, hardwareType, productLine, query, sort, startRow);
    }

    /**
     * Returns a single hardware item by xref ID.
     */
    public HardwareItem getHardwareItemById(final long xref) throws ApiException {
        return hardwareApi.getHardwareItemById(xref);
    }

    /**
     * Searches for a part number to find an exact or closest match.
     *
     * @param manufacturerId the manufacturer ID, abbreviation, or name
     */
    public List<HardwareMatch> locateHardware(final String manufacturerId, final String partNumber, final int resultQty, final boolean excludeNoPriceOptions,
            final boolean excludeOptions, final boolean includeHardwareItem, final String productLine, final String separatorCharacter) throws ApiException {
        return hardwareApi.locateHardware(manufacturerId, partNumber, resultQty, excludeNoPriceOptions, excludeOptions, includeHardwareItem, productLine,
                separatorCharacter);
    }

    // -- Hardware Item Options --

    /**
     * Returns a list of hardware options for a hardware item.
     */
    public HardwareOptions getHardwareItemsOptions(final long xref, final Long endRow, final String query, final String sort, final Long startRow)
            throws ApiException {
        return hardwareApi.getHardwareItemsOptions(xref, endRow, query, sort, startRow);
    }

    /**
     * Returns a single hardware option for a hardware item.
     */
    public HardwareOption getHardwareItemsOptionById(final long optionxref, final long xref) throws ApiException {
        return hardwareApi.getHardwareItemsOptionById(optionxref, xref);
    }

    /**
     * Gets the price and updated description of a hardware item with options.
     */
    public DescriptionAndPrices getHardwareItemPriceWithOptions(final long xref, final String hand, final String height, final String length,
            final List<Long> optionxref, final String priceBook, final String width) throws ApiException {
        return hardwareApi.getHardwareItemPriceWithOptions(xref, hand, height, length, optionxref, priceBook, width);
    }

    // -- Hardware Options --

    /**
     * Returns a list of hardware options.
     *
     * @param manufacturerId the manufacturer ID, abbreviation, or name
     */
    public HardwareOptions getHardwareOptions(final String manufacturerId, final Long endRow, final String productLine, final String query, final String sort,
            final Long startRow) throws ApiException {
        return hardwareApi.getHardwareOptions(manufacturerId, endRow, productLine, query, sort, startRow);
    }

    /**
     * Returns a single hardware option by option xref ID.
     */
    public HardwareOption getHardwareOptionById(final long optionxref) throws ApiException {
        return hardwareApi.getHardwareOptionById(optionxref);
    }

    // -- Product Lines & Subtypes --

    /**
     * Returns a list of product lines for a manufacturer.
     *
     * @param manufacturerId the manufacturer ID, abbreviation, or name
     */
    public List<ProductLine> getProductLines(final String manufacturerId) throws ApiException {
        return hardwareApi.getProductLines(manufacturerId);
    }

    /**
     * Lists subtypes by manufacturer.
     *
     * @param manufacturerId the manufacturer ID, abbreviation, or name
     */
    public List<String> getSubTypes(final String manufacturerId, final String productLine) throws ApiException {
        return hardwareApi.getSubTypes(manufacturerId, productLine);
    }

    // -- Manufacturers --

    /**
     * Returns a list of manufacturers.
     *
     * @param manufacturerId the manufacturer ID, abbreviation, or name
     */
    public Manufacturers getManufacturers(final String manufacturerId, final boolean hasProduct, final ManufacturerType type, final Long endRow,
            final String query, final String sort, final Long startRow) throws ApiException {
        return manufacturersApi.getManufacturers(hasProduct, manufacturerId, type, endRow, query, sort, startRow);
    }

    /**
     * Returns a single manufacturer by ID.
     */
    public Manufacturer getManufacturerById(final long manufacturerId) throws ApiException {
        return manufacturersApi.getManufacturerById(manufacturerId);
    }

    // -- Price Books --

    /**
     * Returns a list of hdw standards that have been removed and added with their prices.
     *
     * @param manufacturerId the manufacturer ID, abbreviation, or name
     */
    public void getPriceBookComparisons(final String manufacturerId, final boolean futurePriceBook) throws ApiException {
        pbsApi.getPriceBookComparisons(manufacturerId, futurePriceBook);
    }

    /**
     * Retrieves all price books available after given date.
     *
     * @param manufacturerId the manufacturer ID, abbreviation, or name
     */
    public List<PriceBook> getPriceBooks(final LocalDate priceBooksAfterDate, final String manufacturerId) throws ApiException {
        return pbsApi.getPriceBooks(priceBooksAfterDate, manufacturerId);
    }

    /**
     * Retrieves all price books available for manufacturer.
     *
     * @param manufacturerId the manufacturer ID, abbreviation, or name
     */
    public List<PriceBook> listPriceBooks(final String manufacturerId) throws ApiException {
        return pbsApi.listPriceBooks(manufacturerId);
    }

}
