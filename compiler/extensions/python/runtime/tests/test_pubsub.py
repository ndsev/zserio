import unittest

from zserio import PubsubInterface

class PubsubTest(unittest.TestCase):
    def testPublish(self):
        pubsub = PubsubInterface()
        with self.assertRaises(NotImplementedError):
            pubsub.publish("topic", bytes(), None)

    def testReserveId(self):
        pubsub = PubsubInterface()
        with self.assertRaises(NotImplementedError):
            pubsub.reserveId()

    def testSubscribe(self):
        pubsub = PubsubInterface()
        with self.assertRaises(NotImplementedError):
            pubsub.subscribe(0, "topic", None, None)

    def testUnsubscribe(self):
        pubsub = PubsubInterface()
        with self.assertRaises(NotImplementedError):
            pubsub.unsubscribe(0)
