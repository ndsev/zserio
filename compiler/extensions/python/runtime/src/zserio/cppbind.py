"""
The module provides helper for importing of optimized C++ classes.
"""

import importlib
import os
import typing

ZSERIO_PYTHON_IMPLEMENTATION_ENV = "ZSERIO_PYTHON_IMPLEMENTATION"
ZSERIO_CPP_MODULE = "zserio_cpp"


def import_cpp_class(cppname: str, *, exception_class=None) -> typing.Optional[typing.Type[typing.Any]]:
    """
    Tries to import optimized C++ implementation of the given python class if 'ZSERIO_PYTHON_IMPLEMENTATION'
    environment variable is either unset or set to 'cpp'.

    Depending on the content of the 'ZSERIO_PYTHON_IMPLEMENTATION' environment variable,
    it either fails when no C++ implementation is available ('cpp') or ignores missing implementation
    and just return the original python class (None) or even does not try to load the C++ implementation if
    the variable is set to anyhing else (e.g. 'python').

    :param pyclass: Pure python class implemenation for which the C++ optimized version should be loaded.
    :param cppname: Name of optimized C++ class in case that it differs from the pyclass name.
    :param exception_class: Exception to raise in case of an error.
    :returns: Requested implemenation of the given pyclass.
    :raises PythonRuntimeException: When the requested implementation is not available.
    """

    if exception_class is None:
        # we need to break cyclic import from zserio.exception
        # pylint: disable-next=cyclic-import,import-outside-toplevel
        from zserio.exception import PythonRuntimeException

        exception_class = PythonRuntimeException

    impl = os.getenv(ZSERIO_PYTHON_IMPLEMENTATION_ENV)
    if not impl in [None, "python", "cpp", "c++"]:
        raise exception_class(f"Zserio Python runtime implementation '{impl}' is not available!")

    if impl != "python":
        try:
            return getattr(importlib.import_module(ZSERIO_CPP_MODULE), cppname)
        except (ImportError, AttributeError) as err:
            if impl in ["cpp", "c++"]:
                message = f"Zserio C++ implementation of '{cppname}' is not available!"
                raise exception_class(message) from err
    return None
