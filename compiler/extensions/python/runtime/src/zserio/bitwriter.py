"""
The module implements abstraction for writing data to the bit stream.
"""

from zserio.bitbuffer import BitBuffer
from zserio.bitsizeof import (bitsizeof_varint16, bitsizeof_varint32,
                              bitsizeof_varint64, bitsizeof_varint,
                              bitsizeof_varuint16, bitsizeof_varuint32,
                              bitsizeof_varuint64, bitsizeof_varuint,
                              bitsizeof_varsize)
from zserio.exception import PythonRuntimeException
from zserio.float import float_to_uint16, float_to_uint32, float_to_uint64
from zserio.limits import INT64_MIN

class BitStreamWriter:
    """
    Bit stream writer using bytearray.
    """

    def __init__(self) -> None:
        """
        Constructor.
        """

        self._byte_array: bytearray = bytearray()
        self._bitposition: int = 0

    def write_bits(self, value: int, numbits: int) -> None:
        """
        Writes the given value with the given number of bits to the underlying storage.

        :param value: Value to write.
        :param numbits: Number of bits to write.
        :raises PythonRuntimeException: If the value is out of the range or if the number of bits is invalid.
        """

        if numbits <= 0:
            raise PythonRuntimeException(f"BitStreamWriter: numbits '{numbits}' is less than 1!")

        min_value = 0
        max_value = (1 << numbits) - 1
        if value < min_value or value > max_value:
            raise PythonRuntimeException(f"BitStreamWriter: Value '{value}' is out of the range "
                                         f"<{min_value},{max_value}>!")

        self._write_bits(value, numbits, signed=False)

    def write_signed_bits(self, value: int, numbits: int) -> None:
        """
        Writes the given signed value with the given number of bits to the underlying storage.
        Provided for convenience.

        :param value: Signed value to write.
        :param numbits: Number of bits to write.
        :raises PythonRuntimeException: If the value is out of the range or if the number of bits is invalid.
        """

        if numbits <= 0:
            raise PythonRuntimeException(f"BitStreamWriter: numbits '{numbits}' is less than 1!")

        min_value = -(1 << (numbits - 1))
        max_value = (1 << (numbits - 1)) - 1
        if value < min_value or value > max_value:
            raise PythonRuntimeException(f"BitStreamWriter: Value '{value}' is out of the range "
                                         f"<{min_value},{max_value}>!")

        self._write_bits(value, numbits, signed=True)

    def write_varint16(self, value: int) -> None:
        """
        Writes a variable 16-bit signed integer value to the underlying storage.

        :param value: Value to write.
        :raises PythonRuntimeException: If the value is out of the range.
        """

        self._write_varnum(value, 2, bitsizeof_varint16(value) // 8, is_signed=True)

    def write_varint32(self, value: int) -> None:
        """
        Writes a variable 32-bit signed integer value to the underlying storage.

        :param value: Value to write.
        :raises PythonRuntimeException: If the value is out of the range.
        """

        self._write_varnum(value, 4, bitsizeof_varint32(value) // 8, is_signed=True)

    def write_varint64(self, value: int) -> None:
        """
        Writes a variable 16-bit signed integer value to the underlying storage.

        :param value: Value to write.
        :raises PythonRuntimeException: If the value is out of the range.
        """

        self._write_varnum(value, 8, bitsizeof_varint64(value) // 8, is_signed=True)

    def write_varint(self, value: int) -> None:
        """
        Writes a variable signed integer value (up to 9 bytes) to the underlying storage.

        :param value: Value to write.
        :raises PythonRuntimeException: If the value is out of the range.
        """

        if value == INT64_MIN:
            self.write_bits(0x80, 8) # INT64_MIN is stored as -0
        else:
            self._write_varnum(value, 9, bitsizeof_varint(value) // 8, is_signed=True)

    def write_varuint16(self, value: int) -> None:
        """
        Writes a variable 16-bit unsigned integer value to the underlying storage.

        :param value: Value to write.
        :raises PythonRuntimeException: If the value is out of the range.
        """

        self._write_varnum(value, 2, bitsizeof_varuint16(value) // 8, is_signed=False)

    def write_varuint32(self, value: int) -> None:
        """
        Writes a variable 32-bit unsigned integer value to the underlying storage.

        :param value: Value to write.
        :raises PythonRuntimeException: If the value is out of the range.
        """

        self._write_varnum(value, 4, bitsizeof_varuint32(value) // 8, is_signed=False)

    def write_varuint64(self, value: int) -> None:
        """
        Writes a variable 16-bit unsigned integer value to the underlying storage.

        :param value: Value to write.
        :raises PythonRuntimeException: If the value is out of the range.
        """

        self._write_varnum(value, 8, bitsizeof_varuint64(value) // 8, is_signed=False)

    def write_varuint(self, value: int) -> None:
        """
        Writes a variable unsigned integer value (up to 9 bytes) to the underlying storage.

        :param value: Value to write.
        :raises PythonRuntimeException: If the value is out of the range.
        """

        self._write_varnum(value, 9, bitsizeof_varuint(value) // 8, is_signed=False)

    def write_varsize(self, value: int) -> None:
        """
        Writes a variable size integer value to the underlying storage.

        :param value: Value to write.
        :raises PythonRuntimeException: If the value is out of the range.
        """

        self._write_varnum(value, 5, bitsizeof_varsize(value) // 8, is_signed=False)

    def write_float16(self, value: float) -> None:
        """
        Writes a 16-bit float value to the underlying storage according to IEEE 754 binary16.

        :param value: Float value to write.
        """

        self.write_bits(float_to_uint16(value), 16)

    def write_float32(self, value: float) -> None:
        """
        Writes a 32-bit float value to the underlying storage according to IEEE 754 binary32.

        :param value: Float value to write.
        """

        self.write_bits(float_to_uint32(value), 32)

    def write_float64(self, value: float) -> None:
        """
        Writes a 64-bit float value to the underlying storage according to IEEE 754 binary64.

        :param value: Float value to write.
        """

        self.write_bits(float_to_uint64(value), 64)

    def write_string(self, string: str) -> None:
        """
        Writes the given string to the underlying storage in UTF-8 encoding. Length of the string is written
        as varuint64 at the beginning.

        :param string: String to write.
        """

        string_bytes = string.encode("utf-8")
        self.write_varsize(len(string_bytes))
        for string_byte in string_bytes:
            self.write_bits(string_byte, 8)

    def write_bool(self, value: bool) -> None:
        """
        Writes bool in a single bit.

        :param value: Bool value to write.
        """

        self.write_bits(1 if value else 0, 1)

    def write_bitbuffer(self, bitbuffer: BitBuffer) -> None:
        """
        Writes a bit buffer to the underlying storage. Length of the bit buffer is written as varuint64
        at the beginning.

        :param bitbuffer: Bit buffer to write.
        """

        bitsize = bitbuffer.bitsize
        self.write_varsize(bitsize)

        write_buffer = bitbuffer.buffer
        num_bytes_to_write = bitsize // 8
        num_rest_bits = bitsize - num_bytes_to_write * 8
        begin_bitposition = self._bitposition
        if (begin_bitposition & 0x07) != 0:
            # we are not aligned to byte
            for i in range(num_bytes_to_write):
                self.write_bits(write_buffer[i], 8)
        else:
            # we are aligned to byte
            self._byte_array += write_buffer[0:num_bytes_to_write]
            self._bitposition += num_bytes_to_write * 8

        if num_rest_bits > 0:
            self.write_bits(write_buffer[num_bytes_to_write] >> (8 - num_rest_bits), num_rest_bits)

    @property
    def byte_array(self) -> bytes:
        """
        Gets internal bytearray.

        :returns: Underlying bytearray object.
        """

        return self._byte_array

    def to_file(self, filename: str) -> None:
        """
        Writes underlying bytearray to binary file.

        :param filename: File to write.
        """

        with open(filename, "wb") as file:
            file.write(self._byte_array)

    @property
    def bitposition(self) -> int:
        """
        Gets current bit position.

        :returns: Current bit position.
        """

        return self._bitposition

    def alignto(self, alignment: int) -> None:
        """
        Aligns the bit position according to the aligning value.

        :param alignment: An aligning value to use.
        """

        offset = self._bitposition % alignment
        if offset != 0:
            self.write_bits(0, alignment - offset)

    def _write_bits(self, value: int, numbits: int, *, signed: bool) -> None:
        buffer_last_byte_bits = self._bitposition % 8
        buffer_free_bits = (8 - buffer_last_byte_bits) if buffer_last_byte_bits != 0 else 0
        value_first_byte_bits = numbits % 8 or 8
        if value_first_byte_bits <= buffer_free_bits:
            left_shift = buffer_free_bits - value_first_byte_bits
        else:
            left_shift = buffer_free_bits + 8 - value_first_byte_bits
        value <<= left_shift
        num_bytes = (numbits + left_shift + 7) // 8
        value_bytes = value.to_bytes(num_bytes, byteorder='big', signed=signed)
        if buffer_free_bits == 0:
            self._byte_array.extend(value_bytes)
        else:
            value_first_byte = value_bytes[0] & ((1 << buffer_free_bits) - 1)
            self._byte_array[-1] |= value_first_byte
            self._byte_array.extend(value_bytes[1:])

        self._bitposition += numbits

    def _write_varnum(self, value: int, max_var_bytes: int, num_var_bytes: int, *, is_signed: bool) -> None:
        abs_value = abs(value)
        has_max_byte_range = (num_var_bytes == max_var_bytes)
        for i in range(num_var_bytes):
            byte = 0x00
            numbits = 8
            has_next_byte = (i < num_var_bytes - 1)
            has_sign_bit = (is_signed and i == 0)
            if has_sign_bit:
                if value < 0:
                    byte |= 0x80
                numbits -= 1
            if has_next_byte:
                numbits -= 1
                byte |= (1 << numbits) # use bit 6 if signed bit is present, use bit 7 otherwise
            else: # this is the last byte
                if not has_max_byte_range: # next byte flag isn't used in last byte in case of max byte range
                    numbits -= 1

            shift_bits = (num_var_bytes - (i + 1)) * 7 + (1 if has_max_byte_range and has_next_byte else 0)
            byte |= (abs_value >> shift_bits) & VAR_NUM_BIT_MASKS[numbits - 1]
            self.write_bits(byte, 8)

VAR_NUM_BIT_MASKS = [0x01, 0x03, 0x07, 0x0f, 0x1f, 0x3f, 0x7f, 0xff]
