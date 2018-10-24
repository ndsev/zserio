"""
The module provides implementation of zserio built-in operators.
"""

def getNumBits(value):
    """
    Gets the minimum number of bits required to encode value-1.

    This method implements zserio built-in operator numBits.
    Note that numBits returns 1 if the value is less than 3.

    :param value: An integral value.
    :returns Number of bits required to encode value-1.
    """

    if value < 3:
        return 1
    return (value - 1).bit_length()
