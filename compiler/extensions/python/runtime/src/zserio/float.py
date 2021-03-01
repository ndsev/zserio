"""
The module provides help methods for manipulation with float numbers.

The following float formats defined by IEEE 754 standard are supported:

* half precision float point format (https://en.wikipedia.org/wiki/Half-precision_floating-point_format)
* single precision float point format (https://en.wikipedia.org/wiki/Single-precision_floating-point_format)
* double precision float point format (https://en.wikipedia.org/wiki/Double-precision_floating-point_format)
"""

import struct

def uint16_to_float(float16_value: int) -> float:
    """
    Converts 16-bit float stored as an integer value to python native float.

    :param float16_value: Half precision float value stored as an integer value to convert.
    :returns: Converted python native float.
    """

    # decompose half precision float (float16)
    sign16_shifted = (float16_value & FLOAT16_SIGN_MASK)
    exponent16 = (float16_value & FLOAT16_EXPONENT_MASK) >> FLOAT16_EXPONENT_BIT_POSITION
    significand16 = (float16_value & FLOAT16_SIGNIFICAND_MASK)

    # calculate significand for single precision float (float32)
    significand32 = significand16 << (FLOAT32_SIGNIFICAND_NUM_BITS - FLOAT16_SIGNIFICAND_NUM_BITS)

    # calculate exponent for single precision float (float32)
    if exponent16 == 0:
        if significand32 != 0:
            # subnormal (denormal) number will be normalized
            exponent32 = (1 + FLOAT32_EXPONENT_BIAS -
                          FLOAT16_EXPONENT_BIAS) # exp is initialized by -14
            # shift significand until leading bit overflows into exponent bit
            while (significand32 & (FLOAT32_SIGNIFICAND_MASK + 1)) == 0:
                exponent32 = exponent32 - 1
                significand32 <<= 1

            # mask out overflowed leading bit from significand (normalized has implicit leading bit 1)
            significand32 &= FLOAT32_SIGNIFICAND_MASK
        else:
            # zero
            exponent32 = 0
    elif exponent16 == FLOAT16_EXPONENT_INFINITY_NAN:
        # infinity or NaN
        exponent32 = FLOAT32_EXPONENT_INFINITY_NAN
    else:
        # normal number
        exponent32 = exponent16 - FLOAT16_EXPONENT_BIAS + FLOAT32_EXPONENT_BIAS

    # compose single precision float (float32)
    sign32_shifted = sign16_shifted << (FLOAT32_SIGN_BIT_POSITION - FLOAT16_SIGN_BIT_POSITION)
    exponent32_shifted = exponent32 << FLOAT32_EXPONENT_BIT_POSITION
    float32_value = sign32_shifted | exponent32_shifted | significand32

    # convert it to float
    return uint32_to_float(float32_value)

