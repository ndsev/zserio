import io
import unittest

from zserio.debugstring import to_debug_stream, to_debug_string, to_debug_file
from zserio.typeinfo import TypeInfo, TypeAttribute, MemberInfo, MemberAttribute
from zserio.walker import DepthWalkFilter, DefaultWalkFilter

class DummyObject:
    @staticmethod
    def type_info():
        return TypeInfo("DummyObject", DummyObject,
            attributes={TypeAttribute.FIELDS : [
                MemberInfo("text", TypeInfo("str", str), attributes={
                    MemberAttribute.PROPERTY_NAME: "text"
                })
            ]}
        )

    @property
    def text(self):
        return "test"

class DebugStringTest(unittest.TestCase):

    def test_to_debug_stream_default(self):
        obj = DummyObject()
        text_io = io.StringIO()
        to_debug_stream(obj, text_io)
        self.assertEqual("{\n    \"text\": \"test\"\n}", text_io.getvalue())

    def test_to_debug_stream_indent_2(self):
        obj = DummyObject()
        text_io = io.StringIO()
        to_debug_stream(obj, text_io, indent=2)
        self.assertEqual("{\n  \"text\": \"test\"\n}", text_io.getvalue())

    def test_to_debug_stream_indent_str(self):
        obj = DummyObject()
        text_io = io.StringIO()
        to_debug_stream(obj, text_io, indent=" ")
        self.assertEqual("{\n \"text\": \"test\"\n}", text_io.getvalue())

    def test_to_debug_stream_filter(self):
        obj = DummyObject()
        text_io = io.StringIO()
        to_debug_stream(obj, text_io, walk_filter=DepthWalkFilter(0))
        self.assertEqual("{\n}", text_io.getvalue())

    def test_to_debug_stream_indent_2_filter(self):
        obj = DummyObject()
        text_io = io.StringIO()
        to_debug_stream(obj, text_io, indent=2, walk_filter=DefaultWalkFilter())
        self.assertEqual("{\n  \"text\": \"test\"\n}", text_io.getvalue())

    def test_to_debug_string_default(self):
        obj = DummyObject()
        self.assertEqual("{\n    \"text\": \"test\"\n}", to_debug_string(obj))

    def test_to_debug_string_indent_2(self):
        obj = DummyObject()
        self.assertEqual("{\n  \"text\": \"test\"\n}", to_debug_string(obj, indent=2))

    def test_to_debug_string_indent_str(self):
        obj = DummyObject()
        self.assertEqual("{\n   \"text\": \"test\"\n}", to_debug_string(obj, indent="   "))

    def test_to_debug_string_filter(self):
        obj = DummyObject()
        self.assertEqual("{\n}", to_debug_string(obj, walk_filter=DepthWalkFilter(0)))

    def test_to_debug_string_indent_2_filter(self):
        obj = DummyObject()
        self.assertEqual("{\n  \"text\": \"test\"\n}",
                         to_debug_string(obj, indent=2, walk_filter=DefaultWalkFilter()))

    def test_to_debug_file_default(self):
        obj = DummyObject()
        to_debug_file(obj, self.TEST_FILE_NAME)
        with open(self.TEST_FILE_NAME, "r", encoding="utf-8") as text_io:
            self.assertEqual("{\n    \"text\": \"test\"\n}", text_io.read())

    def test_to_debug_file_indent_2(self):
        obj = DummyObject()
        to_debug_file(obj, self.TEST_FILE_NAME, indent=2)
        with open(self.TEST_FILE_NAME, "r", encoding="utf-8") as text_io:
            self.assertEqual("{\n  \"text\": \"test\"\n}", text_io.read())

    def test_to_debug_file_indent_str(self):
        obj = DummyObject()
        to_debug_file(obj, self.TEST_FILE_NAME, indent=" ")
        with open(self.TEST_FILE_NAME, "r", encoding="utf-8") as text_io:
            self.assertEqual("{\n \"text\": \"test\"\n}", text_io.read())

    def test_to_debug_file_filter(self):
        obj = DummyObject()
        to_debug_file(obj, self.TEST_FILE_NAME, walk_filter=DepthWalkFilter(0))
        with open(self.TEST_FILE_NAME, "r", encoding="utf-8") as text_io:
            self.assertEqual("{\n}", text_io.read())

    def test_to_debug_file_indent_2_filter(self):
        obj = DummyObject()
        to_debug_file(obj, self.TEST_FILE_NAME, indent=2, walk_filter=DefaultWalkFilter())
        with open(self.TEST_FILE_NAME, "r", encoding="utf-8") as text_io:
            self.assertEqual("{\n  \"text\": \"test\"\n}", text_io.read())

    TEST_FILE_NAME = "DebugStringTest.bin"
