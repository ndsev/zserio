import zserio
import zserio_runtime
import pubsub_poc.api as api

from functools import partial

class Subscriber:
    def __init__(self, topic, callback):
        self.topic = topic
        self.callback = callback

class TestPubSub(zserio_runtime.PubSubInterface):
    def __init__(self):
        self._subscribers = {}
        self._numIds = 0

    def publish(self, topic, data, context=None):
        for subscriber in self._subscribers.values():
            if topic == subscriber.topic:
                subscriber.callback(topic, data)

    def subscribe(self, topic, callback, context=None):
        subscriptionId = self._numIds
        self._numIds += 1
        self._subscribers[subscriptionId] = Subscriber(topic, callback)
        return subscriptionId

    def unsubscribe(self, subscriptionId):
        self._subscribers.pop(subscriptionId)

if __name__ == "__main__":
    testPubSub = TestPubSub()

    simplePubSub = api.SimplePubSub(testPubSub)
    int32Value = api.Int32Value.fromFields(13)
    simplePubSub.subscribeInt32ValueSub(lambda topic, value: (
        print("got topic=", topic, ",value=", value.getValue()
    )))
    simplePubSub.publishInt32ValuePub(int32Value)
