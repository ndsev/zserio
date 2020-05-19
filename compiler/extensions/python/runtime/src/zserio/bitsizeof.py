"""
The module provides help methods to calculate size of nontrivial types.
"""

from zserio.exception import PythonRuntimeException
from zserio.limits import INT64_MIN

def getBitSizeOfVarInt16(value):
    """
    Gets bit size of variable 16-bit signed integer value.

    :param value: Value to use for bit size calculation.
    :returns: Bit size of the value.
    :raises PythonRuntimeException: Throws if given value is out of range for varint16 type.
    """

    return _getBitSizeOfVarIntImpl(abs(value), VARINT16_MAX_VALUES, "varint16")

def getBitSizeOfVarInt32(value):
    """
    Gets bit size of variable 32-bit signed integer value.

    :param value: Value to use for bit size calculation.
    :returns: Bit size of the value.
    :raises PythonRuntimeException: Throws if given value is out of range for varint32 type.
    """

    return _getBitSizeOfVarIntImpl(abs(value), VARINT32_MAX_VALUES, "varint32")

def getBitSizeOfVarInt64(value):
    """
    Gets bit size of variable 64-bit signed integer value.

    :param value: Value to use for bit size calculation.
    :returns: Bit size of the value.
    :raises PythonRuntimeException: Throws if given value is out of range for varint64 type.
    """

    return _getBitSizeOfVarIntImpl(abs(value), VARINT64_MAX_VALUES, "varint64")

def getBitSizeOfVarInt(value):
    """
    Gets bit size of variable signed integer value (up to 9 bytes).

    :param value: Value to use for bit size calculation.
    :returns: Bit size of the value.
    :raises PythonRuntimeException: Throws if given value is out of range for varint type.
    """

    if value == INT64_MIN:
        return 8 # INT64_MIN is stored as -0
    return _getBitSizeOfVarIntImpl(abs(value), VARINT_MAX_VALUES, "varint")

def getBitSizeOfVarUInt16(value):
    """
    Gets bit size of variable 16-bit unsigned integer value.

    :param value: Value to use for bit size calculation.
    :returns: Bit size of the value.
    :raises PythonRuntimeException: Throws if given value is out of range for varuint16 type.
    """

    return _getBitSizeOfVarIntImpl(value, VARUINT16_MAX_VALUES, "varuint16")

def getBitSizeOfVarUInt32(value):
    """
    Gets bit size of variable 32-bit unsigned integer value.

    :param value: Value to use for bit size calculation.
    :returns: Bit size of the value.
    :raises PythonRuntimeException: Throws if given value is out of range for varuint32 type.
    """

    return _getBitSizeOfVarIntImpl(value, VARUINT32_MAX_VALUES, "varuint32")

def getBitSizeOfVarUInt64(value):
    """
    Gets bit size of variable 64-bit unsigned integer value.

    :param value: Value to use for bit size calculation.
    :returns: Bit size of the value.
    :raises PythonRuntimeException: Throws if given value is out of range for varuint64 type.
    """

    return _getBitSizeOfVarIntImpl(value, VARUINT64_MAX_VALUES, "varuint64")

def getBitSizeOfVarUInt(value):
    """
    Gets bit size of variable unsigned integer value (up to 9 bytes).

    :param value: Value to use for bit size calculation.
    :returns: Bit size of the value.
    :raises PythonRuntimeException: Throws if given value is out of range for varuint type.
    """

    return _getBitSizeOfVarIntImpl(value, VARUINT_MAX_VALUES, "varuint")

def getBitSizeOfVarSize(value):
    """
    Gets bit size of variable size integer value.

    :param value: Value to use for bit size calculation.
    :returns: Bit size of the value.
    :raises PythonRuntimeException: Throws if given value is out of range for varsize type.
    """

    return _getBitSizeOfVarIntImpl(value, VARSIZE_MAX_VALUES, "varsize")

def getBitSizeOfString(string):
    """
    Gets bit size of string.

    :param string: String value to use for bit size calculation.
    :raises PythonRuntimeException: Throws if given string is too long.
    """

    stringBytes = string.encode("utf-8")
    return getBitSizeOfVarSize(len(stringBytes)) + len(stringBytes) * 8

