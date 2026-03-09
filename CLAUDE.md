# CLAUDE.md

## Project Structure

- `java/` - Maven project, OpenAPI Generator for client code generation
- `dotnet/` - .NET project, NSwag for client code generation
- `openapi.yaml` - Shared OpenAPI 3.1.0 spec at repo root
- Both projects use awk preprocessing to simplify `manufacturerId` oneOf → plain string

## Key Versions

- Java 25 (Corretto distribution in CI)
- .NET 10.0
- Jackson: 2.21.1 for databind/jsr310, 2.21 for annotations (different versioning scheme)

## Release Process

- Uses git flow (`git flow release start X.Y.Z` / `git flow release finish X.Y.Z`)
- Tags do NOT have a "v" prefix (e.g. `0.0.1`, not `v0.0.1`)
- Immediately after creating the release branch, bump the version on `develop` to the next version:
  - Java: `pom.xml` → `<version>X.Y.Z-SNAPSHOT</version>`
  - .NET: `PbsApiClient.csproj` → `<Version>X.Y.Z</Version>`
- Once the release branch exists, `develop` represents the next version
- After finishing the release, push master, develop, and the tag

## Publishing

- Artifacts publish to GitHub Packages (not Maven Central / NuGet.org)
- Auth uses `GITHUB_TOKEN` — no external secrets needed except `PBS_API_KEY` for tests
- GitHub Packages requires authentication to pull (even for public repos)
