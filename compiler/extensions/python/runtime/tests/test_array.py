import unittest

from zserio.array import (Array, BitFieldArrayTraits, SignedBitFieldArrayTraits,
                          VarUInt16ArrayTraits, VarUInt32ArrayTraits, VarUInt64ArrayTraits, VarUIntArrayTraits,
                          VarSizeArrayTraits, VarInt16ArrayTraits, VarInt32ArrayTraits, VarInt64ArrayTraits,
                          VarIntArrayTraits, Float16ArrayTraits, Float32ArrayTraits, Float64ArrayTraits,
                          BytesArrayTraits, StringArrayTraits, BoolArrayTraits, BitBufferArrayTraits,
                          ObjectArrayTraits, DeltaContext)
from zserio.bitposition import alignto
from zserio.bitbuffer import BitBuffer
from zserio.bitreader import BitStreamReader
from zserio.bitsizeof import bitsizeof_varsize
from zserio.bitwriter import BitStreamWriter
from zserio import PythonRuntimeException
from zserio.limits import UINT64_MIN, UINT64_MAX, INT64_MIN, INT64_MAX, UINT8_MAX, INT16_MIN

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

    def test_bytes_array(self):
        array_traits = BytesArrayTraits()
        array1_values = [bytearray([1, 255]), bytearray([127, 128])]
        array1_bitsizeof = 2 * (1 + 2) * 8
        array1_aligned_bitsizeof = array1_bitsizeof
        array2_values = [bytearray([0, 0]), bytearray([255, 255])]
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
        array1_values = [BitBuffer(bytes([0xAB, 0xE0]), 11), BitBuffer(bytes([0xAB, 0xCD, 0xFE]), 23)]
        array1_bitsizeof = 8 + 11 + 8 + 23
        array1_aligned_bitsizeof = 8 + 11 + 5 + 8 + 23
        array2_values = [BitBuffer(bytes([0xBA, 0xE0]), 11), BitBuffer(bytes([0xBA, 0xDC, 0xFE]), 23)]
        self._test_array(array_traits, array1_values, array1_bitsizeof, array1_aligned_bitsizeof, array2_values)

    class DummyObject:
        def __init__(self, value = int()):
            self._value = value

        @classmethod
        def from_reader(cls, zserio_reader: BitStreamReader):
            self = object.__new__(cls)
            self.read(zserio_reader)
            return self

        @classmethod
        def from_reader_packed(cls, zserio_context, zserio_reader: BitStreamReader):
            self = object.__new__(cls)
            self.read_packed(zserio_context, zserio_reader)
            return self

        def __eq__(self, other):
            return self._value == other._value

        def __hash__(self):
            return hash(self._value)

        class ZserioPackingContext:
            def __init__(self):
                self._value = DeltaContext()

            @property
            def value(self):
                return self._value

        def init_packing_context(self, context):
            context.value.init(BitFieldArrayTraits(31), self._value)

        @staticmethod
        def bitsizeof(_bitposition):
            return 31 # to make an unaligned type

        def bitsizeof_packed(self, context, bitposition):
            end_bitposition = bitposition

            end_bitposition += context.value.bitsizeof(BitFieldArrayTraits(31), self._value)

            return end_bitposition - bitposition

        def initialize_offsets(self, bitposition):
            return bitposition + self.bitsizeof(bitposition)

        def initialize_offsets_packed(self, context, bitposition):
            end_bitposition = bitposition

            end_bitposition += context.value.bitsizeof(BitFieldArrayTraits(31), self._value)

            return end_bitposition

        def read(self, reader):
            self._value = reader.read_bits(self.bitsizeof(0))

        def read_packed(self, context, reader):
            self._value = context.value.read(BitFieldArrayTraits(31), reader)

        def write(self, writer, *, zserio_call_initialize_offsets=True):
            del zserio_call_initialize_offsets
            writer.write_bits(self._value, self.bitsizeof(0))

        def write_packed(self, context, writer):
            context.value.write(BitFieldArrayTraits(31), writer, self._value)

    class DummyObjectElementFactory:
        IS_OBJECT_PACKABLE = True

        @staticmethod
        def create(reader, _index):
            return ArrayTest.DummyObject.from_reader(reader)

        @staticmethod
        def create_packing_context():
            return ArrayTest.DummyObject.ZserioPackingContext()

        @staticmethod
        def create_packed(context, reader, _index):
            return ArrayTest.DummyObject.from_reader_packed(context, reader)

    def test_object_array(self):
        array_traits = ObjectArrayTraits(ArrayTest.DummyObjectElementFactory)
        array1_values = [ArrayTest.DummyObject(1), ArrayTest.DummyObject(2)]
        array1_bitsizeof = 2 * 31
        array1_aligned_bitsizeof = 31 + 1 + 31
        array2_values = [ArrayTest.DummyObject(3), ArrayTest.DummyObject(4)]
        self._test_array(array_traits, array1_values, array1_bitsizeof, array1_aligned_bitsizeof, array2_values)

    def test_bitfield_packed_array(self):
        array_traits64 = BitFieldArrayTraits(64)

        # none-zero delta
        array1_values = [10, 11, 12]
        array1_max_delta_bit_size = 1
        array1_bitsizeof = self._calc_packed_bit_size(64, len(array1_values), array1_max_delta_bit_size)
        array1_aligned_bitsizeof = self._calc_aligned_packed_bit_size(64, len(array1_values),
                                                                      array1_max_delta_bit_size)
        self._test_packed_array(array_traits64, array1_values, array1_bitsizeof, array1_aligned_bitsizeof)

        # zero delta
        array2_values = [10, 10, 10]
        array2_bitsizeof = self.PACKING_DESCRIPTOR_BITSIZE + 64
        array2_aligned_bitsizeof = self.PACKING_DESCRIPTOR_BITSIZE + 64 + 1
        self._test_packed_array(array_traits64, array2_values, array2_bitsizeof, array2_aligned_bitsizeof)

        # one-element array
        array3_values = [10]
        array3_bitsizeof = 1 + 64
        array3_aligned_bitsizeof = 1 + 64
        self._test_packed_array(array_traits64, array3_values, array3_bitsizeof, array3_aligned_bitsizeof)

        # empty array
        array4_values = []
        array4_bitsizeof = 0
        array4_aligned_bitsizeof = 0
        self._test_packed_array(array_traits64, array4_values, array4_bitsizeof, array4_aligned_bitsizeof)

        # packing not applied, delta is too big
        self._test_packed_array(array_traits64, [UINT64_MIN, UINT64_MAX])
        self._test_packed_array(array_traits64, [UINT64_MAX, UINT64_MAX // 2, UINT64_MIN])

        # will have maxBitNumber 62 bits
        self._test_packed_array(array_traits64, [0, INT64_MAX // 2, 100, 200, 300, 400, 500, 600, 700])

        # will not be packed because unpacked 8bit values will be more efficient
        array_traits8 = BitFieldArrayTraits(8)
        array5_values = [UINT8_MAX, 0, 10, 20, 30, 40] # max_bit_number 8, delta needs 9 bits
        array5_bitsizeof = 1 + 6 * 8
        array5_aligned_bitsizeof = 1 + 8 + 7 + 5 * 8
        self._test_packed_array(array_traits8, array5_values, array5_bitsizeof, array5_aligned_bitsizeof)

        # will not be packed because unpacked 8bit values will be more efficient
        # (6 bits more are needed to store max_bit_number in descriptor if packing was enabled)
        array6_values = [UINT8_MAX, UINT8_MAX // 2 + 1, 10, 20, 30, 40] # max_bit_number 7, delta needs 8 bits
        array6_bitsizeof = 1 + 6 * 8
        array6_aligned_bitsizeof = 1 + 8 + 7 + 5 * 8
        self._test_packed_array(array_traits8, array6_values, array6_bitsizeof, array6_aligned_bitsizeof)

    def test_signed_bitfield_packed_array(self):
        array_traits64 = SignedBitFieldArrayTraits(64)
        self._test_packed_array(array_traits64, [-10, 11, -12])
        self._test_packed_array(array_traits64, [-10, -10, -10]) # zero delta

        self._test_packed_array(array_traits64, []) # empty
        self._test_packed_array(array_traits64, [-10]) # single element

        # packing not applied, delta is too big
        self._test_packed_array(array_traits64, [INT64_MIN, INT64_MAX])
        self._test_packed_array(array_traits64, [INT64_MIN, 0, INT64_MAX])

        # will not be packed because unpacked 16bit values will be more efficient
        # (6 bits more are needed to store max_bit_number in descriptor if packing was enabled)
        array_traits16 = SignedBitFieldArrayTraits(16)
        array16_values = [INT16_MIN, -1, 10, 20, 30, 40] # max_bit_number 15, delta needs 16 bits
        array16_bitsizeof = 1 + 6 * 16
        array16_aligned_bitsizeof = 1 + 16 + 7 + 5 * 16
        self._test_packed_array(array_traits16, array16_values, array16_bitsizeof, array16_aligned_bitsizeof)

    def test_varuint_packed_array(self):
        array_traits = VarUIntArrayTraits()
        self._test_packed_array(array_traits, [100, 200, 300])
        self._test_packed_array(array_traits, [300, 200, 100])

        # won't be packed because unpacked varuint values will be more efficient
        unpacked_array = [5000000, 0, 0, 0, 0, 0, 0]
        unpacked_bitsizeof = 1 + 32 + 6 * 8
        unpacked_aligned_bitsizeof = 1 + 32 + 7 + 6 * 8
        self._test_packed_array(array_traits, unpacked_array, unpacked_bitsizeof, unpacked_aligned_bitsizeof)

        self._test_packed_array(array_traits, [UINT64_MIN, UINT64_MAX])
        self._test_packed_array(array_traits, [UINT64_MAX, UINT64_MAX // 2, UINT64_MIN])

    def test_object_packed_array(self):
        array_traits = ObjectArrayTraits(ArrayTest.DummyObjectElementFactory)
        self._test_packed_array(array_traits, [ArrayTest.DummyObject(1), ArrayTest.DummyObject(2)])
        self._test_packed_array(array_traits, [ArrayTest.DummyObject(3), ArrayTest.DummyObject(4)])

    @staticmethod
    def _set_offset_method(_index, _bitoffset):
        pass

    @staticmethod
    def _check_offset_method(_index, _bitoffset):
        pass

    def _test_array(self, array_traits,
                    array1_values, array1_bitsizeof, array1_aligned_bitsizeof, array2_values):
        self._test_eq(array_traits, array1_values, array2_values)
        self._test_hashcode(array_traits, array1_values, array2_values)
        self._test_len(array_traits, array1_values)
        self._test_get_item(array_traits, array1_values)
        self._test_set_item(array_traits, array1_values)
        self._test_raw_array(array_traits, array1_values, array2_values)

        auto_bitsize = bitsizeof_varsize(len(array1_values))

        self._test_array_normal(array_traits, array1_values, array1_bitsizeof)
        self._test_array_auto(array_traits, array1_values, auto_bitsize + array1_bitsizeof)
        self._test_array_aligned(array_traits, array1_values, array1_aligned_bitsizeof)
        self._test_array_aligned_auto(array_traits, array1_values, auto_bitsize + array1_aligned_bitsizeof)
        self._test_array_implicit(array_traits, array1_values, array1_bitsizeof)

    def _test_packed_array(self, array_traits, array_values, array_bitsizeof=None,
                           array_aligned_bitsizeof=None):

        self._test_packed_array_normal(array_traits, array_values, array_bitsizeof)

        auto_size_bitsize = bitsizeof_varsize(len(array_values))
        auto_bitsize = auto_size_bitsize + array_bitsizeof if array_bitsizeof is not None else None
        self._test_packed_array_auto(array_traits, array_values, auto_bitsize)

        self._test_packed_array_aligned(array_traits, array_values, array_aligned_bitsizeof)

        auto_aligned_bitsize = (auto_size_bitsize + array_aligned_bitsizeof
                                if array_aligned_bitsizeof is not None else None)
        self._test_packed_array_aligned_auto(array_traits, array_values, auto_aligned_bitsize)

        self._test_packed_array_implicit(array_traits, array_values, array_bitsizeof)

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

    def _test_array_normal(self, array_traits, array_values, expected_bitsize):
        for i in range(8):
            array = Array(array_traits, array_values)

            bitsize = array.bitsizeof(i)
            self.assertEqual(expected_bitsize, bitsize)
            self.assertEqual(i + bitsize, array.initialize_offsets(i), i)

            writer = BitStreamWriter()
            if i > 0:
                writer.write_bits(0, i)
            array.write(writer)
            self.assertEqual(i + bitsize, writer.bitposition, i)

            from_reader = BitStreamReader(writer.byte_array, writer.bitposition)
            self.assertEqual(0, from_reader.read_bits(i))
            read_array_from_reader = Array.from_reader(array_traits, from_reader, len(array_values))
            self.assertEqual(array, read_array_from_reader, i)

            reader = BitStreamReader(writer.byte_array, writer.bitposition)
            if i > 0:
                self.assertEqual(0, reader.read_bits(i))
            read_array = Array(array_traits)
            read_array.read(reader, len(array_values))
            self.assertEqual(array, read_array, i)

    def _test_array_auto(self, array_traits, array_values, expected_bitsize):
        for i in range(8):
            array = Array(array_traits, array_values, is_auto=True)

            bitsize = array.bitsizeof(i)
            self.assertEqual(expected_bitsize, bitsize, i)
            self.assertEqual(i + bitsize, array.initialize_offsets(i), i)

            writer = BitStreamWriter()
            if i > 0:
                writer.write_bits(0, i)
            array.write(writer)
            self.assertEqual(i + bitsize, writer.bitposition, i)

            from_reader = BitStreamReader(writer.byte_array, writer.bitposition)
            self.assertEqual(0, from_reader.read_bits(i))
            read_array_from_reader = Array.from_reader(array_traits, from_reader, is_auto=True)
            self.assertEqual(array, read_array_from_reader, i)

            reader = BitStreamReader(writer.byte_array, writer.bitposition)
            if i > 0:
                self.assertEqual(0, reader.read_bits(i))
            read_array = Array(array_traits, is_auto=True)
            read_array.read(reader)
            self.assertEqual(array, read_array, i)

    def _test_array_aligned(self, array_traits, array_values, expected_bitsize):
        for i in range(8):
            array = Array(array_traits, array_values, set_offset_method=ArrayTest._set_offset_method,
                          check_offset_method=ArrayTest._check_offset_method)

            bitsize = array.bitsizeof(i)
            self.assertEqual(alignto(8, i) - i + expected_bitsize, bitsize, i)
            self.assertEqual(i + bitsize, array.initialize_offsets(i), i)

            writer = BitStreamWriter()
            if i > 0:
                writer.write_bits(0, i)
            array.write(writer)
            self.assertEqual(i + bitsize, writer.bitposition, i)

            from_reader = BitStreamReader(writer.byte_array, writer.bitposition)
            self.assertEqual(0, from_reader.read_bits(i))
            read_array_from_reader = Array.from_reader(array_traits, from_reader, len(array_values),
                                                       set_offset_method=ArrayTest._set_offset_method,
                                                       check_offset_method=ArrayTest._check_offset_method)
            self.assertEqual(array, read_array_from_reader, i)

            reader = BitStreamReader(writer.byte_array, writer.bitposition)
            if i > 0:
                self.assertEqual(0, reader.read_bits(i))
            read_array = Array(array_traits, set_offset_method=ArrayTest._set_offset_method,
                               check_offset_method=ArrayTest._check_offset_method)
            read_array.read(reader, len(array_values))
            self.assertEqual(array, read_array, i)

    def _test_array_aligned_auto(self, array_traits, array_values, expected_bitsize):
        for i in range(8):
            array = Array(array_traits, array_values, is_auto=True,
                          set_offset_method=ArrayTest._set_offset_method,
                          check_offset_method=ArrayTest._check_offset_method)

            bitsize = array.bitsizeof(i)
            self.assertEqual(alignto(8, i) - i + expected_bitsize, bitsize, i)
            self.assertEqual(i + bitsize, array.initialize_offsets(i), i)

            writer = BitStreamWriter()
            if i > 0:
                writer.write_bits(0, i)
            array.write(writer)
            self.assertEqual(i + bitsize, writer.bitposition, i)

            from_reader = BitStreamReader(writer.byte_array, writer.bitposition)
            self.assertEqual(0, from_reader.read_bits(i))
            read_array_from_reader = Array.from_reader(array_traits, from_reader, is_auto=True,
                                                       set_offset_method=ArrayTest._set_offset_method,
                                                       check_offset_method=ArrayTest._check_offset_method)
            self.assertEqual(array, read_array_from_reader, i)

            reader = BitStreamReader(writer.byte_array, writer.bitposition)
            if i > 0:
                self.assertEqual(0, reader.read_bits(i))
            read_array = Array(array_traits, is_auto=True, set_offset_method=ArrayTest._set_offset_method,
                               check_offset_method=ArrayTest._check_offset_method)
            read_array.read(reader)
            self.assertEqual(array, read_array, i)

    def _test_array_implicit(self, array_traits, array_values, expected_bitsize):
        for i in range(8):
            array = Array(array_traits, array_values, is_implicit=True)

            bitsize = array.bitsizeof(i)
            self.assertEqual(expected_bitsize, bitsize, i)
            self.assertEqual(i + bitsize, array.initialize_offsets(i), i)

            writer = BitStreamWriter()
            if i > 0:
                writer.write_bits(0, i)
            array.write(writer)
            self.assertEqual(i + bitsize, writer.bitposition, i)

            from_reader = BitStreamReader(writer.byte_array, writer.bitposition)
            if i > 0:
                self.assertEqual(0, from_reader.read_bits(i))
            if array_traits.HAS_BITSIZEOF_CONSTANT:
                read_array_from_reader = Array.from_reader(array_traits, from_reader, is_implicit=True)
                self.assertEqual(array, read_array_from_reader, i)
            else:
                with self.assertRaises(PythonRuntimeException):
                    Array.from_reader(array_traits, from_reader, is_implicit=True)

            reader = BitStreamReader(writer.byte_array, writer.bitposition)
            if i > 0:
                self.assertEqual(0, reader.read_bits(i))
            read_array = Array(array_traits, is_implicit=True)
            if array_traits.HAS_BITSIZEOF_CONSTANT:
                read_array.read(reader)
                self.assertEqual(array, read_array, i)
            else:
                with self.assertRaises(PythonRuntimeException):
                    read_array.read(reader)

    def _test_packed_array_normal(self, array_traits, array_values, expected_bitsize):
        for i in range(8):
            array = Array(array_traits, array_values)

            bitsize = array.bitsizeof_packed(i)
            if expected_bitsize is not None:
                self.assertEqual(expected_bitsize, bitsize)
            self.assertEqual(i + bitsize, array.initialize_offsets_packed(i), i)

            writer = BitStreamWriter()
            if i > 0:
                writer.write_bits(0, i)
            array.write_packed(writer)
            self.assertEqual(i + bitsize, writer.bitposition, i)

            from_reader = BitStreamReader(writer.byte_array, writer.bitposition)
            self.assertEqual(0, from_reader.read_bits(i))
            read_array_from_reader = Array.from_reader_packed(array_traits, from_reader, len(array_values))
            self.assertEqual(array, read_array_from_reader, i)

            reader = BitStreamReader(writer.byte_array, writer.bitposition)
            if i > 0:
                self.assertEqual(0, reader.read_bits(i))
            read_array = Array(array_traits)
            read_array.read_packed(reader, len(array_values))
            self.assertEqual(array, read_array, i)

    def _test_packed_array_auto(self, array_traits, array_values, expected_bitsize):
        for i in range(8):
            array = Array(array_traits, array_values, is_auto=True)

            bitsize = array.bitsizeof_packed(i)
            if expected_bitsize is not None:
                self.assertEqual(expected_bitsize, bitsize)
            self.assertEqual(i + bitsize, array.initialize_offsets_packed(i), i)

            writer = BitStreamWriter()
            if i > 0:
                writer.write_bits(0, i)
            array.write_packed(writer)
            self.assertEqual(i + bitsize, writer.bitposition, i)

            from_reader = BitStreamReader(writer.byte_array, writer.bitposition)
            self.assertEqual(0, from_reader.read_bits(i))
            read_array_from_reader = Array.from_reader_packed(array_traits, from_reader, is_auto=True)
            self.assertEqual(array, read_array_from_reader, i)

            reader = BitStreamReader(writer.byte_array, writer.bitposition)
            if i > 0:
                self.assertEqual(0, reader.read_bits(i))
            read_array = Array(array_traits, is_auto=True)
            read_array.read_packed(reader)
            self.assertEqual(array, read_array, i)

    def _test_packed_array_aligned(self, array_traits, array_values, expected_bitsize):
        for i in range(8):
            array = Array(array_traits, array_values, set_offset_method=ArrayTest._set_offset_method,
                          check_offset_method=ArrayTest._check_offset_method)

            bitsize = array.bitsizeof_packed(i)
            if expected_bitsize is not None and i == 0:
                self.assertEqual(expected_bitsize, bitsize)
            self.assertEqual(i + bitsize, array.initialize_offsets_packed(i), i)

            writer = BitStreamWriter()
            if i > 0:
                writer.write_bits(0, i)
            array.write_packed(writer)
            self.assertEqual(i + bitsize, writer.bitposition, i)

            from_reader = BitStreamReader(writer.byte_array, writer.bitposition)
            self.assertEqual(0, from_reader.read_bits(i))
            read_array_from_reader = Array.from_reader_packed(
                array_traits, from_reader, len(array_values),
                set_offset_method=ArrayTest._set_offset_method,
                check_offset_method=ArrayTest._check_offset_method)
            self.assertEqual(array, read_array_from_reader, i)

            reader = BitStreamReader(writer.byte_array, writer.bitposition)
            if i > 0:
                self.assertEqual(0, reader.read_bits(i))
            read_array = Array(array_traits, set_offset_method=ArrayTest._set_offset_method,
                               check_offset_method=ArrayTest._check_offset_method)
            read_array.read_packed(reader, len(array_values))
            self.assertEqual(array, read_array, i)

    def _test_packed_array_aligned_auto(self, array_traits, array_values, expected_bitsize):
        for i in range(8):
            array = Array(array_traits, array_values, is_auto=True,
                          set_offset_method=ArrayTest._set_offset_method,
                          check_offset_method=ArrayTest._check_offset_method)

            bitsize = array.bitsizeof_packed(i)
            if expected_bitsize is not None and i == 0:
                self.assertEqual(expected_bitsize, bitsize)
            self.assertEqual(i + bitsize, array.initialize_offsets_packed(i), i)

            writer = BitStreamWriter()
            if i > 0:
                writer.write_bits(0, i)
            array.write_packed(writer)
            self.assertEqual(i + bitsize, writer.bitposition, i)

            from_reader = BitStreamReader(writer.byte_array, writer.bitposition)
            self.assertEqual(0, from_reader.read_bits(i))
            read_array_from_reader = Array.from_reader_packed(
                array_traits, from_reader, is_auto=True,
                set_offset_method=ArrayTest._set_offset_method,
                check_offset_method=ArrayTest._check_offset_method
            )
            self.assertEqual(array, read_array_from_reader, i)

            reader = BitStreamReader(writer.byte_array, writer.bitposition)
            if i > 0:
                self.assertEqual(0, reader.read_bits(i))
            read_array = Array(array_traits, is_auto=True, set_offset_method=ArrayTest._set_offset_method,
                               check_offset_method=ArrayTest._check_offset_method)
            read_array.read_packed(reader)
            self.assertEqual(array, read_array, i)

    def _test_packed_array_implicit(self, array_traits, array_values, expected_bitsize):
        for i in range(8):
            array = Array(array_traits, array_values, is_implicit=True)

            bitsize = array.bitsizeof_packed(i)
            if expected_bitsize is not None:
                self.assertEqual(expected_bitsize, bitsize)
            self.assertEqual(i + bitsize, array.initialize_offsets_packed(i), i)

            writer = BitStreamWriter()
            if i > 0:
                writer.write_bits(0, i)
            array.write_packed(writer)
            self.assertEqual(i + bitsize, writer.bitposition, i)

            reader = BitStreamReader(writer.byte_array, writer.bitposition)
            if i > 0:
                self.assertEqual(0, reader.read_bits(i))
            read_array = Array(array_traits, is_implicit=True)
            with self.assertRaises(PythonRuntimeException):
                read_array.read_packed(reader)

    def _calc_packed_bit_size(self, element_bit_size, array_size, max_delta_bit_size):
        return self.PACKING_DESCRIPTOR_BITSIZE + element_bit_size + (array_size - 1) * (max_delta_bit_size + 1)

    def _calc_aligned_packed_bit_size(self, element_bit_size, array_size, max_delta_bit_size):
        first_element_with_descriptor_bit_size = self.PACKING_DESCRIPTOR_BITSIZE + element_bit_size
        aligned_first_element_with_descriptor_bit_size = (first_element_with_descriptor_bit_size + 7) // 8 * 8
        aligned_max_delta_bit_size = (max_delta_bit_size + 1 + 7) // 8 * 8

        return (aligned_first_element_with_descriptor_bit_size +
                (array_size - 2) * aligned_max_delta_bit_size + (max_delta_bit_size + 1))

    PACKING_DESCRIPTOR_BITSIZE = 1 + 6
