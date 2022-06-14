import io
import typing
import unittest
import enum

from zserio.exception import PythonRuntimeException
from zserio.array import Array, ObjectArrayTraits, StringArrayTraits, BitBufferArrayTraits
from zserio.creator import ZserioTreeCreator
from zserio.json import JsonReader
from zserio.typeinfo import TypeInfo, TypeAttribute, MemberInfo, MemberAttribute, ItemInfo
from zserio.bitbuffer import BitBuffer

class DummyEnum(enum.Enum):
    ONE = 0
    TWO = 1

    @staticmethod
    def type_info():
        attribute_list = {
            TypeAttribute.UNDERLYING_TYPE : TypeInfo('uint8', int),
            TypeAttribute.ENUM_ITEMS: [
                ItemInfo('ONE', DummyEnum.ONE),
                ItemInfo('TWO', DummyEnum.TWO)
            ]
        }

        return TypeInfo('DummyEnum', DummyEnum, attributes=attribute_list)

class DummyBitmask:
    def __init__(self) -> None:
        self._value = 0

    @classmethod
    def from_value(cls: typing.Type['DummyBitmask'], value: int) -> 'DummyBitmask':
        instance = cls()
        instance._value = value
        return instance

    @staticmethod
    def type_info():
        attribute_list = {
            TypeAttribute.UNDERLYING_TYPE : TypeInfo('uint8', int),
            TypeAttribute.BITMASK_VALUES: [
                ItemInfo('READ', DummyBitmask.Values.READ),
                ItemInfo('WRITE', DummyBitmask.Values.WRITE)
            ]
        }

        return TypeInfo('DummyBitmask', DummyBitmask, attributes=attribute_list)

    def __eq__(self, other: object) -> bool:
        return self._value == other._value

    class Values:
        READ: 'DummyBitmask' = None
        WRITE: 'DummyBitmask' = None

DummyBitmask.Values.READ = DummyBitmask.from_value(1)
DummyBitmask.Values.WRITE = DummyBitmask.from_value(2)

class DummyNested:
    def __init__(self, param_ : int, value_ : int = int(),  text_ : str = str(),
                 data_: typing.Union[BitBuffer, None] = None,
                 dummy_enum_: typing.Union[DummyEnum, None] = None,
                 dummy_bitmask_: typing.Union[DummyBitmask, None] = None) -> None:
        self._param_ = param_
        self._value_ = value_
        self._text_ = text_
        self._data_ = data_
        self._dummy_enum_ = dummy_enum_
        self._dummy_bitmask_ = dummy_bitmask_

    @staticmethod
    def type_info() -> TypeInfo:
        field_list: typing.List[MemberInfo] = [
            MemberInfo(
                'value', TypeInfo('uint32', int),
                attributes = {
                    MemberAttribute.PROPERTY_NAME : 'value',
                    MemberAttribute.CONSTRAINT : 'self.value < self.param'
                }
            ),
            MemberInfo(
                'text', TypeInfo('string', str),
                attributes={
                    MemberAttribute.PROPERTY_NAME : 'text'
                }
            ),
            MemberInfo(
                'data', TypeInfo('extern', BitBuffer),
                attributes={
                    MemberAttribute.PROPERTY_NAME : 'data'
                }
            ),
            MemberInfo(
                'dummyEnum', DummyEnum.type_info(),
                attributes={
                    MemberAttribute.PROPERTY_NAME : 'dummy_enum'
                }
            ),
            MemberInfo(
                'dummyBitmask', DummyBitmask.type_info(),
                attributes={
                    MemberAttribute.PROPERTY_NAME : 'dummy_bitmask'
                }
            )
        ]
        parameter_list: typing.List[MemberInfo] = [
            MemberInfo(
                'param', TypeInfo('uint32', int),
                attributes = {
                    MemberAttribute.PROPERTY_NAME : 'param'
                }
            )
        ]
        attribute_list = {
            TypeAttribute.FIELDS : field_list,
            TypeAttribute.PARAMETERS : parameter_list
        }

        return TypeInfo("DummyNested", DummyNested, attributes=attribute_list)

    @property
    def param(self) -> int:
        return self._param_

    @property
    def value(self) -> int:
        return self._value_

    @value.setter
    def value(self, value_: int) -> None:
        self._value_ = value_

    @property
    def text(self) -> str:
        return self._text_

    @text.setter
    def text(self, text) -> None:
        self._text_ = text

    @property
    def data(self) -> typing.Union[BitBuffer, None]:
        return self._data_

    @data.setter
    def data(self, data_: typing.Union[BitBuffer, None]) -> None:
        self._data_ = data_

    @property
    def dummy_enum(self) -> typing.Union[DummyEnum, None]:
        return self._dummy_enum_

    @dummy_enum.setter
    def dummy_enum(self, dummy_enum_: typing.Union[DummyEnum, None]) -> None:
        self._dummy_enum_ = dummy_enum_

    @property
    def dummy_bitmask(self) -> typing.Union[DummyBitmask, None]:
        return self._dummy_bitmask_

    @dummy_bitmask.setter
    def dummy_bitmask(self, dummy_bitmask_: typing.Union[DummyBitmask, None]) -> None:
        self._dummy_bitmask_ = dummy_bitmask_

