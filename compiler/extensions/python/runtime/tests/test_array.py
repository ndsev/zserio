import unittest

from zserio.array import (Array, BitFieldArrayTraits, SignedBitFieldArrayTraits,
                          VarUInt16ArrayTraits, VarUInt32ArrayTraits, VarUInt64ArrayTraits, VarUIntArrayTraits,
                          VarSizeArrayTraits, VarInt16ArrayTraits, VarInt32ArrayTraits, VarInt64ArrayTraits,
                          VarIntArrayTraits, Float16ArrayTraits, Float32ArrayTraits, Float64ArrayTraits,
                          StringArrayTraits, BoolArrayTraits, BitBufferArrayTraits, ObjectArrayTraits,
                          PackingContextNode)
from zserio.bitposition import alignto
from zserio.bitbuffer import BitBuffer
from zserio.bitreader import BitStreamReader
from zserio.bitsizeof import bitsizeof_varuint64
from zserio.bitwriter import BitStreamWriter
from zserio import PythonRuntimeException
from zserio.limits import UINT64_MIN, UINT64_MAX, INT64_MIN, INT64_MAX

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

    class DummyObject:
        def __init__(self, value = int()):
            self._value = value

        @classmethod
        def create(cls, reader, _index):
            instance = cls()
            instance.read(reader)

            return instance

        @classmethod
        def create_packed(cls, context_node, reader, _index):
            instance = cls()
            instance.read_packed(context_node, reader)

            return instance

        def __eq__(self, other):
            return self._value == other._value

        def __hash__(self):
            return hash(self._value)

        @property
        def value(self):
            return self._value

        @staticmethod
        def create_packing_context(context_node):
            context_node.create_child().create_context()

        def init_packing_context(self, context_node):
            context = context_node.children[0].context
            context.init(self._value)

        @staticmethod
        def bitsizeof(_bitposition):
            return 31 # to make an unaligned type

        def bitsizeof_packed(self, context_node, bitposition):
            end_bitposition = bitposition

            context = context_node.children[0].context
            end_bitposition += context.bitsizeof(BitFieldArrayTraits(31), end_bitposition, self._value)

            return end_bitposition - bitposition

        def initialize_offsets(self, bitposition):
            return bitposition + self.bitsizeof(bitposition)

        def initialize_offsets_packed(self, context_node, bitposition):
            end_bitposition = bitposition

            context = context_node.children[0].context
            end_bitposition += context.bitsizeof(BitFieldArrayTraits(31), end_bitposition, self._value)

            return end_bitposition

        def read(self, reader):
            self._value = reader.read_bits(self.bitsizeof(0))

        def read_packed(self, context_node, reader):
            context = context_node.children[0].context
            self._value = context.read(BitFieldArrayTraits(31), reader)

        def write(self, writer, *, zserio_call_initialize_offsets=True):
            del zserio_call_initialize_offsets
            writer.write_bits(self._value, self.bitsizeof(0))

        def write_packed(self, context_node, writer):
            context = context_node.children[0].context
            context.write(BitFieldArrayTraits(31), writer, self._value)

    def test_object_array(self):
        array_traits = ObjectArrayTraits(ArrayTest.DummyObject.create,
                                         ArrayTest.DummyObject.create_packed,
                                         ArrayTest.DummyObject.create_packing_context)
        array1_values = [ArrayTest.DummyObject(1), ArrayTest.DummyObject(2)]
        array1_bitsizeof = 2 * 31
        array1_aligned_bitsizeof = 31 + 1 + 31
        array2_values = [ArrayTest.DummyObject(3), ArrayTest.DummyObject(4)]
        self._test_array(array_traits, array1_values, array1_bitsizeof, array1_aligned_bitsizeof, array2_values)

    class PackedBitsizeCalculator:
        @staticmethod
        def calc_max_delta_bits(array_values):
            max_delta_bits = 0
            if array_values:
                prev = array_values[0]
                for i in range(1, len(array_values)):
                    delta = array_values[i] - prev
                    if (delta.bit_length() + 1) > max_delta_bits:
                        max_delta_bits = delta.bit_length() + 1
                    prev = array_values[i]
            return max_delta_bits

        @staticmethod
        def calc_bitsize(array_traits, array_values, signed_max_delta_bits):
            num_values = len(array_values)
            if num_values == 0:
                return 0

            bitsize = 1 # descriptor bit - is packed
            if num_values > 1 and signed_max_delta_bits <= 64:
                bitsize += 6 # descriptor bits - max bit number
                if array_traits.HAS_BITSIZEOF_CONSTANT:
                    bitsize += array_traits.bitsizeof() # first element
                else:
                    bitsize += array_traits.bitsizeof(0, array_values[0])
                if signed_max_delta_bits > 1:
                    bitsize += (num_values - 1) * signed_max_delta_bits # remaining elements
                return bitsize
            else:
                if array_traits.HAS_BITSIZEOF_CONSTANT:
                    bitsize += num_values * array_traits.bitsizeof()
                else:
                    for value in array_values:
                        bitsize += array_traits.bitsizeof(0, value)
                return bitsize

        @staticmethod
        def calc_aligned_bitsize(array_traits, bitposition, array_values, signed_max_delta_bits):
            num_values = len(array_values)
            if num_values == 0:
                return 0

            end_bitposition = bitposition
            end_bitposition += 1 # descriptor bit - is packed
            if num_values > 1 and signed_max_delta_bits <= 64:
                end_bitposition += 6 # descriptor bits - max bit number
                end_bitposition = alignto(8, end_bitposition) # alignment before first element
                if array_traits.HAS_BITSIZEOF_CONSTANT:
                    end_bitposition += array_traits.bitsizeof() # first element
                else:
                    end_bitposition += array_traits.bitsizeof(end_bitposition, array_values[0])
                if signed_max_delta_bits > 1:
                    end_bitposition = alignto(8, end_bitposition) # alignment before the second element
                    end_bitposition += signed_max_delta_bits # second element (stored as delta)
                    # remaining elements including alignment
                    end_bitposition += (num_values - 2) * alignto(8, signed_max_delta_bits)
            else:
                if array_traits.HAS_BITSIZEOF_CONSTANT:
                    end_bitposition = alignto(8, end_bitposition) # alignment before first element
                    end_bitposition += array_traits.bitsizeof() # first element
                    # remaining elements including alignment
                    end_bitposition += (num_values - 1) * alignto(8, array_traits.bitsizeof())
                else:
                    for value in array_values:
                        end_bitposition = alignto(8, end_bitposition)
                        end_bitposition += array_traits.bitsizeof(end_bitposition, value)

            return end_bitposition - bitposition

    def test_bitfield_packed_array(self):
        array_traits = BitFieldArrayTraits(64)
        array1_values = [10, 11, 12]
        array2_values = [10, 10, 10] # zero delta
        self._test_packed_array(array_traits, array1_values, array2_values)

        array1_values = [] # empty
        array2_values = [10] # single element
        self._test_packed_array(array_traits, array1_values, array2_values)

        # packing not applied, delta is too big
        array1_values = [UINT64_MIN, UINT64_MAX]
        array2_values = [UINT64_MAX, UINT64_MAX // 2, UINT64_MIN]
        self._test_packed_array(array_traits, array1_values, array2_values)

    def test_signed_bitfield_packed_array(self):
        array_traits = SignedBitFieldArrayTraits(64)
        array1_values = [-10, 11, -12]
        array2_values = [-10, -10, -10] # zero delta
        self._test_packed_array(array_traits, array1_values, array2_values)

        array1_values = [] # empty
        array2_values = [-10] # single element
        self._test_packed_array(array_traits, array1_values, array2_values)

        # packing not applied, delta is too big
        array1_values = [INT64_MIN, INT64_MAX]
        array2_values = [INT64_MIN, 0, INT64_MAX]
        self._test_packed_array(array_traits, array1_values, array2_values)

    def test_varuint_packed_array(self):
        array_traits = VarUIntArrayTraits()
        array1_values = [100, 200, 300]
        array2_values = [300, 200, 100]
        self._test_packed_array(array_traits, array1_values, array2_values)

        array1_values = [UINT64_MIN, UINT64_MAX]
        array2_values = [UINT64_MAX, UINT64_MAX // 2, UINT64_MIN]
        self._test_packed_array(array_traits, array1_values, array2_values)

    def test_object_packed_array(self):
        class DummyObjectBitsizeCalculator:
            @staticmethod
            def calc_max_delta_bits(array_values):
                plain_values = [obj.value for obj in array_values]
                return ArrayTest.PackedBitsizeCalculator.calc_max_delta_bits(plain_values)

            @staticmethod
            def calc_bitsize(_array_traits, array_values, signed_max_delta_bits):
                # array_traits are not usable here, we need array traits for the underlying value
                plain_values = [obj.value for obj in array_values]
                return ArrayTest.PackedBitsizeCalculator.calc_bitsize(
                    BitFieldArrayTraits(31), plain_values, signed_max_delta_bits)

            @staticmethod
            def calc_aligned_bitsize(_array_traits, bitposition, array_values, signed_max_delta_bits):
                # array_traits are not usable here, we need array traits for the underlying value
                plain_values = [obj.value for obj in array_values]
                return ArrayTest.PackedBitsizeCalculator.calc_aligned_bitsize(
                    BitFieldArrayTraits(31), bitposition, plain_values, signed_max_delta_bits)

        array_traits = ObjectArrayTraits(ArrayTest.DummyObject.create,
                                         ArrayTest.DummyObject.create_packed,
                                         ArrayTest.DummyObject.create_packing_context)
        array1_values = [ArrayTest.DummyObject(1), ArrayTest.DummyObject(2)]
        array2_values = [ArrayTest.DummyObject(3), ArrayTest.DummyObject(4)]
        self._test_packed_array(array_traits, array1_values, array2_values, DummyObjectBitsizeCalculator)

    def test_packing_context_node(self):
        with self.assertRaises(PythonRuntimeException):
            self.assertIsNotNone(PackingContextNode().context)

    @staticmethod
    def _set_offset_method(_index, _bitoffset):
        pass

    @staticmethod
    def _check_offset_method(_index, _bitoffset):
        pass

    def _test_array(self, array_traits,
                    array1_values, array1_bitsizeof, array1_aligned_bitsizeof, array2_values):
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

        aligned_array = Array(array_traits, array_values,
                              set_offset_method=ArrayTest._set_offset_method)
        self.assertEqual(0 + expected_aligned_bitsize, aligned_array.bitsizeof(0))
        self.assertEqual(7 + expected_aligned_bitsize, aligned_array.bitsizeof(1))
        self.assertEqual(5 + expected_aligned_bitsize, aligned_array.bitsizeof(3))
        self.assertEqual(3 + expected_aligned_bitsize, aligned_array.bitsizeof(5))
        self.assertEqual(1 + expected_aligned_bitsize, aligned_array.bitsizeof(7))

    def _test_initialize_offsets(self, array_traits,
                                 array_values, expected_bitsize, expected_aligned_bitsize):
        array = Array(array_traits, array_values)
        self.assertEqual(0 + expected_bitsize, array.initialize_offsets(0))
        self.assertEqual(7 + expected_bitsize, array.initialize_offsets(7))

        auto_array = Array(array_traits, array_values, is_auto=True)
        self.assertEqual(0 + bitsizeof_varuint64(len(array_values)) + expected_bitsize,
                         auto_array.initialize_offsets(0))
        self.assertEqual(7 + bitsizeof_varuint64(len(array_values)) + expected_bitsize,
                         auto_array.initialize_offsets(7))

        aligned_array = Array(array_traits, array_values, set_offset_method=ArrayTest._set_offset_method)
        self.assertEqual(0 + expected_aligned_bitsize, aligned_array.initialize_offsets(0))
        self.assertEqual(1 + 7 + expected_aligned_bitsize, aligned_array.initialize_offsets(1))
        self.assertEqual(3 + 5 + expected_aligned_bitsize, aligned_array.initialize_offsets(3))
        self.assertEqual(5 + 3 + expected_aligned_bitsize, aligned_array.initialize_offsets(5))
        self.assertEqual(7 + 1 + expected_aligned_bitsize, aligned_array.initialize_offsets(7))

    def _test_read(self, array_traits, array_values):
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

        aligned_array = Array(array_traits, array_values, check_offset_method=ArrayTest._check_offset_method)
        writer = BitStreamWriter()
        aligned_array.write(writer)
        reader = BitStreamReader(writer.byte_array)
        read_aligned_array = Array(array_traits, check_offset_method=ArrayTest._check_offset_method)
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

    def _test_write(self, array_traits,
                    array_values, expected_bitsize, expected_aligned_bitsize):
        array = Array(array_traits, array_values)
        writer = BitStreamWriter()
        array.write(writer)
        self.assertEqual(expected_bitsize, writer.bitposition)

        auto_array = Array(array_traits, array_values, is_auto=True)
        writer = BitStreamWriter()
        auto_array.write(writer)
        self.assertEqual(bitsizeof_varuint64(len(array_values)) + expected_bitsize, writer.bitposition)

        aligned_array = Array(array_traits, array_values, check_offset_method=ArrayTest._check_offset_method)
        writer = BitStreamWriter()
        writer.write_bool(False)
        aligned_array.write(writer)
        self.assertEqual(1 + 7 + expected_aligned_bitsize, writer.bitposition)

    def _test_packed_array(self, array_traits, array1_values, array2_values,
                           packed_bitsize_calculator=PackedBitsizeCalculator):
        self._test_from_reader_packed(array_traits, array1_values)
        self._test_from_reader_packed(array_traits, array2_values)

        self._test_bitsizeof_packed(array_traits, array1_values, packed_bitsize_calculator)
        self._test_bitsizeof_packed(array_traits, array2_values, packed_bitsize_calculator)

        self._test_initialize_offsets_packed(array_traits, array1_values, packed_bitsize_calculator)
        self._test_initialize_offsets_packed(array_traits, array2_values, packed_bitsize_calculator)

        self._test_read_packed(array_traits, array1_values)
        self._test_read_packed(array_traits, array2_values)

        self._test_write_packed(array_traits, array1_values, packed_bitsize_calculator)
        self._test_write_packed(array_traits, array2_values, packed_bitsize_calculator)

    def _test_from_reader_packed(self, array_traits, array_values):
        array = Array(array_traits, array_values)
        writer = BitStreamWriter()
        array.write_packed(writer)
        reader = BitStreamReader(writer.byte_array)
        read_array = Array.from_reader_packed(array_traits, reader, len(array_values))
        self.assertEqual(array, read_array)

    def _test_bitsizeof_packed(self, array_traits, array_values, packed_bitsize_calculator):
        signed_max_delta_bits = packed_bitsize_calculator.calc_max_delta_bits(array_values)
        expected_bitsize = packed_bitsize_calculator.calc_bitsize(array_traits, array_values,
                                                                  signed_max_delta_bits)

        array = Array(array_traits, array_values)
        self.assertEqual(expected_bitsize, array.bitsizeof_packed(0))
        self.assertEqual(expected_bitsize, array.bitsizeof_packed(7))

        auto_array = Array(array_traits, array_values, is_auto=True)
        self.assertEqual(bitsizeof_varuint64(len(array_values)) + expected_bitsize,
                         auto_array.bitsizeof_packed(0))
        self.assertEqual(bitsizeof_varuint64(len(array_values)) + expected_bitsize,
                         auto_array.bitsizeof_packed(7))

        aligned_array = Array(array_traits, array_values,
                              set_offset_method=ArrayTest._set_offset_method,
                              check_offset_method=ArrayTest._check_offset_method)

        for bitposition in [0, 1, 3, 5, 7]:
            expected_aligned_bitsize = packed_bitsize_calculator.calc_aligned_bitsize(
                array_traits, bitposition, array_values, signed_max_delta_bits)
            self.assertEqual(expected_aligned_bitsize, aligned_array.bitsizeof_packed(bitposition),
                             f"bitposition = {bitposition}")

    def _test_initialize_offsets_packed(self, array_traits, array_values, packed_bitsize_calculator):
        signed_max_delta_bits = packed_bitsize_calculator.calc_max_delta_bits(array_values)
        expected_bitsize = packed_bitsize_calculator.calc_bitsize(array_traits, array_values,
                                                                  signed_max_delta_bits)

        array = Array(array_traits, array_values)
        self.assertEqual(0 + expected_bitsize, array.initialize_offsets_packed(0))
        self.assertEqual(7 + expected_bitsize, array.initialize_offsets_packed(7))

        auto_array = Array(array_traits, array_values, is_auto=True)
        self.assertEqual(0 + bitsizeof_varuint64(len(array_values)) + expected_bitsize,
                         auto_array.initialize_offsets_packed(0))
        self.assertEqual(7 + bitsizeof_varuint64(len(array_values)) + expected_bitsize,
                         auto_array.initialize_offsets_packed(7))

        aligned_array = Array(array_traits, array_values,
                              set_offset_method=ArrayTest._set_offset_method,
                              check_offset_method=ArrayTest._check_offset_method)

        for bitposition in [0, 1, 3, 5, 7]:
            expected_aligned_bitsize = packed_bitsize_calculator.calc_aligned_bitsize(
                array_traits, bitposition, array_values, signed_max_delta_bits)
            self.assertEqual(bitposition + expected_aligned_bitsize,
                             aligned_array.initialize_offsets_packed(bitposition),
                             f"bitposition = {bitposition}")

    def _test_read_packed(self, array_traits, array_values):
        array = Array(array_traits, array_values)
        writer = BitStreamWriter()
        array.write_packed(writer)
        reader = BitStreamReader(writer.byte_array)
        read_array = Array(array_traits)
        read_array.read_packed(reader, len(array.raw_array))
        self.assertEqual(array, read_array)

        auto_array = Array(array_traits, array_values, is_auto=True)
        writer = BitStreamWriter()
        auto_array.write_packed(writer)
        reader = BitStreamReader(writer.byte_array)
        read_auto_array = Array(array_traits, is_auto=True)
        read_auto_array.read_packed(reader, len(auto_array.raw_array))
        self.assertEqual(auto_array, read_auto_array)

        aligned_array = Array(array_traits, array_values,
                              set_offset_method=ArrayTest._set_offset_method,
                              check_offset_method=ArrayTest._check_offset_method)
        writer = BitStreamWriter()
        aligned_array.write_packed(writer)
        reader = BitStreamReader(writer.byte_array)
        read_aligned_array = Array(array_traits,
                                   set_offset_method=ArrayTest._set_offset_method,
                                   check_offset_method=ArrayTest._check_offset_method)
        read_aligned_array.read_packed(reader, len(aligned_array.raw_array))
        self.assertEqual(aligned_array, read_aligned_array)

        with self.assertRaises(PythonRuntimeException):
            Array(array_traits, is_implicit=True).read_packed(reader)

    def _test_write_packed(self, array_traits, array_values, packed_bitsize_calculator):
        signed_max_delta_bits = packed_bitsize_calculator.calc_max_delta_bits(array_values)
        expected_bitsize = packed_bitsize_calculator.calc_bitsize(array_traits, array_values,
                                                                  signed_max_delta_bits)

        array = Array(array_traits, array_values)
        writer = BitStreamWriter()
        array.write_packed(writer)
        self.assertEqual(expected_bitsize, writer.bitposition)

        auto_array = Array(array_traits, array_values, is_auto=True)
        writer = BitStreamWriter()
        auto_array.write_packed(writer)
        self.assertEqual(bitsizeof_varuint64(len(array_values)) + expected_bitsize, writer.bitposition)

        aligned_array = Array(array_traits, array_values,
                              set_offset_method=ArrayTest._set_offset_method,
                              check_offset_method=ArrayTest._check_offset_method)
        for start_bitposition in [0, 1, 3, 5, 7]:
            writer = BitStreamWriter()
            if start_bitposition:
                writer.write_bits(0, start_bitposition)
            expected_aligned_bitsize = packed_bitsize_calculator.calc_aligned_bitsize(
                array_traits, start_bitposition, array_values, signed_max_delta_bits)
            aligned_array.write_packed(writer)
            self.assertEqual(start_bitposition + expected_aligned_bitsize, writer.bitposition,
                             f"start_bitposition={start_bitposition}")
