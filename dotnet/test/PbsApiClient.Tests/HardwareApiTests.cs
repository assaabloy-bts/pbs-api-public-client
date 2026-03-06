using System.Net;
using AssaAbloy.Bts.PbsApiClient;
using WireMock.RequestBuilders;
using WireMock.ResponseBuilders;
using WireMock.Server;
using Xunit;

namespace PbsApiClient.Tests;

public class HardwareApiTests : IDisposable
{
    private readonly WireMockServer _server;
    private readonly AssaAbloy.Bts.PbsApiClient.PbsApiClient _client;

    public HardwareApiTests()
    {
        _server = WireMockServer.Start();
        var httpClient = new HttpClient { BaseAddress = new Uri(_server.Url!) };
        _client = new AssaAbloy.Bts.PbsApiClient.PbsApiClient(httpClient);
    }

    public void Dispose()
    {
        _server.Stop();
        _server.Dispose();
    }

    [Fact]
    public async Task GetHardwareAttributes_ReturnsAttributes()
    {
        var responseBody = """
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
                            "code": "STD",
                            "description": "Standard prep"
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
                    "environmentalManagementSystem": "ISO14001",
                    "referenceServiceLife": "25 years",
                    "fire": 0,
                    "smoke": 0,
                    "stc": 0
                }
            ]
        }
        """;

        _server.Given(
            Request.Create()
                .WithPath("/hardwareattributes")
                .WithParam("manufacturerId", "SA")
                .UsingGet()
        ).RespondWith(
            Response.Create()
                .WithStatusCode(200)
                .WithHeader("Content-Type", "application/json")
                .WithBody(responseBody)
        );

        var result = await _client.GetHardwareAttributesAsync("SA", null, null, null, null, null, null);

        Assert.NotNull(result);
        Assert.Equal(0, result.StartRow);
        Assert.Equal(1, result.EndRow);
        Assert.Equal(1, result.TotalRows);
        Assert.NotNull(result.Data);
        Assert.Single(result.Data);

        var attribute = result.Data[0];
        Assert.Equal(766078, attribute.Xref);
        Assert.Equal("SA", attribute.Manufacturer);
        Assert.Equal("Function", attribute.Type);
        Assert.Equal("7805", attribute.PrintCode);
        Assert.Equal("Office or Entry", attribute.PrintDescription);
        Assert.Equal(994, attribute.Price!.ListPrice);
    }

    [Fact]
    public async Task GetHardwareItems_ReturnsItems()
    {
        var responseBody = """
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
                            "id": "asset-1",
                            "type": "image",
                            "title": "Front View",
                            "mimeType": "image/png",
                            "url": "https://example.com/image.png"
                        }
                    ],
                    "weight": 5.2,
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
                    "attributes": [],
                    "preps": []
                }
            ]
        }
        """;

        _server.Given(
            Request.Create()
                .WithPath("/hardwareitems")
                .WithParam("manufacturerId", "SA")
                .UsingGet()
        ).RespondWith(
            Response.Create()
                .WithStatusCode(200)
                .WithHeader("Content-Type", "application/json")
                .WithBody(responseBody)
        );

        var result = await _client.GetHardwareItemsAsync(null, "SA", null, null, null, null, null, null, null);

        Assert.NotNull(result);
        Assert.Equal(1, result.TotalRows);
        Assert.NotNull(result.Data);
        Assert.Single(result.Data);

        var item = result.Data[0];
        Assert.Equal(766078, item.Xref);
        Assert.Equal("SA", item.Manufacturer);
        Assert.Equal("7800 Mortise Locks", item.ProductLine);
        Assert.Equal("LO", item.Type);
        Assert.Equal("Locksets", item.TypeDescription);
        Assert.Equal("Mortise Lock", item.SubTypeDescription);
        Assert.Equal("7805 LB US15", item.PartNumber);
        Assert.True(item.DeadLock);
        Assert.NotNull(item.Assets);
        Assert.Single(item.Assets);
    }

