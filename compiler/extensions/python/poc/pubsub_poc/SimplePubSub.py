import functools

import zserio

import pubsub_poc.UInt64Value
import pubsub_poc.Int32Value
import pubsub_poc.BoolValue

class SimplePubSub:
    def __init__(self, client):
        self._client = client # PubSubClient interface

        self._subscribersUint64ValueSub = {}
        self._subscribersInt32ValueSub = {}
        self._subscribersBoolValueSub = {}

    def publishUint64ValuePub(self, zserioObject, context=None):
        self._publishZserioObject("pubsub/uint64", zserioObject, context)

    def publishInt32ValuePub(self, zserioObject, context=None):
        self._publishZserioObject("pubsub/int32", zserioObject, context)

    def publishBoolValuePub(self, zserioObject, context=None):
        self._publishZserioObject("pubsub/bool", zserioObject, context)

    def subscribeUint64ValueSub(self, callback, context=None):
        topic = "pubsub/uint64"
        subscriptionId = self._client.subscribe(topic, self._onRawUint64ValueSub, context)
        self._subscribersUint64ValueSub[subscriptionId] = callback
        return subscriptionId

    def subscribeInt32ValueSub(self, callback, context=None):
        topic = "pubsub/int32"
        subscriptionId = self._client.subscribe(topic, self._onRawInt32ValueSub, context)
        self._subscribersInt32ValueSub[subscriptionId] = callback
        return subscriptionId

    def subscribeBoolValueSub(self, callback, context=None):
        topic = "pubsub/bool"
        subscriptionId = self._client.subscribe(topic, self._onRawBoolValueSub, context)
        self._subscribersBoolValueSub[subscriptionId] = callback
        return subscriptionId

    def unsubscribeUint64ValueSub(self, subscriptionId):
        self._subscribersUint64ValueSub.pop(subscriptionId)

    def unsubscribeInt32ValueSub(self, subscriptionId):
        self._subscribersInt32ValueSub.pop(subscriptionId)

    def unsubscribeBoolValueSub(self, subscriptionId):
        self._subscribersBoolValueSub.pop(subscriptionId)

    def _onRawUint64ValueSub(self, topic, data):
        reader = zserio.BitStreamReader(data)
        uint64Value = pubsub_poc.UInt64Value.UInt64Value.fromReader(reader)

        for subscriberCallback in self._subscribersUint64ValueSub.values():
            subscriberCallback(topic, uint64Value)

    def _onRawInt32ValueSub(self, topic, data):
        reader = zserio.BitStreamReader(data)
        int32Value = pubsub_poc.Int32Value.Int32Value.fromReader(reader)

        for subscriberCallback in self._subscribersInt32ValueSub.values():
            subscriberCallback(topic, int32Value)

    def _onRawBoolValueSub(self, topic, data):
        reader = zserio.BitStreamReader(data)
        boolValue = pubsub_poc.BoolValue.BoolValue.fromReader(reader)

        for subscriberCallback in self._subscribersBoolValueSub.values():
            subscriberCallback(topic, boolValue)

    def _publishZserioObject(self, topic, zserioObject, context):
        writer = zserio.BitStreamWriter()
        zserioObject.write(writer)
        self._client.publish(topic, writer.getByteArray(), context)
