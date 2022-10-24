import unittest

from zserio.serialization import (serialize, deserialize, serialize_to_bytes, deserialize_bytes,
                                  serialize_to_file, deserialize_from_file)
from zserio.bitbuffer import BitBuffer
from zserio.exception import PythonRuntimeException

class DummyObject:
    def __init__(self, parameter, value = 0):
        self._parameter = parameter
        self._value = value
        self._offsets_initialized = False

    @classmethod
    def from_reader(cls, reader, parameter):
        instance = cls(parameter)
        instance.read(reader)

        return instance

    def get_value(self):
        return self._value

    @staticmethod
    def bitsizeof(_bitposition = 0):
        return 31 # to make an unaligned type

    def initialize_offsets(self, bitposition: int) -> int:
        self._offsets_initialized = True
        return bitposition + DummyObject.bitsizeof(bitposition)

    def read(self, reader):
        self._value = reader.read_bits(self.bitsizeof(0))

    def write(self, writer):
        # don't write anything if offsets are not initialized to force test failure
        if self._offsets_initialized:
            writer.write_bits(self._value, self.bitsizeof(0))

class SerializationTest(unittest.TestCase):

    def test_serialize(self):
        dummy_object = DummyObject(0xAB, 0xDEAD)
        bitbuffer = serialize(dummy_object)
        expected_bitsize = 31
        self.assertEqual(expected_bitsize, bitbuffer.bitsize)
        self.assertEqual(b'\x00\x01\xBD\x5A', bitbuffer.buffer)

    def test_deserialize(self):
        bitbuffer = BitBuffer(b'\x00\x01\xBD\x5A', 31)
        dummy_object = deserialize(DummyObject, bitbuffer, 0xAB)
        self.assertEqual(0xDEAD, dummy_object.get_value())

        wrong_bitbuffer = BitBuffer(b'\x00\x01\xBD\x5A', 30)
        with self.assertRaises(PythonRuntimeException):
            deserialize(DummyObject, wrong_bitbuffer, 0xAB) # reading behind the stream!

    def test_serialize_to_bytes(self):
        dummy_object = DummyObject(0xAB, 0xDEAD)
        buffer = serialize_to_bytes(dummy_object)
        expected_bitsize = 31
        self.assertEqual((expected_bitsize + 7) // 8, len(buffer))
        self.assertEqual(b'\x00\x01\xBD\x5A', buffer)

    def test_deserialize_bytes(self):
        buffer = b'\x00\x01\xBD\x5A'
        dummy_object = deserialize_bytes(DummyObject, buffer, 0xAB)
        self.assertEqual(0xDEAD, dummy_object.get_value())

    def test_to_file_from_file(self):
        dummy_object = DummyObject(0xAB, 0xDEAD)
        filename = "SerializationTest.bin"
        serialize_to_file(dummy_object, filename)
        read_dummy_object = deserialize_from_file(DummyObject, filename, 0xAB)
        self.assertEqual(dummy_object.get_value(), read_dummy_object.get_value())
