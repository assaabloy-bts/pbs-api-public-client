"""Data models for PBS API responses."""

from __future__ import annotations

from dataclasses import dataclass, field
from datetime import date


def _parse_date(value: str | None) -> date | None:
    if value is None:
        return None
    return date.fromisoformat(value)


@dataclass
class Price:
    price_book: str | None = None
    effective_date: date | None = None
    effective: bool | None = None
    list_price: float | None = None
    price: float | None = None
    currency: str | None = None

    @classmethod
    def from_dict(cls, data: dict) -> Price:
        return cls(
            price_book=data.get("priceBook"),
            effective_date=_parse_date(data.get("effectiveDate")),
            effective=data.get("effective"),
            list_price=data.get("listPrice"),
            price=data.get("price"),
            currency=data.get("currency"),
        )


@dataclass
class Asset:
    id: str | None = None
    type: str | None = None
    title: str | None = None
    mime_type: str | None = None
    url: str | None = None

    @classmethod
    def from_dict(cls, data: dict) -> Asset:
        return cls(
            id=data.get("id"),
            type=data.get("type"),
            title=data.get("title"),
            mime_type=data.get("mimeType"),
            url=data.get("url"),
        )


@dataclass
class Prep:
    id: int | None = None
    type: str | None = None
    code: str | None = None
    description: str | None = None

    @classmethod
    def from_dict(cls, data: dict) -> Prep:
        return cls(
            id=data.get("id"),
            type=data.get("type"),
            code=data.get("code"),
            description=data.get("description"),
        )


@dataclass
class Attribute:
    xref: int | None = None
    manufacturer: str | None = None
    type: str | None = None
    print_code: str | None = None
    print_description: str | None = None
    uom: str | None = None
    price: Price | None = None
    preps: list[Prep] = field(default_factory=list)
    num_cylinders: int | None = None
    ansi: str | None = None
    grade: str | None = None
    electrical: bool | None = None

    @classmethod
    def from_dict(cls, data: dict) -> Attribute:
        price_data = data.get("price")
        return cls(
            xref=data.get("xref"),
            manufacturer=data.get("manufacturer"),
            type=data.get("type"),
            print_code=data.get("printCode"),
            print_description=data.get("printDescription"),
            uom=data.get("uom"),
            price=Price.from_dict(price_data) if price_data else None,
            preps=[Prep.from_dict(p) for p in data.get("preps", [])],
            num_cylinders=data.get("numCylinders"),
            ansi=data.get("ansi"),
            grade=data.get("grade"),
            electrical=data.get("electrical"),
        )


@dataclass
class HardwareAttributes:
    start_row: int | None = None
    end_row: int | None = None
    total_rows: int | None = None
    data: list[Attribute] = field(default_factory=list)

    @classmethod
    def from_dict(cls, data: dict) -> HardwareAttributes:
        return cls(
            start_row=data.get("startRow"),
            end_row=data.get("endRow"),
            total_rows=data.get("totalRows"),
            data=[Attribute.from_dict(a) for a in data.get("data", [])],
        )


@dataclass
class HardwareItem:
    xref: int | None = None
    manufacturer: str | None = None
    product_line: str | None = None
    type: str | None = None
    type_description: str | None = None
    sub_type_description: str | None = None
    part_number: str | None = None
    order_description: str | None = None
    num_cylinders: int | None = None
    dead_lock: bool | None = None
    assets: list[Asset] = field(default_factory=list)
    weight: float | None = None
    price: list[Price] = field(default_factory=list)
    attributes: list[Attribute] = field(default_factory=list)
    preps: list[Prep] = field(default_factory=list)

    @classmethod
    def from_dict(cls, data: dict) -> HardwareItem:
        return cls(
            xref=data.get("xref"),
            manufacturer=data.get("manufacturer"),
            product_line=data.get("productLine"),
            type=data.get("type"),
            type_description=data.get("typeDescription"),
            sub_type_description=data.get("subTypeDescription"),
            part_number=data.get("partNumber"),
            order_description=data.get("orderDescription"),
            num_cylinders=data.get("numCylinders"),
            dead_lock=data.get("deadLock"),
            assets=[Asset.from_dict(a) for a in data.get("assets", [])],
            weight=data.get("weight"),
            price=[Price.from_dict(p) for p in data.get("price", [])],
            attributes=[Attribute.from_dict(a) for a in data.get("attributes", [])],
            preps=[Prep.from_dict(p) for p in data.get("preps", [])],
        )


@dataclass
class HardwareItems:
    start_row: int | None = None
    end_row: int | None = None
    total_rows: int | None = None
    data: list[HardwareItem] = field(default_factory=list)

    @classmethod
    def from_dict(cls, data: dict) -> HardwareItems:
        return cls(
            start_row=data.get("startRow"),
            end_row=data.get("endRow"),
            total_rows=data.get("totalRows"),
            data=[HardwareItem.from_dict(i) for i in data.get("data", [])],
        )


