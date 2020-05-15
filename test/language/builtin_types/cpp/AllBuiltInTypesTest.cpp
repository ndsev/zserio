#include <limits>

#include "gtest/gtest.h"

#include "builtin_types/all_builtin_types/AllBuiltInTypes.h"
#include "builtin_types/all_builtin_types/ExternalStructure.h"

namespace builtin_types
{
namespace all_builtin_types
{

class AllBuiltInTypesTest : public ::testing::Test
{
protected:
    zserio::BitBuffer getExternalBitBuffer()
    {
        ExternalStructure externalStructure(0xCD, 0x03);
        zserio::BitStreamWriter writer;
        externalStructure.write(writer);
        size_t bufferSize;
        const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

        return zserio::BitBuffer(buffer, writer.getBitPosition());
    }

protected:
    AllBuiltInTypes  m_AllBuiltInTypes;
};

TEST_F(AllBuiltInTypesTest, uint8Type)
{
    const uint8_t maxUint8Type = std::numeric_limits<uint8_t>::max();
    m_AllBuiltInTypes.setUint8Type(maxUint8Type);
    const uint8_t uint8Type = m_AllBuiltInTypes.getUint8Type();
    ASSERT_EQ(maxUint8Type, uint8Type);
}

TEST_F(AllBuiltInTypesTest, uint16Type)
{
    const uint16_t maxUint16Type = std::numeric_limits<uint16_t>::max();
    m_AllBuiltInTypes.setUint16Type(maxUint16Type);
    const uint16_t uint16Type = m_AllBuiltInTypes.getUint16Type();
    ASSERT_EQ(maxUint16Type, uint16Type);
}

TEST_F(AllBuiltInTypesTest, uint32Type)
{
    const uint32_t maxUint32Type = std::numeric_limits<uint32_t>::max();
    m_AllBuiltInTypes.setUint32Type(maxUint32Type);
    const uint32_t uint32Type = m_AllBuiltInTypes.getUint32Type();
    ASSERT_EQ(maxUint32Type, uint32Type);
}

TEST_F(AllBuiltInTypesTest, uint64Type)
{
    const uint64_t maxUint64Type = std::numeric_limits<uint64_t>::max();
    m_AllBuiltInTypes.setUint64Type(maxUint64Type);
    const uint64_t uint64Type = m_AllBuiltInTypes.getUint64Type();
    ASSERT_EQ(maxUint64Type, uint64Type);
}

TEST_F(AllBuiltInTypesTest, int8Type)
{
    const int8_t maxInt8Type = std::numeric_limits<int8_t>::max();
    m_AllBuiltInTypes.setInt8Type(maxInt8Type);
    const int8_t int8Type = m_AllBuiltInTypes.getInt8Type();
    ASSERT_EQ(maxInt8Type, int8Type);
}

TEST_F(AllBuiltInTypesTest, int16Type)
{
    const int16_t maxInt16Type = std::numeric_limits<int16_t>::max();
    m_AllBuiltInTypes.setInt16Type(maxInt16Type);
    const int16_t int16Type = m_AllBuiltInTypes.getInt16Type();
    ASSERT_EQ(maxInt16Type, int16Type);
}

TEST_F(AllBuiltInTypesTest, int32Type)
{
    const int32_t maxInt32Type = std::numeric_limits<int32_t>::max();
    m_AllBuiltInTypes.setInt32Type(maxInt32Type);
    const int32_t int32Type = m_AllBuiltInTypes.getInt32Type();
    ASSERT_EQ(maxInt32Type, int32Type);
}

TEST_F(AllBuiltInTypesTest, int64Type)
{
    const int64_t maxInt64Type = std::numeric_limits<int64_t>::max();
    m_AllBuiltInTypes.setInt64Type(maxInt64Type);
    const int64_t int64Type = m_AllBuiltInTypes.getInt64Type();
    ASSERT_EQ(maxInt64Type, int64Type);
}

TEST_F(AllBuiltInTypesTest, bitfield7Type)
{
    const uint8_t maxBitfield7Type = UINT8_C(0x7F);
    m_AllBuiltInTypes.setBitfield7Type(maxBitfield7Type);
    const uint8_t bitfield7Type = m_AllBuiltInTypes.getBitfield7Type();
    ASSERT_EQ(maxBitfield7Type, bitfield7Type);
}

TEST_F(AllBuiltInTypesTest, bitfield8Type)
{
    const uint8_t maxBitfield8Type = std::numeric_limits<uint8_t>::max();
    m_AllBuiltInTypes.setBitfield8Type(maxBitfield8Type);
    const uint8_t bitfield8Type = m_AllBuiltInTypes.getBitfield8Type();
    ASSERT_EQ(maxBitfield8Type, bitfield8Type);
}

TEST_F(AllBuiltInTypesTest, bitfield15Type)
{
    const uint16_t maxBitfield15Type = UINT16_C(0x7FFF);
    m_AllBuiltInTypes.setBitfield15Type(maxBitfield15Type);
    const uint16_t bitfield15Type = m_AllBuiltInTypes.getBitfield15Type();
    ASSERT_EQ(maxBitfield15Type, bitfield15Type);
}

TEST_F(AllBuiltInTypesTest, bitfield16Type)
{
    const uint16_t maxBitfield16Type = std::numeric_limits<uint16_t>::max();
    m_AllBuiltInTypes.setBitfield16Type(maxBitfield16Type);
    const uint16_t bitfield16Type = m_AllBuiltInTypes.getBitfield16Type();
    ASSERT_EQ(maxBitfield16Type, bitfield16Type);
}

TEST_F(AllBuiltInTypesTest, bitfield31Type)
{
    const uint32_t maxBitfield31Type = UINT32_C(0x7FFFFFFF);
    m_AllBuiltInTypes.setBitfield31Type(maxBitfield31Type);
    const uint32_t bitfield31Type = m_AllBuiltInTypes.getBitfield31Type();
    ASSERT_EQ(maxBitfield31Type, bitfield31Type);
}

TEST_F(AllBuiltInTypesTest, bitfield32Type)
{
    const uint32_t maxBitfield32Type = std::numeric_limits<uint32_t>::max();
    m_AllBuiltInTypes.setBitfield32Type(maxBitfield32Type);
    const uint32_t bitfield32Type = m_AllBuiltInTypes.getBitfield32Type();
    ASSERT_EQ(maxBitfield32Type, bitfield32Type);
}

TEST_F(AllBuiltInTypesTest, bitfield63Type)
{
    const uint64_t maxBitfield63Type = UINT64_C(0x7FFFFFFFFFFFFFFF);
    m_AllBuiltInTypes.setBitfield63Type(maxBitfield63Type);
    const uint64_t bitfield63Type = m_AllBuiltInTypes.getBitfield63Type();
    ASSERT_EQ(maxBitfield63Type, bitfield63Type);
}

TEST_F(AllBuiltInTypesTest, variableBitfieldType)
{
    const uint64_t maxVariableBitfieldType = std::numeric_limits<uint64_t>::max();
    m_AllBuiltInTypes.setVariableBitfieldType(maxVariableBitfieldType);
    const uint64_t variableBitfieldType = m_AllBuiltInTypes.getVariableBitfieldType();
    ASSERT_EQ(maxVariableBitfieldType, variableBitfieldType);
}

TEST_F(AllBuiltInTypesTest, variableBitfield8Type)
{
    const uint8_t maxVariableBitfield8Type = std::numeric_limits<uint8_t>::max();
    m_AllBuiltInTypes.setVariableBitfield8Type(maxVariableBitfield8Type);
    const uint8_t variableBitfield8Type = m_AllBuiltInTypes.getVariableBitfield8Type();
    ASSERT_EQ(maxVariableBitfield8Type, variableBitfield8Type);
}

TEST_F(AllBuiltInTypesTest, intfield8Type)
{
    const int8_t maxIntfield8Type = std::numeric_limits<int8_t>::max();
    m_AllBuiltInTypes.setIntfield8Type(maxIntfield8Type);
    const int8_t intfield8Type = m_AllBuiltInTypes.getIntfield8Type();
    ASSERT_EQ(maxIntfield8Type, intfield8Type);
}

TEST_F(AllBuiltInTypesTest, intfield16Type)
{
    const int16_t maxIntfield16Type = std::numeric_limits<int16_t>::max();
    m_AllBuiltInTypes.setIntfield16Type(maxIntfield16Type);
    const int16_t intfield16Type = m_AllBuiltInTypes.getIntfield16Type();
    ASSERT_EQ(maxIntfield16Type, intfield16Type);
}

TEST_F(AllBuiltInTypesTest, intfield32Type)
{
    const int32_t maxIntfield32Type = std::numeric_limits<int32_t>::max();
    m_AllBuiltInTypes.setIntfield32Type(maxIntfield32Type);
    const int32_t intfield32Type = m_AllBuiltInTypes.getIntfield32Type();
    ASSERT_EQ(maxIntfield32Type, intfield32Type);
}

TEST_F(AllBuiltInTypesTest, intfield64Type)
{
    const int64_t maxIntfield64Type = std::numeric_limits<int64_t>::max();
    m_AllBuiltInTypes.setIntfield64Type(maxIntfield64Type);
    const int64_t intfield64Type = m_AllBuiltInTypes.getIntfield64Type();
    ASSERT_EQ(maxIntfield64Type, intfield64Type);
}

TEST_F(AllBuiltInTypesTest, variableIntfieldType)
{
    const int16_t maxVariableIntfieldType = std::numeric_limits<int16_t>::max();
    m_AllBuiltInTypes.setVariableIntfieldType(maxVariableIntfieldType);
    const int16_t variableIntfieldType = m_AllBuiltInTypes.getVariableIntfieldType();
    ASSERT_EQ(maxVariableIntfieldType, variableIntfieldType);
}

TEST_F(AllBuiltInTypesTest, variableIntfield8Type)
{
    const int8_t maxVariableIntfield8Type = std::numeric_limits<int8_t>::max();
    m_AllBuiltInTypes.setVariableIntfield8Type(maxVariableIntfield8Type);
    const int8_t variableIntfield8Type = m_AllBuiltInTypes.getVariableIntfield8Type();
    ASSERT_EQ(maxVariableIntfield8Type, variableIntfield8Type);
}

TEST_F(AllBuiltInTypesTest, float16Type)
{
    const float maxFloat16Type = std::numeric_limits<float>::max();
    m_AllBuiltInTypes.setFloat16Type(maxFloat16Type);
    const float float16Type = m_AllBuiltInTypes.getFloat16Type();
    ASSERT_TRUE(maxFloat16Type - float16Type <= std::numeric_limits<float>::epsilon());
}

TEST_F(AllBuiltInTypesTest, float32Type)
{
    const float maxFloat32Type = std::numeric_limits<float>::max();
    m_AllBuiltInTypes.setFloat32Type(maxFloat32Type);
    const float float32Type = m_AllBuiltInTypes.getFloat32Type();
    ASSERT_TRUE(maxFloat32Type - float32Type <= std::numeric_limits<float>::epsilon());
}

TEST_F(AllBuiltInTypesTest, float64Type)
{
    const double maxFloat64Type = std::numeric_limits<double>::max();
    m_AllBuiltInTypes.setFloat64Type(maxFloat64Type);
    const double float64Type = m_AllBuiltInTypes.getFloat64Type();
    ASSERT_TRUE(maxFloat64Type - float64Type <= std::numeric_limits<double>::epsilon());
}

TEST_F(AllBuiltInTypesTest, varuint16Type)
{
    const uint16_t maxVaruint16Type = (UINT16_C(1) << 15) - 1;
    m_AllBuiltInTypes.setVaruint16Type(maxVaruint16Type);
    const uint16_t varuint16Type = m_AllBuiltInTypes.getVaruint16Type();
    ASSERT_EQ(maxVaruint16Type, varuint16Type);
}

TEST_F(AllBuiltInTypesTest, varuint32Type)
{
    const uint32_t maxVaruint32Type = (UINT32_C(1) << 29) - 1;
    m_AllBuiltInTypes.setVaruint32Type(maxVaruint32Type);
    const uint32_t varuint32Type = m_AllBuiltInTypes.getVaruint32Type();
    ASSERT_EQ(maxVaruint32Type, varuint32Type);
}

TEST_F(AllBuiltInTypesTest, varuint64Type)
{
    const uint64_t maxVaruint64Type = (UINT64_C(1) << 57) - 1;
    m_AllBuiltInTypes.setVaruint64Type(maxVaruint64Type);
    const uint64_t varuint64Type = m_AllBuiltInTypes.getVaruint64Type();
    ASSERT_EQ(maxVaruint64Type, varuint64Type);
}

TEST_F(AllBuiltInTypesTest, varuintTypeMin)
{
    const uint64_t minVaruintType = 0;
    m_AllBuiltInTypes.setVaruintType(minVaruintType);
    const uint64_t readMinVaruintType = m_AllBuiltInTypes.getVaruintType();
    ASSERT_EQ(minVaruintType, readMinVaruintType);

    const uint64_t maxVaruintType = UINT64_MAX;
    m_AllBuiltInTypes.setVaruintType(maxVaruintType);
    const uint64_t readMaxVaruintType = m_AllBuiltInTypes.getVaruintType();
    ASSERT_EQ(maxVaruintType, readMaxVaruintType);
}

TEST_F(AllBuiltInTypesTest, varint16Type)
{
    const int16_t maxVarint16Type = (INT16_C(1) << 14) - 1;
    m_AllBuiltInTypes.setVarint16Type(maxVarint16Type);
    const int16_t varint16Type = m_AllBuiltInTypes.getVarint16Type();
    ASSERT_EQ(maxVarint16Type, varint16Type);
}

TEST_F(AllBuiltInTypesTest, varint32Type)
{
    const int32_t maxVarint32Type = (INT32_C(1) << 28) - 1;
    m_AllBuiltInTypes.setVarint32Type(maxVarint32Type);
    const int32_t varint32Type = m_AllBuiltInTypes.getVarint32Type();
    ASSERT_EQ(maxVarint32Type, varint32Type);
}

TEST_F(AllBuiltInTypesTest, varint64Type)
{
    const int64_t maxVarint64Type = (INT64_C(1) << 56) - 1;
    m_AllBuiltInTypes.setVarint64Type(maxVarint64Type);
    const int64_t varint64Type = m_AllBuiltInTypes.getVarint64Type();
    ASSERT_EQ(maxVarint64Type, varint64Type);
}

TEST_F(AllBuiltInTypesTest, varintTypeMin)
{
    const int64_t minVarintType = INT64_MIN;
    m_AllBuiltInTypes.setVarintType(minVarintType);
    const int64_t readMinVarintType = m_AllBuiltInTypes.getVarintType();
    ASSERT_EQ(minVarintType, readMinVarintType);

    const int64_t maxVarintType = INT64_MAX;
    m_AllBuiltInTypes.setVarintType(maxVarintType);
    const int64_t readMaxVarintType = m_AllBuiltInTypes.getVarintType();
    ASSERT_EQ(maxVarintType, readMaxVarintType);
}

TEST_F(AllBuiltInTypesTest, boolType)
{
    m_AllBuiltInTypes.setBoolType(true);
    const bool boolType = m_AllBuiltInTypes.getBoolType();
    ASSERT_EQ(true, boolType);
}

TEST_F(AllBuiltInTypesTest, stringType)
{
    const std::string testString("TEST");
    m_AllBuiltInTypes.setStringType(testString);
    const std::string& stringType = m_AllBuiltInTypes.getStringType();
    ASSERT_TRUE(stringType.compare(testString) == 0);
}

TEST_F(AllBuiltInTypesTest, externType)
{
    const zserio::BitBuffer testExtern = getExternalBitBuffer();
    m_AllBuiltInTypes.setExternType(testExtern);
    const zserio::BitBuffer& externType = m_AllBuiltInTypes.getExternType();
    ASSERT_EQ(testExtern, externType);
}

TEST_F(AllBuiltInTypesTest, bitSizeOf)
{
    m_AllBuiltInTypes.setBoolType(true);
    m_AllBuiltInTypes.setUint8Type(1);
    m_AllBuiltInTypes.setUint16Type(std::numeric_limits<uint16_t>::max());
    m_AllBuiltInTypes.setUint32Type(std::numeric_limits<uint32_t>::max());
    m_AllBuiltInTypes.setUint64Type(std::numeric_limits<uint64_t>::max());
    m_AllBuiltInTypes.setInt8Type(std::numeric_limits<int8_t>::max());
    m_AllBuiltInTypes.setInt16Type(std::numeric_limits<int16_t>::max());
    m_AllBuiltInTypes.setInt32Type(std::numeric_limits<int32_t>::max());
    m_AllBuiltInTypes.setInt64Type(std::numeric_limits<int64_t>::max());
    m_AllBuiltInTypes.setBitfield7Type(UINT8_C(0x7F));
    m_AllBuiltInTypes.setBitfield8Type(std::numeric_limits<uint8_t>::max());
    m_AllBuiltInTypes.setBitfield15Type(UINT16_C(0x7FFF));
    m_AllBuiltInTypes.setBitfield16Type(std::numeric_limits<uint16_t>::max());
    m_AllBuiltInTypes.setBitfield31Type(UINT32_C(0x7FFFFFFF));
    m_AllBuiltInTypes.setBitfield32Type(std::numeric_limits<uint32_t>::max());
    m_AllBuiltInTypes.setBitfield63Type(UINT64_C(0x7FFFFFFFFFFFFFFF));
    m_AllBuiltInTypes.setVariableBitfieldType(1);
    m_AllBuiltInTypes.setVariableBitfield8Type(std::numeric_limits<uint8_t>::max());
    m_AllBuiltInTypes.setIntfield8Type(std::numeric_limits<int8_t>::max());
    m_AllBuiltInTypes.setIntfield16Type(std::numeric_limits<int16_t>::max());
    m_AllBuiltInTypes.setIntfield32Type(std::numeric_limits<int32_t>::max());
    m_AllBuiltInTypes.setIntfield64Type(std::numeric_limits<int64_t>::max());
    m_AllBuiltInTypes.setVariableIntfieldType(1);
    m_AllBuiltInTypes.setVariableIntfield8Type(std::numeric_limits<int8_t>::max());
    m_AllBuiltInTypes.setFloat16Type(std::numeric_limits<float>::max());
    m_AllBuiltInTypes.setFloat32Type(std::numeric_limits<float>::max());
    m_AllBuiltInTypes.setFloat64Type(std::numeric_limits<double>::max());
    m_AllBuiltInTypes.setVaruint16Type((UINT16_C(1) << 15) - 1);
    m_AllBuiltInTypes.setVaruint32Type((UINT32_C(1) << 29) - 1);
    m_AllBuiltInTypes.setVaruint64Type((UINT64_C(1) << 57) - 1);
    m_AllBuiltInTypes.setVaruintType(UINT64_MAX);
    m_AllBuiltInTypes.setVarint16Type((INT16_C(1) << 14) - 1);
    m_AllBuiltInTypes.setVarint32Type((INT32_C(1) << 28) - 1);
    m_AllBuiltInTypes.setVarint64Type((INT64_C(1) << 56) - 1);
    m_AllBuiltInTypes.setVarintType(INT64_MAX);
    m_AllBuiltInTypes.setStringType("TEST");
    m_AllBuiltInTypes.setExternType(getExternalBitBuffer());
    const size_t expectedBitSizeOf = 1102;
    ASSERT_EQ(expectedBitSizeOf, m_AllBuiltInTypes.bitSizeOf());
}

TEST_F(AllBuiltInTypesTest, readWrite)
{
    m_AllBuiltInTypes.setBoolType(true);
    m_AllBuiltInTypes.setUint8Type(8);
    m_AllBuiltInTypes.setUint16Type(std::numeric_limits<uint16_t>::max());
    m_AllBuiltInTypes.setUint32Type(std::numeric_limits<uint32_t>::max());
    m_AllBuiltInTypes.setUint64Type(std::numeric_limits<uint64_t>::max());
    m_AllBuiltInTypes.setInt8Type(std::numeric_limits<int8_t>::max());
    m_AllBuiltInTypes.setInt16Type(std::numeric_limits<int16_t>::max());
    m_AllBuiltInTypes.setInt32Type(std::numeric_limits<int32_t>::max());
    m_AllBuiltInTypes.setInt64Type(std::numeric_limits<int64_t>::max());
    m_AllBuiltInTypes.setBitfield7Type(UINT8_C(0x7F));
    m_AllBuiltInTypes.setBitfield8Type(std::numeric_limits<uint8_t>::max());
    m_AllBuiltInTypes.setBitfield15Type(UINT16_C(0x7FFF));
    m_AllBuiltInTypes.setBitfield16Type(std::numeric_limits<uint16_t>::max());
    m_AllBuiltInTypes.setBitfield31Type(UINT32_C(0x7FFFFFFF));
    m_AllBuiltInTypes.setBitfield32Type(std::numeric_limits<uint32_t>::max());
    m_AllBuiltInTypes.setBitfield63Type(UINT64_C(0x7FFFFFFFFFFFFFFF));
    m_AllBuiltInTypes.setVariableBitfieldType(std::numeric_limits<uint8_t>::max());
    m_AllBuiltInTypes.setVariableBitfield8Type(std::numeric_limits<uint8_t>::max());
    m_AllBuiltInTypes.setIntfield8Type(std::numeric_limits<int8_t>::max());
    m_AllBuiltInTypes.setIntfield16Type(std::numeric_limits<int16_t>::max());
    m_AllBuiltInTypes.setIntfield32Type(std::numeric_limits<int32_t>::max());
    m_AllBuiltInTypes.setIntfield64Type(std::numeric_limits<int64_t>::max());
    m_AllBuiltInTypes.setVariableIntfieldType(std::numeric_limits<int8_t>::max());
    m_AllBuiltInTypes.setVariableIntfield8Type(std::numeric_limits<int8_t>::max());
    m_AllBuiltInTypes.setFloat16Type(1.0f);
    m_AllBuiltInTypes.setFloat32Type(std::numeric_limits<float>::max());
    m_AllBuiltInTypes.setFloat64Type(std::numeric_limits<double>::max());
    m_AllBuiltInTypes.setVaruint16Type((UINT16_C(1) << 15) - 1);
    m_AllBuiltInTypes.setVaruint32Type((UINT32_C(1) << 29) - 1);
    m_AllBuiltInTypes.setVaruint64Type((UINT64_C(1) << 57) - 1);
    m_AllBuiltInTypes.setVaruintType(UINT64_MAX);
    m_AllBuiltInTypes.setVarint16Type((INT16_C(1) << 14) - 1);
    m_AllBuiltInTypes.setVarint32Type((INT32_C(1) << 28) - 1);
    m_AllBuiltInTypes.setVarint64Type((INT64_C(1) << 56) - 1);
    m_AllBuiltInTypes.setVarintType(INT64_MAX);
    m_AllBuiltInTypes.setStringType("TEST");
    m_AllBuiltInTypes.setExternType(getExternalBitBuffer());

    zserio::BitStreamWriter writer;
    m_AllBuiltInTypes.write(writer);
    size_t bufferSize;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    const AllBuiltInTypes readAllBuiltInTypes(reader);

    ASSERT_TRUE(m_AllBuiltInTypes == readAllBuiltInTypes);
}

} // namespace all_builtin_types
} // namespace builtin_types
