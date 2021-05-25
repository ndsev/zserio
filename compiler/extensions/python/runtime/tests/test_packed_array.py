import unittest

from zserio.array import BitFieldArrayTraits, SignedBitFieldArrayTraits, VarUIntArrayTraits, Float16ArrayTraits
from zserio.packed_array import PackedArray, PackedArrayTraits, ObjectPackedArrayTraits
from zserio.bitposition import alignto
from zserio.bitreader import BitStreamReader
from zserio.bitsizeof import bitsizeof_varuint64
from zserio.bitwriter import BitStreamWriter
from zserio.limits import UINT64_MIN, UINT64_MAX, INT64_MIN, INT64_MAX

class PackedArrayTest(unittest.TestCase):

    class BitsizeCalculator:
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
            if signed_max_delta_bits <= 64:
                bitsize += 6 # descriptor bits - max bit number
                if array_traits.HAS_BITSIZEOF_CONSTANT:
                    bitsize += array_traits.bitsizeof() # first element
                else:
                    bitsize += array_traits.bitsizeof(0, array_values[0])
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
            if signed_max_delta_bits <= 64:
                end_bitposition += 6 # descriptor bits - max bit number
                end_bitposition = alignto(8, end_bitposition) # alignment before first element
                if array_traits.HAS_BITSIZEOF_CONSTANT:
                    end_bitposition += array_traits.bitsizeof() # first element
                else:
                    end_bitposition += array_traits.bitsizeof(end_bitposition, array_values[0])
                if num_values > 1:
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

    def test_bitfield_array(self):
        array_traits = BitFieldArrayTraits(64)
        packed_array_traits = PackedArrayTraits(array_traits)
        array1_values = [10, 11, 12]
        array2_values = [10, 10, 10] # zero delta
        self._test_array(array_traits, packed_array_traits, array1_values, array2_values)

        array1_values = [] # empty
        array2_values = [10] # single element
        self._test_array(array_traits, packed_array_traits, array1_values, array2_values)

        # packing not applied, delta is too big
        array1_values = [UINT64_MIN, UINT64_MAX]
        array2_values = [UINT64_MAX, UINT64_MAX // 2, UINT64_MIN]
        self._test_array(array_traits, packed_array_traits, array1_values, array2_values)

    def test_signed_bitfield_array(self):
        array_traits = SignedBitFieldArrayTraits(64)
        packed_array_traits = PackedArrayTraits(array_traits)
        array1_values = [-10, 11, -12]
        array2_values = [-10, -10, -10] # zero delta
        self._test_array(array_traits, packed_array_traits, array1_values, array2_values)

        array1_values = [] # empty
        array2_values = [-10] # single element
        self._test_array(array_traits, packed_array_traits, array1_values, array2_values)

        # packing not applied, delta is too big
        array1_values = [INT64_MIN, INT64_MAX]
        array2_values = [INT64_MIN, 0, INT64_MAX]
        self._test_array(array_traits, packed_array_traits, array1_values, array2_values)

    def test_varuint_array(self):
        array_traits = VarUIntArrayTraits()
        packed_array_traits = PackedArrayTraits(array_traits)
        array1_values = [100, 200, 300]
        array2_values = [300, 200, 100]
        self._test_array(array_traits, packed_array_traits, array1_values, array2_values)

        array1_values = [UINT64_MIN, UINT64_MAX]
        array2_values = [UINT64_MAX, UINT64_MAX // 2, UINT64_MIN]
        self._test_array(array_traits, packed_array_traits, array1_values, array2_values)

    def test_float16_array(self):
        class Float16BitsizeCalculator:
            @staticmethod
            def calc_max_delta_bits(_array_values):
                return 0

            @staticmethod
            def calc_bitsize(_array_traits, array_values, _signed_max_delta_bits):
                if array_values:
                    return 1 + len(array_values) * 16
                else:
                    return 0

            @staticmethod
            def calc_aligned_bitsize(_array_traits, bitposition, array_values, _signed_max_delta_bits):
                if array_values:
                    end_bitposition = alignto(8, bitposition + 1)
                    end_bitposition += len(array_values) * 16
                    return end_bitposition - bitposition
                else:
                    return 0

        array_traits = Float16ArrayTraits
        packed_array_traits = PackedArrayTraits(array_traits)
        array1_values = [-1.0, 1.0]
        array2_values = []
        self._test_array(array_traits, packed_array_traits, array1_values, array2_values,
                         Float16BitsizeCalculator)

    def test_object_array(self):
        value_array_traits = BitFieldArrayTraits(31)

        class DummyObject:
            def __init__(self, value = int()):
                self._value = value

            @classmethod
            def create_packed(cls, context_iterator, reader, _index):
                instance = cls()
                instance.read_packed(context_iterator, reader)

                return instance

            def __eq__(self, other):
                return self._value == other._value

            def __hash__(self):
                return hash(self._value)

            @property
            def value(self):
                return self._value

            @staticmethod
            def create_packing_context(context_builder):
                context_builder.add_context(BitFieldArrayTraits)

            def init_packing_context(self, context_iterator):
                context = next(context_iterator)
                context.init(self._value)

            def bitsizeof_packed(self, context_iterator, bitposition):
                end_bitposition = bitposition

                context = next(context_iterator)
                end_bitposition += context.bitsizeof(value_array_traits, end_bitposition, self._value)

                return end_bitposition - bitposition

            def initialize_offsets_packed(self, context_iterator, bitposition):
                end_bitposition = bitposition

                context = next(context_iterator)
                end_bitposition += context.bitsizeof(value_array_traits, end_bitposition, self._value)

                return end_bitposition

            def write_packed(self, context_iterator, writer):
                context = next(context_iterator)
                context.write(value_array_traits, writer, self._value)

            def read_packed(self, context_iterator, reader):
                context = next(context_iterator)
                self._value = context.read(value_array_traits, reader)

        class DummyObjectBitsizeCalculator:
            @staticmethod
            def calc_max_delta_bits(array_values):
                plain_values = [obj.value for obj in array_values]
                return PackedArrayTest.BitsizeCalculator.calc_max_delta_bits(plain_values)

            @staticmethod
            def calc_bitsize(_array_traits, array_values, signed_max_delta_bits):
                # array_traits are not usable here, we need array traits for the underlying value
                plain_values = [obj.value for obj in array_values]
                return PackedArrayTest.BitsizeCalculator.calc_bitsize(
                    value_array_traits, plain_values, signed_max_delta_bits)

            @staticmethod
            def calc_aligned_bitsize(_array_traits, bitposition, array_values, signed_max_delta_bits):
                # array_traits are not usable here, we need array traits for the underlying value
                plain_values = [obj.value for obj in array_values]
                return PackedArrayTest.BitsizeCalculator.calc_aligned_bitsize(
                    value_array_traits, bitposition, plain_values, signed_max_delta_bits)

        array_traits = None # not used
        packed_array_traits = ObjectPackedArrayTraits(DummyObject.create_packed,
                                                      DummyObject.create_packing_context)
        array1_values = [DummyObject(1), DummyObject(2)]
        array2_values = [DummyObject(3), DummyObject(4)]
        self._test_array(array_traits, packed_array_traits, array1_values, array2_values,
                         DummyObjectBitsizeCalculator)

    @staticmethod
    def _set_offset_method(_index, _bitoffset):
        pass

    @staticmethod
    def _check_offset_method(_index, _bitoffset):
        pass

    def _test_array(self, array_traits, packed_array_traits, array1_values, array2_values,
                    bitsize_calculator=BitsizeCalculator):
        self._test_from_reader(packed_array_traits, array1_values)
        self._test_from_reader(packed_array_traits, array2_values)

        self._test_eq(packed_array_traits, array1_values, array2_values)

        self._test_hashcode(packed_array_traits, array1_values, array2_values)

        self._test_len(packed_array_traits, array1_values)
        self._test_len(packed_array_traits, array2_values)

        self._test_get_item(packed_array_traits, array1_values)
        self._test_get_item(packed_array_traits, array2_values)

        self._test_set_item(packed_array_traits, array1_values)
        self._test_set_item(packed_array_traits, array2_values)

        self._test_raw_array(packed_array_traits, array1_values, array2_values)

        self._test_bitsizeof(array_traits, packed_array_traits, array1_values, bitsize_calculator)
        self._test_bitsizeof(array_traits, packed_array_traits, array2_values, bitsize_calculator)

        self._test_initialize_offsets(array_traits, packed_array_traits, array1_values, bitsize_calculator)
        self._test_initialize_offsets(array_traits, packed_array_traits, array2_values, bitsize_calculator)

        self._test_read(packed_array_traits, array1_values)
        self._test_read(packed_array_traits, array2_values)

        self._test_write(array_traits, packed_array_traits, array1_values, bitsize_calculator)
        self._test_write(array_traits, packed_array_traits, array2_values, bitsize_calculator)

    def _test_from_reader(self, packed_array_traits, array_values):
        array = PackedArray(packed_array_traits, array_values)
        writer = BitStreamWriter()
        array.write(writer)
        reader = BitStreamReader(writer.byte_array)
        read_array = PackedArray.from_reader(packed_array_traits, reader, len(array_values))
        self.assertEqual(array, read_array)

    def _test_eq(self, packed_array_traits, array1_values, array2_values):
        array1 = PackedArray(packed_array_traits, array1_values)
        array2 = PackedArray(packed_array_traits, array2_values)
        array3 = PackedArray(packed_array_traits, array1_values)
        self.assertNotEqual(array1, None)
        self.assertNotEqual(array1, array2)
        self.assertEqual(array1, array3)

    def _test_hashcode(self, packed_array_traits, array1_values, array2_values):
        array1 = PackedArray(packed_array_traits, array1_values)
        array2 = PackedArray(packed_array_traits, array2_values)
        array3 = PackedArray(packed_array_traits, array1_values)
        self.assertNotEqual(hash(array1), hash(array2))
        self.assertEqual(hash(array1), hash(array3))

    def _test_len(self, packed_array_traits, array_values):
        array = PackedArray(packed_array_traits, array_values)
        raw_array = array.raw_array
        self.assertEqual(len(raw_array), len(array))

    def _test_get_item(self, packed_array_traits, array_values):
        array = PackedArray(packed_array_traits, array_values)
        raw_array = array.raw_array
        for value, raw_value in zip(array, raw_array):
            self.assertEqual(value, raw_value)

    def _test_set_item(self, packed_array_traits, array_values):
        array = PackedArray(packed_array_traits, array_values)
        if len(array) > 1:
            raw_array = array.raw_array
            first_value = array[0]
            second_value = array[1]
            array[0] = second_value
            self.assertEqual(array[0], raw_array[0])
            raw_array[0] = first_value # return the original value for other tests
            self.assertEqual(array[0], raw_array[0])

    def _test_raw_array(self, packed_array_traits, array1_values, array2_values):
        array1 = PackedArray(packed_array_traits, array1_values)
        array2 = PackedArray(packed_array_traits, array2_values)
        array3 = PackedArray(packed_array_traits, array1_values)
        self.assertNotEqual(array1.raw_array, array2.raw_array)
        self.assertEqual(array1.raw_array, array3.raw_array)

    def _test_bitsizeof(self, array_traits, packed_array_traits, array_values, bitsize_calculator):
        signed_max_delta_bits = bitsize_calculator.calc_max_delta_bits(array_values)
        expected_bitsize = bitsize_calculator.calc_bitsize(array_traits, array_values, signed_max_delta_bits)

        array = PackedArray(packed_array_traits, array_values)
        self.assertEqual(expected_bitsize, array.bitsizeof(0))
        self.assertEqual(expected_bitsize, array.bitsizeof(7))

        auto_array = PackedArray(packed_array_traits, array_values, is_auto=True)
        self.assertEqual(bitsizeof_varuint64(len(array_values)) + expected_bitsize, auto_array.bitsizeof(0))
        self.assertEqual(bitsizeof_varuint64(len(array_values)) + expected_bitsize, auto_array.bitsizeof(7))

        aligned_array = PackedArray(packed_array_traits, array_values,
                                    set_offset_method=PackedArrayTest._set_offset_method,
                                    check_offset_method=PackedArrayTest._check_offset_method)

        for bitposition in [0, 1, 3, 5, 7]:
            expected_aligned_bitsize = bitsize_calculator.calc_aligned_bitsize(
                array_traits, bitposition, array_values, signed_max_delta_bits)
            self.assertEqual(expected_aligned_bitsize, aligned_array.bitsizeof(bitposition),
                             f"bitposition = {bitposition}")

    def _test_initialize_offsets(self, array_traits, packed_array_traits, array_values, bitsize_calculator):
        signed_max_delta_bits = bitsize_calculator.calc_max_delta_bits(array_values)
        expected_bitsize = bitsize_calculator.calc_bitsize(array_traits, array_values, signed_max_delta_bits)

        array = PackedArray(packed_array_traits, array_values)
        self.assertEqual(0 + expected_bitsize, array.initialize_offsets(0))
        self.assertEqual(7 + expected_bitsize, array.initialize_offsets(7))

        auto_array = PackedArray(packed_array_traits, array_values, is_auto=True)
        self.assertEqual(0 + bitsizeof_varuint64(len(array_values)) + expected_bitsize,
                         auto_array.initialize_offsets(0))
        self.assertEqual(7 + bitsizeof_varuint64(len(array_values)) + expected_bitsize,
                         auto_array.initialize_offsets(7))

        aligned_array = PackedArray(packed_array_traits, array_values,
                                    set_offset_method=PackedArrayTest._set_offset_method,
                                    check_offset_method=PackedArrayTest._check_offset_method)

        for bitposition in [0, 1, 3, 5, 7]:
            expected_aligned_bitsize = bitsize_calculator.calc_aligned_bitsize(
                array_traits, bitposition, array_values, signed_max_delta_bits)
            self.assertEqual(bitposition + expected_aligned_bitsize,
                             aligned_array.initialize_offsets(bitposition),
                             f"bitposition = {bitposition}")

    def _test_read(self, packed_array_traits, array_values):
        array = PackedArray(packed_array_traits, array_values)
        writer = BitStreamWriter()
        array.write(writer)
        reader = BitStreamReader(writer.byte_array)
        read_array = PackedArray(packed_array_traits)
        read_array.read(reader, len(array.raw_array))
        self.assertEqual(array, read_array)

        auto_array = PackedArray(packed_array_traits, array_values, is_auto=True)
        writer = BitStreamWriter()
        auto_array.write(writer)
        reader = BitStreamReader(writer.byte_array)
        read_auto_array = PackedArray(packed_array_traits, is_auto=True)
        read_auto_array.read(reader, len(auto_array.raw_array))
        self.assertEqual(auto_array, read_auto_array)

        aligned_array = PackedArray(packed_array_traits, array_values,
                                    set_offset_method=PackedArrayTest._set_offset_method,
                                    check_offset_method=PackedArrayTest._check_offset_method)
        writer = BitStreamWriter()
        aligned_array.write(writer)
        reader = BitStreamReader(writer.byte_array)
        read_aligned_array = PackedArray(packed_array_traits,
                                         set_offset_method=PackedArrayTest._set_offset_method,
                                         check_offset_method=PackedArrayTest._check_offset_method)
        read_aligned_array.read(reader, len(aligned_array.raw_array))
        self.assertEqual(aligned_array, read_aligned_array)

    def _test_write(self, array_traits, packed_array_traits, array_values, bitsize_calculator):
        signed_max_delta_bits = bitsize_calculator.calc_max_delta_bits(array_values)
        expected_bitsize = bitsize_calculator.calc_bitsize(array_traits, array_values, signed_max_delta_bits)

        array = PackedArray(packed_array_traits, array_values)
        writer = BitStreamWriter()
        array.write(writer)
        self.assertEqual(expected_bitsize, writer.bitposition)

        auto_array = PackedArray(packed_array_traits, array_values, is_auto=True)
        writer = BitStreamWriter()
        auto_array.write(writer)
        self.assertEqual(bitsizeof_varuint64(len(array_values)) + expected_bitsize, writer.bitposition)

        aligned_array = PackedArray(packed_array_traits, array_values,
                                    set_offset_method=PackedArrayTest._set_offset_method,
                                    check_offset_method=PackedArrayTest._check_offset_method)
        for start_bitposition in [0, 1, 3, 5, 7]:
            writer = BitStreamWriter()
            if start_bitposition:
                writer.write_bits(0, start_bitposition)
            expected_aligned_bitsize = bitsize_calculator.calc_aligned_bitsize(
                array_traits, start_bitposition, array_values, signed_max_delta_bits)
            aligned_array.write(writer)
            self.assertEqual(start_bitposition + expected_aligned_bitsize, writer.bitposition,
                             f"start_bitposition={start_bitposition}")
