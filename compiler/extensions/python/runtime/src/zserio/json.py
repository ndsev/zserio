"""
The module implements WalkObserver for writing of zserio objects to JSON format.
"""

import enum
import io
import json
import typing

from zserio.bitbuffer import BitBuffer
from zserio.creator import ZserioTreeCreator
from zserio.exception import PythonRuntimeException
from zserio.typeinfo import TypeInfo, RecursiveTypeInfo, TypeAttribute, MemberInfo
from zserio.walker import WalkObserver

class JsonEnumerableFormat(enum.Enum):
    """
    Configuration for writing of enumerable types.
    """

    #: Print as JSON integral value.
    NUMBER = enum.auto()
    #: Print as JSON string according to the following rules:
    #:
    #: #. Enums
    #:
    #:    * when an exact match with an enumerable item is found, the item name is used - e.g. "FIRST",
    #:    * when no exact match is found, it's an invalid value, the integral value is converted to string
    #:      and an appropriate comment is included - e.g. "10 /\* no match \*/".
    #:
    #: #. Bitmasks
    #:
    #:    * when an exact mach with or-ed bitmask values is found, it's used - e.g. "READ | WRITE",
    #:    * when no exact match is found, but some or-ed values match, the integral value is converted
    #:      to string and the or-ed values are included in a comment - e.g. "127 /\* READ | CREATE \*/",
    #:    * when no match is found at all, the integral value is converted to string and an appropriate
    #:      comment is included - e.g. "13 /\* no match \*/".
    STRING = enum.auto()

class JsonWriter(WalkObserver):
    """
    Walker observer which dumps zserio objects to JSON format.
    """

    def __init__(self, *, text_io: typing.Optional[typing.TextIO] = None,
                 enumerable_format: JsonEnumerableFormat = JsonEnumerableFormat.STRING,
                 item_separator: typing.Optional[str] = None,
                 key_separator: typing.Optional[str] = None,
                 indent: typing.Union[str, int] = None) -> None:
        """
        Constructor.

        :param text_io: Optional text stream for JSON output, io.StringIO is used by default.
        :param item_separator: Optional item separator, default is ', ' if indent is None, ',' otherwise.
        :param key_separator: Optional key separator, default is ': '.
        :param enumerable_format: Optional enumerable format to use, default is JsonEnumerableFormat.STRING.
        :param indent: String or (non-negative) integer defining the indent. If not None, newlines are inserted.
        """

        self._io : typing.TextIO = text_io if text_io else io.StringIO()
        self._item_separator : str = item_separator if item_separator else ("," if indent is not None else ", ")
        self._key_separator : str = key_separator if key_separator else ": "
        self._enumerable_format = enumerable_format

        self._indent : typing.Optional[str] = (
            (indent if isinstance(indent, str) else " " * indent) if indent is not None else None
        )

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
        self._io.write(f"{self._json_encoder.encode_value(key)}{self._key_separator}")

    def _write_value(self, value: typing.Any, member_info: MemberInfo) -> None:
        if value is None:
            self._io.write(self._json_encoder.encode_value(None))
            return

        if isinstance(value, BitBuffer):
            self._write_bitbuffer(value)
        else:
            type_info = member_info.type_info
            if TypeAttribute.ENUM_ITEMS in type_info.attributes:
                if self._enumerable_format == JsonEnumerableFormat.STRING:
                    self._write_stringified_enum(value, type_info)
                else:
                    self._io.write(self._json_encoder.encode_value(value.value))
            elif TypeAttribute.BITMASK_VALUES in type_info.attributes:
                if self._enumerable_format == JsonEnumerableFormat.STRING:
                    self._write_stringified_bitmask(value, type_info)
                else:
                    self._io.write(self._json_encoder.encode_value(value.value))
            else:
                self._io.write(self._json_encoder.encode_value(value))

    def _write_bitbuffer(self, value: typing.Any) -> None:
        self._begin_object()
        self._begin_item()
        self._write_key("buffer")
        self._begin_array()
        for byte in value.buffer:
            self._begin_item()
            self._io.write(self._json_encoder.encode_value(byte))
            self._end_item()
        self._end_array()
        self._end_item()
        self._begin_item()
        self._write_key("bitSize")
        self._io.write(self._json_encoder.encode_value(value.bitsize))
        self._end_item()
        self._end_object()

    def _write_stringified_enum(self, value: typing.Any,
                                type_info: typing.Union[TypeInfo, RecursiveTypeInfo]) -> typing.Any:
        for item in type_info.attributes[TypeAttribute.ENUM_ITEMS]:
            if item.py_item == value:
                # exact match
                self._io.write(self._json_encoder.encode_value(item.schema_name))
                return

        #no match
        self._io.write(self._json_encoder.encode_value(str(value.value) + " /* no match */"))

    def _write_stringified_bitmask(self, value: typing.Any,
                                  type_info: typing.Union[TypeInfo, RecursiveTypeInfo]) -> typing.Any:
        string_value = ""
        bitmask_value = value.value
        value_check = 0

        for item_info in type_info.attributes[TypeAttribute.BITMASK_VALUES]:
            is_zero = item_info.py_item.value == 0
            if ((not is_zero and (bitmask_value & item_info.py_item.value == item_info.py_item.value)) or
                (is_zero and bitmask_value == 0)):
                value_check |= item_info.py_item.value
                if string_value:
                    string_value += " | "
                string_value += item_info.schema_name

        if not string_value:
            # no match
            string_value += str(bitmask_value) + " /* no match */"
        elif bitmask_value != value_check:
            # partial match
            string_value = str(bitmask_value) + " /* partial match: " + string_value + " */"
        # else exact match

        self._io.write(self._json_encoder.encode_value(string_value))


