import zserio

import PubsubTypes

from TestPubsub import TestPubsub, TestPubsubContext


class SimplePubsubTest(PubsubTypes.TestCase):
    def setUp(self):
        pubsub = TestPubsub()
        self.simplePubsubProvider = self.api.SimplePubsubProvider(pubsub)
        self.simplePubsubClient = self.api.SimplePubsubClient(pubsub)
        self.simplePubsub = self.api.SimplePubsub(pubsub)

    def testPowerOfTwoClientAndProvider(self):
        def requestCallback(topic, value):
            self.assertEqual("simple_pubsub/request", topic)
            result = self.api.UInt64Value(value.value * value.value)
            self.simplePubsubProvider.publish_power_of_two(result)

        self.simplePubsubProvider.subscribe_request(requestCallback)

        result = {"value": 0}

        def powerOfTwoCallback(topic, value):
            self.assertEqual("simple_pubsub/power_of_two", topic)
            result["value"] = value.value

        self.simplePubsubClient.subscribe_power_of_two(powerOfTwoCallback)

        request = self.api.Int32Value(13)
        self.simplePubsubClient.publish_request(request)
        self.assertEqual(169, result["value"])

        request.value = -13
        self.simplePubsubClient.publish_request(request)
        self.assertEqual(169, result["value"])

        request.value = 2
        self.simplePubsubClient.publish_request(request)
        self.assertEqual(4, result["value"])

        request.value = -2
        self.simplePubsubClient.publish_request(request)
        self.assertEqual(4, result["value"])

    def testPowerOfTwoSimplePubsub(self):
        def requestCallback(topic, value):
            self.assertEqual("simple_pubsub/request", topic)
            result = self.api.UInt64Value(value.value * value.value)
            self.simplePubsub.publish_power_of_two(result)

        self.simplePubsub.subscribe_request(requestCallback)

        result = {"value": 0}

        def powerOfTwoCallback(topic, value):
            self.assertEqual("simple_pubsub/power_of_two", topic)
            result["value"] = value.value

        self.simplePubsub.subscribe_power_of_two(powerOfTwoCallback)

        request = self.api.Int32Value(13)
        self.simplePubsub.publish_request(request)
        self.assertEqual(169, result["value"])

        request.value = -13
        self.simplePubsub.publish_request(request)
        self.assertEqual(169, result["value"])

        request.value = 2
        self.simplePubsub.publish_request(request)
        self.assertEqual(4, result["value"])

        request.value = -2
        self.simplePubsub.publish_request(request)
        self.assertEqual(4, result["value"])

    def testPowerOfTwoRawClientAndProvider(self):
        def requestRawCallback(topic, value_data):
            self.assertEqual("simple_pubsub/request_raw", topic)
            value = zserio.deserialize_from_bytes(self.api.Int32Value, value_data)
            result = self.api.UInt64Value(value.value * value.value)
            result_data = zserio.serialize_to_bytes(result)
            self.simplePubsubProvider.publish_power_of_two_raw(result_data)

        self.simplePubsubProvider.subscribe_request_raw(requestRawCallback)

        result = {"value": 0}

        def powerOfTwoRawCallback(topic, value_data):
            self.assertEqual("simple_pubsub/power_of_two_raw", topic)
            value = zserio.deserialize_from_bytes(self.api.UInt64Value, value_data)
            result["value"] = value.value

        self.simplePubsubClient.subscribe_power_of_two_raw(powerOfTwoRawCallback)

        request = self.api.Int32Value(13)
        request_data = zserio.serialize_to_bytes(request)
        self.simplePubsubClient.publish_request_raw(request_data)
        self.assertEqual(169, result["value"])

    def testPowerOfTwoRawSimplePubsub(self):
        def requestRawCallback(topic, value_data):
            self.assertEqual("simple_pubsub/request_raw", topic)
            value = zserio.deserialize_from_bytes(self.api.Int32Value, value_data)
            result = self.api.UInt64Value(value.value * value.value)
            result_data = zserio.serialize_to_bytes(result)
            self.simplePubsub.publish_power_of_two_raw(result_data)

        self.simplePubsub.subscribe_request_raw(requestRawCallback)

        result = {"value": 0}

        def powerOfTwoRawCallback(topic, value_data):
            self.assertEqual("simple_pubsub/power_of_two_raw", topic)
            value = zserio.deserialize_from_bytes(self.api.UInt64Value, value_data)
            result["value"] = value.value

        self.simplePubsub.subscribe_power_of_two_raw(powerOfTwoRawCallback)

        request = self.api.Int32Value(13)
        request_data = zserio.serialize_to_bytes(request)
        self.simplePubsub.publish_request_raw(request_data)
        self.assertEqual(169, result["value"])

    def testPublishRequestWithContext(self):
        context = TestPubsubContext()
        self.assertFalse(context.seenByPubsub)
        self.simplePubsub.publish_request(self.api.Int32Value(42), context)
        self.assertTrue(context.seenByPubsub)

    def testSubscribeRequestWithContext(self):
        context = TestPubsubContext()
        self.assertFalse(context.seenByPubsub)
        self.simplePubsub.subscribe_request(lambda topic, value: None, context)
        self.assertTrue(context.seenByPubsub)

    def testUnsubscribe(self):
        def requestCallback(topic, value):
            self.assertEqual("simple_pubsub/request", topic)
            result = self.api.UInt64Value(value.value * value.value)
            self.simplePubsub.publish_power_of_two(result)

        id0 = self.simplePubsub.subscribe_request(requestCallback)

        result = {"value1": 0, "value2": 0}

        def powerOfTwoCallback1(topic, value):
            self.assertEqual("simple_pubsub/power_of_two", topic)
            result["value1"] = value.value

        id1 = self.simplePubsub.subscribe_power_of_two(powerOfTwoCallback1)

        def powerOfTwoCallback2(topic, value):
            self.assertEqual("simple_pubsub/power_of_two", topic)
            result["value2"] = value.value

        id2 = self.simplePubsub.subscribe_power_of_two(powerOfTwoCallback2)

        request = self.api.Int32Value(13)
        self.simplePubsub.publish_request(request)
        self.assertEqual(169, result["value1"])
        self.assertEqual(169, result["value2"])

        self.simplePubsub.unsubscribe(id1)
        request.value = 2
        self.simplePubsub.publish_request(request)
        self.assertEqual(169, result["value1"])  # shall not be changed!
        self.assertEqual(4, result["value2"])

        self.simplePubsub.unsubscribe(id0)  # unsubscribe publisher
        request.value = 3
        self.simplePubsub.publish_request(request)
        self.assertEqual(169, result["value1"])  # shall not be changed!
        self.assertEqual(4, result["value2"])  # shall not be changed!

        self.simplePubsub.unsubscribe(id2)

    def testUnsubscribeInvalid(self):
        with self.assertRaises(zserio.PubsubException):
            self.simplePubsub.unsubscribe(0)
