import io
import typing
import unittest

from zserio.exception import PythonRuntimeException
from zserio.array import Array, ObjectArrayTraits, StringArrayTraits
from zserio.creator import ZserioTreeCreator
from zserio.json import JsonReader
from zserio.typeinfo import TypeInfo, TypeAttribute, MemberInfo, MemberAttribute

class DummyNested:
    def __init__(self, param_ : int, value_ : int = int(),  text_ : str = str()) -> None:
        self._param_ = param_
        self._value_ = value_
        self._text_ = text_

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

    def test_create_object_set_nested(self):
        creator = ZserioTreeCreator(DummyObject.type_info())

        creator.begin_root()
        creator.set_value("value", 13)
        creator.set_value("text", "test")
        creator.begin_compound("nested")
        creator.set_value("value", 10)
        creator.set_value("text", "nested")
        creator.end_compound()
        creator.begin_array("nestedArray")
        creator.begin_compound_element()
        creator.set_value("value", 5)
        creator.set_value("text", "nestedArray")
        creator.end_compound_element()
        creator.end_array()
        creator.begin_array("textArray")
        creator.add_value_element("this")
        creator.add_value_element("is")
        creator.add_value_element("text")
        creator.add_value_element("array")
        creator.end_array()
        obj = creator.end_root()

        self.assertEqual(13, obj.value)
        self.assertEqual(13, obj.nested.param)
        self.assertEqual(10, obj.nested.value)
        self.assertEqual("nested", obj.nested.text)
        self.assertEqual("test", obj.text)
        self.assertEqual(1, len(obj.nested_array))
        self.assertEqual(5, obj.nested_array[0].value)
        self.assertEqual("nestedArray", obj.nested_array[0].text)
        self.assertEqual(["this", "is", "text", "array"], obj.text_array)

    def test_exception_before_root(self):
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

    def test_create_object(self):
        json_reader = JsonReader(DummyObject.type_info())

        text_io = io.StringIO(
            "{\n"
            "    \"value\": 13,\n"
            "    \"nested\": {\n"
            "        \"value\": 10,\n"
            "        \"text\": \"nested\"\n"
            "    },\n"
            "    \"text\": \"test\",\n"
            "    \"nestedArray\": [\n"
            "        {\n"
            "            \"value\": 5,\n"
            "            \"text\": \"nestedArray\"\n"
            "        }\n"
            "    ],\n"
            "    \"textArray\": [\n"
            "        \"this\",\n"
            "        \"is\",\n"
            "        \"text\",\n"
            "        \"array\"\n"
            "    ]\n"
            "}"
        )

        obj = json_reader.read(text_io)

        self.assertEqual(13, obj.value)
        self.assertEqual(13, obj.nested.param)
        self.assertEqual(10, obj.nested.value)
        self.assertEqual("nested", obj.nested.text)
        self.assertEqual("test", obj.text)
        self.assertEqual(1, len(obj.nested_array))
        self.assertEqual(5, obj.nested_array[0].value)
        self.assertEqual("nestedArray", obj.nested_array[0].text)
        self.assertEqual(["this", "is", "text", "array"], obj.text_array)

    def test_json_array(self):
        json_reader = JsonReader(DummyObject.type_info())

        with self.assertRaises(PythonRuntimeException):
            json_reader.read(io.StringIO("[1, 2]"))

    def test_json_value(self):
        json_reader = JsonReader(DummyObject.type_info())

        with self.assertRaises(PythonRuntimeException):
            json_reader.read(io.StringIO("\"text\""))

    def test_creator_adapter_unexpected_get(self):
        creator = ZserioTreeCreator(DummyObject.type_info())
        creator_adapter = JsonReader._CreatorAdapter(creator)

        with self.assertRaises(PythonRuntimeException):
            creator_adapter.get()