class JsonEncoder:
    """
    Converts zserio values to Json string representation.
    """

    def __init__(self) -> None:
        """
        Constructor.
        """

        self._encoder = json.JSONEncoder(ensure_ascii=False)

    def encode_value(self, value: typing.Any) -> str:
        """
        Encodes value to JSON string representation.

        :param value: Value to encode.

        :returns: Value encoded to string as a valid JSON value.
        """

        return self._encoder.encode(value)

class JsonToken(enum.Enum):
    """
    Tokens used by Json Tokenizer.
    """

    BEGIN_OF_FILE = enum.auto()
    END_OF_FILE = enum.auto()
    BEGIN_OBJECT = enum.auto()
    END_OBJECT = enum.auto()
    BEGIN_ARRAY = enum.auto()
    END_ARRAY = enum.auto()
    KEY_SEPARATOR = enum.auto()
    ITEM_SEPARATOR = enum.auto()
    VALUE = enum.auto()

class JsonParserException(PythonRuntimeException):
    """
    Exception used to distinguish exceptions from the JsonParser.
    """

class JsonParser:
    """
    Json Parser.

    Parses the JSON on the fly and calls an observer.
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

        def visit_key(self, key: str) -> None:
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
        :param observer: Observer to use.
        """

        self._tokenizer = JsonTokenizer(text_io)
        self._observer = observer

    def parse(self) -> bool:
        """
        Parses single JSON element from the text stream.

        :returns: True when end-of-file is reached, False otherwise (i.e. another JSON element is present).
        :raises JsonParserException: When parsing fails.
        """

        if self._tokenizer.get_token() == JsonToken.BEGIN_OF_FILE:
            self._tokenizer.next()

        if self._tokenizer.get_token() == JsonToken.END_OF_FILE:
            return True

        self._parse_element()

        return self._tokenizer.get_token() == JsonToken.END_OF_FILE

    def get_line(self) -> int:
        """
        Gets current line number.

        :returns: Line number.
        """

        return self._tokenizer.get_line()

    def get_column(self) -> int:
        """
        Gets current column number.

        :returns: Column number.
        """

        return self._tokenizer.get_column()

    def _parse_element(self) -> None:
        token = self._tokenizer.get_token()
        if token == JsonToken.BEGIN_ARRAY:
            self._parse_array()
        elif token == JsonToken.BEGIN_OBJECT:
            self._parse_object()
        elif token == JsonToken.VALUE:
            self._parse_value()
        else:
            self._raise_unexpected_token(JsonParser.ELEMENT_TOKENS)

    def _parse_array(self) -> None:
        self._observer.begin_array()
        token = self._tokenizer.next()

        if token in JsonParser.ELEMENT_TOKENS:
            self._parse_elements()

        self._consume_token(JsonToken.END_ARRAY)
        self._observer.end_array()

    def _parse_elements(self) -> None:
        self._parse_element()
        while self._tokenizer.get_token() == JsonToken.ITEM_SEPARATOR:
            self._tokenizer.next()
            self._parse_element()

    def _parse_object(self) -> None:
        self._observer.begin_object()
        token = self._tokenizer.next()
        if token == JsonToken.VALUE:
            self._parse_members()

        self._consume_token(JsonToken.END_OBJECT)
        self._observer.end_object()

    def _parse_members(self) -> None:
        self._parse_member()
        while self._tokenizer.get_token() == JsonToken.ITEM_SEPARATOR:
            self._tokenizer.next()
            self._parse_member()

    def _parse_member(self) -> None:
        self._check_token(JsonToken.VALUE)
        key = self._tokenizer.get_value()
        if not isinstance(key, str):
            raise JsonParserException(f"JsonParser:{self.get_line()}:{self.get_column()}: "
                                      f"Key must be a string value!")

        self._observer.visit_key(key)
        self._tokenizer.next()

        self._consume_token(JsonToken.KEY_SEPARATOR)

        self._parse_element()

    def _parse_value(self) -> None:
        self._observer.visit_value(self._tokenizer.get_value())
        self._tokenizer.next()

    def _consume_token(self, token: JsonToken) -> None:
        self._check_token(token)
        self._tokenizer.next()

    def _check_token(self, token: JsonToken) -> None:
        if self._tokenizer.get_token() != token:
            self._raise_unexpected_token([token])

    def _raise_unexpected_token(self, expecting: typing.List[JsonToken]) -> None:
        msg = (f"JsonParser:{self.get_line()}:{self.get_column()}: "
               f"Unexpected token: {self._tokenizer.get_token()}")
        if self._tokenizer.get_value() is not None:
            msg += f" ('{self._tokenizer.get_value()}')"
        if len(expecting) == 1:
            msg += f", expecting {expecting[0]}!"
        else:
            msg += ", expecting one of [" + ", ".join([str(x) for x in expecting]) + "]!"

        raise JsonParserException(msg)

    ELEMENT_TOKENS = [JsonToken.BEGIN_OBJECT, JsonToken.BEGIN_ARRAY, JsonToken.VALUE]

