import zserio

class TestPubsub(zserio.PubsubInterface):
    def __init__(self):
        self._subscriptions = {}
        self._numIds = 0

    def publish(self, topic, data, context=None):
        if context:
            context.seenByPubsub = True

        for subscription in self._subscriptions.values():
            if subscription["topic"] == topic:
                subscription["callback"](topic, data)

    def subscribe(self, topic, callback, context=None):
        if context:
            context.seenByPubsub = True

        subscriptionId = self._numIds
        self._numIds += 1
        self._subscriptions[subscriptionId] = {"topic": topic, "callback": callback}
        return subscriptionId

    def unsubscribe(self, subscriptionId):
        if not subscriptionId in self._subscriptions:
            raise zserio.PubsubException("TestPubsub: Invalid subscription ID '%d'!" % subscriptionId)

        self._subscriptions.pop(subscriptionId)

class TestPubsubContext:
    def __init__(self):
        self.seenByPubsub = False
