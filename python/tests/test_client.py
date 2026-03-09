"""Integration tests for PbsClient."""

import os
from datetime import date

import pytest

from pbs_api_client import PbsClient, ApiException

api_key = os.environ.get("PBS_API_KEY")
base_url = os.environ.get("PBS_API_URL")

pytestmark = pytest.mark.skipif(not api_key, reason="PBS_API_KEY environment variable is required")


@pytest.fixture(scope="module")
def client():
    if base_url:
        return PbsClient(api_key, base_url=base_url)
    return PbsClient(api_key)


@pytest.fixture(scope="module")
def manufacturer(client):
    result = client.get_manufacturers(manufacturer_id="SA", has_product=True, end_row=20, start_row=0)
    assert result.data
    return result.data[0]


@pytest.fixture(scope="module")
def hardware_item(client, manufacturer):
    result = client.get_hardware_items(manufacturer.abbr, end_row=5, start_row=0)
    assert result.data
    return result.data[0]


# -- Manufacturers --


def test_get_manufacturers(client, manufacturer):
    result = client.get_manufacturers(manufacturer_id="SA", has_product=True, end_row=20, start_row=0)

    assert result.data
    assert result.start_row == 0
    assert result.total_rows > 0

    mfg = result.data[0]
    assert mfg.name is not None
    assert mfg.abbr is not None
    assert mfg.manufacturer_id is not None


def test_get_manufacturer_by_id(client, manufacturer):
    result = client.get_manufacturer_by_id(manufacturer.manufacturer_id)

    assert result.manufacturer_id == manufacturer.manufacturer_id
    assert result.name is not None
    assert result.abbr is not None
    assert result.type is not None


# -- Product Lines & Subtypes --


def test_get_product_lines(client, manufacturer):
    result = client.get_product_lines(manufacturer.abbr)

    assert result
    pl = result[0]
    assert pl.product_line is not None
    assert pl.hardware_type is not None


def test_get_sub_types(client, manufacturer):
    result = client.get_sub_types(manufacturer.abbr)

    assert result
    assert result[0] is not None


# -- Hardware Items --


def test_get_hardware_items(client, manufacturer):
    result = client.get_hardware_items(manufacturer.abbr, end_row=5, start_row=0)

    assert result.data
    assert result.start_row == 0
    assert result.total_rows > 0

    item = result.data[0]
    assert item.xref is not None
    assert item.manufacturer is not None
    assert item.part_number is not None


def test_get_hardware_item_by_id(client, hardware_item):
    result = client.get_hardware_item_by_id(hardware_item.xref)

    assert result.xref == hardware_item.xref
    assert result.manufacturer is not None
    assert result.part_number is not None
    assert result.type is not None


def test_get_hardware_attributes(client, manufacturer):
    result = client.get_hardware_attributes(manufacturer.abbr, end_row=5, start_row=0)

    assert result.data
    assert result.start_row == 0
    assert result.total_rows > 0

    attr = result.data[0]
    assert attr.xref is not None
    assert attr.manufacturer is not None
    assert attr.type is not None
    assert attr.print_code is not None
    assert attr.print_description is not None


def test_locate_hardware(client, manufacturer, hardware_item):
    item = client.get_hardware_item_by_id(hardware_item.xref)
    result = client.locate_hardware(manufacturer.abbr, item.part_number, 5)

    assert result
    match = result[0]
    assert match.part_number is not None
    assert match.order_description is not None
    assert match.match_score is not None


# -- Hardware Item Options --


def test_get_hardware_items_options(client, hardware_item):
    result = client.get_hardware_items_options(hardware_item.xref, end_row=5, start_row=0)

    assert result is not None
    assert result.start_row == 0

    if result.data:
        opt = result.data[0]
        assert opt.xref is not None
        assert opt.manufacturer is not None
        assert opt.print_code is not None
        assert opt.print_description is not None


def test_get_hardware_items_option_by_id(client, hardware_item):
    options = client.get_hardware_items_options(hardware_item.xref, end_row=1, start_row=0)
    if not options.data:
        pytest.skip("No options available for the test hardware item")

    opt = options.data[0]
    result = client.get_hardware_items_option_by_id(hardware_item.xref, opt.xref)

    assert result.xref == opt.xref
    assert result.manufacturer is not None
    assert result.print_code is not None


def test_get_hardware_item_price_with_options(client, hardware_item):
    result = client.get_hardware_item_price_with_options(hardware_item.xref)

    assert result.part_number is not None
    assert result.order_description is not None


# -- Hardware Options (global) --


def test_get_hardware_options(client, manufacturer):
    result = client.get_hardware_options(manufacturer.abbr, end_row=5, start_row=0)

    assert result.data
    assert result.start_row == 0
    assert result.total_rows > 0

    opt = result.data[0]
    assert opt.xref is not None
    assert opt.manufacturer is not None
    assert opt.print_code is not None
    assert opt.print_description is not None


def test_get_hardware_option_by_id(client, manufacturer):
    options = client.get_hardware_options(manufacturer.abbr, end_row=1, start_row=0)
    assert options.data

    result = client.get_hardware_option_by_id(options.data[0].xref)

    assert result.xref == options.data[0].xref
    assert result.manufacturer is not None
    assert result.print_code is not None


# -- Price Books --


def test_get_price_book_comparisons(client, manufacturer):
    try:
        client.get_price_book_comparisons(manufacturer.abbr)
    except ApiException as e:
        # 504 Gateway Timeout is acceptable for this long-running comparison
        if e.status_code != 504:
            raise


def test_list_price_books(client, manufacturer):
    result = client.list_price_books(manufacturer.abbr)

    assert result
    pb = result[0]
    assert pb.manufacturer is not None
    assert pb.description is not None
    assert pb.effective_date is not None


def test_get_price_books(client, manufacturer):
    result = client.get_price_books(date(2000, 1, 1), manufacturer_id=manufacturer.abbr)

    assert result
    pb = result[0]
    assert pb.manufacturer is not None
    assert pb.description is not None
    assert pb.effective_date is not None
