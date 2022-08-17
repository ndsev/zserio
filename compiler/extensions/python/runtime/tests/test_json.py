import enum
import io
import unittest
import math


from test_creator_object import DummyBitmask, DummyEnum, DummyObject

from zserio.bitbuffer import BitBuffer
from zserio.json import (JsonWriter, JsonEncoder, JsonParser, JsonTokenizer, JsonToken, JsonParserException,
                         JsonDecoder, JsonReader, JsonEnumerableFormat)
from zserio.typeinfo import TypeInfo, MemberInfo, ItemInfo, TypeAttribute, MemberAttribute
from zserio.limits import INT64_MIN, UINT64_MAX
from zserio.exception import PythonRuntimeException

class JsonWriterTest(unittest.TestCase):

    def test_empty(self):
        json_writer = JsonWriter()
        self.assertEqual("", json_writer.get_io().getvalue())

    def test_null_value(self):
        json_writer = JsonWriter()
        json_writer.visit_value(None, MemberInfo("text", TypeInfo("string", str)))
        # note that this is not valid JSON
        self.assertEqual("\"text\": null", json_writer.get_io().getvalue())

    def test_value(self):
        json_writer = JsonWriter()
        json_writer.visit_value("test", MemberInfo("text", TypeInfo("string", str)))
        # note that this is not valid JSON
        self.assertEqual("\"text\": \"test\"", json_writer.get_io().getvalue())

    def test_enum_value(self):
        class TestEnum(enum.Enum):
            ZERO = 0
            ONE = 1
            MINUS_ONE = -1
            NOT_IN_TYPE_INFO = 2

        test_enum_type_info = TypeInfo("TestEnum", TestEnum, attributes={
            TypeAttribute.ENUM_ITEMS : [
                ItemInfo("ZERO", TestEnum.ZERO),
                ItemInfo("One", TestEnum.ONE),
                ItemInfo("MINUS_ONE", TestEnum.MINUS_ONE)
            ]
        })

        test_enum_member_info = MemberInfo("enumField", test_enum_type_info)

        json_writer = JsonWriter()
        json_writer.visit_value(TestEnum.ZERO, test_enum_member_info)
        json_writer.visit_value(TestEnum.ONE, test_enum_member_info)
        json_writer.visit_value(TestEnum.NOT_IN_TYPE_INFO, test_enum_member_info)
        json_writer.visit_value(TestEnum.MINUS_ONE, test_enum_member_info)
        # note that this is not valid JSON
        self.assertEqual("\"enumField\": \"ZERO\", "
                         "\"enumField\": \"One\", "
                         "\"enumField\": \"2 /* no match */\", "
                         "\"enumField\": \"MINUS_ONE\"",
                         json_writer.get_io().getvalue())

        json_writer = JsonWriter(enumerable_format=JsonEnumerableFormat.NUMBER)
        json_writer.visit_value(TestEnum.ZERO, test_enum_member_info)
        json_writer.visit_value(TestEnum.NOT_IN_TYPE_INFO, test_enum_member_info)
        json_writer.visit_value(TestEnum.MINUS_ONE, test_enum_member_info)
        # note that this is not valid JSON
        self.assertEqual("\"enumField\": 0, \"enumField\": 2, \"enumField\": -1",
                         json_writer.get_io().getvalue())

    def test_bitmask_value(self):
        class TestBitmask:
            def __init__(self, value):
                self._value = value

            @property
            def value(self):
                return self._value

            class Values:
                ZERO = None
                ONE = None
                TWO = None

        TestBitmask.Values.ZERO = TestBitmask(0)
        TestBitmask.Values.ONE = TestBitmask(1)
        TestBitmask.Values.TWO = TestBitmask(2)

        test_bitmask_type_info = TypeInfo("TestBitmask", TestBitmask, attributes={
            TypeAttribute.BITMASK_VALUES : [
                ItemInfo("ZERO", TestBitmask.Values.ZERO),
                ItemInfo("One", TestBitmask.Values.ONE),
                ItemInfo("TWO", TestBitmask.Values.TWO)
            ]
        })

        test_bitmask_member_info = MemberInfo("bitmaskField", test_bitmask_type_info)

        json_writer = JsonWriter()
        json_writer.visit_value(TestBitmask(0), test_bitmask_member_info)
        json_writer.visit_value(TestBitmask(2), test_bitmask_member_info)
        json_writer.visit_value(TestBitmask(3), test_bitmask_member_info)
        json_writer.visit_value(TestBitmask(4), test_bitmask_member_info)
        json_writer.visit_value(TestBitmask(7), test_bitmask_member_info)
        # note that this is not valid JSON
        self.assertEqual("\"bitmaskField\": \"ZERO\", "
                         "\"bitmaskField\": \"TWO\", "
                         "\"bitmaskField\": \"One | TWO\", "
                         "\"bitmaskField\": \"4 /* no match */\", "
                         "\"bitmaskField\": \"7 /* One | TWO */\"",
                         json_writer.get_io().getvalue())

        json_writer = JsonWriter(enumerable_format=JsonEnumerableFormat.NUMBER)
        json_writer.visit_value(TestBitmask(0), test_bitmask_member_info)
        json_writer.visit_value(TestBitmask(7), test_bitmask_member_info)
        # note that this is not valid JSON
        self.assertEqual("\"bitmaskField\": 0, \"bitmaskField\": 7", json_writer.get_io().getvalue())

    def test_compound(self):
        json_writer = JsonWriter()
        json_writer.begin_root(object())
        json_writer.visit_value(13, MemberInfo("identifier", TypeInfo("uint32", int)))
        json_writer.visit_value("test", MemberInfo("text", TypeInfo("string", str)))
        json_writer.visit_value(BitBuffer(bytes([0xFF,0x1F]), 13),
                                MemberInfo("data", TypeInfo("extern", BitBuffer)))
        json_writer.end_root(object())
        self.assertEqual(
            "{\"identifier\": 13, \"text\": \"test\", \"data\": {\"buffer\": [255, 31], \"bitSize\": 13}}",
            json_writer.get_io().getvalue())

    def test_nested_compound(self):
        json_writer = JsonWriter()
        self._walk_nested(json_writer)
        self.assertEqual("{\"identifier\": 13, \"nested\": {\"text\": \"test\"}}",
                         json_writer.get_io().getvalue())

    def test_array(self):
        json_writer = JsonWriter()
        self._walk_array(json_writer)
        self.assertEqual("{\"array\": [1, 2]}", json_writer.get_io().getvalue())

    def test_array_with_indent(self):
        json_writer = JsonWriter(indent=2)
        self._walk_array(json_writer)
        self.assertEqual("{\n  \"array\": [\n    1,\n    2\n  ]\n}", json_writer.get_io().getvalue())

    def test_empty_indent(self):
        json_writer = JsonWriter(indent="")
        self._walk_nested(json_writer)
        self.assertEqual("{\n\"identifier\": 13,\n\"nested\": {\n\"text\": \"test\"\n}\n}",
                         json_writer.get_io().getvalue())

    def test_str_indent(self):
        json_writer = JsonWriter(indent="  ")
        self._walk_nested(json_writer)
        self.assertEqual("{\n  \"identifier\": 13,\n  \"nested\": {\n    \"text\": \"test\"\n  }\n}",
                         json_writer.get_io().getvalue())

    def test_int_indent(self):
        json_writer = JsonWriter(indent=2)
        self._walk_nested(json_writer)
        self.assertEqual("{\n  \"identifier\": 13,\n  \"nested\": {\n    \"text\": \"test\"\n  }\n}",
                         json_writer.get_io().getvalue())

    def test_compact_separators(self):
        json_writer = JsonWriter(key_separator=":", item_separator=",")
        self._walk_nested(json_writer)
        self.assertEqual("{\"identifier\":13,\"nested\":{\"text\":\"test\"}}",
                         json_writer.get_io().getvalue())

    def test_multiple_objects(self):
        json_writer = JsonWriter()
        self._walk_nested(json_writer)
        self._walk_nested(json_writer)
        self.assertEqual(
            "{\"identifier\": 13, \"nested\": {\"text\": \"test\"}}"
            "{\"identifier\": 13, \"nested\": {\"text\": \"test\"}}",
            json_writer.get_io().getvalue()
        )

    @staticmethod
    def _walk_nested(json_writer):
        dummy_type_info = TypeInfo("Dummy", object)

        json_writer.begin_root(object())
        json_writer.visit_value(13, MemberInfo("identifier", TypeInfo("uint32", int)))
        json_writer.begin_compound(object(), MemberInfo("nested", dummy_type_info))
        json_writer.visit_value("test", MemberInfo("text", TypeInfo("string", str)))
        json_writer.end_compound(object(), MemberInfo("nested", dummy_type_info))
        json_writer.end_root(object())

    @staticmethod
    def _walk_array(json_writer):
        array_member_info = MemberInfo("array", TypeInfo("uint32", int), attributes={
            MemberAttribute.ARRAY_LENGTH : None
        })

        json_writer.begin_root(object())
        json_writer.begin_array([1, 2], array_member_info)
        json_writer.visit_value(1, array_member_info, 0)
        json_writer.visit_value(2, array_member_info, 1)
        json_writer.end_array([1, 2], array_member_info)
        json_writer.end_root(object())