    [Fact]
    public async Task LocateHardware_ReturnsMatches()
    {
        var responseBody = """
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

        _server.Given(
            Request.Create()
                .WithPath("/hardwareitems/locate")
                .WithParam("manufacturerId", "SA")
                .WithParam("partNumber", "7805 LB US15")
                .WithParam("resultQty", "10")
                .UsingGet()
        ).RespondWith(
            Response.Create()
                .WithStatusCode(200)
                .WithHeader("Content-Type", "application/json")
                .WithBody(responseBody)
        );

        var result = await _client.LocateHardwareAsync(null, null, null, "SA", "7805 LB US15", null, 10, null);

        Assert.NotNull(result);
        Assert.Single(result);

        var match = result[0];
        Assert.Equal("7805 LB US15", match.PartNumber);
        Assert.Equal("7805 LB US15", match.OrderDescription);
        Assert.True(match.ExactMatch);
        Assert.Equal(1.0f, match.MatchScore);
        Assert.Equal(766078, match.Xref);
        Assert.NotNull(match.Price);
        Assert.Equal(994, match.Price.ListPrice);
    }

    [Fact]
    public async Task GetHardwareItemById_ReturnsItem()
    {
        var responseBody = """
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
            "assets": [],
            "weight": 5.2,
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
            "attributes": [],
            "preps": []
        }
        """;

        _server.Given(
            Request.Create()
                .WithPath("/hardwareitems/766078")
                .UsingGet()
        ).RespondWith(
            Response.Create()
                .WithStatusCode(200)
                .WithHeader("Content-Type", "application/json")
                .WithBody(responseBody)
        );

        var result = await _client.GetHardwareItemByIdAsync(766078);

        Assert.NotNull(result);
        Assert.Equal(766078, result.Xref);
        Assert.Equal("SA", result.Manufacturer);
        Assert.Equal("7800 Mortise Locks", result.ProductLine);
        Assert.Equal("7805 LB US15", result.PartNumber);
        Assert.True(result.DeadLock);
        Assert.NotNull(result.Price);
        Assert.Single(result.Price);
        Assert.Equal(994, result.Price[0].ListPrice);
    }

    [Fact]
    public async Task GetHardwareItemsOptions_ReturnsOptions()
    {
        var responseBody = """
        {
            "startRow": 0,
            "endRow": 1,
            "totalRows": 1,
            "data": [
                {
                    "xref": 890123,
                    "manufacturer": "SA",
                    "type": "Finish",
                    "printCode": "US15",
                    "printDescription": "Satin Nickel",
                    "uom": "EA",
                    "price": [
                        {
                            "priceBook": "May2020",
                            "effectiveDate": "2017-07-21",
                            "effective": true,
                            "listPrice": 0,
                            "price": 0,
                            "currency": "USD"
                        }
                    ],
                    "preps": [],
                    "numCylinders": 0,
                    "ansi": "",
                    "grade": "",
                    "electrical": false,
                    "co2e": 0,
                    "embodiedCarbon": 0,
                    "energyInUse": 0,
                    "recyclability": 0,
                    "environmentalManagementSystem": "",
                    "referenceServiceLife": "",
                    "fire": 0,
                    "smoke": 0,
                    "stc": 0,
                    "productLines": ["7800 Mortise Locks"]
                }
            ]
        }
        """;

        _server.Given(
            Request.Create()
                .WithPath("/hardwareitems/766078/options")
                .UsingGet()
        ).RespondWith(
            Response.Create()
                .WithStatusCode(200)
                .WithHeader("Content-Type", "application/json")
                .WithBody(responseBody)
        );

        var result = await _client.GetHardwareItemsOptionsAsync(766078, null, null, null, null);

        Assert.NotNull(result);
        Assert.Equal(1, result.TotalRows);
        Assert.NotNull(result.Data);
        Assert.Single(result.Data);

        var option = result.Data[0];
        Assert.Equal(890123, option.Xref);
        Assert.Equal("SA", option.Manufacturer);
        Assert.Equal("Finish", option.Type);
        Assert.Equal("US15", option.PrintCode);
        Assert.Equal("Satin Nickel", option.PrintDescription);
        Assert.NotNull(option.ProductLines);
        Assert.Single(option.ProductLines);
        Assert.Equal("7800 Mortise Locks", option.ProductLines[0]);
    }

    [Fact]
    public async Task GetHardwareItemsOptionById_ReturnsSingleOption()
    {
        var responseBody = """
        {
            "xref": 890123,
            "manufacturer": "SA",
            "type": "Finish",
            "printCode": "US15",
            "printDescription": "Satin Nickel",
            "uom": "EA",
            "price": [
                {
                    "priceBook": "May2020",
                    "effectiveDate": "2017-07-21",
                    "effective": true,
                    "listPrice": 0,
                    "price": 0,
                    "currency": "USD"
                }
            ],
            "preps": [],
            "numCylinders": 0,
            "ansi": "",
            "grade": "",
            "electrical": false,
            "co2e": 0,
            "embodiedCarbon": 0,
            "energyInUse": 0,
            "recyclability": 0,
            "environmentalManagementSystem": "",
            "referenceServiceLife": "",
            "fire": 0,
            "smoke": 0,
            "stc": 0,
            "productLines": ["7800 Mortise Locks"]
        }
        """;

        _server.Given(
            Request.Create()
                .WithPath("/hardwareitems/766078/options/890123")
                .UsingGet()
        ).RespondWith(
            Response.Create()
                .WithStatusCode(200)
                .WithHeader("Content-Type", "application/json")
                .WithBody(responseBody)
        );

        var result = await _client.GetHardwareItemsOptionByIdAsync(890123, 766078);

        Assert.NotNull(result);
        Assert.Equal(890123, result.Xref);
        Assert.Equal("SA", result.Manufacturer);
        Assert.Equal("Finish", result.Type);
        Assert.Equal("US15", result.PrintCode);
        Assert.Equal("Satin Nickel", result.PrintDescription);
    }

    [Fact]
    public async Task GetHardwareItemPriceWithOptions_ReturnsPriceInfo()
    {
        var responseBody = """
        {
            "partNumber": "7805 LB US15",
            "orderDescription": "7805 LB US15",
            "description": "7805 LB US15 - Office or Entry, Satin Nickel",
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

        _server.Given(
            Request.Create()
                .WithPath("/hardwareitems/766078/pricewithoptions")
                .UsingGet()
        ).RespondWith(
            Response.Create()
                .WithStatusCode(200)
                .WithHeader("Content-Type", "application/json")
                .WithBody(responseBody)
        );

        var result = await _client.GetHardwareItemPriceWithOptionsAsync(766078, null, null, null, null, null, null);

        Assert.NotNull(result);
        Assert.Equal("7805 LB US15", result.PartNumber);
        Assert.Equal("7805 LB US15", result.OrderDescription);
        Assert.NotNull(result.Price);
        Assert.Single(result.Price);
        Assert.Equal(994, result.Price[0].ListPrice);
        Assert.Equal("USD", result.Price[0].Currency);
    }

