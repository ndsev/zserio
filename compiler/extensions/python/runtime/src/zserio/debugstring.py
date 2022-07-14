"""
The module provides utilities for JSON debug string which can be obtained from zserio objects.

.. note:: Zserio objects must be generated with `-withTypeInfoCode` zserio option to enable JSON debug string!
"""

import io
import typing

from zserio.walker import Walker, WalkFilter
from zserio.json import JsonWriter, JsonReader

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

def from_json_stream(obj_class: typing.Type[typing.Any], text_io: typing.TextIO,
                     *arguments: typing.List[typing.Any]) -> typing.Any:
    """
    Parses JSON debug string from given text stream and creates instance of the requested zserio object
    according to the data contained in the debug string.

    .. note:: The created object can be only partially initialized depending on the data stored in the
              JSON debug string.

    .. code:: python

        import io
        import zserio

        text_io = io.StringIO("{\\\"field1\\\": 13}")
        obj = zserio.from_json_stream(SomeZserioObject, text_io)

    :param obj_class: Class instance of the generated object to create.
    :param text_io: Text stream to use.
    :param arguments: Arguments of the generated zserio object.
    :returns: Instance of the requested zserio object.
    :raises PythonRuntimeException: In case of any error.
    """

    json_reader = JsonReader(text_io)
    return json_reader.read(obj_class.type_info(), *arguments)

def from_json_string(obj_class: typing.Type[typing.Any], json_string: str,
                     *arguments: typing.List[typing.Any]) -> typing.Any:
    """
    Parses JSON debug string and creates instance of the requested zserio object
    according to the data contained in the debug string.

    .. note:: The created object can be only partially initialized depending on the data stored in the
              JSON debug string.

    .. code:: python

        import zserio

        json_string = "{\\\"field1\\\": 13}"
        obj = zserio.from_json_string(SomeZserioObject, json_string)

    :param obj_class: Class instance of the generated object to create.
    :param json_string: JSON debug string to parse.
    :param arguments: Arguments of the generated zserio object.
    :returns: Instance of the requested zserio object.
    :raises PythonRuntimeException: In case of any error.
    """

    return from_json_stream(obj_class, io.StringIO(json_string), *arguments)

def from_json_file(obj_class: typing.Type[typing.Any], filename: str,
                   *arguments: typing.List[typing.Any]) -> typing.Any:
    """
    Parses JSON debug string from given file and creates instance of the requested zserio object
    according to the data contained in the debug string.

    .. note:: The created object can be only partially initialized depending on the data stored in the
              JSON debug string.

    .. code:: python

        import zserio

        obj = zserio.from_json_file(SomeZserioObject, "file_name.json")

    :param obj_class: Class instance of the generated object to create.
    :param filename: File name to read.
    :param arguments: Arguments of the generated zserio object.
    :returns: Instance of the requested zserio object.
    :raises PythonRuntimeException: In case of any error.
    """

    with open(filename, "r", encoding="utf-8") as text_io:
        return from_json_stream(obj_class, text_io, *arguments)