def getBitSizeOfBitBuffer(bitBuffer):
    """
    Gets the bit size of bit buffer which is stored in bit stream.

    :param bitBuffer: Bit buffer for calculation.
    :returns: Length of bit buffer in bits.
    :raises PythonRuntimeException: Throws if given bit buffer is too long.
    """
    bitBufferSize = bitBuffer.getBitSize()

    # bit buffer consists of varsize for bit size followed by the bits
    return getBitSizeOfVarSize(bitBufferSize) + bitBufferSize

def _getBitSizeOfVarIntImpl(value, maxValues, varIntName):
    if value >= 0:
        absValue = abs(value)
        for i, maxValue in enumerate(maxValues):
            if absValue <= maxValue:
                return (i + 1) * 8

    raise PythonRuntimeException("bitsizeof: Value '%d' is out of range for %s!" % (value, varIntName))

VARINT16_MAX_VALUES = [
    (1 << (6)) - 1,
    (1 << (6 + 8)) - 1
]

VARINT32_MAX_VALUES = [
    (1 << (6)) - 1,
    (1 << (6 + 7)) - 1,
    (1 << (6 + 7 + 7)) - 1,
    (1 << (6 + 7 + 7 + 8)) - 1,
]

VARINT64_MAX_VALUES = [
    (1 << (6)) - 1,
    (1 << (6 + 7)) - 1,
    (1 << (6 + 7 + 7)) - 1,
    (1 << (6 + 7 + 7 + 7)) - 1,
    (1 << (6 + 7 + 7 + 7 + 7)) - 1,
    (1 << (6 + 7 + 7 + 7 + 7 + 7)) - 1,
    (1 << (6 + 7 + 7 + 7 + 7 + 7 + 7)) - 1,
    (1 << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1,
]

VARINT_MAX_VALUES = [
    (1 << (6)) - 1,
    (1 << (6 + 7)) - 1,
    (1 << (6 + 7 + 7)) - 1,
    (1 << (6 + 7 + 7 + 7)) - 1,
    (1 << (6 + 7 + 7 + 7 + 7)) - 1,
    (1 << (6 + 7 + 7 + 7 + 7 + 7)) - 1,
    (1 << (6 + 7 + 7 + 7 + 7 + 7 + 7)) - 1,
    (1 << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 7)) - 1,
    (1 << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1
]

VARUINT16_MAX_VALUES = [
    (1 << (7)) - 1,
    (1 << (7 + 8)) - 1
]

VARUINT32_MAX_VALUES = [
    (1 << (7)) - 1,
    (1 << (7 + 7)) - 1,
    (1 << (7 + 7 + 7)) - 1,
    (1 << (7 + 7 + 7 + 8)) - 1
]

VARUINT64_MAX_VALUES = [
    (1 << (7)) - 1,
    (1 << (7 + 7)) - 1,
    (1 << (7 + 7 + 7)) - 1,
    (1 << (7 + 7 + 7 + 7)) - 1,
    (1 << (7 + 7 + 7 + 7 + 7)) - 1,
    (1 << (7 + 7 + 7 + 7 + 7 + 7)) - 1,
    (1 << (7 + 7 + 7 + 7 + 7 + 7 + 7)) - 1,
    (1 << (7 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1
]

VARUINT_MAX_VALUES = [
    (1 << (7)) - 1,
    (1 << (7 + 7)) - 1,
    (1 << (7 + 7 + 7)) - 1,
    (1 << (7 + 7 + 7 + 7)) - 1,
    (1 << (7 + 7 + 7 + 7 + 7)) - 1,
    (1 << (7 + 7 + 7 + 7 + 7 + 7)) - 1,
    (1 << (7 + 7 + 7 + 7 + 7 + 7 + 7)) - 1,
    (1 << (7 + 7 + 7 + 7 + 7 + 7 + 7 + 7)) - 1,
    (1 << (7 + 7 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1,
]

VARSIZE_MAX_VALUES = [
    (1 << (7)) - 1,
    (1 << (7 + 7)) - 1,
    (1 << (7 + 7 + 7)) - 1,
    (1 << (7 + 7 + 7 + 7)) - 1,
    (1 << (2 + 7 + 7 + 7 + 8)) - 1,
]
