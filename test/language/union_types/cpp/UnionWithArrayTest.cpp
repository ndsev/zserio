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
    std::vector<Data8> data8(4);
    void* ptr = &data8[0];
    test.setArray8(data8);
    ASSERT_EQ(4, test.getArray8().size());
    ASSERT_NE(ptr, &test.getArray8()[0]);

    test.setArray8(std::move(data8));
    ASSERT_EQ(4, test.getArray8().size());
    ASSERT_EQ(ptr, &test.getArray8()[0]);
}

TEST(UnionWithArrayTest, array16)
{
    TestUnion test;
    std::vector<int16_t> data16(4);
    void* ptr = &data16[0];
    test.setArray16(data16);
    ASSERT_EQ(4, test.getArray16().size());
    ASSERT_NE(ptr, &test.getArray16()[0]);

    test.setArray16(std::move(data16));
    ASSERT_EQ(4, test.getArray16().size());
    ASSERT_EQ(ptr, &test.getArray16()[0]);
}

} // namespace union_with_array
} // namespace union_types
