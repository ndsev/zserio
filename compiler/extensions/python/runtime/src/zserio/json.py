"""
The module implements zserio.Walker.Observer for writing of zserio objects to JSON format.
"""

import io
import typing
import json

from zserio.bitbuffer import BitBuffer
from zserio.typeinfo import TypeInfo, RecursiveTypeInfo, TypeAttribute, MemberInfo, MemberAttribute
from zserio.walker import Walker

class JsonWriter(Walker.Observer):
    """
    Observer implementing zserio.Walker.Observer which dumps zserio objects to JSON format.
    """

    def __init__(self, *, text_io: typing.Optional[io.TextIOBase] = None,
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

    def get_io(self) -> io.TextIOBase:
        """
        Gets the underlying text stream.

        :returns: Underlying text steam.
        """

        return self._io

    def begin_root(self, _compound: typing.Any) -> None:
        self._begin_obj()

    def end_root(self, _compound: typing.Any) -> None:
        self._end_obj()

    def begin_array(self, array: typing.List[typing.Any], member_info: MemberInfo) -> None:
        self._begin_item()

        self._io.write(f"\"{member_info.schema_name}\"{self._key_separator}")

        self._begin_array()

    def end_array(self, array: typing.List[typing.Any], member_info: MemberInfo) -> None:
        self._is_first = False

        self._end_array()

        self._end_item()

    def begin_compound(self, compound: typing.Any, member_info: MemberInfo,
                       _element_index: typing.Optional[int] = None) -> None:
        self._begin_item()

        if not MemberAttribute.ARRAY_LENGTH in member_info.attributes:
            self._io.write(f"\"{member_info.schema_name}\"{self._key_separator}")

        self._begin_obj()

    def end_compound(self, compound: typing.Any, member_info: MemberInfo,
                     _element_index: typing.Optional[int] = None) -> None:
        self._is_first = False

        self._end_obj()

        self._end_item()

    def visit_value(self, value: typing.Any, member_info: MemberInfo,
                    _element_index: typing.Optional[int] = None) -> None:
        self._begin_item()

        if value is None or not MemberAttribute.ARRAY_LENGTH in member_info.attributes:
            self._io.write(f"\"{member_info.schema_name}\"{self._key_separator}")

        if isinstance(value, BitBuffer):
            self._visit_bitbuffer(value, member_info)
        else:
            json_value = self._json_encoder.encode(value, member_info.type_info)
            self._io.write(json_value)

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

    def _begin_obj(self):
        self._io.write("{")

        self._is_first = True
        self._level += 1

    def _end_obj(self):
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

    def _visit_bitbuffer(self, value: typing.Any, member_info: MemberInfo):
        self._begin_obj()
        self._begin_item()
        self._io.write(f"\"buffer\"{self._key_separator}")
        json_value = self._json_encoder.encode(value.buffer, member_info.type_info)
        self._io.write(json_value)
        self._end_item()
        self._begin_item()
        self._io.write(f"\"bitSize\"{self._key_separator}")
        json_value = self._json_encoder.encode(value.bitsize, member_info.type_info)
        self._io.write(json_value)
        self._end_item()
        self._end_obj()

class JsonEncoder:
    """
    Converts zserio values to/from Json string representation.
    """

    def __init__(self):
        """
        Constructor.
        """

        self._encoder = json.JSONEncoder()

    def encode(self, value: typing.Any, type_info: typing.Union[TypeInfo, RecursiveTypeInfo]) -> str:
        """
        Encodes zserio value to Json string representation.
        """

        if TypeAttribute.ENUM_ITEMS in type_info.attributes:
            return self._encoder.encode(value.value)
        elif TypeAttribute.BITMASK_VALUES in type_info.attributes:
            return self._encoder.encode(value.value)
        elif isinstance(value, bytes):
            return self._encoder.encode(f"{value!r}")
        elif isinstance(value, str):
            return self._encoder.encode(value)
        else:
            return self._encoder.encode(value)