@dataclass
class HardwareMatch:
    part_number: str | None = None
    order_description: str | None = None
    price: Price | None = None
    exact_match: bool | None = None
    match_score: float | None = None
    xref: int | None = None
    option_xrefs: list[int] = field(default_factory=list)
    hardware_item: HardwareItem | None = None

    @classmethod
    def from_dict(cls, data: dict) -> HardwareMatch:
        price_data = data.get("price")
        hw_data = data.get("hardwareItem")
        return cls(
            part_number=data.get("partNumber"),
            order_description=data.get("orderDescription"),
            price=Price.from_dict(price_data) if price_data else None,
            exact_match=data.get("exactMatch"),
            match_score=data.get("matchScore"),
            xref=data.get("xref"),
            option_xrefs=data.get("optionXrefs", []),
            hardware_item=HardwareItem.from_dict(hw_data) if hw_data else None,
        )


@dataclass
class HardwareOption:
    xref: int | None = None
    manufacturer: str | None = None
    type: str | None = None
    print_code: str | None = None
    print_description: str | None = None
    uom: str | None = None
    price: list[Price] = field(default_factory=list)
    preps: list[Prep] = field(default_factory=list)
    num_cylinders: int | None = None
    ansi: str | None = None
    grade: str | None = None
    electrical: bool | None = None
    product_lines: list[str] = field(default_factory=list)

    @classmethod
    def from_dict(cls, data: dict) -> HardwareOption:
        return cls(
            xref=data.get("xref"),
            manufacturer=data.get("manufacturer"),
            type=data.get("type"),
            print_code=data.get("printCode"),
            print_description=data.get("printDescription"),
            uom=data.get("uom"),
            price=[Price.from_dict(p) for p in data.get("price", [])],
            preps=[Prep.from_dict(p) for p in data.get("preps", [])],
            num_cylinders=data.get("numCylinders"),
            ansi=data.get("ansi"),
            grade=data.get("grade"),
            electrical=data.get("electrical"),
            product_lines=data.get("productLines", []),
        )


@dataclass
class HardwareOptions:
    start_row: int | None = None
    end_row: int | None = None
    total_rows: int | None = None
    data: list[HardwareOption] = field(default_factory=list)

    @classmethod
    def from_dict(cls, data: dict) -> HardwareOptions:
        return cls(
            start_row=data.get("startRow"),
            end_row=data.get("endRow"),
            total_rows=data.get("totalRows"),
            data=[HardwareOption.from_dict(o) for o in data.get("data", [])],
        )


@dataclass
class DescriptionAndPrices:
    part_number: str | None = None
    order_description: str | None = None
    description: str | None = None
    price: list[Price] = field(default_factory=list)

    @classmethod
    def from_dict(cls, data: dict) -> DescriptionAndPrices:
        return cls(
            part_number=data.get("partNumber"),
            order_description=data.get("orderDescription"),
            description=data.get("description"),
            price=[Price.from_dict(p) for p in data.get("price", [])],
        )


@dataclass
class Manufacturer:
    manufacturer_id: int | None = None
    type: str | None = None
    name: str | None = None
    abbr: str | None = None
    address1: str | None = None
    address2: str | None = None
    address3: str | None = None
    city: str | None = None
    state: str | None = None
    zip: str | None = None
    country: str | None = None
    phone: str | None = None
    fax: str | None = None
    email: str | None = None
    web_url: str | None = None
    business_unit: str | None = None

    @classmethod
    def from_dict(cls, data: dict) -> Manufacturer:
        return cls(
            manufacturer_id=data.get("manufacturerId"),
            type=data.get("type"),
            name=data.get("name"),
            abbr=data.get("abbr"),
            address1=data.get("address1"),
            address2=data.get("address2"),
            address3=data.get("address3"),
            city=data.get("city"),
            state=data.get("state"),
            zip=data.get("zip"),
            country=data.get("country"),
            phone=data.get("phone"),
            fax=data.get("fax"),
            email=data.get("email"),
            web_url=data.get("webUrl"),
            business_unit=data.get("businessUnit"),
        )


@dataclass
class Manufacturers:
    start_row: int | None = None
    end_row: int | None = None
    total_rows: int | None = None
    data: list[Manufacturer] = field(default_factory=list)

    @classmethod
    def from_dict(cls, data: dict) -> Manufacturers:
        return cls(
            start_row=data.get("startRow"),
            end_row=data.get("endRow"),
            total_rows=data.get("totalRows"),
            data=[Manufacturer.from_dict(m) for m in data.get("data", [])],
        )


@dataclass
class ProductLine:
    product_line: str | None = None
    hardware_type: str | None = None

    @classmethod
    def from_dict(cls, data: dict) -> ProductLine:
        return cls(
            product_line=data.get("productLine"),
            hardware_type=data.get("hardwareType"),
        )


@dataclass
class PriceBook:
    manufacturer: str | None = None
    description: str | None = None
    effective_date: date | None = None

    @classmethod
    def from_dict(cls, data: dict) -> PriceBook:
        return cls(
            manufacturer=data.get("manufacturer"),
            description=data.get("description"),
            effective_date=_parse_date(data.get("effectiveDate")),
        )
