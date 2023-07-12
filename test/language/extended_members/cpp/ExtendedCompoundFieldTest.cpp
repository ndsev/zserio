#include "gtest/gtest.h"

#include "extended_members/extended_compound_field/Original.h"
#include "extended_members/extended_compound_field/Extended.h"

#include "zserio/SerializeUtil.h"
#include "zserio/BitSizeOfCalculator.h"
#include "zserio/SizeConvertUtil.h"

namespace extended_members
{
namespace extended_compound_field
{

using allocator_type = Extended::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class ExtendedCompoundFieldTest : public ::testing::Test
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

    static const vector_type<uint32_t> COMPOUND_ARRAY;

    static const size_t ORIGINAL_BIT_SIZE;
    static const size_t EXTENDED_BIT_SIZE_DEFAULT;
    static const size_t EXTENDED_BIT_SIZE_WITH_ARRAY;
};

const vector_type<uint32_t> ExtendedCompoundFieldTest::COMPOUND_ARRAY = { 0, 1, 2, 3, 4 };

const size_t ExtendedCompoundFieldTest::ORIGINAL_BIT_SIZE = 4 * 8;
const size_t ExtendedCompoundFieldTest::EXTENDED_BIT_SIZE_DEFAULT = 4 * 8 + zserio::bitSizeOfVarSize(0);
const size_t ExtendedCompoundFieldTest::EXTENDED_BIT_SIZE_WITH_ARRAY = ORIGINAL_BIT_SIZE +
        zserio::bitSizeOfVarSize(zserio::convertSizeToUInt32(COMPOUND_ARRAY.size())) +
        COMPOUND_ARRAY.size() * 4 * 8;

TEST_F(ExtendedCompoundFieldTest, defaultConstructor)
{
    Extended extended;

    // always present when not read from stream
    ASSERT_TRUE(extended.isExtendedValuePresent());

    // default initialized
    ASSERT_EQ(0, extended.getValue());
    ASSERT_EQ(0, extended.getExtendedValue().getArray().size());
}

TEST_F(ExtendedCompoundFieldTest, fieldConstructor)
{
    Extended extended(42, Compound(COMPOUND_ARRAY));
    ASSERT_TRUE(extended.isExtendedValuePresent());

    ASSERT_EQ(42, extended.getValue());
    ASSERT_EQ(COMPOUND_ARRAY, extended.getExtendedValue().getArray());
}

TEST_F(ExtendedCompoundFieldTest, operatorEquality)
{
    Extended extended1;
    Extended extended2;
    ASSERT_EQ(extended1, extended2);

    extended1.setValue(13);
    ASSERT_FALSE(extended1 == extended2);
    extended2.setValue(13);
    ASSERT_EQ(extended1, extended2);

    extended2.setExtendedValue(Compound(COMPOUND_ARRAY));
    ASSERT_FALSE(extended1 == extended2);
    extended1.setExtendedValue(Compound(COMPOUND_ARRAY));
    ASSERT_EQ(extended1, extended2);
}

TEST_F(ExtendedCompoundFieldTest, hashCode)
{
    Extended extended1;
    Extended extended2;
    ASSERT_EQ(extended1.hashCode(), extended2.hashCode());

    extended1.setValue(13);
    ASSERT_NE(extended1.hashCode(), extended2.hashCode());
    extended2.setValue(13);
    ASSERT_EQ(extended1.hashCode(), extended2.hashCode());

    extended2.setExtendedValue(Compound(COMPOUND_ARRAY));
    ASSERT_NE(extended1.hashCode(), extended2.hashCode());
    extended1.setExtendedValue(Compound(COMPOUND_ARRAY));
    ASSERT_EQ(extended1.hashCode(), extended2.hashCode());
}

TEST_F(ExtendedCompoundFieldTest, bitSizeOf)
{
    Extended extended;
    ASSERT_EQ(EXTENDED_BIT_SIZE_DEFAULT, extended.bitSizeOf());
}

TEST_F(ExtendedCompoundFieldTest, initializeOffsets)
{
    Extended extended;
    ASSERT_EQ(EXTENDED_BIT_SIZE_DEFAULT, extended.initializeOffsets(0));
}

TEST_F(ExtendedCompoundFieldTest, writeReadExtended)
{
    Extended extended(42, Compound(COMPOUND_ARRAY));
    auto bitBuffer = zserio::serialize(extended);
    ASSERT_EQ(EXTENDED_BIT_SIZE_WITH_ARRAY, bitBuffer.getBitSize());

    auto readExtended = zserio::deserialize<Extended>(bitBuffer);
    ASSERT_TRUE(readExtended.isExtendedValuePresent());
    ASSERT_EQ(extended, readExtended);

    checkCopyAndMove(extended, true);
    checkCopyAndMove(readExtended, true);
}

TEST_F(ExtendedCompoundFieldTest, writeOriginalReadExtended)
{
    Original original(42);
    auto bitBuffer = zserio::serialize(original);
    auto readExtended = zserio::deserialize<Extended>(bitBuffer);
    ASSERT_FALSE(readExtended.isExtendedValuePresent());

    // extended value is default constructed
    ASSERT_EQ(Compound(), readExtended.getExtendedValue());

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
    readExtended.setExtendedValue(Compound(COMPOUND_ARRAY));
    ASSERT_TRUE(readExtended.isExtendedValuePresent());

    // bit size as extended
    ASSERT_EQ(EXTENDED_BIT_SIZE_WITH_ARRAY, readExtended.bitSizeOf());

    // initialize offsets as extended
    ASSERT_EQ(EXTENDED_BIT_SIZE_WITH_ARRAY, readExtended.initializeOffsets(0));

    // write as extended
    bitBuffer = zserio::serialize(readExtended);
    ASSERT_EQ(EXTENDED_BIT_SIZE_WITH_ARRAY, bitBuffer.getBitSize());

    checkCopyAndMove(readExtended, true);
}

TEST_F(ExtendedCompoundFieldTest, writeExtendedReadOriginal)
{
    Extended extended(42, Compound(COMPOUND_ARRAY));
    auto bitBuffer = zserio::serialize(extended);
    ASSERT_EQ(EXTENDED_BIT_SIZE_WITH_ARRAY, bitBuffer.getBitSize());

    zserio::BitStreamReader reader(bitBuffer);
    Original readOriginal(reader);
    ASSERT_EQ(extended.getValue(), readOriginal.getValue());
    ASSERT_EQ(ORIGINAL_BIT_SIZE, reader.getBitPosition());
}

} // namespace extended_compound_field
} // namespace extended_members
