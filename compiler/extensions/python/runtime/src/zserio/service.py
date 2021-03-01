"""
The module provides classes for Zserio services.
"""

import typing

from zserio.exception import PythonRuntimeException

class ServiceInterface:
    """
    Generic interface for all Zserio services.
    """

    def call_method(self, method_name: str, request_data: bytes, context: typing.Any = None) -> bytes:
        """
        Calls method with the given name synchronously.

        :param method_name: Name of the service method to call.
        :param request_data: Request data to be passed to the method.
        :param context: Context specific for particular service.
        :returns: Response data.
        :raises ServiceException: If the call fails.
        """
        raise NotImplementedError()

class ServiceException(PythonRuntimeException):
    """
    Exception thrown in case of an error in Zserio Service
    """
