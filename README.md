# PBS Public API Client

Official API client libraries for the [ASSA ABLOY PBS Public API](https://public.api.aa-bts.com). Available for Java and .NET.

## Java

### Installation

Maven:
```xml
<dependency>
    <groupId>com.assaabloy.bts</groupId>
    <artifactId>pbs-api-public-client</artifactId>
    <version>1.0.0</version>
</dependency>
```

Gradle:
```groovy
implementation 'com.assaabloy.bts:pbs-api-public-client:1.0.0'
```

### Usage

```java
import com.assaabloy.bts.pbs.api.client.ApiClient;
import com.assaabloy.bts.pbs.api.client.api.HardwareApi;
import com.assaabloy.bts.pbs.api.client.api.ManufacturersApi;
import com.assaabloy.bts.pbs.api.client.api.PbsApi;
import com.assaabloy.bts.pbs.api.client.model.*;

ApiClient client = new ApiClient();
client.setRequestInterceptor(builder ->
    builder.header("Authorization", "Bearer " + token));

ManufacturersApi manufacturersApi = new ManufacturersApi(client);
Manufacturers manufacturers = manufacturersApi.getManufacturers(
    null, null, null, null, null, null, null);

HardwareApi hardwareApi = new HardwareApi(client);
HardwareItems items = hardwareApi.getHardwareItems(
    "SA", null, null, null, null, null, null, null, null);
```

### Building

```bash
cd java
mvn clean verify
```

## .NET

### Installation

```bash
dotnet add package AssaAbloy.Bts.PbsApiClient
```

### Usage

```csharp
using AssaAbloy.Bts.PbsApiClient;

var httpClient = new HttpClient();
httpClient.BaseAddress = new Uri("https://public.api.aa-bts.com");
httpClient.DefaultRequestHeaders.Authorization =
    new System.Net.Http.Headers.AuthenticationHeaderValue("Bearer", token);

var client = new PbsApiClient(httpClient);

var manufacturers = await client.GetManufacturersAsync();
var items = await client.GetHardwareItemsAsync(manufacturerId: "SA");
```

### Building

```bash
cd dotnet
dotnet build
dotnet test
```

## Branching Strategy

This project follows gitflow:

- `master` - production releases
- `develop` - integration branch
- `feature/*` - new features
- `release/*` - release preparation
- `hotfix/*` - production fixes

## Releases

Releases are automated via GitHub Actions. Only tagged commits publish to Maven Central and NuGet:

```bash
git tag v1.0.0
git push origin v1.0.0
```

### Required GitHub Secrets

| Secret | Description |
|--------|-------------|
| `MAVEN_CENTRAL_USERNAME` | Maven Central (Sonatype) username |
| `MAVEN_CENTRAL_PASSWORD` | Maven Central (Sonatype) password |
| `MAVEN_GPG_PRIVATE_KEY` | GPG private key for signing JARs |
| `MAVEN_GPG_PASSPHRASE` | GPG key passphrase |
| `NUGET_API_KEY` | NuGet.org API key |

## API Documentation

See the [OpenAPI specification](openapi.yaml) for full API documentation.

---
(c)ASSA ABLOY
