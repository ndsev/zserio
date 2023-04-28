#include "zserio/FloatUtil.h"

namespace zserio
{

static const uint16_t FLOAT16_SIGN_MASK = UINT16_C(0x8000);
static const uint16_t FLOAT16_EXPONENT_MASK = UINT16_C(0x7C00);
static const uint16_t FLOAT16_SIGNIFICAND_MASK = UINT16_C(0x03FF);

static const uint16_t FLOAT16_SIGN_BIT_POSITION = UINT16_C(15);
static const uint16_t FLOAT16_EXPONENT_BIT_POSITION = UINT16_C(10);

static const uint16_t FLOAT16_SIGNIFICAND_NUM_BITS = FLOAT16_EXPONENT_BIT_POSITION;

static const uint16_t FLOAT16_EXPONENT_INFINITY_NAN = UINT16_C(0x001F);
static const uint16_t FLOAT16_EXPONENT_BIAS = UINT16_C(15);

static const uint32_t FLOAT32_SIGN_MASK = UINT32_C(0x80000000);
static const uint32_t FLOAT32_EXPONENT_MASK = UINT32_C(0x7F800000);
static const uint32_t FLOAT32_SIGNIFICAND_MASK = UINT32_C(0x007FFFFF);

static const uint32_t FLOAT32_SIGN_BIT_POSITION = UINT32_C(31);
static const uint32_t FLOAT32_EXPONENT_BIT_POSITION = UINT32_C(23);

static const uint32_t FLOAT32_SIGNIFICAND_NUM_BITS = FLOAT32_EXPONENT_BIT_POSITION;

static const uint32_t FLOAT32_EXPONENT_INFINITY_NAN = UINT32_C(0x00FF);
static const uint32_t FLOAT32_EXPONENT_BIAS = UINT32_C(127);

float convertUInt16ToFloat(uint16_t float16Value)
{
    // decompose half precision float (float16)
    const uint16_t sign16Shifted = (float16Value & FLOAT16_SIGN_MASK);
    const uint16_t exponent16 = static_cast<uint16_t>(float16Value & FLOAT16_EXPONENT_MASK) >>
            FLOAT16_EXPONENT_BIT_POSITION;
    const uint16_t significand16 = (float16Value & FLOAT16_SIGNIFICAND_MASK);

    // calculate significand for single precision float (float32)
    uint32_t significand32 = static_cast<uint32_t>(significand16) <<
            (FLOAT32_SIGNIFICAND_NUM_BITS - FLOAT16_SIGNIFICAND_NUM_BITS);

    // calculate exponent for single precision float (float32)
    uint32_t exponent32 = 0;
    if (exponent16 == 0)
    {
        if (significand32 != 0)
        {
            // subnormal (denormal) number will be normalized
            exponent32 = 1 + FLOAT32_EXPONENT_BIAS - FLOAT16_EXPONENT_BIAS; // exp is initialized by -14
            // shift significand until leading bit overflows into exponent bit
            while ((significand32 & (FLOAT32_SIGNIFICAND_MASK + 1)) == 0)
            {
                exponent32--;
                significand32 <<= 1U;
            }
            // mask out overflowed leading bit from significand (normalized has implicit leading bit 1)
            significand32 &= FLOAT32_SIGNIFICAND_MASK;
        }
    }
    else if (exponent16 == FLOAT16_EXPONENT_INFINITY_NAN)
    {
        // infinity or NaN
        exponent32 = FLOAT32_EXPONENT_INFINITY_NAN;
    }
    else
    {
        // normal number
        exponent32 = exponent16 - FLOAT16_EXPONENT_BIAS + FLOAT32_EXPONENT_BIAS;
    }

    // compose single precision float (float32)
    const uint32_t sign32Shifted = static_cast<uint32_t>(sign16Shifted) << (FLOAT32_SIGN_BIT_POSITION -
            FLOAT16_SIGN_BIT_POSITION);
    const uint32_t exponent32Shifted = exponent32 << FLOAT32_EXPONENT_BIT_POSITION;
    const uint32_t float32Value = sign32Shifted | exponent32Shifted | significand32;

    // convert it to float
    return convertUInt32ToFloat(float32Value);
}

uint16_t convertFloatToUInt16(float float32)
{
    const uint32_t float32Value = convertFloatToUInt32(float32);

    // decompose single precision float (float32)
    const uint32_t sign32Shifted = (float32Value & FLOAT32_SIGN_MASK);
    const uint32_t exponent32 = (float32Value & FLOAT32_EXPONENT_MASK) >> FLOAT32_EXPONENT_BIT_POSITION;
    const uint32_t significand32 = (float32Value & FLOAT32_SIGNIFICAND_MASK);

    // calculate significand for half precision float (float16)
    uint16_t significand16 = static_cast<uint16_t>((significand32 >>
            (FLOAT32_SIGNIFICAND_NUM_BITS - FLOAT16_SIGNIFICAND_NUM_BITS)));

    // calculate exponent for half precision float (float16)
    bool needsRounding = false;
    uint16_t exponent16 = 0;
    if (exponent32 == 0)
    {
        if (significand32 != 0)
        {
            // subnormal (denormal) number will be zero
            significand16 = 0;
        }
    }
    else if (exponent32 == FLOAT32_EXPONENT_INFINITY_NAN)
    {
        // infinity or NaN
        exponent16 = FLOAT16_EXPONENT_INFINITY_NAN;
    }
    else
    {
        // normal number
        const int16_t signedExponent16 = static_cast<int16_t>(static_cast<int32_t>(exponent32) -
                static_cast<int32_t>(FLOAT32_EXPONENT_BIAS) + static_cast<int32_t>(FLOAT16_EXPONENT_BIAS));
        if (signedExponent16 > FLOAT16_EXPONENT_INFINITY_NAN)
        {
            // exponent overflow, set infinity or NaN
            exponent16 = FLOAT16_EXPONENT_INFINITY_NAN;
        }
        else if (signedExponent16 <= 0)
        {
            // exponent underflow
            if (signedExponent16 <= static_cast<int16_t>(-FLOAT16_SIGNIFICAND_NUM_BITS))
            {
                // too big underflow, set to zero
                significand16 = 0;
            }
            else
            {
                // we can still use subnormal numbers
                const uint32_t fullSignificand32 = significand32 | (FLOAT32_SIGNIFICAND_MASK + 1);
                const uint32_t significandShift = static_cast<uint32_t>(1 - signedExponent16);
                significand16 = static_cast<uint16_t>(fullSignificand32 >>
                        (FLOAT32_SIGNIFICAND_NUM_BITS - FLOAT16_SIGNIFICAND_NUM_BITS + significandShift));

                needsRounding = ((fullSignificand32 >> (FLOAT32_SIGNIFICAND_NUM_BITS -
                        FLOAT16_SIGNIFICAND_NUM_BITS + significandShift - 1)) & UINT32_C(1)) != 0;
            }
        }
        else
        {
            // exponent ok
            exponent16 = static_cast<uint16_t>(signedExponent16);
            needsRounding = ((significand32 >> (FLOAT32_SIGNIFICAND_NUM_BITS -
                    FLOAT16_SIGNIFICAND_NUM_BITS - 1)) & UINT32_C(1)) != 0;
        }
    }

    // compose half precision float (float16)
    const uint16_t sign16Shifted = static_cast<uint16_t>(sign32Shifted >> (FLOAT32_SIGN_BIT_POSITION -
            FLOAT16_SIGN_BIT_POSITION));
    const uint16_t exponent16Shifted = static_cast<uint16_t>(exponent16 << FLOAT16_EXPONENT_BIT_POSITION);
    uint16_t float16Value = static_cast<uint16_t>(sign16Shifted | exponent16Shifted) | significand16;

    // check rounding
    if (needsRounding)
        float16Value += UINT16_C(1); // might overflow to infinity

    return float16Value;
}

float convertUInt32ToFloat(uint32_t float32Value)
{
    const float* convertedFloat = reinterpret_cast<const float*>(static_cast<void*>(&float32Value));

    return *convertedFloat;
}

uint32_t convertFloatToUInt32(float float32)
{
    const uint32_t* float32ValuePtr = reinterpret_cast<const uint32_t*>(static_cast<void*>(&float32));

    return *float32ValuePtr;
}

double convertUInt64ToDouble(uint64_t float64Value)
{
    const double* convertedDouble = reinterpret_cast<const double*>(static_cast<void*>(&float64Value));

    return *convertedDouble;
}

uint64_t convertDoubleToUInt64(double float64)
{
    const uint64_t* float64ValuePtr = reinterpret_cast<const uint64_t*>(static_cast<void*>(&float64));

    return *float64ValuePtr;
}

} // namespace zserio
