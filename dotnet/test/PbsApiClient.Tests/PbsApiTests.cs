using System.Net;
using AssaAbloy.Bts.PbsApiClient;
using WireMock.RequestBuilders;
using WireMock.ResponseBuilders;
using WireMock.Server;
using Xunit;

namespace PbsApiClient.Tests;

public class PbsApiTests : IDisposable
{
    private readonly WireMockServer _server;
    private readonly AssaAbloy.Bts.PbsApiClient.PbsApiClient _client;

    public PbsApiTests()
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
    public async Task GetPriceBookComparisons_ReturnsSuccessfully()
    {
        _server.Given(
            Request.Create()
                .WithPath("/priceBookComparisonTool")
                .WithParam("manufacturerId", "SA")
                .UsingGet()
        ).RespondWith(
            Response.Create()
                .WithStatusCode(200)
                .WithHeader("Content-Type", "application/json")
                .WithBody("[]")
        );

        // The getPriceBookComparisons endpoint returns empty content in the spec (content: {}),
        // so NSwag may generate a void return or a generic response. We test that it does not throw.
        await _client.GetPriceBookComparisonsAsync(null, "SA");
    }

    [Fact]
    public async Task GetPriceBooks_ReturnsPriceBooks()
    {
        var responseBody = """
        [
            {
                "manufacturer": "SARGENT",
                "description": "May2020",
                "effectiveDate": "2020-05-01"
            },
            {
                "manufacturer": "SARGENT",
                "description": "Jan2021",
                "effectiveDate": "2021-01-15"
            }
        ]
        """;

        _server.Given(
            Request.Create()
                .WithPath("/pricebooks")
                .WithParam("priceBooksAfterDate", "2020-01-01")
                .UsingGet()
        ).RespondWith(
            Response.Create()
                .WithStatusCode(200)
                .WithHeader("Content-Type", "application/json")
                .WithBody(responseBody)
        );

        var result = await _client.GetPriceBooksAsync(null, new DateTimeOffset(2020, 1, 1, 0, 0, 0, TimeSpan.Zero));

        Assert.NotNull(result);
        Assert.Equal(2, result.Count);
        Assert.Equal("SARGENT", result[0].Manufacturer);
        Assert.Equal("May2020", result[0].Description);
        Assert.Equal("Jan2021", result[1].Description);
    }

    [Fact]
    public async Task ListPriceBooks_ReturnsPriceBooks()
    {
        var responseBody = """
        [
            {
                "manufacturer": "SARGENT",
                "description": "May2020",
                "effectiveDate": "2020-05-01"
            },
            {
                "manufacturer": "SARGENT",
                "description": "Jan2021",
                "effectiveDate": "2021-01-15"
            },
            {
                "manufacturer": "SARGENT",
                "description": "Jul2021",
                "effectiveDate": "2021-07-01"
            }
        ]
        """;

        _server.Given(
            Request.Create()
                .WithPath("/pricebooks/list")
                .WithParam("manufacturerId", "SA")
                .UsingGet()
        ).RespondWith(
            Response.Create()
                .WithStatusCode(200)
                .WithHeader("Content-Type", "application/json")
                .WithBody(responseBody)
        );

        var result = await _client.ListPriceBooksAsync("SA");

        Assert.NotNull(result);
        Assert.Equal(3, result.Count);
        Assert.Equal("SARGENT", result[0].Manufacturer);
        Assert.Equal("May2020", result[0].Description);
        Assert.Equal("Jan2021", result[1].Description);
        Assert.Equal("Jul2021", result[2].Description);
    }

    [Fact]
    public async Task GetProductLines_ReturnsProductLines()
    {
        var responseBody = """
        [
            {
                "productLine": "7800 Mortise Locks",
                "hardwareType": "LO"
            },
            {
                "productLine": "8200 Cylindrical Locks",
                "hardwareType": "LO"
            },
            {
                "productLine": "80 Series Exit Devices",
                "hardwareType": "ED"
            }
        ]
        """;

        _server.Given(
            Request.Create()
                .WithPath("/productlines")
                .WithParam("manufacturerId", "SA")
                .UsingGet()
        ).RespondWith(
            Response.Create()
                .WithStatusCode(200)
                .WithHeader("Content-Type", "application/json")
                .WithBody(responseBody)
        );

        var result = await _client.GetProductLinesAsync("SA");

        Assert.NotNull(result);
        Assert.Equal(3, result.Count);
        Assert.Equal("7800 Mortise Locks", result[0].ProductLine1);
        Assert.Equal("8200 Cylindrical Locks", result[1].ProductLine1);
        Assert.Equal("80 Series Exit Devices", result[2].ProductLine1);
    }

    [Fact]
    public async Task GetSubTypes_ReturnsSubTypes()
    {
        var responseBody = """
        [
            "Mortise Lock",
            "Cylindrical Lock",
            "Exit Device",
            "Door Closer"
        ]
        """;

        _server.Given(
            Request.Create()
                .WithPath("/subtypes")
                .WithParam("manufacturerId", "SA")
                .UsingGet()
        ).RespondWith(
            Response.Create()
                .WithStatusCode(200)
                .WithHeader("Content-Type", "application/json")
                .WithBody(responseBody)
        );

        var result = await _client.GetSubTypesAsync("SA", null);

        Assert.NotNull(result);
        Assert.Equal(4, result.Count);
        Assert.Equal("Mortise Lock", result[0]);
        Assert.Equal("Cylindrical Lock", result[1]);
        Assert.Equal("Exit Device", result[2]);
        Assert.Equal("Door Closer", result[3]);
    }

    [Fact]
    public async Task GetPriceBooks_Unauthorized_ThrowsException()
    {
        var responseBody = """
        {
            "code": 401,
            "message": "Unauthorized - Invalid or expired JWT token"
        }
        """;

        _server.Given(
            Request.Create()
                .WithPath("/pricebooks")
                .UsingGet()
        ).RespondWith(
            Response.Create()
                .WithStatusCode(401)
                .WithHeader("Content-Type", "application/json")
                .WithBody(responseBody)
        );

        await Assert.ThrowsAsync<ApiException>(async () =>
            await _client.GetPriceBooksAsync(null, new DateTimeOffset(2020, 1, 1, 0, 0, 0, TimeSpan.Zero)));
    }

    [Fact]
    public async Task GetSubTypes_BadRequest_ThrowsException()
    {
        var responseBody = """
        {
            "code": 400,
            "message": "Bad request: manufacturerId is required"
        }
        """;

        _server.Given(
            Request.Create()
                .WithPath("/subtypes")
                .UsingGet()
        ).RespondWith(
            Response.Create()
                .WithStatusCode(400)
                .WithHeader("Content-Type", "application/json")
                .WithBody(responseBody)
        );

        await Assert.ThrowsAsync<ApiException>(async () =>
            await _client.GetSubTypesAsync("INVALID", null));
    }
}
