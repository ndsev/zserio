import unittest
import zserio

from testutils import getZserioApi

class FakeContext:
    def __init__(self):
        self.seenByService = False

class LocalServiceClient(zserio.ServiceClientInterface):
    def __init__(self, service):
        self._service = service

    def call_method(self, method_name, request, context = None):
        response = self._service.call_method(method_name, request.byte_array, context)

        return response.byte_array

class SimpleServiceTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "service_types.zs").simple_service

        class Service(cls.api.SimpleService.Service):
            @staticmethod
            def _power_of_two_impl(request, context):
                if context:
                    context.seenByService = True

                value = request.value
                response = cls.api.Response(value**2)
                return response

        cls.Service = Service

    def setUp(self):
        self.service = self.Service()
        self.client = self.api.SimpleService.Client(LocalServiceClient(self.service))

    def testServiceFullName(self):
        self.assertEqual("service_types.simple_service.SimpleService",
                         self.api.SimpleService.Service.SERVICE_FULL_NAME)

    def testMethodNames(self):
        self.assertEqual("powerOfTwo", self.api.SimpleService.Service.METHOD_NAMES[0])

    def testPowerOfTwo(self):
        request = self.api.Request(13)
        self.assertEqual(169, self.client.power_of_two(request).value)
        request.value = -13
        self.assertEqual(169, self.client.power_of_two(request).value)
        request.value = 2
        self.assertEqual(4, self.client.power_of_two(request).value)
        request.value = -2
        self.assertEqual(4, self.client.power_of_two(request).value)

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
