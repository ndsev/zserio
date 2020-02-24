import functools

import zserio

import pubsub_poc.UInt64Value
import pubsub_poc.BoolValue

class Client:
    def __init__(self, client):
        self._client = client # PubSubClient interface

        self._subscribersUInt64Value = {}
        self._subscribersBoolValue = {}

    def publishRequest(self, zserioObject, context=None):
        self._publishZserioObject("pubsub/request", zserioObject, context)

    def subscribePowerOfTwo(self, callback, context=None):
        topic = "pubsub/powerOfTwo"
        subscriptionId = self._client.subscribe(topic, self._onRawUInt64Value, context)
        self._subscribersUInt64Value[subscriptionId] = callback
        return subscriptionId

    def subscribeBooleanResponse(self, callback, context=None):
        topic = "pubsub/boolean/#"
        subscriptionId = self._client.subscribe(topic, self._onRawBoolValue, context)
        self._subscribersBoolValue[subscriptionId] = callback
        return subscriptionId

    def unsubscribePowerOfTwo(self, subscriptionId):
        self._client.unsubscribe(subscriptionId)
        self._subscribersUInt64Value.pop(subscriptionId)

    def unsubscribeBooleanResponse(self, subscriptionId):
        self._client.unsubscribe(subscriptionId)
        self._subscribersBoolValue.pop(subscriptionId)

    def _onRawUInt64Value(self, subscriptionId, topic, data):
        reader = zserio.BitStreamReader(data)
        uint64Value = pubsub_poc.UInt64Value.UInt64Value.fromReader(reader)

        self._subscribersUInt64Value[subscriptionId](topic, uint64Value)

    def _onRawBoolValue(self, subscriptionId, topic, data):
        reader = zserio.BitStreamReader(data)
        boolValue = pubsub_poc.BoolValue.BoolValue.fromReader(reader)

        self._subscribersBoolValue[subscriptionId](topic, boolValue)

    def _publishZserioObject(self, topic, zserioObject, context):
        writer = zserio.BitStreamWriter()
        zserioObject.write(writer)
        self._client.publish(topic, writer.getByteArray(), context)
