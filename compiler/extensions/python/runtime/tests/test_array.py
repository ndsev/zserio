import unittest

from zserio.array import (Array, BitFieldArrayTraits, SignedBitFieldArrayTraits,
                          VarUInt16ArrayTraits, VarUInt32ArrayTraits, VarUInt64ArrayTraits, VarUIntArrayTraits,
                          VarSizeArrayTraits, VarInt16ArrayTraits, VarInt32ArrayTraits, VarInt64ArrayTraits,
                          VarIntArrayTraits, Float16ArrayTraits, Float32ArrayTraits, Float64ArrayTraits,
                          StringArrayTraits, BoolArrayTraits, BitBufferArrayTraits, ObjectArrayTraits)
from zserio.bitbuffer import BitBuffer
from zserio.bitreader import BitStreamReader
from zserio.bitsizeof import bitsizeof_varuint64
from zserio.bitwriter import BitStreamWriter
from zserio import PythonRuntimeException

class ArrayTest(unittest.TestCase):

    def test_bitfield_array(self):
        array_traits = BitFieldArrayTraits(5)
        array1_values = [1, 2]
        array1_bitsizeof = 2 * 5
        array1_aligned_bitsizeof = 5 + 3 + 5
        array2_values = [3, 4]
        self._test_array(array_traits, array1_values, array1_bitsizeof, array1_aligned_bitsizeof, array2_values)

    def test_signed_bitfield_array(self):
        array_traits = SignedBitFieldArrayTraits(5)
        array1_values = [-1, 1]
        array1_bitsizeof = 2 * 5
        array1_aligned_bitsizeof = 5 + 3 + 5
        array2_values = [-2, 2]
        self._test_array(array_traits, array1_values, array1_bitsizeof, array1_aligned_bitsizeof, array2_values)

    def test_varuint16_array(self):
        array_traits = VarUInt16ArrayTraits()
        array1_values = [1, 1024]
        array1_bitsizeof = 8 + 16
        array1_aligned_bitsizeof = array1_bitsizeof
        array2_values = [1, 8192]
        self._test_array(array_traits, array1_values, array1_bitsizeof, array1_aligned_bitsizeof, array2_values)

    def test_varuint32_array(self):
        array_traits = VarUInt32ArrayTraits()
        array1_values = [1, 16384]
        array1_bitsizeof = 8 + 24
        array1_aligned_bitsizeof = array1_bitsizeof
        array2_values = [1, 32768]
        self._test_array(array_traits, array1_values, array1_bitsizeof, array1_aligned_bitsizeof, array2_values)

    def test_varuint64_array(self):
        array_traits = VarUInt64ArrayTraits()
        array1_values = [1, 16384]
        array1_bitsizeof = 8 + 24
        array1_aligned_bitsizeof = array1_bitsizeof
        array2_values = [1, 65536]
        self._test_array(array_traits, array1_values, array1_bitsizeof, array1_aligned_bitsizeof, array2_values)

    def test_varuint_array(self):
        array_traits = VarUIntArrayTraits()
        array1_values = [1, 1024]
        array1_bitsizeof = 8 + 16
        array1_aligned_bitsizeof = array1_bitsizeof
        array2_values = [1, 8192]
        self._test_array(array_traits, array1_values, array1_bitsizeof, array1_aligned_bitsizeof, array2_values)

    def test_varsize_array(self):
        array_traits = VarSizeArrayTraits()
        array1_values = [1, 16384]
        array1_bitsizeof = 8 + 24
        array1_aligned_bitsizeof = array1_bitsizeof
        array2_values = [1, 32768]
        self._test_array(array_traits, array1_values, array1_bitsizeof, array1_aligned_bitsizeof, array2_values)

    def test_varint16_array(self):
        array_traits = VarInt16ArrayTraits()
        array1_values = [-1, 1024]
        array1_bitsizeof = 8 + 16
        array1_aligned_bitsizeof = array1_bitsizeof
        array2_values = [-1, 8192]
        self._test_array(array_traits, array1_values, array1_bitsizeof, array1_aligned_bitsizeof, array2_values)

    def test_varint32_array(self):
        array_traits = VarInt32ArrayTraits()
        array1_values = [-1, 16384]
        array1_bitsizeof = 8 + 24
        array1_aligned_bitsizeof = array1_bitsizeof
        array2_values = [-1, 32768]
        self._test_array(array_traits, array1_values, array1_bitsizeof, array1_aligned_bitsizeof, array2_values)

    def test_varint64_array(self):
        array_traits = VarInt64ArrayTraits()
        array1_values = [-1, 16384]
        array1_bitsizeof = 8 + 24
        array1_aligned_bitsizeof = array1_bitsizeof
        array2_values = [-1, 65536]
        self._test_array(array_traits, array1_values, array1_bitsizeof, array1_aligned_bitsizeof, array2_values)

    def test_varint_array(self):
        array_traits = VarIntArrayTraits()
        array1_values = [-1, 1024]
        array1_bitsizeof = 8 + 16
        array1_aligned_bitsizeof = array1_bitsizeof
        array2_values = [-1, 8192]
        self._test_array(array_traits, array1_values, array1_bitsizeof, array1_aligned_bitsizeof, array2_values)

    def test_float16_array(self):
        array_traits = Float16ArrayTraits()
        array1_values = [-1.0, 1.0]
        array1_bitsizeof = 2 * 16
        array1_aligned_bitsizeof = array1_bitsizeof
        array2_values = [-3.5, 3.5]
        self._test_array(array_traits, array1_values, array1_bitsizeof, array1_aligned_bitsizeof, array2_values)

    def test_float32_array(self):
        array_traits = Float32ArrayTraits()
        array1_values = [-1.0, 1.0]
        array1_bitsizeof = 2 * 32
        array1_aligned_bitsizeof = array1_bitsizeof
        array2_values = [-3.5, 3.5]
        self._test_array(array_traits, array1_values, array1_bitsizeof, array1_aligned_bitsizeof, array2_values)

    def test_float64_array(self):
        array_traits = Float64ArrayTraits()
        array1_values = [-1.0, 1.0]
        array1_bitsizeof = 2 * 64
        array1_aligned_bitsizeof = array1_bitsizeof
        array2_values = [-3.5, 3.5]
        self._test_array(array_traits, array1_values, array1_bitsizeof, array1_aligned_bitsizeof, array2_values)

    def test_string_array(self):
        array_traits = StringArrayTraits()
        array1_values = ["Text1", "Text2"]
        array1_bitsizeof = 2 * (1 + len("TextN")) * 8
        array1_aligned_bitsizeof = array1_bitsizeof
        array2_values = ["Text3", "Text4"]
        self._test_array(array_traits, array1_values, array1_bitsizeof, array1_aligned_bitsizeof, array2_values)

    def test_bool_array(self):
        array_traits = BoolArrayTraits()
        array1_values = [True, False]
        array1_bitsizeof = 2 * 1
        array1_aligned_bitsizeof = 1 + 7 + 1
        array2_values = [True, True]
        self._test_array(array_traits, array1_values, array1_bitsizeof, array1_aligned_bitsizeof, array2_values)

    def test_bitbuffer_array(self):
        array_traits = BitBufferArrayTraits()
        array1_values = [BitBuffer(bytes([0xAB, 0x07]), 11), BitBuffer(bytes([0xAB, 0xCD, 0x7F]), 23)]
        array1_bitsizeof = 8 + 11 + 8 + 23
        array1_aligned_bitsizeof = 8 + 11 + 5 + 8 + 23
        array2_values = [BitBuffer(bytes([0xBA, 0x07]), 11), BitBuffer(bytes([0xBA, 0xDC, 0x7F]), 23)]
        self._test_array(array_traits, array1_values, array1_bitsizeof, array1_aligned_bitsizeof, array2_values)

    def test_object_array(self):
        class DummyObject:
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
            def bitSizeOf(_bitposition):
                return 31 # to make an unaligned type

            def initializeOffsets(self, bitposition):
                return bitposition + self.bitSizeOf(bitposition)

            def read(self, reader):
                self._value = reader.read_bits(self.bitSizeOf(0))

            def write(self, writer):
                writer.write_bits(self._value, self.bitSizeOf(0))

        array_traits = ObjectArrayTraits(DummyObject.create)
        array1_values = [DummyObject(1), DummyObject(2)]
        array1_bitsizeof = 2 * 31
        array1_aligned_bitsizeof = 31 + 1 + 31
        array2_values = [DummyObject(3), DummyObject(4)]
        self._test_array(array_traits, array1_values, array1_bitsizeof, array1_aligned_bitsizeof, array2_values)

    def _test_array(self, array_traits, array1_values, array1_bitsizeof, array1_aligned_bitsizeof,
                    array2_values):
        self._test_from_reader(array_traits, array1_values)
        self._test_eq(array_traits, array1_values, array2_values)
        self._test_hashcode(array_traits, array1_values, array2_values)
        self._test_len(array_traits, array1_values)
        self._test_get_item(array_traits, array1_values)
        self._test_set_item(array_traits, array1_values)
        self._test_raw_array(array_traits, array1_values, array2_values)
        self._test_bitsizeof(array_traits, array1_values, array1_bitsizeof, array1_aligned_bitsizeof)
        self._test_initialize_offsets(array_traits, array1_values, array1_bitsizeof, array1_aligned_bitsizeof)
        self._test_read(array_traits, array1_values)
        self._test_write(array_traits, array1_values, array1_bitsizeof, array1_aligned_bitsizeof)

    def _test_from_reader(self, array_traits, array_values):
        array = Array(array_traits, array_values)
        writer = BitStreamWriter()
        array.write(writer)
        reader = BitStreamReader(writer.byte_array)
        read_array = Array.from_reader(array_traits, reader, len(array_values))
        self.assertEqual(array, read_array)

    def _test_eq(self, array_traits, array1_values, array2_values):
        array1 = Array(array_traits, array1_values)
        array2 = Array(array_traits, array2_values)
        array3 = Array(array_traits, array1_values)
        self.assertNotEqual(array1, None)
        self.assertNotEqual(array1, array2)
        self.assertEqual(array1, array3)

    def _test_hashcode(self, array_traits, array1_values, array2_values):
        array1 = Array(array_traits, array1_values)
        array2 = Array(array_traits, array2_values)
        array3 = Array(array_traits, array1_values)
        self.assertNotEqual(hash(array1), hash(array2))
        self.assertEqual(hash(array1), hash(array3))

    def _test_len(self, array_traits, array_values):
        array = Array(array_traits, array_values)
        raw_array = array.raw_array
        self.assertEqual(len(raw_array), len(array))

    def _test_get_item(self, array_traits, array_values):
        array = Array(array_traits, array_values)
        raw_array = array.raw_array
        for value, raw_value in zip(array, raw_array):
            self.assertEqual(value, raw_value)

    def _test_set_item(self, array_traits, array_values):
        array = Array(array_traits, array_values)
        raw_array = array.raw_array
        self.assertTrue(len(array) > 1)
        first_value = array[0]
        second_value = array[1]
        array[0] = second_value
        self.assertEqual(array[0], raw_array[0])
        raw_array[0] = first_value # return the original value for other tests
        self.assertEqual(array[0], raw_array[0])

    def _test_raw_array(self, array_traits, array1_values, array2_values):
        array1 = Array(array_traits, array1_values)
        array2 = Array(array_traits, array2_values)
        array3 = Array(array_traits, array1_values)
        self.assertNotEqual(array1.raw_array, array2.raw_array)
        self.assertEqual(array1.raw_array, array3.raw_array)

    def _test_bitsizeof(self, array_traits, array_values, expected_bitsize, expected_aligned_bitsize):
        array = Array(array_traits, array_values)
        self.assertEqual(expected_bitsize, array.bitsizeof(0))
        self.assertEqual(expected_bitsize, array.bitsizeof(7))

        auto_array = Array(array_traits, array_values, is_auto=True)
        self.assertEqual(bitsizeof_varuint64(len(array_values)) + expected_bitsize, auto_array.bitsizeof(0))
        self.assertEqual(bitsizeof_varuint64(len(array_values)) + expected_bitsize, auto_array.bitsizeof(7))

        aligned_array = Array(array_traits, array_values, set_offset_method=not None)
        self.assertEqual(expected_aligned_bitsize, aligned_array.bitsizeof(0))

    def _test_initialize_offsets(self, array_traits, array_values, expected_bitsize, expected_aligned_bitsize):
        def _set_offset_method(_index, _bitoffset):
            pass

        array = Array(array_traits, array_values)
        self.assertEqual(0 + expected_bitsize, array.initialize_offsets(0))
        self.assertEqual(7 + expected_bitsize, array.initialize_offsets(7))

        auto_array = Array(array_traits, array_values, is_auto=True)
        self.assertEqual(0 + bitsizeof_varuint64(len(array_values)) + expected_bitsize,
                         auto_array.initialize_offsets(0))
        self.assertEqual(7 + bitsizeof_varuint64(len(array_values)) + expected_bitsize,
                         auto_array.initialize_offsets(7))

        aligned_array = Array(array_traits, array_values, set_offset_method=_set_offset_method)
        self.assertEqual(0 + expected_aligned_bitsize, aligned_array.initialize_offsets(0))

    def _test_read(self, array_traits, array_values):
        def _check_offset_method(_index, _bitoffset):
            pass

        array = Array(array_traits, array_values)
        writer = BitStreamWriter()
        array.write(writer)
        reader = BitStreamReader(writer.byte_array)
        read_array = Array(array_traits)
        read_array.read(reader, len(array.raw_array))
        self.assertEqual(array, read_array)

        auto_array = Array(array_traits, array_values, is_auto=True)
        writer = BitStreamWriter()
        auto_array.write(writer)
        reader = BitStreamReader(writer.byte_array)
        read_auto_array = Array(array_traits, is_auto=True)
        read_auto_array.read(reader, len(auto_array.raw_array))
        self.assertEqual(auto_array, read_auto_array)

        aligned_array = Array(array_traits, array_values, check_offset_method=_check_offset_method)
        writer = BitStreamWriter()
        aligned_array.write(writer)
        reader = BitStreamReader(writer.byte_array)
        read_aligned_array = Array(array_traits, check_offset_method=_check_offset_method)
        read_aligned_array.read(reader, len(aligned_array.raw_array))
        self.assertEqual(aligned_array, read_aligned_array)

        if array_traits.HAS_BITSIZEOF_CONSTANT and array_traits.bitsizeof() % 8 == 0:
            implicit_array = Array(array_traits, array_values, is_implicit=True)
            writer = BitStreamWriter()
            implicit_array.write(writer)
            reader = BitStreamReader(writer.byte_array)
            read_implicit_array = Array(array_traits, is_implicit=True)
            read_implicit_array.read(reader)
            self.assertEqual(implicit_array, read_implicit_array)
        elif not array_traits.HAS_BITSIZEOF_CONSTANT:
            with self.assertRaises(PythonRuntimeException):
                Array(array_traits, is_implicit=True).read(reader)

    def _test_write(self, array_traits, array_values, expected_bitsize, expected_aligned_bitsize):
        def _check_offset_method(_index, _bitoffset):
            pass

        array = Array(array_traits, array_values)
        writer = BitStreamWriter()
        array.write(writer)
        self.assertEqual(expected_bitsize, writer.bitposition)

        auto_array = Array(array_traits, array_values, is_auto=True)
        writer = BitStreamWriter()
        auto_array.write(writer)
        self.assertEqual(bitsizeof_varuint64(len(array_values)) + expected_bitsize, writer.bitposition)

        aligned_array = Array(array_traits, array_values, check_offset_method=_check_offset_method)
        writer = BitStreamWriter()
        aligned_array.write(writer)
        self.assertEqual(expected_aligned_bitsize, writer.bitposition)