class JsonEncoderTest(unittest.TestCase):

    def test_encode_null(self):
        json_encoder = JsonEncoder()
        self.assertEqual("null", json_encoder.encode_value(None))

    def test_encode_bool(self):
        json_encoder = JsonEncoder()
        self.assertEqual("true", json_encoder.encode_value(True))
        self.assertEqual("false", json_encoder.encode_value(False))

    def test_encode_integral(self):
        json_encoder = JsonEncoder()
        self.assertEqual("-9223372036854775808", json_encoder.encode_value(INT64_MIN))
        self.assertEqual("-1000", json_encoder.encode_value(-1000))
        self.assertEqual("0", json_encoder.encode_value(0))
        self.assertEqual("1000", json_encoder.encode_value(1000))
        self.assertEqual("18446744073709551615", json_encoder.encode_value(UINT64_MAX))

    def test_encode_floating_point(self):
        json_encoder = JsonEncoder()
        self.assertEqual("-1.0", json_encoder.encode_value(-1.0))
        self.assertEqual("0.0", json_encoder.encode_value(0.0))
        self.assertEqual("1.0", json_encoder.encode_value(1.0))

        self.assertEqual("3.5", json_encoder.encode_value(3.5))
        self.assertEqual("9.875", json_encoder.encode_value(9.875))
        self.assertEqual("0.6171875", json_encoder.encode_value(0.6171875))

        self.assertEqual("1e+20", json_encoder.encode_value(1e20))

        self.assertEqual("NaN", json_encoder.encode_value(float('nan')))
        self.assertEqual("Infinity", json_encoder.encode_value(float('inf')))
        self.assertEqual("-Infinity", json_encoder.encode_value(float('-inf')))

    def test_encode_string(self):
        json_encoder = JsonEncoder()
        self.assertEqual("\"\"", json_encoder.encode_value(""))
        self.assertEqual("\"test\"", json_encoder.encode_value("test"))
        self.assertEqual("\"München\"", json_encoder.encode_value("München"))
        self.assertEqual("\"€\"", json_encoder.encode_value("€"))

        # escapes
        self.assertEqual("\"\\\\\"", json_encoder.encode_value("\\"))
        self.assertEqual("\"\\\"\"", json_encoder.encode_value("\""))
        self.assertEqual("\"\\b\"", json_encoder.encode_value("\b"))
        self.assertEqual("\"\\f\"", json_encoder.encode_value("\f"))
        self.assertEqual("\"\\n\"", json_encoder.encode_value("\n"))
        self.assertEqual("\"\\r\"", json_encoder.encode_value("\r"))
        self.assertEqual("\"\\t\"", json_encoder.encode_value("\t"))

        self.assertEqual("\"\\n\\t%^@(*aAzZ01234569$%^!?<>[]](){}-=+~:;/|\\\\\\\"'Hello World2\"",
                         json_encoder.encode_value(
                             "\n\t%^@(*aAzZ01234569$%^!?<>[]](){}-=+~:;/|\\\"'Hello World2"))

        # <= 0x1F -> unicode escape
        self.assertEqual("\"\\u001f\"", json_encoder.encode_value("\x1f"))

