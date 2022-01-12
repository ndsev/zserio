"""
The module provides help methods for bit position calculation.
"""

from zserio.exception import PythonRuntimeException

def alignto(alignment_value: int, bitposition: int) -> int:
    """
    Aligns the bit size to the given alignment value.

    :param alignment_value: Value to align.
    :param bitposition: Current bit position where to apply alignment.
    :returns: Aligned bit position.
    """

    if bitposition <= 0 or alignment_value == 0:
        return bitposition

    return (((bitposition - 1) // alignment_value) + 1) * alignment_value

def bits_to_bytes(numbits: int) -> int:
    """
    Converts number of bits to bytes.

    :param numbits: The number of bits to convert.
    :returns: Number of bytes
    :raises PythonRuntimeException: If number of bits to convert is not divisible by 8.
    """

    if numbits % 8 != 0:
        raise PythonRuntimeException(f"bitposition: '{numbits}' is not a multiple of 8!")

    return numbits // 8

def bytes_to_bits(num_bytes: int) -> int:
    """
    Converts number of bytes to bits.

    :param num_bytes: The n number of bytes to convert.
    :returns: Number of bits.
    """

    return num_bytes * 8

def bitsize_to_bytesize(bitsize: int) -> int:
    """
    Converts number of bits to number of bytes.

    :param bitsize: Size in bits to convert.
    :returns: Size in bytes.
    """

    return (bitsize + 7) // 8
