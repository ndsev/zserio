#include "gtest/gtest.h"

#include "extended_members/multiple_extended_fields/Original.h"
#include "extended_members/multiple_extended_fields/Extended1.h"
#include "extended_members/multiple_extended_fields/Extended2.h"

#include "zserio/SerializeUtil.h"
#include "zserio/BitSizeOfCalculator.h"

namespace extended_members
{
namespace multiple_extended_fields
{

using allocator_type = Extended2::allocator_type;

class MultipleExtendedFieldsTest : public ::testing::Test
{
protected:
    void checkCopyAndMove(const Extended2& extended2, bool expectedIsPresent1, bool expectedIsPresent2)
    {
        auto copiedExtended2(extended2);
        ASSERT_EQ(expectedIsPresent1, copiedExtended2.isExtendedValue1Present());
        ASSERT_EQ(expectedIsPresent2, copiedExtended2.isExtendedValue2Present());
        ASSERT_EQ(extended2, copiedExtended2);

        auto movedExtended2(std::move(copiedExtended2));
        ASSERT_EQ(expectedIsPresent1, movedExtended2.isExtendedValue1Present());
        ASSERT_EQ(expectedIsPresent2, movedExtended2.isExtendedValue2Present());
        ASSERT_EQ(extended2, movedExtended2);

        Extended2 copiedWithPropagateAllocatorExtended2(zserio::PropagateAllocator, extended2, allocator_type());
        ASSERT_EQ(expectedIsPresent1, copiedWithPropagateAllocatorExtended2.isExtendedValue1Present());
        ASSERT_EQ(expectedIsPresent2, copiedWithPropagateAllocatorExtended2.isExtendedValue2Present());
        ASSERT_EQ(extended2, copiedWithPropagateAllocatorExtended2);

        Extended2 copyAssignedExtended2;
        copyAssignedExtended2 = extended2;
        ASSERT_EQ(expectedIsPresent1, copyAssignedExtended2.isExtendedValue1Present());
        ASSERT_EQ(expectedIsPresent2, copyAssignedExtended2.isExtendedValue2Present());
        ASSERT_EQ(extended2, copyAssignedExtended2);

        Extended2 moveAssignedExtended2;
        moveAssignedExtended2 = copyAssignedExtended2;
        ASSERT_EQ(expectedIsPresent1, moveAssignedExtended2.isExtendedValue1Present());
        ASSERT_EQ(expectedIsPresent2, moveAssignedExtended2.isExtendedValue2Present());
        ASSERT_EQ(extended2, moveAssignedExtended2);
    }

    static const std::string DEFAULT_EXTENDED_VALUE2;

