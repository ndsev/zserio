import unittest

from zserio import ServiceInterface

class ServiceTest(unittest.TestCase):
    def test_call_method(self):
        service_interface = ServiceInterface()
        with self.assertRaises(NotImplementedError):
            service_interface.call_method("method", bytes(), None)
