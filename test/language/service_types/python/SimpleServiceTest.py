import unittest
import zserio

from testutils import getZserioApi

class FakeContext:
    def __init__(self):
        self.seenByService = False

class SimpleServiceTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "service_types.zs").simple_service

        class Service(cls.api.SimpleService.Service):
            @staticmethod
            def _powerOfTwoImpl(request, context):
                if context:
                    context.seenByService = True

                value = request.getValue()
                response = cls.api.Response.fromFields(value**2)
                return response

        cls.Service = Service

    def setUp(self):
        self.service = self.Service()
        self.client = self.api.SimpleService.Client(self.service)

    def testServiceFullName(self):
        self.assertEqual("service_types.simple_service.SimpleService",
                         self.api.SimpleService.Service.SERVICE_FULL_NAME)

    def testMethodNames(self):
        self.assertEqual("powerOfTwo", self.api.SimpleService.Service.METHOD_NAMES[0])

    def testPowerOfTwo(self):
        request = self.api.Request.fromFields(13)
        self.assertEqual(169, self.client.powerOfTwoMethod(request).getValue())
        request.setValue(-13)
        self.assertEqual(169, self.client.powerOfTwoMethod(request).getValue())
        request.setValue(2)
        self.assertEqual(4, self.client.powerOfTwoMethod(request).getValue())
        request.setValue(-2)
        self.assertEqual(4, self.client.powerOfTwoMethod(request).getValue())

    def testInvalidServiceMethod(self):
        with self.assertRaises(zserio.ServiceException):
            self.service.callMethod("nonexistentMethod", bytes())

    def testCallWithContext(self):
        fakeContext = FakeContext()
        self.assertFalse(fakeContext.seenByService)
        request = self.api.Request.fromFields(10)
        response = self.client.powerOfTwoMethod(request, fakeContext)
        self.assertEqual(100, response.getValue())
        self.assertTrue(fakeContext.seenByService)
