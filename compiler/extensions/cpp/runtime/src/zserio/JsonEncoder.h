#ifndef ZSERIO_JSON_ENCODER_H_INC
#define ZSERIO_JSON_ENCODER_H_INC

#include <type_traits>

#include "zserio/String.h"
#include "zserio/Vector.h"
#include "zserio/StringConvertUtil.h"
#include "zserio/CppRuntimeException.h"

namespace zserio
{

class JsonEncoder
{
public:
    static void encodeNull(std::ostream& os);

    static void encodeBool(std::ostream& os, bool value);

    template <typename T>
    static void encodeIntegral(std::ostream& os, T value);

    static void encodeFloatingPoint(std::ostream& os, double value);

    static void encodeString(std::ostream& os, StringView value);
};

template <typename T>
void JsonEncoder::encodeIntegral(std::ostream& os, T value)
{
    if (std::is_signed<T>::value)
        os << static_cast<int64_t>(value);
    else
        os << static_cast<uint64_t>(value);
}

} // namespace zserio

#endif // ZSERIO_JSON_ENCODER_H_INC
