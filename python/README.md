# PBS Public API Client - Python

Python client library for the [ASSA ABLOY PBS Public API](https://public.api.aa-bts.com).

## Requirements

- Python 3.10+

## Installation

Packages are published as [GitHub Release](https://github.com/assaabloy-bts/pbs-api-public-client/releases) assets.

### Install from GitHub

```bash
pip install pbs-api-client --find-links https://github.com/assaabloy-bts/pbs-api-public-client/releases/latest/download/
```

Or install directly from source:

```bash
pip install "pbs-api-client @ git+https://github.com/assaabloy-bts/pbs-api-public-client.git#subdirectory=python"
```

## Usage

### Creating a Client

```python
from pbs_api_client import PbsClient

# Connect to the default production URL
client = PbsClient("your-api-key")

# Or connect to a custom environment
client = PbsClient("your-api-key", base_url="https://custom-url.example.com")
```

### Querying Manufacturers

```python
# Search manufacturers by abbreviation
result = client.get_manufacturers(manufacturer_id="SA", has_product=True, end_row=20, start_row=0)

for mfg in result.data:
    print(f"{mfg.name} ({mfg.abbr})")

# Get a single manufacturer by numeric ID
mfg = client.get_manufacturer_by_id(1)
```

### Browsing Hardware Items

```python
# List hardware items for a manufacturer
items = client.get_hardware_items("SA", end_row=10, start_row=0)

for item in items.data:
    print(f"{item.part_number} - {item.manufacturer}")

# Get a single hardware item by xref
item = client.get_hardware_item_by_id(12345)
```

### Searching for Parts

```python
# Find a part number match
matches = client.locate_hardware("SA", "8204", result_qty=5)

for match in matches:
    print(f"{match.part_number:<20} Score: {match.match_score}  {match.order_description}")
```

### Getting Prices

```python
# Get price for a hardware item with selected options
pricing = client.get_hardware_item_price_with_options(12345)

print(pricing.part_number)
print(pricing.order_description)
for p in pricing.price:
    print(f"  {p.price_book}: ${p.unit_price:.2f}")
```

### Working with Hardware Options

```python
# List options for a hardware item
item_options = client.get_hardware_items_options(12345, end_row=10, start_row=0)

# List all options for a manufacturer
all_options = client.get_hardware_options("SA", end_row=10, start_row=0)
```

### Product Lines and Subtypes

```python
# Get product lines
product_lines = client.get_product_lines("SA")

# Get subtypes
sub_types = client.get_sub_types("SA")
```

### Price Books

```python
from datetime import date

# List all price books for a manufacturer
price_books = client.list_price_books("SA")

# Get price books after a specific date
recent_books = client.get_price_books(date(2024, 1, 1), manufacturer_id="SA")
```

### Error Handling

All API methods raise `ApiException` on failure:

```python
from pbs_api_client import PbsClient, ApiException

try:
    mfg = client.get_manufacturer_by_id(999999)
except ApiException as e:
    print(f"HTTP {e.status_code}: {e.message}")
```

## Building

```bash
cd python
pip install -e ".[dev]"
```

## Running Tests

Integration tests require a valid API key:

```bash
PBS_API_KEY=your-api-key pytest
```

Without the API key, tests are skipped.

---
&#169;ASSA ABLOY
