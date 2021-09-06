#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"
#include "zserio/PackingContext.h"

#include "parameterized_types/compound_and_field_with_same_param/CompoundReadTest.h"
#include "parameterized_types/compound_and_field_with_same_param/CompoundPackingTest.h"

namespace parameterized_types
{
namespace compound_and_field_with_same_param
{

class ParameterizedTypesCompoundAndFieldWithSameParamTest : public ::testing::Test
{
protected:
    void writeCompoundReadToStream(zserio::BitStreamWriter& writer)
    {
        writer.writeBits(FIELD1, 32);
        writer.writeBits(FIELD2, 32);
    }

    void writeCompoundPackingToStream(zserio::BitStreamWriter& writer)
    {
        writer.writeBits(FIELD1, 32);
        writer.writeBits(FIELD2, 32);
        writer.writeBits(FIELD3, 32);
    }

    static const int PARAM;
    static const uint32_t FIELD1;
    static const uint32_t FIELD2;
    static const uint32_t FIELD3;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

// used int to cause selection of field constructor instead of read constructor
// in case that field constructor is not properly guarded by enable_if
const int ParameterizedTypesCompoundAndFieldWithSameParamTest::PARAM = 10;
const uint32_t ParameterizedTypesCompoundAndFieldWithSameParamTest::FIELD1 = 1;
const uint32_t ParameterizedTypesCompoundAndFieldWithSameParamTest::FIELD2 = 9;
const uint32_t ParameterizedTypesCompoundAndFieldWithSameParamTest::FIELD3 = 5;

TEST_F(ParameterizedTypesCompoundAndFieldWithSameParamTest, bitStreamReaderConstructor)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writeCompoundReadToStream(writer);

    {
        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        CompoundRead compoundRead = CompoundRead(reader, PARAM);
        ASSERT_EQ(compoundRead.getField1().getValue(), FIELD1);
        ASSERT_EQ(compoundRead.getField2().getValue(), FIELD2);
    }

    {
        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        CompoundReadTest compoundReadTest = CompoundReadTest(reader);
        ASSERT_EQ(PARAM, compoundReadTest.getCompoundRead().getParam());
        ASSERT_EQ(FIELD1, compoundReadTest.getCompoundRead().getField1().getValue());
        ASSERT_EQ(FIELD2, compoundReadTest.getCompoundRead().getField2().getValue());
    }
}

TEST_F(ParameterizedTypesCompoundAndFieldWithSameParamTest, packingContextConstructor)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writeCompoundPackingToStream(writer);

    {
        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        const std::allocator<uint8_t> allocator = std::allocator<uint8_t>();
        zserio::PackingContextNode contextNode(allocator);
        CompoundPacking::createPackingContext(contextNode);
        CompoundPacking compoundPacking = CompoundPacking(contextNode, reader, PARAM);
        ASSERT_EQ(compoundPacking.getField1().getValue(), FIELD1);
        ASSERT_EQ(compoundPacking.getField2().getValue(), FIELD2);
        ASSERT_EQ(compoundPacking.getField3().getValue(), FIELD3);
    }

    {
        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        const std::allocator<uint8_t> allocator = std::allocator<uint8_t>();
        zserio::PackingContextNode contextNode(allocator);
        CompoundPackingTest::createPackingContext(contextNode);
        CompoundPackingTest compoundPackingTest = CompoundPackingTest(contextNode, reader);
        ASSERT_EQ(PARAM, compoundPackingTest.getCompoundPacking().getParam());
        ASSERT_EQ(FIELD1, compoundPackingTest.getCompoundPacking().getField1().getValue());
        ASSERT_EQ(FIELD2, compoundPackingTest.getCompoundPacking().getField2().getValue());
        ASSERT_EQ(FIELD3, compoundPackingTest.getCompoundPacking().getField3().getValue());
    }
}

} // namespace compound_and_field_with_same_param
} // namespace parameterized_types