    static const size_t ORIGINAL_BIT_SIZE;
    static const size_t EXTENDED1_BIT_SIZE;
    static const size_t EXTENDED2_BIT_SIZE;
};

const std::string MultipleExtendedFieldsTest::DEFAULT_EXTENDED_VALUE2 = "test";

const size_t MultipleExtendedFieldsTest::ORIGINAL_BIT_SIZE = 4 * 8;
const size_t MultipleExtendedFieldsTest::EXTENDED1_BIT_SIZE = ORIGINAL_BIT_SIZE + 4;
const size_t MultipleExtendedFieldsTest::EXTENDED2_BIT_SIZE = zserio::alignTo(8, (EXTENDED1_BIT_SIZE)) +
        zserio::bitSizeOfString(DEFAULT_EXTENDED_VALUE2);

TEST_F(MultipleExtendedFieldsTest, defaultConstructor)
{
    Extended2 extended2;

    // always present when not read from stream
    ASSERT_TRUE(extended2.isExtendedValue1Present());
    ASSERT_TRUE(extended2.isExtendedValue2Present());

    // default initialized
    ASSERT_EQ(0, extended2.getValue());
    ASSERT_EQ(0, extended2.getExtendedValue1());
    ASSERT_EQ(DEFAULT_EXTENDED_VALUE2, extended2.getExtendedValue2());
}

TEST_F(MultipleExtendedFieldsTest, fieldConstructor)
{
    Extended2 extended2(42, 2, "other");
    ASSERT_TRUE(extended2.isExtendedValue1Present());
    ASSERT_TRUE(extended2.isExtendedValue2Present());

    ASSERT_EQ(42, extended2.getValue());
    ASSERT_EQ(2, extended2.getExtendedValue1());
    ASSERT_EQ("other", extended2.getExtendedValue2());
}

TEST_F(MultipleExtendedFieldsTest, operatorEquality)
{
    Extended2 extended1;
    Extended2 extended2;
    ASSERT_EQ(extended1, extended2);

    extended1.setValue(13);
    ASSERT_FALSE(extended1 == extended2);
    extended2.setValue(13);
    ASSERT_EQ(extended1, extended2);

    extended2.setExtendedValue1(2);
    ASSERT_FALSE(extended1 == extended2);
    extended1.setExtendedValue1(2);
    ASSERT_EQ(extended1, extended2);

    extended1.setExtendedValue2("value");
    ASSERT_FALSE(extended1 == extended2);
    extended2.setExtendedValue2("value");
    ASSERT_EQ(extended1, extended2);
}

TEST_F(MultipleExtendedFieldsTest, hashCode)
{
    Extended2 extended1;
    Extended2 extended2;
    ASSERT_EQ(extended1.hashCode(), extended2.hashCode());

    extended1.setValue(13);
    ASSERT_NE(extended1.hashCode(), extended2.hashCode());
    extended2.setValue(13);
    ASSERT_EQ(extended1.hashCode(), extended2.hashCode());

    extended2.setExtendedValue1(2);
    ASSERT_NE(extended1.hashCode(), extended2.hashCode());
    extended1.setExtendedValue1(2);
    ASSERT_EQ(extended1.hashCode(), extended2.hashCode());

    extended1.setExtendedValue2("value");
    ASSERT_NE(extended1.hashCode(), extended2.hashCode());
    extended2.setExtendedValue2("value");
    ASSERT_EQ(extended1.hashCode(), extended2.hashCode());
}

TEST_F(MultipleExtendedFieldsTest, bitSizeOf)
{
    Extended2 extended2;
    ASSERT_EQ(EXTENDED2_BIT_SIZE, extended2.bitSizeOf());
}

TEST_F(MultipleExtendedFieldsTest, initializeOffsets)
{
    Extended2 extended2;
    ASSERT_EQ(EXTENDED2_BIT_SIZE, extended2.initializeOffsets(0));
}

TEST_F(MultipleExtendedFieldsTest, writeReadExtended2)
{
    Extended2 extended2(42, 2, DEFAULT_EXTENDED_VALUE2);
    auto bitBuffer = zserio::serialize(extended2);
    ASSERT_EQ(EXTENDED2_BIT_SIZE, bitBuffer.getBitSize());

    auto readExtended = zserio::deserialize<Extended2>(bitBuffer);
    ASSERT_TRUE(readExtended.isExtendedValue1Present());
    ASSERT_TRUE(readExtended.isExtendedValue2Present());
    ASSERT_EQ(extended2, readExtended);

    checkCopyAndMove(extended2, true, true);
}

TEST_F(MultipleExtendedFieldsTest, writeOriginalReadExtened2)
{
    Original original(42);
    auto bitBuffer = zserio::serialize(original);
    auto readExtended2 = zserio::deserialize<Extended2>(bitBuffer);
    ASSERT_FALSE(readExtended2.isExtendedValue1Present());
    ASSERT_FALSE(readExtended2.isExtendedValue2Present());

    // extended values are default constructed
    ASSERT_EQ(0, readExtended2.getExtendedValue1());
    ASSERT_EQ(DEFAULT_EXTENDED_VALUE2, readExtended2.getExtendedValue2());

    // bit size as original
    ASSERT_EQ(ORIGINAL_BIT_SIZE, readExtended2.bitSizeOf());

    // initialize offsets as original
    ASSERT_EQ(ORIGINAL_BIT_SIZE, readExtended2.initializeOffsets(0));

    // writes as original
    bitBuffer = zserio::serialize(readExtended2);
    ASSERT_EQ(ORIGINAL_BIT_SIZE, bitBuffer.getBitSize());

    // read original again
    auto readOriginal = zserio::deserialize<Original>(bitBuffer);
    ASSERT_EQ(original, readOriginal);

    checkCopyAndMove(readExtended2, false, false);

    // any setter makes all values present!
    Extended2 readExtended2Setter1 = readExtended2;
    readExtended2Setter1.setExtendedValue1(2);
    ASSERT_TRUE(readExtended2Setter1.isExtendedValue1Present());
    ASSERT_TRUE(readExtended2Setter1.isExtendedValue2Present());

    Extended2 readExtended2Setter2 = readExtended2;
    readExtended2Setter2.setExtendedValue2(DEFAULT_EXTENDED_VALUE2);
    ASSERT_TRUE(readExtended2Setter2.isExtendedValue1Present());
    ASSERT_TRUE(readExtended2Setter2.isExtendedValue2Present());

    // bit size as extended2
    ASSERT_EQ(EXTENDED2_BIT_SIZE, readExtended2Setter1.bitSizeOf());
    ASSERT_EQ(EXTENDED2_BIT_SIZE, readExtended2Setter2.bitSizeOf());

    // initialize offsets as extended2
    ASSERT_EQ(EXTENDED2_BIT_SIZE, readExtended2Setter1.initializeOffsets(0));
    ASSERT_EQ(EXTENDED2_BIT_SIZE, readExtended2Setter2.initializeOffsets(0));

    // writes as extended2
    bitBuffer = zserio::serialize(readExtended2Setter1);
    ASSERT_EQ(EXTENDED2_BIT_SIZE, bitBuffer.getBitSize());
    bitBuffer = zserio::serialize(readExtended2Setter2);
    ASSERT_EQ(EXTENDED2_BIT_SIZE, bitBuffer.getBitSize());

    checkCopyAndMove(readExtended2Setter1, true, true);
    checkCopyAndMove(readExtended2Setter2, true, true);
}

TEST_F(MultipleExtendedFieldsTest, writeExtended1ReadExtended2)
{
    Extended1 extended1(42, 2);
    auto bitBuffer = zserio::serialize(extended1);
    auto readExtended2 = zserio::deserialize<Extended2>(bitBuffer);
    ASSERT_TRUE(readExtended2.isExtendedValue1Present());
    ASSERT_FALSE(readExtended2.isExtendedValue2Present());

    ASSERT_EQ(2, readExtended2.getExtendedValue1());
    // extended value is default constructed
    ASSERT_EQ(DEFAULT_EXTENDED_VALUE2, readExtended2.getExtendedValue2());

    // bit size as extended1
    ASSERT_EQ(EXTENDED1_BIT_SIZE, readExtended2.bitSizeOf());

    // initialize offsets as extended1
    ASSERT_EQ(EXTENDED1_BIT_SIZE, readExtended2.initializeOffsets(0));

    // write as extended1
    bitBuffer = zserio::serialize(readExtended2);
    ASSERT_EQ(EXTENDED1_BIT_SIZE, bitBuffer.getBitSize());

    // read extended1 again
    auto readExtended1 = zserio::deserialize<Extended1>(bitBuffer);
    ASSERT_EQ(extended1, readExtended1);

    // read original
    auto readOriginal = zserio::deserialize<Original>(bitBuffer);
    ASSERT_EQ(42, readOriginal.getValue());

    checkCopyAndMove(readExtended2, true, false);

    // any setter makes all values present!
    Extended2 readExtended2Setter1 = readExtended2;
    readExtended2Setter1.setExtendedValue1(2);
    ASSERT_TRUE(readExtended2Setter1.isExtendedValue1Present());
    ASSERT_TRUE(readExtended2Setter1.isExtendedValue2Present());

    Extended2 readExtended2Setter2 = readExtended2;
    readExtended2Setter2.setExtendedValue2(DEFAULT_EXTENDED_VALUE2);
    ASSERT_TRUE(readExtended2Setter2.isExtendedValue1Present());
    ASSERT_TRUE(readExtended2Setter2.isExtendedValue2Present());

    // bit size as extended2
    ASSERT_EQ(EXTENDED2_BIT_SIZE, readExtended2Setter1.bitSizeOf());
    ASSERT_EQ(EXTENDED2_BIT_SIZE, readExtended2Setter2.bitSizeOf());

    // initialize offsets as extended2
    ASSERT_EQ(EXTENDED2_BIT_SIZE, readExtended2Setter1.initializeOffsets(0));
    ASSERT_EQ(EXTENDED2_BIT_SIZE, readExtended2Setter2.initializeOffsets(0));

    // writes as extended2
    bitBuffer = zserio::serialize(readExtended2Setter1);
    ASSERT_EQ(EXTENDED2_BIT_SIZE, bitBuffer.getBitSize());
    bitBuffer = zserio::serialize(readExtended2Setter2);
    ASSERT_EQ(EXTENDED2_BIT_SIZE, bitBuffer.getBitSize());

    checkCopyAndMove(readExtended2Setter1, true, true);
    checkCopyAndMove(readExtended2Setter2, true, true);
}

TEST_F(MultipleExtendedFieldsTest, writeExtended2ReadOriginal)
{
    Extended2 extended2(42, 2, DEFAULT_EXTENDED_VALUE2);
    auto bitBuffer = zserio::serialize(extended2);
    ASSERT_EQ(EXTENDED2_BIT_SIZE, bitBuffer.getBitSize());

    zserio::BitStreamReader reader(bitBuffer);
    Original readOriginal(reader);
    ASSERT_EQ(extended2.getValue(), readOriginal.getValue());
    ASSERT_EQ(ORIGINAL_BIT_SIZE, reader.getBitPosition());
}

TEST_F(MultipleExtendedFieldsTest, writeExtended2ReadExtended1)
{
    Extended2 extended2(42, 2, DEFAULT_EXTENDED_VALUE2);
    auto bitBuffer = zserio::serialize(extended2);
    ASSERT_EQ(EXTENDED2_BIT_SIZE, bitBuffer.getBitSize());

    zserio::BitStreamReader reader(bitBuffer);
    Extended1 readExtended1(reader);
    ASSERT_EQ(extended2.getValue(), readExtended1.getValue());
    ASSERT_EQ(EXTENDED1_BIT_SIZE, reader.getBitPosition());
}

} // namespace multiple_extended_fields
} // namespace extended_members
