import unittest

from zserio import ServiceInterface

class ServiceTest(unittest.TestCase):
    def testCallMethod(self):
        serviceInterface = ServiceInterface()
        with self.assertRaises(NotImplementedError):
            serviceInterface.callMethod("method", bytes(), None)
