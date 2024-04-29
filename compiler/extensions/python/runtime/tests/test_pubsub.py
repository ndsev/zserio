import unittest

from zserio import PubsubInterface


class PubsubTest(unittest.TestCase):
    def test_publish(self):
        pubsub = PubsubInterface()
        with self.assertRaises(NotImplementedError):
            pubsub.publish("topic", bytes(), None)

    def test_subscribe(self):
        pubsub = PubsubInterface()
        with self.assertRaises(NotImplementedError):
            pubsub.subscribe("topic", None, None)

    def test_unsubscribe(self):
        pubsub = PubsubInterface()
        with self.assertRaises(NotImplementedError):
            pubsub.unsubscribe(0)
