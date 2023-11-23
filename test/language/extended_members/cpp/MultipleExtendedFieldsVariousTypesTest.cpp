#include "gtest/gtest.h"

#include <numeric>

#include "extended_members/multiple_extended_fields_various_types/Original.h"
#include "extended_members/multiple_extended_fields_various_types/Extended1.h"
#include "extended_members/multiple_extended_fields_various_types/Extended2.h"

#include "zserio/SerializeUtil.h"
#include "zserio/BitSizeOfCalculator.h"

namespace extended_members
{
namespace multiple_extended_fields_various_types
{

using allocator_type = Extended2::allocator_type;
using string_type = zserio::string<allocator_type>;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;
using BitBuffer = zserio::BasicBitBuffer<zserio::RebindAlloc<allocator_type, uint8_t>>;

class MultipleExtendedFieldsVariousTypesTest : public ::testing::Test
{
protected:
    void checkExtended1FieldsPresent(const Extended2& extended2, bool expectedExtended1FieldsPresent)
    {
        ASSERT_EQ(expectedExtended1FieldsPresent, extended2.isExtendedValue1Present());
        ASSERT_EQ(expectedExtended1FieldsPresent, extended2.isExtendedValue2Present());
        ASSERT_EQ(expectedExtended1FieldsPresent, extended2.isExtendedValue3Present());
    }

    void checkExtended2FieldsPresent(const Extended2& extended2, bool expectedExtended2FieldsPresent)
    {
        ASSERT_EQ(expectedExtended2FieldsPresent, extended2.isExtendedValue4Present());
        ASSERT_EQ(expectedExtended2FieldsPresent, extended2.isExtendedValue5Present());
        ASSERT_EQ(expectedExtended2FieldsPresent, extended2.isExtendedValue6Present());
        ASSERT_EQ(expectedExtended2FieldsPresent, extended2.isExtendedValue7Present());
        ASSERT_EQ(expectedExtended2FieldsPresent, extended2.isExtendedValue8Present());
        ASSERT_EQ(expectedExtended2FieldsPresent, extended2.isExtendedValue9Present());
    }

    void checkAllExtendedFieldsPresent(const Extended2& extended2, bool expectedPresent)
    {
        checkExtended1FieldsPresent(extended2, expectedPresent);
        checkExtended2FieldsPresent(extended2, expectedPresent);
    }

    void checkCopyAndMove(const Extended2& extended2, bool expectedExtended1FieldsPresent,
            bool expectedExtended2FieldsPresent)
    {
        auto copiedExtended2(extended2);
        checkExtended1FieldsPresent(copiedExtended2, expectedExtended1FieldsPresent);
        checkExtended2FieldsPresent(copiedExtended2, expectedExtended2FieldsPresent);
        ASSERT_EQ(extended2, copiedExtended2);

        auto movedExtended2(std::move(copiedExtended2));
        checkExtended1FieldsPresent(movedExtended2, expectedExtended1FieldsPresent);
        checkExtended2FieldsPresent(movedExtended2, expectedExtended2FieldsPresent);
        ASSERT_EQ(extended2, movedExtended2);

        Extended2 copiedWithPropagateAllocatorExtended2(zserio::PropagateAllocator, extended2, allocator_type());
        checkExtended1FieldsPresent(copiedWithPropagateAllocatorExtended2, expectedExtended1FieldsPresent);
        checkExtended2FieldsPresent(copiedWithPropagateAllocatorExtended2, expectedExtended2FieldsPresent);
        ASSERT_EQ(extended2, copiedWithPropagateAllocatorExtended2);

        Extended2 copyAssignedExtended2;
        copyAssignedExtended2 = extended2;
        checkExtended1FieldsPresent(copyAssignedExtended2, expectedExtended1FieldsPresent);
        checkExtended2FieldsPresent(copyAssignedExtended2, expectedExtended2FieldsPresent);
        ASSERT_EQ(extended2, copyAssignedExtended2);

        Extended2 moveAssignedExtended2;
        moveAssignedExtended2 = copyAssignedExtended2;
        checkExtended1FieldsPresent(moveAssignedExtended2, expectedExtended1FieldsPresent);
        checkExtended2FieldsPresent(moveAssignedExtended2, expectedExtended2FieldsPresent);
        ASSERT_EQ(extended2, moveAssignedExtended2);
    }

