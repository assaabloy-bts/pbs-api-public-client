package com.assaabloy.bts.pbs.api.client;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.assaabloy.bts.pbs.api.client.model.Attribute;
import com.assaabloy.bts.pbs.api.client.model.DescriptionAndPrices;
import com.assaabloy.bts.pbs.api.client.model.HardwareAttributes;
import com.assaabloy.bts.pbs.api.client.model.HardwareItem;
import com.assaabloy.bts.pbs.api.client.model.HardwareItems;
import com.assaabloy.bts.pbs.api.client.model.HardwareMatch;
import com.assaabloy.bts.pbs.api.client.model.HardwareOption;
import com.assaabloy.bts.pbs.api.client.model.HardwareOptions;
import com.assaabloy.bts.pbs.api.client.model.Manufacturer;
import com.assaabloy.bts.pbs.api.client.model.Manufacturers;
import com.assaabloy.bts.pbs.api.client.model.PriceBook;
import com.assaabloy.bts.pbs.api.client.model.ProductLine;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PbsClientTest {

    private static PbsClient client;
    private static String    manufacturerAbbr;
    private static Long      manufacturerNumericId;
    private static Long      hardwareItemXref;
    private static Long      hardwareOptionXref;

    @BeforeAll
    static void setUp() {
        final String apiKey = System.getenv("PBS_API_KEY");
        Assumptions.assumeTrue(apiKey != null && !apiKey.isBlank(), "PBS_API_KEY environment variable is required to run integration tests");

        final String baseUrl = System.getenv("PBS_API_URL");
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
        final Manufacturers result = client.getManufacturers("SA", true, null, 20L, null, null, 0L);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getData());
        Assertions.assertFalse(result.getData().isEmpty(), "Expected at least one manufacturer");
        Assertions.assertEquals(0, result.getStartRow());
        Assertions.assertTrue(result.getTotalRows() > 0);

        final Manufacturer mfg = result.getData().getFirst();
        Assertions.assertNotNull(mfg.getName());
        Assertions.assertNotNull(mfg.getAbbr());
        Assertions.assertNotNull(mfg.getManufacturerId());

        manufacturerAbbr = mfg.getAbbr();
        manufacturerNumericId = mfg.getManufacturerId();
    }

    @Test
    @Order(2)
    void getManufacturerById() throws ApiException {
        Assertions.assertNotNull(manufacturerNumericId, "Requires getManufacturers to run first");

        final Manufacturer result = client.getManufacturerById(manufacturerNumericId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(manufacturerNumericId, result.getManufacturerId());
        Assertions.assertNotNull(result.getName());
        Assertions.assertNotNull(result.getAbbr());
        Assertions.assertNotNull(result.getType());
    }

    // -- Product Lines & Subtypes --

    @Test
    @Order(3)
    void getProductLines() throws ApiException {
        Assertions.assertNotNull(manufacturerAbbr);

        final List<ProductLine> result = client.getProductLines(manufacturerAbbr);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty(), "Expected at least one product line");

        final ProductLine pl = result.getFirst();
        Assertions.assertNotNull(pl.getProductLine());
        Assertions.assertNotNull(pl.getHardwareType());
    }

    @Test
    @Order(4)
    void getSubTypes() throws ApiException {
        Assertions.assertNotNull(manufacturerAbbr);

        final List<String> result = client.getSubTypes(manufacturerAbbr, null);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty(), "Expected at least one subtype");
        Assertions.assertNotNull(result.getFirst());
    }

    // -- Hardware Items --

    @Test
    @Order(5)
    void getHardwareItems() throws ApiException {
        Assertions.assertNotNull(manufacturerAbbr);

        final HardwareItems result = client.getHardwareItems(manufacturerAbbr, null, null, 5L, null, null, null, null, 0L);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getData());
        Assertions.assertFalse(result.getData().isEmpty(), "Expected at least one hardware item");
        Assertions.assertEquals(0, result.getStartRow());
        Assertions.assertTrue(result.getTotalRows() > 0);

        final HardwareItem item = result.getData().getFirst();
        Assertions.assertNotNull(item.getXref());
        Assertions.assertNotNull(item.getManufacturer());
        Assertions.assertNotNull(item.getPartNumber());

        hardwareItemXref = item.getXref();
    }

    @Test
    @Order(6)
    void getHardwareItemById() throws ApiException {
        Assertions.assertNotNull(hardwareItemXref, "Requires getHardwareItems to run first");

        final HardwareItem result = client.getHardwareItemById(hardwareItemXref);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(hardwareItemXref, result.getXref());
        Assertions.assertNotNull(result.getManufacturer());
        Assertions.assertNotNull(result.getPartNumber());
        Assertions.assertNotNull(result.getType());
    }

    @Test
    @Order(7)
    void getHardwareAttributes() throws ApiException {
        Assertions.assertNotNull(manufacturerAbbr);

        final HardwareAttributes result = client.getHardwareAttributes(manufacturerAbbr, 5L, null, null, null, null, 0L);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getData());
        Assertions.assertFalse(result.getData().isEmpty(), "Expected at least one hardware attribute");
        Assertions.assertEquals(0, result.getStartRow());
        Assertions.assertTrue(result.getTotalRows() > 0);

        final Attribute attr = result.getData().getFirst();
        Assertions.assertNotNull(attr.getXref());
        Assertions.assertNotNull(attr.getManufacturer());
        Assertions.assertNotNull(attr.getType());
        Assertions.assertNotNull(attr.getPrintCode());
        Assertions.assertNotNull(attr.getPrintDescription());
    }

    @Test
    @Order(8)
    void locateHardware() throws ApiException {
        Assertions.assertNotNull(hardwareItemXref, "Requires getHardwareItems to run first");

        // Fetch the item to get its part number
        final HardwareItem item = client.getHardwareItemById(hardwareItemXref);

        final List<HardwareMatch> result = client.locateHardware(manufacturerAbbr, item.getPartNumber(), 5, false, false, false, null, null);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty(), "Expected at least one match");

        final HardwareMatch match = result.getFirst();
        Assertions.assertNotNull(match.getPartNumber());
        Assertions.assertNotNull(match.getOrderDescription());
        Assertions.assertNotNull(match.getMatchScore());
    }

    // -- Hardware Item Options --

    @Test
    @Order(9)
    void getHardwareItemsOptions() throws ApiException {
        Assertions.assertNotNull(hardwareItemXref, "Requires getHardwareItems to run first");

        final HardwareOptions result = client.getHardwareItemsOptions(hardwareItemXref, 5L, null, null, 0L);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getData());
        Assertions.assertEquals(0, result.getStartRow());

        if (!result.getData().isEmpty()) {
            final HardwareOption opt = result.getData().getFirst();
            Assertions.assertNotNull(opt.getXref());
            Assertions.assertNotNull(opt.getManufacturer());
            Assertions.assertNotNull(opt.getPrintCode());
            Assertions.assertNotNull(opt.getPrintDescription());
            hardwareOptionXref = opt.getXref();
        }
    }

    @Test
    @Order(10)
    void getHardwareItemsOptionById() throws ApiException {
        Assumptions.assumeTrue(hardwareOptionXref != null, "No options available for the test hardware item");

        final HardwareOption result = client.getHardwareItemsOptionById(hardwareOptionXref, hardwareItemXref);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(hardwareOptionXref, result.getXref());
        Assertions.assertNotNull(result.getManufacturer());
        Assertions.assertNotNull(result.getPrintCode());
    }

    @Test
    @Order(11)
    void getHardwareItemPriceWithOptions() throws ApiException {
        Assertions.assertNotNull(hardwareItemXref, "Requires getHardwareItems to run first");

        final DescriptionAndPrices result = client.getHardwareItemPriceWithOptions(hardwareItemXref, null, null, null, null, null, null);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getPartNumber());
        Assertions.assertNotNull(result.getOrderDescription());
    }

    // -- Hardware Options (global) --

    @Test
    @Order(12)
    void getHardwareOptions() throws ApiException {
        Assertions.assertNotNull(manufacturerAbbr);

        final HardwareOptions result = client.getHardwareOptions(manufacturerAbbr, 5L, null, null, null, 0L);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getData());
        Assertions.assertFalse(result.getData().isEmpty(), "Expected at least one hardware option");
        Assertions.assertEquals(0, result.getStartRow());
        Assertions.assertTrue(result.getTotalRows() > 0);

        final HardwareOption opt = result.getData().getFirst();
        Assertions.assertNotNull(opt.getXref());
        Assertions.assertNotNull(opt.getManufacturer());
        Assertions.assertNotNull(opt.getPrintCode());
        Assertions.assertNotNull(opt.getPrintDescription());
    }

    @Test
    @Order(13)
    void getHardwareOptionById() throws ApiException {
        Assertions.assertNotNull(manufacturerAbbr);

        // Get an option xref from the global options list
        final HardwareOptions options = client.getHardwareOptions(manufacturerAbbr, 1L, null, null, null, 0L);
        Assertions.assertFalse(options.getData().isEmpty());
        final long optXref = options.getData().getFirst().getXref();

        final HardwareOption result = client.getHardwareOptionById(optXref);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(optXref, result.getXref());
        Assertions.assertNotNull(result.getManufacturer());
        Assertions.assertNotNull(result.getPrintCode());
    }

    // -- Price Books --

    @Test
    @Order(14)
    void getPriceBookComparisons() throws ApiException {
        Assertions.assertNotNull(manufacturerAbbr);

        try {
            client.getPriceBookComparisons(manufacturerAbbr, false);
        } catch (final ApiException e) {
            // 504 Gateway Timeout is acceptable for this long-running comparison
            if (e.getCode() != 504) {
                throw e;
            }
        }
    }

    @Test
    @Order(15)
    void listPriceBooks() throws ApiException {
        Assertions.assertNotNull(manufacturerAbbr);

        final List<PriceBook> result = client.listPriceBooks(manufacturerAbbr);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty(), "Expected at least one price book");

        final PriceBook pb = result.getFirst();
        Assertions.assertNotNull(pb.getManufacturer());
        Assertions.assertNotNull(pb.getDescription());
        Assertions.assertNotNull(pb.getEffectiveDate());
    }

    @Test
    @Order(16)
    void getPriceBooks() throws ApiException {
        Assertions.assertNotNull(manufacturerAbbr);

        final List<PriceBook> result = client.getPriceBooks(LocalDate.of(2000, 1, 1), manufacturerAbbr);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty(), "Expected at least one price book after 2000-01-01");

        final PriceBook pb = result.getFirst();
        Assertions.assertNotNull(pb.getManufacturer());
        Assertions.assertNotNull(pb.getDescription());
        Assertions.assertNotNull(pb.getEffectiveDate());
    }

}
