import unittest

from zserio import ServiceData, ServiceInterface, ServiceClientInterface
from zserio.service import ObjectServiceData, RawServiceData


class ServiceTest(unittest.TestCase):
    def test_object_service_data(self):
        class ZserioObject:
            def __init__(self, value):
                self._value = value

            def __eq__(self, other):
                return self._value == other._value

            @staticmethod
            def bitsizeof(_bitposition):
                return 31  # to make an unaligned type

            def write(self, writer):
                writer.write_bits(self._value, self.bitsizeof(0))

        zserio_object = ZserioObject(0xABCD)
        service_data = ObjectServiceData(zserio_object)
        self.assertEqual(zserio_object, service_data.zserio_object)
        self.assertEqual(bytes([0x00, 0x01, 0x57, 0x9A]), service_data.byte_array)

    def test_raw_service_data(self):
        data = [0x00, 0x01, 0x57, 0x9A]
        service_data = RawServiceData(bytes(data))
        self.assertIsNone(service_data.zserio_object)
        self.assertEqual(bytes(data), service_data.byte_array)

    def test_service_data(self):
        service_data = ServiceData()
        with self.assertRaises(NotImplementedError):
            service_data.zserio_object()
        with self.assertRaises(NotImplementedError):
            service_data.byte_array()

    def test_service_interface(self):
        service_interface = ServiceInterface()
        with self.assertRaises(NotImplementedError):
            service_interface.call_method("method", bytes(), None)
        with self.assertRaises(NotImplementedError):
            service_interface.service_full_name()
        with self.assertRaises(NotImplementedError):
            service_interface.method_names()

    def test_service_client_interface(self):
        service_client_interface = ServiceClientInterface()
        with self.assertRaises(NotImplementedError):
            service_client_interface.call_method("method", ObjectServiceData(None), None)