    [Fact]
    public async Task GetHardwareOptions_ReturnsOptions()
    {
        var responseBody = """
        {
            "startRow": 0,
            "endRow": 1,
            "totalRows": 1,
            "data": [
                {
                    "xref": 890123,
                    "manufacturer": "SA",
                    "type": "Finish",
                    "printCode": "US15",
                    "printDescription": "Satin Nickel",
                    "uom": "EA",
                    "price": [
                        {
                            "priceBook": "May2020",
                            "effectiveDate": "2017-07-21",
                            "effective": true,
                            "listPrice": 50,
                            "price": 50,
                            "currency": "USD"
                        }
                    ],
                    "preps": [],
                    "numCylinders": 0,
                    "ansi": "",
                    "grade": "",
                    "electrical": false,
                    "co2e": 0,
                    "embodiedCarbon": 0,
                    "energyInUse": 0,
                    "recyclability": 0,
                    "environmentalManagementSystem": "",
                    "referenceServiceLife": "",
                    "fire": 0,
                    "smoke": 0,
                    "stc": 0,
                    "productLines": ["7800 Mortise Locks"]
                }
            ]
        }
        """;

        _server.Given(
            Request.Create()
                .WithPath("/hardwareoptions")
                .WithParam("manufacturerId", "SA")
                .UsingGet()
        ).RespondWith(
            Response.Create()
                .WithStatusCode(200)
                .WithHeader("Content-Type", "application/json")
                .WithBody(responseBody)
        );

        var result = await _client.GetHardwareOptionsAsync("SA", null, null, null, null, null);

        Assert.NotNull(result);
        Assert.Equal(1, result.TotalRows);
        Assert.NotNull(result.Data);
        Assert.Single(result.Data);

        var option = result.Data[0];
        Assert.Equal(890123, option.Xref);
        Assert.Equal("SA", option.Manufacturer);
        Assert.Equal("Finish", option.Type);
        Assert.Equal("US15", option.PrintCode);
        Assert.Equal(50, option.Price![0].ListPrice);
    }

