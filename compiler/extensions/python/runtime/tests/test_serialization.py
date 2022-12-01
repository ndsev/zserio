import unittest

from test_object.api import SerializeEnum, SerializeNested, SerializeObject

from zserio.serialization import (serialize, deserialize, serialize_to_bytes, deserialize_from_bytes,
                                  serialize_to_file, deserialize_from_file)
from zserio.bitbuffer import BitBuffer
from zserio.exception import PythonRuntimeException

class SerializationTest(unittest.TestCase):

    def test_serialize_enum(self):
        serialize_enum = SerializeEnum.VALUE3
        bitbuffer = serialize(serialize_enum)
        expected_bitsize = 8
        self.assertEqual(expected_bitsize, bitbuffer.bitsize)
        self.assertEqual(b'\x02', bitbuffer.buffer)

    def test_serialize_parameterized_object(self):
        param = 0x12
        offset = 0
        optional_value = 0xDEADCAFE
        serialize_nested = SerializeNested(param, offset, optional_value)
        bitbuffer = serialize(serialize_nested)
        expected_bitsize = 40
        self.assertEqual(expected_bitsize, bitbuffer.bitsize)
        self.assertEqual(b'\x01\xDE\xAD\xCA\xFE', bitbuffer.buffer)

    def test_serialize_object(self):
        param = 0x12
        offset = 0
        optional_value = 0xDEADCAFE
        serialize_nested = SerializeNested(param, offset, optional_value)
        serialize_object = SerializeObject(param, serialize_nested)
        bitbuffer = serialize(serialize_object)
        expected_bitsize = 48
        self.assertEqual(expected_bitsize, bitbuffer.bitsize)
        self.assertEqual(b'\x12\x02\xDE\xAD\xCA\xFE', bitbuffer.buffer)

    def test_deserialize_enum(self):
        bitbuffer = BitBuffer(b'\x02', 8)
        serialize_enum = deserialize(SerializeEnum, bitbuffer)
        self.assertEqual(SerializeEnum.VALUE3, serialize_enum)

    def test_deserialize_parameterized_object(self):
        bitbuffer = BitBuffer(b'\x01\xDE\xAD\xCA\xFE', 40)
        with self.assertRaises(TypeError):
            deserialize(SerializeNested, bitbuffer)
        serialize_nested = deserialize(SerializeNested, bitbuffer, 0x12)
        self.assertEqual(0x12, serialize_nested.param)
        self.assertEqual(0x01, serialize_nested.offset)
        self.assertEqual(0xDEADCAFE, serialize_nested.optional_value)

        wrong_bitbuffer = BitBuffer(b'\x02\xDE\xAD\xCA\xFE', 39)
        with self.assertRaises(PythonRuntimeException):
            deserialize(SerializeNested, wrong_bitbuffer, 0x12)

    def test_deserialize_object(self):
        bitbuffer = BitBuffer(b'\x12\x02\xDE\xAD\xCA\xFE', 48)
        serialize_object = deserialize(SerializeObject, bitbuffer)
        self.assertEqual(0x12, serialize_object.param)
        serialize_nested = serialize_object.nested
        self.assertEqual(0x12, serialize_nested.param)
        self.assertEqual(0x02, serialize_nested.offset)
        self.assertEqual(0xDEADCAFE, serialize_nested.optional_value)

    def test_serialize_enum_to_bytes(self):
        serialize_enum = SerializeEnum.VALUE3
        buffer = serialize_to_bytes(serialize_enum)
        self.assertEqual(1, len(buffer))
        self.assertEqual(b'\x02', buffer)

    def test_serialize_parameterized_object_to_bytes(self):
        param = 0x12
        offset = 0
        optional_value = 0xDEADCAFE
        serialize_nested = SerializeNested(param, offset, optional_value)
        buffer = serialize_to_bytes(serialize_nested)
        self.assertEqual(5, len(buffer))
        self.assertEqual(b'\x01\xDE\xAD\xCA\xFE', buffer)

    def test_serialize_object_to_bytes(self):
        param = 0x12
        offset = 0
        optional_value = 0xDEADCAFE
        serialize_nested = SerializeNested(param, offset, optional_value)
        serialize_object = SerializeObject(param, serialize_nested)
        buffer = serialize_to_bytes(serialize_object)
        self.assertEqual(6, len(buffer))
        self.assertEqual(b'\x12\x02\xDE\xAD\xCA\xFE', buffer)

    def test_deserialize_enum_from_bytes(self):
        buffer = b'\x02'
        serialize_enum = deserialize_from_bytes(SerializeEnum, buffer)
        self.assertEqual(SerializeEnum.VALUE3, serialize_enum)

    def test_deserialize_parameterized_object_from_bytes(self):
        buffer = b'\x01\xDE\xAD\xCA\xFE'
        with self.assertRaises(TypeError):
            deserialize_from_bytes(SerializeNested, buffer)
        serialize_nested = deserialize_from_bytes(SerializeNested, buffer, 0x12)
        self.assertEqual(0x12, serialize_nested.param)
        self.assertEqual(0x01, serialize_nested.offset)
        self.assertEqual(0xDEADCAFE, serialize_nested.optional_value)

        wrong_buffer = b'\x00\xDE\xAD\xCA\xFE'
        with self.assertRaises(PythonRuntimeException):
            deserialize_from_bytes(SerializeNested, wrong_buffer, 0x12)

    def test_deserialize_object_from_bytes(self):
        buffer = b'\x12\x02\xDE\xAD\xCA\xFE'
        serialize_object = deserialize_from_bytes(SerializeObject, buffer)
        self.assertEqual(0x12, serialize_object.param)
        serialize_nested = serialize_object.nested
        self.assertEqual(0x12, serialize_nested.param)
        self.assertEqual(0x02, serialize_nested.offset)
        self.assertEqual(0xDEADCAFE, serialize_nested.optional_value)

    def test_to_file_from_file(self):
        param = 0x12
        offset = 0
        optional_value = 0xDEADCAFE
        serialize_nested = SerializeNested(param, offset, optional_value)
        serialize_object = SerializeObject(param, serialize_nested)
        filename = "SerializationTest.bin"
        serialize_to_file(serialize_object, filename)
        read_serialize_object = deserialize_from_file(SerializeObject, filename)
        self.assertEqual(serialize_object, read_serialize_object)
