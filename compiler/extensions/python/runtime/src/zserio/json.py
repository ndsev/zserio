"""
The module implements WalkObserver for writing of zserio objects to JSON format.
"""

import enum
import io
import json
import re
import typing

from zserio.bitbuffer import BitBuffer
from zserio.creator import ZserioTreeCreator
from zserio.exception import PythonRuntimeException
from zserio.typeinfo import TypeInfo, RecursiveTypeInfo, TypeAttribute, MemberInfo
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

class JsonParserException(PythonRuntimeException):
    """
    Exception used to distinguish exceptions from the JsonParser.
    """

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

    def __init__(self, text_io: typing.TextIO, observer: Observer) -> None:
        """
        Constructor.

        :param text_io: Text stream to parse.
        :param observer: Json reader observer.
        """

        self._tokenizer = JsonParser.Tokenizer(text_io)
        self._observer = observer

    def parse(self) -> bool:
        """
        Parses single JSON element from the text stream.

        :returns: True when end-of-file is reached, False otherwise (i.e. another JSON element is present).
        :raises JsonParserException: When parsing fails.
        """

        if self._tokenizer.get_token() == JsonParser.Token.BEGIN_OF_FILE:
            if self._tokenizer.next() == JsonParser.Token.END_OF_FILE:
                return True

        self._parse_element()

        return self._tokenizer.get_token() == JsonParser.Token.END_OF_FILE

    def get_line(self) -> int:
        """
        Gets current line number.

        :returns: Line number.
        """

        return self._tokenizer.get_line()

    def _parse_element(self) -> None:
        token = self._tokenizer.get_token()
        if token == JsonParser.Token.BEGIN_ARRAY:
            self._parse_array()
        elif token == JsonParser.Token.BEGIN_OBJECT:
            self._parse_object()
        elif token == JsonParser.Token.VALUE:
            self._observer.visit_value(self._tokenizer.get_value())
            self._tokenizer.next()
        else:
            self._raise_unexpected_token(JsonParser.ELEMENT_TOKENS)

    def _parse_object(self) -> None:
        self._consume_token(JsonParser.Token.BEGIN_OBJECT)
        self._observer.begin_object()

        if self._tokenizer.get_token() == JsonParser.Token.VALUE:
            self._parse_members()

        self._consume_token(JsonParser.Token.END_OBJECT)
        self._observer.end_object()

    def _parse_members(self) -> None:
        self._parse_member()
        while self._tokenizer.get_token() == JsonParser.Token.ITEM_SEPARATOR:
            self._tokenizer.next()
            self._parse_member()

    def _parse_member(self) -> None:
        self._check_token(JsonParser.Token.VALUE)
        key = self._tokenizer.get_value()
        if not isinstance(key, str):
            raise JsonParserException(f"JsonParser line {self.get_line()}: Key must be a string value!")
        self._observer.visit_key(key)
        self._tokenizer.next()

        self._consume_token(JsonParser.Token.KEY_SEPARATOR)

        self._parse_element()

    def _parse_array(self) -> None:
        self._consume_token(JsonParser.Token.BEGIN_ARRAY)
        self._observer.begin_array()

        if self._tokenizer.get_token() in JsonParser.ELEMENT_TOKENS:
            self._parse_elements()

        self._consume_token(JsonParser.Token.END_ARRAY)
        self._observer.end_array()

    def _parse_elements(self) -> None:
        self._parse_element()
        while self._tokenizer.get_token() == JsonParser.Token.ITEM_SEPARATOR:
            self._tokenizer.next()
            self._parse_element()

    def _check_token(self, token: 'JsonParser.Token') -> None:
        if self._tokenizer.get_token() != token:
            self._raise_unexpected_token([token])

    def _consume_token(self, token: 'JsonParser.Token') -> None:
        self._check_token(token)
        self._tokenizer.next()

    def _raise_unexpected_token(self, expecting: typing.List['JsonParser.Token']) -> None:
        msg = f"JsonParser line {self.get_line()}: Unexpected token: {self._tokenizer.get_token()}"
        if self._tokenizer.get_value() is not None:
            msg += f" ('{self._tokenizer.get_value()}')"
        if len(expecting) == 1:
            msg += f", expecting {expecting[0]}!"
        else:
            msg += ", expecting one of [" + ", ".join([str(x) for x in expecting]) + "]!"

        raise JsonParserException(msg)

    class Token(enum.Enum):
        """
        JsonParser tokens definition.
        """

        UNKNOWN = -1
        BEGIN_OF_FILE = enum.auto()
        END_OF_FILE = enum.auto()
        BEGIN_OBJECT = enum.auto()
        END_OBJECT = enum.auto()
        BEGIN_ARRAY = enum.auto()
        END_ARRAY = enum.auto()
        KEY_SEPARATOR = enum.auto()
        ITEM_SEPARATOR = enum.auto()
        VALUE = enum.auto()

    class Tokenizer:
        """
        Tokenizer used by JsonParser.
        """

        def __init__(self, text_io: typing.TextIO) -> None:
            """
            Constructor.

            :param text_io: Text stream to tokenize.
            """
            self._io = text_io

            self._decoder = json.JSONDecoder()
            self._content = self._io.readline(JsonParser.Tokenizer.MAX_LINE_LEN)
            self._line_number = 1
            self._pos = 0
            self._token = JsonParser.Token.BEGIN_OF_FILE
            self._value: typing.Any = None

        def next(self) -> 'JsonParser.Token':
            """
            Moves to next token.

            :returns: Token.
            :raises JsonParserException: When unknown token is reached.
            """

            current_line_number = self._line_number
            while not self._decode_next():
                new_content = self._io.readline(JsonParser.Tokenizer.MAX_LINE_LEN)
                if not new_content:
                    if self._token == JsonParser.Token.END_OF_FILE:
                        return self._token
                    raise JsonParserException(f"JsonParser line {current_line_number}: Unknown token: "
                                                    f"'{self._value}' ({self._token})!")
                self._line_number += 1
                self._content = self._content[self._pos:]
                self._content += new_content
                self._pos = 0

            return self._token

        def get_token(self) -> 'JsonParser.Token':
            """
            Gets current token.

            :returns: Current token.
            """
            return self._token

        def get_value(self) -> typing.Any:
            """
            Gets current value.

            :returns: Current value.
            """
            return self._value

        def get_line(self) -> int:
            """
            Gets current line number.

            :returns: Line number.
            """
            return self._line_number

        def _decode_next(self) -> bool:
            if self._pos >= len(self._content):
                self._token = JsonParser.Token.END_OF_FILE
                self._value = None
                return False
            ws_match = JsonParser.Tokenizer.WHITESPACE.match(self._content, self._pos)
            if ws_match:
                self._pos = ws_match.end()
            if self._pos >= len(self._content):
                self._token = JsonParser.Token.END_OF_FILE
                self._value = None
                return False

            next_char = self._content[self._pos]
            if next_char == "{":
                self._token = JsonParser.Token.BEGIN_OBJECT
            elif next_char == "}":
                self._token = JsonParser.Token.END_OBJECT
            elif next_char == "[":
                self._token = JsonParser.Token.BEGIN_ARRAY
            elif next_char == "]":
                self._token = JsonParser.Token.END_ARRAY
            elif next_char == ":":
                self._token = JsonParser.Token.KEY_SEPARATOR
            elif next_char == ",":
                self._token = JsonParser.Token.ITEM_SEPARATOR
            else:
                try:
                    self._value, self._pos = self._decoder.raw_decode(self._content, self._pos)
                    self._token = JsonParser.Token.VALUE
                    return True
                except json.JSONDecodeError:
                    self._token = JsonParser.Token.UNKNOWN
                    self._value = next_char
                    return False

            self._pos += 1
            self._value = next_char
            return True

        WHITESPACE = re.compile(r'[ \t\n\r]*', re.VERBOSE | re.MULTILINE | re.DOTALL) # copied from json.decoder
        MAX_LINE_LEN = 64 * 1024

    ELEMENT_TOKENS = [Token.BEGIN_OBJECT, Token.BEGIN_ARRAY, Token.VALUE]

