"""
The module provides help methods for manipulation with float numbers.

The following float formats defined by IEEE 754 standard are supported:

* half precision float point format (https://en.wikipedia.org/wiki/Half-precision_floating-point_format)
* single precision float point format (https://en.wikipedia.org/wiki/Single-precision_floating-point_format)
* double precision float point format (https://en.wikipedia.org/wiki/Double-precision_floating-point_format)
"""

import struct

def convertUInt16ToFloat(float16Value):
    """
    Converts 16-bit float stored as an integer value to python native float.

    :param float16Value: Half precision float value stored as an integer value to convert.
    :returns: Converted python native float.
    """

    # decompose half precision float (float16)
    sign16Shifted = (float16Value & FLOAT16_SIGN_MASK)
    exponent16 = (float16Value & FLOAT16_EXPONENT_MASK) >> FLOAT16_EXPONENT_BIT_POSITION
    significand16 = (float16Value & FLOAT16_SIGNIFICAND_MASK)

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
    sign32Shifted = sign16Shifted << (FLOAT32_SIGN_BIT_POSITION - FLOAT16_SIGN_BIT_POSITION)
    exponent32Shifted = exponent32 << FLOAT32_EXPONENT_BIT_POSITION
    float32Value = sign32Shifted | exponent32Shifted | significand32

    # convert it to float
    return convertUInt32ToFloat(float32Value)

def convertFloatToUInt16(float64):
    """
    Converts python native float to 16-bit float stored as integer value.

    :param float64: Python native float to convert.
    :returns: Converted half precision float value stored as an integer value.
    """

    float32Value = convertFloatToUInt32(float64)

    # decompose single precision float (float32)
    sign32Shifted = (float32Value & FLOAT32_SIGN_MASK)
    exponent32 = (float32Value & FLOAT32_EXPONENT_MASK) >> FLOAT32_EXPONENT_BIT_POSITION
    significand32 = (float32Value & FLOAT32_SIGNIFICAND_MASK)

    # calculate significand for half precision float (float16)
    significand16 = significand32 >> (FLOAT32_SIGNIFICAND_NUM_BITS - FLOAT16_SIGNIFICAND_NUM_BITS)

    # calculate exponent for half precision float (float16)
    needsRounding = False
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
        signedExponent16 = exponent32 - FLOAT32_EXPONENT_BIAS + FLOAT16_EXPONENT_BIAS
        if signedExponent16 > FLOAT16_EXPONENT_INFINITY_NAN:
            # exponent overflow, set infinity or NaN
            exponent16 = FLOAT16_EXPONENT_INFINITY_NAN
        elif signedExponent16 <= 0:
            # exponent underflow
            if signedExponent16 <= -FLOAT16_SIGNIFICAND_NUM_BITS:
                # too big underflow, set to zero
                exponent16 = 0
                significand16 = 0
            else:
                # we can still use subnormal numbers
                exponent16 = 0
                fullSignificand32 = significand32 | (FLOAT32_SIGNIFICAND_MASK + 1)
                significandShift = 1 - signedExponent16
                significand16 = fullSignificand32 >> (FLOAT32_SIGNIFICAND_NUM_BITS -
                                                      FLOAT16_SIGNIFICAND_NUM_BITS + significandShift)

                needsRounding = ((fullSignificand32 >>
                                  (FLOAT32_SIGNIFICAND_NUM_BITS - FLOAT16_SIGNIFICAND_NUM_BITS +
                                   significandShift - 1)) & 1) != 0
        else:
            # exponent ok
            exponent16 = signedExponent16
            needsRounding = ((significand32 >> (FLOAT32_SIGNIFICAND_NUM_BITS -
                                                FLOAT16_SIGNIFICAND_NUM_BITS - 1)) & 1) != 0

    # compose half precision float (float16)
    sign16Shifted = sign32Shifted >> (FLOAT32_SIGN_BIT_POSITION - FLOAT16_SIGN_BIT_POSITION)
    exponent16Shifted = exponent16 << FLOAT16_EXPONENT_BIT_POSITION
    float16Value = sign16Shifted | exponent16Shifted | significand16

    # check rounding
    if needsRounding:
        float16Value += 1   # might overflow to infinity

    return float16Value

def convertUInt32ToFloat(float32Value):
    """
    Converts 32-bit float stored as an integer value to python native float.

    :param float32Value: Single precision float value stored as an integer value to convert.
    :returns: Converted python native float.
    """

    float32ValueInBytes = float32Value.to_bytes(4, byteorder="big")

    return struct.unpack('>f', float32ValueInBytes)[0]

def convertFloatToUInt32(float64):
    """
    Converts python native float to 32-bit float stored as integer value.

    :param float64: Python native float to convert.
    :returns: Converted single precision float value stored as an integer value.
    """

    float32ValueInBytes = struct.pack('>f', float64)

    return int.from_bytes(float32ValueInBytes, byteorder="big")

def convertUInt64ToFloat(float64Value):
    """
    Converts 64-bit float stored as an integer value to python native float.

    :param float64Value: Double precision float value stored as an integer value to convert.
    :returns: Converted python native float.
    """

    float64ValueInBytes = float64Value.to_bytes(8, byteorder="big")

    return struct.unpack('>d', float64ValueInBytes)[0]

def convertFloatToUInt64(float64):
    """
    Converts python native float to 64-bit float stored as integer value.

    :param float64: Python native float to convert.
    :returns: Converted double precision float value stored as an integer value.
    """

    float64ValueInBytes = struct.pack('>d', float64)

    return int.from_bytes(float64ValueInBytes, byteorder="big")

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
