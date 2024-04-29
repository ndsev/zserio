import io
import unittest

from zserio.debugstring import (
    to_json_stream,
    to_json_string,
    to_json_file,
    from_json_stream,
    from_json_string,
    from_json_file,
)
from zserio.typeinfo import TypeInfo, TypeAttribute, MemberInfo, MemberAttribute
from zserio.walker import DepthWalkFilter, DefaultWalkFilter


class DummyObject:
    def __init__(self, text_: str = str()):
        self._text_ = text_

    @staticmethod
    def type_info():
        return TypeInfo(
            "DummyObject",
            DummyObject,
            attributes={
                TypeAttribute.FIELDS: [
                    MemberInfo(
                        "text",
                        TypeInfo("str", str),
                        attributes={MemberAttribute.PROPERTY_NAME: "text"},
                    )
                ]
            },
        )

    @property
    def text(self):
        return self._text_

    @text.setter
    def text(self, text_):
        self._text_ = text_


class ParameterizedDummyObject:
    def __init__(self, param_: int, text_: str = str()) -> None:
        self._param_: int = param_
        self._text_ = text_

    @staticmethod
    def type_info() -> TypeInfo:
        field_list = [
            MemberInfo(
                "text",
                TypeInfo("string", str),
                attributes={MemberAttribute.PROPERTY_NAME: "text"},
            )
        ]
        parameter_list = [
            MemberInfo(
                "param",
                TypeInfo("int", int),
                attributes={MemberAttribute.PROPERTY_NAME: "param"},
            )
        ]
        attribute_list = {
            TypeAttribute.FIELDS: field_list,
            TypeAttribute.PARAMETERS: parameter_list,
        }

        return TypeInfo(
            "ParameterizedDummyObject",
            ParameterizedDummyObject,
            attributes=attribute_list,
        )

    @property
    def param(self) -> int:
        return self._param_

    @property
    def text(self) -> str:
        return self._text_

    @text.setter
    def text(self, text_: str) -> None:
        self._text_ = text_