class JsonParserTest(unittest.TestCase):

    class DummyObserver(JsonParser.Observer):
        def __init__(self):
            self._report = []

        @property
        def report(self):
            return self._report

        def begin_object(self):
            self._report.append("begin_object")

        def end_object(self):
            self._report.append("end_object")

        def begin_array(self):
            self._report.append("begin_array")

        def end_array(self):
            self._report.append("end_array")

        def visit_key(self, key):
            self._report.append(f"visit_key: {key}")

        def visit_value(self, value):
            self._report.append(f"visit_value: {value}")

    def test_json_parser_observer(self):
        observer = JsonParser.Observer()
        with self.assertRaises(NotImplementedError):
            observer.begin_object()
        with self.assertRaises(NotImplementedError):
            observer.end_object()
        with self.assertRaises(NotImplementedError):
            observer.begin_array()
        with self.assertRaises(NotImplementedError):
            observer.end_array()
        with self.assertRaises(NotImplementedError):
            observer.visit_key("key")
        with self.assertRaises(NotImplementedError):
            observer.visit_value(13)

    def test_json_parser_tokenizer(self):
        text_io = io.StringIO("{\"array\":\n[\n{\"key\":\n10}]}")
        tokenizer = JsonTokenizer(text_io)
        token = tokenizer.next()
        tokens = []
        while token not in [JsonToken.END_OF_FILE]:
            tokens.append((token, tokenizer.get_value()))
            token = tokenizer.next()

        self.assertEqual(
            [
                (JsonToken.BEGIN_OBJECT, "{"),
                (JsonToken.VALUE, "array"),
                (JsonToken.KEY_SEPARATOR, ":"),
                (JsonToken.BEGIN_ARRAY, "["),
                (JsonToken.BEGIN_OBJECT, "{"),
                (JsonToken.VALUE, "key"),
                (JsonToken.KEY_SEPARATOR, ":"),
                (JsonToken.VALUE, 10),
                (JsonToken.END_OBJECT, "}"),
                (JsonToken.END_ARRAY, "]"),
                (JsonToken.END_OBJECT, "}")
            ], tokens
        )

    def test_empty(self):
        text_io = io.StringIO("")
        observer = JsonParserTest.DummyObserver()
        json_parser = JsonParser(text_io, observer)
        self.assertTrue(json_parser.parse())
        self.assertEqual([], observer.report)

    def test_one_string(self):
        text_io = io.StringIO("\"text\"")
        observer = JsonParserTest.DummyObserver()
        json_parser = JsonParser(text_io, observer)
        self.assertTrue(json_parser.parse())
        self.assertEqual(["visit_value: text"], observer.report)

    def test_two_strings(self):
        text_io = io.StringIO("\"text\"\"second\"")
        observer = JsonParserTest.DummyObserver()
        json_parser = JsonParser(text_io, observer)
        self.assertFalse(json_parser.parse())
        self.assertEqual(["visit_value: text"], observer.report)
        self.assertTrue(json_parser.parse())
        self.assertEqual(["visit_value: text", "visit_value: second"], observer.report)

    def test_parse(self):
        text_io = io.StringIO("{\"array\":\n[\n{\"key1\":\n10, \"key2\":\n\"text\"}, {}]}")
        observer = JsonParserTest.DummyObserver()
        json_parser = JsonParser(text_io, observer)
        self.assertTrue(json_parser.parse())

        self.assertEqual([
            "begin_object",
            "visit_key: array",
            "begin_array",
            "begin_object",
            "visit_key: key1",
            "visit_value: 10",
            "visit_key: key2",
            "visit_value: text",
            "end_object",
            "begin_object",
            "end_object",
            "end_array",
            "end_object"
        ], observer.report)

    def test_unexpected_object(self):
        text_io = io.StringIO("{\n\n{\n\n")
        observer = JsonParserTest.DummyObserver()
        json_parser = JsonParser(text_io, observer)
        with self.assertRaises(JsonParserException) as error:
            json_parser.parse()

        self.assertEqual("JsonParser:3:1: Unexpected token: JsonToken.BEGIN_OBJECT ('{'), "
                         "expecting JsonToken.END_OBJECT!", str(error.exception))

        self.assertEqual(["begin_object"], observer.report)

    def test_unexpected_object_after_item_separator(self):
        text_io = io.StringIO("{\n  \"key\": 10,\n  {\n")
        observer = JsonParserTest.DummyObserver()
        json_parser = JsonParser(text_io, observer)
        with self.assertRaises(JsonParserException) as error:
            json_parser.parse()

        self.assertEqual("JsonParser:3:3: Unexpected token: JsonToken.BEGIN_OBJECT ('{'), "
                         "expecting JsonToken.VALUE!", str(error.exception))

        self.assertEqual(["begin_object", "visit_key: key", "visit_value: 10"], observer.report)

    def test_missing_object_item_separator(self):
        text_io = io.StringIO("{\n\"item1\":\"text\"\n\"item2\":\"text\"\n}")
        observer = JsonParserTest.DummyObserver()
        json_parser = JsonParser(text_io, observer)
        with self.assertRaises(JsonParserException) as error:
            json_parser.parse()

        self.assertEqual("JsonParser:3:1: Unexpected token: JsonToken.VALUE ('item2'), "
                         "expecting JsonToken.END_OBJECT!", str(error.exception))

        self.assertEqual([
            "begin_object",
            "visit_key: item1", "visit_value: text"
        ], observer.report)

    def test_wrong_key_type(self):
        text_io = io.StringIO("{\n10:\"text\"\n}")
        observer = JsonParserTest.DummyObserver()
        json_parser = JsonParser(text_io, observer)
        with self.assertRaises(JsonParserException) as error:
            json_parser.parse()

        self.assertEqual("JsonParser:2:1: Key must be a string value!", str(error.exception))

        self.assertEqual(["begin_object"], observer.report)

    def test_unexpected_element_token(self):
        text_io = io.StringIO("{\n\"item\":}")
        observer = JsonParserTest.DummyObserver()
        json_parser = JsonParser(text_io, observer)
        with self.assertRaises(JsonParserException) as error:
            json_parser.parse()

        self.assertEqual("JsonParser:2:8: Unexpected token: JsonToken.END_OBJECT ('}'), "
                         "expecting one of [JsonToken.BEGIN_OBJECT, JsonToken.BEGIN_ARRAY, JsonToken.VALUE]!",
                         str(error.exception))

        self.assertEqual(["begin_object", "visit_key: item"], observer.report)

    def test_missing_array_element_separator(self):
        text_io = io.StringIO("{\n\"array\":\n[10\n20\n]}")
        observer = JsonParserTest.DummyObserver()
        json_parser = JsonParser(text_io, observer)
        with self.assertRaises(JsonParserException) as error:
            json_parser.parse()

        self.assertEqual("JsonParser:4:1: Unexpected token: JsonToken.VALUE ('20'), "
                         "expecting JsonToken.END_ARRAY!", str(error.exception))

        self.assertEqual([
            "begin_object",
            "visit_key: array",
            "begin_array",
            "visit_value: 10"
        ], observer.report)

