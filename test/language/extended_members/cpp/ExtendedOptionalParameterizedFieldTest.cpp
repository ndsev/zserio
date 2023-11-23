#include "gtest/gtest.h"

#include <numeric>

#include "extended_members/extended_optional_parameterized_field/Original.h"
#include "extended_members/extended_optional_parameterized_field/Extended.h"

#include "zserio/SerializeUtil.h"

namespace extended_members
{
namespace extended_optional_parameterized_field
{

using allocator_type = Extended::allocator_type;
using string_type = zserio::string<allocator_type>;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class ExtendedOptionalParameterizedFieldTest : public ::testing::Test
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

    static const vector_type<string_type> ARRAY;

    static const size_t ORIGINAL_BIT_SIZE;
    static const size_t EXTENDED_BIT_SIZE_WITHOUT_OPTIONAL;
    static const size_t EXTENDED_BIT_SIZE_WITH_OPTIONAL;
};

const vector_type<string_type> ExtendedOptionalParameterizedFieldTest::ARRAY = { "this", "is", "test" };

const size_t ExtendedOptionalParameterizedFieldTest::ORIGINAL_BIT_SIZE = 11;
const size_t ExtendedOptionalParameterizedFieldTest::EXTENDED_BIT_SIZE_WITHOUT_OPTIONAL =
        zserio::alignTo(8, ORIGINAL_BIT_SIZE) + 1;
const size_t ExtendedOptionalParameterizedFieldTest::EXTENDED_BIT_SIZE_WITH_OPTIONAL =
        zserio::alignTo(8, ORIGINAL_BIT_SIZE) + 1 +
        std::accumulate(ARRAY.begin(), ARRAY.end(), static_cast<size_t>(0),
                [](size_t size, const string_type& str) { return size + zserio::bitSizeOfString(str); });

TEST_F(ExtendedOptionalParameterizedFieldTest, defaultConstructor)
{
    Extended extended;

    // always present when not read from stream
    ASSERT_TRUE(extended.isExtendedValuePresent());

    // default initialized
    ASSERT_EQ(0, extended.getValue());
    // optional is unset
    ASSERT_FALSE(extended.isExtendedValueSet());
    ASSERT_FALSE(extended.isExtendedValueUsed());
}

TEST_F(ExtendedOptionalParameterizedFieldTest, fieldConstructor)
{
    Extended extended(static_cast<uint16_t>(ARRAY.size()), Parameterized());
    extended.getExtendedValue().setArray(ARRAY);
    ASSERT_TRUE(extended.isExtendedValuePresent());

    ASSERT_EQ(ARRAY.size(), extended.getValue());
    ASSERT_EQ(ARRAY, extended.getExtendedValue().getArray());
}

TEST_F(ExtendedOptionalParameterizedFieldTest, operatorEquality)
{
    Extended extended1;
    Extended extended2;
    ASSERT_EQ(extended1, extended2);

    extended1.setValue(static_cast<uint16_t>(ARRAY.size()));
    ASSERT_FALSE(extended1 == extended2);
    extended2.setValue(static_cast<uint16_t>(ARRAY.size()));
    ASSERT_EQ(extended1, extended2);

    Parameterized extendedValue;
    extendedValue.setArray(ARRAY);
    extended2.setExtendedValue(extendedValue);
    extended2.initializeChildren();
    ASSERT_FALSE(extended1 == extended2);
    extended1.setExtendedValue(extendedValue);
    extended1.initializeChildren();
    ASSERT_EQ(extended1, extended2);
}

TEST_F(ExtendedOptionalParameterizedFieldTest, operatorLessThan)
{
    Extended extended1;
    Extended extended2;
    ASSERT_FALSE(extended1 < extended2);
    ASSERT_FALSE(extended2 < extended1);

    extended1.setValue(static_cast<uint16_t>(ARRAY.size()));
    ASSERT_FALSE(extended1 < extended2);
    ASSERT_TRUE(extended2 < extended1);

    extended2.setValue(static_cast<uint16_t>(ARRAY.size()));
    ASSERT_FALSE(extended1 < extended2);
    ASSERT_FALSE(extended2 < extended1);

    Parameterized extendedValue;
    extendedValue.setArray(ARRAY);
    extended2.setExtendedValue(extendedValue);
    extended2.initializeChildren();
    ASSERT_TRUE(extended1 < extended2);
    ASSERT_FALSE(extended2 < extended1);

    extended1.setExtendedValue(extendedValue);
    extended1.initializeChildren();
    ASSERT_FALSE(extended1 < extended2);
    ASSERT_FALSE(extended2 < extended1);
}

TEST_F(ExtendedOptionalParameterizedFieldTest, hashCode)
{
    Extended extended1;
    Extended extended2;
    ASSERT_EQ(extended1.hashCode(), extended2.hashCode());

    extended1.setValue(13);
    ASSERT_NE(extended1.hashCode(), extended2.hashCode());
    extended2.setValue(13);
    ASSERT_EQ(extended1.hashCode(), extended2.hashCode());

    Parameterized extendedValue;
    extendedValue.setArray(ARRAY);
    extended2.setExtendedValue(extendedValue);
    extended2.initializeChildren();
    ASSERT_NE(extended1.hashCode(), extended2.hashCode());
    extended1.setExtendedValue(extendedValue);
    extended1.initializeChildren();
    ASSERT_EQ(extended1.hashCode(), extended2.hashCode());
}

TEST_F(ExtendedOptionalParameterizedFieldTest, bitSizeOf)
{
    Extended extended;
    ASSERT_EQ(EXTENDED_BIT_SIZE_WITHOUT_OPTIONAL, extended.bitSizeOf());

    Parameterized extendedValue;
    extendedValue.setArray(ARRAY);
    extended.setExtendedValue(extendedValue);
    ASSERT_EQ(EXTENDED_BIT_SIZE_WITH_OPTIONAL, extended.bitSizeOf());
}

TEST_F(ExtendedOptionalParameterizedFieldTest, initializeOffsets)
{
    Extended extended;
    ASSERT_EQ(EXTENDED_BIT_SIZE_WITHOUT_OPTIONAL, extended.initializeOffsets(0));

    Parameterized extendedValue;
    extendedValue.setArray(ARRAY);
    extended.setExtendedValue(extendedValue);
    ASSERT_EQ(EXTENDED_BIT_SIZE_WITH_OPTIONAL, extended.initializeOffsets(0));
}

TEST_F(ExtendedOptionalParameterizedFieldTest, writeReadExtendedWithoutOptional)
{
    Extended extended(0, zserio::NullOpt);
    auto bitBuffer = zserio::serialize(extended);
    ASSERT_EQ(EXTENDED_BIT_SIZE_WITHOUT_OPTIONAL, bitBuffer.getBitSize());

    auto readExtended = zserio::deserialize<Extended>(bitBuffer);
    ASSERT_TRUE(readExtended.isExtendedValuePresent());
    ASSERT_FALSE(readExtended.isExtendedValueSet());
    ASSERT_FALSE(readExtended.isExtendedValueUsed());
    ASSERT_EQ(extended, readExtended);

    checkCopyAndMove(extended, true);
}

TEST_F(ExtendedOptionalParameterizedFieldTest, writeReadExtendedWithOptional)
{
    Extended extended(static_cast<uint16_t>(ARRAY.size()), Parameterized());
    extended.getExtendedValue().setArray(ARRAY);
    auto bitBuffer = zserio::serialize(extended);
    ASSERT_EQ(EXTENDED_BIT_SIZE_WITH_OPTIONAL, bitBuffer.getBitSize());

    auto readExtended = zserio::deserialize<Extended>(bitBuffer);
    ASSERT_TRUE(readExtended.isExtendedValuePresent());
    ASSERT_TRUE(readExtended.isExtendedValueSet());
    ASSERT_TRUE(readExtended.isExtendedValueUsed());
    ASSERT_EQ(extended, readExtended);

    checkCopyAndMove(extended, true);
}

TEST_F(ExtendedOptionalParameterizedFieldTest, writeOriginalReadExtended)
{
    Original original(static_cast<uint16_t>(ARRAY.size()));
    auto bitBuffer = zserio::serialize(original);
    auto readExtended = zserio::deserialize<Extended>(bitBuffer);
    ASSERT_FALSE(readExtended.isExtendedValuePresent());

    // extended value is default constructed (NullOpt)
    ASSERT_FALSE(readExtended.isExtendedValueSet());
    ASSERT_FALSE(readExtended.isExtendedValueUsed());

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

    // setter makes the value present! (or even resetter)
    Extended extendedWithoutOptional = readExtended;
    extendedWithoutOptional.resetExtendedValue();
    ASSERT_TRUE(extendedWithoutOptional.isExtendedValuePresent());

    Extended extendedWithOptional = readExtended;
    Parameterized extendedValue(ARRAY);
    extendedWithOptional.setExtendedValue(extendedValue);
    extendedWithOptional.initializeChildren();
    ASSERT_TRUE(extendedWithOptional.isExtendedValuePresent());

    // bit size as extended
    ASSERT_EQ(EXTENDED_BIT_SIZE_WITHOUT_OPTIONAL, extendedWithoutOptional.bitSizeOf());
    ASSERT_EQ(EXTENDED_BIT_SIZE_WITH_OPTIONAL, extendedWithOptional.bitSizeOf());

    // initialize offsets as extended
    ASSERT_EQ(EXTENDED_BIT_SIZE_WITHOUT_OPTIONAL, extendedWithoutOptional.initializeOffsets(0));
    ASSERT_EQ(EXTENDED_BIT_SIZE_WITH_OPTIONAL, extendedWithOptional.initializeOffsets(0));

    // writes as extended
    bitBuffer = zserio::serialize(extendedWithoutOptional);
    ASSERT_EQ(EXTENDED_BIT_SIZE_WITHOUT_OPTIONAL, bitBuffer.getBitSize());
    bitBuffer = zserio::serialize(extendedWithOptional);
    ASSERT_EQ(EXTENDED_BIT_SIZE_WITH_OPTIONAL, bitBuffer.getBitSize());

    checkCopyAndMove(extendedWithOptional, true);
}

TEST_F(ExtendedOptionalParameterizedFieldTest, writeExtendedWithoutOptionalReadOriginal)
{
    Extended extended(0, zserio::NullOpt);
    auto bitBuffer = zserio::serialize(extended);
    ASSERT_EQ(EXTENDED_BIT_SIZE_WITHOUT_OPTIONAL, bitBuffer.getBitSize());

    zserio::BitStreamReader reader(bitBuffer);
    Original readOriginal(reader);
    ASSERT_EQ(extended.getValue(), readOriginal.getValue());
    ASSERT_EQ(ORIGINAL_BIT_SIZE, reader.getBitPosition());
}

TEST_F(ExtendedOptionalParameterizedFieldTest, writeExtendedWithOptionalReadOriginal)
{
    Extended extended(static_cast<uint16_t>(ARRAY.size()), Parameterized());
    extended.getExtendedValue().setArray(ARRAY);
    auto bitBuffer = zserio::serialize(extended);
    ASSERT_EQ(EXTENDED_BIT_SIZE_WITH_OPTIONAL, bitBuffer.getBitSize());

    zserio::BitStreamReader reader(bitBuffer);
    Original readOriginal(reader);
    ASSERT_EQ(extended.getValue(), readOriginal.getValue());
    ASSERT_EQ(ORIGINAL_BIT_SIZE, reader.getBitPosition());
}

} // namespace extended_optional_parameterized_field
} // namespace extended_members
