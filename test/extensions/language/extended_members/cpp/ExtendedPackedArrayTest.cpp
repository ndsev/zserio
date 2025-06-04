#include "extended_members/extended_packed_array/Extended1.h"
#include "extended_members/extended_packed_array/Extended2.h"
#include "gtest/gtest.h"
#include "test_utils/TestUtility.h"

namespace extended_members
{
namespace extended_packed_array
{

using AllocatorType = Extended2::allocator_type;
template <typename T>
using VectorType = std::vector<T, zserio::RebindAlloc<AllocatorType, T>>;

class ExtendedPackedArrayTest : public ::testing::Test
{
protected:
    template <typename T>
    T createData()
    {
        T data;
        data.getArray().resize(ARRAY_SIZE);
        data.getPackedArray().resize(PACKED_ARRAY_SIZE);
        return data;
    }

    static constexpr size_t ARRAY_SIZE = 1;
    static constexpr size_t PACKED_ARRAY_SIZE = 5;

    static constexpr size_t ORIGINAL_BIT_SIZE = 8 + 32; // array of Elements of length 1
    static constexpr size_t EXTENDED1_BIT_SIZE =
            zserio::alignTo(8, ORIGINAL_BIT_SIZE) + // align to 8 due to extended
            8 + // varsize
            1 + // is packed
            6 + // max bit number
            32; // first element
    static constexpr size_t EXTENDED2_BIT_SIZE =
            zserio::alignTo(8, EXTENDED1_BIT_SIZE) + // align to 8 due to extended
            1; // auto optional not present
};

constexpr size_t ExtendedPackedArrayTest::EXTENDED1_BIT_SIZE;
constexpr size_t ExtendedPackedArrayTest::EXTENDED2_BIT_SIZE;

TEST_F(ExtendedPackedArrayTest, defaultConstructor)
{
    Extended2 data;

    // always present when not read from stream
    ASSERT_TRUE(data.isPackedArrayPresent());
    ASSERT_TRUE(data.isOptionalPackedArrayPresent());

    // default initialized
    ASSERT_EQ(0, data.getPackedArray().size());
    ASSERT_FALSE(data.isOptionalPackedArraySet());
}

TEST_F(ExtendedPackedArrayTest, fieldConstructor)
{
    Extended2 data(VectorType<Element>{}, VectorType<Element>{{Element{42}}}, zserio::NullOpt);

    ASSERT_TRUE(data.isPackedArrayPresent());

    ASSERT_EQ(42, data.getPackedArray().at(0).getValue());
}

TEST_F(ExtendedPackedArrayTest, operatorEquality)
{
    Extended2 data = createData<Extended2>();
    Extended2 equalData = createData<Extended2>();
    Extended2 lessThanData = createData<Extended2>();
    lessThanData.getPackedArray().back().setValue(12);

    test_utils::comparisonOperatorsTest(data, equalData, lessThanData);
}

TEST_F(ExtendedPackedArrayTest, bitSizeOfExtended1)
{
    Extended1 data = createData<Extended1>();

    ASSERT_EQ(EXTENDED1_BIT_SIZE, data.bitSizeOf());
}

TEST_F(ExtendedPackedArrayTest, bitSizeOfExtended2)
{
    Extended2 data = createData<Extended2>();

    ASSERT_EQ(EXTENDED2_BIT_SIZE, data.bitSizeOf());
}

TEST_F(ExtendedPackedArrayTest, writeReadExtended2)
{
    Extended2 data = createData<Extended2>();

    test_utils::writeReadTest(data);
}

TEST_F(ExtendedPackedArrayTest, writeExtended1ReadExtended2)
{
    Extended1 dataExtended1 = createData<Extended1>();

    auto bitBuffer = zserio::serialize(dataExtended1);
    Extended2 readDataExtended2 = zserio::deserialize<Extended2>(bitBuffer);
    ASSERT_FALSE(readDataExtended2.isOptionalPackedArrayPresent());

    // bit size as extended1
    ASSERT_EQ(EXTENDED1_BIT_SIZE, readDataExtended2.bitSizeOf());

    // write as extened1
    bitBuffer = zserio::serialize(readDataExtended2);
    ASSERT_EQ(EXTENDED1_BIT_SIZE, bitBuffer.getBitSize());

    // read extended1 again
    Extended1 readDataExtended1 = zserio::deserialize<Extended1>(bitBuffer);
    ASSERT_EQ(dataExtended1, readDataExtended1);

    // make the extended value present
    readDataExtended2.resetOptionalPackedArray();
    ASSERT_TRUE(readDataExtended2.isOptionalPackedArrayPresent());
    ASSERT_FALSE(readDataExtended2.isOptionalPackedArraySet()); // optional not present

    // bit size as extended2
    ASSERT_EQ(EXTENDED2_BIT_SIZE, readDataExtended2.bitSizeOf());

    // write as extended2
    bitBuffer = zserio::serialize(readDataExtended2);
    ASSERT_EQ(EXTENDED2_BIT_SIZE, bitBuffer.getBitSize());

    test_utils::writeReadTest(readDataExtended2);
}

TEST_F(ExtendedPackedArrayTest, stdHash)
{
    Extended2 data = createData<Extended2>();
    const size_t dataHash = 3259260897;
    Extended2 equalData = createData<Extended2>();
    Extended2 diffData = createData<Extended2>();
    diffData.getPackedArray().back().setValue(12);
    const size_t diffDataHash = 3259260896;

    test_utils::hashTest(data, dataHash, equalData, diffData, diffDataHash);
}

} // namespace extended_packed_array
} // namespace extended_members
