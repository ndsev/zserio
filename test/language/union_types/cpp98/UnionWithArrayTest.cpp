#include "gtest/gtest.h"

#include "union_types/union_with_array/TestUnion.h"

// just test setters and getters
namespace union_types
{
namespace union_with_array
{

TEST(UnionWithArrayTest, array8)
{
    TestUnion test;
    test.setArray8(zserio::ObjectArray<Data8>(4));
    ASSERT_EQ(4, test.getArray8().size());
}

TEST(UnionWithArrayTest, array16)
{
    TestUnion test;
    test.setArray16(zserio::Int16Array(4));
    ASSERT_EQ(4, test.getArray16().size());
}

} // namespace union_with_array
} // namespace union_types
