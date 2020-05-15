"""
The module provides help methods for bit position calculation.
"""

from zserio.exception import PythonRuntimeException

def alignTo(alignmentValue, bitPosition):
    """
    Aligns the bit size to the given alignment value.

    :param alignmentValue: Value to align.
    :param bitPosition: Current bit position where to apply alignment.
    :returns: Aligned bit position.
    """

    if bitPosition <= 0 or alignmentValue == 0:
        return bitPosition

    return (((bitPosition - 1) // alignmentValue) + 1) * alignmentValue

def bitsToBytes(numBits):
    """
    Converts number of bits to bytes.

    :param numBits: The number of bits to convert.
    :returns: Number of bytes
    :raises PythonRuntimeException: If number of bits to convert is not divisible by 8.
    """

    if numBits % 8 != 0:
        raise PythonRuntimeException("bitPosition: %d is not a multiple of 8!" % numBits)

    return numBits // 8

def bytesToBits(numBytes):
    """
    Converts number of bytes to bits.

    :param numBytes: The n number of bytes to convert.
    :returns: Number of bits.
    """

    return numBytes * 8
