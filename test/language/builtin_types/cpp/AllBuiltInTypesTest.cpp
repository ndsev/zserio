#include <limits>

#include "gtest/gtest.h"

#include "builtin_types/all_builtin_types/AllBuiltInTypes.h"
#include "builtin_types/all_builtin_types/ExternalStructure.h"

#include "zserio/RebindAlloc.h"
#include "zserio/SerializeUtil.h"

namespace builtin_types
{
namespace all_builtin_types
{

using allocator_type = AllBuiltInTypes::allocator_type;
using string_type = zserio::string<zserio::RebindAlloc<allocator_type, char>>;
template <typename T>
using vector_type = std::vector<T, zserio::RebindAlloc<allocator_type, T>>;

using BitBuffer = zserio::BasicBitBuffer<zserio::RebindAlloc<allocator_type, uint8_t>>;

class AllBuiltInTypesTest : public ::testing::Test
{
protected:
    BitBuffer getExternalBitBuffer()
    {
        ExternalStructure externalStructure(0xCD, 0x03);
        BitBuffer bitBuffer = BitBuffer(externalStructure.bitSizeOf());
        zserio::BitStreamWriter writer(bitBuffer);
        externalStructure.write(writer);

        return bitBuffer;
    }

protected:
    AllBuiltInTypes  m_allBuiltInTypes;

