#include "gtest/gtest.h"

#include "optional_members/optional_array/TestStruct.h"

// just test setters and getters
namespace optional_members
{
namespace optional_array
{

TEST(OptionalArrayTest, data8)
{
    TestStruct test;
    test.setHasData8(true);
    test.setData8(std::vector<Data8>(4));
    ASSERT_EQ(4, test.getData8()->size());
}

TEST(OptionalArrayTest, autoData8)
{
    TestStruct test;
    ASSERT_FALSE(test.hasAutoData8());
    test.setAutoData8(std::vector<Data8>(4));
    ASSERT_TRUE(test.hasAutoData8());
    ASSERT_EQ(4, test.getAutoData8()->size());
}

TEST(OptionalArrayTest, data16)
{
    TestStruct test;
    test.setHasData8(false);
    test.setData16(std::vector<int16_t>(4));
    ASSERT_EQ(4, test.getData16()->size());
}

TEST(OptionalArrayTest, autoData16)
{
    TestStruct test;
    ASSERT_FALSE(test.hasAutoData16());
    test.setAutoData16(std::vector<int16_t>(4));
    ASSERT_TRUE(test.hasAutoData16());
    ASSERT_EQ(4, test.getAutoData16()->size());
}

} // namespace optional_array
} // namespace optional_members
