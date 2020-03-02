import unittest
import zserio

from testutils import getZserioApi

from TestPubsub import TestPubsub, TestPubsubContext

class SimplePubsubTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "pubsub_types.zs").simple_pubsub

    def setUp(self):
        pubsub = TestPubsub()
        self.simplePubsubProvider = self.api.SimplePubsubProvider(pubsub)
        self.simplePubsubClient = self.api.SimplePubsubClient(pubsub)
        self.simplePubsub = self.api.SimplePubsub(pubsub)

    def testPowerOfTwoClientAndProvider(self):
        def requestCallback(topic, value):
            self.assertEqual("simple_pubsub/request", topic)
            result = self.api.UInt64Value.fromFields(value.getValue() * value.getValue())
            self.simplePubsubProvider.publishPowerOfTwo(result)

        self.simplePubsubProvider.subscribeRequest(requestCallback)

        result = {"value": 0}
        def powerOfTwoCallback(topic, value):
            self.assertEqual("simple_pubsub/power_of_two", topic)
            result["value"] = value.getValue()

        self.simplePubsubClient.subscribePowerOfTwo(powerOfTwoCallback)

        request = self.api.Int32Value.fromFields(13)
        self.simplePubsubClient.publishRequest(request)
        self.assertEqual(169, result["value"])

        request.setValue(-13)
        self.simplePubsubClient.publishRequest(request)
        self.assertEqual(169, result["value"])

        request.setValue(2)
        self.simplePubsubClient.publishRequest(request)
        self.assertEqual(4, result["value"])

        request.setValue(-2)
        self.simplePubsubClient.publishRequest(request)
        self.assertEqual(4, result["value"])

    def testPowerOfTwoSimplePubsub(self):
        def requestCallback(topic, value):
            self.assertEqual("simple_pubsub/request", topic)
            result = self.api.UInt64Value.fromFields(value.getValue() * value.getValue())
            self.simplePubsub.publishPowerOfTwo(result)

        self.simplePubsub.subscribeRequest(requestCallback)

        result = {"value": 0}
        def powerOfTwoCallback(topic, value):
            self.assertEqual("simple_pubsub/power_of_two", topic)
            result["value"] = value.getValue()

        self.simplePubsub.subscribePowerOfTwo(powerOfTwoCallback)

        request = self.api.Int32Value.fromFields(13)
        self.simplePubsub.publishRequest(request)
        self.assertEqual(169, result["value"])

        request.setValue(-13)
        self.simplePubsub.publishRequest(request)
        self.assertEqual(169, result["value"])

        request.setValue(2)
        self.simplePubsub.publishRequest(request)
        self.assertEqual(4, result["value"])

        request.setValue(-2)
        self.simplePubsub.publishRequest(request)
        self.assertEqual(4, result["value"])

    def testPublishRequestWithContext(self):
        context = TestPubsubContext()
        self.assertFalse(context.seenByPubsub)
        self.simplePubsub.publishRequest(self.api.Int32Value.fromFields(42), context)
        self.assertTrue(context.seenByPubsub)

    def testSubscribeRequestWithContext(self):
        context = TestPubsubContext()
        self.assertFalse(context.seenByPubsub)
        self.simplePubsub.subscribeRequest(lambda topic, value: None, context)
        self.assertTrue(context.seenByPubsub)

    def testUnsubscribe(self):
        def requestCallback(topic, value):
            self.assertEqual("simple_pubsub/request", topic)
            result = self.api.UInt64Value.fromFields(value.getValue() * value.getValue())
            self.simplePubsub.publishPowerOfTwo(result)

        id0 = self.simplePubsub.subscribeRequest(requestCallback)

        result = {"value1": 0, "value2": 0}

        def powerOfTwoCallback1(topic, value):
            self.assertEqual("simple_pubsub/power_of_two", topic)
            result["value1"] = value.getValue()

        id1 = self.simplePubsub.subscribePowerOfTwo(powerOfTwoCallback1)

        def powerOfTwoCallback2(topic, value):
            self.assertEqual("simple_pubsub/power_of_two", topic)
            result["value2"] = value.getValue()

        id2 = self.simplePubsub.subscribePowerOfTwo(powerOfTwoCallback2)

        request = self.api.Int32Value.fromFields(13)
        self.simplePubsub.publishRequest(request)
        self.assertEqual(169, result["value1"])
        self.assertEqual(169, result["value2"])

        self.simplePubsub.unsubscribe(id1)
        request.setValue(2)
        self.simplePubsub.publishRequest(request)
        self.assertEqual(169, result["value1"]) # shall not be changed!
        self.assertEqual(4, result["value2"])

        self.simplePubsub.unsubscribe(id0) # unsubscribe publisher
        request.setValue(3)
        self.simplePubsub.publishRequest(request)
        self.assertEqual(169, result["value1"]) # shall not be changed!
        self.assertEqual(4, result["value2"]) # shall not be changed!

        self.simplePubsub.unsubscribe(id2)

    def testUnsubscribeInvalid(self):
        with self.assertRaises(zserio.PubsubException):
            self.simplePubsub.unsubscribe(0)
