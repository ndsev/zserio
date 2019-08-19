#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

#include "parameterized_types/compound_and_field_with_same_param/SameParamTest.h"

namespace parameterized_types
{
namespace compound_and_field_with_same_param
{

class ParameterizedTypesCompoundAndFieldWithSameParamTest : public ::testing::Test
{
protected:
    void writeToByteArray(zserio::BitStreamWriter& writer)
    {
        writer.writeBits(FIELD1, 32);
        writer.writeBits(FIELD2, 32);
    }

    static const int PARAM;
    static const uint32_t FIELD1;
    static const uint32_t FIELD2;

    static const size_t BIT_SIZE;
};

// used int to cause selection of field constructor instead of read constructor
// in case that field constructor is not properly guarded by enable_if
const int ParameterizedTypesCompoundAndFieldWithSameParamTest::PARAM = 10;
const uint32_t ParameterizedTypesCompoundAndFieldWithSameParamTest::FIELD1 = 1;
const uint32_t ParameterizedTypesCompoundAndFieldWithSameParamTest::FIELD2 = 9;

const size_t ParameterizedTypesCompoundAndFieldWithSameParamTest::BIT_SIZE = 32 + 2 * 32;

TEST_F(ParameterizedTypesCompoundAndFieldWithSameParamTest, bitStreamReaderConstructor)
{
    zserio::BitStreamWriter writer;
    writeToByteArray(writer);

    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    {
        zserio::BitStreamReader reader(buffer, bufferSize);
        Compound compound = Compound(reader, PARAM);
        ASSERT_EQ(compound.getField1().getValue(), FIELD1);
        ASSERT_EQ(compound.getField2().getValue(), FIELD2);
    }

    {
        zserio::BitStreamReader reader(buffer, bufferSize);
        SameParamTest sameParamTest = SameParamTest(reader);
        ASSERT_EQ(PARAM, sameParamTest.getCompound().getParam());
        ASSERT_EQ(FIELD1, sameParamTest.getCompound().getField1().getValue());
        ASSERT_EQ(FIELD2, sameParamTest.getCompound().getField2().getValue());
    }
}

} // namespace compound_and_field_with_same_param
} // namespace parameterized_types
