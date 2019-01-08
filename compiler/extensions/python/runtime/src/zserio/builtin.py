"""
The module provides implementation of zserio built-in operators.
"""

def getNumBits(numValues):
    """
    Gets the minimum number of bits required to encode given number of different values.

    This method implements zserio built-in operator numBits.

    :param numValues: The number of different values from which to calculate number of bits.
    :returns: Number of bits required to encode numValues different values.
    """

    if numValues == 0:
        return 0
    if numValues == 1:
        return 1

    return (numValues - 1).bit_length()
