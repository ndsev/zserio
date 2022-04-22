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
using vector_type = std::vector<T, zserio::RebindAlloc<allocator_type, T>>;

TEST(OptionalArrayTest, data8)
{
    TestStruct test;
    test.setHasData8(true);
    test.setData8(vector_type<Data8>(4));
    ASSERT_EQ(4, test.getData8().size());
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
