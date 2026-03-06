# PBS Public API Client

Official API client libraries for the [ASSA ABLOY PBS Public API](https://public.api.aa-bts.com). Available for Java and .NET.

## Client Libraries

| Language | Package | Documentation |
|----------|---------|---------------|
| Java | `com.assaabloy.bts:pbs-api-public-client` | [Java README](java/README.md) |
| .NET | `AssaAbloy.Bts.PbsApiClient` | [.NET README](dotnet/README.md) |

## API Documentation

See the [OpenAPI specification](openapi.yaml) for full API documentation.

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

---
&#169;ASSA ABLOY
