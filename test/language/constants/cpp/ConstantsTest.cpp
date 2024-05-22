#include <limits>

#include "constants/BITFIELD8_MAX_CONSTANT.h"
#include "constants/BITFIELD8_MIN_CONSTANT.h"
#include "constants/BOOL_FALSE_CONSTANT.h"
#include "constants/BOOL_TRUE_CONSTANT.h"
#include "constants/DEFAULT_PEN_COLOR.h"
#include "constants/DEFAULT_PEN_COLOR_VALUE.h"
#include "constants/FLOAT16_CONSTANT.h"
#include "constants/FLOAT32_CONSTANT.h"
#include "constants/FLOAT64_CONSTANT.h"
#include "constants/HEX_ESC_STRING_CONSTANT.h"
#include "constants/INT16_MAX_CONSTANT.h"
#include "constants/INT16_MIN_CONSTANT.h"
#include "constants/INT32_MAX_CONSTANT.h"
#include "constants/INT32_MIN_CONSTANT.h"
#include "constants/INT64_MAX_CONSTANT.h"
#include "constants/INT64_MIN_CONSTANT.h"
#include "constants/INT8_MAX_CONSTANT.h"
#include "constants/INT8_MIN_CONSTANT.h"
#include "constants/INTFIELD8_MAX_CONSTANT.h"
#include "constants/INTFIELD8_MIN_CONSTANT.h"
#include "constants/JOINED_STRING_CONSTANT.h"
#include "constants/OCTAL_ESC_STRING_CONSTANT.h"
#include "constants/READ_PERMISSION.h"
#include "constants/READ_PERMISSION_VALUE.h"
#include "constants/STRING_CONSTANT.h"
#include "constants/SUBTYPE_BLUE_COLOR_CONSTANT.h"
#include "constants/SUBTYPE_INT25_CONSTANT.h"
#include "constants/SUBTYPE_READ_PERMISSION.h"
#include "constants/SUBTYPE_STRING_CONSTANT.h"
#include "constants/StringPascalCaseConstant.h"
#include "constants/StringSubtype.h"
#include "constants/UINT16_MAX_CONSTANT.h"
#include "constants/UINT16_MIN_CONSTANT.h"
#include "constants/UINT32_FULL_MASK.h"
#include "constants/UINT32_MAX_CONSTANT.h"
#include "constants/UINT32_MIN_CONSTANT.h"
#include "constants/UINT64_MAX_CONSTANT.h"
#include "constants/UINT64_MIN_CONSTANT.h"
#include "constants/UINT8_MAX_CONSTANT.h"
#include "constants/UINT8_MIN_CONSTANT.h"
#include "constants/UNICODE_ESC_STRING_CONSTANT.h"
#include "constants/VARIABLE_BITFIELD_CONSTANT.h"
#include "constants/VARIABLE_INTFIELD_CONSTANT.h"
#include "constants/VARINT16_MAX_CONSTANT.h"
#include "constants/VARINT16_MIN_CONSTANT.h"
#include "constants/VARINT32_MAX_CONSTANT.h"
#include "constants/VARINT32_MIN_CONSTANT.h"
#include "constants/VARINT64_MAX_CONSTANT.h"
#include "constants/VARINT64_MIN_CONSTANT.h"
#include "constants/VARINT_MAX_CONSTANT.h"
#include "constants/VARINT_MIN_CONSTANT.h"
#include "constants/VARSIZE_MAX_CONSTANT.h"
#include "constants/VARSIZE_MIN_CONSTANT.h"
#include "constants/VARUINT16_MAX_CONSTANT.h"
#include "constants/VARUINT16_MIN_CONSTANT.h"
#include "constants/VARUINT32_MAX_CONSTANT.h"
#include "constants/VARUINT32_MIN_CONSTANT.h"
#include "constants/VARUINT64_MAX_CONSTANT.h"
#include "constants/VARUINT64_MIN_CONSTANT.h"
#include "constants/VARUINT_MAX_CONSTANT.h"
#include "constants/VARUINT_MIN_CONSTANT.h"
#include "gtest/gtest.h"