class DebugStringTest(unittest.TestCase):

    def test_to_json_stream_default(self):
        obj = DummyObject("test")
        text_io = io.StringIO()
        to_json_stream(obj, text_io)
        self.assertEqual('{\n    "text": "test"\n}', text_io.getvalue())

    def test_to_json_stream_indent_2(self):
        obj = DummyObject("test")
        text_io = io.StringIO()
        to_json_stream(obj, text_io, indent=2)
        self.assertEqual('{\n  "text": "test"\n}', text_io.getvalue())

    def test_to_json_stream_indent_str(self):
        obj = DummyObject("test")
        text_io = io.StringIO()
        to_json_stream(obj, text_io, indent=" ")
        self.assertEqual('{\n "text": "test"\n}', text_io.getvalue())

    def test_to_json_stream_filter(self):
        obj = DummyObject("test")
        text_io = io.StringIO()
        to_json_stream(obj, text_io, walk_filter=DepthWalkFilter(0))
        self.assertEqual("{\n}", text_io.getvalue())

    def test_to_json_stream_indent_2_filter(self):
        obj = DummyObject("test")
        text_io = io.StringIO()
        to_json_stream(obj, text_io, indent=2, walk_filter=DefaultWalkFilter())
        self.assertEqual('{\n  "text": "test"\n}', text_io.getvalue())

    def test_to_json_string_default(self):
        obj = DummyObject("test")
        self.assertEqual('{\n    "text": "test"\n}', to_json_string(obj))

    def test_to_json_string_indent_2(self):
        obj = DummyObject("test")
        self.assertEqual('{\n  "text": "test"\n}', to_json_string(obj, indent=2))

    def test_to_json_string_indent_str(self):
        obj = DummyObject("test")
        self.assertEqual('{\n   "text": "test"\n}', to_json_string(obj, indent="   "))

    def test_to_json_string_filter(self):
        obj = DummyObject("test")
        self.assertEqual("{\n}", to_json_string(obj, walk_filter=DepthWalkFilter(0)))

    def test_to_json_string_indent_2_filter(self):
        obj = DummyObject("test")
        self.assertEqual(
            '{\n  "text": "test"\n}',
            to_json_string(obj, indent=2, walk_filter=DefaultWalkFilter()),
        )

    def test_to_json_file_default(self):
        obj = DummyObject("test")
        to_json_file(obj, self.TEST_FILE_NAME)
        with open(self.TEST_FILE_NAME, "r", encoding="utf-8") as text_io:
            self.assertEqual('{\n    "text": "test"\n}', text_io.read())

    def test_to_json_file_indent_2(self):
        obj = DummyObject("test")
        to_json_file(obj, self.TEST_FILE_NAME, indent=2)
        with open(self.TEST_FILE_NAME, "r", encoding="utf-8") as text_io:
            self.assertEqual('{\n  "text": "test"\n}', text_io.read())

    def test_to_json_file_indent_str(self):
        obj = DummyObject("test")
        to_json_file(obj, self.TEST_FILE_NAME, indent=" ")
        with open(self.TEST_FILE_NAME, "r", encoding="utf-8") as text_io:
            self.assertEqual('{\n "text": "test"\n}', text_io.read())

    def test_to_json_file_filter(self):
        obj = DummyObject("test")
        to_json_file(obj, self.TEST_FILE_NAME, walk_filter=DepthWalkFilter(0))
        with open(self.TEST_FILE_NAME, "r", encoding="utf-8") as text_io:
            self.assertEqual("{\n}", text_io.read())

    def test_to_json_file_indent_2_filter(self):
        obj = DummyObject("test")
        to_json_file(obj, self.TEST_FILE_NAME, indent=2, walk_filter=DefaultWalkFilter())
        with open(self.TEST_FILE_NAME, "r", encoding="utf-8") as text_io:
            self.assertEqual('{\n  "text": "test"\n}', text_io.read())

    def test_from_json_stream(self):
        text_io = io.StringIO('{"text": "something"}')
        obj = from_json_stream(DummyObject, text_io)
        self.assertTrue(isinstance(obj, DummyObject))
        self.assertEqual("something", obj.text)

    def test_from_json_stream_arguments(self):
        text_io = io.StringIO('{"text": "something"}')
        obj = from_json_stream(ParameterizedDummyObject, text_io, 10)
        self.assertTrue(isinstance(obj, ParameterizedDummyObject))
        self.assertEqual(10, obj.param)
        self.assertEqual("something", obj.text)

    def test_from_json_string(self):
        json_string = '{"text": "something"}'
        obj = from_json_string(DummyObject, json_string)
        self.assertTrue(isinstance(obj, DummyObject))
        self.assertEqual("something", obj.text)

    def test_from_json_string_arguments(self):
        json_string = '{"text": "something"}'
        obj = from_json_string(ParameterizedDummyObject, json_string, 10)
        self.assertTrue(isinstance(obj, ParameterizedDummyObject))
        self.assertEqual(10, obj.param)
        self.assertEqual("something", obj.text)

    def test_from_json_file(self):
        with open(self.TEST_FILE_NAME, "w", encoding="utf-8") as text_io:
            text_io.write('{"text": "something"}')
        obj = from_json_file(DummyObject, self.TEST_FILE_NAME)
        self.assertTrue(isinstance(obj, DummyObject))
        self.assertEqual("something", obj.text)

    def test_from_json_file_arguments(self):
        with open(self.TEST_FILE_NAME, "w", encoding="utf-8") as text_io:
            text_io.write('{"text": "something"}')
        obj = from_json_file(ParameterizedDummyObject, self.TEST_FILE_NAME, 10)
        self.assertTrue(isinstance(obj, ParameterizedDummyObject))
        self.assertEqual(10, obj.param)
        self.assertEqual("something", obj.text)

    TEST_FILE_NAME = "DebugStringTest.json"
