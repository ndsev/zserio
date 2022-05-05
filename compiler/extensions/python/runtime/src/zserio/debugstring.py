"""
The module provides utilities for JSON debug string which can be obtained from zserio objects.

Note that zserio objects must be generated with -withTypeInfoCode zserio option.
"""

import io
import typing

from zserio.walker import Walker, WalkFilter
from zserio.json import JsonWriter

def to_json_stream(obj: typing.Any, text_io: typing.TextIO, *, indent: typing.Union[None, int, str] = 4,
                    walk_filter: typing.Optional[WalkFilter] = None) -> None:
    """
    Writes contents of given zserio object to debug stream in JSON format using Walker with JsonWriter.

    :param obj: Zserio object to use.
    :param text_io: Text stream to use.
    :param indent: Indent argument for JsonWriter.
    :param walk_filter: Walk filter to use by Walker.
    """

    json_writer = JsonWriter(text_io=text_io, indent=indent)
    walker = Walker(json_writer, walk_filter)
    walker.walk(obj)

def to_json_string(obj: typing.Any, *, indent: typing.Union[None, int, str] = 4,
                    walk_filter: typing.Optional[WalkFilter] = None) -> str:
    """
    Gets debug string in JSON format using Walker with JsonWriter for given zserio object.

    :param obj: Zserio object to use.
    :param indent: Indent argument for JsonWriter.
    :param walk_filter: Walk filter to use by Walker.

    :returns: JSON debug string.
    """

    text_io = io.StringIO()
    to_json_stream(obj, text_io, indent=indent, walk_filter=walk_filter)
    return text_io.getvalue()

def to_json_file(obj: typing.Any, filename: str, *, indent: typing.Union[None, int, str] = 4,
                  walk_filter: typing.Optional[WalkFilter] = None) -> None:
    """
    Writes contents of given zserio object to debug file in JSON format using Walker with JsonWriter.

    :param obj: Zserio object to use.
    :param filename: Name of file to write.
    :param indent: Indent argument for JsonWriter.
    :param walk_filter: Walk filter to use by Walker.
    """

    with open(filename, "w", encoding="utf-8") as text_io:
        to_json_stream(obj, text_io, indent=indent, walk_filter=walk_filter)