class JsonDecoderTest(unittest.TestCase):

    def test_decode_null(self):
        self._check_decoder_success("null", 0, 4, None)
        self._check_decoder_success("{ } null", 4, 4, None)
        self._check_decoder_success("null { }", 0, 4, None)
        self._check_decoder_failure("invalid", 0, 1)
        self._check_decoder_failure("invalid", 1, 4)
        self._check_decoder_failure("nul", 0, 3)

    def test_decode_true(self):
        self._check_decoder_success("true", 0, 4, True)
        self._check_decoder_success("{ } true", 4, 4, True)
        self._check_decoder_success("true { }", 0, 4, True)
        self._check_decoder_failure("invalid", 0, 1)
        self._check_decoder_failure("stainless", 1, 4)
        self._check_decoder_failure("tru", 0, 3)

    def test_decode_false(self):
        self._check_decoder_success("false", 0, 5, False)
        self._check_decoder_success("{ } false", 4, 5, False)
        self._check_decoder_success("false { }", 0, 5, False)
        self._check_decoder_failure("invalid", 0, 1)
        self._check_decoder_failure("affected", 1, 5)
        self._check_decoder_failure("fal", 0, 3)

    def test_decode_nan(self):
        self._check_decoder_success("NaN", 0, 3, float("nan"))
        self._check_decoder_success("{ } NaN", 4, 3, float("nan"))
        self._check_decoder_success("NaN { }", 0, 3, float("nan"))
        self._check_decoder_failure("invalid", 0, 1)
        self._check_decoder_failure("iNactive", 1, 3)
        self._check_decoder_failure("Na", 0, 2)

    def test_decode_positive_infinity(self):
        self._check_decoder_success("Infinity", 0, 8, float("inf"))
        self._check_decoder_success("{ } Infinity", 4, 8, float("inf"))
        self._check_decoder_success("Infinity { }", 0, 8, float("inf"))
        self._check_decoder_failure("invalid", 0, 1)
        self._check_decoder_failure("iInfinvalid", 1, 8)
        self._check_decoder_failure("Infin", 0, 5)

    def test_decode_negative_infinity(self):
        self._check_decoder_success("-Infinity", 0, 9, float("-inf"))
        self._check_decoder_success("{ } -Infinity", 4, 9, float("-inf"))
        self._check_decoder_success("-Infinity { }", 0, 9, float("-inf"))
        self._check_decoder_failure("invalid", 0, 1)
        self._check_decoder_failure("i-Infinvalid", 1, 9)
        self._check_decoder_failure("-Infin", 0, 6)
        self._check_decoder_failure("-Infix", 0, 6)

    def test_decode_signed_integral(self):
        self._check_decoder_success("-0", 0, 2, 0)
        self._check_decoder_success("{ } -0", 4, 2, 0)
        self._check_decoder_success("-0 { }", 0, 2, 0)
        self._check_decoder_success("-1", 0, 2, -1)
        self._check_decoder_success("-9223372036854775808", 0, 20, -9223372036854775808)

        self._check_decoder_failure("--10", 0, 1)
        self._check_decoder_failure("-", 0, 1)

    def test_decode_unsigned_integral(self):
        self._check_decoder_success("0", 0, 1, 0)
        self._check_decoder_success("{ } 0", 4, 1, 0)
        self._check_decoder_success("0 { }", 0, 1, 0)
        self._check_decoder_success("1", 0, 1, 1)
        self._check_decoder_success("9223372036854775807", 0, 19, 9223372036854775807)
        self._check_decoder_success("18446744073709551615", 0, 20, 18446744073709551615)

        self._check_decoder_failure("+10", 0, 1)

    def test_decode_double(self):
        self._check_decoder_success("0.0", 0, 3, 0.0)
        self._check_decoder_success("{ } 0.0", 4, 3, 0.0)
        self._check_decoder_success("0.0 { }", 0, 3, 0.0)
        self._check_decoder_success("-1.0", 0, 4, -1.0)
        self._check_decoder_success("1.0", 0, 3, 1.0)
        self._check_decoder_success("3.5", 0, 3, 3.5)
        self._check_decoder_success("9.875", 0, 5, 9.875)
        self._check_decoder_success("0.6171875", 0, 9, 0.6171875)

        self._check_decoder_success("1e+20", 0, 5, 1e+20)
        self._check_decoder_success("1E+20", 0, 5, 1E+20)
        self._check_decoder_success("1e-20", 0, 5, 1e-20)
        self._check_decoder_success("1E-20", 0, 5, 1E-20)
        self._check_decoder_success("-1e+20", 0, 6, -1e+20)
        self._check_decoder_success("-1E+20", 0, 6, -1E+20)
        self._check_decoder_success("-1e-20", 0, 6, -1e-20)
        self._check_decoder_success("-1E-20", 0, 6, -1E-20)

        self._check_decoder_failure("1EE20", 0, 2)
        self._check_decoder_failure("1E++20", 0, 3)

        self._check_decoder_failure("1e", 0, 2)
        self._check_decoder_failure("1e+", 0, 3)
        self._check_decoder_failure("1E-", 0, 3)

    def test_decode_string(self):
        self._check_decoder_success("\"\"", 0, 2, "")
        self._check_decoder_success("{ } \"\"", 4, 2, "")
        self._check_decoder_success("\"\" { }", 0, 2, "")

        self._check_decoder_success("\"test\"", 0, 6, "test")
        self._check_decoder_success("\"München\"", 0, 9, "München")
        self._check_decoder_success("\"€\"", 0, 3, "€")

        # escapes
        self._check_decoder_success("\"\\\\\"", 0, 4, "\\")
        self._check_decoder_success("\"\\\"\"", 0, 4, "\"")
        self._check_decoder_success("\"\\b\"", 0, 4, "\b")
        self._check_decoder_success("\"\\f\"", 0, 4, "\f")
        self._check_decoder_success("\"\\n\"", 0, 4, "\n")
        self._check_decoder_success("\"\\r\"", 0, 4, "\r")
        self._check_decoder_success("\"\\t\"", 0, 4, "\t")

        self._check_decoder_success("\"\\n\\t%^@(*aAzZ01234569$%^!?<>[]](){}-=+~:;/|\\\\\\\"'Hello World2\"",
                                    0, 62, "\n\t%^@(*aAzZ01234569$%^!?<>[]](){}-=+~:;/|\\\"\'Hello World2")

        # <= 0x1F -> unicode escape
        self._check_decoder_success("\"\\u001f\"", 0, 8, "\u001f")

        self._check_decoder_failure("\"\\u001x\"", 0, 7)
        self._check_decoder_failure("\"unterminated", 0, 13)
        self._check_decoder_failure("\"wrong escape \\", 0, 15)
        self._check_decoder_failure("\"wrong unicode escape \\u0", 0, 25)
        self._check_decoder_failure("\"unknown escape \\x", 0, 18)

    def test_wrong_arguments(self):
        self._check_decoder_failure("", 1, 0)

    def _check_decoder_success(self, content, pos, expected_num_read, expected_value):
        result = JsonDecoder.decode_value(content, pos)
        self.assertEqual(True, result.success)
        if isinstance(expected_value, float) and math.isnan(expected_value):
            self.assertTrue(math.isnan(result.value))
        else:
            self.assertEqual(expected_value, result.value)
        self.assertEqual(expected_num_read, result.num_read_chars)

    def _check_decoder_failure(self, content, pos, expected_num_read):
        result = JsonDecoder.decode_value(content, pos)
        self.assertEqual(False, result.success)
        self.assertEqual(None, result.value)
        self.assertEqual(expected_num_read, result.num_read_chars)

