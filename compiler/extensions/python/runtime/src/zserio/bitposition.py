"""
The module provides help methods for bit position calculation.
"""

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

def alignToByte(bitPosition):
    """
    Aligns the bit size to the byte boundary.

    :param bitPosition: Current bit position where to apply alignment.
    :returns: Aligned bit position.
    """

    return alignTo(8, bitPosition)

def bitsToBytes(numBits):
    """
    Converts number of bits to bytes.

    :param numBits: The number of bits to convert.
    :returns: Number of bytes (could be a float number).
    """

    return numBits / 8

def bytesToBits(numBytes):
    """
    Converts number of bytes to bits.

    :param numBytes: The n number of bytes to convert.
    :returns: Number of bits.
    """

    return numBytes * 8
