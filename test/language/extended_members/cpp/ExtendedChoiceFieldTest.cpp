#include "extended_members/extended_choice_field/Extended.h"
#include "extended_members/extended_choice_field/Original.h"
#include "gtest/gtest.h"
#include "zserio/BitSizeOfCalculator.h"
#include "zserio/SerializeUtil.h"
#include "zserio/SizeConvertUtil.h"

namespace extended_members
{
namespace extended_choice_field
{

using allocator_type = Extended::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class ExtendedChoiceFieldTest : public ::testing::Test
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

        // TODO[Mi-L@]: cannot do allocator propagating copy on uninitialized choice!
        if (extended.getExtendedValue().isInitialized())
        {
            Extended copiedWithPropagateAllocatorExtended(
                    zserio::PropagateAllocator, extended, allocator_type());
            ASSERT_EQ(expectedIsPresent, copiedWithPropagateAllocatorExtended.isExtendedValuePresent());
            ASSERT_EQ(extended, copiedWithPropagateAllocatorExtended);
        }

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
    static const size_t EXTENDED_BIT_SIZE_EMPTY;
    static const size_t EXTENDED_BIT_SIZE_VALUE;

    static const vector_type<uint32_t> VALUES;
    static const size_t EXTENDED_BIT_SIZE_VALUES;
};

const size_t ExtendedChoiceFieldTest::ORIGINAL_BIT_SIZE = 4 * 8;
const size_t ExtendedChoiceFieldTest::EXTENDED_BIT_SIZE_EMPTY = ORIGINAL_BIT_SIZE;
const size_t ExtendedChoiceFieldTest::EXTENDED_BIT_SIZE_VALUE = ORIGINAL_BIT_SIZE + 4 * 8;

const vector_type<uint32_t> ExtendedChoiceFieldTest::VALUES = {0, 1, 2, 3, 4};
const size_t ExtendedChoiceFieldTest::EXTENDED_BIT_SIZE_VALUES = ORIGINAL_BIT_SIZE + VALUES.size() * 4 * 8;

TEST_F(ExtendedChoiceFieldTest, defaultConstructor)
{
    Extended extended;

    // always present when not read from stream
    ASSERT_TRUE(extended.isExtendedValuePresent());

    // default initialized
    ASSERT_EQ(0, extended.getNumElements());

    extended.initializeChildren();
    ASSERT_EQ(Choice::UNDEFINED_CHOICE, extended.getExtendedValue().choiceTag());
    ASSERT_EQ(0, extended.getExtendedValue().getNumElements());
}

TEST_F(ExtendedChoiceFieldTest, fieldConstructor)
{
    Extended extended(1, Choice());
    extended.getExtendedValue().setValue(42);

    ASSERT_TRUE(extended.isExtendedValuePresent());

    ASSERT_EQ(1, extended.getNumElements());

    extended.initializeChildren();
    ASSERT_EQ(Choice::CHOICE_value, extended.getExtendedValue().choiceTag());
    ASSERT_EQ(42, extended.getExtendedValue().getValue());
}

TEST_F(ExtendedChoiceFieldTest, operatorEquality)
{
    Extended extended1;
    Extended extended2;
    extended1.initializeChildren();
    extended2.initializeChildren();
    ASSERT_EQ(extended1, extended2);

    // do not re-initialize children until the choice is properly set in setExtendedValue
    extended1.setNumElements(1);
    ASSERT_FALSE(extended1 == extended2);
    extended2.setNumElements(1);
    ASSERT_EQ(extended1, extended2);

    Choice extendedValue;
    extendedValue.setValue(42);
    extended2.setExtendedValue(extendedValue);
    extended2.initializeChildren();
    ASSERT_FALSE(extended1 == extended2);
    extended1.setExtendedValue(extendedValue);
    extended1.initializeChildren();
    ASSERT_EQ(extended1, extended2);
}

TEST_F(ExtendedChoiceFieldTest, operatorLessThan)
{
    Extended extended1;
    Extended extended2;
    extended1.initializeChildren();
    extended2.initializeChildren();
    ASSERT_FALSE(extended1 < extended2);
    ASSERT_FALSE(extended2 < extended1);

    extended1.setNumElements(1);
    ASSERT_FALSE(extended1 < extended2);
    ASSERT_TRUE(extended2 < extended1);

    extended2.setNumElements(1);
    ASSERT_FALSE(extended1 < extended2);
    ASSERT_FALSE(extended2 < extended1);

    Choice extendedValue;
    extendedValue.setValue(42);
    extended2.setExtendedValue(extendedValue);
    extended2.initializeChildren();
    ASSERT_TRUE(extended1 < extended2);
    ASSERT_FALSE(extended2 < extended1);
    extended1.setExtendedValue(extendedValue);
    extended1.initializeChildren();
    ASSERT_FALSE(extended1 < extended2);
    ASSERT_FALSE(extended2 < extended1);

    extended1.getExtendedValue().setValue(41);
    ASSERT_TRUE(extended1 < extended2);
    ASSERT_FALSE(extended2 < extended1);
}

TEST_F(ExtendedChoiceFieldTest, hashCode)
{
    Extended extended1;
    Extended extended2;
    extended1.initializeChildren();
    extended2.initializeChildren();
    ASSERT_EQ(extended1.hashCode(), extended2.hashCode());

    // do not re-initialize children until the choice is properly set in setExtendedValue
    extended1.setNumElements(static_cast<uint32_t>(VALUES.size()));
    ASSERT_NE(extended1.hashCode(), extended2.hashCode());
    extended2.setNumElements(static_cast<uint32_t>(VALUES.size()));
    ASSERT_EQ(extended1.hashCode(), extended2.hashCode());

    Choice extendedValue;
    extendedValue.setValues(VALUES);
    extended2.setExtendedValue(extendedValue);
    extended2.initializeChildren();
    ASSERT_NE(extended1.hashCode(), extended2.hashCode());
    extended1.setExtendedValue(extendedValue);
    extended1.initializeChildren();
    ASSERT_EQ(extended1.hashCode(), extended2.hashCode());
}

TEST_F(ExtendedChoiceFieldTest, bitSizeOf)
{
    Extended extendedEmpty(0, Choice());
    extendedEmpty.initializeChildren();
    ASSERT_EQ(EXTENDED_BIT_SIZE_EMPTY, extendedEmpty.bitSizeOf());

    Extended extendedValue(1, Choice());
    extendedValue.getExtendedValue().setValue(42);
    extendedValue.initializeChildren();
    ASSERT_EQ(EXTENDED_BIT_SIZE_VALUE, extendedValue.bitSizeOf());

    Extended extendedValues(static_cast<uint32_t>(VALUES.size()), Choice());
    extendedValues.getExtendedValue().setValues(VALUES);
    extendedValues.initializeChildren();
    ASSERT_EQ(EXTENDED_BIT_SIZE_VALUES, extendedValues.bitSizeOf());
}

TEST_F(ExtendedChoiceFieldTest, initializeOffsets)
{
    Extended extendedEmpty(0, Choice());
    extendedEmpty.initializeChildren();
    ASSERT_EQ(EXTENDED_BIT_SIZE_EMPTY, extendedEmpty.initializeOffsets(0));

    Extended extendedValue(1, Choice());
    extendedValue.getExtendedValue().setValue(42);
    extendedValue.initializeChildren();
    ASSERT_EQ(EXTENDED_BIT_SIZE_VALUE, extendedValue.initializeOffsets(0));

    Extended extendedValues(static_cast<uint32_t>(VALUES.size()), Choice());
    extendedValues.getExtendedValue().setValues(VALUES);
    extendedValues.initializeChildren();
    ASSERT_EQ(EXTENDED_BIT_SIZE_VALUES, extendedValues.initializeOffsets(0));
}

TEST_F(ExtendedChoiceFieldTest, writeReadExtendedEmpty)
{
    Extended extended(0, Choice());
    auto bitBuffer = zserio::serialize(extended);
    ASSERT_EQ(EXTENDED_BIT_SIZE_EMPTY, bitBuffer.getBitSize());

    auto readExtended = zserio::deserialize<Extended>(bitBuffer);
    ASSERT_FALSE(readExtended.isExtendedValuePresent());
    ASSERT_FALSE(extended == readExtended);

    checkCopyAndMove(extended, true);
    checkCopyAndMove(readExtended, false);
}

TEST_F(ExtendedChoiceFieldTest, writeReadExtendedValue)
{
    Extended extended(1, Choice());
    extended.getExtendedValue().setValue(42);
    auto bitBuffer = zserio::serialize(extended);
    ASSERT_EQ(EXTENDED_BIT_SIZE_VALUE, bitBuffer.getBitSize());

    auto readExtended = zserio::deserialize<Extended>(bitBuffer);
    ASSERT_TRUE(readExtended.isExtendedValuePresent());
    ASSERT_EQ(extended, readExtended);

    checkCopyAndMove(extended, true);
    checkCopyAndMove(readExtended, true);
}

TEST_F(ExtendedChoiceFieldTest, writeReadExtendedValues)
{
    Extended extended(static_cast<uint32_t>(VALUES.size()), Choice());
    extended.getExtendedValue().setValues(VALUES);
    auto bitBuffer = zserio::serialize(extended);
    ASSERT_EQ(EXTENDED_BIT_SIZE_VALUES, bitBuffer.getBitSize());

    auto readExtended = zserio::deserialize<Extended>(bitBuffer);
    ASSERT_TRUE(readExtended.isExtendedValuePresent());
    ASSERT_EQ(extended, readExtended);

    checkCopyAndMove(extended, true);
    checkCopyAndMove(readExtended, true);
}

TEST_F(ExtendedChoiceFieldTest, writeOriginalReadExtended)
{
    Original original(static_cast<uint32_t>(VALUES.size()));
    auto bitBuffer = zserio::serialize(original);
    auto readExtended = zserio::deserialize<Extended>(bitBuffer);
    ASSERT_FALSE(readExtended.isExtendedValuePresent());

    // extended value is default constructed
    ASSERT_FALSE(readExtended.getExtendedValue().isInitialized());

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
    Choice extendedValue;
    extendedValue.setValues(VALUES);
    readExtended.setExtendedValue(extendedValue);
    readExtended.initializeChildren();
    ASSERT_TRUE(readExtended.isExtendedValuePresent());

    // bit size as extended
    ASSERT_EQ(EXTENDED_BIT_SIZE_VALUES, readExtended.bitSizeOf());

    // initialize offsets as extended
    ASSERT_EQ(EXTENDED_BIT_SIZE_VALUES, readExtended.initializeOffsets(0));

    // write as extended
    bitBuffer = zserio::serialize(readExtended);
    ASSERT_EQ(EXTENDED_BIT_SIZE_VALUES, bitBuffer.getBitSize());

    checkCopyAndMove(readExtended, true);
}

TEST_F(ExtendedChoiceFieldTest, writeExtendedEmptyReadOriginal)
{
    Extended extended(0, Choice());
    auto bitBuffer = zserio::serialize(extended);
    ASSERT_EQ(EXTENDED_BIT_SIZE_EMPTY, bitBuffer.getBitSize());

    zserio::BitStreamReader reader(bitBuffer);
    Original readOriginal(reader);
    ASSERT_EQ(extended.getNumElements(), readOriginal.getNumElements());
    ASSERT_EQ(ORIGINAL_BIT_SIZE, reader.getBitPosition());
}

TEST_F(ExtendedChoiceFieldTest, writeExtendedValueReadOriginal)
{
    Extended extended(1, Choice());
    extended.getExtendedValue().setValue(42);
    auto bitBuffer = zserio::serialize(extended);
    ASSERT_EQ(EXTENDED_BIT_SIZE_VALUE, bitBuffer.getBitSize());

    zserio::BitStreamReader reader(bitBuffer);
    Original readOriginal(reader);
    ASSERT_EQ(extended.getNumElements(), readOriginal.getNumElements());
    ASSERT_EQ(ORIGINAL_BIT_SIZE, reader.getBitPosition());
}

TEST_F(ExtendedChoiceFieldTest, writeExtendedValuesReadOriginal)
{
    Extended extended(static_cast<uint32_t>(VALUES.size()), Choice());
    extended.getExtendedValue().setValues(VALUES);
    auto bitBuffer = zserio::serialize(extended);
    ASSERT_EQ(EXTENDED_BIT_SIZE_VALUES, bitBuffer.getBitSize());

    zserio::BitStreamReader reader(bitBuffer);
    Original readOriginal(reader);
    ASSERT_EQ(extended.getNumElements(), readOriginal.getNumElements());
    ASSERT_EQ(ORIGINAL_BIT_SIZE, reader.getBitPosition());
}

} // namespace extended_choice_field
} // namespace extended_members
