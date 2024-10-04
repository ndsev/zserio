#include <numeric>

#include "extended_members/extended_indexed_offsets/Extended.h"
#include "extended_members/extended_indexed_offsets/Original.h"
#include "gtest/gtest.h"
#include "zserio/SerializeUtil.h"

namespace extended_members
{
namespace extended_indexed_offsets
{

using allocator_type = Extended::allocator_type;
using string_type = zserio::string<allocator_type>;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class ExtendedIndexedOffsetsTest : public ::testing::Test
{
protected:
    void checkCopyAndMove(const Extended& extended, bool expectedIsPresent)
    {
        auto copiedExtended(extended);
        ASSERT_EQ(expectedIsPresent, copiedExtended.isArrayPresent());
        ASSERT_EQ(extended, copiedExtended);

        auto movedExtended(std::move(copiedExtended));
        ASSERT_EQ(expectedIsPresent, movedExtended.isArrayPresent());
        ASSERT_EQ(extended, movedExtended);

        Extended copiedWithPropagateAllocatorExtended(zserio::PropagateAllocator, extended, allocator_type());
        ASSERT_EQ(expectedIsPresent, copiedWithPropagateAllocatorExtended.isArrayPresent());
        ASSERT_EQ(extended, copiedWithPropagateAllocatorExtended);

        Extended copyAssignedExtended;
        copyAssignedExtended = extended;
        ASSERT_EQ(expectedIsPresent, copyAssignedExtended.isArrayPresent());
        ASSERT_EQ(extended, copyAssignedExtended);

        Extended moveAssignedExtended;
        moveAssignedExtended = copyAssignedExtended;
        ASSERT_EQ(expectedIsPresent, moveAssignedExtended.isArrayPresent());
        ASSERT_EQ(extended, moveAssignedExtended);
    }

    static const vector_type<uint32_t> OFFSETS;
    static const vector_type<string_type> ARRAY;

    static const size_t ORIGINAL_BIT_SIZE;
    static const size_t EXTENDED_BIT_SIZE;
};

const vector_type<uint32_t> ExtendedIndexedOffsetsTest::OFFSETS = {0, 0, 0, 0, 0};
const vector_type<string_type> ExtendedIndexedOffsetsTest::ARRAY = {
        "extended", "indexed", "offsets", "test", "!"};

const size_t ExtendedIndexedOffsetsTest::ORIGINAL_BIT_SIZE =
        zserio::bitSizeOfVarSize(zserio::convertSizeToUInt32(OFFSETS.size())) + OFFSETS.size() * 4 * 8;
const size_t ExtendedIndexedOffsetsTest::EXTENDED_BIT_SIZE = ORIGINAL_BIT_SIZE +
        zserio::bitSizeOfVarSize(zserio::convertSizeToUInt32(ARRAY.size())) +
        std::accumulate(ARRAY.begin(), ARRAY.end(), static_cast<size_t>(0),
                [](size_t size, const string_type& str) { return size + zserio::bitSizeOfString(str); });

TEST_F(ExtendedIndexedOffsetsTest, defaultConstructor)
{
    Extended extended;

    // always present when not read from stream
    ASSERT_TRUE(extended.isArrayPresent());

    // default initialized
    ASSERT_EQ(0, extended.getOffsets().size());
    ASSERT_EQ(0, extended.getArray().size());
}

TEST_F(ExtendedIndexedOffsetsTest, fieldConstructor)
{
    Extended extended(OFFSETS, ARRAY);
    ASSERT_TRUE(extended.isArrayPresent());

    ASSERT_EQ(OFFSETS, extended.getOffsets());
    ASSERT_EQ(ARRAY, extended.getArray());
}

TEST_F(ExtendedIndexedOffsetsTest, operatorEquality)
{
    Extended extended1;
    Extended extended2;
    ASSERT_EQ(extended1, extended2);

    extended1.setOffsets(OFFSETS);
    ASSERT_FALSE(extended1 == extended2);
    extended2.setOffsets(OFFSETS);
    ASSERT_EQ(extended1, extended2);

    extended2.setArray(ARRAY);
    ASSERT_FALSE(extended1 == extended2);
    extended1.setArray(ARRAY);
    ASSERT_EQ(extended1, extended2);
}

TEST_F(ExtendedIndexedOffsetsTest, operatorLessThan)
{
    Extended extended1;
    Extended extended2;
    ASSERT_FALSE(extended1 < extended2);
    ASSERT_FALSE(extended2 < extended1);

    extended1.setOffsets(OFFSETS);
    ASSERT_FALSE(extended1 < extended2);
    ASSERT_TRUE(extended2 < extended1);

    extended2.setOffsets(OFFSETS);
    ASSERT_FALSE(extended1 < extended2);
    ASSERT_FALSE(extended2 < extended1);

    extended2.setArray(ARRAY);
    ASSERT_TRUE(extended1 < extended2);
    ASSERT_FALSE(extended2 < extended1);

    extended1.setArray(ARRAY);
    ASSERT_FALSE(extended1 < extended2);
    ASSERT_FALSE(extended2 < extended1);
}

TEST_F(ExtendedIndexedOffsetsTest, hashCode)
{
    Extended extended1;
    Extended extended2;
    ASSERT_EQ(extended1.hashCode(), extended2.hashCode());

    extended1.setOffsets(OFFSETS);
    ASSERT_NE(extended1.hashCode(), extended2.hashCode());
    extended2.setOffsets(OFFSETS);
    ASSERT_EQ(extended1.hashCode(), extended2.hashCode());

    extended2.setArray(ARRAY);
    ASSERT_NE(extended1.hashCode(), extended2.hashCode());
    extended1.setArray(ARRAY);
    ASSERT_EQ(extended1.hashCode(), extended2.hashCode());
}

TEST_F(ExtendedIndexedOffsetsTest, bitSizeOf)
{
    Extended extended(OFFSETS, ARRAY);
    ASSERT_EQ(EXTENDED_BIT_SIZE, extended.bitSizeOf());
}

TEST_F(ExtendedIndexedOffsetsTest, initializeOffsets)
{
    Extended extended(OFFSETS, ARRAY);
    ASSERT_EQ(EXTENDED_BIT_SIZE, extended.initializeOffsets(0));
}

TEST_F(ExtendedIndexedOffsetsTest, writeReadExtended)
{
    Extended extended(OFFSETS, ARRAY);
    auto bitBuffer = zserio::serialize(extended);
    ASSERT_EQ(EXTENDED_BIT_SIZE, bitBuffer.getBitSize());

    auto readExtended = zserio::deserialize<Extended>(bitBuffer);
    ASSERT_TRUE(readExtended.isArrayPresent());
    ASSERT_EQ(extended, readExtended);

    checkCopyAndMove(extended, true);
}

TEST_F(ExtendedIndexedOffsetsTest, writeOriginalReadExtended)
{
    Original original(OFFSETS);
    auto bitBuffer = zserio::serialize(original);
    auto readExtended = zserio::deserialize<Extended>(bitBuffer);
    ASSERT_FALSE(readExtended.isArrayPresent());

    // extended value is default constructed
    ASSERT_EQ(0, readExtended.getArray().size());

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
    readExtended.setArray(ARRAY);
    ASSERT_TRUE(readExtended.isArrayPresent());

    // bit size as extended
    ASSERT_EQ(EXTENDED_BIT_SIZE, readExtended.bitSizeOf());

    // initialize offsets as extended
    ASSERT_EQ(EXTENDED_BIT_SIZE, readExtended.initializeOffsets(0));

    // writes as extended
    bitBuffer = zserio::serialize(readExtended);
    ASSERT_EQ(EXTENDED_BIT_SIZE, bitBuffer.getBitSize());

    checkCopyAndMove(readExtended, true);
}

TEST_F(ExtendedIndexedOffsetsTest, writeExtendedReadOriginal)
{
    Extended extended(OFFSETS, ARRAY);
    auto bitBuffer = zserio::serialize(extended);
    ASSERT_EQ(EXTENDED_BIT_SIZE, bitBuffer.getBitSize());

    zserio::BitStreamReader reader(bitBuffer);
    Original readOriginal(reader);
    ASSERT_EQ(extended.getOffsets(), readOriginal.getOffsets());
    ASSERT_EQ(ORIGINAL_BIT_SIZE, reader.getBitPosition());
}

} // namespace extended_indexed_offsets
} // namespace extended_members
