"""
The module implements zserio.Walker.Observer for writing of zserio objects to JSON format.
"""

import io
import typing
import json

from zserio.bitbuffer import BitBuffer
from zserio.typeinfo import TypeAttribute, MemberInfo
from zserio.walker import WalkObserver

class JsonWriter(WalkObserver):
    """
    Walker observer which dumps zserio objects to JSON format.
    """

    def __init__(self, *, text_io: typing.Optional[typing.TextIO] = None,
                 item_separator: str = None, key_separator: str = None,
                 indent: typing.Union[str, int] = None) -> None:
        """
        Constructor.

        :param text_io: Optional text stream for JSON output, io.StringIO is used by default.
        :param item_separator: Optional item separator, default is ', ' if indent is None, ',' otherwise.
        :param key_separator: Optional key separator, default is ': '.
        :param indent: String or (non-negative) integer defining the indent. If not None, newlines are inserted.
        """

        self._io = text_io if text_io else io.StringIO()
        self._item_separator = item_separator if item_separator else ("," if indent is not None else ", ")
        self._key_separator = key_separator if key_separator else ": "
        self._indent = (indent if isinstance(indent, str) else " " * indent) if indent is not None else None

        self._is_first = True
        self._level = 0
        self._json_encoder = JsonEncoder()

    def get_io(self) -> typing.TextIO:
        """
        Gets the underlying text stream.

        :returns: Underlying text steam.
        """

        return self._io

    def begin_root(self, _compound: typing.Any) -> None:
        self._begin_object()

    def end_root(self, _compound: typing.Any) -> None:
        self._end_object()

    def begin_array(self, array: typing.List[typing.Any], member_info: MemberInfo) -> None:
        self._begin_item()

        self._write_key(member_info.schema_name)

        self._begin_array()

    def end_array(self, array: typing.List[typing.Any], member_info: MemberInfo) -> None:
        self._end_array()

        self._end_item()

    def begin_compound(self, compound: typing.Any, member_info: MemberInfo,
                       element_index: typing.Optional[int] = None) -> None:
        self._begin_item()

        if element_index is None:
            self._write_key(member_info.schema_name)

        self._begin_object()

    def end_compound(self, compound: typing.Any, member_info: MemberInfo,
                     _element_index: typing.Optional[int] = None) -> None:
        self._end_object()

        self._end_item()

    def visit_value(self, value: typing.Any, member_info: MemberInfo,
                    element_index: typing.Optional[int] = None) -> None:
        self._begin_item()

        if element_index is None:
            self._write_key(member_info.schema_name)

        self._write_value(value, member_info)

        self._end_item()

    def _begin_item(self):
        if not self._is_first:
            self._io.write(self._item_separator)

        if self._indent is not None:
            self._io.write("\n")
            if self._indent:
                self._io.write(self._indent * self._level)

    def _end_item(self):
        self._is_first = False

    def _begin_object(self):
        self._io.write("{")

        self._is_first = True
        self._level += 1

    def _end_object(self):
        if self._indent is not None:
            self._io.write("\n")
        self._level -= 1
        if self._indent:
            self._io.write(self._indent * self._level)

        self._io.write("}")

    def _begin_array(self):
        self._io.write("[")

        self._is_first = True
        self._level += 1

    def _end_array(self):
        if self._indent is not None:
            self._io.write("\n")
        self._level -= 1
        if self._indent:
            self._io.write(self._indent * self._level)

        self._io.write("]")

    def _write_key(self, key: str) -> None:
        self._io.write(f"{self._json_encoder.encode(key)}{self._key_separator}")

    def _write_value(self, value: typing.Any, member_info: MemberInfo) -> None:
        if isinstance(value, BitBuffer):
            self._write_bitbuffer(value)
        else:
            type_info = member_info.type_info
            if (TypeAttribute.ENUM_ITEMS in type_info.attributes or
                TypeAttribute.BITMASK_VALUES in type_info.attributes):
                json_value = self._json_encoder.encode(value.value)
            else:
                json_value = self._json_encoder.encode(value)
            self._io.write(json_value)

    def _write_bitbuffer(self, value: typing.Any):
        self._begin_object()
        self._begin_item()
        self._write_key("buffer")
        self._begin_array()
        for byte in value.buffer:
            self._begin_item()
            self._io.write(self._json_encoder.encode(byte))
            self._end_item()
        self._end_array()
        self._end_item()
        self._begin_item()
        self._write_key("bitSize")
        self._io.write(self._json_encoder.encode(value.bitsize))
        self._end_item()
        self._end_object()

class JsonEncoder:
    """
    Converts zserio values to Json string representation.
    """

    def __init__(self):
        """
        Constructor.
        """

        self._encoder = json.JSONEncoder(ensure_ascii=False)

    def encode(self, value: typing.Any) -> str:
        """
        Encodes value to JSON string representation.

        :param value: Value to encode.

        :returns: Value encoded to string as a valid JSON value.
        """

        return self._encoder.encode(value)
