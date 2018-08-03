#include <limits>

#include "gtest/gtest.h"

#include "constants/ConstType.h"

namespace constants
{

TEST(ConstantsTest, uint8MinConstant)
{
    ASSERT_EQ(std::numeric_limits<uint8_t>::min(), ConstType::UINT8_MIN_CONSTANT);
}

TEST(ConstantsTest, uint8MaxConstant)
{
    ASSERT_EQ(std::numeric_limits<uint8_t>::max(), ConstType::UINT8_MAX_CONSTANT);
}

TEST(ConstantsTest, uint16MinConstant)
{
    ASSERT_EQ(std::numeric_limits<uint16_t>::min(), ConstType::UINT16_MIN_CONSTANT);
}

TEST(ConstantsTest, uint16MaxConstant)
{
    ASSERT_EQ(std::numeric_limits<uint16_t>::max(), ConstType::UINT16_MAX_CONSTANT);
}

TEST(ConstantsTest, uint32MinConstant)
{
    ASSERT_EQ(std::numeric_limits<uint32_t>::min(), ConstType::UINT32_MIN_CONSTANT);
}

TEST(ConstantsTest, uint32MaxConstant)
{
    ASSERT_EQ(std::numeric_limits<uint32_t>::max(), ConstType::UINT32_MAX_CONSTANT);
}

TEST(ConstantsTest, uint64MinConstant)
{
    ASSERT_EQ(std::numeric_limits<uint64_t>::min(), ConstType::UINT64_MIN_CONSTANT);
}

TEST(ConstantsTest, uint64MaxConstant)
{
    ASSERT_EQ(std::numeric_limits<uint64_t>::max(), ConstType::UINT64_MAX_CONSTANT);
}

TEST(ConstantsTest, int8MinConstant)
{
    ASSERT_EQ(std::numeric_limits<int8_t>::min(), ConstType::INT8_MIN_CONSTANT);
}

TEST(ConstantsTest, int8MaxConstant)
{
    ASSERT_EQ(std::numeric_limits<int8_t>::max(), ConstType::INT8_MAX_CONSTANT);
}

TEST(ConstantsTest, int16MinConstant)
{
    ASSERT_EQ(std::numeric_limits<int16_t>::min(), ConstType::INT16_MIN_CONSTANT);
}

TEST(ConstantsTest, int16MaxConstant)
{
    ASSERT_EQ(std::numeric_limits<int16_t>::max(), ConstType::INT16_MAX_CONSTANT);
}

TEST(ConstantsTest, int32MinConstant)
{
    ASSERT_EQ(std::numeric_limits<int32_t>::min(), ConstType::INT32_MIN_CONSTANT);
}

TEST(ConstantsTest, int32MaxConstant)
{
    ASSERT_EQ(std::numeric_limits<int32_t>::max(), ConstType::INT32_MAX_CONSTANT);
}

TEST(ConstantsTest, int64MinConstant)
{
    ASSERT_EQ(std::numeric_limits<int64_t>::min(), ConstType::INT64_MIN_CONSTANT);
}

TEST(ConstantsTest, int64MaxConstant)
{
    ASSERT_EQ(std::numeric_limits<int64_t>::max(), ConstType::INT64_MAX_CONSTANT);
}

TEST(ConstantsTest, bitfield8MinConstant)
{
    ASSERT_EQ(std::numeric_limits<uint8_t>::min(), ConstType::BITFIELD8_MIN_CONSTANT);
}

TEST(ConstantsTest, bitfield8MaxConstant)
{
    ASSERT_EQ(std::numeric_limits<uint8_t>::max(), ConstType::BITFIELD8_MAX_CONSTANT);
}

TEST(ConstantsTest, variableBitfield8Constant)
{
    ASSERT_EQ(static_cast<uint8_t>(0xAB), ConstType::VARIABLE_BITFIELD_CONSTANT);
}

TEST(ConstantsTest, intfield8MinConstant)
{
    ASSERT_EQ(std::numeric_limits<int8_t>::min(), ConstType::INTFIELD8_MIN_CONSTANT);
}

TEST(ConstantsTest, intfield8MaxConstant)
{
    ASSERT_EQ(std::numeric_limits<int8_t>::max(), ConstType::INTFIELD8_MAX_CONSTANT);
}

TEST(ConstantsTest, variableIntfield8Constant)
{
    ASSERT_EQ(static_cast<int8_t>(0x12), ConstType::VARIABLE_INTFIELD_CONSTANT);
}

TEST(ConstantsTest, floatConstant)
{
    float diff = 3.13f - ConstType::FLOAT16_CONSTANT;
    if (diff < 0.0f)
        diff = -diff;
    ASSERT_TRUE(diff <= std::numeric_limits<float>::epsilon());
}

TEST(ConstantsTest, varuint16MinConstant)
{
    ASSERT_EQ(static_cast<uint16_t>(0x0000), ConstType::VARUINT16_MIN_CONSTANT);
}

TEST(ConstantsTest, varuint16MaxConstant)
{
    ASSERT_EQ(static_cast<uint16_t>(0x7FFF), ConstType::VARUINT16_MAX_CONSTANT);
}

TEST(ConstantsTest, varuint32MinConstant)
{
    ASSERT_EQ(static_cast<uint32_t>(0x00000000), ConstType::VARUINT32_MIN_CONSTANT);
}

TEST(ConstantsTest, varuint32MaxConstant)
{
    ASSERT_EQ(static_cast<uint32_t>(0x1FFFFFFF), ConstType::VARUINT32_MAX_CONSTANT);
}

TEST(ConstantsTest, varuint64MinConstant)
{
    ASSERT_EQ(static_cast<uint64_t>(0x0000000000000000ULL), ConstType::VARUINT64_MIN_CONSTANT);
}

TEST(ConstantsTest, varuint64MaxConstant)
{
    ASSERT_EQ(static_cast<uint64_t>(0x01FFFFFFFFFFFFFFULL), ConstType::VARUINT64_MAX_CONSTANT);
}

TEST(ConstantsTest, varuintMinConstant)
{
    ASSERT_EQ(std::numeric_limits<uint64_t>::min(), ConstType::VARUINT_MIN_CONSTANT);
}

TEST(ConstantsTest, varuintMaxConstant)
{
    ASSERT_EQ(std::numeric_limits<uint64_t>::max(), ConstType::VARUINT_MAX_CONSTANT);
}

TEST(ConstantsTest, varint16MinConstant)
{
    ASSERT_EQ(static_cast<int16_t>(-16383), ConstType::VARINT16_MIN_CONSTANT);
}

TEST(ConstantsTest, varint16MaxConstant)
{
    ASSERT_EQ(static_cast<int16_t>(16383), ConstType::VARINT16_MAX_CONSTANT);
}

TEST(ConstantsTest, varint32MinConstant)
{
    ASSERT_EQ(static_cast<int32_t>(-268435455), ConstType::VARINT32_MIN_CONSTANT);
}

TEST(ConstantsTest, varint32MaxConstant)
{
    ASSERT_EQ(static_cast<int32_t>(268435455), ConstType::VARINT32_MAX_CONSTANT);
}

TEST(ConstantsTest, varint64MinConstant)
{
    ASSERT_EQ(static_cast<int64_t>(-72057594037927935LL), ConstType::VARINT64_MIN_CONSTANT);
}

TEST(ConstantsTest, varint64MaxConstant)
{
    ASSERT_EQ(static_cast<int64_t>(72057594037927935LL), ConstType::VARINT64_MAX_CONSTANT);
}

TEST(ConstantsTest, varintMinConstant)
{
    ASSERT_EQ(std::numeric_limits<int64_t>::min(), ConstType::VARINT_MIN_CONSTANT);
}

TEST(ConstantsTest, varintMaxConstant)
{
    ASSERT_EQ(std::numeric_limits<int64_t>::max(), ConstType::VARINT_MAX_CONSTANT);
}

TEST(ConstantsTest, boolTrueConstant)
{
    ASSERT_TRUE(ConstType::BOOL_TRUE_CONSTANT);
}

TEST(ConstantsTest, boolFalseConstant)
{
    ASSERT_FALSE(ConstType::BOOL_FALSE_CONSTANT);
}

TEST(ConstantsTest, stringConstant)
{
    ASSERT_EQ("Test \"Quated\" String", ConstType::STRING_CONSTANT);
}

TEST(ConstantsTest, unicodeEscStringConstant)
{
    ASSERT_EQ("Test string with unicode escape \x19", ConstType::UNICODE_ESC_STRING_CONSTANT);
}

TEST(ConstantsTest, hexEscStringConstant)
{
    ASSERT_EQ("Test string with hexadecimal escape \x19", ConstType::HEX_ESC_STRING_CONSTANT);
}

TEST(ConstantsTest, octalEscStringConstant)
{
    ASSERT_EQ("Test string with octal escape \031", ConstType::OCTAL_ESC_STRING_CONSTANT);
}

TEST(ConstantsTest, enumerationConstant)
{
    ASSERT_EQ(Colors::BLACK, ConstType::DEFAULT_PEN_COLOR);
}

TEST(ConstantsTest, constantDefinedByConstant)
{
    ASSERT_EQ(ConstType::UINT32_FULL_MASK, ConstType::UINT32_MAX_CONSTANT);
}

} // namespace constants
