"""PBS Public API client."""

from __future__ import annotations

from datetime import date

import requests

from pbs_api_client.exceptions import ApiException
from pbs_api_client.models import (
    DescriptionAndPrices,
    HardwareAttributes,
    HardwareItem,
    HardwareItems,
    HardwareMatch,
    HardwareOption,
    HardwareOptions,
    Manufacturer,
    Manufacturers,
    PriceBook,
    ProductLine,
)

_DEFAULT_BASE_URL = "https://public.api.aa-bts.com"


class PbsClient:
    """Unified client for the PBS Public API."""

    def __init__(
        self,
        api_key: str,
        base_url: str = _DEFAULT_BASE_URL,
    ) -> None:
        self._session = requests.Session()
        self._session.headers["Authorization"] = f"Bearer {api_key}"
        self._base_url = base_url.rstrip("/")

    def _get(self, path: str, params: dict | None = None) -> dict | list:
        resp = self._request(path, params)
        return resp.json()

    def _request(self, path: str, params: dict | None = None) -> requests.Response:
        url = f"{self._base_url}{path}"
        if params:
            params = {k: v for k, v in params.items() if v is not None}
        resp = self._session.get(url, params=params)
        if not resp.ok:
            raise ApiException(resp.status_code, resp.text)
        return resp

    # -- Hardware Attributes --

    def get_hardware_attributes(
        self,
        manufacturer_id: str,
        *,
        end_row: int | None = None,
        hardware_type: str | None = None,
        product_line: str | None = None,
        query: str | None = None,
        sort: str | None = None,
        start_row: int | None = None,
    ) -> HardwareAttributes:
        data = self._get("/hardwareattributes", {
            "manufacturerId": manufacturer_id,
            "endRow": end_row,
            "hardwareType": hardware_type,
            "productLine": product_line,
            "query": query,
            "sort": sort,
            "startRow": start_row,
        })
        return HardwareAttributes.from_dict(data)

    # -- Hardware Items --

    def get_hardware_items(
        self,
        manufacturer_id: str,
        *,
        attribute_xrefs: list[int] | None = None,
        xref: int | None = None,
        end_row: int | None = None,
        hardware_type: str | None = None,
        product_line: str | None = None,
        query: str | None = None,
        sort: str | None = None,
        start_row: int | None = None,
    ) -> HardwareItems:
        params: dict = {
            "manufacturerId": manufacturer_id,
            "xref": xref,
            "endRow": end_row,
            "hardwareType": hardware_type,
            "productLine": product_line,
            "query": query,
            "sort": sort,
            "startRow": start_row,
        }
        if attribute_xrefs:
            params["attributeXrefs"] = attribute_xrefs
        data = self._get("/hardwareitems", params)
        return HardwareItems.from_dict(data)

    def get_hardware_item_by_id(self, xref: int) -> HardwareItem:
        data = self._get(f"/hardwareitems/{xref}")
        return HardwareItem.from_dict(data)

    def locate_hardware(
        self,
        manufacturer_id: str,
        part_number: str,
        result_qty: int = 10,
        *,
        exclude_no_price_options: bool | None = None,
        exclude_options: bool | None = None,
        include_hardware_item: bool | None = None,
        product_line: str | None = None,
        separator_character: str | None = None,
    ) -> list[HardwareMatch]:
        data = self._get("/hardwareitems/locate", {
            "manufacturerId": manufacturer_id,
            "partNumber": part_number,
            "resultQty": result_qty,
            "excludeNoPriceOptions": exclude_no_price_options,
            "excludeOptions": exclude_options,
            "includeHardwareItem": include_hardware_item,
            "productLine": product_line,
            "separatorCharacter": separator_character,
        })
        return [HardwareMatch.from_dict(m) for m in data]

    # -- Hardware Item Options --

    def get_hardware_items_options(
        self,
        xref: int,
        *,
        end_row: int | None = None,
        query: str | None = None,
        sort: str | None = None,
        start_row: int | None = None,
    ) -> HardwareOptions:
        data = self._get(f"/hardwareitems/{xref}/options", {
            "endRow": end_row,
            "query": query,
            "sort": sort,
            "startRow": start_row,
        })
        return HardwareOptions.from_dict(data)

    def get_hardware_items_option_by_id(
        self, xref: int, option_xref: int
    ) -> HardwareOption:
        data = self._get(f"/hardwareitems/{xref}/options/{option_xref}")
        return HardwareOption.from_dict(data)

    def get_hardware_item_price_with_options(
        self,
        xref: int,
        *,
        hand: str | None = None,
        height: str | None = None,
        length: str | None = None,
        option_xrefs: list[int] | None = None,
        price_book: str | None = None,
        width: str | None = None,
    ) -> DescriptionAndPrices:
        params: dict = {
            "hand": hand,
            "height": height,
            "length": length,
            "priceBook": price_book,
            "width": width,
        }
        if option_xrefs:
            params["optionxref"] = option_xrefs
        data = self._get(f"/hardwareitems/{xref}/pricewithoptions", params)
        return DescriptionAndPrices.from_dict(data)

    # -- Hardware Options --

    def get_hardware_options(
        self,
        manufacturer_id: str,
        *,
        end_row: int | None = None,
        product_line: str | None = None,
        query: str | None = None,
        sort: str | None = None,
        start_row: int | None = None,
    ) -> HardwareOptions:
        data = self._get("/hardwareoptions", {
            "manufacturerId": manufacturer_id,
            "endRow": end_row,
            "productLine": product_line,
            "query": query,
            "sort": sort,
            "startRow": start_row,
        })
        return HardwareOptions.from_dict(data)

    def get_hardware_option_by_id(self, option_xref: int) -> HardwareOption:
        data = self._get(f"/hardwareoptions/{option_xref}")
        return HardwareOption.from_dict(data)

    # -- Manufacturers --

    def get_manufacturers(
        self,
        *,
        manufacturer_id: str | None = None,
        has_product: bool | None = None,
        type: str | None = None,
        end_row: int | None = None,
        query: str | None = None,
        sort: str | None = None,
        start_row: int | None = None,
    ) -> Manufacturers:
        data = self._get("/manufacturers", {
            "manufacturerId": manufacturer_id,
            "hasProduct": has_product,
            "type": type,
            "endRow": end_row,
            "query": query,
            "sort": sort,
            "startRow": start_row,
        })
        return Manufacturers.from_dict(data)

    def get_manufacturer_by_id(self, manufacturer_id: int) -> Manufacturer:
        data = self._get(f"/manufacturers/{manufacturer_id}")
        return Manufacturer.from_dict(data)

    # -- Product Lines & Subtypes --

    def get_product_lines(self, manufacturer_id: str) -> list[ProductLine]:
        data = self._get("/productlines", {"manufacturerId": manufacturer_id})
        return [ProductLine.from_dict(pl) for pl in data]

    def get_sub_types(
        self,
        manufacturer_id: str,
        *,
        product_line: str | None = None,
    ) -> list[str]:
        data = self._get("/subtypes", {
            "manufacturerId": manufacturer_id,
            "productLine": product_line,
        })
        return data

    # -- Price Books --

    def get_price_book_comparisons(
        self,
        manufacturer_id: str,
        *,
        future_price_book: bool | None = None,
    ) -> str:
        """Returns CSV data comparing price books."""
        resp = self._request("/priceBookComparisonTool", {
            "manufacturerId": manufacturer_id,
            "futurePriceBook": future_price_book,
        })
        return resp.text

    def get_price_books(
        self,
        price_books_after_date: date,
        *,
        manufacturer_id: str | None = None,
    ) -> list[PriceBook]:
        data = self._get("/pricebooks", {
            "priceBooksAfterDate": price_books_after_date.isoformat(),
            "manufacturerId": manufacturer_id,
        })
        return [PriceBook.from_dict(pb) for pb in data]

    def list_price_books(self, manufacturer_id: str) -> list[PriceBook]:
        data = self._get("/pricebooks/list", {"manufacturerId": manufacturer_id})
        return [PriceBook.from_dict(pb) for pb in data]