class DummyObject:
    def __init__(self, value_: int = int(),
                 nested_: typing.Optional[DummyNested] = None,
                 text_: str = str(),
                 nested_array_: typing.List[DummyNested] = None,
                 text_array_: typing.List[str] = None) -> None:
        self._value_ = value_
        self._nested_ = nested_
        self._text_ = text_
        self._nested_array_ = Array(ObjectArrayTraits(None, None, None), nested_array_, is_auto=True)
        self._text_array = Array(StringArrayTraits(), text_array_, is_auto=True)
        self._extern_array_ = None
        self._optional_bool_ = None
        self._optional_nested_ = None

    @staticmethod
    def type_info() -> TypeInfo:
        field_list: typing.List[MemberInfo] = [
            MemberInfo(
                'value', TypeInfo('uint32', int),
                attributes={
                    MemberAttribute.PROPERTY_NAME : 'value'
                }
            ),
            MemberInfo(
                'nested', DummyNested.type_info(),
                attributes={
                    MemberAttribute.PROPERTY_NAME : 'nested',
                    MemberAttribute.TYPE_ARGUMENTS : [(lambda self, zserio_index: self.value)]
                }
            ),
            MemberInfo(
                'text', TypeInfo('string', str),
                attributes={
                    MemberAttribute.PROPERTY_NAME : 'text'
                }
            ),
            MemberInfo(
                'nestedArray', DummyNested.type_info(),
                attributes={
                    MemberAttribute.PROPERTY_NAME : 'nested_array',
                    MemberAttribute.ARRAY_LENGTH : None,
                    MemberAttribute.TYPE_ARGUMENTS : [(lambda self, zserio_index: self.value)]
                }
            ),
            MemberInfo(
                'textArray', TypeInfo('string', str),
                attributes={
                    MemberAttribute.PROPERTY_NAME: 'text_array',
                    MemberAttribute.ARRAY_LENGTH: None
                }
            ),
            MemberInfo(
                'externArray', TypeInfo('extern', BitBuffer),
                attributes={
                    MemberAttribute.PROPERTY_NAME : 'extern_array',
                    MemberAttribute.ARRAY_LENGTH : None,
                    MemberAttribute.OPTIONAL : None,
                    MemberAttribute.IS_USED_INDICATOR_NAME : 'is_extern_array_used',
                    MemberAttribute.IS_SET_INDICATOR_NAME : 'is_extern_array_set'
                }
            ),
            MemberInfo(
                'optionalBool', TypeInfo('bool', bool),
                attributes={
                    MemberAttribute.PROPERTY_NAME : 'optional_bool',
                    MemberAttribute.OPTIONAL : None,
                    MemberAttribute.IS_USED_INDICATOR_NAME : 'is_optional_bool_used',
                    MemberAttribute.IS_SET_INDICATOR_NAME : 'is_optional_bool_set'
                }
            ),
            MemberInfo(
                'optionalNested', DummyNested.type_info(),
                attributes={
                    MemberAttribute.PROPERTY_NAME : 'optional_nested',
                    MemberAttribute.OPTIONAL : None,
                    MemberAttribute.IS_USED_INDICATOR_NAME : 'is_optional_nested_used',
                    MemberAttribute.IS_SET_INDICATOR_NAME : 'is_optional_nested_set',
                    MemberAttribute.TYPE_ARGUMENTS : [(lambda self, zserio_index: self.value)]
                }
            )
        ]
        attribute_list = {
            TypeAttribute.FIELDS : field_list
        }

        return TypeInfo("DummyObject", DummyObject, attributes=attribute_list)

    @property
    def value(self) -> int:
        return self._value_

    @value.setter
    def value(self, value) -> None:
        self._value_ = value

    @property
    def nested(self) -> DummyNested:
        return self._nested_

    @nested.setter
    def nested(self, nested) -> None:
        self._nested_ = nested

    @property
    def text(self) -> str:
        return self._text_

    @text.setter
    def text(self, text) -> None:
        self._text_ = text

    @property
    def nested_array(self) -> typing.List[DummyNested]:
        return self._nested_array_.raw_array

    @nested_array.setter
    def nested_array(self, nested_array_: typing.List[DummyNested]) -> None:
        self._nested_array_ = Array(ObjectArrayTraits(None, None, None), nested_array_, is_auto=True)

    @property
    def text_array(self) -> typing.List[str]:
        return self._text_array.raw_array

    @text_array.setter
    def text_array(self, text_array_: typing.List[str]) -> None:
        self._text_array = Array(StringArrayTraits(), text_array_, is_auto=True)

    @property
    def extern_array(self) -> typing.List[BitBuffer]:
        return self._extern_array_.raw_array

    @extern_array.setter
    def extern_array(self, extern_array_: typing.List[BitBuffer]) -> None:
        self._extern_array_ = Array(BitBufferArrayTraits(), extern_array_, is_auto=True)

    @property
    def optional_bool(self) -> typing.Optional[bool]:
        return self._optional_bool_

    @optional_bool.setter
    def optional_bool(self, optional_bool_: typing.Optional[bool]) -> None:
        self._optional_bool_ = optional_bool_

    @property
    def optional_nested(self) -> typing.Optional[DummyNested]:
        return self._optional_nested_

    @optional_nested.setter
    def optional_nested(self, optional_nested_: typing.Optional[DummyNested]) -> None:
        self._optional_nested_ = optional_nested_

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
            creator.get_member_type("nonexistent")

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
            creator.get_member_type("nonexistent")

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

        with self.assertRaises(PythonRuntimeException) as error:
            json_reader.read(DummyObject.type_info())

        self.assertTrue(str(error.exception).startswith("JsonParser line 2:"))

    def test_wrong_key_exception(self):
        json_reader = JsonReader(io.StringIO("{\"value\": 13,\n\"nonexisting\": 10}"))

        with self.assertRaises(PythonRuntimeException) as error:
            json_reader.read(DummyObject.type_info())

        self.assertTrue(str(error.exception).endswith("(JsonParser line 2)"))

    def test_wrong_value_type_exception(self):
        json_reader = JsonReader(io.StringIO("{\n  \"value\": \"13\"\n}"))

        with self.assertRaises(PythonRuntimeException) as error:
            json_reader.read(DummyObject.type_info())

        self.assertTrue(str(error.exception).endswith("(JsonParser line 2)"))

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

        self.assertTrue(str(error.exception).endswith("(JsonParser line 12)"))

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

        self.assertTrue(str(error.exception).endswith("(JsonParser line 12)"))

    def test_json_array(self):
        json_reader = JsonReader(io.StringIO("[1, 2]"))

        with self.assertRaises(PythonRuntimeException):
            json_reader.read(DummyObject.type_info())

    def test_json_value(self):
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
