"""
The module provides help methods to calculate size of nontrivial types.
"""

import typing

from zserio.bitbuffer import BitBuffer
from zserio.exception import PythonRuntimeException
from zserio.limits import INT64_MIN

def bitsizeof_varint16(value: int) -> int:
    """
    Gets bit size of variable 16-bit signed integer value.

    :param value: Value to use for bit size calculation.
    :returns: Bit size of the value.
    :raises PythonRuntimeException: Throws if given value is out of range for varint16 type.
    """

    return _bitsizeof_varnum(abs(value), VARINT16_MAX_VALUES, "varint16")

def bitsizeof_varint32(value: int) -> int:
    """
    Gets bit size of variable 32-bit signed integer value.

    :param value: Value to use for bit size calculation.
    :returns: Bit size of the value.
    :raises PythonRuntimeException: Throws if given value is out of range for varint32 type.
    """

    return _bitsizeof_varnum(abs(value), VARINT32_MAX_VALUES, "varint32")

def bitsizeof_varint64(value: int) -> int:
    """
    Gets bit size of variable 64-bit signed integer value.

    :param value: Value to use for bit size calculation.
    :returns: Bit size of the value.
    :raises PythonRuntimeException: Throws if given value is out of range for varint64 type.
    """

    return _bitsizeof_varnum(abs(value), VARINT64_MAX_VALUES, "varint64")

def bitsizeof_varint(value: int) -> int:
    """
    Gets bit size of variable signed integer value (up to 9 bytes).

    :param value: Value to use for bit size calculation.
    :returns: Bit size of the value.
    :raises PythonRuntimeException: Throws if given value is out of range for varint type.
    """

    if value == INT64_MIN:
        return 8 # INT64_MIN is stored as -0
    return _bitsizeof_varnum(abs(value), VARINT_MAX_VALUES, "varint")

def bitsizeof_varuint16(value: int) -> int:
    """
    Gets bit size of variable 16-bit unsigned integer value.

    :param value: Value to use for bit size calculation.
    :returns: Bit size of the value.
    :raises PythonRuntimeException: Throws if given value is out of range for varuint16 type.
    """

    return _bitsizeof_varnum(value, VARUINT16_MAX_VALUES, "varuint16")

def bitsizeof_varuint32(value: int) -> int:
    """
    Gets bit size of variable 32-bit unsigned integer value.

    :param value: Value to use for bit size calculation.
    :returns: Bit size of the value.
    :raises PythonRuntimeException: Throws if given value is out of range for varuint32 type.
    """

    return _bitsizeof_varnum(value, VARUINT32_MAX_VALUES, "varuint32")

def bitsizeof_varuint64(value: int) -> int:
    """
    Gets bit size of variable 64-bit unsigned integer value.

    :param value: Value to use for bit size calculation.
    :returns: Bit size of the value.
    :raises PythonRuntimeException: Throws if given value is out of range for varuint64 type.
    """

    return _bitsizeof_varnum(value, VARUINT64_MAX_VALUES, "varuint64")

def bitsizeof_varuint(value: int) -> int:
    """
    Gets bit size of variable unsigned integer value (up to 9 bytes).

    :param value: Value to use for bit size calculation.
    :returns: Bit size of the value.
    :raises PythonRuntimeException: Throws if given value is out of range for varuint type.
    """

    return _bitsizeof_varnum(value, VARUINT_MAX_VALUES, "varuint")

def bitsizeof_varsize(value: int) -> int:
    """
    Gets bit size of variable size integer value.

    :param value: Value to use for bit size calculation.
    :returns: Bit size of the value.
    :raises PythonRuntimeException: Throws if given value is out of range for varsize type.
    """

    return _bitsizeof_varnum(value, VARSIZE_MAX_VALUES, "varsize")

def bitsizeof_string(string: str) -> int:
    """
    Gets bit size of string.

    :param string: String value to use for bit size calculation.
    :raises PythonRuntimeException: Throws if given string is too long.
    """

    string_bytes = string.encode("utf-8")
    return bitsizeof_varsize(len(string_bytes)) + len(string_bytes) * 8

def bitsizeof_bitbuffer(bitbuffer: BitBuffer) -> int:
    """
    Gets the bit size of bit buffer which is stored in bit stream.

    :param bitbuffer: Bit buffer for calculation.
    :returns: Length of bit buffer in bits.
    :raises PythonRuntimeException: Throws if given bit buffer is too long.
    """
    bitbuffer_size = bitbuffer.bitsize

    # bit buffer consists of varsize for bit size followed by the bits
    return bitsizeof_varsize(bitbuffer_size) + bitbuffer_size

def _bitsizeof_varnum(value: int, max_values: typing.Sequence[int], varint_name: str) -> int:
    if value >= 0:
        abs_value = abs(value)
        for i, max_value in enumerate(max_values):
            if abs_value <= max_value:
                return (i + 1) * 8

    raise PythonRuntimeException(f"bitsizeof: Value '{value}' is out of range for '{varint_name}'!")

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
