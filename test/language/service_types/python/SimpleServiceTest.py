import zserio

import ServiceTypes

class FakeContext:
    def __init__(self):
        self.seenByService = False

class LocalServiceClient(zserio.ServiceClientInterface):
    def __init__(self, service):
        self._service = service

    def call_method(self, method_name, request, context = None):
        response = self._service.call_method(method_name, request.byte_array, context)

        return response.byte_array

class SimpleServiceTest(ServiceTypes.TestCase):
    @classmethod
    def setUpClass(cls):
        super(SimpleServiceTest, cls).setUpClass()

        class Service(cls.api.SimpleService.Service):
            def __init__(self, api):
                super().__init__()
                self.api = api

            @staticmethod
            def _power_of_two_impl(request, context):
                if context:
                    context.seenByService = True

                value = request.value
                response = cls.api.Response(value**2)
                return response

            def _power_of_two_raw_impl(self, request_data, context):
                request = zserio.deserialize_from_bytes(self.api.Request, request_data)
                response = SimpleServiceTest.Service._power_of_two_impl(request, context)
                return zserio.serialize_to_bytes(response)

        cls.Service = Service

    def setUp(self):
        self.service = self.Service(self.api)
        self.client = self.api.SimpleService.Client(LocalServiceClient(self.service))

    def testServiceFullName(self):
        self.assertEqual("service_types.simple_service.SimpleService", self.service.service_full_name)

    def testMethodNames(self):
        self.assertEqual("powerOfTwo", self.service.method_names[0])

    def testPowerOfTwo(self):
        request = self.api.Request(13)
        self.assertEqual(169, self.client.power_of_two(request).value)
        request.value = -13
        self.assertEqual(169, self.client.power_of_two(request).value)
        request.value = 2
        self.assertEqual(4, self.client.power_of_two(request).value)
        request.value = -2
        self.assertEqual(4, self.client.power_of_two(request).value)

    def testPowerOfTwoRaw(self):
        request = self.api.Request(13)
        request_data = zserio.serialize_to_bytes(request)
        response_data = self.client.power_of_two_raw(request_data)
        response = zserio.deserialize_from_bytes(self.api.Response, response_data)
        self.assertEqual(169, response.value)

    def testInvalidServiceMethod(self):
        with self.assertRaises(zserio.ServiceException):
            self.service.call_method("nonexistentMethod", bytes())

    def testCallWithContext(self):
        fakeContext = FakeContext()
        self.assertFalse(fakeContext.seenByService)
        request = self.api.Request(10)
        response = self.client.power_of_two(request, fakeContext)
        self.assertEqual(100, response.value)
        self.assertTrue(fakeContext.seenByService)
