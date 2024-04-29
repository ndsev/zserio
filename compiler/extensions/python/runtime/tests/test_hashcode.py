import enum
import unittest

from zserio.array import Array, SignedBitFieldArrayTraits
from zserio.bitbuffer import BitBuffer
from zserio.hashcode import (
    HASH_SEED,
    HASH_PRIME_NUMBER,
    calc_hashcode_bool,
    calc_hashcode_int32,
    calc_hashcode_int64,
    calc_hashcode_float32,
    calc_hashcode_float64,
    calc_hashcode_bytes,
    calc_hashcode_string,
    calc_hashcode_object,
    calc_hashcode_bool_array,
    calc_hashcode_int_array,
    calc_hashcode_float32_array,
    calc_hashcode_float64_array,
    calc_hashcode_bytes_array,
    calc_hashcode_string_array,
    calc_hashcode_object_array,
)
from zserio.float import float_to_uint32, float_to_uint64


class Color(enum.Enum):
    NONE = 0
    RED = 2
    BLUE = 3
    BLACK = 7

    def __hash__(self):
        result = HASH_SEED
        result = calc_hashcode_int32(result, self.value)
        return result


class Permissions:
    def __init__(self, value):
        self._value = value

    def __hash__(self):
        result = HASH_SEED
        result = calc_hashcode_int32(result, self.value)
        return result

    @property
    def value(self):
        return self._value

    class Values:
        READ = None
        WRITE = None
        CREATE = None


Permissions.Values.READ = Permissions(1)
Permissions.Values.WRITE = Permissions(2)
Permissions.Values.CREATE = Permissions(4)


class DummyObject:
    def __init__(self, hash_code):
        self._hash_code = hash_code

    def __hash__(self):
        return self._hash_code