using namespace zserio::literals;

namespace constants
{

TEST(ConstantsTest, uint8MinConstant)
{
    ASSERT_EQ(std::numeric_limits<uint8_t>::min(), UINT8_MIN_CONSTANT);
}

TEST(ConstantsTest, uint8MaxConstant)
{
    ASSERT_EQ(std::numeric_limits<uint8_t>::max(), UINT8_MAX_CONSTANT);
}

TEST(ConstantsTest, uint16MinConstant)
{
    ASSERT_EQ(std::numeric_limits<uint16_t>::min(), UINT16_MIN_CONSTANT);
}

TEST(ConstantsTest, uint16MaxConstant)
{
    ASSERT_EQ(std::numeric_limits<uint16_t>::max(), UINT16_MAX_CONSTANT);
}

TEST(ConstantsTest, uint32MinConstant)
{
    ASSERT_EQ(std::numeric_limits<uint32_t>::min(), UINT32_MIN_CONSTANT);
}

TEST(ConstantsTest, uint32MaxConstant)
{
    ASSERT_EQ(std::numeric_limits<uint32_t>::max(), UINT32_MAX_CONSTANT);
}

TEST(ConstantsTest, uint64MinConstant)
{
    ASSERT_EQ(std::numeric_limits<uint64_t>::min(), UINT64_MIN_CONSTANT);
}

TEST(ConstantsTest, uint64MaxConstant)
{
    ASSERT_EQ(std::numeric_limits<uint64_t>::max(), UINT64_MAX_CONSTANT);
}

TEST(ConstantsTest, int8MinConstant)
{
    ASSERT_EQ(std::numeric_limits<int8_t>::min(), INT8_MIN_CONSTANT);
}

TEST(ConstantsTest, int8MaxConstant)
{
    ASSERT_EQ(std::numeric_limits<int8_t>::max(), INT8_MAX_CONSTANT);
}

TEST(ConstantsTest, int16MinConstant)
{
    ASSERT_EQ(std::numeric_limits<int16_t>::min(), INT16_MIN_CONSTANT);
}

TEST(ConstantsTest, int16MaxConstant)
{
    ASSERT_EQ(std::numeric_limits<int16_t>::max(), INT16_MAX_CONSTANT);
}

TEST(ConstantsTest, int32MinConstant)
{
    ASSERT_EQ(std::numeric_limits<int32_t>::min(), INT32_MIN_CONSTANT);
}

TEST(ConstantsTest, int32MaxConstant)
{
    ASSERT_EQ(std::numeric_limits<int32_t>::max(), INT32_MAX_CONSTANT);
}

TEST(ConstantsTest, int64MinConstant)
{
    ASSERT_EQ(std::numeric_limits<int64_t>::min(), INT64_MIN_CONSTANT);
}

TEST(ConstantsTest, int64MaxConstant)
{
    ASSERT_EQ(std::numeric_limits<int64_t>::max(), INT64_MAX_CONSTANT);
}

TEST(ConstantsTest, bitfield8MinConstant)
{
    ASSERT_EQ(std::numeric_limits<uint8_t>::min(), BITFIELD8_MIN_CONSTANT);
}

TEST(ConstantsTest, bitfield8MaxConstant)
{
    ASSERT_EQ(std::numeric_limits<uint8_t>::max(), BITFIELD8_MAX_CONSTANT);
}

TEST(ConstantsTest, variableBitfield8Constant)
{
    ASSERT_EQ(static_cast<uint8_t>(0xAB), VARIABLE_BITFIELD_CONSTANT);
}

TEST(ConstantsTest, intfield8MinConstant)
{
    ASSERT_EQ(std::numeric_limits<int8_t>::min(), INTFIELD8_MIN_CONSTANT);
}

TEST(ConstantsTest, intfield8MaxConstant)
{
    ASSERT_EQ(std::numeric_limits<int8_t>::max(), INTFIELD8_MAX_CONSTANT);
}

TEST(ConstantsTest, variableIntfield8Constant)
{
    ASSERT_EQ(static_cast<int8_t>(0x12), VARIABLE_INTFIELD_CONSTANT);
}

TEST(ConstantsTest, float16Constant)
{
    float diff = 3.13F - FLOAT16_CONSTANT;
    if (diff < 0.0F)
    {
        diff = -diff;
    }
    ASSERT_TRUE(diff <= std::numeric_limits<float>::epsilon());
}

TEST(ConstantsTest, float32Constant)
{
    float diff = 3.131F - FLOAT32_CONSTANT;
    if (diff < 0.0F)
    {
        diff = -diff;
    }
    ASSERT_TRUE(diff <= std::numeric_limits<float>::epsilon());
}

TEST(ConstantsTest, float64Constant)
{
    double diff = 3.1314 - FLOAT64_CONSTANT;
    if (diff < 0.0)
    {
        diff = -diff;
    }
    ASSERT_TRUE(diff <= std::numeric_limits<double>::epsilon());
}

TEST(ConstantsTest, varuint16MinConstant)
{
    ASSERT_EQ(static_cast<uint16_t>(0x0000), VARUINT16_MIN_CONSTANT);
}

TEST(ConstantsTest, varuint16MaxConstant)
{
    ASSERT_EQ(static_cast<uint16_t>(0x7FFF), VARUINT16_MAX_CONSTANT);
}

TEST(ConstantsTest, varuint32MinConstant)
{
    ASSERT_EQ(static_cast<uint32_t>(0x00000000), VARUINT32_MIN_CONSTANT);
}

TEST(ConstantsTest, varuint32MaxConstant)
{
    ASSERT_EQ(static_cast<uint32_t>(0x1FFFFFFF), VARUINT32_MAX_CONSTANT);
}

TEST(ConstantsTest, varuint64MinConstant)
{
    ASSERT_EQ(static_cast<uint64_t>(0x0000000000000000ULL), VARUINT64_MIN_CONSTANT);
}

TEST(ConstantsTest, varuint64MaxConstant)
{
    ASSERT_EQ(static_cast<uint64_t>(0x01FFFFFFFFFFFFFFULL), VARUINT64_MAX_CONSTANT);
}

TEST(ConstantsTest, varuintMinConstant)
{
    ASSERT_EQ(std::numeric_limits<uint64_t>::min(), VARUINT_MIN_CONSTANT);
}

TEST(ConstantsTest, varuintMaxConstant)
{
    ASSERT_EQ(std::numeric_limits<uint64_t>::max(), VARUINT_MAX_CONSTANT);
}

TEST(ConstantsTest, varsizeMinConstant)
{
    ASSERT_EQ(static_cast<uint32_t>(0x00000000), VARSIZE_MIN_CONSTANT);
}

TEST(ConstantsTest, varsizeMaxConstant)
{
    ASSERT_EQ(static_cast<uint32_t>(0x7FFFFFFF), VARSIZE_MAX_CONSTANT);
}

TEST(ConstantsTest, varint16MinConstant)
{
    ASSERT_EQ(static_cast<int16_t>(-16383), VARINT16_MIN_CONSTANT);
}

TEST(ConstantsTest, varint16MaxConstant)
{
    ASSERT_EQ(static_cast<int16_t>(16383), VARINT16_MAX_CONSTANT);
}

TEST(ConstantsTest, varint32MinConstant)
{
    ASSERT_EQ(static_cast<int32_t>(-268435455), VARINT32_MIN_CONSTANT);
}

TEST(ConstantsTest, varint32MaxConstant)
{
    ASSERT_EQ(static_cast<int32_t>(268435455), VARINT32_MAX_CONSTANT);
}

TEST(ConstantsTest, varint64MinConstant)
{
    ASSERT_EQ(static_cast<int64_t>(-72057594037927935LL), VARINT64_MIN_CONSTANT);
}

TEST(ConstantsTest, varint64MaxConstant)
{
    ASSERT_EQ(static_cast<int64_t>(72057594037927935LL), VARINT64_MAX_CONSTANT);
}

TEST(ConstantsTest, varintMinConstant)
{
    ASSERT_EQ(std::numeric_limits<int64_t>::min(), VARINT_MIN_CONSTANT);
}

TEST(ConstantsTest, varintMaxConstant)
{
    ASSERT_EQ(std::numeric_limits<int64_t>::max(), VARINT_MAX_CONSTANT);
}

TEST(ConstantsTest, boolTrueConstant)
{
    ASSERT_TRUE(BOOL_TRUE_CONSTANT);
}

TEST(ConstantsTest, boolFalseConstant)
{
    ASSERT_FALSE(BOOL_FALSE_CONSTANT);
}

TEST(ConstantsTest, stringConstant)
{
    ASSERT_EQ("Test \"Quated\" String"_sv, STRING_CONSTANT);
}

TEST(ConstantsTest, joinedStringConstant)
{
    ASSERT_EQ("This is: Test \"Quated\" String"_sv, JOINED_STRING_CONSTANT);
}

TEST(ConstantsTest, unicodeEscStringConstant)
{
    ASSERT_EQ("Test string with unicode escape \x19"_sv, UNICODE_ESC_STRING_CONSTANT);
}

TEST(ConstantsTest, hexEscStringConstant)
{
    ASSERT_EQ("Test string with hexadecimal escape \x19"_sv, HEX_ESC_STRING_CONSTANT);
}

TEST(ConstantsTest, octalEscStringConstant)
{
    ASSERT_EQ("Test string with octal escape \031"_sv, OCTAL_ESC_STRING_CONSTANT);
}

TEST(ConstantsTest, stringPascalCaseConstant)
{
    ASSERT_EQ("Different naming convention"_sv, StringPascalCaseConstant);
}

TEST(ConstantsTest, constantDefinedByConstant)
{
    ASSERT_EQ(UINT32_FULL_MASK, UINT32_MAX_CONSTANT);
}

TEST(ConstantsTest, constantDefinedByEnum)
{
    ASSERT_EQ(Colors::BLACK, DEFAULT_PEN_COLOR);
}

TEST(ConstantsTest, constantDefinedByEnumValueof)
{
    ASSERT_EQ(zserio::enumToValue(Colors::BLACK), DEFAULT_PEN_COLOR_VALUE);
}

TEST(ConstantsTest, subtypeToInt25Constant)
{
    ASSERT_EQ(25, SUBTYPE_INT25_CONSTANT);
    const Int25Subtype expectedValue = 25;
    ASSERT_EQ(expectedValue, SUBTYPE_INT25_CONSTANT);
}

TEST(ConstantsTest, subtypeToStringConstant)
{
    ASSERT_EQ("Subtype string constant"_sv, SUBTYPE_STRING_CONSTANT);
    const StringSubtype expectedValue = "Subtype string constant";
    ASSERT_EQ(zserio::StringView(expectedValue), SUBTYPE_STRING_CONSTANT);
}

TEST(ConstantsTest, subtypeToEnumConstant)
{
    ASSERT_EQ(ColorsSubtype::BLUE, SUBTYPE_BLUE_COLOR_CONSTANT);
    ASSERT_EQ(Colors::BLUE, SUBTYPE_BLUE_COLOR_CONSTANT);
}

TEST(ConstantsTest, constantDefinedByBitmask)
{
    ASSERT_TRUE(Permission::Values::READ == READ_PERMISSION);
}

TEST(ConstantsTest, constantDefinedByBitmaskValueof)
{
    ASSERT_EQ(static_cast<Permission::underlying_type>(Permission::Values::READ), READ_PERMISSION_VALUE);
}

TEST(ConstantsTest, subtypeToBitmaskConstant)
{
    ASSERT_TRUE(PermissionSubtype::Values::READ == SUBTYPE_READ_PERMISSION);
    ASSERT_TRUE(Permission::Values::READ == SUBTYPE_READ_PERMISSION);
}

} // namespace constants
