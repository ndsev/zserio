"""
The module provides classes for Zserio services.
"""

import typing

from zserio.exception import PythonRuntimeException
from zserio.bitwriter import BitStreamWriter

class ServiceData:
    """
    Data abstraction to be sent or to be received in all Zserio services.
    """

    def __init__(self, zserio_object: typing.Any):
        """
        Constructor.

        :param zserio_object: Zserio object from which to create service data.
        """

        self._is_byte_array_initialized = False
        self._byte_array = bytes()
        self._zserio_object = zserio_object

    @property
    def byte_array(self) -> bytes:
        """
        Gets the data which represent the request.

        :returns: The request data which are created by serialization of Zserio object.
        """

        if not self._is_byte_array_initialized:
            writer = BitStreamWriter()
            self._zserio_object.write(writer)
            self._byte_array = writer.byte_array
            self._is_byte_array_initialized = True

        return self._byte_array

    @property
    def zserio_object(self) -> typing.Any:
        """
        Gets the Zserio object which represents the request.

        :returns: The Zserio object.
        """

        return self._zserio_object

class ServiceInterface:
    """
    Generic interface for all Zserio services on server side.
    """

    def call_method(self, method_name: str, request_data: bytes, context: typing.Any = None) -> ServiceData:
        """
        Calls method with the given name synchronously.

        :param method_name: Name of the service method to call.
        :param request_data: Request data to be passed to the method.
        :param context: Context specific for particular service.
        :returns: Response service data.
        :raises ServiceException: If the call fails.
        """
        raise NotImplementedError()

    @property
    def service_full_name(self) -> str:
        """
        Gets service full name.

        :returns: Service full name.
        """
        raise NotImplementedError()

    @property
    def method_names(self) -> typing.List:
        """
        Gets list of service method names.

        :returns: List of service method names.
        """
        raise NotImplementedError()

class ServiceClientInterface:
    """
    Generic interface for all Zserio services on client side.
    """

    def call_method(self, method_name: str, request: ServiceData, context: typing.Any = None) -> bytes:
        """
        Calls method with the given name synchronously.

        :param method_name: Name of the service method to call.
        :param request: Request service data to be passed to the method.
        :param context: Context specific for particular service.
        :returns: Response data.
        :raises ServiceException: If the call fails.
        """
        raise NotImplementedError()

class ServiceException(PythonRuntimeException):
    """
    Exception thrown in case of an error in Zserio Service
    """
