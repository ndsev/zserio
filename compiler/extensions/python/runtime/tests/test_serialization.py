import unittest

from zserio.serialization import serialize, deserialize, serializeToBytes, deserializeBytes
from zserio.bitbuffer import BitBuffer
from zserio.exception import PythonRuntimeException

class DummyObject:
    def __init__(self, parameter, value = 0):
        self._parameter = parameter
        self._value = value

    @classmethod
    def fromReader(cls, reader, parameter):
        instance = cls(parameter)
        instance.read(reader)

        return instance

    def getValue(self):
        return self._value

    @staticmethod
    def bitSizeOf(_bitPosition = 0):
        return 31 # to make an unaligned type

    def read(self, reader):
        self._value = reader.readBits(self.bitSizeOf(0))

    def write(self, writer):
        writer.writeBits(self._value, self.bitSizeOf(0))

class SerializationTest(unittest.TestCase):

    def testSerialize(self):
        dummyObject = DummyObject(0xAB, 0xDEAD)
        bitBuffer = serialize(dummyObject)
        expectedBitSize = 31
        self.assertEqual(expectedBitSize, bitBuffer.getBitSize())
        self.assertEqual((expectedBitSize + 7) // 8, bitBuffer.getByteSize())
        self.assertEqual(b'\x00\x01\xBD\x5A', bitBuffer.getBuffer())

    def testDeserialize(self):
        bitBuffer = BitBuffer(b'\x00\x01\xBD\x5A', 31)
        dummyObject = deserialize(DummyObject, bitBuffer, 0xAB)
        self.assertEqual(0xDEAD, dummyObject.getValue())

        wrongBitBuffer = BitBuffer(b'\x00\x01\xBD\x5A', 30)
        with self.assertRaises(PythonRuntimeException):
            deserialize(DummyObject, wrongBitBuffer, 0xAB) # reading behind the stream!

    def testSerializeToBytes(self):
        dummyObject = DummyObject(0xAB, 0xDEAD)
        buffer = serializeToBytes(dummyObject)
        expectedBitSize = 31
        self.assertEqual((expectedBitSize + 7) // 8, len(buffer))
        self.assertEqual(b'\x00\x01\xBD\x5A', buffer)

    def testDeserializeBytes(self):
        buffer = b'\x00\x01\xBD\x5A'
        dummyObject = deserializeBytes(DummyObject, buffer, 0xAB)
        self.assertEqual(0xDEAD, dummyObject.getValue())
