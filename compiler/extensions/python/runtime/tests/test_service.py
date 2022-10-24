import unittest

from zserio import ServiceData, ServiceInterface, ServiceClientInterface

class ServiceTest(unittest.TestCase):
    def test_service_data(self):
        class ZserioObject:
            def __init__(self, value):
                self._value = value
                self._offsets_initialized = False

            def __eq__(self, other):
                return self._value == other._value

            @staticmethod
            def bitsizeof(_bitposition):
                return 31 # to make an unaligned type

            def initialize_offsets(self, bitposition: int) -> int:
                self._offsets_initialized = True
                return bitposition + ZserioObject.bitsizeof(bitposition)

            def write(self, writer):
                # don't write anything if offsets are not initialized to force test failure
                if self._offsets_initialized:
                    writer.write_bits(self._value, self.bitsizeof(0))

        zserio_object = ZserioObject(0xABCD)
        service_data = ServiceData(zserio_object)
        self.assertEqual(bytes([0x00, 0x01, 0x57, 0x9A]), service_data.byte_array)
        self.assertEqual(zserio_object, service_data.zserio_object)

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
            service_client_interface.call_method("method", ServiceData(None), None)
