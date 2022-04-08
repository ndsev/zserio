import unittest

from zserio import JsonWriter, BitBuffer
from zserio.json import JsonEncoder
from zserio.typeinfo import TypeInfo, MemberInfo, ItemInfo, TypeAttribute, MemberAttribute

class JsonWriterTest(unittest.TestCase):

    def test_null(self):
        json_writer = JsonWriter()
        self.assertEqual("", json_writer.get_io().getvalue())

    def test_simple_field(self):
        json_writer = JsonWriter()
        json_writer.visit_value("test", MemberInfo("text", TypeInfo("string", str)))
        # note that this is not valid JSON
        self.assertEqual("\"text\": \"test\"", json_writer.get_io().getvalue())

    def test_compound(self):
        json_writer = JsonWriter()
        json_writer.begin_root(object())
        json_writer.visit_value(13, MemberInfo("identifier", TypeInfo("uint32", int)))
        json_writer.visit_value("test", MemberInfo("text", TypeInfo("string", str)))
        json_writer.visit_value(BitBuffer(bytes([0x1F]), 5), MemberInfo("data", TypeInfo("extern", BitBuffer)))
        json_writer.end_root(object())
        self.assertEqual(
            "{\"identifier\": 13, \"text\": \"test\", \"data\": {\"buffer\": \"b'\\\\x1f'\", \"bitSize\": 5}}",
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

    def test_array_indent(self):
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

    @staticmethod
    def _walk_nested(json_writer):
        dummy_type_info = TypeInfo("dummy", object)

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
        json_writer.visit_value(1, array_member_info)
        json_writer.visit_value(2, array_member_info)
        json_writer.end_array([1, 2], array_member_info)
        json_writer.end_root(object())

class JsonEncoderTest(unittest.TestCase):

    def test_encode_enum(self):
        class DummyEnum:
            @property
            def value(self):
                return 0

        dummy_enum_type_info = TypeInfo("DummyEnum", DummyEnum, attributes={
            TypeAttribute.ENUM_ITEMS : [ItemInfo("ZERO", 0)]
        })

        json_encoder = JsonEncoder()
        self.assertEqual("0", json_encoder.encode(DummyEnum(), dummy_enum_type_info))

    def test_encode_bitmask(self):
        class DummyBitmask:
            @property
            def value(self):
                return 0

        dummy_bitmask_type_info = TypeInfo("DummyBitmask", DummyBitmask, attributes={
            TypeAttribute.BITMASK_VALUES : [ItemInfo("ZERO", 0)]
        })

        json_encoder = JsonEncoder()
        self.assertEqual("0", json_encoder.encode(DummyBitmask(), dummy_bitmask_type_info))

    def test_encode_bytes(self):
        json_encoder = JsonEncoder()
        self.assertEqual("\"b'\\\\x1f\\\\x11\\\\xab'\"",
                         json_encoder.encode(bytes([0x1F, 0x11, 0xAB]), TypeInfo("bytes", bytes)))

    def test_encode_str(self):
        json_encoder = JsonEncoder()
        self.assertEqual("\"value\"", json_encoder.encode("value", TypeInfo("string", str)))
