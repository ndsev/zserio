#include "extended_members/extended_field_in_template/ExtendedCompound.h"
#include "extended_members/extended_field_in_template/ExtendedSimple.h"
#include "extended_members/extended_field_in_template/Original.h"
#include "gtest/gtest.h"
#include "zserio/SerializeUtil.h"

namespace extended_members
{
namespace extended_field_in_template
{

using allocator_type = ExtendedCompound::allocator_type;

class ExtendedFieldInTemplateTest : public ::testing::Test
{
protected:
    template <typename EXTENDED>
    void checkCopyAndMove(const EXTENDED& extended, bool expectedIsPresent)
    {
        auto copiedExtended(extended);
        ASSERT_EQ(expectedIsPresent, copiedExtended.isExtendedValuePresent());
        ASSERT_EQ(extended, copiedExtended);

        auto movedExtended(std::move(copiedExtended));
        ASSERT_EQ(expectedIsPresent, movedExtended.isExtendedValuePresent());
        ASSERT_EQ(extended, movedExtended);

        EXTENDED copiedWithPropagateAllocatorExtended(zserio::PropagateAllocator, extended, allocator_type());
        ASSERT_EQ(expectedIsPresent, copiedWithPropagateAllocatorExtended.isExtendedValuePresent());
        ASSERT_EQ(extended, copiedWithPropagateAllocatorExtended);

        EXTENDED copyAssignedExtended;
        copyAssignedExtended = extended;
        ASSERT_EQ(expectedIsPresent, copyAssignedExtended.isExtendedValuePresent());
        ASSERT_EQ(extended, copyAssignedExtended);

        EXTENDED moveAssignedExtended;
        moveAssignedExtended = copyAssignedExtended;
        ASSERT_EQ(expectedIsPresent, moveAssignedExtended.isExtendedValuePresent());
        ASSERT_EQ(extended, moveAssignedExtended);
    }