    [Fact]
    public async Task GetHardwareOptionById_ReturnsSingleOption()
    {
        var responseBody = """
        {
            "xref": 890123,
            "manufacturer": "SA",
            "type": "Finish",
            "printCode": "US15",
            "printDescription": "Satin Nickel",
            "uom": "EA",
            "price": [
                {
                    "priceBook": "May2020",
                    "effectiveDate": "2017-07-21",
                    "effective": true,
                    "listPrice": 50,
                    "price": 50,
                    "currency": "USD"
                }
            ],
            "preps": [],
            "numCylinders": 0,
            "ansi": "",
            "grade": "",
            "electrical": false,
            "co2e": 0,
            "embodiedCarbon": 0,
            "energyInUse": 0,
            "recyclability": 0,
            "environmentalManagementSystem": "",
            "referenceServiceLife": "",
            "fire": 0,
            "smoke": 0,
            "stc": 0,
            "productLines": ["7800 Mortise Locks"]
        }
        """;

        _server.Given(
            Request.Create()
                .WithPath("/hardwareoptions/890123")
                .UsingGet()
        ).RespondWith(
            Response.Create()
                .WithStatusCode(200)
                .WithHeader("Content-Type", "application/json")
                .WithBody(responseBody)
        );

        var result = await _client.GetHardwareOptionByIdAsync(890123);

        Assert.NotNull(result);
        Assert.Equal(890123, result.Xref);
        Assert.Equal("SA", result.Manufacturer);
        Assert.Equal("Finish", result.Type);
        Assert.Equal("US15", result.PrintCode);
        Assert.Equal("Satin Nickel", result.PrintDescription);
        Assert.NotNull(result.Price);
        Assert.Single(result.Price);
        Assert.Equal(50, result.Price[0].Price);
    }

    [Fact]
    public async Task GetHardwareAttributes_Unauthorized_ThrowsException()
    {
        var responseBody = """
        {
            "code": 401,
            "message": "Unauthorized"
        }
        """;

        _server.Given(
            Request.Create()
                .WithPath("/hardwareattributes")
                .UsingGet()
        ).RespondWith(
            Response.Create()
                .WithStatusCode(401)
                .WithHeader("Content-Type", "application/json")
                .WithBody(responseBody)
        );

        await Assert.ThrowsAsync<ApiException>(async () =>
            await _client.GetHardwareAttributesAsync("SA", null, null, null, null, null, null));
    }

    [Fact]
    public async Task GetHardwareItems_BadRequest_ThrowsException()
    {
        var responseBody = """
        {
            "code": 400,
            "message": "Bad request: manufacturerId is required"
        }
        """;

        _server.Given(
            Request.Create()
                .WithPath("/hardwareitems")
                .UsingGet()
        ).RespondWith(
            Response.Create()
                .WithStatusCode(400)
                .WithHeader("Content-Type", "application/json")
                .WithBody(responseBody)
        );

        await Assert.ThrowsAsync<ApiException>(async () =>
            await _client.GetHardwareItemsAsync(null, "INVALID", null, null, null, null, null, null, null));
    }
}