class JsonTokenizerTest(unittest.TestCase):

    def test_tokens(self):
        text_io = io.StringIO("{\"array\":\n[\n{\"key\":\n10}]}")
        tokenizer = JsonTokenizer(text_io)

        self.assertEqual(JsonToken.BEGIN_OBJECT, tokenizer.next())
        self.assertEqual('{', tokenizer.get_value())
        self.assertEqual(JsonToken.VALUE, tokenizer.next())
        self.assertEqual("array", tokenizer.get_value())
        self.assertEqual(JsonToken.KEY_SEPARATOR, tokenizer.next())
        self.assertEqual(':', tokenizer.get_value())
        self.assertEqual(JsonToken.BEGIN_ARRAY, tokenizer.next())
        self.assertEqual('[', tokenizer.get_value())
        self.assertEqual(JsonToken.BEGIN_OBJECT, tokenizer.next())
        self.assertEqual('{', tokenizer.get_value())
        self.assertEqual(JsonToken.VALUE, tokenizer.next())
        self.assertEqual("key", tokenizer.get_value())
        self.assertEqual(JsonToken.KEY_SEPARATOR, tokenizer.next())
        self.assertEqual(':', tokenizer.get_value())
        self.assertEqual(JsonToken.VALUE, tokenizer.next())
        self.assertEqual(10, tokenizer.get_value())
        self.assertEqual(JsonToken.END_OBJECT, tokenizer.next())
        self.assertEqual('}', tokenizer.get_value())
        self.assertEqual(JsonToken.END_ARRAY, tokenizer.next())
        self.assertEqual(']', tokenizer.get_value())
        self.assertEqual(JsonToken.END_OBJECT, tokenizer.next())
        self.assertEqual('}', tokenizer.get_value())
        self.assertEqual(JsonToken.END_OF_FILE, tokenizer.next())

    def test_line_column(self):
        text_io = io.StringIO("\n{\r   \"key\"  \n :\n10}\r")
        tokenizer = JsonTokenizer(text_io)

        self.assertEqual(JsonToken.BEGIN_OBJECT, tokenizer.next())
        self.assertEqual('{', tokenizer.get_value())
        self.assertEqual(2, tokenizer.get_line())
        self.assertEqual(1, tokenizer.get_column())

        self.assertEqual(JsonToken.VALUE, tokenizer.next())
        self.assertEqual("key", tokenizer.get_value())
        self.assertEqual(3, tokenizer.get_line())
        self.assertEqual(4, tokenizer.get_column())

        self.assertEqual(JsonToken.KEY_SEPARATOR, tokenizer.next())
        self.assertEqual(':', tokenizer.get_value())
        self.assertEqual(4, tokenizer.get_line())
        self.assertEqual(2, tokenizer.get_column())

        self.assertEqual(JsonToken.VALUE, tokenizer.next())
        self.assertEqual(10, tokenizer.get_value())
        self.assertEqual(5, tokenizer.get_line())
        self.assertEqual(1, tokenizer.get_column())

        self.assertEqual(JsonToken.END_OBJECT, tokenizer.next())
        self.assertEqual('}', tokenizer.get_value())
        self.assertEqual(5, tokenizer.get_line())
        self.assertEqual(3, tokenizer.get_column())

        self.assertEqual(JsonToken.END_OF_FILE, tokenizer.next())
        self.assertEqual(5, tokenizer.get_line())
        self.assertEqual(4, tokenizer.get_column())

    def test_long_input_split_in_number(self):
        json_string = ""
        json_string += "{\n" # 2 chars
        k = 4000
        for i in range(k): # 20 x 4000 > 65534 to check reading by chunks
            # BUFFER_SIZE is 65536, thus 65534 % 20 gives position within the string below
            # where the buffer will be split => 14, which is somewhere in the middle of the number
            #             |->            <-|
            json_string += "  \"key\": 100000000,\n" # 20 chars
        json_string += "  \"key\": 100000000\n"
        json_string += "}"

        text_io = io.StringIO(json_string)
        tokenizer = JsonTokenizer(text_io)

        self.assertEqual(JsonToken.BEGIN_OBJECT, tokenizer.next())
        self.assertEqual('{', tokenizer.get_value())
        self.assertEqual(1, tokenizer.get_line())
        self.assertEqual(1, tokenizer.get_column())

        for i in range(k):
            self.assertEqual(JsonToken.VALUE, tokenizer.next())
            self.assertEqual("key", tokenizer.get_value())
            self.assertEqual(1 + i + 1, tokenizer.get_line())
            self.assertEqual(3, tokenizer.get_column())

            self.assertEqual(JsonToken.KEY_SEPARATOR, tokenizer.next())
            self.assertEqual(':', tokenizer.get_value())
            self.assertEqual(1 + i + 1, tokenizer.get_line())
            self.assertEqual(8, tokenizer.get_column())

            self.assertEqual(JsonToken.VALUE, tokenizer.next())
            self.assertEqual(100000000, tokenizer.get_value())
            self.assertEqual(1 + i + 1, tokenizer.get_line())
            self.assertEqual(10, tokenizer.get_column())

            self.assertEqual(JsonToken.ITEM_SEPARATOR, tokenizer.next())
            self.assertEqual(',', tokenizer.get_value())
            self.assertEqual(1 + i + 1, tokenizer.get_line())
            self.assertEqual(19, tokenizer.get_column())

        self.assertEqual(JsonToken.VALUE, tokenizer.next())
        self.assertEqual("key", tokenizer.get_value())
        self.assertEqual(1 + k + 1, tokenizer.get_line())
        self.assertEqual(3, tokenizer.get_column())

        self.assertEqual(JsonToken.KEY_SEPARATOR, tokenizer.next())
        self.assertEqual(':', tokenizer.get_value())
        self.assertEqual(1 + k + 1, tokenizer.get_line())
        self.assertEqual(8, tokenizer.get_column())

        self.assertEqual(JsonToken.VALUE, tokenizer.next())
        self.assertEqual(100000000, tokenizer.get_value())
        self.assertEqual(1 + k + 1, tokenizer.get_line())
        self.assertEqual(10, tokenizer.get_column())

        self.assertEqual(JsonToken.END_OBJECT, tokenizer.next())
        self.assertEqual(1 + k + 2, tokenizer.get_line())
        self.assertEqual(1, tokenizer.get_column())

    def test_long_input_split_in_string(self):
        json_string = ""
        json_string += "{\n" # 2 chars
        k = 4000
        for i in range(k): # 20 x 4000 > 65534 to check reading by chunks
            # BUFFER_SIZE is 65536, thus 65534 % 20 gives position within the string below
            # where the buffer will be split => 14, which is somewhere in the middle of the number
            #             |->             <-|
            json_string += "  \"key\": \"1000000\",\n" # 20 chars
        json_string += "  \"key\": \"1000000\"\n"
        json_string += "}"

        text_io = io.StringIO(json_string)
        tokenizer = JsonTokenizer(text_io)

        self.assertEqual(JsonToken.BEGIN_OBJECT, tokenizer.next())
        self.assertEqual('{', tokenizer.get_value())
        self.assertEqual(1, tokenizer.get_line())
        self.assertEqual(1, tokenizer.get_column())

        for i in range(k):
            self.assertEqual(JsonToken.VALUE, tokenizer.next())
            self.assertEqual("key", tokenizer.get_value())
            self.assertEqual(1 + i + 1, tokenizer.get_line())
            self.assertEqual(3, tokenizer.get_column())

            self.assertEqual(JsonToken.KEY_SEPARATOR, tokenizer.next())
            self.assertEqual(':', tokenizer.get_value())
            self.assertEqual(1 + i + 1, tokenizer.get_line())
            self.assertEqual(8, tokenizer.get_column())

            self.assertEqual(JsonToken.VALUE, tokenizer.next())
            self.assertEqual("1000000", tokenizer.get_value())
            self.assertEqual(1 + i + 1, tokenizer.get_line())
            self.assertEqual(10, tokenizer.get_column())

            self.assertEqual(JsonToken.ITEM_SEPARATOR, tokenizer.next())
            self.assertEqual(',', tokenizer.get_value())
            self.assertEqual(1 + i + 1, tokenizer.get_line())
            self.assertEqual(19, tokenizer.get_column())

        self.assertEqual(JsonToken.VALUE, tokenizer.next())
        self.assertEqual("key", tokenizer.get_value())
        self.assertEqual(1 + k + 1, tokenizer.get_line())
        self.assertEqual(3, tokenizer.get_column())

        self.assertEqual(JsonToken.KEY_SEPARATOR, tokenizer.next())
        self.assertEqual(':', tokenizer.get_value())
        self.assertEqual(1 + k + 1, tokenizer.get_line())
        self.assertEqual(8, tokenizer.get_column())

        self.assertEqual(JsonToken.VALUE, tokenizer.next())
        self.assertEqual("1000000", tokenizer.get_value())
        self.assertEqual(1 + k + 1, tokenizer.get_line())
        self.assertEqual(10, tokenizer.get_column())

        self.assertEqual(JsonToken.END_OBJECT, tokenizer.next())
        self.assertEqual(1 + k + 2, tokenizer.get_line())
        self.assertEqual(1, tokenizer.get_column())

    def test_long_input_split_in_double_after_e(self):
        json_string = ""
        json_string += "{\n" # 2 chars
        k = 4000
        for i in range(k): # 20 x 4000 > 65534 to check reading by chunks
            # BUFFER_SIZE is 65536, thus 65534 % 20 gives position within the string below
            # where the buffer will be split => 14, which is somewhere in the middle of the number
            #             |->            <-|
            json_string += "  \"key\":    1e5   ,\n" # 20 chars
        json_string += "  \"key\":    1e5  \n"
        json_string += "}"

        text_io = io.StringIO(json_string)
        tokenizer = JsonTokenizer(text_io)

        self.assertEqual(JsonToken.BEGIN_OBJECT, tokenizer.next())
        self.assertEqual('{', tokenizer.get_value())
        self.assertEqual(1, tokenizer.get_line())
        self.assertEqual(1, tokenizer.get_column())

        for i in range(k):
            self.assertEqual(JsonToken.VALUE, tokenizer.next())
            self.assertEqual("key", tokenizer.get_value())
            self.assertEqual(1 + i + 1, tokenizer.get_line())
            self.assertEqual(3, tokenizer.get_column())

            self.assertEqual(JsonToken.KEY_SEPARATOR, tokenizer.next())
            self.assertEqual(':', tokenizer.get_value())
            self.assertEqual(1 + i + 1, tokenizer.get_line())
            self.assertEqual(8, tokenizer.get_column())

            self.assertEqual(JsonToken.VALUE, tokenizer.next())
            self.assertEqual(1e5, tokenizer.get_value())
            self.assertEqual(1 + i + 1, tokenizer.get_line())
            self.assertEqual(13, tokenizer.get_column())

            self.assertEqual(JsonToken.ITEM_SEPARATOR, tokenizer.next())
            self.assertEqual(',', tokenizer.get_value())
            self.assertEqual(1 + i + 1, tokenizer.get_line())
            self.assertEqual(19, tokenizer.get_column())

        self.assertEqual(JsonToken.VALUE, tokenizer.next())
        self.assertEqual("key", tokenizer.get_value())
        self.assertEqual(1 + k + 1, tokenizer.get_line())
        self.assertEqual(3, tokenizer.get_column())

        self.assertEqual(JsonToken.KEY_SEPARATOR, tokenizer.next())
        self.assertEqual(':', tokenizer.get_value())
        self.assertEqual(1 + k + 1, tokenizer.get_line())
        self.assertEqual(8, tokenizer.get_column())

        self.assertEqual(JsonToken.VALUE, tokenizer.next())
        self.assertEqual(1e5, tokenizer.get_value())
        self.assertEqual(1 + k + 1, tokenizer.get_line())
        self.assertEqual(13, tokenizer.get_column())

        self.assertEqual(JsonToken.END_OBJECT, tokenizer.next())
        self.assertEqual(1 + k + 2, tokenizer.get_line())
        self.assertEqual(1, tokenizer.get_column())

    def test_unknown_token(self):
        text_io = io.StringIO("\\\n")
        tokenizer = JsonTokenizer(text_io)
        with self.assertRaises(JsonParserException) as error:
            tokenizer.next()
        self.assertEqual("JsonTokenizer:1:1: Unknown token!", str(error.exception))

