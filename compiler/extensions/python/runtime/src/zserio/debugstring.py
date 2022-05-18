"""
The module provides utilities for JSON debug string which can be obtained from zserio objects.

.. note:: Zserio objects must be generated with `-withTypeInfoCode` zserio option to enable JSON debug string!
"""

import io
import typing

from zserio.walker import Walker, WalkFilter
from zserio.json import JsonWriter

def to_json_stream(obj: typing.Any, text_io: typing.TextIO, *, indent: typing.Union[None, int, str] = 4,
                    walk_filter: typing.Optional[WalkFilter] = None) -> None:
    """
    Writes contents of given zserio object to debug stream in JSON format using Walker with JsonWriter.

    Example:

    .. code:: python

        import io
        import zserio

        obj = SomeZserioObject()
        text_io = io.StringIO()
        zserio.to_json_stream(obj, text_io)

    The function allows usage of walk filters as well. The following example shows filtering of arrays
    up to 5 elements:

    .. code:: python

        import io
        import zserio

        obj = SomeZserioObject()
        text_io = io.StringIO()
        walk_filter = zserio.ArrayLengthWalkFilter(5)
        zserio.to_json_stream(obj, text_io, walk_filter=walk_filter)

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

    Example:

    .. code:: python

        import zserio

        obj = SomeZserioObject()
        zserio.to_json_string(obj)

    The function allows usage of walk filters as well. The following example shows filtering of arrays
    up to 5 elements:

    .. code:: python

        import zserio

        obj = SomeZserioObject()
        walk_filter = zserio.ArrayLengthWalkFilter(5)
        zserio.to_json_string(obj, walk_filter=walk_filter)

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

    Example:

    .. code:: python

        import zserio

        obj = SomeZserioObject()
        zserio.to_json_file(obj, "file_name.json")

    The function allows usage of walk filters as well. The following example shows filtering of arrays
    up to 5 elements:

    .. code:: python

        import zserio

        obj = SomeZserioObject()
        walk_filter = zserio.ArrayLengthWalkFilter(5)
        zserio.to_json_file(obj, "file_name.json", walk_filter=walk_filter)

    :param obj: Zserio object to use.
    :param filename: Name of file to write.
    :param indent: Indent argument for JsonWriter.
    :param walk_filter: Walk filter to use by Walker.
    """

    with open(filename, "w", encoding="utf-8") as text_io:
        to_json_stream(obj, text_io, indent=indent, walk_filter=walk_filter)