def float_to_uint16(float64: float) -> int:
    """
    Converts python native float to 16-bit float stored as integer value.

    :param float64: Python native float to convert.
    :returns: Converted half precision float value stored as an integer value.
    """

    float32_value = float_to_uint32(float64)

    # decompose single precision float (float32)
    sign32_shifted = (float32_value & FLOAT32_SIGN_MASK)
    exponent32 = (float32_value & FLOAT32_EXPONENT_MASK) >> FLOAT32_EXPONENT_BIT_POSITION
    significand32 = (float32_value & FLOAT32_SIGNIFICAND_MASK)

    # calculate significand for half precision float (float16)
    significand16 = significand32 >> (FLOAT32_SIGNIFICAND_NUM_BITS - FLOAT16_SIGNIFICAND_NUM_BITS)

    # calculate exponent for half precision float (float16)
    needs_rounding = False
    if exponent32 == 0:
        if significand32 != 0:
            # subnormal (denormal) number will be zero
            significand16 = 0
        exponent16 = 0
    elif exponent32 == FLOAT32_EXPONENT_INFINITY_NAN:
        # infinity or NaN
        exponent16 = FLOAT16_EXPONENT_INFINITY_NAN
    else:
        # normal number
        signed_exponent16 = exponent32 - FLOAT32_EXPONENT_BIAS + FLOAT16_EXPONENT_BIAS
        if signed_exponent16 > FLOAT16_EXPONENT_INFINITY_NAN:
            # exponent overflow, set infinity or NaN
            exponent16 = FLOAT16_EXPONENT_INFINITY_NAN
        elif signed_exponent16 <= 0:
            # exponent underflow
            if signed_exponent16 <= -FLOAT16_SIGNIFICAND_NUM_BITS:
                # too big underflow, set to zero
                exponent16 = 0
                significand16 = 0
            else:
                # we can still use subnormal numbers
                exponent16 = 0
                full_significand32 = significand32 | (FLOAT32_SIGNIFICAND_MASK + 1)
                significand_shift = 1 - signed_exponent16
                significand16 = full_significand32 >> (FLOAT32_SIGNIFICAND_NUM_BITS -
                                                       FLOAT16_SIGNIFICAND_NUM_BITS + significand_shift)

                needs_rounding = ((full_significand32 >>
                                  (FLOAT32_SIGNIFICAND_NUM_BITS - FLOAT16_SIGNIFICAND_NUM_BITS +
                                   significand_shift - 1)) & 1) != 0
        else:
            # exponent ok
            exponent16 = signed_exponent16
            needs_rounding = ((significand32 >> (FLOAT32_SIGNIFICAND_NUM_BITS -
                                                 FLOAT16_SIGNIFICAND_NUM_BITS - 1)) & 1) != 0

    # compose half precision float (float16)
    sign16_shifted = sign32_shifted >> (FLOAT32_SIGN_BIT_POSITION - FLOAT16_SIGN_BIT_POSITION)
    exponent16_shifted = exponent16 << FLOAT16_EXPONENT_BIT_POSITION
    float16_value = sign16_shifted | exponent16_shifted | significand16

    # check rounding
    if needs_rounding:
        float16_value += 1   # might overflow to infinity

    return float16_value

def uint32_to_float(float32_value: int) -> float:
    """
    Converts 32-bit float stored as an integer value to python native float.

    :param float32_value: Single precision float value stored as an integer value to convert.
    :returns: Converted python native float.
    """

    float32_value_in_bytes = float32_value.to_bytes(4, byteorder="big")

    return struct.unpack('>f', float32_value_in_bytes)[0]

def float_to_uint32(float64: float) -> int:
    """
    Converts python native float to 32-bit float stored as integer value.

    :param float64: Python native float to convert.
    :returns: Converted single precision float value stored as an integer value.
    """

    float32_value_in_bytes = struct.pack('>f', float64)

    return int.from_bytes(float32_value_in_bytes, byteorder="big")

def uint64_to_float(float64_value: int) -> float:
    """
    Converts 64-bit float stored as an integer value to python native float.

    :param float64_value: Double precision float value stored as an integer value to convert.
    :returns: Converted python native float.
    """

    float64_value_in_bytes = float64_value.to_bytes(8, byteorder="big")

    return struct.unpack('>d', float64_value_in_bytes)[0]

def float_to_uint64(float64: float) -> int:
    """
    Converts python native float to 64-bit float stored as integer value.

    :param float64: Python native float to convert.
    :returns: Converted double precision float value stored as an integer value.
    """

    float64_value_in_bytes = struct.pack('>d', float64)

    return int.from_bytes(float64_value_in_bytes, byteorder="big")

FLOAT16_SIGN_MASK = 0x8000
FLOAT16_EXPONENT_MASK = 0x7C00
FLOAT16_SIGNIFICAND_MASK = 0x03FF

FLOAT16_SIGN_BIT_POSITION = 15
FLOAT16_EXPONENT_BIT_POSITION = 10

FLOAT16_SIGNIFICAND_NUM_BITS = FLOAT16_EXPONENT_BIT_POSITION

FLOAT16_EXPONENT_INFINITY_NAN = 0x001F
FLOAT16_EXPONENT_BIAS = 15

FLOAT32_SIGN_MASK = 0x80000000
FLOAT32_EXPONENT_MASK = 0x7F800000
FLOAT32_SIGNIFICAND_MASK = 0x007FFFFF

FLOAT32_SIGN_BIT_POSITION = 31
FLOAT32_EXPONENT_BIT_POSITION = 23

FLOAT32_SIGNIFICAND_NUM_BITS = FLOAT32_EXPONENT_BIT_POSITION

FLOAT32_EXPONENT_INFINITY_NAN = 0x00FF
FLOAT32_EXPONENT_BIAS = 127
