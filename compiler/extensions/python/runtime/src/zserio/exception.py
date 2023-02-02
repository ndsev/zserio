"""
The module implements exceptions for Zserio python runtime library.
"""

from zserio.cppbind import import_cpp_class

class PythonRuntimeException(Exception):
    """
    Exception thrown in case of an error in Zserio python runtime library.
    """

PythonRuntimeException = import_cpp_class( # type: ignore
    "PythonRuntimeException", exception_class=PythonRuntimeException) or PythonRuntimeException
