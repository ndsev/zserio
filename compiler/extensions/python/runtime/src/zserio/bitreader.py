"""
The module implements abstraction for reading data to the bit stream.
"""

import typing

from zserio.bitbuffer import BitBuffer
from zserio.limits import INT64_MIN
from zserio.exception import PythonRuntimeException
from zserio.float import uint16_to_float, uint32_to_float, uint64_to_float
from zserio.cppbind import import_cpp_class


class BitStreamReader:
    """
    Bit stream reader.
    """

    def __init__(self, buffer: bytes, bitsize: typing.Optional[int] = None) -> None:
        """
        Constructs bit stream reader from bytes buffer.

        Because bit buffer size does not have to be byte aligned (divisible by 8), it's possible that not all
        bits of the last byte are used. In this case, only most significant bits of the corresponded size are
        used.

        :param buffer: Bytes-like buffer to read as a bit stream.
        :param bitsize: Number of bits stored in buffer to use.
        :raises PythonRuntimeException: If bitsize is out of range.
        """

        if bitsize is None:
            bitsize = len(buffer) * 8
        elif len(buffer) * 8 < bitsize:
            raise PythonRuntimeException(
                f"BitStreamReader: Bit size '{bitsize}' out of range "
                f"for the given buffer byte size '{len(buffer)}'!"
            )

        self._buffer: bytes = buffer
        self._bitsize: int = bitsize
        self._bitposition: int = 0

    @classmethod
    def from_bitbuffer(cls: typing.Type["BitStreamReader"], bitbuffer: BitBuffer) -> "BitStreamReader":
        """
        Constructs bit stream reader from bit buffer.

        :param bitbuffer: Bit buffer to read as a bit stream.
        """

        instance = cls(bitbuffer.buffer, bitbuffer.bitsize)

        return instance

    @classmethod
    def from_file(cls: typing.Type["BitStreamReader"], filename: str) -> "BitStreamReader":
        """
        Constructs bit stream reader from file.

        :param filename: Filename to read as a bit stream.
        """

        with open(filename, "rb") as file:
            return cls(file.read())

    def read_bits(self, numbits: int) -> int:
        """
        Reads given number of bits from the bit stream as an unsigned integer.

        :param numbits: Number of bits to read.
        :returns: Read bits as an unsigned integer.
        :raises PythonRuntimeException: If the numbits is invalid number of the reading goes behind the stream.
        """

        if numbits < 1 or numbits > 64:
            raise PythonRuntimeException("BitStreamReader: numbits '{numbits}' not in range [1,64]!")

        return self.read_bits_unchecked(numbits)

    def read_signed_bits(self, numbits: int) -> int:
        """
        Reads given number of bits from the bit stream as a signed integer.

        :param numbits: Number of bits to read
        :returns: Read bits as a signed integer.
        :raises PythonRuntimeException: If the numbits is invalid number of the reading goes behind the stream.
        """

        if numbits < 1 or numbits > 64:
            raise PythonRuntimeException(f"BitStreamReader: numbits '{numbits}' not in range [1,64]!")

        return self.read_signed_bits_unchecked(numbits)

    def read_bits_unchecked(self, numbits: int) -> int:
        """
        Reads given number of bits from the bit stream as an unsigned integer.

        This method does not check that numbits >= 0 and assumes that it's ensured by the caller.

        :param numbits: Number of bits to read.
        :returns: Read bits as an unsigned integer.
        :raises PythonRuntimeException: If the numbits is invalid number of the reading goes behind the stream.
        """

        end_bitposition = self._bitposition + numbits

        if end_bitposition > self._bitsize:
            raise PythonRuntimeException("BitStreamReader: Reading behind the stream!")

        start_byte = self._bitposition // 8
        end_byte = (end_bitposition - 1) // 8

        value = int.from_bytes(self._buffer[start_byte : end_byte + 1], byteorder="big", signed=False)

        last_bits = end_bitposition % 8
        if last_bits != 0:
            value >>= 8 - last_bits
        value &= (1 << numbits) - 1

        self._bitposition = end_bitposition

        return value

    def read_signed_bits_unchecked(self, numbits: int) -> int:
        """
        Reads given number of bits from the bit stream as a signed integer.

        This method does not check that numbits >= 0 and assumes that it's ensured by the caller.

        :param numbits: Number of bits to read
        :returns: Read bits as a signed integer.
        :raises PythonRuntimeException: If the numbits is invalid number of the reading goes behind the stream.
        """

        value = self.read_bits_unchecked(numbits)

        if numbits != 0 and (value >> (numbits - 1)) != 0:
            # signed
            return value - (1 << numbits)
        else:
            # unsigned
            return value

    def read_varint16(self) -> int:
        """
        Reads variable 16-bit signed integer value from the bit stream.

        :returns: Variable 16-bit signed integer value.
        """

        byte = self.read_bits_unchecked(8)  # byte 1
        sign = byte & VARINT_SIGN_1
        result = byte & VARINT_BYTE_1
        if byte & VARINT_HAS_NEXT_1 == 0:
            return -result if sign != 0 else result

        result = (result << 8) | self.read_bits_unchecked(8)  # byte 2
        return -result if sign else result

    def read_varint32(self) -> int:
        """
        Reads variable 32-bit signed integer value from the bit stream.

        :returns: Variable 32-bit signed integer value.
        """

        byte = self.read_bits_unchecked(8)  # byte 1
        sign = byte & VARINT_SIGN_1
        result = byte & VARINT_BYTE_1
        if byte & VARINT_HAS_NEXT_1 == 0:
            return -result if sign else result

        byte = self.read_bits_unchecked(8)  # byte 2
        result = result << 7 | (byte & VARINT_BYTE_N)
        if byte & VARINT_HAS_NEXT_N == 0:
            return -result if sign else result

        byte = self.read_bits_unchecked(8)  # byte 3
        result = result << 7 | (byte & VARINT_BYTE_N)
        if byte & VARINT_HAS_NEXT_N == 0:
            return -result if sign else result

        result = result << 8 | self.read_bits_unchecked(8)  # byte 4
        return -result if sign else result

    def read_varint64(self) -> int:
        """
        Reads variable 64-bit signed integer value from the bit stream.

        :returns: Variable 64-bit signed integer value.
        """

        byte = self.read_bits_unchecked(8)  # byte 1
        sign = byte & VARINT_SIGN_1
        result = byte & VARINT_BYTE_1
        if byte & VARINT_HAS_NEXT_1 == 0:
            return -result if sign else result

        byte = self.read_bits_unchecked(8)  # byte 2
        result = result << 7 | (byte & VARINT_BYTE_N)
        if byte & VARINT_HAS_NEXT_N == 0:
            return -result if sign else result

        byte = self.read_bits_unchecked(8)  # byte 3
        result = result << 7 | (byte & VARINT_BYTE_N)
        if byte & VARINT_HAS_NEXT_N == 0:
            return -result if sign else result

        byte = self.read_bits_unchecked(8)  # byte 4
        result = result << 7 | (byte & VARINT_BYTE_N)
        if byte & VARINT_HAS_NEXT_N == 0:
            return -result if sign else result

        byte = self.read_bits_unchecked(8)  # byte 5
        result = result << 7 | (byte & VARINT_BYTE_N)
        if byte & VARINT_HAS_NEXT_N == 0:
            return -result if sign else result

        byte = self.read_bits_unchecked(8)  # byte 6
        result = result << 7 | (byte & VARINT_BYTE_N)
        if byte & VARINT_HAS_NEXT_N == 0:
            return -result if sign else result

        byte = self.read_bits_unchecked(8)  # byte 7
        result = result << 7 | (byte & VARINT_BYTE_N)
        if byte & VARINT_HAS_NEXT_N == 0:
            return -result if sign else result

        result = result << 8 | self.read_bits_unchecked(8)  # byte 8
        return -result if sign else result

    def read_varint(self) -> int:
        """
        Reads variable signed integer value (up to 9 bytes) from the bit stream.

        :returns: Variable signed integer value (up to 9 bytes).
        """

        byte = self.read_bits_unchecked(8)  # byte 1
        sign = byte & VARINT_SIGN_1
        result = byte & VARINT_BYTE_1
        if byte & VARINT_HAS_NEXT_1 == 0:
            return (INT64_MIN if result == 0 else -result) if sign else result

        byte = self.read_bits_unchecked(8)  # byte 2
        result = result << 7 | (byte & VARINT_BYTE_N)
        if byte & VARINT_HAS_NEXT_N == 0:
            return -result if sign else result

        byte = self.read_bits_unchecked(8)  # byte 3
        result = result << 7 | (byte & VARINT_BYTE_N)
        if byte & VARINT_HAS_NEXT_N == 0:
            return -result if sign else result

        byte = self.read_bits_unchecked(8)  # byte 4
        result = result << 7 | (byte & VARINT_BYTE_N)
        if byte & VARINT_HAS_NEXT_N == 0:
            return -result if sign else result

        byte = self.read_bits_unchecked(8)  # byte 5
        result = result << 7 | (byte & VARINT_BYTE_N)
        if byte & VARINT_HAS_NEXT_N == 0:
            return -result if sign else result

        byte = self.read_bits_unchecked(8)  # byte 6
        result = result << 7 | (byte & VARINT_BYTE_N)
        if byte & VARINT_HAS_NEXT_N == 0:
            return -result if sign else result

        byte = self.read_bits_unchecked(8)  # byte 7
        result = result << 7 | (byte & VARINT_BYTE_N)
        if byte & VARINT_HAS_NEXT_N == 0:
            return -result if sign else result

        byte = self.read_bits_unchecked(8)  # byte 8
        result = result << 7 | (byte & VARINT_BYTE_N)
        if byte & VARINT_HAS_NEXT_N == 0:
            return -result if sign else result

        result = result << 8 | self.read_bits_unchecked(8)  # byte 9
        return -result if sign else result

    def read_varuint16(self) -> int:
        """
        Reads variable 16-bit unsigned integer value from the bit stream.

        :returns: Variable 16-bit unsigned integer value.
        """

        byte = self.read_bits_unchecked(8)  # byte 1
        result = byte & VARUINT_BYTE
        if byte & VARUINT_HAS_NEXT == 0:
            return result

        result = result << 8 | self.read_bits_unchecked(8)  # byte 2
        return result

    def read_varuint32(self) -> int:
        """
        Reads variable 32-bit unsigned integer value from the bit stream.

        :returns: Variable 32-bit unsigned integer value.
        """

        byte = self.read_bits_unchecked(8)  # byte 1
        result = byte & VARUINT_BYTE
        if byte & VARUINT_HAS_NEXT == 0:
            return result

        byte = self.read_bits_unchecked(8)  # byte 2
        result = result << 7 | (byte & VARUINT_BYTE)
        if byte & VARUINT_HAS_NEXT == 0:
            return result

        byte = self.read_bits_unchecked(8)  # byte 3
        result = result << 7 | (byte & VARUINT_BYTE)
        if byte & VARUINT_HAS_NEXT == 0:
            return result

        result = result << 8 | self.read_bits_unchecked(8)  # byte 4
        return result

    def read_varuint64(self) -> int:
        """
        Reads variable 64-bit unsigned integer value from the bit stream.

        :returns: Variable 64-bit unsigned integer value.
        """

        byte = self.read_bits_unchecked(8)  # byte 1
        result = byte & VARUINT_BYTE
        if byte & VARUINT_HAS_NEXT == 0:
            return result

        byte = self.read_bits_unchecked(8)  # byte 2
        result = result << 7 | (byte & VARUINT_BYTE)
        if byte & VARUINT_HAS_NEXT == 0:
            return result

        byte = self.read_bits_unchecked(8)  # byte 3
        result = result << 7 | (byte & VARUINT_BYTE)
        if byte & VARUINT_HAS_NEXT == 0:
            return result

        byte = self.read_bits_unchecked(8)  # byte 4
        result = result << 7 | (byte & VARUINT_BYTE)
        if byte & VARUINT_HAS_NEXT == 0:
            return result

        byte = self.read_bits_unchecked(8)  # byte 5
        result = result << 7 | (byte & VARUINT_BYTE)
        if byte & VARUINT_HAS_NEXT == 0:
            return result

        byte = self.read_bits_unchecked(8)  # byte 6
        result = result << 7 | (byte & VARUINT_BYTE)
        if byte & VARUINT_HAS_NEXT == 0:
            return result

        byte = self.read_bits_unchecked(8)  # byte 7
        result = result << 7 | (byte & VARUINT_BYTE)
        if byte & VARUINT_HAS_NEXT == 0:
            return result

        result = result << 8 | self.read_bits_unchecked(8)  # byte 8
        return result

    def read_varuint(self) -> int:
        """
        Reads variable unsigned integer value (up to 9 bytes) from the bit stream.

        :returns: Variable unsigned integer value (up to 9 bytes).
        """

        byte = self.read_bits_unchecked(8)  # byte 1
        result = byte & VARUINT_BYTE
        if byte & VARUINT_HAS_NEXT == 0:
            return result

        byte = self.read_bits_unchecked(8)  # byte 2
        result = result << 7 | (byte & VARUINT_BYTE)
        if byte & VARUINT_HAS_NEXT == 0:
            return result

        byte = self.read_bits_unchecked(8)  # byte 3
        result = result << 7 | (byte & VARUINT_BYTE)
        if byte & VARUINT_HAS_NEXT == 0:
            return result

        byte = self.read_bits_unchecked(8)  # byte 4
        result = result << 7 | (byte & VARUINT_BYTE)
        if byte & VARUINT_HAS_NEXT == 0:
            return result

        byte = self.read_bits_unchecked(8)  # byte 5
        result = result << 7 | (byte & VARUINT_BYTE)
        if byte & VARUINT_HAS_NEXT == 0:
            return result

        byte = self.read_bits_unchecked(8)  # byte 6
        result = result << 7 | (byte & VARUINT_BYTE)
        if byte & VARUINT_HAS_NEXT == 0:
            return result

        byte = self.read_bits_unchecked(8)  # byte 7
        result = result << 7 | (byte & VARUINT_BYTE)
        if byte & VARUINT_HAS_NEXT == 0:
            return result

        byte = self.read_bits_unchecked(8)  # byte 8
        result = result << 7 | (byte & VARUINT_BYTE)
        if byte & VARUINT_HAS_NEXT == 0:
            return result

        result = result << 8 | self.read_bits_unchecked(8)  # byte 9
        return result

    def read_varsize(self) -> int:
        """
        Reads variable size integer value from the bit stream.

        :returns: Variable size integer value.
        :raises PythonRuntimeException: If read variable size integer is out of range.
        """

        byte = self.read_bits_unchecked(8)  # byte 1
        result = byte & VARUINT_BYTE
        if byte & VARUINT_HAS_NEXT == 0:
            return result

        byte = self.read_bits_unchecked(8)  # byte 2
        result = result << 7 | (byte & VARUINT_BYTE)
        if byte & VARUINT_HAS_NEXT == 0:
            return result

        byte = self.read_bits_unchecked(8)  # byte 3
        result = result << 7 | (byte & VARUINT_BYTE)
        if byte & VARUINT_HAS_NEXT == 0:
            return result

        byte = self.read_bits_unchecked(8)  # byte 4
        result = result << 7 | (byte & VARUINT_BYTE)
        if byte & VARUINT_HAS_NEXT == 0:
            return result

        result = result << 8 | self.read_bits_unchecked(8)  # byte 5
        if result > VARSIZE_MAX_VALUE:
            raise PythonRuntimeException(
                f"BitStreamReader: Read value '{result}' is out of range " "for varsize type!"
            )

        return result

    def read_float16(self) -> float:
        """
        Read 16-bits from the stream as a float value encoded according to IEEE 754 binary16.

        :returns: Read float value.
        :raises PythonRuntimeException: If the reading goes behind the stream.
        """

        return uint16_to_float(self.read_bits_unchecked(16))

    def read_float32(self) -> float:
        """
        Read 32-bits from the stream as a float value encoded according to IEEE 754 binary32.

        :returns: Read float value.
        :raises PythonRuntimeException: If the reading goes behind the stream.
        """

        return uint32_to_float(self.read_bits_unchecked(32))

    def read_float64(self) -> float:
        """
        Read 64-bits from the stream as a float value encoded according to IEEE 754 binary64.

        :returns: Read float value.
        :raises PythonRuntimeException: If the reading goes behind the stream.
        """

        return uint64_to_float(self.read_bits_unchecked(64))

    def read_bytes(self) -> bytearray:
        """
        Reads bytes from the stream.

        :returns: Read bytes.
        :raises PythonRuntimeException: If the reading goes behind the stream.
        """

        length = self.read_varsize()
        begin_bitposition = self._bitposition

        if (begin_bitposition & 0x07) != 0:
            # we are not aligned to byte
            value = bytearray()
            for _ in range(length):
                value.append(self.read_bits_unchecked(8))
        else:
            # we are aligned to byte
            self.bitposition = begin_bitposition + length * 8
            value = bytearray(length)
            begin_byte_position = begin_bitposition // 8
            value[0:length] = self._buffer[begin_byte_position : begin_byte_position + length]

        return value

    def read_string(self) -> str:
        """
        Reads string from the stream.

        :returns: Read string.
        :raises PythonRuntimeException: If the reading goes behind the stream.
        """

        length = self.read_varsize()
        begin_bitposition = self._bitposition
        if (begin_bitposition & 0x07) != 0:
            # we are not aligned to byte
            value = bytearray()
            for _ in range(length):
                value.append(self.read_bits_unchecked(8))
        else:
            # we are aligned to byte
            self.bitposition = begin_bitposition + length * 8
            value = bytearray(length)
            begin_byte_position = begin_bitposition // 8
            value[0:length] = self._buffer[begin_byte_position : begin_byte_position + length]

        return value.decode("utf-8")

    def read_bool(self) -> bool:
        """
        Reads single bit as a bool value.

        :returns: Read bool values.
        :raises PythonRuntimeException: If the reading goes behind the stream.
        """

        return self.read_bits_unchecked(1) != 0

    def read_bitbuffer(self) -> BitBuffer:
        """
        Reads a bit buffer from the stream.

        :returns: Read bit buffer.
        :raises PythonRuntimeException: If the reading goes behind the stream.
        """

        bitsize = self.read_varsize()
        num_bytes_to_read = bitsize // 8
        num_rest_bits = bitsize - num_bytes_to_read * 8
        bytesize = (bitsize + 7) // 8
        read_buffer = bytearray(bytesize)
        begin_bitposition = self._bitposition
        if (begin_bitposition & 0x07) != 0:
            # we are not aligned to byte
            for i in range(num_bytes_to_read):
                read_buffer[i] = self.read_bits_unchecked(8)
        else:
            # we are aligned to byte
            self.bitposition = begin_bitposition + num_bytes_to_read * 8
            begin_byte_position = begin_bitposition // 8
            read_buffer[0:num_bytes_to_read] = self._buffer[
                begin_byte_position : begin_byte_position + num_bytes_to_read
            ]

        if num_rest_bits != 0:
            read_buffer[num_bytes_to_read] = self.read_bits(num_rest_bits) << (8 - num_rest_bits)

        return BitBuffer(read_buffer, bitsize)

    @property
    def bitposition(self) -> int:
        """
        Gets current bit position.

        :returns: Current bit position.
        """

        return self._bitposition

    @bitposition.setter
    def bitposition(self, bitposition: int) -> None:
        """
        Sets bit position.

        :param bitposition: New bit position.
        :raises PythonRuntimeException: If the position is not within the stream.
        """

        if bitposition < 0:
            raise PythonRuntimeException("BitStreamReader: Cannot set negative bit position!")
        if bitposition > self._bitsize:
            raise PythonRuntimeException("BitStreamReader: Setting bit position behind the stream!")

        self._bitposition = bitposition

    def alignto(self, alignment: int) -> None:
        """
        Aligns the bit position according to the aligning value.

        :param alignment: An aligning value to use.
        :raises PythonRuntimeException: If the aligning moves behind the stream."
        """

        offset = self._bitposition % alignment
        if offset != 0:
            self.bitposition = self._bitposition + alignment - offset

    @property
    def buffer_bitsize(self) -> int:
        """
        Gets size of the underlying buffer in bits.

        :returns: Buffer bit size.
        """

        return self._bitsize


VARINT_SIGN_1 = 0x80
VARINT_BYTE_1 = 0x3F
VARINT_BYTE_N = 0x7F
VARINT_HAS_NEXT_1 = 0x40
VARINT_HAS_NEXT_N = 0x80
VARUINT_BYTE = 0x7F
VARUINT_HAS_NEXT = 0x80
VARSIZE_MAX_VALUE = (1 << 31) - 1

_BitStreamReaderCpp = import_cpp_class("BitStreamReader")
if _BitStreamReaderCpp is not None:
    BitStreamReader = _BitStreamReaderCpp  # type: ignore

    def _bitstreamreader_fromfile(filename: str) -> "BitStreamReader":
        with open(filename, "rb") as file:
            return BitStreamReader(file.read())

    BitStreamReader.from_file = _bitstreamreader_fromfile
