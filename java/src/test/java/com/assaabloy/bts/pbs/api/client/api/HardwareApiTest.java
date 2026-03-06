package com.assaabloy.bts.pbs.api.client.api;

import com.assaabloy.bts.pbs.api.client.ApiClient;
import com.assaabloy.bts.pbs.api.client.ApiException;
import com.assaabloy.bts.pbs.api.client.model.Attribute;
import com.assaabloy.bts.pbs.api.client.model.DescriptionAndPrices;
import com.assaabloy.bts.pbs.api.client.model.HardwareAttributes;
import com.assaabloy.bts.pbs.api.client.model.HardwareItem;
import com.assaabloy.bts.pbs.api.client.model.HardwareItems;
import com.assaabloy.bts.pbs.api.client.model.HardwareMatch;
import com.assaabloy.bts.pbs.api.client.model.HardwareOption;
import com.assaabloy.bts.pbs.api.client.model.HardwareOptions;
import com.assaabloy.bts.pbs.api.client.model.HdwTypeEnum;
import com.assaabloy.bts.pbs.api.client.model.ManufacturerId;
import com.assaabloy.bts.pbs.api.client.model.ProductLine;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for HardwareApi endpoints.
 */
class HardwareApiTest {

    private static final String BEARER_TOKEN = "test-jwt-token";

    private static WireMockServer wireMockServer;
    private static HardwareApi hardwareApi;

    @BeforeAll
    static void setUp() {
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());

