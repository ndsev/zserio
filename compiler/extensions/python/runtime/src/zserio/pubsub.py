"""
The module provides classes for Zserio Pub/Sub.
"""

import typing

from zserio.exception import PythonRuntimeException

class PubsubInterface:
    """ Interface for Pub/Sub client backends. """

    def publish(self, topic: str, data: bytes, context: typing.Any = None) -> None:
        """
        Publishes given data as a specified topic.

        :param topic: Topic definition.
        :param data: Data to publish.
        :param context: Context specific for a particular Pub/Sub implementation.
        :raises PubsubException: If the publishing fails.
        """
        raise NotImplementedError()

    def subscribe(self, topic: str, callback: typing.Callable[[str, bytes], None],
                  context: typing.Any = None) -> int:
        """
        Subscribes a topic.

        :param topic: Topic definition to subscribe. Note that the definition format depends on the particular
                     Pub/Sub backend implementation and therefore e.g. wildcards can be used only if they are
                     supported by Pub/Sub backend.
        :param callback: Callback to be called when a message with the specified topic arrives.
        :param context: Context specific for a particular Pub/Sub implementation.
        :returns: Subscription ID.
        :raises PubsubException: If subscribing fails.
        """
        raise NotImplementedError()

    def unsubscribe(self, subscription_id: int) -> None:
        """
        Unsubscribes the subscription with the given ID.

        :param id: ID of the subscription to be unsubscribed.
        :raises PubsubException: If unsubscribing fails.
        """
        raise NotImplementedError()

class PubsubException(PythonRuntimeException):
    """
    Exception thrown in case of an error in Zserio Pub/Sub.
    """
