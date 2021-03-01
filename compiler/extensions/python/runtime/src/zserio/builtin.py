"""
The module provides implementation of zserio built-in operators.
"""

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
