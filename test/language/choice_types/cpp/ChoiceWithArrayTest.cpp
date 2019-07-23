#include "gtest/gtest.h"

#include "choice_types/choice_with_array/TestChoice.h"

// just testChoice setters and getters
namespace choice_types
{
namespace choice_with_array
{

TEST(ChoiceWithArrayTest, fieldConstructor)
{
    TestChoice choice8(8, std::vector<Data8>{ Data8{1}, Data8{2} , Data8{3} });
    TestChoice choice16(16, std::vector<int16_t>{ -1, 1 });

    ASSERT_EQ(3, choice8.getArray8().size());
    ASSERT_EQ(2, choice16.getArray16().size());
    ASSERT_THROW(choice8.getArray16(), zserio::CppRuntimeException);
    ASSERT_THROW(choice16.getArray8(), zserio::CppRuntimeException);

    std::vector<Data8> data8 = { Data8{1}, Data8{2}, Data8{3} };
    const void* dataPtr = &data8[0];
    TestChoice choiceData8Copied(8, data8);
    ASSERT_NE(dataPtr, &choiceData8Copied.getArray8()[0]);
    TestChoice choiceData8Moved(8, std::move(data8));
    ASSERT_EQ(dataPtr, &choiceData8Moved.getArray8()[0]);
}

TEST(ChoiceWithArrayTest, moveConstructor)
{
    TestChoice choice8(8, std::vector<Data8>{ Data8{1}, Data8{2} , Data8{3} });
    const void* dataPtr = &choice8.getArray8()[0];
    TestChoice choice8Moved(std::move(choice8));
    ASSERT_EQ(dataPtr, &choice8Moved.getArray8()[0]);
}

TEST(ChoiceWithArrayTest, moveAssignmentConstructor)
{
    TestChoice choice8(8, std::vector<Data8>{ Data8{1}, Data8{2} , Data8{3} });
    const void* dataPtr = &choice8.getArray8()[0];
    TestChoice choice8Moved;
    choice8Moved = std::move(choice8);
    ASSERT_EQ(dataPtr, &choice8Moved.getArray8()[0]);
}

TEST(ChoiceWithArrayTest, array8)
{
    TestChoice testChoice;
    testChoice.initialize(8);
    std::vector<Data8> data8(4);
    const void* dataPtr = &data8[0];
    testChoice.setArray8(data8);
    ASSERT_EQ(4, testChoice.getArray8().size());
    ASSERT_NE(dataPtr, &testChoice.getArray8()[0]);

    testChoice.setArray8(std::move(data8));
    ASSERT_EQ(dataPtr, &testChoice.getArray8()[0]);
}

TEST(ChoiceWithArrayTest, array16)
{
    TestChoice testChoice;
    testChoice.initialize(16);
    std::vector<int16_t> array16(4);
    const void* dataPtr = &array16[0];
    testChoice.setArray16(array16);
    ASSERT_EQ(4, testChoice.getArray16().size());
    ASSERT_NE(dataPtr, &testChoice.getArray16()[0]);

    testChoice.setArray16(std::move(array16));
    ASSERT_EQ(dataPtr, &testChoice.getArray16()[0]);
}

} // namespace choice_with_array
} // namespace choice_types
