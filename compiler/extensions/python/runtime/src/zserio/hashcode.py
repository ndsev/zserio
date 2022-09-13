"""
The module provides utility methods for hash code calculation.
"""

import typing

from zserio.float import float_to_uint32, float_to_uint64

HASH_SEED = 23
HASH_PRIME_NUMBER = 37

def calc_hashcode_bool(seed_value: int, value: bool) -> int:
    """
    Calculates hash code for a boolean value.

    :param seed_value: Seed value (current hash code).
    :param value: Value to use.
    :returns: Calculated hash code.
    """

    return calc_hashcode_int32(seed_value, 1 if value else 0)

def calc_hashcode_int32(seed_value: int, value: int) -> int:
    """
    Calculates hash code for a 32-bit integral value.

    :param seed_value: Seed value (current hash code).
    :param value: Value to use.
    :returns: Calculated hash code.
    """

    if value is None:
        return calc_hashcode_int32(seed_value, 0)

    return (HASH_PRIME_NUMBER * seed_value + value) & 0xFFFFFFFF

def calc_hashcode_int64(seed_value: int, value: int) -> int:
    """
    Calculates hash code for a 64-bit integral value.

    :param seed_value: Seed value (current hash code).
    :param value: Value to use.
    :returns: Calculated hash code.
    """

    if value is None:
        return calc_hashcode_int32(seed_value, 0)

    int_value_for_hash = (value & 0xFFFFFFFF) ^ ((value & 0xFFFFFFFFFFFFFFFF) >> 32)
    return (HASH_PRIME_NUMBER * seed_value + int_value_for_hash) & 0xFFFFFFFF

def calc_hashcode_float32(seed_value: int, value: float) -> int:
    """
    Calculates hash code for a 32-bit float value.

    :param seed_value: Seed value (current hash code).
    :param value: Value to use.
    :returns: Calculated hash code.
    """

    if value is None:
        return calc_hashcode_int32(seed_value, 0)

    int_value = float_to_uint32(value)
    return calc_hashcode_int32(seed_value, int_value)

def calc_hashcode_float64(seed_value: int, value: float) -> int:
    """
    Calculates hash code for a 64-bit float value.

    :param seed_value: Seed value (current hash code).
    :param value: Value to use.
    :returns: Calculated hash code.
    """

    if value is None:
        return calc_hashcode_int32(seed_value, 0)

    int_value = float_to_uint64(value)
    return calc_hashcode_int64(seed_value, int_value)

def calc_hashcode_string(seed_value: int, value: str) -> int:
    """
    Calculates hash code for a string value.

    :param seed_value: Seed value (current hash code).
    :param value: Value to use.
    :returns: Calculated hash code.
    """

    if value is None:
        return calc_hashcode_int32(seed_value, 0)

    result = seed_value
    for element in value:
        result = calc_hashcode_int32(result, ord(element))

    return result

def calc_hashcode_object(seed_value: int, value: typing.Any) -> int:
    """
    Calculates hash code for an object value.

    This is used for all objects (in zserio runtime or generated) which override the default __hash__ method.

    :param seed_value: Seed value (current hash code).
    :param value: Value to use.
    :returns: Calculated hash code.
    """

    # using __hash__ to prevent 32-bit Python hash() truncation
    return calc_hashcode_int32(seed_value, value.__hash__() if value else 0)

def calc_hashcode_bool_array(seed_value: int, value: typing.List[bool]) -> int:
    """
    Calculates hash code for a boolean array value.

    :param seed_value: Seed value (current hash code).
    :param value: Value to use.
    :returns: Calculated hash code.
    """

    if value is None:
        return calc_hashcode_int32(seed_value, 0)

    result = seed_value
    for element in value:
        result = calc_hashcode_bool(result, element)
    return result

def calc_hashcode_int_array(seed_value: int, value: typing.List[int]) -> int:
    """
    Calculates hash code for an integral array value.

    :param seed_value: Seed value (current hash code).
    :param value: Value to use.
    :returns: Calculated hash code.
    """

    if value is None:
        return calc_hashcode_int32(seed_value, 0)

    result = seed_value
    for element in value:
        result = calc_hashcode_int32(result, element)
    return result

def calc_hashcode_float32_array(seed_value: int, value: typing.List[int]) -> int:
    """
    Calculates hash code for a 32-bit float array value.

    :param seed_value: Seed value (current hash code).
    :param value: Value to use.
    :returns: Calculated hash code.
    """

    if value is None:
        return calc_hashcode_int32(seed_value, 0)

    result = seed_value
    for element in value:
        result = calc_hashcode_float32(result, element)
    return result

def calc_hashcode_float64_array(seed_value: int, value: typing.List[int]) -> int:
    """
    Calculates hash code for a 64-bit float array value.

    :param seed_value: Seed value (current hash code).
    :param value: Value to use.
    :returns: Calculated hash code.
    """

    if value is None:
        return calc_hashcode_int32(seed_value, 0)

    result = seed_value
    for element in value:
        result = calc_hashcode_float64(result, element)
    return result

def calc_hashcode_string_array(seed_value: int, value: typing.List[str]) -> int:
    """
    Calculates hash code for a string array value.

    :param seed_value: Seed value (current hash code).
    :param value: Value to use.
    :returns: Calculated hash code.
    """

    if value is None:
        return calc_hashcode_int32(seed_value, 0)

    result = seed_value
    for element in value:
        result = calc_hashcode_string(result, element)
    return result

def calc_hashcode_object_array(seed_value: int, value: typing.List[typing.Any]) -> int:
    """
    Calculates hash code for an object array value.

    This is used for arrays of all objects (in zserio runtime or generated) which override the default
    __hash__ method.

    :param seed_value: Seed value (current hash code).
    :param value: Value to use.
    :returns: Calculated hash code.
    """

    if value is None:
        return calc_hashcode_int32(seed_value, 0)

    result = seed_value
    for element in value:
        result = calc_hashcode_object(result, element)
    return result
