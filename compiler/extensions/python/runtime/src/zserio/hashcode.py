"""
The module provides utility methods for hash code calculation.
"""

HASH_SEED = 23

def calc_hashcode(seed_value: int, value: int) -> int:
    """
    Calculates hash code of the value using seed value.

    :param seed_value: Seed value (current hash code).
    :param value: Value for which to calculate hash code.
    :returns: Calculated hash code.
    """

    return (HASH_PRIME_NUMBER * seed_value + value) & 0xFFFFFFFF

HASH_PRIME_NUMBER = 37
