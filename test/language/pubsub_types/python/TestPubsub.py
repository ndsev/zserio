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

    def unsubscribe(self, subscription_id):
        if not subscription_id in self._subscriptions:
            raise zserio.PubsubException(f"TestPubsub: Invalid subscription ID '{subscription_id}'!")

        self._subscriptions.pop(subscription_id)


class TestPubsubContext:
    def __init__(self):
        self.seenByPubsub = False
