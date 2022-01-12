"""
The module provides help methods for bit fields calculation.
"""

from zserio.exception import PythonRuntimeException

def bitfield_lowerbound(length: int) -> int:
    """
    Gets the lower bound of a unsigned bitfield type with given length.

    :param length: Length of the unsigned bitfield in bits.
    :returns: The lowest value the unsigned bitfield can hold.
    :raises PythonRuntimeException: If unsigned bitfield with wrong length has been specified.
    """

    _check_bitfield_length(length)
    return 0

def bitfield_upperbound(length: int) -> int:

    """
    Gets the upper bound of a unsigned bitfield type with given length.

    :param length: Length of the unsigned bitfield in bits.
    :returns: The largest value the unsigned bitfield can hold.
    :raises PythonRuntimeException: If unsigned bitfield with wrong length has been specified.
    """

    _check_bitfield_length(length)
    return (1 << length) - 1

def signed_bitfield_lowerbound(length: int) -> int:
    """
    Gets the lower bound of a signed bitfield type with given length.

    :param length: Length of the signed bitfield in bits.
    :returns: The lowest value the signed bitfield can hold.
    :raises PythonRuntimeException: If signed bitfield with wrong length has been specified.
    """

    _check_bitfield_length(length)
    return -(1 << (length - 1))

def signed_bitfield_upperbound(length: int) -> int:
    """
    Gets the upper bound of a signed bitfield type with given length.

    :param length: Length of the signed bitfield in bits.
    :returns: The largest value the signed bitfield can hold.
    :raises PythonRuntimeException: If signed bitfield with wrong length has been specified.
    """

    _check_bitfield_length(length)
    return (1 << (length - 1)) - 1

def _check_bitfield_length(length: int) -> None:
    if length <= 0 or length > MAX_BITFIELD_BITS:
        raise PythonRuntimeException(f"bitfield: Asking for bound of bitfield with invalid length '{length}'!")

MAX_BITFIELD_BITS = 64