    Extended1 createExtended1()
    {
        Extended1 extended1;
        extended1.setValue(VALUE);
        extended1.setExtendedValue1(EXTENDED_VALUE1);
        extended1.setExtendedValue2(EXTENDED_VALUE2);
        extended1.setExtendedValue3(EXTENDED_VALUE3);
        return extended1;
    }

    Extended2 createExtended2()
    {
        Extended2 extended2;
        extended2.setValue(VALUE);
        extended2.setExtendedValue1(EXTENDED_VALUE1);
        extended2.setExtendedValue2(EXTENDED_VALUE2);
        extended2.setExtendedValue3(EXTENDED_VALUE3);
        extended2.resetExtendedValue4();
        extended2.setExtendedValue5(EXTENDED_VALUE5);
        extended2.setExtendedValue6(EXTENDED_VALUE6);
        extended2.getExtendedValue7().setValueU32(UINT32_MAX);
        extended2.resetExtendedValue8();
        extended2.setExtendedValue9(EXTENDED_VALUE9);
        extended2.initializeChildren();
        return extended2;
    }

    static size_t calcExtended1BitSize()
    {
        size_t bitSize = ORIGINAL_BIT_SIZE;
        bitSize = zserio::alignTo(8, bitSize);
        bitSize += 1 + 4 * 8; // optional extendedValue1
        bitSize = zserio::alignTo(8, bitSize);
        bitSize += zserio::bitSizeOfBitBuffer(EXTENDED_VALUE2);
        bitSize = zserio::alignTo(8, bitSize);
        bitSize += zserio::bitSizeOfBytes(EXTENDED_VALUE3);
        return bitSize;
    }

    static size_t calcExtended2BitSize()
    {
        size_t bitSize = calcExtended1BitSize();
        bitSize = zserio::alignTo(8, bitSize);
        bitSize += 1; // unset optional extendedValue4
        bitSize = zserio::alignTo(8, bitSize);
        bitSize += zserio::bitSizeOfVarSize(EXTENDED_VALUE5);
        bitSize = zserio::alignTo(8, bitSize);
        bitSize += std::accumulate(EXTENDED_VALUE6.begin(), EXTENDED_VALUE6.end(), static_cast<size_t>(0),
                [](size_t size, const string_type& str)
                {
                    return size + zserio::bitSizeOfString(str);
                });
        bitSize = zserio::alignTo(8, bitSize);
        bitSize += 8 + 4 * 8; // extendedValue7 (choiceTag + valueU32)
        bitSize = zserio::alignTo(8, bitSize);
        bitSize += 1; // unset optional extendedValue8
        bitSize = zserio::alignTo(8, bitSize);
        bitSize += EXTENDED_VALUE5; // used non-auto optional dynamic bit field extendedValue9
        return bitSize;
    }

    static constexpr int8_t VALUE = -13;
    static constexpr uint32_t EXTENDED_VALUE1 = 42;
    static const BitBuffer EXTENDED_VALUE2;
    static const vector_type<uint8_t> EXTENDED_VALUE3;
    static constexpr uint32_t EXTENDED_VALUE5 = 3;
    static const vector_type<string_type> EXTENDED_VALUE6;
    static constexpr uint64_t EXTENDED_VALUE9 = 7; // bit<EXTENDED_VALUE5> == bit<3>

