"""
The module implements WalkObserver for writing of zserio objects to JSON format.
"""

import io
import json
import re
import typing

from zserio.bitbuffer import BitBuffer
from zserio.creator import ZserioTreeCreator
from zserio.exception import PythonRuntimeException
from zserio.typeinfo import TypeInfo, TypeAttribute, MemberInfo
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
        if value is None:
            self._io.write(self._json_encoder.encode(None))
            return

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

class JsonParser:
    """
    Json Parse.

    Parses the json on the fly and calls an observer.
    """

    class Observer:
        """
        Json parser observer.
        """

        def begin_object(self) -> None:
            """
            Called when a JSON object begins - i.e. on '{'.
            """

            raise NotImplementedError()

        def end_object(self) -> None:
            """
            Called when a JSON object ends - i.e. on '}'.
            """

            raise NotImplementedError()

        def begin_array(self) -> None:
            """
            Called when a JSON array begins - i.e. on '['.
            """

            raise NotImplementedError()

        def end_array(self) -> None:
            """
            Called when a JSON array ends - i.e. on ']'.
            """

            raise NotImplementedError()

        def visit_key(self, key) -> None:
            """
            Called on a JSON key.

            :param key: Key value.
            """

            raise NotImplementedError()

        def visit_value(self, value: typing.Any) -> None:
            """
            Called on a JSON value.

            :param value: JSON value.
            """

            raise NotImplementedError()

    def __init__(self, observer: Observer) -> None:
        """
        Constructor.

        :param observer: Json reader observer.
        """

        self._observer = observer

    def parse(self, text_io: typing.TextIO):
        """
        Parses JSON text stream.

        :param text_io: Text stream to parse.
        :raises PythonRuntimeException: When parsing fails.
        """

        tokenizer = JsonParser._Tokenizer(text_io)

        if tokenizer.next() == JsonParser._Tokenizer.EOF:
            return

        self._parse_element(tokenizer)

        if tokenizer.get_token() != JsonParser._Tokenizer.EOF:
            tokenizer.unexpected()

    def _parse_object(self, tokenizer):
        if tokenizer.get_token() == JsonParser._Tokenizer.BEGIN_OBJECT:
            self._observer.begin_object()
            tokenizer.next()
            self._parse_members(tokenizer)
            if tokenizer.get_token() == JsonParser._Tokenizer.END_OBJECT:
                self._observer.end_object()
                tokenizer.next()
                return

        tokenizer.unexpected()

    def _parse_members(self, tokenizer):
        while tokenizer.get_token() == JsonParser._Tokenizer.VALUE:
            self._parse_member(tokenizer)
            if tokenizer.get_token() == JsonParser._Tokenizer.ITEM_SEPARATOR:
                tokenizer.next()
            else:
                return

    def _parse_member(self, tokenizer):
        if tokenizer.get_token() == JsonParser._Tokenizer.VALUE:
            key = tokenizer.get_value()
            if isinstance(key, str):
                self._observer.visit_key(key)
                if tokenizer.next() == JsonParser._Tokenizer.KEY_SEPARATOR:
                    tokenizer.next()
                    self._parse_element(tokenizer)
                    return

        tokenizer.unexpected()

    def _parse_element(self, tokenizer):
        token = tokenizer.get_token()
        if token == JsonParser._Tokenizer.BEGIN_ARRAY:
            self._parse_array(tokenizer)
            return
        elif token == JsonParser._Tokenizer.BEGIN_OBJECT:
            self._parse_object(tokenizer)
            return
        elif token == JsonParser._Tokenizer.VALUE:
            self._observer.visit_value(tokenizer.get_value())
            tokenizer.next()
            return

        tokenizer.unexpected()

    def _parse_array(self, tokenizer):
        if tokenizer.get_token() == JsonParser._Tokenizer.BEGIN_ARRAY:
            self._observer.begin_array()
            tokenizer.next()
            self._parse_elements(tokenizer)
            if tokenizer.get_token() == JsonParser._Tokenizer.END_ARRAY:
                self._observer.end_array()
                tokenizer.next()
                return

        tokenizer.unexpected()

    def _parse_elements(self, tokenizer):
        value_tokens = [
            JsonParser._Tokenizer.BEGIN_ARRAY,
            JsonParser._Tokenizer.BEGIN_OBJECT,
            JsonParser._Tokenizer.VALUE
        ]
        while tokenizer.get_token() in value_tokens:
            self._parse_element(tokenizer)
            if tokenizer.get_token() == JsonParser._Tokenizer.ITEM_SEPARATOR:
                tokenizer.next()
            else:
                return

    class _Tokenizer:
        def __init__(self, text_io: typing.TextIO) -> None:
            self._io = text_io

            self._decoder = json.JSONDecoder()
            self._content = self._io.readline(JsonParser._Tokenizer.MAX_LINE_LEN)
            self._line_number = 0
            self._pos = 0
            self._token = JsonParser._Tokenizer.UNKNOWN
            self._value: typing.Any = None

        def next(self) -> int:
            """
            Moves to next token.

            :returns: Token.
            :raises PythonRuntimeException: When unknown token is reached.
            """

            current_line_number = self._line_number
            while True:
                if self._decode_next():
                    return self._token
                else:
                    new_content = self._io.readline(JsonParser._Tokenizer.MAX_LINE_LEN)
                    self._line_number += 1
                    if not new_content:
                        if self._token == JsonParser._Tokenizer.EOF:
                            return self._token
                        raise PythonRuntimeException(f"{current_line_number}: Unknown token: "
                                                     f"'{self._value}' ({self._token})")

                    self._content = self._content[self._pos:]
                    self._content += new_content
                    self._pos = 0

        def unexpected(self) -> None:
            """
            Reports current token as unexpected.

            :raises PythonRuntimeException: Always.
            """

            raise PythonRuntimeException(f"{self._line_number}: Unexpected token: "
                                         f"'{self._value}' ({self._token})")

        def get_token(self) -> int:
            """
            Gets current token.

            :returns: Current token.
            """
            return self._token

        def get_value(self) -> int:
            """
            Gets current value.

            :returns: Current value.
            """
            return self._value

        def _decode_next(self) -> bool:
            if self._pos >= len(self._content):
                self._token = JsonParser._Tokenizer.EOF
                self._value = None
                return False
            ws_match = JsonParser._Tokenizer.WHITESPACE.match(self._content, self._pos)
            if ws_match:
                self._pos = ws_match.end()
            if self._pos >= len(self._content):
                self._token = JsonParser._Tokenizer.EOF
                self._value = None
                return False

            next_char = self._content[self._pos]
            if next_char == "{":
                self._token = JsonParser._Tokenizer.BEGIN_OBJECT
            elif next_char == "}":
                self._token = JsonParser._Tokenizer.END_OBJECT
            elif next_char == "[":
                self._token = JsonParser._Tokenizer.BEGIN_ARRAY
            elif next_char == "]":
                self._token = JsonParser._Tokenizer.END_ARRAY
            elif next_char == ":":
                self._token = JsonParser._Tokenizer.KEY_SEPARATOR
            elif next_char == ",":
                self._token = JsonParser._Tokenizer.ITEM_SEPARATOR
            else:
                try:
                    self._value, self._pos = self._decoder.raw_decode(self._content, self._pos)
                    self._token = JsonParser._Tokenizer.VALUE
                    return True
                except json.JSONDecodeError:
                    self._token = JsonParser._Tokenizer.UNKNOWN
                    self._value = self._content[self._pos]
                    return False

            self._pos += 1
            self._value = next_char
            return True

        UNKNOWN = -1
        EOF=0
        BEGIN_OBJECT = 1
        END_OBJECT = 2
        BEGIN_ARRAY = 3
        END_ARRAY = 4
        KEY_SEPARATOR = 5
        ITEM_SEPARATOR = 6
        VALUE = 7

        WHITESPACE = re.compile(r'[ \t\n\r]*', re.VERBOSE | re.MULTILINE | re.DOTALL) # copied from json.decoder
        MAX_LINE_LEN = 64 * 1024

