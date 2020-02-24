import functools

import zserio

import pubsub_poc.Int32Value

class GreaterThanTenProvider:
    def __init__(self, client):
        self._client = client # PubSubClient interface

        self._subscribersInt32Value = {}

    def publishGreaterThanTen(self, zserioObject, context=None):
        self._publishZserioObject("pubsub/boolean/greaterThanTen", zserioObject, context)

    def subscribeRequest(self, callback, context=None):
        topic = "pubsub/request"
        subscriptionId = self._client.subscribe(topic, self._onRawInt32Value, context)
        self._subscribersInt32Value[subscriptionId] = callback
        return subscriptionId

    def unsubscribeRequest(self, subscriptionId):
        self._client.unsubscribe(subscriptionId)
        self._subscribersInt32Value.pop(subscriptionId)

    def _onRawInt32Value(self, subscriptionId, topic, data):
        reader = zserio.BitStreamReader(data)
        int32Value = pubsub_poc.Int32Value.Int32Value.fromReader(reader)

        self._subscribersInt32Value[subscriptionId](topic, int32Value)

    def _publishZserioObject(self, topic, zserioObject, context):
        writer = zserio.BitStreamWriter()
        zserioObject.write(writer)
        self._client.publish(topic, writer.getByteArray(), context)
