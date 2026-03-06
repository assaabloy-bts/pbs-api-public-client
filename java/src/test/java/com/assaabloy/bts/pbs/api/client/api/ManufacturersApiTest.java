package com.assaabloy.bts.pbs.api.client.api;

import com.assaabloy.bts.pbs.api.client.ApiClient;
import com.assaabloy.bts.pbs.api.client.ApiException;
import com.assaabloy.bts.pbs.api.client.model.Manufacturer;
import com.assaabloy.bts.pbs.api.client.model.ManufacturerId;
import com.assaabloy.bts.pbs.api.client.model.Manufacturers;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;

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
 * Tests for ManufacturersApi endpoints.
 */
class ManufacturersApiTest {

    private static final String BEARER_TOKEN = "test-jwt-token";

    private static WireMockServer wireMockServer;
    private static ManufacturersApi manufacturersApi;

    @BeforeAll
    static void setUp() {
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());

        ApiClient apiClient = new ApiClient();
        apiClient.updateBaseUri("http://localhost:" + wireMockServer.port());
        apiClient.setRequestInterceptor(builder ->
                builder.header("Authorization", "Bearer " + BEARER_TOKEN));
        manufacturersApi = new ManufacturersApi(apiClient);
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void getManufacturers() throws ApiException {
        String responseBody = """
                {
                  "startRow": 0,
                  "endRow": 1,
                  "totalRows": 1,
                  "data": [
                    {
                      "manufacturerId": 1,
                      "type": "Hardware Manufacturer",
                      "name": "SARGENT",
                      "address1": "100 Sargent Drive",
                      "address2": "P.O. Box 9725",
                      "address3": "",
                      "city": "New Haven",
                      "state": "CT",
                      "zip": "06536-0915",
                      "country": "US",
                      "phone": "(800) 727-5477",
                      "fax": "(888) 863-5054",
                      "email": "webmaster@sargentlock.com",
                      "webUrl": "http://www.sargentlock.com/",
                      "businessUnit": "004",
                      "abbr": "SA"
                    }
                  ]
                }
                """;

        wireMockServer.stubFor(get(urlPathEqualTo("/manufacturers"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        ManufacturerId manufacturerId = new ManufacturerId("SA");
        Manufacturers result = manufacturersApi.getManufacturers(
                null, manufacturerId, null, 20L, null, null, 0L);

        assertNotNull(result);
        assertEquals(0L, result.getStartRow());
        assertEquals(1L, result.getEndRow());
        assertEquals(1L, result.getTotalRows());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());

        Manufacturer mfr = result.getData().get(0);
        assertEquals(1L, mfr.getManufacturerId());
        assertEquals("Hardware Manufacturer", mfr.getType());
        assertEquals("SARGENT", mfr.getName());
        assertEquals("100 Sargent Drive", mfr.getAddress1());
        assertEquals("P.O. Box 9725", mfr.getAddress2());
        assertEquals("New Haven", mfr.getCity());
        assertEquals("CT", mfr.getState());
        assertEquals("06536-0915", mfr.getZip());
        assertEquals("US", mfr.getCountry());
        assertEquals("(800) 727-5477", mfr.getPhone());
        assertEquals("(888) 863-5054", mfr.getFax());
        assertEquals("webmaster@sargentlock.com", mfr.getEmail());
        assertEquals(URI.create("http://www.sargentlock.com/"), mfr.getWebUrl());
        assertEquals("004", mfr.getBusinessUnit());
        assertEquals("SA", mfr.getAbbr());

        wireMockServer.verify(getRequestedFor(urlPathEqualTo("/manufacturers"))
                .withHeader("Authorization", equalTo("Bearer " + BEARER_TOKEN)));
    }

    @Test
    void getManufacturerById() throws ApiException {
        String responseBody = """
                {
                  "manufacturerId": 1,
                  "type": "Hardware Manufacturer",
                  "name": "SARGENT",
                  "address1": "100 Sargent Drive",
                  "address2": "P.O. Box 9725",
                  "address3": "",
                  "city": "New Haven",
                  "state": "CT",
                  "zip": "06536-0915",
                  "country": "US",
                  "phone": "(800) 727-5477",
                  "fax": "(888) 863-5054",
                  "email": "webmaster@sargentlock.com",
                  "webUrl": "http://www.sargentlock.com/",
                  "businessUnit": "004",
                  "abbr": "SA"
                }
                """;

        wireMockServer.stubFor(get(urlPathEqualTo("/manufacturers/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        Manufacturer result = manufacturersApi.getManufacturerById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getManufacturerId());
        assertEquals("Hardware Manufacturer", result.getType());
        assertEquals("SARGENT", result.getName());
        assertEquals("100 Sargent Drive", result.getAddress1());
        assertEquals("P.O. Box 9725", result.getAddress2());
        assertEquals("New Haven", result.getCity());
        assertEquals("CT", result.getState());
        assertEquals("06536-0915", result.getZip());
        assertEquals("US", result.getCountry());
        assertEquals("(800) 727-5477", result.getPhone());
        assertEquals("(888) 863-5054", result.getFax());
        assertEquals("webmaster@sargentlock.com", result.getEmail());
        assertEquals(URI.create("http://www.sargentlock.com/"), result.getWebUrl());
        assertEquals("004", result.getBusinessUnit());
        assertEquals("SA", result.getAbbr());

        wireMockServer.verify(getRequestedFor(urlPathEqualTo("/manufacturers/1"))
                .withHeader("Authorization", equalTo("Bearer " + BEARER_TOKEN)));
    }

    @Test
    void getManufacturerById_unauthorized() {
        String errorBody = """
                {
                  "code": 401,
                  "message": "Unauthorized"
                }
                """;

        wireMockServer.stubFor(get(urlPathEqualTo("/manufacturers/999"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody(errorBody)));

        ApiException exception = assertThrows(ApiException.class, () ->
                manufacturersApi.getManufacturerById(999L));

        assertEquals(401, exception.getCode());
    }

    @Test
    void getManufacturers_badRequest() {
        String errorBody = """
                {
                  "code": 400,
                  "message": "Invalid query parameter"
                }
                """;

        wireMockServer.stubFor(get(urlPathEqualTo("/manufacturers"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody(errorBody)));

        ManufacturerId manufacturerId = new ManufacturerId("INVALID");
        ApiException exception = assertThrows(ApiException.class, () ->
                manufacturersApi.getManufacturers(
                        null, manufacturerId, null, 20L, null, null, 0L));

        assertEquals(400, exception.getCode());
    }
}