class JsonReader:
    """
    Reads zserio object tree defined by type info from a text stream.
    """

    def __init__(self, text_io: typing.TextIO) -> None:
        """
        Constructor.

        :param text_io: Text stream to use for reading.
        """

        self._creator_adapter = JsonReader._CreatorAdapter()
        self._parser = JsonParser(text_io, self._creator_adapter)

    def read(self, type_info: TypeInfo) -> typing.Any:
        """
        Reads a zserio object tree defined by the given type_info from the text steam.

        :param type_info: Type info defining the zserio object tree.
        :returns: Zserio object tree initialized using the JSON data.
        :raises PythonRuntimeException: When the JSON doesn't contain expected zserio object tree.
        """

        self._creator_adapter.set_type(type_info)

        try:
            self._parser.parse()
        except JsonParserException:
            raise
        except PythonRuntimeException as err:
            raise PythonRuntimeException(f"{err} (JsonParser line {self._parser.get_line()})") from err

        return self._creator_adapter.get()

    class _BitBufferAdapter(JsonParser.Observer):
        """
        The adapter which allows to parse Bit Buffer object from JSON.
        """

        def __init__(self) -> None:
            """
            Constructor.
            """

            self._state = JsonReader._BitBufferAdapter._State.VISIT_KEY
            self._buffer: typing.Optional[typing.List[int]] = None
            self._bit_size: typing.Optional[int] = None

        def get(self) -> BitBuffer:
            """
            Gets the created Bit Buffer object.

            :returns: Parser Bit Buffer object.
            :raises PythonRuntimeException: In case of invalid use.
            """

            if self._buffer is None or self._bit_size is None:
                raise PythonRuntimeException("JsonReader: Unexpected end in Bit Buffer!")
            return BitBuffer(bytes(self._buffer), self._bit_size)

        def begin_object(self) -> None:
            raise PythonRuntimeException("JsonReader: Unexpected begin object in Bit Buffer!")

        def end_object(self) -> None:
            raise PythonRuntimeException("JsonReader: Unexpected end object in Bit Buffer!")

        def begin_array(self) -> None:
            if self._state == JsonReader._BitBufferAdapter._State.BEGIN_ARRAY_BUFFER:
                self._state = JsonReader._BitBufferAdapter._State.VISIT_VALUE_BUFFER
            else:
                raise PythonRuntimeException("JsonReader: Unexpected begin array in Bit Buffer!")

        def end_array(self) -> None:
            if self._state == JsonReader._BitBufferAdapter._State.VISIT_VALUE_BUFFER:
                self._state = JsonReader._BitBufferAdapter._State.VISIT_KEY
            else:
                raise PythonRuntimeException("JsonReader: Unexpected end array in Bit Buffer!")

        def visit_key(self, key: str) -> None:
            if self._state == JsonReader._BitBufferAdapter._State.VISIT_KEY:
                if key == "buffer":
                    self._state = JsonReader._BitBufferAdapter._State.BEGIN_ARRAY_BUFFER
                elif key == "bitSize":
                    self._state = JsonReader._BitBufferAdapter._State.VISIT_VALUE_BITSIZE
                else:
                    raise PythonRuntimeException(f"JsonReader: Unknown key '{key}' in Bit Buffer!")
            else:
                raise PythonRuntimeException(f"JsonReader: Unexpected key '{key}' in Bit Buffer!")

        def visit_value(self, value: typing.Any) -> None:
            if self._state == JsonReader._BitBufferAdapter._State.VISIT_VALUE_BUFFER and isinstance(value, int):
                if self._buffer is None:
                    self._buffer = [value]
                else:
                    self._buffer.append(value)
            elif (self._state == JsonReader._BitBufferAdapter._State.VISIT_VALUE_BITSIZE and
                                 isinstance(value, int)):
                self._bit_size = value
                self._state = JsonReader._BitBufferAdapter._State.VISIT_KEY
            else:
                raise PythonRuntimeException(f"JsonReader: Unexpected value '{value}' in Bit Buffer!")

        class _State(enum.Enum):
            VISIT_KEY = enum.auto()
            BEGIN_ARRAY_BUFFER = enum.auto()
            VISIT_VALUE_BUFFER = enum.auto()
            VISIT_VALUE_BITSIZE = enum.auto()

    class _CreatorAdapter(JsonParser.Observer):
        """
        The adapter which allows to use ZserioTreeCreator as an JsonReader observer.
        """

        def __init__(self) -> None:
            """
            Constructor.
            """

            self._creator: typing.Optional[ZserioTreeCreator] = None
            self._key_stack: typing.List[str] = []
            self._object: typing.Any = None
            self._bitbuffer_adapter: typing.Optional[JsonReader._BitBufferAdapter] = None

        def set_type(self, type_info: TypeInfo) -> None:
            """
            Sets type which shall be created next. Resets the current object.

            :param type_info: Type info of the type which is to be created.
            """

            self._creator = ZserioTreeCreator(type_info)
            self._object = None

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
            if self._bitbuffer_adapter:
                self._bitbuffer_adapter.begin_object()
            else:
                if not self._creator:
                    raise PythonRuntimeException("JsonReader: Adapter not initialized!")

                if not self._key_stack:
                    self._creator.begin_root()
                else:
                    if self._key_stack[-1]:
                        if self._creator.get_member_type(self._key_stack[-1]).py_type == BitBuffer:
                            self._bitbuffer_adapter = JsonReader._BitBufferAdapter()
                        else:
                            self._creator.begin_compound(self._key_stack[-1])
                    else:
                        if self._creator.get_element_type().py_type == BitBuffer:
                            self._bitbuffer_adapter = JsonReader._BitBufferAdapter()
                        else:
                            self._creator.begin_compound_element()

        def end_object(self) -> None:
            if self._bitbuffer_adapter:
                bitbuffer = self._bitbuffer_adapter.get()
                self._bitbuffer_adapter = None
                self.visit_value(bitbuffer)
            else:
                if not self._creator:
                    raise PythonRuntimeException("JsonReader: Adapter not initialized!")

                if not self._key_stack:
                    self._object = self._creator.end_root()
                    self._creator = None
                else:
                    if self._key_stack[-1]:
                        self._creator.end_compound()
                        self._key_stack.pop() # finish member
                    else:
                        self._creator.end_compound_element()

        def begin_array(self) -> None:
            if self._bitbuffer_adapter:
                self._bitbuffer_adapter.begin_array()
            else:
                if not self._creator:
                    raise PythonRuntimeException("JsonReader: Adapter not initialized!")

                if not self._key_stack:
                    raise PythonRuntimeException("JsonReader: ZserioTreeCreator expects json object!")

                self._creator.begin_array(self._key_stack[-1])

                self._key_stack.append("")

        def end_array(self) -> None:
            if self._bitbuffer_adapter:
                self._bitbuffer_adapter.end_array()
            else:
                if not self._creator:
                    raise PythonRuntimeException("JsonReader: Adapter not initialized!")

                self._creator.end_array()

                self._key_stack.pop() # finish array
                self._key_stack.pop() # finish member

        def visit_key(self, key: str) -> None:
            if self._bitbuffer_adapter:
                self._bitbuffer_adapter.visit_key(key)
            else:
                if not self._creator:
                    raise PythonRuntimeException("JsonReader: Adapter not initialized!")

                self._key_stack.append(key)

        def visit_value(self, value: typing.Any) -> None:
            if self._bitbuffer_adapter:
                self._bitbuffer_adapter.visit_value(value)
            else:
                if not self._creator:
                    raise PythonRuntimeException("JsonReader: Adapter not initialized!")

                if not self._key_stack:
                    raise PythonRuntimeException("JsonReader: ZserioTreeCreator expects json object!")

                if self._key_stack[-1]:
                    expected_type_info = self._creator.get_member_type(self._key_stack[-1])
                    self._creator.set_value(self._key_stack[-1], self._convert_value(value, expected_type_info))
                    self._key_stack.pop() # finish member
                else:
                    expected_type_info = self._creator.get_element_type()
                    self._creator.add_value_element(self._convert_value(value, expected_type_info))

        @staticmethod
        def _convert_value(value: typing.Any,
                           type_info: typing.Union[TypeInfo, RecursiveTypeInfo]) -> typing.Any:
            if value is None:
                return None

            if TypeAttribute.ENUM_ITEMS in type_info.attributes:
                return type_info.py_type(value)
            elif TypeAttribute.BITMASK_VALUES in type_info.attributes:
                return type_info.py_type.from_value(value)
            else:
                return value
