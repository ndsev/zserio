"""
The module provides classes for Zserio services.
"""

from zserio.exception import PythonRuntimeException

class ServiceInterface:
    """
    Generic interface for all Zserio services.
    """

    def callMethod(self, methodName, requestData, context=None):
        """
        Calls method with the given name synchronously.

        :param methodName: Name of the service method to call.
        :param requestData: Request data to be passed to the method.
        :param context: Context specific for particular service.
        :returns: Response data.
        :raises ServiceException: If the call fails.
        """
        raise NotImplementedError()

class ServiceException(PythonRuntimeException):
    """
    Exception thrown in case of an error in Zserio Service
    """