    static const size_t ORIGINAL_BIT_SIZE;
    static const size_t EXTENDED1_BIT_SIZE;
    static const size_t EXTENDED2_BIT_SIZE;
};

constexpr int8_t MultipleExtendedFieldsVariousTypesTest::VALUE;
constexpr uint32_t MultipleExtendedFieldsVariousTypesTest::EXTENDED_VALUE1;
const BitBuffer MultipleExtendedFieldsVariousTypesTest::EXTENDED_VALUE2 =
        BitBuffer({ 0xCA, 0xFE }, 16);
const vector_type<uint8_t> MultipleExtendedFieldsVariousTypesTest::EXTENDED_VALUE3 = { 0xDE, 0xAD };
constexpr uint32_t MultipleExtendedFieldsVariousTypesTest::EXTENDED_VALUE5;
const vector_type<string_type> MultipleExtendedFieldsVariousTypesTest::EXTENDED_VALUE6 =
        { "this", "is", "test" };
constexpr uint64_t MultipleExtendedFieldsVariousTypesTest::EXTENDED_VALUE9;

const size_t MultipleExtendedFieldsVariousTypesTest::ORIGINAL_BIT_SIZE = 7;
const size_t MultipleExtendedFieldsVariousTypesTest::EXTENDED1_BIT_SIZE = calcExtended1BitSize();
const size_t MultipleExtendedFieldsVariousTypesTest::EXTENDED2_BIT_SIZE = calcExtended2BitSize();

TEST_F(MultipleExtendedFieldsVariousTypesTest, defaultConstructor)
{
    Extended2 extended2;

    // always present when not read from stream
    checkAllExtendedFieldsPresent(extended2, true);

    // default constructed
    ASSERT_FALSE(extended2.isExtendedValue1Set());
    ASSERT_FALSE(extended2.isExtendedValue1Used());
    ASSERT_EQ(BitBuffer(), extended2.getExtendedValue2());
    ASSERT_EQ(vector_type<uint8_t>(), extended2.getExtendedValue3());
    ASSERT_FALSE(extended2.isExtendedValue4Set());
    ASSERT_FALSE(extended2.isExtendedValue4Used());
    ASSERT_EQ(0, extended2.getExtendedValue5());
    ASSERT_EQ(vector_type<string_type>(), extended2.getExtendedValue6());
    ASSERT_FALSE(extended2.getExtendedValue7().isInitialized());
    ASSERT_FALSE(extended2.isExtendedValue8Set());
    ASSERT_FALSE(extended2.isExtendedValue8Used());
    ASSERT_FALSE(extended2.isExtendedValue9Set());
    ASSERT_FALSE(extended2.isExtendedValue9Used());
}

TEST_F(MultipleExtendedFieldsVariousTypesTest, fieldConstructor)
{
    Union extendedValue7;
    Extended2 extended2(VALUE, EXTENDED_VALUE1, EXTENDED_VALUE2, EXTENDED_VALUE3, zserio::NullOpt,
            EXTENDED_VALUE5, EXTENDED_VALUE6, extendedValue7, zserio::NullOpt, EXTENDED_VALUE9);

    checkAllExtendedFieldsPresent(extended2, true);

    ASSERT_TRUE(extended2.isExtendedValue1Set());
    ASSERT_TRUE(extended2.isExtendedValue1Used());
    ASSERT_EQ(EXTENDED_VALUE1, extended2.getExtendedValue1());
    ASSERT_EQ(EXTENDED_VALUE2, extended2.getExtendedValue2());
    ASSERT_EQ(EXTENDED_VALUE3, extended2.getExtendedValue3());
    ASSERT_FALSE(extended2.isExtendedValue4Set());
    ASSERT_FALSE(extended2.isExtendedValue4Used());
    ASSERT_EQ(EXTENDED_VALUE5, extended2.getExtendedValue5());
    ASSERT_EQ(EXTENDED_VALUE6, extended2.getExtendedValue6());
    ASSERT_FALSE(extended2.getExtendedValue7().isInitialized());
    ASSERT_FALSE(extended2.isExtendedValue8Set());
    ASSERT_FALSE(extended2.isExtendedValue8Used());
    ASSERT_EQ(EXTENDED_VALUE9, extended2.getExtendedValue9());
}

TEST_F(MultipleExtendedFieldsVariousTypesTest, operatorEquality)
{
    Extended2 extended1;
    extended1.initializeChildren();
    Extended2 extended2;
    extended2.initializeChildren();
    Extended2 extended3 = createExtended2();
    Extended2 extended4 = createExtended2();

    ASSERT_EQ(extended1, extended2);
    ASSERT_FALSE(extended1 == extended3);
    ASSERT_EQ(extended3, extended4);

    extended3.setExtendedValue9(0);
    ASSERT_FALSE(extended3 == extended4);
}

TEST_F(MultipleExtendedFieldsVariousTypesTest, operatorLessThan)
{
    Extended2 extended1;
    extended1.initializeChildren();
    Extended2 extended2;
    extended2.initializeChildren();
    ASSERT_FALSE(extended1 < extended2);
    ASSERT_FALSE(extended2 < extended1);

    Extended2 extended3 = createExtended2();
    ASSERT_FALSE(extended1 < extended3);
    ASSERT_TRUE(extended3 < extended1); // first field is VALUE (-13)

    Extended2 extended4 = createExtended2();
    ASSERT_FALSE(extended3 < extended4);
    ASSERT_FALSE(extended4 < extended3);

    extended3.setExtendedValue9(0);
    ASSERT_TRUE(extended3 < extended4);
    ASSERT_FALSE(extended4 < extended3);
}

TEST_F(MultipleExtendedFieldsVariousTypesTest, hashCode)
{
    Extended2 extended1;
    extended1.initializeChildren();
    Extended2 extended2;
    extended2.initializeChildren();
    Extended2 extended3 = createExtended2();
    Extended2 extended4 = createExtended2();

    ASSERT_EQ(extended1.hashCode(), extended2.hashCode());
    ASSERT_NE(extended1.hashCode(), extended3.hashCode());
    ASSERT_EQ(extended3.hashCode(), extended4.hashCode());

    extended3.setExtendedValue9(0);
    ASSERT_NE(extended3.hashCode(), extended4.hashCode());
}

TEST_F(MultipleExtendedFieldsVariousTypesTest, bitSizeOf)
{
    Extended1 extended1 = createExtended1();
    ASSERT_EQ(EXTENDED1_BIT_SIZE, extended1.bitSizeOf());

    Extended2 extended2 = createExtended2();
    ASSERT_EQ(EXTENDED2_BIT_SIZE, extended2.bitSizeOf());
}

TEST_F(MultipleExtendedFieldsVariousTypesTest, initializeOffsets)
{
    Extended1 extended1 = createExtended1();
    ASSERT_EQ(EXTENDED1_BIT_SIZE, extended1.initializeOffsets(0));

    Extended2 extended2 = createExtended2();
    ASSERT_EQ(EXTENDED2_BIT_SIZE, extended2.initializeOffsets(0));
}

TEST_F(MultipleExtendedFieldsVariousTypesTest, writeReadExtended2)
{
    Extended2 extended2 = createExtended2();
    auto bitBuffer = zserio::serialize(extended2);
    ASSERT_EQ(EXTENDED2_BIT_SIZE, bitBuffer.getBitSize());

    auto readExtended2 = zserio::deserialize<Extended2>(bitBuffer);
    checkAllExtendedFieldsPresent(extended2, true);
    ASSERT_EQ(extended2, readExtended2);

    checkCopyAndMove(readExtended2, true, true);
}

TEST_F(MultipleExtendedFieldsVariousTypesTest, writeOriginalReadExtended2)
{
    Original original(VALUE);
    auto bitBuffer = zserio::serialize(original);
    auto readExtended2 = zserio::deserialize<Extended2>(bitBuffer);
    checkAllExtendedFieldsPresent(readExtended2, false);

    // extended fields are default constructed
    ASSERT_FALSE(readExtended2.isExtendedValue1Set());
    ASSERT_FALSE(readExtended2.isExtendedValue1Used());
    ASSERT_EQ(BitBuffer(), readExtended2.getExtendedValue2());
    ASSERT_EQ(vector_type<uint8_t>(), readExtended2.getExtendedValue3());
    ASSERT_FALSE(readExtended2.isExtendedValue4Set());
    ASSERT_FALSE(readExtended2.isExtendedValue4Used());
    ASSERT_EQ(0, readExtended2.getExtendedValue5());
    ASSERT_EQ(vector_type<string_type>(), readExtended2.getExtendedValue6());
    ASSERT_FALSE(readExtended2.getExtendedValue7().isInitialized());
    ASSERT_FALSE(readExtended2.isExtendedValue8Set());
    ASSERT_FALSE(readExtended2.isExtendedValue8Used());
    ASSERT_FALSE(readExtended2.isExtendedValue9Set());
    ASSERT_FALSE(readExtended2.isExtendedValue9Used());

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
    readExtended2.setExtendedValue2(EXTENDED_VALUE2);
    checkAllExtendedFieldsPresent(readExtended2, true);

    checkCopyAndMove(readExtended2, true, true);
}

TEST_F(MultipleExtendedFieldsVariousTypesTest, writeExtended1ReadExtended2)
{
    Extended1 extended1 = createExtended1();
    auto bitBuffer = zserio::serialize(extended1);
    auto readExtended2 = zserio::deserialize<Extended2>(bitBuffer);
    checkExtended1FieldsPresent(readExtended2, true);
    checkExtended2FieldsPresent(readExtended2, false);

    // extended1 fields are read from the stream
    ASSERT_TRUE(readExtended2.isExtendedValue1Set());
    ASSERT_TRUE(readExtended2.isExtendedValue1Used());
    ASSERT_EQ(EXTENDED_VALUE1, readExtended2.getExtendedValue1());
    ASSERT_EQ(EXTENDED_VALUE2, readExtended2.getExtendedValue2());
    ASSERT_EQ(EXTENDED_VALUE3, readExtended2.getExtendedValue3());

    // extended2 fields are default constructed
    ASSERT_FALSE(readExtended2.isExtendedValue4Set());
    ASSERT_FALSE(readExtended2.isExtendedValue4Used());
    ASSERT_EQ(0, readExtended2.getExtendedValue5());
    ASSERT_EQ(vector_type<string_type>(), readExtended2.getExtendedValue6());
    ASSERT_FALSE(readExtended2.getExtendedValue7().isInitialized());
    ASSERT_FALSE(readExtended2.isExtendedValue8Set());
    ASSERT_FALSE(readExtended2.isExtendedValue8Used());
    ASSERT_FALSE(readExtended2.isExtendedValue9Set());
    ASSERT_FALSE(readExtended2.isExtendedValue9Used());

    // bit size as extended1
    ASSERT_EQ(EXTENDED1_BIT_SIZE, readExtended2.bitSizeOf());

    // initialize offsets as extended1
    ASSERT_EQ(EXTENDED1_BIT_SIZE, readExtended2.initializeOffsets(0));

    // writes as extended1
    bitBuffer = zserio::serialize(readExtended2);
    ASSERT_EQ(EXTENDED1_BIT_SIZE, bitBuffer.getBitSize());

    // read extended1 again
    auto readExtended1 = zserio::deserialize<Extended1>(bitBuffer);
    ASSERT_EQ(extended1, readExtended1);

    // read original
    auto readOriginal = zserio::deserialize<Original>(bitBuffer);
    ASSERT_EQ(VALUE, readOriginal.getValue());

    checkCopyAndMove(readExtended2, true, false);

    // resetter of actually present optional field will not make all fields present
    Extended2 readExtended2Setter1 = readExtended2;
    EXPECT_TRUE(readExtended2Setter1.isExtendedValue1Set());
    readExtended2Setter1.resetExtendedValue1(); // reset value from Extended1
    readExtended2Setter1.initializeChildren();
    ASSERT_FALSE(readExtended2Setter1.isExtendedValue1Set());
    checkExtended1FieldsPresent(readExtended2Setter1, true);
    checkExtended2FieldsPresent(readExtended2Setter1, false);

    // setter of actually present field will not make all fields present
    Extended2 readExtended2Setter2 = readExtended2;
    readExtended2Setter2.setExtendedValue2(EXTENDED_VALUE2); // set value from Extended1
    readExtended2Setter2.initializeChildren();
    checkExtended1FieldsPresent(readExtended2Setter2, true);
    checkExtended2FieldsPresent(readExtended2Setter2, false);

    // r-value setter of actually present field will not make all fields present
    Extended2 readExtended2RvalueSetter2 = readExtended2;
    {
        BitBuffer extendedValue2 = EXTENDED_VALUE2;
        readExtended2RvalueSetter2.setExtendedValue2(std::move(extendedValue2));
    }
    checkExtended1FieldsPresent(readExtended2RvalueSetter2, true);
    checkExtended2FieldsPresent(readExtended2RvalueSetter2, false);

    // setter of non-present field will make all fields present
    Extended2 readExtended2Setter5 = readExtended2;
    readExtended2Setter5.setExtendedValue5(EXTENDED_VALUE5); // set value from Extended2
    readExtended2Setter5.initializeChildren();
    checkAllExtendedFieldsPresent(readExtended2Setter5, true);

    checkCopyAndMove(readExtended2Setter1, true, false);
    checkCopyAndMove(readExtended2Setter2, true, false);
    checkCopyAndMove(readExtended2Setter5, true, true);
}

} // namespace multiple_extended_fields_various_types
} // namespace extended_members
