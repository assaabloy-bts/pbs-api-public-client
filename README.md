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
import com.assaabloy.bts.pbs.api.client.PbsClient;
import com.assaabloy.bts.pbs.api.client.model.*;

// Default production URL
PbsClient client = new PbsClient("your-api-key");

// Or with a custom URL
PbsClient client = new PbsClient("https://custom-url.example.com", "your-api-key");

Manufacturers manufacturers = client.getManufacturers("SA", null, null, null, null, null, null);
HardwareItems items = client.getHardwareItems("SA", null, null, null, null, null, null, null, null);
```

### Building and Testing

```bash
cd java
PBS_API_KEY=your-api-key mvn clean verify
```

## .NET

### Installation

```bash
dotnet add package AssaAbloy.Bts.PbsApiClient
```

### Usage

```csharp
using AssaAbloy.Bts.PbsApiClient;

// Default production URL
var client = new PbsClient("your-api-key");

// Or with a custom URL
var client = new PbsClient("https://custom-url.example.com", "your-api-key");

var manufacturers = await client.GetManufacturersAsync(manufacturerId: "SA");
var items = await client.GetHardwareItemsAsync("SA");
```

### Building and Testing

```bash
cd dotnet
dotnet build
PBS_API_KEY=your-api-key dotnet test
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
| `PBS_API_KEY` | API key for running integration tests |
| `MAVEN_CENTRAL_USERNAME` | Maven Central (Sonatype) username |
| `MAVEN_CENTRAL_PASSWORD` | Maven Central (Sonatype) password |
| `MAVEN_GPG_PRIVATE_KEY` | GPG private key for signing JARs |
| `MAVEN_GPG_PASSPHRASE` | GPG key passphrase |
| `NUGET_API_KEY` | NuGet.org API key |

## API Documentation

See the [OpenAPI specification](openapi.yaml) for full API documentation.

---
(c)ASSA ABLOY
