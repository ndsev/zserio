import unittest

from zserio import PubsubInterface

class PubsubTest(unittest.TestCase):
    def testPublish(self):
        pubsub = PubsubInterface()
        with self.assertRaises(NotImplementedError):
            pubsub.publish("topic", bytes(), None)

    def testSubscribe(self):
        pubsub = PubsubInterface()
        with self.assertRaises(NotImplementedError):
            pubsub.subscribe("topic", None, None)

    def testUnsubscribe(self):
        pubsub = PubsubInterface()
        with self.assertRaises(NotImplementedError):
            pubsub.unsubscribe(0)
