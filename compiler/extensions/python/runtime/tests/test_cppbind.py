import unittest
import os

from zserio.cppbind import import_cpp_class
from zserio.exception import PythonRuntimeException


class MissingCppClass:
    pass


class CppBindTest(unittest.TestCase):

    def test_invalid_env(self):
        os.environ["ZSERIO_PYTHON_IMPLEMENTATION"] = "invalid"
        with self.assertRaises(PythonRuntimeException):
            import_cpp_class("MissingCppClass")

    def test_missing_cpp_default(self):
        os.environ.pop("ZSERIO_PYTHON_IMPLEMENTATION")
        self.assertIsNone(import_cpp_class("MissingCppClass"))

    def test_missing_cpp_python(self):
        os.environ["ZSERIO_PYTHON_IMPLEMENTATION"] = "python"
        self.assertIsNone(import_cpp_class("MissingCppClass"))

    def test_missing_cpp_cpp(self):
        os.environ["ZSERIO_PYTHON_IMPLEMENTATION"] = "cpp"
        with self.assertRaises(PythonRuntimeException):
            import_cpp_class("MissingCppClass")
