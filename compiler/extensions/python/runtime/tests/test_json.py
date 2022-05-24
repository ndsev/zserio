import io
import unittest

from zserio.bitbuffer import BitBuffer
from zserio.exception import PythonRuntimeException
from zserio.json import JsonWriter, JsonEncoder, JsonParser
from zserio.typeinfo import TypeInfo, MemberInfo, ItemInfo, TypeAttribute, MemberAttribute
from zserio.limits import INT64_MIN, UINT64_MAX

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
        class DummyEnum:
            @property
            def value(self):
                return 0

        dummy_enum_type_info = TypeInfo("DummyEnum", DummyEnum, attributes={
            TypeAttribute.ENUM_ITEMS : [ItemInfo("ZERO", 0)]
        })

        dummy_enum_member_info = MemberInfo("dummyEnum", dummy_enum_type_info)

        json_writer = JsonWriter()
        json_writer.visit_value(DummyEnum(), dummy_enum_member_info)
        # note that this is not valid JSON
        self.assertEqual("\"dummyEnum\": 0", json_writer.get_io().getvalue())

    def test_bitmask_value(self):
        class DummyBitmask:
            @property
            def value(self):
                return 0

        dummy_bitmask_type_info = TypeInfo("DummyBitmask", DummyBitmask, attributes={
            TypeAttribute.BITMASK_VALUES : [ItemInfo("ZERO", 0)]
        })

        dummy_bitmask_member_info = MemberInfo("dummyBitmask", dummy_bitmask_type_info)

        json_writer = JsonWriter()
        json_writer.visit_value(DummyBitmask(), dummy_bitmask_member_info)
        # note that this is not valid JSON
        self.assertEqual("\"dummyBitmask\": 0", json_writer.get_io().getvalue())

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
        self.assertEqual("null", json_encoder.encode(None))

    def test_encode_bool(self):
        json_encoder = JsonEncoder()
        self.assertEqual("true", json_encoder.encode(True))
        self.assertEqual("false", json_encoder.encode(False))

    def test_encode_integral(self):
        json_encoder = JsonEncoder()
        self.assertEqual("-9223372036854775808", json_encoder.encode(INT64_MIN))
        self.assertEqual("-1000", json_encoder.encode(-1000))
        self.assertEqual("0", json_encoder.encode(0))
        self.assertEqual("1000", json_encoder.encode(1000))
        self.assertEqual("18446744073709551615", json_encoder.encode(UINT64_MAX))

    def test_encode_floating_point(self):
        json_encoder = JsonEncoder()
        self.assertEqual("-1.0", json_encoder.encode(-1.0))
        self.assertEqual("0.0", json_encoder.encode(0.0))
        self.assertEqual("1.0", json_encoder.encode(1.0))

        self.assertEqual("3.5", json_encoder.encode(3.5))
        self.assertEqual("9.875", json_encoder.encode(9.875))
        self.assertEqual("0.6171875", json_encoder.encode(0.6171875))

        self.assertEqual("1e+20", json_encoder.encode(1e20))

        self.assertEqual("NaN", json_encoder.encode(float('nan')))
        self.assertEqual("Infinity", json_encoder.encode(float('inf')))
        self.assertEqual("-Infinity", json_encoder.encode(float('-inf')))

    def test_encode_string(self):
        json_encoder = JsonEncoder()
        self.assertEqual("\"\"", json_encoder.encode(""))
        self.assertEqual("\"test\"", json_encoder.encode("test"))
        self.assertEqual("\"München\"", json_encoder.encode("München"))
        self.assertEqual("\"€\"", json_encoder.encode("€"))

        # escapes
        self.assertEqual("\"\\\\\"", json_encoder.encode("\\"))
        self.assertEqual("\"\\\"\"", json_encoder.encode("\""))
        self.assertEqual("\"\\b\"", json_encoder.encode("\b"))
        self.assertEqual("\"\\f\"", json_encoder.encode("\f"))
        self.assertEqual("\"\\n\"", json_encoder.encode("\n"))
        self.assertEqual("\"\\r\"", json_encoder.encode("\r"))
        self.assertEqual("\"\\t\"", json_encoder.encode("\t"))

        self.assertEqual("\"\\n\\t%^@(*aAzZ01234569$%^!?<>[]](){}-=+~:;/|\\\\\\\"'Hello World2\"",
                         json_encoder.encode("\n\t%^@(*aAzZ01234569$%^!?<>[]](){}-=+~:;/|\\\"'Hello World2"))

        # <= 0x1F -> unicode escape
        self.assertEqual("\"\\u001f\"", json_encoder.encode("\x1f"))