    static const std::string BLOB_NAME;
};

const std::string AllBuiltInTypesTest::BLOB_NAME = "language/builtin_types/all_builtin_types.blob";

TEST_F(AllBuiltInTypesTest, uint8Type)
{
    const uint8_t maxUint8Type = std::numeric_limits<uint8_t>::max();
    m_allBuiltInTypes.setUint8Type(maxUint8Type);
    const uint8_t uint8Type = m_allBuiltInTypes.getUint8Type();
    ASSERT_EQ(maxUint8Type, uint8Type);
}

TEST_F(AllBuiltInTypesTest, uint16Type)
{
    const uint16_t maxUint16Type = std::numeric_limits<uint16_t>::max();
    m_allBuiltInTypes.setUint16Type(maxUint16Type);
    const uint16_t uint16Type = m_allBuiltInTypes.getUint16Type();
    ASSERT_EQ(maxUint16Type, uint16Type);
}

TEST_F(AllBuiltInTypesTest, uint32Type)
{
    const uint32_t maxUint32Type = std::numeric_limits<uint32_t>::max();
    m_allBuiltInTypes.setUint32Type(maxUint32Type);
    const uint32_t uint32Type = m_allBuiltInTypes.getUint32Type();
    ASSERT_EQ(maxUint32Type, uint32Type);
}

TEST_F(AllBuiltInTypesTest, uint64Type)
{
    const uint64_t maxUint64Type = std::numeric_limits<uint64_t>::max();
    m_allBuiltInTypes.setUint64Type(maxUint64Type);
    const uint64_t uint64Type = m_allBuiltInTypes.getUint64Type();
    ASSERT_EQ(maxUint64Type, uint64Type);
}

TEST_F(AllBuiltInTypesTest, int8Type)
{
    const int8_t maxInt8Type = std::numeric_limits<int8_t>::max();
    m_allBuiltInTypes.setInt8Type(maxInt8Type);
    const int8_t int8Type = m_allBuiltInTypes.getInt8Type();
    ASSERT_EQ(maxInt8Type, int8Type);
}

TEST_F(AllBuiltInTypesTest, int16Type)
{
    const int16_t maxInt16Type = std::numeric_limits<int16_t>::max();
    m_allBuiltInTypes.setInt16Type(maxInt16Type);
    const int16_t int16Type = m_allBuiltInTypes.getInt16Type();
    ASSERT_EQ(maxInt16Type, int16Type);
}

TEST_F(AllBuiltInTypesTest, int32Type)
{
    const int32_t maxInt32Type = std::numeric_limits<int32_t>::max();
    m_allBuiltInTypes.setInt32Type(maxInt32Type);
    const int32_t int32Type = m_allBuiltInTypes.getInt32Type();
    ASSERT_EQ(maxInt32Type, int32Type);
}

TEST_F(AllBuiltInTypesTest, int64Type)
{
    const int64_t maxInt64Type = std::numeric_limits<int64_t>::max();
    m_allBuiltInTypes.setInt64Type(maxInt64Type);
    const int64_t int64Type = m_allBuiltInTypes.getInt64Type();
    ASSERT_EQ(maxInt64Type, int64Type);
}

TEST_F(AllBuiltInTypesTest, bitfield7Type)
{
    const uint8_t maxBitfield7Type = UINT8_C(0x7F);
    m_allBuiltInTypes.setBitfield7Type(maxBitfield7Type);
    const uint8_t bitfield7Type = m_allBuiltInTypes.getBitfield7Type();
    ASSERT_EQ(maxBitfield7Type, bitfield7Type);
}

TEST_F(AllBuiltInTypesTest, bitfield8Type)
{
    const uint8_t maxBitfield8Type = std::numeric_limits<uint8_t>::max();
    m_allBuiltInTypes.setBitfield8Type(maxBitfield8Type);
    const uint8_t bitfield8Type = m_allBuiltInTypes.getBitfield8Type();
    ASSERT_EQ(maxBitfield8Type, bitfield8Type);
}

TEST_F(AllBuiltInTypesTest, bitfield15Type)
{
    const uint16_t maxBitfield15Type = UINT16_C(0x7FFF);
    m_allBuiltInTypes.setBitfield15Type(maxBitfield15Type);
    const uint16_t bitfield15Type = m_allBuiltInTypes.getBitfield15Type();
    ASSERT_EQ(maxBitfield15Type, bitfield15Type);
}

TEST_F(AllBuiltInTypesTest, bitfield16Type)
{
    const uint16_t maxBitfield16Type = std::numeric_limits<uint16_t>::max();
    m_allBuiltInTypes.setBitfield16Type(maxBitfield16Type);
    const uint16_t bitfield16Type = m_allBuiltInTypes.getBitfield16Type();
    ASSERT_EQ(maxBitfield16Type, bitfield16Type);
}

TEST_F(AllBuiltInTypesTest, bitfield31Type)
{
    const uint32_t maxBitfield31Type = UINT32_C(0x7FFFFFFF);
    m_allBuiltInTypes.setBitfield31Type(maxBitfield31Type);
    const uint32_t bitfield31Type = m_allBuiltInTypes.getBitfield31Type();
    ASSERT_EQ(maxBitfield31Type, bitfield31Type);
}

TEST_F(AllBuiltInTypesTest, bitfield32Type)
{
    const uint32_t maxBitfield32Type = std::numeric_limits<uint32_t>::max();
    m_allBuiltInTypes.setBitfield32Type(maxBitfield32Type);
    const uint32_t bitfield32Type = m_allBuiltInTypes.getBitfield32Type();
    ASSERT_EQ(maxBitfield32Type, bitfield32Type);
}

TEST_F(AllBuiltInTypesTest, bitfield63Type)
{
    const uint64_t maxBitfield63Type = UINT64_C(0x7FFFFFFFFFFFFFFF);
    m_allBuiltInTypes.setBitfield63Type(maxBitfield63Type);
    const uint64_t bitfield63Type = m_allBuiltInTypes.getBitfield63Type();
    ASSERT_EQ(maxBitfield63Type, bitfield63Type);
}

TEST_F(AllBuiltInTypesTest, variableBitfieldType)
{
    const uint64_t maxVariableBitfieldType = std::numeric_limits<uint64_t>::max();
    m_allBuiltInTypes.setVariableBitfieldType(maxVariableBitfieldType);
    const uint64_t variableBitfieldType = m_allBuiltInTypes.getVariableBitfieldType();
    ASSERT_EQ(maxVariableBitfieldType, variableBitfieldType);
}

TEST_F(AllBuiltInTypesTest, variableBitfield8Type)
{
    const uint8_t maxVariableBitfield8Type = std::numeric_limits<uint8_t>::max();
    m_allBuiltInTypes.setVariableBitfield8Type(maxVariableBitfield8Type);
    const uint8_t variableBitfield8Type = m_allBuiltInTypes.getVariableBitfield8Type();
    ASSERT_EQ(maxVariableBitfield8Type, variableBitfield8Type);
}

TEST_F(AllBuiltInTypesTest, intfield8Type)
{
    const int8_t maxIntfield8Type = std::numeric_limits<int8_t>::max();
    m_allBuiltInTypes.setIntfield8Type(maxIntfield8Type);
    const int8_t intfield8Type = m_allBuiltInTypes.getIntfield8Type();
    ASSERT_EQ(maxIntfield8Type, intfield8Type);
}

TEST_F(AllBuiltInTypesTest, intfield16Type)
{
    const int16_t maxIntfield16Type = std::numeric_limits<int16_t>::max();
    m_allBuiltInTypes.setIntfield16Type(maxIntfield16Type);
    const int16_t intfield16Type = m_allBuiltInTypes.getIntfield16Type();
    ASSERT_EQ(maxIntfield16Type, intfield16Type);
}

TEST_F(AllBuiltInTypesTest, intfield32Type)
{
    const int32_t maxIntfield32Type = std::numeric_limits<int32_t>::max();
    m_allBuiltInTypes.setIntfield32Type(maxIntfield32Type);
    const int32_t intfield32Type = m_allBuiltInTypes.getIntfield32Type();
    ASSERT_EQ(maxIntfield32Type, intfield32Type);
}

TEST_F(AllBuiltInTypesTest, intfield64Type)
{
    const int64_t maxIntfield64Type = std::numeric_limits<int64_t>::max();
    m_allBuiltInTypes.setIntfield64Type(maxIntfield64Type);
    const int64_t intfield64Type = m_allBuiltInTypes.getIntfield64Type();
    ASSERT_EQ(maxIntfield64Type, intfield64Type);
}

TEST_F(AllBuiltInTypesTest, variableIntfieldType)
{
    const int16_t variableIntfieldTypeMax = INT16_C((1 << 13) - 1);
    m_allBuiltInTypes.setVariableIntfieldType(variableIntfieldTypeMax);
    const int16_t variableIntfieldType = m_allBuiltInTypes.getVariableIntfieldType();
    ASSERT_EQ(variableIntfieldTypeMax, variableIntfieldType);
}

TEST_F(AllBuiltInTypesTest, variableIntfield8Type)
{
    const int8_t maxVariableIntfield8Type = std::numeric_limits<int8_t>::max();
    m_allBuiltInTypes.setVariableIntfield8Type(maxVariableIntfield8Type);
    const int8_t variableIntfield8Type = m_allBuiltInTypes.getVariableIntfield8Type();
    ASSERT_EQ(maxVariableIntfield8Type, variableIntfield8Type);
}

TEST_F(AllBuiltInTypesTest, float16Type)
{
    const float maxFloat16Type = std::numeric_limits<float>::max();
    m_allBuiltInTypes.setFloat16Type(maxFloat16Type);
    const float float16Type = m_allBuiltInTypes.getFloat16Type();
    ASSERT_TRUE(maxFloat16Type - float16Type <= std::numeric_limits<float>::epsilon());
}

TEST_F(AllBuiltInTypesTest, float32Type)
{
    const float maxFloat32Type = std::numeric_limits<float>::max();
    m_allBuiltInTypes.setFloat32Type(maxFloat32Type);
    const float float32Type = m_allBuiltInTypes.getFloat32Type();
    ASSERT_TRUE(maxFloat32Type - float32Type <= std::numeric_limits<float>::epsilon());
}

TEST_F(AllBuiltInTypesTest, float64Type)
{
    const double maxFloat64Type = std::numeric_limits<double>::max();
    m_allBuiltInTypes.setFloat64Type(maxFloat64Type);
    const double float64Type = m_allBuiltInTypes.getFloat64Type();
    ASSERT_TRUE(maxFloat64Type - float64Type <= std::numeric_limits<double>::epsilon());
}

TEST_F(AllBuiltInTypesTest, varuint16Type)
{
    const uint16_t maxVaruint16Type = (UINT16_C(1) << 15) - 1;
    m_allBuiltInTypes.setVaruint16Type(maxVaruint16Type);
    const uint16_t varuint16Type = m_allBuiltInTypes.getVaruint16Type();
    ASSERT_EQ(maxVaruint16Type, varuint16Type);
}

TEST_F(AllBuiltInTypesTest, varuint32Type)
{
    const uint32_t maxVaruint32Type = (UINT32_C(1) << 29) - 1;
    m_allBuiltInTypes.setVaruint32Type(maxVaruint32Type);
    const uint32_t varuint32Type = m_allBuiltInTypes.getVaruint32Type();
    ASSERT_EQ(maxVaruint32Type, varuint32Type);
}

TEST_F(AllBuiltInTypesTest, varuint64Type)
{
    const uint64_t maxVaruint64Type = (UINT64_C(1) << 57) - 1;
    m_allBuiltInTypes.setVaruint64Type(maxVaruint64Type);
    const uint64_t varuint64Type = m_allBuiltInTypes.getVaruint64Type();
    ASSERT_EQ(maxVaruint64Type, varuint64Type);
}

TEST_F(AllBuiltInTypesTest, varuintType)
{
    const uint64_t minVaruintType = 0;
    m_allBuiltInTypes.setVaruintType(minVaruintType);
    const uint64_t readMinVaruintType = m_allBuiltInTypes.getVaruintType();
    ASSERT_EQ(minVaruintType, readMinVaruintType);

    const uint64_t maxVaruintType = UINT64_MAX;
    m_allBuiltInTypes.setVaruintType(maxVaruintType);
    const uint64_t readMaxVaruintType = m_allBuiltInTypes.getVaruintType();
    ASSERT_EQ(maxVaruintType, readMaxVaruintType);
}

TEST_F(AllBuiltInTypesTest, varsizeType)
{
    const uint32_t maxVarSizeType = (UINT32_C(1) << 31) - 1;
    m_allBuiltInTypes.setVarsizeType(maxVarSizeType);
    const uint32_t varsizeType = m_allBuiltInTypes.getVarsizeType();
    ASSERT_EQ(maxVarSizeType, varsizeType);
}

TEST_F(AllBuiltInTypesTest, varint16Type)
{
    const int16_t maxVarint16Type = (INT16_C(1) << 14) - 1;
    m_allBuiltInTypes.setVarint16Type(maxVarint16Type);
    const int16_t varint16Type = m_allBuiltInTypes.getVarint16Type();
    ASSERT_EQ(maxVarint16Type, varint16Type);
}

TEST_F(AllBuiltInTypesTest, varint32Type)
{
    const int32_t maxVarint32Type = (INT32_C(1) << 28) - 1;
    m_allBuiltInTypes.setVarint32Type(maxVarint32Type);
    const int32_t varint32Type = m_allBuiltInTypes.getVarint32Type();
    ASSERT_EQ(maxVarint32Type, varint32Type);
}

TEST_F(AllBuiltInTypesTest, varint64Type)
{
    const int64_t maxVarint64Type = (INT64_C(1) << 56) - 1;
    m_allBuiltInTypes.setVarint64Type(maxVarint64Type);
    const int64_t varint64Type = m_allBuiltInTypes.getVarint64Type();
    ASSERT_EQ(maxVarint64Type, varint64Type);
}

TEST_F(AllBuiltInTypesTest, varintType)
{
    const int64_t minVarintType = INT64_MIN;
    m_allBuiltInTypes.setVarintType(minVarintType);
    const int64_t readMinVarintType = m_allBuiltInTypes.getVarintType();
    ASSERT_EQ(minVarintType, readMinVarintType);

    const int64_t maxVarintType = INT64_MAX;
    m_allBuiltInTypes.setVarintType(maxVarintType);
    const int64_t readMaxVarintType = m_allBuiltInTypes.getVarintType();
    ASSERT_EQ(maxVarintType, readMaxVarintType);
}

TEST_F(AllBuiltInTypesTest, boolType)
{
    m_allBuiltInTypes.setBoolType(true);
    const bool boolType = m_allBuiltInTypes.getBoolType();
    ASSERT_EQ(true, boolType);
}

TEST_F(AllBuiltInTypesTest, stringType)
{
    const string_type testString("TEST");
    m_allBuiltInTypes.setStringType(testString);
    const string_type& stringType = m_allBuiltInTypes.getStringType();
    ASSERT_TRUE(stringType.compare(testString) == 0);
}

TEST_F(AllBuiltInTypesTest, externType)
{
    const BitBuffer testExtern = getExternalBitBuffer();
    m_allBuiltInTypes.setExternType(testExtern);
    const BitBuffer& externType = m_allBuiltInTypes.getExternType();
    ASSERT_EQ(testExtern, externType);
}

TEST_F(AllBuiltInTypesTest, bitSizeOf)
{
    m_allBuiltInTypes.setBoolType(true);
    m_allBuiltInTypes.setUint8Type(1);
    m_allBuiltInTypes.setUint16Type(std::numeric_limits<uint16_t>::max());
    m_allBuiltInTypes.setUint32Type(std::numeric_limits<uint32_t>::max());
    m_allBuiltInTypes.setUint64Type(10);
    m_allBuiltInTypes.setInt8Type(std::numeric_limits<int8_t>::max());
    m_allBuiltInTypes.setInt16Type(std::numeric_limits<int16_t>::max());
    m_allBuiltInTypes.setInt32Type(std::numeric_limits<int32_t>::max());
    m_allBuiltInTypes.setInt64Type(std::numeric_limits<int64_t>::max());
    m_allBuiltInTypes.setBitfield7Type(UINT8_C(0x7F));
    m_allBuiltInTypes.setBitfield8Type(std::numeric_limits<uint8_t>::max());
    m_allBuiltInTypes.setBitfield15Type(UINT16_C(0x7FFF));
    m_allBuiltInTypes.setBitfield16Type(std::numeric_limits<uint16_t>::max());
    m_allBuiltInTypes.setBitfield31Type(UINT32_C(0x7FFFFFFF));
    m_allBuiltInTypes.setBitfield32Type(std::numeric_limits<uint32_t>::max());
    m_allBuiltInTypes.setBitfield63Type(UINT64_C(0x7FFFFFFFFFFFFFFF));
    m_allBuiltInTypes.setVariableBitfieldType(1);
    m_allBuiltInTypes.setVariableBitfield8Type(std::numeric_limits<uint8_t>::max());
    m_allBuiltInTypes.setIntfield8Type(std::numeric_limits<int8_t>::max());
    m_allBuiltInTypes.setIntfield16Type(std::numeric_limits<int16_t>::max());
    m_allBuiltInTypes.setIntfield32Type(std::numeric_limits<int32_t>::max());
    m_allBuiltInTypes.setIntfield64Type(std::numeric_limits<int64_t>::max());
    const int16_t variableIntfieldTypeMax = INT16_C((1 << 13) - 1);
    m_allBuiltInTypes.setVariableIntfieldType(variableIntfieldTypeMax);
    m_allBuiltInTypes.setVariableIntfield8Type(std::numeric_limits<int8_t>::max());
    m_allBuiltInTypes.setFloat16Type(std::numeric_limits<float>::max());
    m_allBuiltInTypes.setFloat32Type(std::numeric_limits<float>::max());
    m_allBuiltInTypes.setFloat64Type(std::numeric_limits<double>::max());
    m_allBuiltInTypes.setVaruint16Type((UINT16_C(1) << 15) - 1);
    m_allBuiltInTypes.setVaruint32Type((UINT32_C(1) << 29) - 1);
    m_allBuiltInTypes.setVaruint64Type((UINT64_C(1) << 57) - 1);
    m_allBuiltInTypes.setVaruintType(std::numeric_limits<uint64_t>::max());
    m_allBuiltInTypes.setVarsizeType((UINT32_C(1) << 31) - 1);
    m_allBuiltInTypes.setVarint16Type((INT16_C(1) << 14) - 1);
    m_allBuiltInTypes.setVarint32Type((INT32_C(1) << 28) - 1);
    m_allBuiltInTypes.setVarint64Type((INT64_C(1) << 56) - 1);
    m_allBuiltInTypes.setVarintType(std::numeric_limits<int64_t>::max());
    m_allBuiltInTypes.setStringType("TEST");
    m_allBuiltInTypes.setExternType(getExternalBitBuffer());
    const size_t expectedBitSizeOf = 1142;
    ASSERT_EQ(expectedBitSizeOf, m_allBuiltInTypes.bitSizeOf());
}

TEST_F(AllBuiltInTypesTest, readWrite)
{
    m_allBuiltInTypes.setBoolType(true);
    m_allBuiltInTypes.setUint8Type(8);
    m_allBuiltInTypes.setUint16Type(std::numeric_limits<uint16_t>::max());
    m_allBuiltInTypes.setUint32Type(std::numeric_limits<uint32_t>::max());
    m_allBuiltInTypes.setUint64Type(std::numeric_limits<uint64_t>::max());
    m_allBuiltInTypes.setInt8Type(std::numeric_limits<int8_t>::max());
    m_allBuiltInTypes.setInt16Type(std::numeric_limits<int16_t>::max());
    m_allBuiltInTypes.setInt32Type(std::numeric_limits<int32_t>::max());
    m_allBuiltInTypes.setInt64Type(std::numeric_limits<int64_t>::max());
    m_allBuiltInTypes.setBitfield7Type(UINT8_C(0x7F));
    m_allBuiltInTypes.setBitfield8Type(std::numeric_limits<uint8_t>::max());
    m_allBuiltInTypes.setBitfield15Type(UINT16_C(0x7FFF));
    m_allBuiltInTypes.setBitfield16Type(std::numeric_limits<uint16_t>::max());
    m_allBuiltInTypes.setBitfield31Type(UINT32_C(0x7FFFFFFF));
    m_allBuiltInTypes.setBitfield32Type(std::numeric_limits<uint32_t>::max());
    m_allBuiltInTypes.setBitfield63Type(UINT64_C(0x7FFFFFFFFFFFFFFF));
    m_allBuiltInTypes.setVariableBitfieldType(std::numeric_limits<uint8_t>::max());
    m_allBuiltInTypes.setVariableBitfield8Type(std::numeric_limits<uint8_t>::max());
    m_allBuiltInTypes.setIntfield8Type(std::numeric_limits<int8_t>::max());
    m_allBuiltInTypes.setIntfield16Type(std::numeric_limits<int16_t>::max());
    m_allBuiltInTypes.setIntfield32Type(std::numeric_limits<int32_t>::max());
    m_allBuiltInTypes.setIntfield64Type(std::numeric_limits<int64_t>::max());
    const int16_t variableIntfieldTypeMax = INT16_C((1 << 13) - 1);
    m_allBuiltInTypes.setVariableIntfieldType(variableIntfieldTypeMax);
    m_allBuiltInTypes.setVariableIntfield8Type(std::numeric_limits<int8_t>::max());
    m_allBuiltInTypes.setFloat16Type(1.0f);
    m_allBuiltInTypes.setFloat32Type(std::numeric_limits<float>::max());
    m_allBuiltInTypes.setFloat64Type(std::numeric_limits<double>::max());
    m_allBuiltInTypes.setVaruint16Type((UINT16_C(1) << 15) - 1);
    m_allBuiltInTypes.setVaruint32Type((UINT32_C(1) << 29) - 1);
    m_allBuiltInTypes.setVaruint64Type((UINT64_C(1) << 57) - 1);
    m_allBuiltInTypes.setVaruintType(std::numeric_limits<uint64_t>::max());
    m_allBuiltInTypes.setVarsizeType((UINT32_C(1) << 31) - 1);
    m_allBuiltInTypes.setVarint16Type((INT16_C(1) << 14) - 1);
    m_allBuiltInTypes.setVarint32Type((INT32_C(1) << 28) - 1);
    m_allBuiltInTypes.setVarint64Type((INT64_C(1) << 56) - 1);
    m_allBuiltInTypes.setVarintType(std::numeric_limits<int64_t>::max());
    m_allBuiltInTypes.setStringType("TEST");
    m_allBuiltInTypes.setExternType(getExternalBitBuffer());

    zserio::serializeToFile(m_allBuiltInTypes, BLOB_NAME);

    const AllBuiltInTypes readAllBuiltInTypes = zserio::deserializeFromFile<AllBuiltInTypes>(BLOB_NAME);
    ASSERT_TRUE(m_allBuiltInTypes == readAllBuiltInTypes);
}

} // namespace all_builtin_types
} // namespace builtin_types