class JsonReaderTest(unittest.TestCase):

    def test_read_object(self):
        text_io = io.StringIO(
                "{\n" +
                "    \"value\": 13,\n" +
                "    \"nested\": {\n" +
                "        \"value\": 10,\n" +
                "        \"text\": \"nested\",\n" +
                "        \"data\": {\n" +
                "             \"buffer\": [\n" +
                "                 203,\n" +
                "                 240\n" +
                "             ],\n" +
                "             \"bitSize\": 12\n" +
                "        },\n" +
                "        \"dummyEnum\": 0,\n" +
                "        \"dummyBitmask\": 1\n" +
                "    },\n" +
                "    \"text\": \"test\",\n" +
                "    \"nestedArray\": [\n" +
                "        {\n" +
                "            \"value\": 5,\n" +
                "            \"text\": \"nestedArray\",\n" +
                "            \"data\": {\n" +
                "                 \"buffer\": [\n" +
                "                     202,\n" +
                "                     254\n" +
                "                 ]," +
                "                 \"bitSize\": 15\n" +
                "            },\n" +
                "            \"dummyEnum\": 1,\n" +
                "            \"dummyBitmask\": 2\n" +
                "        }\n" +
                "    ],\n" +
                "    \"textArray\": [\n" +
                "        \"this\",\n" +
                "        \"is\",\n" +
                "        \"text\",\n" +
                "        \"array\"\n" +
                "    ],\n" +
                "    \"externArray\": [\n" +
                "        {\n" +
                "            \"buffer\": [\n" +
                "                222,\n" +
                "                209\n" +
                "            ]," +
                "            \"bitSize\": 13\n" +
                "        }\n" +
                "    ],\n" +
                "    \"optionalBool\": null\n" +
                "}"
                )

        json_reader = JsonReader(text_io)
        dummy_object = json_reader.read(DummyObject.type_info())
        self.assertTrue(dummy_object is not None)
        self.assertTrue(isinstance(dummy_object, DummyObject))

        self.assertEqual(13, dummy_object.value)
        self.assertEqual(13, dummy_object.nested.param)
        self.assertEqual(10, dummy_object.nested.value)
        self.assertEqual("nested", dummy_object.nested.text)
        self.assertEqual(BitBuffer(bytes([0xCB, 0xF0]), 12), dummy_object.nested.data)
        self.assertEqual(DummyEnum.ONE, dummy_object.nested.dummy_enum)
        self.assertEqual(DummyBitmask.Values.READ, dummy_object.nested.dummy_bitmask)
        self.assertEqual("test", dummy_object.text)
        self.assertEqual(1, len(dummy_object.nested_array))
        self.assertEqual(5, dummy_object.nested_array[0].value)
        self.assertEqual("nestedArray", dummy_object.nested_array[0].text)
        self.assertEqual(BitBuffer(bytes([0xCA, 0xFE]), 15), dummy_object.nested_array[0].data)
        self.assertEqual(DummyEnum.TWO, dummy_object.nested_array[0].dummy_enum)
        self.assertEqual(DummyBitmask.Values.WRITE, dummy_object.nested_array[0].dummy_bitmask)
        self.assertEqual(4, len(dummy_object.text_array))
        self.assertEqual("this", dummy_object.text_array[0])
        self.assertEqual("is", dummy_object.text_array[1])
        self.assertEqual("text", dummy_object.text_array[2])
        self.assertEqual("array", dummy_object.text_array[3])
        self.assertEqual(1, len(dummy_object.extern_array))
        self.assertEqual(BitBuffer(bytes([0xDE, 0xD1]), 13), dummy_object.extern_array[0])
        self.assertEqual(None, dummy_object.optional_bool)
        self.assertEqual(None, dummy_object.optional_nested) # not present in json

    def test_read_two_objects(self):
        text_io = io.StringIO(
                "{\"value\": 13}\n" +
                "{\"value\": 42, \"text\": \"test\"}\n"
                )

        json_reader = JsonReader(text_io)
        dummy_object1 = json_reader.read(DummyObject.type_info())
        self.assertTrue(dummy_object1 is not None)
        self.assertTrue(isinstance(dummy_object1, DummyObject))

        self.assertEqual(13, dummy_object1.value)
        self.assertEqual("", dummy_object1.text)

        dummy_object2 = json_reader.read(DummyObject.type_info())
        self.assertTrue(dummy_object2 is not None)
        self.assertTrue(isinstance(dummy_object2, DummyObject))

        self.assertEqual(42, dummy_object2.value)
        self.assertEqual("test", dummy_object2.text)

    def test_read_unonrdered_bit_buffer(self):
        text_io = io.StringIO(
                "{\n" +
                "    \"value\": 13,\n" +
                "    \"nested\": {\n" +
                "        \"value\": 10,\n" +
                "        \"text\": \"nested\",\n" +
                "        \"data\": {\n" +
                "             \"bitSize\": 12,\n" +
                "             \"buffer\": [\n" +
                "                 203,\n" +
                "                 240\n" +
                "             ]\n" +
                "        },\n" +
                "        \"dummyEnum\": 0,\n" +
                "        \"dummyBitmask\": 1\n" +
                "    }\n" +
                "}"
                )

        json_reader = JsonReader(text_io)
        dummy_object = json_reader.read(DummyObject.type_info())
        self.assertTrue(dummy_object is not None)
        self.assertTrue(isinstance(dummy_object, DummyObject))

        self.assertEqual(13, dummy_object.value)
        self.assertEqual(13, dummy_object.nested.param)
        self.assertEqual(10, dummy_object.nested.value)
        self.assertEqual("nested", dummy_object.nested.text)
        self.assertEqual(BitBuffer(bytes([0xCB, 0xF0]), 12), dummy_object.nested.data)
        self.assertEqual(DummyEnum.ONE, dummy_object.nested.dummy_enum)
        self.assertEqual(DummyBitmask.Values.READ, dummy_object.nested.dummy_bitmask)

    def test_read_stringified_enum(self):
        self._check_read_stringified_enum("ONE", DummyEnum.ONE)
        self._check_read_stringified_enum("MinusOne", DummyEnum.MINUS_ONE)
        self._check_read_stringified_enum_raises("NONEXISTING",
            "JsonReader: Cannot create enum 'DummyEnum' "
            "from string value 'NONEXISTING'! (JsonParser:3:22)")
        self._check_read_stringified_enum_raises("***",
            "JsonReader: Cannot create enum 'DummyEnum' "
            "from string value '***'! (JsonParser:3:22)")
        self._check_read_stringified_enum_raises("10 /* no match */",
            "JsonReader: Cannot create enum 'DummyEnum' "
            "from string value '10 /* no match */'! (JsonParser:3:22)")
        self._check_read_stringified_enum_raises("-10 /* no match */",
            "JsonReader: Cannot create enum 'DummyEnum' "
            "from string value '-10 /* no match */'! (JsonParser:3:22)")
        self._check_read_stringified_enum_raises("",
            "JsonReader: Cannot create enum 'DummyEnum' "
            "from string value ''! (JsonParser:3:22)")

    def test_read_stringified_bitmask(self):
        self._check_read_stringified_bitmask("READ", DummyBitmask.Values.READ)
        self._check_read_stringified_bitmask("READ | WRITE",
            DummyBitmask.Values.READ | DummyBitmask.Values.WRITE)
        self._check_read_stringified_bitmask_raises("NONEXISTING",
            "JsonReader: Cannot create bitmask 'DummyBitmask' " +
            "from string value 'NONEXISTING'! (JsonParser:3:25)")
        self._check_read_stringified_bitmask_raises("READ | NONEXISTING",
            "JsonReader: Cannot create bitmask 'DummyBitmask' " +
            "from string value 'READ | NONEXISTING'! (JsonParser:3:25)")
        self._check_read_stringified_bitmask_raises("READ * NONEXISTING",
            "JsonReader: Cannot create bitmask 'DummyBitmask' " +
            "from string value 'READ * NONEXISTING'! (JsonParser:3:25)")
        self._check_read_stringified_bitmask("7 /* READ | WRITE */", DummyBitmask.from_value(7))
        self._check_read_stringified_bitmask("15 /* READ | WRITE */", DummyBitmask.from_value(15))
        self._check_read_stringified_bitmask("4 /* no match */", DummyBitmask.from_value(4))
        self._check_read_stringified_bitmask_raises("",
            "JsonReader: Cannot create bitmask 'DummyBitmask' " +
            "from string value ''! (JsonParser:3:25)")
        self._check_read_stringified_bitmask_raises(" ",
            "JsonReader: Cannot create bitmask 'DummyBitmask' " +
            "from string value ' '! (JsonParser:3:25)")
        self._check_read_stringified_bitmask_raises(" | ",
            "JsonReader: Cannot create bitmask 'DummyBitmask' " +
            "from string value ' | '! (JsonParser:3:25)")

    def test_json_parser_exception(self):
        text_io = io.StringIO(
                "{\"value\"\n\"value\""
                )

        json_reader = JsonReader(text_io)
        with self.assertRaises(JsonParserException) as error:
            json_reader.read(DummyObject.type_info())
        self.assertEqual("JsonParser:2:1: Unexpected token: JsonToken.VALUE ('value'), " +
                         "expecting JsonToken.KEY_SEPARATOR!", str(error.exception))

    def test_wrong_key_exception(self):
        text_io = io.StringIO(
                "{\"value\": 13,\n\"nonexisting\": 10}"
                )

        json_reader = JsonReader(text_io)
        with self.assertRaises(PythonRuntimeException) as error:
            json_reader.read(DummyObject.type_info())
        self.assertEqual("ZserioTreeCreator: Field 'nonexisting' not found in 'DummyObject'! " +
                         "(JsonParser:2:16)", str(error.exception))

    def test_wrong_value_type_exception(self):
        text_io = io.StringIO(
                "{\n  \"value\": \"13\"\n}"
                )

        json_reader = JsonReader(text_io)
        with self.assertRaises(PythonRuntimeException) as error:
            json_reader.read(DummyObject.type_info())
        self.assertEqual("ZserioTreeCreator: Unexpected value type '<class 'str'>', " +
                         "expecting '<class 'int'>'! (JsonParser:2:12)", str(error.exception))

    def test_wrong_bit_buffer_exception(self):
        text_io = io.StringIO(
                "{\n" +
                "    \"value\": 13,\n" +
                "    \"nested\": {\n" +
                "        \"value\": 10,\n" +
                "        \"text\": \"nested\",\n" +
                "        \"data\": {\n" +
                "             \"buffer\": [\n" +
                "                 203,\n" +
                "                 240\n" +
                "             ],\n" +
                "             \"bitSize\": {\n" +
                "             }\n" +
                "        }\n" +
                "    }\n" +
                "}"
                )

        json_reader = JsonReader(text_io)
        with self.assertRaises(PythonRuntimeException) as error:
            json_reader.read(DummyObject.type_info())
        self.assertEqual("JsonReader: Unexpected begin object in Bit Buffer! (JsonParser:11:25)",
                         str(error.exception))

    def test_partial_bit_buffer_exception(self):
        text_io = io.StringIO(
                "{\n" +
                "    \"value\": 13,\n" +
                "    \"nested\": {\n" +
                "        \"value\": 10,\n" +
                "        \"text\": \"nested\",\n" +
                "        \"data\": {\n" +
                "             \"buffer\": [\n" +
                "                 203,\n" +
                "                 240\n" +
                "             ]\n" +
                "        }\n" +
                "    }\n" +
                "}"
                )

        json_reader = JsonReader(text_io)
        with self.assertRaises(PythonRuntimeException) as error:
            json_reader.read(DummyObject.type_info())
        self.assertEqual("JsonReader: Unexpected end in Bit Buffer! (JsonParser:12:5)", str(error.exception))

    def test_json_array_exception(self):
        text_io = io.StringIO(
                "[1, 2]"
                )

        json_reader = JsonReader(text_io)
        with self.assertRaises(PythonRuntimeException) as error:
            json_reader.read(DummyObject.type_info())
        self.assertEqual("JsonReader: ZserioTreeCreator expects json object! (JsonParser:1:1)",
                         str(error.exception))

    def test_json_value_exception(self):
        text_io = io.StringIO(
                "\"text\""
                )

        json_reader = JsonReader(text_io)
        with self.assertRaises(PythonRuntimeException) as error:
            json_reader.read(DummyObject.type_info())
        self.assertEqual("JsonReader: ZserioTreeCreator expects json object! (JsonParser:1:1)",
                         str(error.exception))

    def _check_read_stringified_enum(self, string_value, expected_value):
        text_io = io.StringIO(
                "{\n"
                "    \"nested\": {\n"
                "        \"dummyEnum\": \"" + string_value + "\"\n"
                "    }\n"
                "}"
                )

        json_reader = JsonReader(text_io)
        dummy_object = json_reader.read(DummyObject.type_info())
        self.assertTrue(dummy_object is not None)
        self.assertTrue(isinstance(dummy_object, DummyObject))

        self.assertEqual(expected_value, dummy_object.nested.dummy_enum)

    def _check_read_stringified_enum_raises(self, string_value, expected_message):
        text_io = io.StringIO(
                "{\n"
                "    \"nested\": {\n"
                "        \"dummyEnum\": \"" + string_value + "\"\n"
                "    }\n"
                "}"
                )

        json_reader = JsonReader(text_io)
        with self.assertRaises(PythonRuntimeException) as error:
            dummy_object = json_reader.read(DummyObject.type_info())
        self.assertEqual(expected_message, str(error.exception))

    def _check_read_stringified_bitmask(self, string_value, expected_value):
        text_io = io.StringIO(
                "{\n"
                "    \"nested\": {\n"
                "        \"dummyBitmask\": \"" + string_value + "\"\n"
                "    }\n"
                "}"
                )

        json_reader = JsonReader(text_io)
        dummy_object = json_reader.read(DummyObject.type_info())
        self.assertTrue(dummy_object is not None)
        self.assertTrue(isinstance(dummy_object, DummyObject))

        self.assertEqual(expected_value, dummy_object.nested.dummy_bitmask)

    def _check_read_stringified_bitmask_raises(self, string_value, expected_message):
        text_io = io.StringIO(
                "{\n"
                "    \"nested\": {\n"
                "        \"dummyBitmask\": \"" + string_value + "\"\n"
                "    }\n"
                "}"
                )

        json_reader = JsonReader(text_io)
        with self.assertRaises(PythonRuntimeException) as error:
            dummy_object = json_reader.read(DummyObject.type_info())
        self.assertEqual(expected_message, str(error.exception))
