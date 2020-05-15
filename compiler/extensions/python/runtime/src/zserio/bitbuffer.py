"""
The module implements abstraction for holding bit sequence.
"""

from zserio.exception import PythonRuntimeException
from zserio.hashcode import HASH_SEED
from zserio.hashcode import calcHashCode

class BitBuffer:
    """
    Bit buffer.

    Because bit buffer size does not have to be byte aligned (divisible by 8), it's possible that not all bits
    of the last byte are used. In this case, only most significant bits of the corresponded size are used.
    """

    def __init__(self, buffer, bitSize=None):
        """
        Constructs bit buffer from bytes buffer and bit size.

        :param buffer: Bytes-like buffer to construct from.
        :param bitSize: Number of bits stored in buffer to use.
        :raises PythonRuntimeException: If the numBits is invalid number of the reading goes behind the stream.
        """

        if bitSize is None:
            bitSize = len(buffer) * 8
        elif len(buffer) * 8 < bitSize:
            raise PythonRuntimeException("BitBuffer: Bit size %d out of range for given buffer byte "
                                         "size %d!" % (bitSize, len(buffer)))
        self._buffer = buffer
        self._bitSize = bitSize

    def __eq__(self, other):
        if not isinstance(other, BitBuffer):
            return False

        if self._bitSize != other._bitSize:
            return False

        byteSize = self.getByteSize()
        if byteSize > 0:
            if byteSize > 1:
                if self._buffer[0:byteSize - 1] != other._buffer[0:byteSize - 1]:
                    return False

            if self._getMaskedLastByte() != other._getMaskedLastByte():
                return False

        return True

    def __hash__(self):
        result = HASH_SEED
        byteSize = self.getByteSize()
        if byteSize > 0:
            if byteSize > 1:
                for element in self._buffer[0:byteSize - 1]:
                    result = calcHashCode(result, hash(element))

            result = calcHashCode(result, hash(self._getMaskedLastByte()))

        return result

    def getBuffer(self):
        """
        Gets the underlying byte buffer.

        Not all bits of the last byte must be used.

        :returns: The underlying byte buffer.
        """
        return self._buffer

    def getBitSize(self):
        """
        Gets the number of bits stored in the bit buffer.

        :returns: Size of the bit buffer in bits.
        """
        return self._bitSize

    def getByteSize(self):
        """
        Gets the number of bytes stored in the bit buffer.

        :returns: Size of the bit buffer in bytes.
        """
        return (self._bitSize + 7) // 8

    def _getMaskedLastByte(self):
        roundedByteSize = self._bitSize // 8
        lastByteBits = self._bitSize - 8 * roundedByteSize

        return (self._buffer[roundedByteSize - 1] if lastByteBits == 0 else
                self._buffer[roundedByteSize] & (0xFF << (8 - lastByteBits)))
