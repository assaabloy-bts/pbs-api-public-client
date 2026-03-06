using System.Net;
using AssaAbloy.Bts.PbsApiClient;
using WireMock.RequestBuilders;
using WireMock.ResponseBuilders;
using WireMock.Server;
using Xunit;

namespace PbsApiClient.Tests;

public class ManufacturersApiTests : IDisposable
{
    private readonly WireMockServer _server;
    private readonly AssaAbloy.Bts.PbsApiClient.PbsApiClient _client;

    public ManufacturersApiTests()
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
    public async Task GetManufacturers_ReturnsManufacturersList()
    {
        var responseBody = """
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

        _server.Given(
            Request.Create()
                .WithPath("/manufacturers")
                .UsingGet()
        ).RespondWith(
            Response.Create()
                .WithStatusCode(200)
                .WithHeader("Content-Type", "application/json")
                .WithBody(responseBody)
        );

        var result = await _client.GetManufacturersAsync(null, null, null, null, null, null, null);

        Assert.NotNull(result);
        Assert.Equal(0, result.StartRow);
        Assert.Equal(1, result.EndRow);
        Assert.Equal(1, result.TotalRows);
        Assert.NotNull(result.Data);
        Assert.Single(result.Data);

        var manufacturer = result.Data[0];
        Assert.Equal(1, manufacturer.ManufacturerId);
        Assert.Equal("Hardware Manufacturer", manufacturer.Type);
        Assert.Equal("SARGENT", manufacturer.Name);
        Assert.Equal("100 Sargent Drive", manufacturer.Address1);
        Assert.Equal("P.O. Box 9725", manufacturer.Address2);
        Assert.Equal("New Haven", manufacturer.City);
        Assert.Equal("CT", manufacturer.State);
        Assert.Equal("06536-0915", manufacturer.Zip);
        Assert.Equal("US", manufacturer.Country);
        Assert.Equal("(800) 727-5477", manufacturer.Phone);
        Assert.Equal("(888) 863-5054", manufacturer.Fax);
        Assert.Equal("webmaster@sargentlock.com", manufacturer.Email);
        Assert.Equal("http://www.sargentlock.com/", manufacturer.WebUrl);
        Assert.Equal("004", manufacturer.BusinessUnit);
        Assert.Equal("SA", manufacturer.Abbr);
    }

    [Fact]
    public async Task GetManufacturerById_ReturnsSingleManufacturer()
    {
        var responseBody = """
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

        _server.Given(
            Request.Create()
                .WithPath("/manufacturers/1")
                .UsingGet()
        ).RespondWith(
            Response.Create()
                .WithStatusCode(200)
                .WithHeader("Content-Type", "application/json")
                .WithBody(responseBody)
        );

        var result = await _client.GetManufacturerByIdAsync(1);

        Assert.NotNull(result);
        Assert.Equal(1, result.ManufacturerId);
        Assert.Equal("Hardware Manufacturer", result.Type);
        Assert.Equal("SARGENT", result.Name);
        Assert.Equal("100 Sargent Drive", result.Address1);
        Assert.Equal("New Haven", result.City);
        Assert.Equal("CT", result.State);
        Assert.Equal("SA", result.Abbr);
    }

    [Fact]
    public async Task GetManufacturers_Unauthorized_ThrowsException()
    {
        var responseBody = """
        {
            "code": 401,
            "message": "Unauthorized - Invalid or expired JWT token"
        }
        """;

        _server.Given(
            Request.Create()
                .WithPath("/manufacturers")
                .UsingGet()
        ).RespondWith(
            Response.Create()
                .WithStatusCode(401)
                .WithHeader("Content-Type", "application/json")
                .WithBody(responseBody)
        );

        await Assert.ThrowsAsync<ApiException>(async () =>
            await _client.GetManufacturersAsync(null, null, null, null, null, null, null));
    }

    [Fact]
    public async Task GetManufacturerById_BadRequest_ThrowsException()
    {
        var responseBody = """
        {
            "code": 400,
            "message": "Bad request: invalid manufacturer ID format"
        }
        """;

        _server.Given(
            Request.Create()
                .WithPath("/manufacturers/999999")
                .UsingGet()
        ).RespondWith(
            Response.Create()
                .WithStatusCode(400)
                .WithHeader("Content-Type", "application/json")
                .WithBody(responseBody)
        );

        await Assert.ThrowsAsync<ApiException>(async () =>
            await _client.GetManufacturerByIdAsync(999999));
    }
}
