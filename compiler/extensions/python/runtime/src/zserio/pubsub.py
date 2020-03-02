"""
The module provides classes for Zserio Pub/Sub.
"""

from zserio.exception import PythonRuntimeException

class PubsubInterface:
    """ Interface for Pub/Sub client backends. """

    def publish(self, topic, data, context):
        """
        Publishes given data as a specified topic.

        :param topic: Topic definition.
        :param data: Data to publish.
        :param context: Context specific for a particular Pub/Sub implementation.
        :raises PubsubException: If the publishing fails.
        """
        raise NotImplementedError()

    def reserveId(self):
        """
        Reserves subscription ID for a new subscription.

        The ID is released back to the Pub/Sub once unsubscribe is called.

        :returns: Reserved subscription ID.
        """
        raise NotImplementedError()

    def subscribe(self, subscriptionId, topic, callback, context):
        """
        Subscribes a topic.

        :param id: Subscription ID to use.
        :param topic: Topic definition to subscribe. Note that the definition format depends on the particular
                     Pub/Sub backend implementation and therefore e.g. wildcards can be used only if they are
                     supported by Pub/Sub backend.
        :param callback: Callback to be called when a message with the specified topic arrives.
        :param context: Context specific for a particular Pub/Sub implementation.
        :raises PubsubException: If subscribing fails.
        """
        raise NotImplementedError()

    def unsubscribe(self, subscriptionId):
        """
        Unsubscribes the subscription under the given ID (if any) and releases the reserved ID.

        :note: Unsubscribe can be safely called to release the reserved ID even if no subscription was done.

        :param id: ID of the subscription to be unsubscribed.
        :raise PubsubException: If unsubscribing fails.
        """
        raise NotImplementedError()

class PubsubException(PythonRuntimeException):
    """
    Exception thrown in case of an error in Zserio Pub/Sub.
    """