class JsonDecoder:
    """
    JSON value decoder.
    """

    @staticmethod
    def decode_value(content: str, pos: int) -> 'JsonDecoder.Result':
        """
        Decodes the JSON value from the string.

        :param content: String which contains encoded JSON value.
        :param pos: Position from zero in content where the encoded JSON value begins.

        :returns: Decoder result object.
        """

        if pos >= len(content):
            return JsonDecoder.Result.from_failure()

        first_char = content[pos]
        if first_char == 'n':
            return JsonDecoder._decode_literal(content, pos, "null", None)

        if first_char == 't':
            return JsonDecoder._decode_literal(content, pos, "true", True)

        if first_char == 'f':
            return JsonDecoder._decode_literal(content, pos, "false", False)

        if first_char == 'N':
            return JsonDecoder._decode_literal(content, pos, "NaN", float("nan"))

        if first_char == 'I':
            return JsonDecoder._decode_literal(content, pos, "Infinity", float("inf"))

        if first_char == '"':
            return JsonDecoder._decode_string(content, pos)

        if first_char == '-':
            if pos + 1 >= len(content):
                return JsonDecoder.Result.from_failure(1)

            second_char = content[pos + 1]
            if second_char == 'I':
                return JsonDecoder._decode_literal(content, pos, "-Infinity", float("-inf"))

            return JsonDecoder._decode_number(content, pos)

        return JsonDecoder._decode_number(content, pos)

    class Result:
        """
        Decoder result value.
        """

        def __init__(self, success: bool, value: typing.Any, num_read_chars: int):
            """
            Constructor.
            """

            self._success = success
            self._value = value
            self._num_read_chars = num_read_chars

        @classmethod
        def from_failure(cls: typing.Type['JsonDecoder.Result'], num_read_chars:int = 0):
            """
            Creates decoder result value in case of failure.

            :param num_read_chars: Number of processed characters.
            """
            instance = cls(False, None, num_read_chars)

            return instance

        @classmethod
        def from_success(cls: typing.Type['JsonDecoder.Result'], value: typing.Any, num_read_chars:int = 0):
            """
            Creates decoder result value in case of success.

            :param value: Decoded value.
            :param num_read_chars: Number of read characters.
            """
            instance = cls(True, value, num_read_chars)

            return instance

        @property
        def success(self) -> bool:
            """
            Gets the decoder result.

            :returns: True in case of success, otherwise false.
            """

            return self._success

        @property
        def value(self) -> typing.Any:
            """
            Gets the decoded JSON value.

            :returns: Decoded JSON value or None in case of failure.
            """

            return self._value

        @property
        def num_read_chars(self) -> int:
            """
            Gets the number of read characters from the string which contains encoded JSON value.

            In case of failure, it returns the number of processed (read) characters.

            :returns: Number of read characters.
            """

            return self._num_read_chars

    @staticmethod
    def _decode_literal(content: str, pos: int, text: str, decoded_object) -> 'JsonDecoder.Result':
        text_length = len(text)
        if pos + text_length > len(content):
            return JsonDecoder.Result.from_failure(len(content) - pos)

        sub_content = content[pos : pos + text_length]
        if sub_content == text:
            return JsonDecoder.Result.from_success(decoded_object, text_length)

        return JsonDecoder.Result.from_failure(text_length)

    @staticmethod
    def _decode_string(content: str, pos: int) -> 'JsonDecoder.Result':
        decoded_string = ""
        end_of_string_pos = pos + 1 # we know that at the beginning is '"'
        while True:
            if end_of_string_pos >= len(content):
                return JsonDecoder.Result.from_failure(end_of_string_pos - pos)

            next_char = content[end_of_string_pos]
            end_of_string_pos += 1
            if next_char == '\\':
                if end_of_string_pos >= len(content):
                    return JsonDecoder.Result.from_failure(end_of_string_pos - pos)

                next_next_char = content[end_of_string_pos]
                end_of_string_pos += 1
                if next_next_char in ('\\', '"'):
                    decoded_string += next_next_char
                elif next_next_char == 'b':
                    decoded_string += '\b'
                elif next_next_char == 'f':
                    decoded_string += '\f'
                elif next_next_char == 'n':
                    decoded_string += '\n'
                elif next_next_char == 'r':
                    decoded_string += '\r'
                elif next_next_char == 't':
                    decoded_string += '\t'
                elif next_next_char == 'u': # unicode escape
                    unicode_escape_len = 4
                    end_of_string_pos += unicode_escape_len
                    if end_of_string_pos >= len(content):
                        return JsonDecoder.Result.from_failure(len(content) - pos)
                    sub_content = content[end_of_string_pos - unicode_escape_len - 2 : end_of_string_pos]
                    decoded_unicode = JsonDecoder._decode_unicode_escape(sub_content)
                    if decoded_unicode is not None:
                        decoded_string += decoded_unicode
                    else:
                        return JsonDecoder.Result.from_failure(end_of_string_pos - pos)
                else:
                    # unknown escape character, not decoded...
                    return JsonDecoder.Result.from_failure(end_of_string_pos - pos)
            elif next_char == '"':
                break
            else:
                decoded_string += next_char

        return JsonDecoder.Result.from_success(decoded_string, end_of_string_pos - pos)

    @staticmethod
    def _decode_unicode_escape(content: str) -> typing.Optional[str]:
        try:
            return bytes(content, "ascii").decode("unicode-escape")
        except ValueError:
            return None

    @staticmethod
    def _decode_number(content: str, pos: int) -> 'JsonDecoder.Result':
        number_content, is_float = JsonDecoder._extract_number(content, pos)
        number_length = len(number_content)
        if number_length == 0:
            return JsonDecoder.Result.from_failure(1)

        try:
            if is_float:
                float_number = float(number_content)
                return JsonDecoder.Result.from_success(float_number, number_length)
            else:
                int_number = int(number_content)
                return JsonDecoder.Result.from_success(int_number, number_length)
        except ValueError:
            return JsonDecoder.Result.from_failure(number_length)

    @staticmethod
    def _extract_number(content: str, pos: int) -> typing.Tuple[str, bool]:
        end_of_number_pos = pos
        if content[end_of_number_pos] == '-': # we already know that there is something after '-'
            end_of_number_pos += 1
        is_float = False
        accept_sign = False
        while end_of_number_pos < len(content):
            next_char = content[end_of_number_pos]

            if accept_sign:
                accept_sign = False
                if next_char in ('+', '-'):
                    end_of_number_pos += 1
                    continue

            if next_char.isdigit():
                end_of_number_pos += 1
                continue

            if not is_float and (next_char in ('.', 'e', 'E')):
                end_of_number_pos += 1
                is_float = True
                if next_char in ('e', 'E'):
                    accept_sign = True
                continue

            break # pragma: no cover (to satisfy test coverage)

        return content[pos:end_of_number_pos], is_float

