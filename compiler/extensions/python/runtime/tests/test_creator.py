import io
import unittest

from zserio.exception import PythonRuntimeException
from zserio.creator import ZserioTreeCreator
from zserio.json import JsonReader, JsonParserException
from zserio.bitbuffer import BitBuffer

from test_creator_object import DummyEnum, DummyBitmask, DummyObject

class ZserioTreeCreatorTest(unittest.TestCase):

    def test_create_object(self):
        creator = ZserioTreeCreator(DummyObject.type_info())
        creator.begin_root()
        obj = creator.end_root()
        self.assertTrue(isinstance(obj, DummyObject))

    def test_create_object_set_fields(self):
        creator = ZserioTreeCreator(DummyObject.type_info())

        creator.begin_root()
        creator.set_value("value", 13)
        creator.set_value("text", "test")
        obj = creator.end_root()

        self.assertEqual(13, obj.value)
        self.assertEqual("test", obj.text)

    def test_create_object_full(self):
        creator = ZserioTreeCreator(DummyObject.type_info())

        creator.begin_root()
        creator.set_value("value", 13)
        creator.set_value("text", "test")
        creator.begin_compound("nested")
        creator.set_value("value", 10)
        creator.set_value("text", "nested")
        creator.set_value("data", BitBuffer([0x3c], 6))
        creator.set_value("dummyEnum", DummyEnum.ONE)
        creator.set_value("dummyBitmask", DummyBitmask.Values.WRITE)
        creator.end_compound()
        creator.begin_array("nestedArray")
        creator.begin_compound_element()
        creator.set_value("value", 5)
        creator.set_value("text", "nestedArray")
        creator.set_value("dummyEnum", DummyEnum.TWO)
        creator.set_value("dummyBitmask", DummyBitmask.Values.READ)
        creator.end_compound_element()
        creator.end_array()
        creator.begin_array("textArray")
        creator.add_value_element("this")
        creator.add_value_element("is")
        creator.add_value_element("text")
        creator.add_value_element("array")
        creator.end_array()
        creator.begin_array("externArray")
        creator.add_value_element(BitBuffer([0x0f], 4))
        creator.end_array()
        creator.set_value("optionalBool", False)
        creator.begin_compound("optionalNested")
        creator.set_value("text", "optionalNested")
        creator.end_compound()
        obj = creator.end_root()

        self.assertEqual(13, obj.value)
        self.assertEqual("test", obj.text)
        self.assertEqual(13, obj.nested.param)
        self.assertEqual(10, obj.nested.value)
        self.assertEqual("nested", obj.nested.text)
        self.assertEqual([0x3c], obj.nested.data.buffer)
        self.assertEqual(6, obj.nested.data.bitsize)
        self.assertEqual(DummyEnum.ONE, obj.nested.dummy_enum)
        self.assertEqual(DummyBitmask.Values.WRITE, obj.nested.dummy_bitmask)
        self.assertEqual(1, len(obj.nested_array))
        self.assertEqual(5, obj.nested_array[0].value)
        self.assertEqual("nestedArray", obj.nested_array[0].text)
        self.assertEqual(DummyEnum.TWO, obj.nested_array[0].dummy_enum)
        self.assertEqual(DummyBitmask.Values.READ, obj.nested_array[0].dummy_bitmask)
        self.assertEqual(["this", "is", "text", "array"], obj.text_array)
        self.assertEqual(1, len(obj.extern_array))
        self.assertEqual([0x0f], obj.extern_array[0].buffer)
        self.assertEqual(4, obj.extern_array[0].bitsize)
        self.assertEqual(False, obj.optional_bool)
        self.assertEqual("optionalNested", obj.optional_nested.text)

    def test_exceptions_before_root(self):
        creator = ZserioTreeCreator(DummyObject.type_info())

        with self.assertRaises(PythonRuntimeException):
            creator.end_root()
        with self.assertRaises(PythonRuntimeException):
            creator.begin_array("nestedArray")
        with self.assertRaises(PythonRuntimeException):
            creator.end_array()
        with self.assertRaises(PythonRuntimeException):
            creator.begin_compound("nested")
        with self.assertRaises(PythonRuntimeException):
            creator.end_compound()
        with self.assertRaises(PythonRuntimeException):
            creator.set_value("value", 13)
        with self.assertRaises(PythonRuntimeException):
            creator.begin_compound_element()
        with self.assertRaises(PythonRuntimeException):
            creator.end_compound_element()
        with self.assertRaises(PythonRuntimeException):
            creator.add_value_element(13)

    def test_exceptions_in_root(self):
        creator = ZserioTreeCreator(DummyObject.type_info())

        creator.begin_root()

        with self.assertRaises(PythonRuntimeException):
            creator.begin_root()
        with self.assertRaises(PythonRuntimeException):
            creator.begin_array("nonexistent")
        with self.assertRaises(PythonRuntimeException):
            creator.begin_array("nested") # not an array
        with self.assertRaises(PythonRuntimeException):
            creator.end_array()
        with self.assertRaises(PythonRuntimeException):
            creator.begin_compound("nonexistent")
        with self.assertRaises(PythonRuntimeException):
            creator.begin_compound("nestedArray") # is array
        with self.assertRaises(PythonRuntimeException):
            creator.end_compound()
        with self.assertRaises(PythonRuntimeException):
            creator.set_value("nonexistent", 13)
        with self.assertRaises(PythonRuntimeException):
            creator.set_value("nestedArray", 13) # is array
        with self.assertRaises(PythonRuntimeException):
            creator.begin_compound_element()
        with self.assertRaises(PythonRuntimeException):
            creator.end_compound_element()
        with self.assertRaises(PythonRuntimeException):
            creator.add_value_element(13)

    def test_exceptions_in_compound(self):
        creator = ZserioTreeCreator(DummyObject.type_info())

        creator.begin_root()
        creator.begin_compound("nested")

        with self.assertRaises(PythonRuntimeException):
            creator.begin_root()
        with self.assertRaises(PythonRuntimeException):
            creator.end_root()
        with self.assertRaises(PythonRuntimeException):
            creator.begin_array("nonexistent")
        with self.assertRaises(PythonRuntimeException):
            creator.begin_array("value") # not an array
        with self.assertRaises(PythonRuntimeException):
            creator.end_array()
        with self.assertRaises(PythonRuntimeException):
            creator.begin_compound("nonexistent")
        with self.assertRaises(PythonRuntimeException):
            creator.begin_compound("text") # not a compound
        with self.assertRaises(PythonRuntimeException):
            creator.set_value("nonexistent", "test")
        with self.assertRaises(PythonRuntimeException):
            creator.set_value("value", "test") # wrong type
        with self.assertRaises(PythonRuntimeException):
            creator.begin_compound_element()
        with self.assertRaises(PythonRuntimeException):
            creator.end_compound_element()
        with self.assertRaises(PythonRuntimeException):
            creator.add_value_element(13)
        with self.assertRaises(PythonRuntimeException):
            creator.get_element_type()

    def test_exceptions_in_compound_array(self):
        creator = ZserioTreeCreator(DummyObject.type_info())
        creator.begin_root()
        creator.begin_array("nestedArray")

        with self.assertRaises(PythonRuntimeException):
            creator.begin_root()
        with self.assertRaises(PythonRuntimeException):
            creator.end_root()
        with self.assertRaises(PythonRuntimeException):
            creator.begin_array("nonexistent")
        with self.assertRaises(PythonRuntimeException):
            creator.begin_compound("nonexistent")
        with self.assertRaises(PythonRuntimeException):
            creator.end_compound()
        with self.assertRaises(PythonRuntimeException):
            creator.set_value("nonexistent", 13)
        with self.assertRaises(PythonRuntimeException):
            creator.end_compound_element()
        with self.assertRaises(PythonRuntimeException):
            creator.add_value_element(13)
        with self.assertRaises(PythonRuntimeException):
            creator.get_field_type("nonexistent")

    def test_exceptions_in_simple_array(self):
        creator = ZserioTreeCreator(DummyObject.type_info())
        creator.begin_root()
        creator.begin_array("textArray")

        with self.assertRaises(PythonRuntimeException):
            creator.begin_root()
        with self.assertRaises(PythonRuntimeException):
            creator.end_root()
        with self.assertRaises(PythonRuntimeException):
            creator.begin_array("nonexistent")
        with self.assertRaises(PythonRuntimeException):
            creator.begin_compound("nonexistent")
        with self.assertRaises(PythonRuntimeException):
            creator.end_compound()
        with self.assertRaises(PythonRuntimeException):
            creator.set_value("nonexistent", 13)
        with self.assertRaises(PythonRuntimeException):
            creator.begin_compound_element() # not a compound array
        with self.assertRaises(PythonRuntimeException):
            creator.end_compound_element()
        with self.assertRaises(PythonRuntimeException):
            creator.add_value_element(13) # wrong type
        with self.assertRaises(PythonRuntimeException):
            creator.get_field_type("nonexistent")

    def test_exceptions_in_compound_element(self):
        creator = ZserioTreeCreator(DummyObject.type_info())
        creator.begin_root()
        creator.begin_array("nestedArray")
        creator.begin_compound_element()

        with self.assertRaises(PythonRuntimeException):
            creator.begin_root()
        with self.assertRaises(PythonRuntimeException):
            creator.end_root()
        with self.assertRaises(PythonRuntimeException):
            creator.begin_array("nonexistent")
        with self.assertRaises(PythonRuntimeException):
            creator.end_array()
        with self.assertRaises(PythonRuntimeException):
            creator.begin_compound("nonexistent")
        with self.assertRaises(PythonRuntimeException):
            creator.end_compound()
        with self.assertRaises(PythonRuntimeException):
            creator.set_value("nonexistent", 13)
        with self.assertRaises(PythonRuntimeException):
            creator.begin_compound_element()
        with self.assertRaises(PythonRuntimeException):
            creator.add_value_element(13)

