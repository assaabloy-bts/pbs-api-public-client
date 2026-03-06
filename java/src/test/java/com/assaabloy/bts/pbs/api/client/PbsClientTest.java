package com.assaabloy.bts.pbs.api.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.assaabloy.bts.pbs.api.client.model.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PbsClientTest {

    private static PbsClient client;
    private static String manufacturerAbbr;
    private static Long manufacturerNumericId;
    private static Long hardwareItemXref;
    private static Long hardwareOptionXref;

    @BeforeAll
    static void setUp() {
        String apiKey = System.getenv("PBS_API_KEY");
        assumeTrue(apiKey != null && !apiKey.isBlank(),
                "PBS_API_KEY environment variable is required to run integration tests");

        String baseUrl = System.getenv("PBS_API_URL");
        if (baseUrl != null && !baseUrl.isBlank()) {
            client = new PbsClient(baseUrl, apiKey);
        } else {
            client = new PbsClient(apiKey);
        }
    }

    // -- Manufacturers --

    @Test
    @Order(1)
    void getManufacturers() throws ApiException {
        Manufacturers result = client.getManufacturers(
                "SA", true, null, 20L, null, null, 0L);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertFalse(result.getData().isEmpty(), "Expected at least one manufacturer");
        assertEquals(0, result.getStartRow());
        assertTrue(result.getTotalRows() > 0);

        Manufacturer mfg = result.getData().get(0);
        assertNotNull(mfg.getName());
        assertNotNull(mfg.getAbbr());
        assertNotNull(mfg.getManufacturerId());

        manufacturerAbbr = mfg.getAbbr();
        manufacturerNumericId = mfg.getManufacturerId();
    }

    @Test
    @Order(2)
    void getManufacturerById() throws ApiException {
        assertNotNull(manufacturerNumericId, "Requires getManufacturers to run first");

        Manufacturer result = client.getManufacturerById(manufacturerNumericId);

        assertNotNull(result);
        assertEquals(manufacturerNumericId, result.getManufacturerId());
        assertNotNull(result.getName());
        assertNotNull(result.getAbbr());
        assertNotNull(result.getType());
    }

    // -- Product Lines & Subtypes --

    @Test
    @Order(3)
    void getProductLines() throws ApiException {
        assertNotNull(manufacturerAbbr);

        List<ProductLine> result = client.getProductLines(manufacturerAbbr);

        assertNotNull(result);
        assertFalse(result.isEmpty(), "Expected at least one product line");

        ProductLine pl = result.get(0);
        assertNotNull(pl.getProductLine());
        assertNotNull(pl.getHardwareType());
    }

    @Test
    @Order(4)
    void getSubTypes() throws ApiException {
        assertNotNull(manufacturerAbbr);

        List<String> result = client.getSubTypes(manufacturerAbbr, null);

        assertNotNull(result);
        assertFalse(result.isEmpty(), "Expected at least one subtype");
        assertNotNull(result.get(0));
    }

    // -- Hardware Items --

    @Test
    @Order(5)
    void getHardwareItems() throws ApiException {
        assertNotNull(manufacturerAbbr);

        HardwareItems result = client.getHardwareItems(
                manufacturerAbbr, null, null, 5L, null, null, null, null, 0L);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertFalse(result.getData().isEmpty(), "Expected at least one hardware item");
        assertEquals(0, result.getStartRow());
        assertTrue(result.getTotalRows() > 0);

        HardwareItem item = result.getData().get(0);
        assertNotNull(item.getXref());
        assertNotNull(item.getManufacturer());
        assertNotNull(item.getPartNumber());

        hardwareItemXref = item.getXref();
    }

    @Test
    @Order(6)
    void getHardwareItemById() throws ApiException {
        assertNotNull(hardwareItemXref, "Requires getHardwareItems to run first");

        HardwareItem result = client.getHardwareItemById(hardwareItemXref);

        assertNotNull(result);
        assertEquals(hardwareItemXref, result.getXref());
        assertNotNull(result.getManufacturer());
        assertNotNull(result.getPartNumber());
        assertNotNull(result.getType());
    }

    @Test
    @Order(7)
    void getHardwareAttributes() throws ApiException {
        assertNotNull(manufacturerAbbr);

        HardwareAttributes result = client.getHardwareAttributes(
                manufacturerAbbr, 5L, null, null, null, null, 0L);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertFalse(result.getData().isEmpty(), "Expected at least one hardware attribute");
        assertEquals(0, result.getStartRow());
        assertTrue(result.getTotalRows() > 0);

        Attribute attr = result.getData().get(0);
        assertNotNull(attr.getXref());
        assertNotNull(attr.getManufacturer());
        assertNotNull(attr.getType());
        assertNotNull(attr.getPrintCode());
        assertNotNull(attr.getPrintDescription());
    }

    @Test
    @Order(8)
    void locateHardware() throws ApiException {
        assertNotNull(hardwareItemXref, "Requires getHardwareItems to run first");

        // Fetch the item to get its part number
        HardwareItem item = client.getHardwareItemById(hardwareItemXref);

        List<HardwareMatch> result = client.locateHardware(
                manufacturerAbbr, item.getPartNumber(), 5, null, null, null, null, null);

        assertNotNull(result);
        assertFalse(result.isEmpty(), "Expected at least one match");

        HardwareMatch match = result.get(0);
        assertNotNull(match.getPartNumber());
        assertNotNull(match.getOrderDescription());
        assertNotNull(match.getMatchScore());
    }

    // -- Hardware Item Options --

    @Test
    @Order(9)
    void getHardwareItemsOptions() throws ApiException {
        assertNotNull(hardwareItemXref, "Requires getHardwareItems to run first");

        HardwareOptions result = client.getHardwareItemsOptions(
                hardwareItemXref, 5L, null, null, 0L);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(0, result.getStartRow());

        if (!result.getData().isEmpty()) {
            HardwareOption opt = result.getData().get(0);
            assertNotNull(opt.getXref());
            assertNotNull(opt.getManufacturer());
            assertNotNull(opt.getPrintCode());
            assertNotNull(opt.getPrintDescription());
            hardwareOptionXref = opt.getXref();
        }
    }

    @Test
    @Order(10)
    void getHardwareItemsOptionById() throws ApiException {
        assumeTrue(hardwareOptionXref != null,
                "No options available for the test hardware item");

        HardwareOption result = client.getHardwareItemsOptionById(
                hardwareOptionXref, hardwareItemXref);

        assertNotNull(result);
        assertEquals(hardwareOptionXref, result.getXref());
        assertNotNull(result.getManufacturer());
        assertNotNull(result.getPrintCode());
    }

    @Test
    @Order(11)
    void getHardwareItemPriceWithOptions() throws ApiException {
        assertNotNull(hardwareItemXref, "Requires getHardwareItems to run first");

        DescriptionAndPrices result = client.getHardwareItemPriceWithOptions(
                hardwareItemXref, null, null, null, null, null, null);

        assertNotNull(result);
        assertNotNull(result.getPartNumber());
        assertNotNull(result.getOrderDescription());
    }

    // -- Hardware Options (global) --

    @Test
    @Order(12)
    void getHardwareOptions() throws ApiException {
        assertNotNull(manufacturerAbbr);

        HardwareOptions result = client.getHardwareOptions(
                manufacturerAbbr, 5L, null, null, null, 0L);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertFalse(result.getData().isEmpty(), "Expected at least one hardware option");
        assertEquals(0, result.getStartRow());
        assertTrue(result.getTotalRows() > 0);

        HardwareOption opt = result.getData().get(0);
        assertNotNull(opt.getXref());
        assertNotNull(opt.getManufacturer());
        assertNotNull(opt.getPrintCode());
        assertNotNull(opt.getPrintDescription());
    }

    @Test
    @Order(13)
    void getHardwareOptionById() throws ApiException {
        assertNotNull(manufacturerAbbr);

        // Get an option xref from the global options list
        HardwareOptions options = client.getHardwareOptions(
                manufacturerAbbr, 1L, null, null, null, 0L);
        assertFalse(options.getData().isEmpty());
        long optXref = options.getData().get(0).getXref();

        HardwareOption result = client.getHardwareOptionById(optXref);

        assertNotNull(result);
        assertEquals(optXref, result.getXref());
        assertNotNull(result.getManufacturer());
        assertNotNull(result.getPrintCode());
    }

    // -- Price Books --

    @Test
    @Order(14)
    void getPriceBookComparisons() throws ApiException {
        assertNotNull(manufacturerAbbr);

        try {
            client.getPriceBookComparisons(manufacturerAbbr, false);
        } catch (ApiException e) {
            // 504 Gateway Timeout is acceptable for this long-running comparison
            if (e.getCode() != 504) {
                throw e;
            }
        }
    }

    @Test
    @Order(15)
    void listPriceBooks() throws ApiException {
        assertNotNull(manufacturerAbbr);

        List<PriceBook> result = client.listPriceBooks(manufacturerAbbr);

        assertNotNull(result);
        assertFalse(result.isEmpty(), "Expected at least one price book");

        PriceBook pb = result.get(0);
        assertNotNull(pb.getManufacturer());
        assertNotNull(pb.getDescription());
        assertNotNull(pb.getEffectiveDate());
    }

    @Test
    @Order(16)
    void getPriceBooks() throws ApiException {
        assertNotNull(manufacturerAbbr);

        List<PriceBook> result = client.getPriceBooks(
                LocalDate.of(2000, 1, 1), manufacturerAbbr);

        assertNotNull(result);
        assertFalse(result.isEmpty(), "Expected at least one price book after 2000-01-01");

        PriceBook pb = result.get(0);
        assertNotNull(pb.getManufacturer());
        assertNotNull(pb.getDescription());
        assertNotNull(pb.getEffectiveDate());
    }
}
