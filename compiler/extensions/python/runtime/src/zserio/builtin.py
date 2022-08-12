"""
The module provides implementation of zserio built-in operators.
"""

import typing

def isset(bitmask_value: typing.Any, required_mask: typing.Any) -> bool:
    """
    Checks whether the required_mask is set within the bitmask_value.

    This method implements zserio built-in operator isset.

    :param bitmask_value: Bitmask value to check.
    :param required_mask: Mask to use.
    :returns: True when the required_mask is set within the bitmask_value, False otherwise.
    """

    return (bitmask_value & required_mask) == required_mask

def numbits(num_values: int) -> int:
    """
    Gets the minimum number of bits required to encode given number of different values.

    This method implements zserio built-in operator numBits.

    :param num_values: The number of different values from which to calculate number of bits.
    :returns: Number of bits required to encode num_values different values.
    """

    if num_values == 0:
        return 0
    if num_values == 1:
        return 1

    return (num_values - 1).bit_length()
