import unittest

from zserio import ServiceInterface

class FakeService(ServiceInterface):
    def callMethod(self, methodName, requestData, context=None):
        return bytes([0x00])

class ServiceTest(unittest.TestCase):
    def testCallMethod(self):
        fakeService = FakeService()
        self.assertEqual(bytes([0x00]), fakeService.callMethod("fakeMethod", bytes()))

    def testCallMethodWithContext(self):
        fakeService = FakeService()
        self.assertEqual(bytes([0x00]), fakeService.callMethod("fakeMethod", bytes(), None))

    def testServiceInterface(self):
        serviceInterface = ServiceInterface
        with self.assertRaises(NotImplementedError):
            serviceInterface.callMethod("method", bytes(), None)
