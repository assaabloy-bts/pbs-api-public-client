package com.assaabloy.bts.pbs.api.client.api;

import com.assaabloy.bts.pbs.api.client.ApiClient;
import com.assaabloy.bts.pbs.api.client.ApiException;
import com.assaabloy.bts.pbs.api.client.model.ManufacturerId;
import com.assaabloy.bts.pbs.api.client.model.PriceBook;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
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

/**
 * Tests for PbsApi endpoints.
 */
class PbsApiTest {

    private static final String BEARER_TOKEN = "test-jwt-token";

    private static WireMockServer wireMockServer;
    private static PbsApi pbsApi;
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
        pbsApi = new PbsApi(apiClient);
        hardwareApi = new HardwareApi(apiClient);
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void getPriceBookComparisons() throws ApiException {
        wireMockServer.stubFor(get(urlPathEqualTo("/priceBookComparisonTool"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("")));

        ManufacturerId manufacturerId = new ManufacturerId("SA");
        // getPriceBookComparisons returns void since the spec defines empty content
        pbsApi.getPriceBookComparisons(manufacturerId, false);

        wireMockServer.verify(getRequestedFor(urlPathEqualTo("/priceBookComparisonTool"))
                .withQueryParam("futurePriceBook", equalTo("false"))
                .withHeader("Authorization", equalTo("Bearer " + BEARER_TOKEN)));
    }

    @Test
    void getPriceBooks() throws ApiException {
        String responseBody = """
                [
                  {
                    "manufacturer": "SARGENT",
                    "description": "May2020",
                    "effectiveDate": "2020-05-01"
                  },
                  {
                    "manufacturer": "SARGENT",
                    "description": "Jan2021",
                    "effectiveDate": "2021-01-01"
                  }
                ]
                """;

        wireMockServer.stubFor(get(urlPathEqualTo("/pricebooks"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        ManufacturerId manufacturerId = new ManufacturerId("SA");
        List<PriceBook> result = pbsApi.getPriceBooks(
                LocalDate.of(2020, 1, 1), manufacturerId);

        assertNotNull(result);
        assertEquals(2, result.size());

        PriceBook priceBook1 = result.get(0);
        assertEquals("SARGENT", priceBook1.getManufacturer());
        assertEquals("May2020", priceBook1.getDescription());
        assertEquals(LocalDate.of(2020, 5, 1), priceBook1.getEffectiveDate());

        PriceBook priceBook2 = result.get(1);
        assertEquals("SARGENT", priceBook2.getManufacturer());
        assertEquals("Jan2021", priceBook2.getDescription());
        assertEquals(LocalDate.of(2021, 1, 1), priceBook2.getEffectiveDate());

        wireMockServer.verify(getRequestedFor(urlPathEqualTo("/pricebooks"))
                .withQueryParam("priceBooksAfterDate", equalTo("2020-01-01"))
                .withHeader("Authorization", equalTo("Bearer " + BEARER_TOKEN)));
    }

    @Test
    void listPriceBooks() throws ApiException {
        String responseBody = """
                [
                  {
                    "manufacturer": "SARGENT",
                    "description": "May2020",
                    "effectiveDate": "2020-05-01"
                  },
                  {
                    "manufacturer": "SARGENT",
                    "description": "Jan2021",
                    "effectiveDate": "2021-01-01"
                  },
                  {
                    "manufacturer": "SARGENT",
                    "description": "Jul2021",
                    "effectiveDate": "2021-07-01"
                  }
                ]
                """;

        wireMockServer.stubFor(get(urlPathEqualTo("/pricebooks/list"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        ManufacturerId manufacturerId = new ManufacturerId("SA");
        List<PriceBook> result = pbsApi.listPriceBooks(manufacturerId);

        assertNotNull(result);
        assertEquals(3, result.size());

        assertEquals("SARGENT", result.get(0).getManufacturer());
        assertEquals("May2020", result.get(0).getDescription());
        assertEquals(LocalDate.of(2020, 5, 1), result.get(0).getEffectiveDate());

        assertEquals("Jul2021", result.get(2).getDescription());
        assertEquals(LocalDate.of(2021, 7, 1), result.get(2).getEffectiveDate());

        wireMockServer.verify(getRequestedFor(urlPathEqualTo("/pricebooks/list"))
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
        List<com.assaabloy.bts.pbs.api.client.model.ProductLine> result =
                hardwareApi.getProductLines(manufacturerId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("7800 Mortise Locks", result.get(0).getProductLine());
        assertEquals("8800 Rim Exit Devices", result.get(1).getProductLine());

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
        List<String> result = hardwareApi.getSubTypes(manufacturerId, "7800 Mortise Locks");

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Mortise Lock", result.get(0));
        assertEquals("Cylindrical Lock", result.get(1));
        assertEquals("Exit Device", result.get(2));

        wireMockServer.verify(getRequestedFor(urlPathEqualTo("/subtypes"))
                .withQueryParam("productLine", equalTo("7800 Mortise Locks"))
                .withHeader("Authorization", equalTo("Bearer " + BEARER_TOKEN)));
    }

    @Test
    void getPriceBooks_unauthorized() {
        String errorBody = """
                {
                  "code": 401,
                  "message": "Unauthorized"
                }
                """;

        wireMockServer.stubFor(get(urlPathEqualTo("/pricebooks"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody(errorBody)));

        ManufacturerId manufacturerId = new ManufacturerId("SA");
        ApiException exception = assertThrows(ApiException.class, () ->
                pbsApi.getPriceBooks(LocalDate.of(2020, 1, 1), manufacturerId));

        assertEquals(401, exception.getCode());
    }

    @Test
    void listPriceBooks_badRequest() {
        String errorBody = """
                {
                  "code": 400,
                  "message": "Invalid manufacturer ID"
                }
                """;

        wireMockServer.stubFor(get(urlPathEqualTo("/pricebooks/list"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody(errorBody)));

        ManufacturerId manufacturerId = new ManufacturerId("INVALID");
        ApiException exception = assertThrows(ApiException.class, () ->
                pbsApi.listPriceBooks(manufacturerId));

        assertEquals(400, exception.getCode());
    }
}