class JsonReader:
    """
    Reads zserio object tree defined by type info from a text stream.
    """

    def __init__(self, type_info: TypeInfo) -> None:
        """
        Constructor.

        :param type_info: Type info defining the zserio object tree.
        """

        self._type_info = type_info

    def read(self, text_io: typing.TextIO) -> typing.Any:
        """
        Reads a zserio object tree from the given text steam, which must contain the whole tree.

        :param text_io: Text stream to use for reading.
        :returns: Zserio object tree initialized using the JSON data.
        :raises PythonRuntimeException: When the JSON doesn't contain expected zserio object tree.
        """

        creator_adapter = JsonReader._CreatorAdapter(ZserioTreeCreator(self._type_info))
        parser = JsonParser(creator_adapter)
        parser.parse(text_io)

        return creator_adapter.get()

    class _CreatorAdapter(JsonParser.Observer):
        """
        The adapter which allows to use ZserioTreeCreator as an JsonReader observer.
        """

        def __init__(self, creator: ZserioTreeCreator) -> None:
            """
            Constructor.

            :param creator: Creator to use.
            """

            self._creator = creator
            self._key_stack: typing.List[str] = []
            self._object: typing.Any = None

        def get(self) -> typing.Any:
            """
            Gets the created zserio object tree.

            :returns: Zserio object tree.
            :raises PythonRuntimeException: In case of invalid use.
            """

            if not self._object:
                raise PythonRuntimeException("JsonReader: Zserio tree not created!")

            return self._object

        def begin_object(self) -> None:
            if not self._key_stack:
                self._creator.begin_root()
            else:
                if self._key_stack[-1]:
                    self._creator.begin_compound(self._key_stack[-1])
                else:
                    self._creator.begin_compound_element()

        def end_object(self) -> None:
            if not self._key_stack:
                self._object = self._creator.end_root()
            else:
                if self._key_stack[-1]:
                    self._creator.end_compound()
                    self._key_stack.pop() # finish member
                else:
                    self._creator.end_compound_element()

        def begin_array(self) -> None:
            if not self._key_stack:
                raise PythonRuntimeException("JsonReader: ZserioTreeCreator expects json object!")

            self._creator.begin_array(self._key_stack[-1])

            self._key_stack.append("")

        def end_array(self) -> None:
            self._creator.end_array()

            self._key_stack.pop() # finish array
            self._key_stack.pop() # finish member

        def visit_key(self, key: str) -> None:
            self._key_stack.append(key)

        def visit_value(self, value: typing.Any) -> None:
            if not self._key_stack:
                raise PythonRuntimeException("JsonReader: ZserioTreeCreator expects json object!")

            if self._key_stack[-1]:
                self._creator.set_value(self._key_stack[-1], value)
                self._key_stack.pop() # finish member
            else:
                self._creator.add_value_element(value)
