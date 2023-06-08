#ifndef ZSERIO_JSON_ENCODER_H_INC
#define ZSERIO_JSON_ENCODER_H_INC

#include <type_traits>

#include "zserio/String.h"
#include "zserio/Vector.h"
#include "zserio/StringConvertUtil.h"
#include "zserio/CppRuntimeException.h"
#include "zserio/StringView.h"

namespace zserio
{

/**
 * Converts zserio values to Json string representation.
 */
class JsonEncoder
{
public:
    /**
     * Encodes JSON null value to the given stream.
     *
     * \param os Stream to use.
     */
    static void encodeNull(std::ostream& os);

    /**
     * Encodes JSON boolean value to the given stream.
     *
     * \param os Stream to use.
     * \param value Value to encode.
     */
    static void encodeBool(std::ostream& os, bool value);

    /**
     * Encodes JSON integral value to the given stream.
     *
     * \param os Stream to use.
     * \param value Value to encode.
     */
    template <typename T>
    static void encodeIntegral(std::ostream& os, T value);

    /**
     * Encodes JSON floating-point value to the given stream.
     *
     * \param os Stream to use.
     * \param value Value to encode.
     */
    static void encodeFloatingPoint(std::ostream& os, double value);

    /**
     * Encodes JSON string value to the given stream.
     *
     * Note that this method performs escaping necessary to get a proper JSON string.
     *
     * \param os Stream to use.
     * \param value Value to encode.
     */
    static void encodeString(std::ostream& os, StringView value);
};

template <typename T>
void JsonEncoder::encodeIntegral(std::ostream& os, T value)
{
    using U = typename std::conditional<std::is_signed<T>::value, int64_t, uint64_t>::type;
    os << static_cast<U>(value);
}

} // namespace zserio

#endif // ZSERIO_JSON_ENCODER_H_INC
