"""
The module provides classes for Zserio Pub/Sub.
"""

from zserio.exception import PythonRuntimeException

class PubSubInterface:
    def publish(self, topic, data, context):
        raise NotImplentedError()

    def subscribe(self, topic, callback, context):
        raise NotImplentedError()

    def unsubscribe(self, subscriptionId):
        raise NotImplentedError()

class PubSubException(PythonRuntimeException):
    """
    Exception thrown in case of an error in Zserio Pub/Sub
    """