class JsonReaderTest(unittest.TestCase):

    def test_read_object(self):
        json_reader = JsonReader(io.StringIO(
            "{\n"
            "    \"value\": 13,\n"
            "    \"nested\": {\n"
            "        \"value\": 10,\n"
            "        \"text\": \"nested\",\n"
            "        \"data\": {\n"
            "             \"buffer\": [\n"
            "                 203,\n"
            "                 240\n"
            "             ],\n"
            "             \"bitSize\": 12\n"
            "        },\n"
            "        \"dummyEnum\": 0,\n"
            "        \"dummyBitmask\": 1\n"
            "    },\n"
            "    \"text\": \"test\",\n"
            "    \"nestedArray\": [\n"
            "        {\n"
            "            \"value\": 5,\n"
            "            \"text\": \"nestedArray\",\n"
            "            \"data\": {\n"
            "                 \"buffer\": [\n"
            "                     202,\n"
            "                     254\n"
            "                 ],"
            "                 \"bitSize\": 15\n"
            "            },\n"
            "            \"dummyEnum\": 1,\n"
            "            \"dummyBitmask\": 2\n"
            "        }\n"
            "    ],\n"
            "    \"textArray\": [\n"
            "        \"this\",\n"
            "        \"is\",\n"
            "        \"text\",\n"
            "        \"array\"\n"
            "    ],\n"
            "    \"externArray\": [\n"
            "        {\n"
            "            \"buffer\": [\n"
            "                222,\n"
            "                209\n"
            "            ],"
            "            \"bitSize\": 13\n"
            "        }\n"
            "    ],\n"
            "    \"optionalBool\": null\n"
            "}"
        ))

        obj = json_reader.read(DummyObject.type_info())

        self.assertEqual(13, obj.value)
        self.assertEqual(13, obj.nested.param)
        self.assertEqual(10, obj.nested.value)
        self.assertEqual("nested", obj.nested.text)
        self.assertEqual(BitBuffer(bytes([0xCB, 0xF0]), 12), obj.nested.data)
        self.assertEqual(DummyEnum.ONE, obj.nested.dummy_enum)
        self.assertEqual(DummyBitmask.Values.READ, obj.nested.dummy_bitmask)
        self.assertEqual("test", obj.text)
        self.assertEqual(1, len(obj.nested_array))
        self.assertEqual(5, obj.nested_array[0].value)
        self.assertEqual("nestedArray", obj.nested_array[0].text)
        self.assertEqual(BitBuffer(bytes([0xCA, 0xFE]), 15), obj.nested_array[0].data)
        self.assertEqual(DummyEnum.TWO, obj.nested_array[0].dummy_enum)
        self.assertEqual(DummyBitmask.Values.WRITE, obj.nested_array[0].dummy_bitmask)
        self.assertEqual(["this", "is", "text", "array"], obj.text_array)
        self.assertEqual([BitBuffer(bytes([0xDE, 0xD1]), 13)], obj.extern_array)
        self.assertEqual(None, obj.optional_bool)
        self.assertEqual(None, obj.optional_nested) # not present in json

    def test_read_two_objects(self):
        json_reader = JsonReader(io.StringIO(
            "{\"value\": 13}\n"
            "{\"value\": 42, \"text\": \"test\"}\n"
        ))

        obj1 = json_reader.read(DummyObject.type_info())
        self.assertEqual(13, obj1.value)
        self.assertEqual("", obj1.text)

        obj2 = json_reader.read(DummyObject.type_info())
        self.assertEqual(42, obj2.value)
        self.assertEqual("test", obj2.text)

    def test_read_unordered_bitbuffer(self):
        json_reader = JsonReader(io.StringIO(
            "{\n"
            "    \"value\": 13,\n"
            "    \"nested\": {\n"
            "        \"value\": 10,\n"
            "        \"text\": \"nested\",\n"
            "        \"data\": {\n"
            "             \"bitSize\": 12,\n"
            "             \"buffer\": [\n"
            "                 203,\n"
            "                 240\n"
            "             ]\n"
            "        },\n"
            "        \"dummyEnum\": 0,\n"
            "        \"dummyBitmask\": 1\n"
            "    }\n"
            "}"
        ))

        obj = json_reader.read(DummyObject.type_info())

        self.assertEqual(13, obj.value)
        self.assertEqual(13, obj.nested.param)
        self.assertEqual(10, obj.nested.value)
        self.assertEqual("nested", obj.nested.text)
        self.assertEqual(BitBuffer(bytes([0xCB, 0xF0]), 12), obj.nested.data)
        self.assertEqual(DummyEnum.ONE, obj.nested.dummy_enum)
        self.assertEqual(DummyBitmask.Values.READ, obj.nested.dummy_bitmask)

    def test_json_parser_exception(self):
        json_reader = JsonReader(io.StringIO("{\"value\"\n\"value\""))

        with self.assertRaises(JsonParserException) as error:
            json_reader.read(DummyObject.type_info())

        self.assertEqual("JsonParser:2:1: Unexpected token: JsonToken.VALUE ('value'), expecting "
                         "JsonToken.KEY_SEPARATOR!", str(error.exception))

    def test_wrong_key_exception(self):
        json_reader = JsonReader(io.StringIO("{\"value\": 13,\n\"nonexisting\": 10}"))

        with self.assertRaises(PythonRuntimeException) as error:
            json_reader.read(DummyObject.type_info())

        self.assertEqual("ZserioTreeCreator: Field 'nonexisting' not found in 'DummyObject'! "
                         "(JsonParser:2:16)", str(error.exception))

    def test_wrong_value_type_exception(self):
        json_reader = JsonReader(io.StringIO("{\n  \"value\": \"13\"\n}"))

        with self.assertRaises(PythonRuntimeException) as error:
            json_reader.read(DummyObject.type_info())

        self.assertEqual("ZserioTreeCreator: Unexpected value type '<class 'str'>', expecting '<class 'int'>'! "
                         "(JsonParser:2:12)", str(error.exception))

    def test_wrong_bitbuffer_exception(self):
        json_reader = JsonReader(io.StringIO(
            "{\n"
            "    \"value\": 13,\n"
            "    \"nested\": {\n"
            "        \"value\": 10,\n"
            "        \"text\": \"nested\",\n"
            "        \"data\": {\n"
            "             \"buffer\": [\n"
            "                 203,\n"
            "                 240\n"
            "             ],\n"
            "             \"bitSize\": {\n"
            "             }\n"
            "        }\n"
            "    }\n"
            "}"
        ))

        with self.assertRaises(PythonRuntimeException) as error:
            json_reader.read(DummyObject.type_info())

        self.assertEqual("JsonReader: Unexpected begin object in Bit Buffer! (JsonParser:11:25)",
                         str(error.exception))

    def test_partial_bitbuffer_exception(self):
        json_reader = JsonReader(io.StringIO(
            "{\n"
            "    \"value\": 13,\n"
            "    \"nested\": {\n"
            "        \"value\": 10,\n"
            "        \"text\": \"nested\",\n"
            "        \"data\": {\n"
            "             \"buffer\": [\n"
            "                 203,\n"
            "                 240\n"
            "             ]\n"
            "        }\n"
            "    }\n"
            "}"
        ))

        with self.assertRaises(PythonRuntimeException) as error:
            json_reader.read(DummyObject.type_info())

        self.assertEqual("JsonReader: Unexpected end in Bit Buffer! (JsonParser:12:5)", str(error.exception))

    def test_json_array_exception(self):
        json_reader = JsonReader(io.StringIO("[1, 2]"))

        with self.assertRaises(PythonRuntimeException):
            json_reader.read(DummyObject.type_info())

    def test_json_value_exception(self):
        json_reader = JsonReader(io.StringIO("\"text\""))

        with self.assertRaises(PythonRuntimeException):
            json_reader.read(DummyObject.type_info())

    def test_bitbuffer_adapter_uninitialized_calls(self):
        bitbuffer_adapter = JsonReader._BitBufferAdapter()

        with self.assertRaises(PythonRuntimeException):
            bitbuffer_adapter.begin_object()
        with self.assertRaises(PythonRuntimeException):
            bitbuffer_adapter.end_object()
        with self.assertRaises(PythonRuntimeException):
            bitbuffer_adapter.begin_array()
        with self.assertRaises(PythonRuntimeException):
            bitbuffer_adapter.end_array()
        with self.assertRaises(PythonRuntimeException):
            bitbuffer_adapter.visit_key("nonexisting")
        bitbuffer_adapter.visit_key("buffer")
        with self.assertRaises(PythonRuntimeException):
            bitbuffer_adapter.visit_key("nonexisting")
        with self.assertRaises(PythonRuntimeException):
            bitbuffer_adapter.visit_value("BadValue")

    def test_creator_adapter_uninitialized_calls(self):
        creator_adapter = JsonReader._CreatorAdapter()

        with self.assertRaises(PythonRuntimeException):
            creator_adapter.get()
        with self.assertRaises(PythonRuntimeException):
            creator_adapter.begin_object()
        with self.assertRaises(PythonRuntimeException):
            creator_adapter.end_object()
        with self.assertRaises(PythonRuntimeException):
            creator_adapter.begin_array()
        with self.assertRaises(PythonRuntimeException):
            creator_adapter.end_array()
        with self.assertRaises(PythonRuntimeException):
            creator_adapter.visit_key("key")
        with self.assertRaises(PythonRuntimeException):
            creator_adapter.visit_value(None)
