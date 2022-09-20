"""
The module implements abstraction for holding bit sequence.
"""

import typing

from zserio.exception import PythonRuntimeException
from zserio.hashcode import HASH_SEED
from zserio.hashcode import calc_hashcode_int32
from zserio.bitposition import bitsize_to_bytesize

class BitBuffer:
    """
    Bit buffer.

    Because bit buffer size does not have to be byte aligned (divisible by 8), it's possible that not all bits
    of the last byte are used. In this case, only most significant bits of the corresponded size are used.
    """

    def __init__(self, buffer: bytes, bitsize: typing.Optional[int] = None) -> None:
        """
        Constructs bit buffer from bytes buffer and bit size.

        :param buffer: Bytes-like buffer to construct from.
        :param bitsize: Number of bits stored in buffer to use.
        :raises PythonRuntimeException: If bitsize is out of range.
        """

        if bitsize is None:
            bitsize = len(buffer) * 8
        elif len(buffer) * 8 < bitsize:
            raise PythonRuntimeException(f"BitBuffer: Bit size '{bitsize}' out of range "
                                         f"for the given buffer byte size '{len(buffer)}'!")
        self._buffer: bytes = buffer
        self._bitsize: int = bitsize

    def __eq__(self, other: object) -> bool:
        if not isinstance(other, BitBuffer):
            return False

        if self._bitsize != other._bitsize:
            return False

        bytesize = bitsize_to_bytesize(self._bitsize)
        if bytesize > 0:
            if bytesize > 1:
                if self._buffer[0:bytesize - 1] != other._buffer[0:bytesize - 1]:
                    return False

            if self._masked_last_byte() != other._masked_last_byte():
                return False

        return True

    def __hash__(self) -> int:
        result = HASH_SEED
        bytesize = bitsize_to_bytesize(self._bitsize)
        if bytesize > 0:
            if bytesize > 1:
                for element in self._buffer[0:bytesize - 1]:
                    result = calc_hashcode_int32(result, element)

            result = calc_hashcode_int32(result, self._masked_last_byte())

        return result

    @property
    def buffer(self) -> bytes:
        """
        Gets the underlying byte buffer.

        Not all bits of the last byte must be used.

        :returns: The underlying byte buffer.
        """
        return self._buffer

    @property
    def bitsize(self) -> int:
        """
        Gets the number of bits stored in the bit buffer.

        :returns: Size of the bit buffer in bits.
        """
        return self._bitsize

    def _masked_last_byte(self) -> int:
        rounded_bytesize = self._bitsize // 8
        last_byte_bits = self._bitsize - 8 * rounded_bytesize

        return (self._buffer[rounded_bytesize - 1] if last_byte_bits == 0 else
                self._buffer[rounded_bytesize] & (0xFF << (8 - last_byte_bits)))
