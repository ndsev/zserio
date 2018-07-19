#include <limits>

#include "CppRuntimeException.h"
#include "StringConvertUtil.h"
#include "BitPositionUtil.h"
#include "BitSizeOfCalculator.h"

namespace zserio
{

static const uint64_t VarInt16MaxValues[] =
{
    (UINT64_C(1) << (6)) - 1,
    (UINT64_C(1) << (6 + 8)) - 1,
};
static const size_t VarInt16MaxNumValues = sizeof(VarInt16MaxValues) / sizeof(VarInt16MaxValues[0]);

static const uint64_t VarInt32MaxValues[] =
{
    (UINT64_C(1) << (6)) - 1,
    (UINT64_C(1) << (6 + 7)) - 1,
    (UINT64_C(1) << (6 + 7 + 7)) - 1,
    (UINT64_C(1) << (6 + 7 + 7 + 8)) - 1
};
static const size_t VarInt32MaxNumValues = sizeof(VarInt32MaxValues) / sizeof(VarInt32MaxValues[0]);

static const uint64_t VarInt64MaxValues[] =
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
static const size_t VarInt64MaxNumValues = sizeof(VarInt64MaxValues) / sizeof(VarInt64MaxValues[0]);

static const uint64_t VarUInt16MaxValues[] =
{
    (UINT64_C(1) << (7)) - 1,
    (UINT64_C(1) << (7 + 8)) - 1,
};
static const size_t VarUInt16MaxNumValues = sizeof(VarUInt16MaxValues) / sizeof(VarUInt16MaxValues[0]);

static const uint64_t VarUInt32MaxValues[] =
{
    (UINT64_C(1) << (7)) - 1,
    (UINT64_C(1) << (7 + 7)) - 1,
    (UINT64_C(1) << (7 + 7 + 7)) - 1,
    (UINT64_C(1) << (7 + 7 + 7 + 8)) - 1
};
static const size_t VarUInt32MaxNumValues = sizeof(VarUInt32MaxValues) / sizeof(VarUInt32MaxValues[0]);

static const uint64_t VarUInt64MaxValues[] =
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
static const size_t VarUInt64MaxNumValues = sizeof(VarUInt64MaxValues) / sizeof(VarUInt64MaxValues[0]);

static size_t getBitSizeOfVarInt(uint64_t value, const uint64_t* maxValues, size_t numMaxValues)
{
    const uint64_t* maxValue = maxValues;
    size_t byteSize = 1;
    for (; byteSize <= numMaxValues; ++byteSize)
    {
        if (value <= *maxValue)
            break;
        maxValue++;
    }

    if (byteSize > numMaxValues)
    {
        throw CppRuntimeException("VarInt" + convertToString(bytesToBits(numMaxValues)) + " value " +
                convertToString(value) + " is out of range");
    }

    return bytesToBits(byteSize);
}

template<typename T>
static uint64_t convertToAbsValue(T value)
{
    return static_cast<uint64_t>((value < 0) ? -value : value);
}

size_t getBitSizeOfVarInt16(int16_t value)
{
    return getBitSizeOfVarInt(convertToAbsValue(value), VarInt16MaxValues, VarInt16MaxNumValues);
}

size_t getBitSizeOfVarInt32(int32_t value)
{
    return getBitSizeOfVarInt(convertToAbsValue(value), VarInt32MaxValues, VarInt32MaxNumValues);
}

size_t getBitSizeOfVarInt64(int64_t value)
{
    return getBitSizeOfVarInt(convertToAbsValue(value), VarInt64MaxValues, VarInt64MaxNumValues);
}

size_t getBitSizeOfVarUInt16(uint16_t value)
{
    return getBitSizeOfVarInt(value, VarUInt16MaxValues, VarUInt16MaxNumValues);
}

size_t getBitSizeOfVarUInt32(uint32_t value)
{
    return getBitSizeOfVarInt(value, VarUInt32MaxValues, VarUInt32MaxNumValues);
}

size_t getBitSizeOfVarUInt64(uint64_t value)
{
    return getBitSizeOfVarInt(value, VarUInt64MaxValues, VarUInt64MaxNumValues);
}

size_t getBitSizeOfString(const std::string& value)
{
    const size_t stringSize = value.size();

    // the string consists of varuint64 for size followed by the UTF-8 encoded string
    return getBitSizeOfVarUInt64(static_cast<uint64_t>(stringSize)) + bytesToBits(stringSize);
}

} // namespace zserio