class HashCodeTest(unittest.TestCase):

    def test_bool_type(self):
        hash_seed = 1
        bool_value = True
        self.assertEqual(HASH_PRIME_NUMBER + 1, calc_hashcode_bool(hash_seed, bool_value))

        self.assertEqual(HASH_PRIME_NUMBER, calc_hashcode_bool(hash_seed, None))

    def test_int_type(self):
        hash_seed = 1
        int_value = 10
        self.assertEqual(HASH_PRIME_NUMBER + 10, calc_hashcode_int32(hash_seed, int_value))

        int_value = -1
        self.assertEqual(HASH_PRIME_NUMBER - 1, calc_hashcode_int32(hash_seed, int_value))
        self.assertEqual(HASH_PRIME_NUMBER, calc_hashcode_int64(hash_seed, int_value))

        self.assertEqual(HASH_PRIME_NUMBER, calc_hashcode_int32(hash_seed, None))
        self.assertEqual(HASH_PRIME_NUMBER, calc_hashcode_int64(hash_seed, None))

    def test_float32_type(self):
        hash_seed = 1
        float_value = 10.0
        self.assertEqual(
            HASH_PRIME_NUMBER + float_to_uint32(float_value),
            calc_hashcode_float32(hash_seed, float_value),
        )

        self.assertEqual(HASH_PRIME_NUMBER, calc_hashcode_float32(hash_seed, None))

    def test_float64_type(self):
        hash_seed = 1
        float_value = 10.0
        uint64_value = float_to_uint64(float_value)
        expected_hash_code = (
            HASH_PRIME_NUMBER + ((uint64_value & 0xFFFFFFFF) ^ ((uint64_value & 0xFFFFFFFFFFFFFFFF) >> 32))
        ) & 0xFFFFFFFF
        self.assertEqual(expected_hash_code, calc_hashcode_float64(hash_seed, float_value))

        self.assertEqual(HASH_PRIME_NUMBER, calc_hashcode_float64(hash_seed, None))

    def test_bytes_type(self):
        hash_seed = 1
        bytes_value = bytearray([1])
        self.assertEqual(HASH_PRIME_NUMBER + 1, calc_hashcode_bytes(hash_seed, bytes_value))

        self.assertEqual(HASH_PRIME_NUMBER, calc_hashcode_bytes(hash_seed, None))

    def test_string_type(self):
        hash_seed = 1
        string_value = "0"
        self.assertEqual(HASH_PRIME_NUMBER + ord("0"), calc_hashcode_string(hash_seed, string_value))

        self.assertEqual(HASH_PRIME_NUMBER, calc_hashcode_string(hash_seed, None))

    def test_bitbuffer_type(self):
        hash_seed = 1
        bitbuffer_value = BitBuffer(bytes())
        self.assertEqual(
            HASH_PRIME_NUMBER + HASH_SEED,
            calc_hashcode_object(hash_seed, bitbuffer_value),
        )

    def test_enum_type(self):
        hash_seed = 1
        self.assertEqual(
            HASH_PRIME_NUMBER + (HASH_PRIME_NUMBER * HASH_SEED + Color.NONE.value),
            calc_hashcode_object(hash_seed, Color.NONE),
        )
        self.assertEqual(
            HASH_PRIME_NUMBER + (HASH_PRIME_NUMBER * HASH_SEED + Color.RED.value),
            calc_hashcode_object(hash_seed, Color.RED),
        )
        self.assertEqual(
            HASH_PRIME_NUMBER + (HASH_PRIME_NUMBER * HASH_SEED + Color.BLUE.value),
            calc_hashcode_object(hash_seed, Color.BLUE),
        )
        self.assertEqual(
            HASH_PRIME_NUMBER + (HASH_PRIME_NUMBER * HASH_SEED + Color.BLACK.value),
            calc_hashcode_object(hash_seed, Color.BLACK),
        )

    def test_bitmask_type(self):
        hash_seed = 1
        self.assertEqual(
            HASH_PRIME_NUMBER + (HASH_PRIME_NUMBER * HASH_SEED + Permissions.Values.READ.value),
            calc_hashcode_object(hash_seed, Permissions.Values.READ),
        )
        self.assertEqual(
            HASH_PRIME_NUMBER + (HASH_PRIME_NUMBER * HASH_SEED + Permissions.Values.WRITE.value),
            calc_hashcode_object(hash_seed, Permissions.Values.WRITE),
        )
        self.assertEqual(
            HASH_PRIME_NUMBER + (HASH_PRIME_NUMBER * HASH_SEED + Permissions.Values.CREATE.value),
            calc_hashcode_object(hash_seed, Permissions.Values.CREATE),
        )

    def test_object_type(self):
        hash_seed = 1
        object_value = DummyObject(10)
        self.assertEqual(HASH_PRIME_NUMBER + 10, calc_hashcode_object(hash_seed, object_value))

        self.assertEqual(HASH_PRIME_NUMBER, calc_hashcode_object(hash_seed, None))

    def test_array_type(self):
        hash_seed = 1
        array_value = Array(SignedBitFieldArrayTraits(32), [3, 7])

        raw_array_hash_code = (HASH_PRIME_NUMBER * HASH_SEED + 3) * HASH_PRIME_NUMBER + 7
        self.assertEqual(
            HASH_PRIME_NUMBER + raw_array_hash_code,
            calc_hashcode_object(hash_seed, array_value),
        )

    def test_bool_array_type(self):
        hash_seed = 1
        bool_array_value = [False, True]
        self.assertEqual(
            (HASH_PRIME_NUMBER + 0) * HASH_PRIME_NUMBER + 1,
            calc_hashcode_bool_array(hash_seed, bool_array_value),
        )

        self.assertEqual(HASH_PRIME_NUMBER, calc_hashcode_bool_array(hash_seed, None))

    def test_int_array_type(self):
        hash_seed = 1
        int_array_value = [3, 7]
        self.assertEqual(
            (HASH_PRIME_NUMBER + 3) * HASH_PRIME_NUMBER + 7,
            calc_hashcode_int_array(hash_seed, int_array_value),
        )

        self.assertEqual(HASH_PRIME_NUMBER, calc_hashcode_int_array(hash_seed, None))

    def test_float32_array_type(self):
        hash_seed = 1
        float32_array_value = [10.0]
        self.assertEqual(
            HASH_PRIME_NUMBER + float_to_uint32(float32_array_value[0]),
            calc_hashcode_float32_array(hash_seed, float32_array_value),
        )

        self.assertEqual(HASH_PRIME_NUMBER, calc_hashcode_float32_array(hash_seed, None))

    def test_float64_array_type(self):
        hash_seed = 1
        float64_array_value = [10.0]
        uint64_value = float_to_uint64(float64_array_value[0])
        expected_hash_code = (
            HASH_PRIME_NUMBER + ((uint64_value & 0xFFFFFFFF) ^ ((uint64_value & 0xFFFFFFFFFFFFFFFF) >> 32))
        ) & 0xFFFFFFFF
        self.assertEqual(
            expected_hash_code,
            calc_hashcode_float64_array(hash_seed, float64_array_value),
        )

        self.assertEqual(HASH_PRIME_NUMBER, calc_hashcode_float64_array(hash_seed, None))

    def test_bytes_array_type(self):
        hash_seed = 1
        bytes_value = [bytearray([1])]
        self.assertEqual(HASH_PRIME_NUMBER + 1, calc_hashcode_bytes_array(hash_seed, bytes_value))

        self.assertEqual(HASH_PRIME_NUMBER, calc_hashcode_bytes_array(hash_seed, None))

    def test_str_array_type(self):
        hash_seed = 1
        str_array_value = ["0"]
        self.assertEqual(
            HASH_PRIME_NUMBER + ord(str_array_value[0]),
            calc_hashcode_string_array(hash_seed, str_array_value),
        )

        self.assertEqual(HASH_PRIME_NUMBER, calc_hashcode_string_array(hash_seed, None))

    def test_bitbuffer_array_type(self):
        hash_seed = 1
        bitbuffer_array_value = [BitBuffer(bytes())]
        self.assertEqual(
            HASH_PRIME_NUMBER + HASH_SEED,
            calc_hashcode_object_array(hash_seed, bitbuffer_array_value),
        )

    def test_enum_array_type(self):
        hash_seed = 1
        enum_array_value = [Color.NONE]
        self.assertEqual(
            HASH_PRIME_NUMBER + (HASH_PRIME_NUMBER * HASH_SEED + Color.NONE.value),
            calc_hashcode_object_array(hash_seed, enum_array_value),
        )

    def test_bitmask_array_type(self):
        hash_seed = 1
        bitmask_array_value = [Permissions.Values.READ]
        self.assertEqual(
            HASH_PRIME_NUMBER + (HASH_PRIME_NUMBER * HASH_SEED + Permissions.Values.READ.value),
            calc_hashcode_object_array(hash_seed, bitmask_array_value),
        )

    def test_object_array_type(self):
        hash_seed = 1
        object_array_value = [DummyObject(3), DummyObject(7)]
        self.assertEqual(
            (HASH_PRIME_NUMBER + 3) * HASH_PRIME_NUMBER + 7,
            calc_hashcode_object_array(hash_seed, object_array_value),
        )

        self.assertEqual(HASH_PRIME_NUMBER, calc_hashcode_object_array(hash_seed, None))
