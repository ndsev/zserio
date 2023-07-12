#include "gtest/gtest.h"

#include "extended_members/extended_unaligned_field/Original.h"
#include "extended_members/extended_unaligned_field/Extended.h"

#include "zserio/SerializeUtil.h"

namespace extended_members
{
namespace extended_unaligned_field
{

using allocator_type = Extended::allocator_type;

class ExtendedUnalignedFieldTest : public ::testing::Test
{
protected:
    void checkCopyAndMove(const Extended& extended, bool expectedIsPresent)
    {
        auto copiedExtended(extended);
        ASSERT_EQ(expectedIsPresent, copiedExtended.isExtendedValuePresent());
        ASSERT_EQ(extended, copiedExtended);

        auto movedExtended(std::move(copiedExtended));
        ASSERT_EQ(expectedIsPresent, movedExtended.isExtendedValuePresent());
        ASSERT_EQ(extended, movedExtended);

        Extended copiedWithPropagateAllocatorExtended(zserio::PropagateAllocator, extended, allocator_type());
        ASSERT_EQ(expectedIsPresent, copiedWithPropagateAllocatorExtended.isExtendedValuePresent());
        ASSERT_EQ(extended, copiedWithPropagateAllocatorExtended);

        Extended copyAssignedExtended;
        copyAssignedExtended = extended;
        ASSERT_EQ(expectedIsPresent, copyAssignedExtended.isExtendedValuePresent());
        ASSERT_EQ(extended, copyAssignedExtended);

        Extended moveAssignedExtended;
        moveAssignedExtended = copyAssignedExtended;
        ASSERT_EQ(expectedIsPresent, moveAssignedExtended.isExtendedValuePresent());
        ASSERT_EQ(extended, moveAssignedExtended);
    }


    static const size_t ORIGINAL_BIT_SIZE;
    static const size_t EXTENDED_BIT_SIZE;
};

const size_t ExtendedUnalignedFieldTest::ORIGINAL_BIT_SIZE = 3;
const size_t ExtendedUnalignedFieldTest::EXTENDED_BIT_SIZE = zserio::alignTo(8, ORIGINAL_BIT_SIZE) + 8 * 8;

TEST_F(ExtendedUnalignedFieldTest, defaultConstructor)
{
    Extended extended;

    // always present when not read from stream
    ASSERT_TRUE(extended.isExtendedValuePresent());

    // default initialized
    ASSERT_EQ(0, extended.getValue());
    ASSERT_EQ(0, extended.getExtendedValue());
}

TEST_F(ExtendedUnalignedFieldTest, fieldConstructor)
{
    Extended extended(2, UINT64_MAX);
    ASSERT_TRUE(extended.isExtendedValuePresent());

    ASSERT_EQ(2, extended.getValue());
    ASSERT_EQ(UINT64_MAX, extended.getExtendedValue());
}

TEST_F(ExtendedUnalignedFieldTest, operatorEquality)
{
    Extended extended1;
    Extended extended2;
    ASSERT_EQ(extended1, extended2);

    extended1.setValue(2);
    ASSERT_FALSE(extended1 == extended2);
    extended2.setValue(2);
    ASSERT_EQ(extended1, extended2);

    extended2.setExtendedValue(42);
    ASSERT_FALSE(extended1 == extended2);
    extended1.setExtendedValue(42);
    ASSERT_EQ(extended1, extended2);
}

TEST_F(ExtendedUnalignedFieldTest, hashCode)
{
    Extended extended1;
    Extended extended2;
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

TEST_F(ExtendedUnalignedFieldTest, bitSizeOf)
{
    Extended extended;
    ASSERT_EQ(EXTENDED_BIT_SIZE, extended.bitSizeOf());
}

TEST_F(ExtendedUnalignedFieldTest, initializeOffsets)
{
    Extended extended;
    ASSERT_EQ(EXTENDED_BIT_SIZE, extended.initializeOffsets(0));
}

TEST_F(ExtendedUnalignedFieldTest, writeReadExtended)
{
    Extended extended(2, UINT64_MAX);
    auto bitBuffer = zserio::serialize(extended);
    ASSERT_EQ(EXTENDED_BIT_SIZE, bitBuffer.getBitSize());

    auto readExtended = zserio::deserialize<Extended>(bitBuffer);
    ASSERT_TRUE(readExtended.isExtendedValuePresent());
    ASSERT_EQ(extended, readExtended);

    checkCopyAndMove(extended, true);
}

TEST_F(ExtendedUnalignedFieldTest, writeOriginalReadExtended)
{
    Original original(2);
    auto bitBuffer = zserio::serialize(original);
    auto readExtended = zserio::deserialize<Extended>(bitBuffer);
    ASSERT_FALSE(readExtended.isExtendedValuePresent());

    // default constructed
    ASSERT_EQ(0, readExtended.getExtendedValue());

    // bit size as original
    ASSERT_EQ(ORIGINAL_BIT_SIZE, readExtended.bitSizeOf());

    // initialize offsets as original
    ASSERT_EQ(ORIGINAL_BIT_SIZE, readExtended.initializeOffsets(0));

    // writes as original
    bitBuffer = zserio::serialize(readExtended);
    ASSERT_EQ(ORIGINAL_BIT_SIZE, bitBuffer.getBitSize());

    // read original again
    auto readOriginal = zserio::deserialize<Original>(bitBuffer);
    ASSERT_EQ(original, readOriginal);

    checkCopyAndMove(readExtended, false);

    // setter makes the value present!
    readExtended.setExtendedValue(UINT64_MAX);
    ASSERT_TRUE(readExtended.isExtendedValuePresent());

    // bit size as extended
    ASSERT_EQ(EXTENDED_BIT_SIZE, readExtended.bitSizeOf());

    // initialize offsets as extended
    ASSERT_EQ(EXTENDED_BIT_SIZE, readExtended.initializeOffsets(0));

    // write as extended
    bitBuffer = zserio::serialize(readExtended);
    ASSERT_EQ(EXTENDED_BIT_SIZE, bitBuffer.getBitSize());

    checkCopyAndMove(readExtended, true);
}

TEST_F(ExtendedUnalignedFieldTest, writeExtendedReadOriginal)
{
    Extended extended(2, UINT64_MAX);
    auto bitBuffer = zserio::serialize(extended);
    ASSERT_EQ(EXTENDED_BIT_SIZE, bitBuffer.getBitSize());

    zserio::BitStreamReader reader(bitBuffer);
    Original readOriginal(reader);
    ASSERT_EQ(extended.getValue(), readOriginal.getValue());
    ASSERT_EQ(ORIGINAL_BIT_SIZE, reader.getBitPosition());
}

} // namespace extended_unaligned_field
} // namespace extended_members
