import zserio

class TestPubsub(zserio.PubsubInterface):
    def __init__(self):
        self._subscriptions = {}
        self._numIds = 0

    def publish(self, topic, data, context):
        if context:
            context.seenByPubsub = True

        for subscriptionId, subscription in self._subscriptions.items():
            if subscription["topic"] == topic:
                subscription["callback"](subscriptionId, topic, data)

    def reserveId(self):
        subscriptionId = self._numIds
        self._numIds += 1
        return subscriptionId

    def subscribe(self, subscriptionId, topic, callback, context):
        if context:
            context.seenByPubsub = True

        self._subscriptions[subscriptionId] = {"topic": topic, "callback": callback}

    def unsubscribe(self, subscriptionId):
        if subscriptionId in self._subscriptions:
            self._subscriptions.pop(subscriptionId)

class TestPubsubContext:
    def __init__(self):
        self.seenByPubsub = False
