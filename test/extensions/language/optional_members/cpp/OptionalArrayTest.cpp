#include "gtest/gtest.h"
#include "optional_members/optional_array/TestStruct.h"
#include "zserio/RebindAlloc.h"

// just test setters and getters
namespace optional_members
{
namespace optional_array
{

using allocator_type = TestStruct::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

TEST(OptionalArrayTest, data8)
{
    TestStruct test;
    test.setHasData8(true);
    test.setData8(vector_type<Data8>(4));
    ASSERT_EQ(4, test.getData8().size());

    ASSERT_FALSE(test.isData16Set());
    ASSERT_FALSE(test.isData16Used());
    test.setData16(vector_type<int16_t>(4));
    ASSERT_TRUE(test.isData16Set());
    ASSERT_FALSE(test.isData16Used());
}

TEST(OptionalArrayTest, autoData8)
{
    TestStruct test;
    ASSERT_FALSE(test.isAutoData8Set());
    ASSERT_FALSE(test.isAutoData8Used());
    test.setAutoData8(vector_type<Data8>(4));
    ASSERT_TRUE(test.isAutoData8Set());
    ASSERT_TRUE(test.isAutoData8Used());
    ASSERT_EQ(4, test.getAutoData8().size());
}

TEST(OptionalArrayTest, data16)
{
    TestStruct test;
    test.setHasData8(false);
    test.setData16(vector_type<int16_t>(4));
    ASSERT_EQ(4, test.getData16().size());

    ASSERT_FALSE(test.isData8Set());
    ASSERT_FALSE(test.isData8Used());
    test.setData8(vector_type<Data8>(4));
    ASSERT_TRUE(test.isData8Set());
    ASSERT_FALSE(test.isData8Used());
}

TEST(OptionalArrayTest, autoData16)
{
    TestStruct test;
    ASSERT_FALSE(test.isAutoData16Set());
    ASSERT_FALSE(test.isAutoData16Used());
    test.setAutoData16(vector_type<int16_t>(4));
    ASSERT_TRUE(test.isAutoData16Set());
    ASSERT_TRUE(test.isAutoData16Used());
    ASSERT_EQ(4, test.getAutoData16().size());
}

} // namespace optional_array
} // namespace optional_members
