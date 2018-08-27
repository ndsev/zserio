#include <limits>

#include "gtest/gtest.h"

#include "base_types/BaseTypes.h"

namespace base_types
{

class BaseTypesTest : public ::testing::Test
{
protected:
    BaseTypes   m_baseTypes;
};

TEST_F(BaseTypesTest, uint8Type)
{
    const uint8_t maxUint8Type = std::numeric_limits<uint8_t>::max();
    m_baseTypes.setUint8Type(maxUint8Type);
    const uint8_t uint8Type = m_baseTypes.getUint8Type();
    ASSERT_EQ(maxUint8Type, uint8Type);
}

TEST_F(BaseTypesTest, uint16Type)
{
    const uint16_t maxUint16Type = std::numeric_limits<uint16_t>::max();
    m_baseTypes.setUint16Type(maxUint16Type);
    const uint16_t uint16Type = m_baseTypes.getUint16Type();
    ASSERT_EQ(maxUint16Type, uint16Type);
}

TEST_F(BaseTypesTest, uint32Type)
{
    const uint32_t maxUint32Type = std::numeric_limits<uint32_t>::max();
    m_baseTypes.setUint32Type(maxUint32Type);
    const uint32_t uint32Type = m_baseTypes.getUint32Type();
    ASSERT_EQ(maxUint32Type, uint32Type);
}

TEST_F(BaseTypesTest, uint64Type)
{
    const uint64_t maxUint64Type = std::numeric_limits<uint64_t>::max();
    m_baseTypes.setUint64Type(maxUint64Type);
    const uint64_t uint64Type = m_baseTypes.getUint64Type();
    ASSERT_EQ(maxUint64Type, uint64Type);
}

TEST_F(BaseTypesTest, int8Type)
{
    const int8_t maxInt8Type = std::numeric_limits<int8_t>::max();
    m_baseTypes.setInt8Type(maxInt8Type);
    const int8_t int8Type = m_baseTypes.getInt8Type();
    ASSERT_EQ(maxInt8Type, int8Type);
}

TEST_F(BaseTypesTest, int16Type)
{
    const int16_t maxInt16Type = std::numeric_limits<int16_t>::max();
    m_baseTypes.setInt16Type(maxInt16Type);
    const int16_t int16Type = m_baseTypes.getInt16Type();
    ASSERT_EQ(maxInt16Type, int16Type);
}

TEST_F(BaseTypesTest, int32Type)
{
    const int32_t maxInt32Type = std::numeric_limits<int32_t>::max();
    m_baseTypes.setInt32Type(maxInt32Type);
    const int32_t int32Type = m_baseTypes.getInt32Type();
    ASSERT_EQ(maxInt32Type, int32Type);
}

TEST_F(BaseTypesTest, int64Type)
{
    const int64_t maxInt64Type = std::numeric_limits<int64_t>::max();
    m_baseTypes.setInt64Type(maxInt64Type);
    const int64_t int64Type = m_baseTypes.getInt64Type();
    ASSERT_EQ(maxInt64Type, int64Type);
}

TEST_F(BaseTypesTest, bitfield7Type)
{
    const uint8_t maxBitfield7Type = UINT8_C(0x7F);
    m_baseTypes.setBitfield7Type(maxBitfield7Type);
    const uint8_t bitfield7Type = m_baseTypes.getBitfield7Type();
    ASSERT_EQ(maxBitfield7Type, bitfield7Type);
}

TEST_F(BaseTypesTest, bitfield8Type)
{
    const uint8_t maxBitfield8Type = std::numeric_limits<uint8_t>::max();
    m_baseTypes.setBitfield8Type(maxBitfield8Type);
    const uint8_t bitfield8Type = m_baseTypes.getBitfield8Type();
    ASSERT_EQ(maxBitfield8Type, bitfield8Type);
}

TEST_F(BaseTypesTest, bitfield15Type)
{
    const uint16_t maxBitfield15Type = UINT16_C(0x7FFF);
    m_baseTypes.setBitfield15Type(maxBitfield15Type);
    const uint16_t bitfield15Type = m_baseTypes.getBitfield15Type();
    ASSERT_EQ(maxBitfield15Type, bitfield15Type);
}

TEST_F(BaseTypesTest, bitfield16Type)
{
    const uint16_t maxBitfield16Type = std::numeric_limits<uint16_t>::max();
    m_baseTypes.setBitfield16Type(maxBitfield16Type);
    const uint16_t bitfield16Type = m_baseTypes.getBitfield16Type();
    ASSERT_EQ(maxBitfield16Type, bitfield16Type);
}

TEST_F(BaseTypesTest, bitfield31Type)
{
    const uint32_t maxBitfield31Type = UINT32_C(0x7FFFFFFF);
    m_baseTypes.setBitfield31Type(maxBitfield31Type);
    const uint32_t bitfield31Type = m_baseTypes.getBitfield31Type();
    ASSERT_EQ(maxBitfield31Type, bitfield31Type);
}

TEST_F(BaseTypesTest, bitfield32Type)
{
    const uint32_t maxBitfield32Type = std::numeric_limits<uint32_t>::max();
    m_baseTypes.setBitfield32Type(maxBitfield32Type);
    const uint32_t bitfield32Type = m_baseTypes.getBitfield32Type();
    ASSERT_EQ(maxBitfield32Type, bitfield32Type);
}

TEST_F(BaseTypesTest, bitfield63Type)
{
    const uint64_t maxBitfield63Type = UINT64_C(0x7FFFFFFFFFFFFFFF);
    m_baseTypes.setBitfield63Type(maxBitfield63Type);
    const uint64_t bitfield63Type = m_baseTypes.getBitfield63Type();
    ASSERT_EQ(maxBitfield63Type, bitfield63Type);
}

TEST_F(BaseTypesTest, variableBitfieldType)
{
    const uint64_t maxVariableBitfieldType = std::numeric_limits<uint64_t>::max();
    m_baseTypes.setVariableBitfieldType(maxVariableBitfieldType);
    const uint64_t variableBitfieldType = m_baseTypes.getVariableBitfieldType();
    ASSERT_EQ(maxVariableBitfieldType, variableBitfieldType);
}

TEST_F(BaseTypesTest, variableBitfield8Type)
{
    const uint8_t maxBitfield8Type = std::numeric_limits<uint8_t>::max();
    m_baseTypes.setBitfield8Type(maxBitfield8Type);
    const uint8_t bitfield8Type = m_baseTypes.getBitfield8Type();
    ASSERT_EQ(maxBitfield8Type, bitfield8Type);
}

TEST_F(BaseTypesTest, intfield8Type)
{
    const int8_t maxIntfield8Type = std::numeric_limits<int8_t>::max();
    m_baseTypes.setIntfield8Type(maxIntfield8Type);
    const int8_t intfield8Type = m_baseTypes.getIntfield8Type();
    ASSERT_EQ(maxIntfield8Type, intfield8Type);
}

TEST_F(BaseTypesTest, intfield16Type)
{
    const int16_t maxIntfield16Type = std::numeric_limits<int16_t>::max();
    m_baseTypes.setIntfield16Type(maxIntfield16Type);
    const int16_t intfield16Type = m_baseTypes.getIntfield16Type();
    ASSERT_EQ(maxIntfield16Type, intfield16Type);
}

TEST_F(BaseTypesTest, intfield32Type)
{
    const int32_t maxIntfield32Type = std::numeric_limits<int32_t>::max();
    m_baseTypes.setIntfield32Type(maxIntfield32Type);
    const int32_t intfield32Type = m_baseTypes.getIntfield32Type();
    ASSERT_EQ(maxIntfield32Type, intfield32Type);
}

TEST_F(BaseTypesTest, intfield64Type)
{
    const int64_t maxIntfield64Type = std::numeric_limits<int64_t>::max();
    m_baseTypes.setIntfield64Type(maxIntfield64Type);
    const int64_t intfield64Type = m_baseTypes.getIntfield64Type();
    ASSERT_EQ(maxIntfield64Type, intfield64Type);
}

TEST_F(BaseTypesTest, variableIntfieldType)
{
    const int16_t maxVariableIntfieldType = std::numeric_limits<int16_t>::max();
    m_baseTypes.setVariableIntfieldType(maxVariableIntfieldType);
    const int16_t variableIntfieldType = m_baseTypes.getVariableIntfieldType();
    ASSERT_EQ(maxVariableIntfieldType, variableIntfieldType);

    // this is necessary to check mapping error to int64_t type (must be int16_t)
    // outOfRangeValue cannot be const because of C++11 fires an overflow warning
    int64_t outOfRangeValue = static_cast<int64_t>(maxVariableIntfieldType) + 1;
    m_baseTypes.setVariableIntfieldType(outOfRangeValue);
    const int64_t readOutOfRangeValue = m_baseTypes.getVariableIntfieldType();
    ASSERT_NE(outOfRangeValue, readOutOfRangeValue);
}

TEST_F(BaseTypesTest, variableIntfield8Type)
{
    const int8_t maxIntfield8Type = std::numeric_limits<int8_t>::max();
    m_baseTypes.setIntfield8Type(maxIntfield8Type);
    const int8_t intfield8Type = m_baseTypes.getIntfield8Type();
    ASSERT_EQ(maxIntfield8Type, intfield8Type);
}

TEST_F(BaseTypesTest, float16Type)
{
    const float maxFloat16Type = std::numeric_limits<float>::max();
    m_baseTypes.setFloat16Type(maxFloat16Type);
    const float float16Type = m_baseTypes.getFloat16Type();
    ASSERT_TRUE(maxFloat16Type - float16Type <= std::numeric_limits<float>::epsilon());
}

TEST_F(BaseTypesTest, float32Type)
{
    const float maxFloat32Type = std::numeric_limits<float>::max();
    m_baseTypes.setFloat32Type(maxFloat32Type);
    const float float32Type = m_baseTypes.getFloat32Type();
    ASSERT_TRUE(maxFloat32Type - float32Type <= std::numeric_limits<float>::epsilon());
}

TEST_F(BaseTypesTest, float64Type)
{
    const double maxFloat64Type = std::numeric_limits<double>::max();
    m_baseTypes.setFloat64Type(maxFloat64Type);
    const double float64Type = m_baseTypes.getFloat64Type();
    ASSERT_TRUE(maxFloat64Type - float64Type <= std::numeric_limits<double>::epsilon());
}

TEST_F(BaseTypesTest, varuint16Type)
{
    const uint16_t maxVaruint16Type = (UINT16_C(1) << 15) - 1;
    m_baseTypes.setVaruint16Type(maxVaruint16Type);
    const uint16_t varuint16Type = m_baseTypes.getVaruint16Type();
    ASSERT_EQ(maxVaruint16Type, varuint16Type);
}

TEST_F(BaseTypesTest, varuint32Type)
{
    const uint32_t maxVaruint32Type = (UINT32_C(1) << 29) - 1;
    m_baseTypes.setVaruint32Type(maxVaruint32Type);
    const uint32_t varuint32Type = m_baseTypes.getVaruint32Type();
    ASSERT_EQ(maxVaruint32Type, varuint32Type);
}

TEST_F(BaseTypesTest, varuint64Type)
{
    const uint64_t maxVaruint64Type = (UINT64_C(1) << 57) - 1;
    m_baseTypes.setVaruint64Type(maxVaruint64Type);
    const uint64_t varuint64Type = m_baseTypes.getVaruint64Type();
    ASSERT_EQ(maxVaruint64Type, varuint64Type);
}

TEST_F(BaseTypesTest, varuintTypeMin)
{
    const uint64_t minVaruintType = 0;
    m_baseTypes.setVaruintType(minVaruintType);
    const uint64_t readMinVaruintType = m_baseTypes.getVaruintType();
    ASSERT_EQ(minVaruintType, readMinVaruintType);

    const uint64_t maxVaruintType = UINT64_MAX;
    m_baseTypes.setVaruintType(maxVaruintType);
    const uint64_t readMaxVaruintType = m_baseTypes.getVaruintType();
    ASSERT_EQ(maxVaruintType, readMaxVaruintType);
}

TEST_F(BaseTypesTest, varint16Type)
{
    const int16_t maxVarint16Type = (INT16_C(1) << 14) - 1;
    m_baseTypes.setVarint16Type(maxVarint16Type);
    const int16_t varint16Type = m_baseTypes.getVarint16Type();
    ASSERT_EQ(maxVarint16Type, varint16Type);
}

TEST_F(BaseTypesTest, varint32Type)
{
    const int32_t maxVarint32Type = (INT32_C(1) << 28) - 1;
    m_baseTypes.setVarint32Type(maxVarint32Type);
    const int32_t varint32Type = m_baseTypes.getVarint32Type();
    ASSERT_EQ(maxVarint32Type, varint32Type);
}

TEST_F(BaseTypesTest, varint64Type)
{
    const int64_t maxVarint64Type = (INT64_C(1) << 56) - 1;
    m_baseTypes.setVarint64Type(maxVarint64Type);
    const int64_t varint64Type = m_baseTypes.getVarint64Type();
    ASSERT_EQ(maxVarint64Type, varint64Type);
}

TEST_F(BaseTypesTest, varintTypeMin)
{
    const int64_t minVarintType = INT64_MIN;
    m_baseTypes.setVarintType(minVarintType);
    const int64_t readMinVarintType = m_baseTypes.getVarintType();
    ASSERT_EQ(minVarintType, readMinVarintType);

    const int64_t maxVarintType = INT64_MAX;
    m_baseTypes.setVarintType(maxVarintType);
    const int64_t readMaxVarintType = m_baseTypes.getVarintType();
    ASSERT_EQ(maxVarintType, readMaxVarintType);
}

TEST_F(BaseTypesTest, boolType)
{
    m_baseTypes.setBoolType(true);
    const bool boolType = m_baseTypes.getBoolType();
    ASSERT_EQ(true, boolType);
}

TEST_F(BaseTypesTest, stringType)
{
    const std::string testString("TEST");
    m_baseTypes.setStringType(testString);
    const std::string& stringType = m_baseTypes.getStringType();
    ASSERT_TRUE(stringType.compare(testString) == 0);
}

} // namespace base_types
