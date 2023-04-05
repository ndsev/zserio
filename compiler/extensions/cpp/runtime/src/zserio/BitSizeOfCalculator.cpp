#include <limits>
#include <array>

#include "zserio/BitSizeOfCalculator.h"
#include "zserio/CppRuntimeException.h"

namespace zserio
{

static const std::array<uint64_t, 2> VARIN16_MAX_VALUES =
{
    (UINT64_C(1) << (6)) - 1,
    (UINT64_C(1) << (6 + 8)) - 1,
};

static const std::array<uint64_t, 4> VARINT32_MAX_VALUES =
{
    (UINT64_C(1) << (6)) - 1,
    (UINT64_C(1) << (6 + 7)) - 1,
    (UINT64_C(1) << (6 + 7 + 7)) - 1,
    (UINT64_C(1) << (6 + 7 + 7 + 8)) - 1
};

static const std::array<uint64_t, 8> VARINT64_MAX_VALUES =
{
    (UINT64_C(1) << (6)) - 1,
    (UINT64_C(1) << (6 + 7)) - 1,
    (UINT64_C(1) << (6 + 7 + 7)) - 1,
    (UINT64_C(1) << (6 + 7 + 7 + 7)) - 1,
    (UINT64_C(1) << (6 + 7 + 7 + 7 + 7)) - 1,
    (UINT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7)) - 1,
    (UINT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7 + 7)) - 1,
    (UINT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1
};

static const std::array<uint64_t, 2> VARUINT16_MAX_VALUES =
{
    (UINT64_C(1) << (7)) - 1,
    (UINT64_C(1) << (7 + 8)) - 1,
};

static const std::array<uint64_t, 4> VARUINT32_MAX_VALUES =
{
    (UINT64_C(1) << (7)) - 1,
    (UINT64_C(1) << (7 + 7)) - 1,
    (UINT64_C(1) << (7 + 7 + 7)) - 1,
    (UINT64_C(1) << (7 + 7 + 7 + 8)) - 1
};

static const std::array<uint64_t, 8> VARUINT64_MAX_VALUES =
{
    (UINT64_C(1) << (7)) - 1,
    (UINT64_C(1) << (7 + 7)) - 1,
    (UINT64_C(1) << (7 + 7 + 7)) - 1,
    (UINT64_C(1) << (7 + 7 + 7 + 7)) - 1,
    (UINT64_C(1) << (7 + 7 + 7 + 7 + 7)) - 1,
    (UINT64_C(1) << (7 + 7 + 7 + 7 + 7 + 7)) - 1,
    (UINT64_C(1) << (7 + 7 + 7 + 7 + 7 + 7 + 7)) - 1,
    (UINT64_C(1) << (7 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1
};

static const std::array<uint64_t, 9> VARINT_MAX_VALUES =
{
    (UINT64_C(1) << (6)) - 1,
    (UINT64_C(1) << (6 + 7)) - 1,
    (UINT64_C(1) << (6 + 7 + 7)) - 1,
    (UINT64_C(1) << (6 + 7 + 7 + 7)) - 1,
    (UINT64_C(1) << (6 + 7 + 7 + 7 + 7)) - 1,
    (UINT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7)) - 1,
    (UINT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7 + 7)) - 1,
    (UINT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 7)) - 1,
    (UINT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1
};

static const std::array<uint64_t, 9> VARUINT_MAX_VALUES =
{
    (UINT64_C(1) << (7)) - 1,
    (UINT64_C(1) << (7 + 7)) - 1,
    (UINT64_C(1) << (7 + 7 + 7)) - 1,
    (UINT64_C(1) << (7 + 7 + 7 + 7)) - 1,
    (UINT64_C(1) << (7 + 7 + 7 + 7 + 7)) - 1,
    (UINT64_C(1) << (7 + 7 + 7 + 7 + 7 + 7)) - 1,
    (UINT64_C(1) << (7 + 7 + 7 + 7 + 7 + 7 + 7)) - 1,
    (UINT64_C(1) << (7 + 7 + 7 + 7 + 7 + 7 + 7 + 7)) - 1,
    UINT64_MAX
};

static const std::array<uint64_t, 5> VARSIZE_MAX_VALUES =
{
    (UINT64_C(1) << (7)) - 1,
    (UINT64_C(1) << (7 + 7)) - 1,
    (UINT64_C(1) << (7 + 7 + 7)) - 1,
    (UINT64_C(1) << (7 + 7 + 7 + 7)) - 1,
    (UINT64_C(1) << (2 + 7 + 7 + 7 + 8)) - 1,
};

template <std::size_t SIZE>
static size_t bitSizeOfVarIntImpl(uint64_t value, const std::array<uint64_t, SIZE>& maxValues,
        const char* varIntName)
{
    size_t byteSize = 1;
    for (uint64_t maxValue : maxValues)
    {
        if (value <= maxValue)
            break;
        byteSize++;
    }

    if (byteSize > maxValues.size())
    {
        throw CppRuntimeException("BitSizeOfCalculator: Value '") << value <<
                "' is out of range for " << varIntName << "!";
    }

    return byteSize * 8;
}

template <typename T>
static uint64_t convertToAbsValue(T value)
{
    return static_cast<uint64_t>((value < 0) ? -value : value);
}

size_t bitSizeOfVarInt16(int16_t value)
{
    return bitSizeOfVarIntImpl(convertToAbsValue(value), VARIN16_MAX_VALUES, "varint16");
}

size_t bitSizeOfVarInt32(int32_t value)
{
    return bitSizeOfVarIntImpl(convertToAbsValue(value), VARINT32_MAX_VALUES, "varint32");
}

size_t bitSizeOfVarInt64(int64_t value)
{
    return bitSizeOfVarIntImpl(convertToAbsValue(value), VARINT64_MAX_VALUES, "varint64");
}

size_t bitSizeOfVarUInt16(uint16_t value)
{
    return bitSizeOfVarIntImpl(value, VARUINT16_MAX_VALUES, "varuint16");
}

size_t bitSizeOfVarUInt32(uint32_t value)
{
    return bitSizeOfVarIntImpl(value, VARUINT32_MAX_VALUES, "varuint32");
}

size_t bitSizeOfVarUInt64(uint64_t value)
{
    return bitSizeOfVarIntImpl(value, VARUINT64_MAX_VALUES, "varuint64");
}

size_t bitSizeOfVarInt(int64_t value)
{
    if (value == INT64_MIN)
        return 8; // INT64_MIN is stored as -0

    return bitSizeOfVarIntImpl(convertToAbsValue(value), VARINT_MAX_VALUES, "varint");
}

size_t bitSizeOfVarUInt(uint64_t value)
{
    return bitSizeOfVarIntImpl(value, VARUINT_MAX_VALUES, "varuint");
}

size_t bitSizeOfVarSize(uint32_t value)
{
    return bitSizeOfVarIntImpl(value, VARSIZE_MAX_VALUES, "varsize");
}

size_t bitSizeOfBytes(Span<const uint8_t> bytesValue)
{
    const size_t bytesSize = bytesValue.size();

    // the bytes consists of varsize for size followed by the bytes
    return bitSizeOfVarSize(convertSizeToUInt32(bytesSize)) + bytesSize * 8;
}

size_t bitSizeOfString(StringView stringValue)
{
    const size_t stringSize = stringValue.size();

    // the string consists of varsize for size followed by the UTF-8 encoded string
    return bitSizeOfVarSize(convertSizeToUInt32(stringSize)) + stringSize * 8;
}

} // namespace zserio