class JsonTokenizer:
    """
    Tokenizer used by JsonParser.
    """

    def __init__(self, text_io: typing.TextIO) -> None:
        """
        Constructor.

        :param text_io: Text stream to tokenize.
        """
        self._io = text_io

        self._content = self._io.read(JsonTokenizer.MAX_LINE_LEN)
        self._line_number = 1
        self._column_number = 1
        self._token_column_number = 1
        self._pos = 0
        self._set_token(JsonToken.BEGIN_OF_FILE if self._content else JsonToken.END_OF_FILE, None)
        self._decoder_result = JsonDecoder.Result.from_failure()

    def next(self) -> JsonToken:
        """
        Moves to next token.

        :returns: Token.
        :raises JsonParserException: When unknown token is reached.
        """

        while not self._decode_next():
            new_content = self._io.read(JsonTokenizer.MAX_LINE_LEN)
            if not new_content:
                if self._token == JsonToken.END_OF_FILE:
                    self._token_column_number = self._column_number
                else:
                    # stream is finished but last token is not EOF => value must be at the end
                    self._set_token_value()

                return self._token

            self._content = self._content[self._pos:]
            self._content += new_content
            self._pos = 0

        return self._token

    def get_token(self) -> JsonToken:
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
        Gets line number of the current token.

        :returns: Line number.
        """
        return self._line_number

    def get_column(self) -> int:
        """
        Gets column number of the current token.

        :returns: Column number.
        """
        return self._token_column_number

    def _decode_next(self) -> bool:
        if not self._skip_whitespaces():
            return False

        self._token_column_number = self._column_number

        next_char = self._content[self._pos]
        if next_char == "{":
            self._set_token(JsonToken.BEGIN_OBJECT, next_char)
            self._set_position(self._pos + 1, self._column_number + 1)
        elif next_char == "}":
            self._set_token(JsonToken.END_OBJECT, next_char)
            self._set_position(self._pos + 1, self._column_number + 1)
        elif next_char == "[":
            self._set_token(JsonToken.BEGIN_ARRAY, next_char)
            self._set_position(self._pos + 1, self._column_number + 1)
        elif next_char == "]":
            self._set_token(JsonToken.END_ARRAY, next_char)
            self._set_position(self._pos + 1, self._column_number + 1)
        elif next_char == ":":
            self._set_token(JsonToken.KEY_SEPARATOR, next_char)
            self._set_position(self._pos + 1, self._column_number + 1)
        elif next_char == ",":
            self._set_token(JsonToken.ITEM_SEPARATOR, next_char)
            self._set_position(self._pos + 1, self._column_number + 1)
        else:
            self._decoder_result = JsonDecoder.decode_value(self._content, self._pos)
            if self._pos + self._decoder_result.num_read_chars >= len(self._content):
                return False # we are at the end of chunk => try to read more

            self._set_token_value()

        return True

    def _skip_whitespaces(self) -> bool:
        while True:
            if self._pos >= len(self._content):
                self._set_token(JsonToken.END_OF_FILE, None)
                return False

            next_char = self._content[self._pos]
            if next_char in (' ', '\t'):
                self._set_position(self._pos + 1, self._column_number + 1)
            elif next_char == '\n':
                self._line_number += 1
                self._set_position(self._pos + 1, 1)
            elif next_char == '\r':
                if self._pos + 1 >= len(self._content):
                    self._set_token(JsonToken.END_OF_FILE, None)
                    return False

                next_next_char = self._content[self._pos + 1]
                self._line_number += 1
                self._set_position(self._pos + (2 if (next_next_char == '\n') else 1), 1)
            else:
                return True

    def _set_token(self, new_token: JsonToken, new_value: typing.Any) -> None:
        self._token = new_token
        self._value = new_value

    def _set_position(self, new_pos: int, new_column_number: int) -> None:
        self._pos = new_pos
        self._column_number = new_column_number

    def _set_token_value(self) -> None:
        if not self._decoder_result.success:
            raise JsonParserException(f"JsonTokenizer:{self._line_number}:{self._token_column_number}: "
                                      f"Unknown token!")

        self._set_token(JsonToken.VALUE, self._decoder_result.value)
        num_read_chars = self._decoder_result.num_read_chars
        self._set_position(self._pos + num_read_chars, self._column_number + num_read_chars)

    MAX_LINE_LEN = 64 * 1024

class JsonReader:
    """
    Reads zserio object tree defined by a type info from a text stream.
    """

    def __init__(self, text_io: typing.TextIO) -> None:
        """
        Constructor.

        :param text_io: Text stream to read.
        """

        self._creator_adapter = JsonReader._CreatorAdapter()
        self._parser = JsonParser(text_io, self._creator_adapter)

    def read(self, type_info: TypeInfo, *arguments: typing.List[typing.Any]) -> typing.Any:
        """
        Reads a zserio object tree defined by the given type info from the text steam.

        :param type_info: Type info defining the expected zserio object tree.
        :param arguments: Arguments of type defining the expected zserio object tree.
        :returns: Zserio object tree initialized using the JSON data.
        :raises PythonRuntimeException: When the JSON doesn't contain expected zserio object tree.
        """

        self._creator_adapter.set_type(type_info, *arguments)

        try:
            self._parser.parse()
        except JsonParserException:
            raise
        except PythonRuntimeException as err:
            raise PythonRuntimeException(f"{err} (JsonParser:"
                                         f"{self._parser.get_line()}:{self._parser.get_column()})") from err

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

        def set_type(self, type_info: TypeInfo, *arguments: typing.List[typing.Any]) -> None:
            """
            Sets type which shall be created next. Resets the current object.

            :param type_info: Type info of the type which is to be created.
            :param arguments: Arguments of type defining the expected zserio object tree.
            """

            self._creator = ZserioTreeCreator(type_info, *arguments)
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
                        if self._creator.get_field_type(self._key_stack[-1]).py_type == BitBuffer:
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
                    expected_type_info = self._creator.get_field_type(self._key_stack[-1])
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
                if isinstance(value, str):
                    return JsonReader._CreatorAdapter._enum_from_string(value, type_info)
                else:
                    return type_info.py_type(value)
            elif TypeAttribute.BITMASK_VALUES in type_info.attributes:
                if isinstance(value, str):
                    return JsonReader._CreatorAdapter._bitmask_from_string(value, type_info)
                else:
                    return type_info.py_type.from_value(value)
            else:
                return value

        @staticmethod
        def _enum_from_string(string_value: str,
                              type_info: typing.Union[TypeInfo, RecursiveTypeInfo]) -> typing.Any:
            if string_value:
                first_char = string_value[0]
                if ('A' <= first_char <= 'Z') or ('a' <= first_char <= 'z') or first_char == '_':
                    py_item = JsonReader._CreatorAdapter._parse_enum_string_value(string_value, type_info)
                    if py_item is not None:
                        return py_item
                # else it's a no match

            raise PythonRuntimeException(f"JsonReader: Cannot create enum '{type_info.schema_name}' "
                                         f"from string value '{string_value}'!")

        @staticmethod
        def _bitmask_from_string(string_value: str,
                                 type_info: typing.Union[TypeInfo, RecursiveTypeInfo]) -> typing.Any:
            if string_value:
                first_char = string_value[0]
                if ('A' <= first_char <= 'Z') or ('a' <= first_char <= 'z') or first_char == '_':
                    value = JsonReader._CreatorAdapter._parse_bitmask_string_value(string_value, type_info)
                    if value is not None:
                        return type_info.py_type.from_value(value)
                elif '0' <= first_char <= '9': # bitmask can be only unsigned
                    value = JsonReader._CreatorAdapter._parse_bitmask_numeric_string_value(string_value)
                    if value is not None:
                        return type_info.py_type.from_value(value)

            raise PythonRuntimeException(f"JsonReader: Cannot create bitmask '{type_info.schema_name}' "
                                         f"from string value '{string_value}'!")

        @staticmethod
        def _parse_enum_string_value(string_value: str,
                                     type_info: typing.Union[TypeInfo, RecursiveTypeInfo]) -> typing.Any:
            for item_info in type_info.attributes[TypeAttribute.ENUM_ITEMS]:
                if string_value == item_info.schema_name:
                    return item_info.py_item

            return None

        @staticmethod
        def _parse_bitmask_string_value(string_value: str,
                                        type_info: typing.Union[TypeInfo, RecursiveTypeInfo]) -> typing.Any:
            value = 0
            identifiers = string_value.split('|')
            for identifier_with_spaces in identifiers:
                identifier = identifier_with_spaces.strip()
                match = False
                for item_info in type_info.attributes[TypeAttribute.BITMASK_VALUES]:
                    if identifier == item_info.schema_name:
                        match = True
                        value |= item_info.py_item.value
                        break

                if not match:
                    return None

            return value

        @staticmethod
        def _parse_bitmask_numeric_string_value(string_value: str):
            number_len = 1
            while string_value[number_len] >= '0'and string_value[number_len] <= '9':
                number_len += 1

            return int(string_value[0:number_len])
