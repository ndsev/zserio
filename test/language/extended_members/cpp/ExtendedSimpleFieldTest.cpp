#include "gtest/gtest.h"

#include "extended_members/extended_simple_field/Original.h"
#include "extended_members/extended_simple_field/Extended.h"

#include "zserio/SerializeUtil.h"

namespace extended_members
{
namespace extended_simple_field
{

using allocator_type = Extended::allocator_type;

class ExtendedSimpleFieldTest : public ::testing::Test
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

const size_t ExtendedSimpleFieldTest::ORIGINAL_BIT_SIZE = 4 * 8;
const size_t ExtendedSimpleFieldTest::EXTENDED_BIT_SIZE = ORIGINAL_BIT_SIZE + 8 * 8;

TEST_F(ExtendedSimpleFieldTest, defaultConstructor)
{
    Extended extended;

    // always present when not read from stream
    ASSERT_TRUE(extended.isExtendedValuePresent());

    // default initialized
    ASSERT_EQ(0, extended.getValue());
    ASSERT_EQ(0, extended.getExtendedValue());
}

TEST_F(ExtendedSimpleFieldTest, fieldConstructor)
{
    Extended extended(42, UINT64_MAX);
    ASSERT_TRUE(extended.isExtendedValuePresent());

    ASSERT_EQ(42, extended.getValue());
    ASSERT_EQ(UINT64_MAX, extended.getExtendedValue());
}

TEST_F(ExtendedSimpleFieldTest, operatorEquality)
{
    Extended extended1;
    Extended extended2;
    ASSERT_EQ(extended1, extended2);

    extended1.setValue(13);
    ASSERT_FALSE(extended1 == extended2);
    extended2.setValue(13);
    ASSERT_EQ(extended1, extended2);

    extended2.setExtendedValue(UINT64_MAX);
    ASSERT_FALSE(extended1 == extended2);
    extended1.setExtendedValue(UINT64_MAX);
    ASSERT_EQ(extended1, extended2);
}

TEST_F(ExtendedSimpleFieldTest, hashCode)
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

TEST_F(ExtendedSimpleFieldTest, bitSizeOf)
{
    Extended extended;
    ASSERT_EQ(EXTENDED_BIT_SIZE, extended.bitSizeOf());
}

TEST_F(ExtendedSimpleFieldTest, initializeOffsets)
{
    Extended extended;
    ASSERT_EQ(EXTENDED_BIT_SIZE, extended.initializeOffsets(0));
}

TEST_F(ExtendedSimpleFieldTest, writeReadExtended)
{
    Extended extended(42, UINT64_MAX);
    auto bitBuffer = zserio::serialize(extended);
    ASSERT_EQ(EXTENDED_BIT_SIZE, bitBuffer.getBitSize());

    auto readExtended = zserio::deserialize<Extended>(bitBuffer);
    ASSERT_TRUE(readExtended.isExtendedValuePresent());
    ASSERT_EQ(extended, readExtended);

    checkCopyAndMove(extended, true);
}

TEST_F(ExtendedSimpleFieldTest, writeOriginalReadExtened)
{
    Original original(42);
    auto bitBuffer = zserio::serialize(original);
    auto readExtended = zserio::deserialize<Extended>(bitBuffer);
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
    readExtended.setExtendedValue(UINT64_MAX);
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

TEST_F(ExtendedSimpleFieldTest, writeExtendedReadOriginal)
{
    Extended extended(42, UINT64_MAX);
    auto bitBuffer = zserio::serialize(extended);
    ASSERT_EQ(EXTENDED_BIT_SIZE, bitBuffer.getBitSize());

    zserio::BitStreamReader reader(bitBuffer);
    Original readOriginal(reader);
    ASSERT_EQ(extended.getValue(), readOriginal.getValue());
    ASSERT_EQ(ORIGINAL_BIT_SIZE, reader.getBitPosition());
}

} // namespace extended_simple_field
} // namespace extended_members
