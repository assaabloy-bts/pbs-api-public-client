# PBS Public API Client - Java

Java client library for the [ASSA ABLOY PBS Public API](https://public.api.aa-bts.com).

## Requirements

- Java 25+
- Maven 3.6.3+

## Installation

Maven:

```xml
<dependency>
    <groupId>com.assaabloy.bts</groupId>
    <artifactId>pbs-api-public-client</artifactId>
    <version>0.0.1</version>
</dependency>
```

Gradle:

```groovy
implementation 'com.assaabloy.bts:pbs-api-public-client:0.0.1'
```

## Usage

### Creating a Client

```java
import com.assaabloy.bts.pbs.api.client.PbsClient;
import com.assaabloy.bts.pbs.api.client.ApiException;

// Connect to the default production URL
PbsClient client = new PbsClient("your-api-key");

// Or connect to a custom environment
PbsClient client = new PbsClient("https://custom-url.example.com", "your-api-key");
```

### Querying Manufacturers

```java
import com.assaabloy.bts.pbs.api.client.model.*;

// Search manufacturers by abbreviation
Manufacturers result = client.getManufacturers(
        "SA",    // manufacturerId (abbreviation, name, or numeric ID)
        true,    // hasProduct
        null,    // type
        20L,     // endRow
        null,    // query
        null,    // sort
        0L);     // startRow

for (Manufacturer mfg : result.getData()) {
    System.out.println(mfg.getName() + " (" + mfg.getAbbr() + ")");
}

// Get a single manufacturer by numeric ID
Manufacturer mfg = client.getManufacturerById(1L);
```

### Browsing Hardware Items

```java
// List hardware items for a manufacturer
HardwareItems items = client.getHardwareItems(
        "SA",    // manufacturerId
        null,    // attributeXrefs
        null,    // xref
        10L,     // endRow
        null,    // hardwareType
        null,    // productLine
        null,    // query
        null,    // sort
        0L);     // startRow

for (HardwareItem item : items.getData()) {
    System.out.println(item.getPartNumber() + " - " + item.getManufacturer());
}

// Get a single hardware item by xref
HardwareItem item = client.getHardwareItemById(12345L);
```

### Searching for Parts

```java
// Find a part number match
List<HardwareMatch> matches = client.locateHardware(
        "SA",           // manufacturerId
        "8204",         // partNumber to search
        5,              // resultQty (max results)
        false,          // excludeNoPriceOptions
        false,          // excludeOptions
        false,          // includeHardwareItem
        null,           // productLine
        null);          // separatorCharacter

for (HardwareMatch match : matches) {
    System.out.printf("%-20s Score: %d  %s%n",
            match.getPartNumber(), match.getMatchScore(), match.getOrderDescription());
}
```

### Getting Prices

```java
// Get price for a hardware item with selected options
DescriptionAndPrices pricing = client.getHardwareItemPriceWithOptions(
        12345L,  // hardware item xref
        null,    // hand
        null,    // height
        null,    // length
        null,    // optionxref (list of option xrefs)
        null,    // priceBook
        null);   // width

System.out.println(pricing.getPartNumber());
System.out.println(pricing.getOrderDescription());
for (Price p : pricing.getPrice()) {
    System.out.printf("  %s: $%.2f%n", p.getPriceBook(), p.getUnitPrice());
}
```

### Working with Hardware Options

```java
// List options for a hardware item
HardwareOptions itemOptions = client.getHardwareItemsOptions(
        12345L,  // hardware item xref
        10L,     // endRow
        null,    // query
        null,    // sort
        0L);     // startRow

// List all options for a manufacturer
HardwareOptions allOptions = client.getHardwareOptions(
        "SA",    // manufacturerId
        10L,     // endRow
        null,    // productLine
        null,    // query
        null,    // sort
        0L);     // startRow
```

### Product Lines and Subtypes

```java
// Get product lines
List<ProductLine> productLines = client.getProductLines("SA");

// Get subtypes
List<String> subTypes = client.getSubTypes("SA", null);
```

### Price Books

```java
// List all price books for a manufacturer
List<PriceBook> priceBooks = client.listPriceBooks("SA");

// Get price books after a specific date
List<PriceBook> recentBooks = client.getPriceBooks(
        LocalDate.of(2024, 1, 1),  // priceBooksAfterDate
        "SA");                      // manufacturerId
```

### Error Handling

All API methods throw `ApiException` on failure. The exception includes the HTTP status code and response body:

```java
try {
    Manufacturer mfg = client.getManufacturerById(999999L);
} catch (ApiException e) {
    System.err.println("HTTP " + e.getCode() + ": " + e.getMessage());
}
```

## Building

```bash
mvn clean compile
```

## Running Tests

Integration tests require a valid API key:

```bash
PBS_API_KEY=your-api-key mvn clean verify
```

Without the API key, tests are skipped.

---
&#169;ASSA ABLOY
