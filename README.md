# PBS Public API Client

Official API client libraries for the [ASSA ABLOY PBS Public API](https://public.api.aa-bts.com). Available for Java, .NET, and Python.

## Client Libraries

| Language | Package | Documentation |
|----------|---------|---------------|
| Java | `com.assaabloy.bts:pbs-api-public-client` | [Java README](java/README.md) |
| .NET | `AssaAbloy.Bts.PbsApiClient` | [.NET README](dotnet/README.md) |
| Python | `pbs-api-client` | [Python README](python/README.md) |

## API Documentation

- [OpenAPI specification](openapi.yaml)
- [Swagger UI](https://public.api.aa-bts.com/q/swagger-ui/)

## Branching Strategy

This project follows gitflow:

- `master` - production releases
- `develop` - integration branch
- `feature/*` - new features
- `release/*` - release preparation
- `hotfix/*` - production fixes

## Releases

Releases are automated via GitHub Actions using git flow. Tagged commits publish to GitHub Packages:

```bash
git flow release start 1.0.0
# bump version on develop, then:
git flow release finish 1.0.0
git push origin master develop 1.0.0
```

### Required GitHub Secrets

| Secret | Description |
|--------|-------------|
| `PBS_API_KEY` | API key for running integration tests |

---
&#169;ASSA ABLOY