class JsonParserTest(unittest.TestCase):

    class DummyObserver(JsonParser.Observer):
        def __init__(self):
            self._report = []

        @property
        def report(self):
            return self._report

        def begin_array(self):
            self._report.append("begin_array")

        def end_array(self):
            self._report.append("end_array")

        def begin_object(self):
            self._report.append("begin_object")

        def end_object(self):
            self._report.append("end_object")

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
        tokenizer = JsonParser._Tokenizer(text_io)
        token = tokenizer.next()
        tokens = []
        while token not in [JsonParser._Tokenizer.EOF, JsonParser._Tokenizer.UNKNOWN]:
            tokens.append((token, tokenizer.get_value()))
            token = tokenizer.next()

        self.assertEqual(
            [
                (JsonParser._Tokenizer.BEGIN_OBJECT, "{"),
                (JsonParser._Tokenizer.VALUE, "array"),
                (JsonParser._Tokenizer.KEY_SEPARATOR, ":"),
                (JsonParser._Tokenizer.BEGIN_ARRAY, "["),
                (JsonParser._Tokenizer.BEGIN_OBJECT, "{"),
                (JsonParser._Tokenizer.VALUE, "key"),
                (JsonParser._Tokenizer.KEY_SEPARATOR, ":"),
                (JsonParser._Tokenizer.VALUE, 10),
                (JsonParser._Tokenizer.END_OBJECT, "}"),
                (JsonParser._Tokenizer.END_ARRAY, "]"),
                (JsonParser._Tokenizer.END_OBJECT, "}")
            ], tokens
        )

    def test_empty(self):
        text_io = io.StringIO("")
        observer = JsonParserTest.DummyObserver()
        json_parser = JsonParser(text_io, observer)
        self.assertTrue(json_parser.parse())
        self.assertEqual([], observer.report)

    def test_str(self):
        text_io = io.StringIO("\"text\"")
        observer = JsonParserTest.DummyObserver()
        json_parser = JsonParser(text_io, observer)
        self.assertTrue(json_parser.parse())
        self.assertEqual(["visit_value: text"], observer.report)

    def test_two_strings(self):
        text_io = io.StringIO("\"text\"\"text\"")
        observer = JsonParserTest.DummyObserver()
        json_parser = JsonParser(text_io, observer)
        self.assertFalse(json_parser.parse())
        self.assertEqual(["visit_value: text"], observer.report)

    def test_unexpected_object(self):
        text_io = io.StringIO("{\n\n{\n\n")
        observer = JsonParserTest.DummyObserver()
        json_parser = JsonParser(text_io, observer)
        with self.assertRaises(PythonRuntimeException) as error:
            json_parser.parse()

        self.assertTrue(str(error.exception).startswith("2:"), error.exception)

        self.assertEqual(["begin_object"], observer.report)

    def test_missing_object_item_separator(self):
        text_io = io.StringIO("{\n\"item1\":\"text\"\n\"item2\":\"text\"\n}")
        observer = JsonParserTest.DummyObserver()
        json_parser = JsonParser(text_io, observer)
        with self.assertRaises(PythonRuntimeException) as error:
            json_parser.parse()

        self.assertTrue(str(error.exception).startswith("2:"), error.exception)

        self.assertEqual([
            "begin_object",
            "visit_key: item1", "visit_value: text"
        ], observer.report)

    def test_wrong_key_type(self):
        text_io = io.StringIO("{\n10:\"text\"\n}")
        observer = JsonParserTest.DummyObserver()
        json_parser = JsonParser(text_io, observer)
        with self.assertRaises(PythonRuntimeException) as error:
            json_parser.parse()

        self.assertTrue(str(error.exception).startswith("1:"), error.exception)

        self.assertEqual(["begin_object"], observer.report)

    def test_unexpected_element_token(self):
        text_io = io.StringIO("{\n\"item\":}")
        observer = JsonParserTest.DummyObserver()
        json_parser = JsonParser(text_io, observer)
        with self.assertRaises(PythonRuntimeException) as error:
            json_parser.parse()

        self.assertTrue(str(error.exception).startswith("1:"), error.exception)

        self.assertEqual(["begin_object", "visit_key: item"], observer.report)

    def test_missing_array_item_separator(self):
        text_io = io.StringIO("{\n\"array\":\n[10\n20\n]}")
        observer = JsonParserTest.DummyObserver()
        json_parser = JsonParser(text_io, observer)
        with self.assertRaises(PythonRuntimeException) as error:
            json_parser.parse()

        self.assertTrue(str(error.exception).startswith("3:"), error.exception)

        self.assertEqual([
            "begin_object",
            "visit_key: array",
            "begin_array",
            "visit_value: 10"
        ], observer.report)

    def test_unknown_token(self):
        text_io = io.StringIO("\\\n")
        observer = JsonParserTest.DummyObserver()
        json_parser = JsonParser(text_io, observer)
        with self.assertRaises(PythonRuntimeException) as error:
            json_parser.parse()

        self.assertTrue(str(error.exception).startswith("0:"), error.exception)

        self.assertEqual([], observer.report)

    def test_parse(self):
        text_io = io.StringIO("{\"array\":\n[\n{\"key1\":\n10, \"key2\":\n\"text\"}, {}]}")
        observer = JsonParserTest.DummyObserver()
        json_parser = JsonParser(text_io, observer)
        json_parser.parse()

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
