"""
The module provides utility methods for hash code calculation.
"""

HASH_SEED = 23

def calcHashCode(seedValue, value):
    """
    Calculates hash code of the value using seed value.

    :param seedValue: Seed value (current hash code).
    :param value: Value for which to calculate hash code.
    :returns: Calculated hash code.
    """

    return (HASH_PRIME_NUMBER * seedValue + value) & 0xFFFFFFFF

HASH_PRIME_NUMBER = 37