        ApiClient apiClient = new ApiClient();
        apiClient.updateBaseUri("http://localhost:" + wireMockServer.port());
        apiClient.setRequestInterceptor(builder ->
                builder.header("Authorization", "Bearer " + BEARER_TOKEN));
        hardwareApi = new HardwareApi(apiClient);
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void getHardwareAttributes() throws ApiException {
        String responseBody = """
                {
                  "startRow": 0,
                  "endRow": 1,
                  "totalRows": 1,
                  "data": [
                    {
                      "xref": 766078,
                      "manufacturer": "SA",
                      "type": "Function",
                      "printCode": "7805",
                      "printDescription": "Office or Entry",
                      "uom": "EA",
                      "price": {
                        "priceBook": "May2020",
                        "effectiveDate": "2017-07-21",
                        "effective": true,
                        "listPrice": 994,
                        "price": 994,
                        "currency": "USD"
                      },
                      "preps": [
                        {
                          "id": 0,
                          "type": "Frame",
                          "code": "PREP1",
                          "description": "Standard Frame Prep"
                        }
                      ],
                      "numCylinders": 0,
                      "ansi": "A156.13",
                      "grade": "1",
                      "electrical": true,
                      "co2e": 0,
                      "embodiedCarbon": 0,
                      "energyInUse": 0,
                      "recyclability": 0,
                      "environmentalManagementSystem": "ISO 14001",
                      "referenceServiceLife": "25 years",
                      "fire": 0,
                      "smoke": 0,
                      "stc": 0
                    }
                  ]
                }
                """;

        wireMockServer.stubFor(get(urlPathEqualTo("/hardwareattributes"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        ManufacturerId manufacturerId = new ManufacturerId("SA");
        HardwareAttributes result = hardwareApi.getHardwareAttributes(
                manufacturerId, 20L, null, null, null, null, 0L);

        assertNotNull(result);
        assertEquals(0L, result.getStartRow());
        assertEquals(1L, result.getEndRow());
        assertEquals(1L, result.getTotalRows());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());

        Attribute attr = result.getData().get(0);
        assertEquals(766078L, attr.getXref());
        assertEquals("SA", attr.getManufacturer());
        assertEquals("Function", attr.getType());
        assertEquals("7805", attr.getPrintCode());
        assertEquals("Office or Entry", attr.getPrintDescription());
        assertTrue(attr.getElectrical());

        wireMockServer.verify(getRequestedFor(urlPathEqualTo("/hardwareattributes"))
                .withHeader("Authorization", equalTo("Bearer " + BEARER_TOKEN)));
    }

    @Test
    void getHardwareItems() throws ApiException {
        String responseBody = """
                {
                  "startRow": 0,
                  "endRow": 1,
                  "totalRows": 1,
                  "data": [
                    {
                      "xref": 766078,
                      "manufacturer": "SA",
                      "productLine": "7800 Mortise Locks",
                      "type": "LO",
                      "typeDescription": "Locksets",
                      "subTypeDescription": "Mortise Lock",
                      "partNumber": "7805 LB US15",
                      "orderDescription": "7805 LB US15",
                      "numCylinders": 0,
                      "deadLock": true,
                      "assets": [
                        {
                          "id": "asset-001",
                          "type": "image",
                          "title": "Product Image",
                          "mimeType": "image/png",
                          "url": "https://example.com/image.png"
                        }
                      ],
                      "weight": 5.5,
                      "price": [
                        {
                          "priceBook": "May2020",
                          "effectiveDate": "2017-07-21",
                          "effective": true,
                          "listPrice": 994,
                          "price": 994,
                          "currency": "USD"
                        }
                      ],
                      "attributes": [
                        {
                          "xref": 766078,
                          "manufacturer": "SA",
                          "type": "Function",
                          "printCode": "7805",
                          "printDescription": "Office or Entry"
                        }
                      ],
                      "preps": [
                        {
                          "id": 0,
                          "type": "Frame",
                          "code": "PREP1",
                          "description": "Standard Frame Prep"
                        }
                      ]
                    }
                  ]
                }
                """;

        wireMockServer.stubFor(get(urlPathEqualTo("/hardwareitems"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        ManufacturerId manufacturerId = new ManufacturerId("SA");
        HardwareItems result = hardwareApi.getHardwareItems(
                manufacturerId, null, null, 20L, null, null, null, null, 0L);

        assertNotNull(result);
        assertEquals(1, result.getData().size());

        HardwareItem item = result.getData().get(0);
        assertEquals(766078L, item.getXref());
        assertEquals("SA", item.getManufacturer());
        assertEquals("7800 Mortise Locks", item.getProductLine());
        assertEquals("LO", item.getType());
        assertEquals("Locksets", item.getTypeDescription());
        assertEquals("Mortise Lock", item.getSubTypeDescription());
        assertEquals("7805 LB US15", item.getPartNumber());
        assertTrue(item.getDeadLock());
        assertEquals(new BigDecimal("5.5"), item.getWeight());
        assertEquals(1, item.getPrice().size());
        assertEquals("May2020", item.getPrice().get(0).getPriceBook());

        wireMockServer.verify(getRequestedFor(urlPathEqualTo("/hardwareitems"))
                .withHeader("Authorization", equalTo("Bearer " + BEARER_TOKEN)));
    }

    @Test
    void locateHardware() throws ApiException {
        String responseBody = """
                [
                  {
                    "partNumber": "7805 LB US15",
                    "orderDescription": "7805 LB US15",
                    "price": {
                      "priceBook": "May2020",
                      "effectiveDate": "2017-07-21",
                      "effective": true,
                      "listPrice": 994,
                      "price": 994,
                      "currency": "USD"
                    },
                    "exactMatch": true,
                    "matchScore": 1.0,
                    "xref": 766078,
                    "optionXrefs": []
                  }
                ]
                """;

        wireMockServer.stubFor(get(urlPathEqualTo("/hardwareitems/locate"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        ManufacturerId manufacturerId = new ManufacturerId("SA");
        List<HardwareMatch> result = hardwareApi.locateHardware(
                manufacturerId, "7805 LB US15", 10, null, null, null, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());

        HardwareMatch match = result.get(0);
        assertEquals("7805 LB US15", match.getPartNumber());
        assertEquals("7805 LB US15", match.getOrderDescription());
        assertTrue(match.getExactMatch());
        assertEquals(1.0f, match.getMatchScore());
        assertEquals(766078L, match.getXref());
        assertNotNull(match.getOptionXrefs());
        assertTrue(match.getOptionXrefs().isEmpty());
        assertNotNull(match.getPrice());
        assertEquals("May2020", match.getPrice().getPriceBook());
        assertEquals(new BigDecimal("994"), match.getPrice().getListPrice());

        wireMockServer.verify(getRequestedFor(urlPathEqualTo("/hardwareitems/locate"))
                .withQueryParam("partNumber", equalTo("7805 LB US15"))
                .withQueryParam("resultQty", equalTo("10"))
                .withHeader("Authorization", equalTo("Bearer " + BEARER_TOKEN)));
    }

    @Test
    void getHardwareItemById() throws ApiException {
        String responseBody = """
                {
                  "xref": 766078,
                  "manufacturer": "SA",
                  "productLine": "7800 Mortise Locks",
                  "type": "LO",
                  "typeDescription": "Locksets",
                  "subTypeDescription": "Mortise Lock",
                  "partNumber": "7805 LB US15",
                  "orderDescription": "7805 LB US15",
                  "numCylinders": 0,
                  "deadLock": true,
                  "assets": [
                    {
                      "id": "asset-001",
                      "type": "image",
                      "title": "Product Image",
                      "mimeType": "image/png",
                      "url": "https://example.com/image.png"
                    }
                  ],
                  "weight": 0,
                  "price": [
                    {
                      "priceBook": "May2020",
                      "effectiveDate": "2017-07-21",
                      "effective": true,
                      "listPrice": 994,
                      "price": 994,
                      "currency": "USD"
                    }
                  ],
                  "attributes": [
                    {
                      "xref": 766078,
                      "manufacturer": "SA",
                      "type": "Function",
                      "printCode": "7805",
                      "printDescription": "Office or Entry"
                    }
                  ],
                  "preps": [
                    {
                      "id": 0,
                      "type": "Frame",
                      "code": "PREP1",
                      "description": "Standard Frame Prep"
                    }
                  ]
                }
                """;

        wireMockServer.stubFor(get(urlPathEqualTo("/hardwareitems/766078"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        HardwareItem result = hardwareApi.getHardwareItemById(766078L);

        assertNotNull(result);
        assertEquals(766078L, result.getXref());
        assertEquals("SA", result.getManufacturer());
        assertEquals("7800 Mortise Locks", result.getProductLine());
        assertEquals("LO", result.getType());
        assertEquals("7805 LB US15", result.getPartNumber());
        assertTrue(result.getDeadLock());
        assertEquals(1, result.getAssets().size());
        assertEquals("asset-001", result.getAssets().get(0).getId());
        assertEquals(1, result.getAttributes().size());
        assertEquals(1, result.getPreps().size());

        wireMockServer.verify(getRequestedFor(urlPathEqualTo("/hardwareitems/766078"))
                .withHeader("Authorization", equalTo("Bearer " + BEARER_TOKEN)));
    }

    @Test
    void getHardwareItemsOptions() throws ApiException {
        String responseBody = """
                {
                  "startRow": 0,
                  "endRow": 1,
                  "totalRows": 1,
                  "data": [
                    {
                      "xref": 766078,
                      "manufacturer": "SA",
                      "type": "Function",
                      "printCode": "7805",
                      "printDescription": "Office or Entry",
                      "uom": "EA",
                      "price": [
                        {
                          "priceBook": "May2020",
                          "effectiveDate": "2017-07-21",
                          "effective": true,
                          "listPrice": 994,
                          "price": 994,
                          "currency": "USD"
                        }
                      ],
                      "preps": [
                        {
                          "id": 0,
                          "type": "Frame",
                          "code": "PREP1",
                          "description": "Standard Frame Prep"
                        }
                      ],
                      "numCylinders": 0,
                      "ansi": "A156.13",
                      "grade": "1",
                      "electrical": true,
                      "co2e": 0,
                      "embodiedCarbon": 0,
                      "energyInUse": 0,
                      "recyclability": 0,
                      "environmentalManagementSystem": "ISO 14001",
                      "referenceServiceLife": "25 years",
                      "fire": 0,
                      "smoke": 0,
                      "stc": 0,
                      "productLines": ["7800 Mortise Locks"]
                    }
                  ]
                }
                """;

        wireMockServer.stubFor(get(urlPathEqualTo("/hardwareitems/766078/options"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        HardwareOptions result = hardwareApi.getHardwareItemsOptions(
                766078L, 20L, null, null, 0L);

        assertNotNull(result);
        assertEquals(0L, result.getStartRow());
        assertEquals(1L, result.getEndRow());
        assertEquals(1L, result.getTotalRows());
        assertEquals(1, result.getData().size());

        HardwareOption option = result.getData().get(0);
        assertEquals(766078L, option.getXref());
        assertEquals("SA", option.getManufacturer());
        assertEquals("Function", option.getType());
        assertEquals("7805", option.getPrintCode());
        assertTrue(option.getElectrical());
        assertNotNull(option.getProductLines());
        assertEquals(1, option.getProductLines().size());
        assertEquals("7800 Mortise Locks", option.getProductLines().get(0));

        wireMockServer.verify(getRequestedFor(urlPathEqualTo("/hardwareitems/766078/options"))
                .withHeader("Authorization", equalTo("Bearer " + BEARER_TOKEN)));
    }

    @Test
    void getHardwareItemsOptionById() throws ApiException {
        String responseBody = """
                {
                  "xref": 766078,
                  "manufacturer": "SA",
                  "type": "Function",
                  "printCode": "7805",
                  "printDescription": "Office or Entry",
                  "uom": "EA",
                  "price": [
                    {
                      "priceBook": "May2020",
                      "effectiveDate": "2017-07-21",
                      "effective": true,
                      "listPrice": 994,
                      "price": 994,
                      "currency": "USD"
                    }
                  ],
                  "preps": [
                    {
                      "id": 0,
                      "type": "Frame",
                      "code": "PREP1",
                      "description": "Standard Frame Prep"
                    }
                  ],
                  "numCylinders": 0,
                  "ansi": "A156.13",
                  "grade": "1",
                  "electrical": true,
                  "co2e": 0,
                  "embodiedCarbon": 0,
                  "energyInUse": 0,
                  "recyclability": 0,
                  "environmentalManagementSystem": "ISO 14001",
                  "referenceServiceLife": "25 years",
                  "fire": 0,
                  "smoke": 0,
                  "stc": 0,
                  "productLines": ["7800 Mortise Locks"]
                }
                """;

        wireMockServer.stubFor(get(urlPathEqualTo("/hardwareitems/766078/options/123456"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        HardwareOption result = hardwareApi.getHardwareItemsOptionById(123456L, 766078L);

        assertNotNull(result);
        assertEquals(766078L, result.getXref());
        assertEquals("SA", result.getManufacturer());
        assertEquals("Function", result.getType());
        assertEquals("7805", result.getPrintCode());
        assertEquals("Office or Entry", result.getPrintDescription());
        assertTrue(result.getElectrical());
        assertEquals(1, result.getPrice().size());
        assertEquals("May2020", result.getPrice().get(0).getPriceBook());

        wireMockServer.verify(getRequestedFor(urlPathEqualTo("/hardwareitems/766078/options/123456"))
                .withHeader("Authorization", equalTo("Bearer " + BEARER_TOKEN)));
    }

    @Test
    void getHardwareItemPriceWithOptions() throws ApiException {
        String responseBody = """
                {
                  "partNumber": "7805 LB US15",
                  "orderDescription": "7805 LB US15",
                  "description": "Mortise Lock, Office or Entry, LB Lever, US15 Satin Nickel",
                  "price": [
                    {
                      "priceBook": "May2020",
                      "effectiveDate": "2017-07-21",
                      "effective": true,
                      "listPrice": 994,
                      "price": 994,
                      "currency": "USD"
                    }
                  ]
                }
                """;

        wireMockServer.stubFor(get(urlPathEqualTo("/hardwareitems/766078/pricewithoptions"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        DescriptionAndPrices result = hardwareApi.getHardwareItemPriceWithOptions(
                766078L, "LH", null, null, List.of(100L, 200L), "May2020", null);

        assertNotNull(result);
        assertEquals("7805 LB US15", result.getPartNumber());
        assertEquals("7805 LB US15", result.getOrderDescription());
        assertEquals("Mortise Lock, Office or Entry, LB Lever, US15 Satin Nickel", result.getDescription());
        assertNotNull(result.getPrice());
        assertEquals(1, result.getPrice().size());
        assertEquals(new BigDecimal("994"), result.getPrice().get(0).getListPrice());
        assertEquals("USD", result.getPrice().get(0).getCurrency());

        wireMockServer.verify(getRequestedFor(urlPathEqualTo("/hardwareitems/766078/pricewithoptions"))
                .withQueryParam("hand", equalTo("LH"))
                .withQueryParam("priceBook", equalTo("May2020"))
                .withHeader("Authorization", equalTo("Bearer " + BEARER_TOKEN)));
    }

    @Test
    void getHardwareOptions() throws ApiException {
        String responseBody = """
                {
                  "startRow": 0,
                  "endRow": 1,
                  "totalRows": 1,
                  "data": [
                    {
                      "xref": 766078,
                      "manufacturer": "SA",
                      "type": "Function",
                      "printCode": "7805",
                      "printDescription": "Office or Entry",
                      "uom": "EA",
                      "price": [
                        {
                          "priceBook": "May2020",
                          "effectiveDate": "2017-07-21",
                          "effective": true,
                          "listPrice": 994,
                          "price": 994,
                          "currency": "USD"
                        }
                      ],
                      "preps": [
                        {
                          "id": 0,
                          "type": "Frame",
                          "code": "PREP1",
                          "description": "Standard Frame Prep"
                        }
                      ],
                      "numCylinders": 0,
                      "ansi": "A156.13",
                      "grade": "1",
                      "electrical": true,
                      "co2e": 0,
                      "embodiedCarbon": 0,
                      "energyInUse": 0,
                      "recyclability": 0,
                      "environmentalManagementSystem": "ISO 14001",
                      "referenceServiceLife": "25 years",
                      "fire": 0,
                      "smoke": 0,
                      "stc": 0,
                      "productLines": ["7800 Mortise Locks"]
                    }
                  ]
                }
                """;

        wireMockServer.stubFor(get(urlPathEqualTo("/hardwareoptions"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        ManufacturerId manufacturerId = new ManufacturerId("SA");
        HardwareOptions result = hardwareApi.getHardwareOptions(
                manufacturerId, 20L, null, null, null, 0L);

        assertNotNull(result);
        assertEquals(0L, result.getStartRow());
        assertEquals(1L, result.getEndRow());
        assertEquals(1L, result.getTotalRows());
        assertEquals(1, result.getData().size());

        HardwareOption option = result.getData().get(0);
        assertEquals(766078L, option.getXref());
        assertEquals("SA", option.getManufacturer());
        assertEquals("7805", option.getPrintCode());
        assertEquals("Office or Entry", option.getPrintDescription());
        assertNotNull(option.getProductLines());
        assertEquals("7800 Mortise Locks", option.getProductLines().get(0));

        wireMockServer.verify(getRequestedFor(urlPathEqualTo("/hardwareoptions"))
                .withHeader("Authorization", equalTo("Bearer " + BEARER_TOKEN)));
    }

    @Test
    void getHardwareOptionById() throws ApiException {
        String responseBody = """
                {
                  "xref": 766078,
                  "manufacturer": "SA",
                  "type": "Function",
                  "printCode": "7805",
                  "printDescription": "Office or Entry",
                  "uom": "EA",
                  "price": [
                    {
                      "priceBook": "May2020",
                      "effectiveDate": "2017-07-21",
                      "effective": true,
                      "listPrice": 994,
                      "price": 994,
                      "currency": "USD"
                    }
                  ],
                  "preps": [
                    {
                      "id": 0,
                      "type": "Frame",
                      "code": "PREP1",
                      "description": "Standard Frame Prep"
                    }
                  ],
                  "numCylinders": 0,
                  "ansi": "A156.13",
                  "grade": "1",
                  "electrical": true,
                  "co2e": 0,
                  "embodiedCarbon": 0,
                  "energyInUse": 0,
                  "recyclability": 0,
                  "environmentalManagementSystem": "ISO 14001",
                  "referenceServiceLife": "25 years",
                  "fire": 0,
                  "smoke": 0,
                  "stc": 0,
                  "productLines": ["7800 Mortise Locks"]
                }
                """;

        wireMockServer.stubFor(get(urlPathEqualTo("/hardwareoptions/766078"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        HardwareOption result = hardwareApi.getHardwareOptionById(766078L);

        assertNotNull(result);
        assertEquals(766078L, result.getXref());
        assertEquals("SA", result.getManufacturer());
        assertEquals("Function", result.getType());
        assertEquals("7805", result.getPrintCode());
        assertEquals("Office or Entry", result.getPrintDescription());
        assertTrue(result.getElectrical());
        assertEquals(1, result.getPrice().size());
        assertEquals(1, result.getPreps().size());
        assertEquals(1, result.getProductLines().size());

        wireMockServer.verify(getRequestedFor(urlPathEqualTo("/hardwareoptions/766078"))
                .withHeader("Authorization", equalTo("Bearer " + BEARER_TOKEN)));
    }

    @Test
    void getProductLines() throws ApiException {
        String responseBody = """
                [
                  {
                    "productLine": "7800 Mortise Locks",
                    "hardwareType": "LO"
                  },
                  {
                    "productLine": "8800 Rim Exit Devices",
                    "hardwareType": "ED"
                  }
                ]
                """;

        wireMockServer.stubFor(get(urlPathEqualTo("/productlines"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        ManufacturerId manufacturerId = new ManufacturerId("SA");
        List<ProductLine> result = hardwareApi.getProductLines(manufacturerId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("7800 Mortise Locks", result.get(0).getProductLine());
        assertEquals(HdwTypeEnum.LO, result.get(0).getHardwareType());
        assertEquals("8800 Rim Exit Devices", result.get(1).getProductLine());
        assertEquals(HdwTypeEnum.ED, result.get(1).getHardwareType());

        wireMockServer.verify(getRequestedFor(urlPathEqualTo("/productlines"))
                .withHeader("Authorization", equalTo("Bearer " + BEARER_TOKEN)));
    }

    @Test
    void getSubTypes() throws ApiException {
        String responseBody = """
                ["Mortise Lock", "Cylindrical Lock", "Exit Device"]
                """;

        wireMockServer.stubFor(get(urlPathEqualTo("/subtypes"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        ManufacturerId manufacturerId = new ManufacturerId("SA");
        List<String> result = hardwareApi.getSubTypes(manufacturerId, null);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Mortise Lock", result.get(0));
        assertEquals("Cylindrical Lock", result.get(1));
        assertEquals("Exit Device", result.get(2));

        wireMockServer.verify(getRequestedFor(urlPathEqualTo("/subtypes"))
                .withHeader("Authorization", equalTo("Bearer " + BEARER_TOKEN)));
    }

    @Test
    void getHardwareAttributes_unauthorized() {
        String errorBody = """
                {
                  "code": 401,
                  "message": "Unauthorized"
                }
                """;

        wireMockServer.stubFor(get(urlPathEqualTo("/hardwareattributes"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody(errorBody)));

        ManufacturerId manufacturerId = new ManufacturerId("SA");
        ApiException exception = assertThrows(ApiException.class, () ->
                hardwareApi.getHardwareAttributes(
                        manufacturerId, 20L, null, null, null, null, 0L));

        assertEquals(401, exception.getCode());
    }

    @Test
    void getHardwareItems_badRequest() {
        String errorBody = """
                {
                  "code": 400,
                  "message": "Invalid manufacturer ID"
                }
                """;

        wireMockServer.stubFor(get(urlPathEqualTo("/hardwareitems"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody(errorBody)));

        ManufacturerId manufacturerId = new ManufacturerId("INVALID");
        ApiException exception = assertThrows(ApiException.class, () ->
                hardwareApi.getHardwareItems(
                        manufacturerId, null, null, 20L, null, null, null, null, 0L));

        assertEquals(400, exception.getCode());
    }
}
