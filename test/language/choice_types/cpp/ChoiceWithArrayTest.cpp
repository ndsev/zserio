#include "gtest/gtest.h"

#include "choice_types/choice_with_array/TestChoice.h"

// just test setters and getters
namespace choice_types
{
namespace choice_with_array
{

TEST(ChoiceWithArrayTest, array8)
{
    TestChoice test;
    test.initialize(8);
    test.setArray8(zserio::ObjectArray<Data8>(4));
    ASSERT_EQ(4, test.getArray8().size());
}

TEST(ChoiceWithArrayTest, array16)
{
    TestChoice test;
    test.initialize(16);
    test.setArray16(zserio::Int16Array(4));
    ASSERT_EQ(4, test.getArray16().size());
}

} // namespace choice_with_array
} // namespace choice_types
