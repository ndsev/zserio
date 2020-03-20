import unittest

from zserio.array import (Array, BitFieldArrayTraits, SignedBitFieldArrayTraits,
                          VarUInt16ArrayTraits, VarUInt32ArrayTraits, VarUInt64ArrayTraits, VarUIntArrayTraits,
                          VarInt16ArrayTraits, VarInt32ArrayTraits, VarInt64ArrayTraits, VarIntArrayTraits,
                          Float16ArrayTraits, Float32ArrayTraits, Float64ArrayTraits,
                          StringArrayTraits, BoolArrayTraits, BitBufferArrayTraits, ObjectArrayTraits)
from zserio.bitbuffer import BitBuffer
from zserio.bitreader import BitStreamReader
from zserio.bitsizeof import getBitSizeOfVarUInt64
from zserio.bitwriter import BitStreamWriter
from zserio import PythonRuntimeException

class ArrayTest(unittest.TestCase):

    def testBitFieldArray(self):
        arrayTraits = BitFieldArrayTraits(5)
        array1Values = [1, 2]
        array1BitSizeOf = 2 * 5
        array1AlignedBitSizeOf = 5 + 3 + 5
        array2Values = [3, 4]
        self._testArray(arrayTraits, array1Values, array1BitSizeOf, array1AlignedBitSizeOf, array2Values)

    def testSignedBitFieldArray(self):
        arrayTraits = SignedBitFieldArrayTraits(5)
        array1Values = [-1, 1]
        array1BitSizeOf = 2 * 5
        array1AlignedBitSizeOf = 5 + 3 + 5
        array2Values = [-2, 2]
        self._testArray(arrayTraits, array1Values, array1BitSizeOf, array1AlignedBitSizeOf, array2Values)

    def testVarUInt16Array(self):
        arrayTraits = VarUInt16ArrayTraits()
        array1Values = [1, 1024]
        array1BitSizeOf = 8 + 16
        array1AlignedBitSizeOf = array1BitSizeOf
        array2Values = [1, 8192]
        self._testArray(arrayTraits, array1Values, array1BitSizeOf, array1AlignedBitSizeOf, array2Values)

    def testVarUInt32Array(self):
        arrayTraits = VarUInt32ArrayTraits()
        array1Values = [1, 16384]
        array1BitSizeOf = 8 + 24
        array1AlignedBitSizeOf = array1BitSizeOf
        array2Values = [1, 32768]
        self._testArray(arrayTraits, array1Values, array1BitSizeOf, array1AlignedBitSizeOf, array2Values)

    def testVarUInt64Array(self):
        arrayTraits = VarUInt64ArrayTraits()
        array1Values = [1, 16384]
        array1BitSizeOf = 8 + 24
        array1AlignedBitSizeOf = array1BitSizeOf
        array2Values = [1, 65536]
        self._testArray(arrayTraits, array1Values, array1BitSizeOf, array1AlignedBitSizeOf, array2Values)

    def testVarUIntArray(self):
        arrayTraits = VarUIntArrayTraits()
        array1Values = [1, 1024]
        array1BitSizeOf = 8 + 16
        array1AlignedBitSizeOf = array1BitSizeOf
        array2Values = [1, 8192]
        self._testArray(arrayTraits, array1Values, array1BitSizeOf, array1AlignedBitSizeOf, array2Values)

    def testVarInt16Array(self):
        arrayTraits = VarInt16ArrayTraits()
        array1Values = [-1, 1024]
        array1BitSizeOf = 8 + 16
        array1AlignedBitSizeOf = array1BitSizeOf
        array2Values = [-1, 8192]
        self._testArray(arrayTraits, array1Values, array1BitSizeOf, array1AlignedBitSizeOf, array2Values)

    def testVarInt32Array(self):
        arrayTraits = VarInt32ArrayTraits()
        array1Values = [-1, 16384]
        array1BitSizeOf = 8 + 24
        array1AlignedBitSizeOf = array1BitSizeOf
        array2Values = [-1, 32768]
        self._testArray(arrayTraits, array1Values, array1BitSizeOf, array1AlignedBitSizeOf, array2Values)

    def testVarInt64Array(self):
        arrayTraits = VarInt64ArrayTraits()
        array1Values = [-1, 16384]
        array1BitSizeOf = 8 + 24
        array1AlignedBitSizeOf = array1BitSizeOf
        array2Values = [-1, 65536]
        self._testArray(arrayTraits, array1Values, array1BitSizeOf, array1AlignedBitSizeOf, array2Values)

    def testVarIntArray(self):
        arrayTraits = VarIntArrayTraits()
        array1Values = [-1, 1024]
        array1BitSizeOf = 8 + 16
        array1AlignedBitSizeOf = array1BitSizeOf
        array2Values = [-1, 8192]
        self._testArray(arrayTraits, array1Values, array1BitSizeOf, array1AlignedBitSizeOf, array2Values)

    def testFloat16Array(self):
        arrayTraits = Float16ArrayTraits()
        array1Values = [-1.0, 1.0]
        array1BitSizeOf = 2 * 16
        array1AlignedBitSizeOf = array1BitSizeOf
        array2Values = [-3.5, 3.5]
        self._testArray(arrayTraits, array1Values, array1BitSizeOf, array1AlignedBitSizeOf, array2Values)

    def testFloat32Array(self):
        arrayTraits = Float32ArrayTraits()
        array1Values = [-1.0, 1.0]
        array1BitSizeOf = 2 * 32
        array1AlignedBitSizeOf = array1BitSizeOf
        array2Values = [-3.5, 3.5]
        self._testArray(arrayTraits, array1Values, array1BitSizeOf, array1AlignedBitSizeOf, array2Values)

    def testFloat64Array(self):
        arrayTraits = Float64ArrayTraits()
        array1Values = [-1.0, 1.0]
        array1BitSizeOf = 2 * 64
        array1AlignedBitSizeOf = array1BitSizeOf
        array2Values = [-3.5, 3.5]
        self._testArray(arrayTraits, array1Values, array1BitSizeOf, array1AlignedBitSizeOf, array2Values)

    def testStringArray(self):
        arrayTraits = StringArrayTraits()
        array1Values = ["Text1", "Text2"]
        array1BitSizeOf = 2 * (1 + len("TextN")) * 8
        array1AlignedBitSizeOf = array1BitSizeOf
        array2Values = ["Text3", "Text4"]
        self._testArray(arrayTraits, array1Values, array1BitSizeOf, array1AlignedBitSizeOf, array2Values)

    def testBoolArray(self):
        arrayTraits = BoolArrayTraits()
        array1Values = [True, False]
        array1BitSizeOf = 2 * 1
        array1AlignedBitSizeOf = 1 + 7 + 1
        array2Values = [True, True]
        self._testArray(arrayTraits, array1Values, array1BitSizeOf, array1AlignedBitSizeOf, array2Values)

    def testBitBufferArray(self):
        arrayTraits = BitBufferArrayTraits()
        array1Values = [BitBuffer(bytes([0xAB, 0x07]), 11), BitBuffer(bytes([0xAB, 0xCD, 0x7F]), 23)]
        array1BitSizeOf = 8 + 11 + 8 + 23
        array1AlignedBitSizeOf = 8 + 11 + 5 + 8 + 23
        array2Values = [BitBuffer(bytes([0xBA, 0x07]), 11), BitBuffer(bytes([0xBA, 0xDC, 0x7F]), 23)]
        self._testArray(arrayTraits, array1Values, array1BitSizeOf, array1AlignedBitSizeOf, array2Values)

    def testObjectArray(self):
        class DummyObject():
            def __init__(self, value):
                self._value = value

            @classmethod
            def create(cls, reader, index):
                instance = cls(index)
                instance.read(reader)

                return instance

            def __eq__(self, other):
                return self._value == other._value

            def __hash__(self):
                return hash(self._value)

            @staticmethod
            def bitSizeOf(_bitPosition):
                return 31 # to make an unaligned type

            def initializeOffsets(self, bitPosition):
                return bitPosition + self.bitSizeOf(bitPosition)

            def read(self, reader):
                self._value = reader.readBits(self.bitSizeOf(0))

            def write(self, writer):
                writer.writeBits(self._value, self.bitSizeOf(0))

        arrayTraits = ObjectArrayTraits(DummyObject.create)
        array1Values = [DummyObject(1), DummyObject(2)]
        array1BitSizeOf = 2 * 31
        array1AlignedBitSizeOf = 31 + 1 + 31
        array2Values = [DummyObject(3), DummyObject(4)]
        self._testArray(arrayTraits, array1Values, array1BitSizeOf, array1AlignedBitSizeOf, array2Values)

    def _testArray(self, arrayTraits, array1Values, array1BitSizeOf, array1AlignedBitSizeOf, array2Values):
        self._testFromReader(arrayTraits, array1Values)
        self._testEq(arrayTraits, array1Values, array2Values)
        self._testHashCode(arrayTraits, array1Values, array2Values)
        self._testLen(arrayTraits, array1Values)
        self._testGetItem(arrayTraits, array1Values)
        self._testSetItem(arrayTraits, array1Values)
        self._testGetRawArray(arrayTraits, array1Values, array2Values)
        self._testBitSizeOf(arrayTraits, array1Values, array1BitSizeOf, array1AlignedBitSizeOf)
        self._testInitializeOffsets(arrayTraits, array1Values, array1BitSizeOf, array1AlignedBitSizeOf)
        self._testRead(arrayTraits, array1Values)
        self._testWrite(arrayTraits, array1Values, array1BitSizeOf, array1AlignedBitSizeOf)

    def _testFromReader(self, arrayTraits, arrayValues):
        array = Array(arrayTraits, arrayValues)
        writer = BitStreamWriter()
        array.write(writer)
        reader = BitStreamReader(writer.getByteArray())
        readArray = Array.fromReader(arrayTraits, reader, len(arrayValues))
        self.assertEqual(array, readArray)

    def _testEq(self, arrayTraits, array1Values, array2Values):
        array1 = Array(arrayTraits, array1Values)
        array2 = Array(arrayTraits, array2Values)
        array3 = Array(arrayTraits, array1Values)
        self.assertNotEqual(array1, None)
        self.assertNotEqual(array1, array2)
        self.assertEqual(array1, array3)

    def _testHashCode(self, arrayTraits, array1Values, array2Values):
        array1 = Array(arrayTraits, array1Values)
        array2 = Array(arrayTraits, array2Values)
        array3 = Array(arrayTraits, array1Values)
        self.assertNotEqual(hash(array1), hash(array2))
        self.assertEqual(hash(array1), hash(array3))

    def _testLen(self, arrayTraits, arrayValues):
        array = Array(arrayTraits, arrayValues)
        rawArray = array.getRawArray()
        self.assertEqual(len(rawArray), len(array))

    def _testGetItem(self, arrayTraits, arrayValues):
        array = Array(arrayTraits, arrayValues)
        rawArray = array.getRawArray()
        for value, rawValue in zip(array, rawArray):
            self.assertEqual(value, rawValue)

    def _testSetItem(self, arrayTraits, arrayValues):
        array = Array(arrayTraits, arrayValues)
        rawArray = array.getRawArray()
        self.assertTrue(len(array) > 1)
        firstValue = array[0]
        secondValue = array[1]
        array[0] = secondValue
        self.assertEqual(array[0], rawArray[0])
        rawArray[0] = firstValue # return the original value for other tests
        self.assertEqual(array[0], rawArray[0])

    def _testGetRawArray(self, arrayTraits, array1Values, array2Values):
        array1 = Array(arrayTraits, array1Values)
        array2 = Array(arrayTraits, array2Values)
        array3 = Array(arrayTraits, array1Values)
        self.assertNotEqual(array1.getRawArray(), array2.getRawArray())
        self.assertEqual(array1.getRawArray(), array3.getRawArray())

    def _testBitSizeOf(self, arrayTraits, arrayValues, expectedBitSize, expectedAlignedBitSize):
        array = Array(arrayTraits, arrayValues)
        self.assertEqual(expectedBitSize, array.bitSizeOf(0))
        self.assertEqual(expectedBitSize, array.bitSizeOf(7))

        autoArray = Array(arrayTraits, arrayValues, isAuto=True)
        self.assertEqual(getBitSizeOfVarUInt64(len(arrayValues)) + expectedBitSize, autoArray.bitSizeOf(0))
        self.assertEqual(getBitSizeOfVarUInt64(len(arrayValues)) + expectedBitSize, autoArray.bitSizeOf(7))

        alignedArray = Array(arrayTraits, arrayValues, setOffsetMethod=not None)
        self.assertEqual(expectedAlignedBitSize, alignedArray.bitSizeOf(0))

    def _testInitializeOffsets(self, arrayTraits, arrayValues, expectedBitSize, expectedAlignedBitSize):
        def _setOffsetMethod(_index, _bitOffset):
            pass

        array = Array(arrayTraits, arrayValues)
        self.assertEqual(0 + expectedBitSize, array.initializeOffsets(0))
        self.assertEqual(7 + expectedBitSize, array.initializeOffsets(7))

        autoArray = Array(arrayTraits, arrayValues, isAuto=True)
        self.assertEqual(0 + getBitSizeOfVarUInt64(len(arrayValues)) + expectedBitSize,
                         autoArray.initializeOffsets(0))
        self.assertEqual(7 + getBitSizeOfVarUInt64(len(arrayValues)) + expectedBitSize,
                         autoArray.initializeOffsets(7))

        alignedArray = Array(arrayTraits, arrayValues, setOffsetMethod=_setOffsetMethod)
        self.assertEqual(0 + expectedAlignedBitSize, alignedArray.initializeOffsets(0))

    def _testRead(self, arrayTraits, arrayValues):
        def _checkOffsetMethod(_index, _bitOffset):
            pass

        array = Array(arrayTraits, arrayValues)
        writer = BitStreamWriter()
        array.write(writer)
        reader = BitStreamReader(writer.getByteArray())
        readArray = Array(arrayTraits)
        readArray.read(reader, len(array.getRawArray()))
        self.assertEqual(array, readArray)

        autoArray = Array(arrayTraits, arrayValues, isAuto=True)
        writer = BitStreamWriter()
        autoArray.write(writer)
        reader = BitStreamReader(writer.getByteArray())
        readAutoArray = Array(arrayTraits, isAuto=True)
        readAutoArray.read(reader, len(autoArray.getRawArray()))
        self.assertEqual(autoArray, readAutoArray)

        alignedArray = Array(arrayTraits, arrayValues, checkOffsetMethod=_checkOffsetMethod)
        writer = BitStreamWriter()
        alignedArray.write(writer)
        reader = BitStreamReader(writer.getByteArray())
        readAlignedArray = Array(arrayTraits, checkOffsetMethod=_checkOffsetMethod)
        readAlignedArray.read(reader, len(alignedArray.getRawArray()))
        self.assertEqual(alignedArray, readAlignedArray)

        if arrayTraits.HAS_BITSIZEOF_CONSTANT and arrayTraits.bitSizeOf() % 8 == 0:
            implicitArray = Array(arrayTraits, arrayValues, isImplicit=True)
            writer = BitStreamWriter()
            implicitArray.write(writer)
            reader = BitStreamReader(writer.getByteArray())
            readImplicitArray = Array(arrayTraits, isImplicit=True)
            readImplicitArray.read(reader)
            self.assertEqual(implicitArray, readImplicitArray)
        elif not arrayTraits.HAS_BITSIZEOF_CONSTANT:
            with self.assertRaises(PythonRuntimeException):
                Array(arrayTraits, isImplicit=True).read(reader)

    def _testWrite(self, arrayTraits, arrayValues, expectedBitSize, expectedAlignedBitSize):
        def _checkOffsetMethod(_index, _bitOffset):
            pass

        array = Array(arrayTraits, arrayValues)
        writer = BitStreamWriter()
        array.write(writer)
        self.assertEqual(expectedBitSize, writer.getBitPosition())

        autoArray = Array(arrayTraits, arrayValues, isAuto=True)
        writer = BitStreamWriter()
        autoArray.write(writer)
        self.assertEqual(getBitSizeOfVarUInt64(len(arrayValues)) + expectedBitSize, writer.getBitPosition())

        alignedArray = Array(arrayTraits, arrayValues, checkOffsetMethod=_checkOffsetMethod)
        writer = BitStreamWriter()
        alignedArray.write(writer)
        self.assertEqual(expectedAlignedBitSize, writer.getBitPosition())
