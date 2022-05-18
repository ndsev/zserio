#ifndef ZSERIO_FLOAT_UTIL_H_INC
#define ZSERIO_FLOAT_UTIL_H_INC

#include "zserio/Types.h"

/**
 * \file
 * The module provides help methods for manipulation with float numbers.
 *
 * The following float formats defined by IEEE 754 standard are supported:
 *
 * - half precision float point format (https://en.wikipedia.org/wiki/Half-precision_floating-point_format)
 * - single precision float point format (https://en.wikipedia.org/wiki/Single-precision_floating-point_format)
 * - double precision float point format (https://en.wikipedia.org/wiki/Double-precision_floating-point_format)
 */
namespace zserio
{
    /**
     * Converts 16-bit float stored in uint16_t value to 32-bit float.
     *
     * \param float16Value Half precision float value stored in uint16_t to convert.
     *
     * \return Converted single precision float.
     */
    float convertUInt16ToFloat(uint16_t float16Value);

    /**
     * Converts 32-bit float to 16-bit float stored in uint16_t value.
     *
     * \param float32 Single precision float to convert.
     *
     * \return Converted half precision float value stored in uint16_t.
     */
    uint16_t convertFloatToUInt16(float float32);

    /**
     * Converts 32-bit float stored in uint32_t value to 32-bit float.
     *
     * \param float32Value Single precision float value stored in uint32_t to convert.
     *
     * \return Converted single precision float.
     */
    float convertUInt32ToFloat(uint32_t float32Value);

    /**
     * Converts 32-bit float to 32-bit float stored in uint32_t value.
     *
     * \param float32 Single precision float to convert.
     *
     * \return Converted single precision float value stored in uint32_t.
     */
    uint32_t convertFloatToUInt32(float float32);

    /**
     * Converts 64-bit float (double) stored in uint64_t value to 64-bit float (double).
     *
     * \param float64Value Double precision float value stored in uint64_t to convert.
     *
     * \return Converted double precision float.
     */
    double convertUInt64ToDouble(uint64_t float64Value);

    /**
     * Converts 64-bit float (double) to 64-bit float (double) stored in uint64_t value.
     *
     * \param float64 Double precision float to convert.
     *
     * \return Converted double precision float value stored in uint64_t.
     */
    uint64_t convertDoubleToUInt64(double float64);
} // namespace zserio

#endif // ZSERIO_FLOAT_UTIL_H_INC
