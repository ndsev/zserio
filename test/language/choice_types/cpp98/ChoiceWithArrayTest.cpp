#include "gtest/gtest.h"

#include "choice_types/choice_with_array/TestChoice.h"

// just testChoice setters and getters
namespace choice_types
{
namespace choice_with_array
{

TEST(ChoiceWithArrayTest, array8)
{
    TestChoice testChoice;
    testChoice.initialize(8);
    testChoice.setArray8(zserio::ObjectArray<Data8>(4));
    ASSERT_EQ(4, testChoice.getArray8().size());
}

TEST(ChoiceWithArrayTest, array16)
{
    TestChoice testChoice;
    testChoice.initialize(16);
    testChoice.setArray16(zserio::Int16Array(4));
    ASSERT_EQ(4, testChoice.getArray16().size());
}

} // namespace choice_with_array
} // namespace choice_types