    static const size_t ORIGINAL_BIT_SIZE;
    static const size_t EXTENDED_BIT_SIZE;
};

const size_t ExtendedFieldInTemplateTest::ORIGINAL_BIT_SIZE = 4 * 8;
const size_t ExtendedFieldInTemplateTest::EXTENDED_BIT_SIZE = ORIGINAL_BIT_SIZE + 4 * 8;

TEST_F(ExtendedFieldInTemplateTest, defaultConstructorSimple)
{
    ExtendedSimple extended;

    // always present when not read from stream
    ASSERT_TRUE(extended.isExtendedValuePresent());

    // default initialized
    ASSERT_EQ(0, extended.getValue());
    ASSERT_EQ(0, extended.getExtendedValue());
}

TEST_F(ExtendedFieldInTemplateTest, defaultConstructorCompound)
{
    ExtendedCompound extended;

    // always present when not read from stream
    ASSERT_TRUE(extended.isExtendedValuePresent());

    // default initialized
    ASSERT_EQ(0, extended.getValue());
    ASSERT_EQ(Compound(), extended.getExtendedValue());
}

TEST_F(ExtendedFieldInTemplateTest, fieldConstructorSimple)
{
    ExtendedSimple extended(42, UINT32_MAX);
    ASSERT_TRUE(extended.isExtendedValuePresent());

    ASSERT_EQ(42, extended.getValue());
    ASSERT_EQ(UINT32_MAX, extended.getExtendedValue());
}

TEST_F(ExtendedFieldInTemplateTest, fieldConstructorCompound)
{
    ExtendedCompound extended(42, Compound(UINT32_MAX));
    ASSERT_TRUE(extended.isExtendedValuePresent());

    ASSERT_EQ(42, extended.getValue());
    ASSERT_EQ(UINT32_MAX, extended.getExtendedValue().getField());
}

TEST_F(ExtendedFieldInTemplateTest, operatorEqualitySimple)
{
    ExtendedSimple extended1;
    ExtendedSimple extended2;
    ASSERT_EQ(extended1, extended2);

    extended1.setValue(13);
    ASSERT_FALSE(extended1 == extended2);
    extended2.setValue(13);
    ASSERT_EQ(extended1, extended2);

    extended2.setExtendedValue(UINT32_MAX);
    ASSERT_FALSE(extended1 == extended2);
    extended1.setExtendedValue(UINT32_MAX);
    ASSERT_EQ(extended1, extended2);
}

TEST_F(ExtendedFieldInTemplateTest, operatorLessThanSimple)
{
    ExtendedSimple extended1;
    ExtendedSimple extended2;
    ASSERT_FALSE(extended1 < extended2);
    ASSERT_FALSE(extended2 < extended1);

    extended1.setValue(13);
    ASSERT_FALSE(extended1 < extended2);
    ASSERT_TRUE(extended2 < extended1);

    extended2.setValue(13);
    ASSERT_FALSE(extended1 < extended2);
    ASSERT_FALSE(extended2 < extended1);

    extended2.setExtendedValue(UINT32_MAX);
    ASSERT_TRUE(extended1 < extended2);
    ASSERT_FALSE(extended2 < extended1);

    extended1.setExtendedValue(UINT32_MAX);
    ASSERT_FALSE(extended1 < extended2);
    ASSERT_FALSE(extended2 < extended1);
}

TEST_F(ExtendedFieldInTemplateTest, operatorEqualityCompound)
{
    ExtendedCompound extended1;
    ExtendedCompound extended2;
    ASSERT_EQ(extended1, extended2);

    extended1.setValue(13);
    ASSERT_FALSE(extended1 == extended2);
    extended2.setValue(13);
    ASSERT_EQ(extended1, extended2);

    extended2.setExtendedValue(Compound(UINT32_MAX));
    ASSERT_FALSE(extended1 == extended2);
    extended1.setExtendedValue(Compound(UINT32_MAX));
    ASSERT_EQ(extended1, extended2);
}

TEST_F(ExtendedFieldInTemplateTest, operatorLessThanCompound)
{
    ExtendedCompound extended1;
    ExtendedCompound extended2;
    ASSERT_FALSE(extended1 < extended2);
    ASSERT_FALSE(extended2 < extended1);

    extended1.setValue(13);
    ASSERT_FALSE(extended1 < extended2);
    ASSERT_TRUE(extended2 < extended1);

    extended2.setValue(13);
    ASSERT_FALSE(extended1 < extended2);
    ASSERT_FALSE(extended2 < extended1);

    extended2.setExtendedValue(Compound(UINT32_MAX));
    ASSERT_TRUE(extended1 < extended2);
    ASSERT_FALSE(extended2 < extended1);

    extended1.setExtendedValue(Compound(UINT32_MAX));
    ASSERT_FALSE(extended1 < extended2);
    ASSERT_FALSE(extended2 < extended1);
}

TEST_F(ExtendedFieldInTemplateTest, hashCodeSimple)
{
    ExtendedSimple extended1;
    ExtendedSimple extended2;
    ASSERT_EQ(extended1.hashCode(), extended2.hashCode());

    extended1.setValue(13);
    ASSERT_NE(extended1.hashCode(), extended2.hashCode());
    extended2.setValue(13);
    ASSERT_EQ(extended1.hashCode(), extended2.hashCode());

    extended2.setExtendedValue(42);
    ASSERT_NE(extended1.hashCode(), extended2.hashCode());
    extended1.setExtendedValue(42);
    ASSERT_EQ(extended1.hashCode(), extended2.hashCode());
}

TEST_F(ExtendedFieldInTemplateTest, hashCodeCompound)
{
    ExtendedCompound extended1;
    ExtendedCompound extended2;
    ASSERT_EQ(extended1.hashCode(), extended2.hashCode());

    extended1.setValue(13);
    ASSERT_NE(extended1.hashCode(), extended2.hashCode());
    extended2.setValue(13);
    ASSERT_EQ(extended1.hashCode(), extended2.hashCode());

    extended2.setExtendedValue(Compound(42));
    ASSERT_NE(extended1.hashCode(), extended2.hashCode());
    extended1.setExtendedValue(Compound(42));
    ASSERT_EQ(extended1.hashCode(), extended2.hashCode());
}

TEST_F(ExtendedFieldInTemplateTest, bitSizeOfSimple)
{
    ExtendedSimple extended;
    ASSERT_EQ(EXTENDED_BIT_SIZE, extended.bitSizeOf());
}

TEST_F(ExtendedFieldInTemplateTest, bitSizeOfCompound)
{
    ExtendedCompound extended;
    ASSERT_EQ(EXTENDED_BIT_SIZE, extended.bitSizeOf());
}

TEST_F(ExtendedFieldInTemplateTest, initializeOffsetsSimple)
{
    ExtendedSimple extended;
    ASSERT_EQ(EXTENDED_BIT_SIZE, extended.initializeOffsets(0));
}

TEST_F(ExtendedFieldInTemplateTest, initializeOffsetsCompound)
{
    ExtendedCompound extended;
    ASSERT_EQ(EXTENDED_BIT_SIZE, extended.initializeOffsets(0));
}

TEST_F(ExtendedFieldInTemplateTest, writeReadExtendedSimple)
{
    ExtendedSimple extended(42, UINT32_MAX);
    auto bitBuffer = zserio::serialize(extended);
    ASSERT_EQ(EXTENDED_BIT_SIZE, bitBuffer.getBitSize());

    auto readExtended = zserio::deserialize<ExtendedSimple>(bitBuffer);
    ASSERT_TRUE(readExtended.isExtendedValuePresent());
    ASSERT_EQ(extended, readExtended);

    checkCopyAndMove(extended, true);
}

TEST_F(ExtendedFieldInTemplateTest, writeReadExtendedCompound)
{
    ExtendedCompound extended(42, Compound(UINT32_MAX));
    auto bitBuffer = zserio::serialize(extended);
    ASSERT_EQ(EXTENDED_BIT_SIZE, bitBuffer.getBitSize());

    auto readExtended = zserio::deserialize<ExtendedCompound>(bitBuffer);
    ASSERT_TRUE(readExtended.isExtendedValuePresent());
    ASSERT_EQ(extended, readExtended);

    checkCopyAndMove(extended, true);
}

TEST_F(ExtendedFieldInTemplateTest, writeOriginalReadExtendedSimple)
{
    Original original(42);
    auto bitBuffer = zserio::serialize(original);
    auto readExtended = zserio::deserialize<ExtendedSimple>(bitBuffer);
    ASSERT_FALSE(readExtended.isExtendedValuePresent());

    // extended value is default constructed
    ASSERT_EQ(0, readExtended.getExtendedValue());

    // bit size as original
    ASSERT_EQ(ORIGINAL_BIT_SIZE, readExtended.bitSizeOf());

    // initialize offsets as original
    ASSERT_EQ(ORIGINAL_BIT_SIZE, readExtended.initializeOffsets(0));

    // write as original
    bitBuffer = zserio::serialize(readExtended);
    ASSERT_EQ(ORIGINAL_BIT_SIZE, bitBuffer.getBitSize());

    // read original again
    auto readOriginal = zserio::deserialize<Original>(bitBuffer);
    ASSERT_EQ(original, readOriginal);

    checkCopyAndMove(readExtended, false);

    // setter makes the value present!
    readExtended.setExtendedValue(UINT32_MAX);
    ASSERT_TRUE(readExtended.isExtendedValuePresent());

    // bit size as extended
    ASSERT_EQ(EXTENDED_BIT_SIZE, readExtended.bitSizeOf());

    // initialize offsets as extended
    ASSERT_EQ(EXTENDED_BIT_SIZE, readExtended.initializeOffsets(0));

    // writes as extended
    bitBuffer = zserio::serialize(readExtended);
    ASSERT_EQ(EXTENDED_BIT_SIZE, bitBuffer.getBitSize());

    checkCopyAndMove(readExtended, true);
}

TEST_F(ExtendedFieldInTemplateTest, writeOriginalReadExtendedCompound)
{
    Original original(42);
    auto bitBuffer = zserio::serialize(original);
    auto readExtended = zserio::deserialize<ExtendedCompound>(bitBuffer);
    ASSERT_FALSE(readExtended.isExtendedValuePresent());

    // extended value is default constructed
    ASSERT_EQ(Compound(), readExtended.getExtendedValue());

    // bit size as original
    ASSERT_EQ(ORIGINAL_BIT_SIZE, readExtended.bitSizeOf());

    // initialize offsets as original
    ASSERT_EQ(ORIGINAL_BIT_SIZE, readExtended.initializeOffsets(0));

    // write as original
    bitBuffer = zserio::serialize(readExtended);
    ASSERT_EQ(ORIGINAL_BIT_SIZE, bitBuffer.getBitSize());

    // read original again
    auto readOriginal = zserio::deserialize<Original>(bitBuffer);
    ASSERT_EQ(original, readOriginal);

    checkCopyAndMove(readExtended, false);

    // setter makes the value present!
    readExtended.setExtendedValue(Compound(UINT32_MAX));
    ASSERT_TRUE(readExtended.isExtendedValuePresent());

    // bit size as extended
    ASSERT_EQ(EXTENDED_BIT_SIZE, readExtended.bitSizeOf());

    // initialize offsets as extended
    ASSERT_EQ(EXTENDED_BIT_SIZE, readExtended.initializeOffsets(0));

    // writes as extended
    bitBuffer = zserio::serialize(readExtended);
    ASSERT_EQ(EXTENDED_BIT_SIZE, bitBuffer.getBitSize());

    checkCopyAndMove(readExtended, true);
}

TEST_F(ExtendedFieldInTemplateTest, writeExtendedSimpleReadOriginal)
{
    ExtendedSimple extended(42, UINT32_MAX);
    auto bitBuffer = zserio::serialize(extended);
    ASSERT_EQ(EXTENDED_BIT_SIZE, bitBuffer.getBitSize());

    zserio::BitStreamReader reader(bitBuffer);
    Original readOriginal(reader);
    ASSERT_EQ(extended.getValue(), readOriginal.getValue());
    ASSERT_EQ(ORIGINAL_BIT_SIZE, reader.getBitPosition());
}

TEST_F(ExtendedFieldInTemplateTest, writeExtendedCompoundReadOriginal)
{
    ExtendedCompound extended(42, Compound(UINT32_MAX));
    auto bitBuffer = zserio::serialize(extended);
    ASSERT_EQ(EXTENDED_BIT_SIZE, bitBuffer.getBitSize());

    zserio::BitStreamReader reader(bitBuffer);
    Original readOriginal(reader);
    ASSERT_EQ(extended.getValue(), readOriginal.getValue());
    ASSERT_EQ(ORIGINAL_BIT_SIZE, reader.getBitPosition());
}

} // namespace extended_field_in_template
} // namespace extended_members
