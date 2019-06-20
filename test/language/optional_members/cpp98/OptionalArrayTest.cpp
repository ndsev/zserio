#include "gtest/gtest.h"

#include "optional_members/optional_array/TestStruct.h"

// just test setters and getters
namespace optional_members
{
namespace optional_array
{

TEST(OptionalArray, data8)
{
    TestStruct test;
    test.setHasData8(true);
    test.setData8(zserio::ObjectArray<Data8>(4));
    ASSERT_EQ(4, test.getData8().size());
}

TEST(OptionalArray, autoData8)
{
    TestStruct test;
    ASSERT_FALSE(test.hasAutoData8());
    test.setAutoData8(zserio::ObjectArray<Data8>(4));
    ASSERT_TRUE(test.hasAutoData8());
    ASSERT_EQ(4, test.getAutoData8().size());
}

TEST(OptionalArray, data16)
{
    TestStruct test;
    test.setHasData8(false);
    test.setData16(zserio::Int16Array(4));
    ASSERT_EQ(4, test.getData16().size());
}

TEST(OptionalArray, autoData16)
{
    TestStruct test;
    ASSERT_FALSE(test.hasAutoData16());
    test.setAutoData16(zserio::Int16Array(4));
    ASSERT_TRUE(test.hasAutoData16());
    ASSERT_EQ(4, test.getAutoData16().size());
}

} // namespace optional_array
} // namespace optional_members
