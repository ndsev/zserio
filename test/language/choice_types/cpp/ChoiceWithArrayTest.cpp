#include "gtest/gtest.h"

#include "choice_types/choice_with_array/TestChoice.h"

#include "zserio/RebindAlloc.h"
#include "zserio/SerializeUtil.h"

// just testChoice setters and getters
namespace choice_types
{
namespace choice_with_array
{

using allocator_type = TestChoice::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class ChoiceWithArrayTest : public ::testing::Test
{
protected:
    TestChoice::ParameterExpressions parameterExpressions8 = {
            nullptr, 0, [](void*, size_t) { return static_cast<int8_t>(8); } };
    TestChoice::ParameterExpressions parameterExpressions16 = {
            nullptr, 0, [](void*, size_t) { return static_cast<int8_t>(16); } };
};

// The following three tests are here because of tests of move operator for arrays.
TEST_F(ChoiceWithArrayTest, setters)
{
    TestChoice choice8;
    choice8.setArray8(vector_type<Data8>{ Data8{1}, Data8{2} , Data8{3} });
    choice8.initialize(parameterExpressions8);
    TestChoice choice16;
    choice16.setArray16(vector_type<int16_t>{ -1, 1 });
    choice16.initialize(parameterExpressions16);

    ASSERT_EQ(3, choice8.getArray8().size());
    ASSERT_EQ(2, choice16.getArray16().size());
    ASSERT_THROW(choice8.getArray16(), zserio::CppRuntimeException);
    ASSERT_THROW(choice16.getArray8(), zserio::CppRuntimeException);

    vector_type<Data8> data8 = { Data8{1}, Data8{2}, Data8{3} };
    const void* dataPtr = data8.data();
    TestChoice choiceData8Copied;
    choiceData8Copied.setArray8(data8);
    choiceData8Copied.initialize(parameterExpressions8);
    ASSERT_NE(dataPtr, choiceData8Copied.getArray8().data());
    TestChoice choiceData8Moved;
    choiceData8Moved.setArray8(std::move(data8));
    choiceData8Moved.initialize(parameterExpressions8);
    ASSERT_EQ(dataPtr, choiceData8Moved.getArray8().data());
}

TEST_F(ChoiceWithArrayTest, moveConstructor)
{
    TestChoice choice8;
    choice8.setArray8(vector_type<Data8>{ Data8{1}, Data8{2} , Data8{3} });
    choice8.initialize(parameterExpressions8);
    const void* dataPtr = choice8.getArray8().data();
    TestChoice choice8Moved(std::move(choice8));
    ASSERT_EQ(dataPtr, choice8Moved.getArray8().data());
}

TEST_F(ChoiceWithArrayTest, moveAssignmentOperator)
{
    TestChoice choice8;
    choice8.setArray8(vector_type<Data8>{ Data8{1}, Data8{2} , Data8{3} });
    choice8.initialize(parameterExpressions8);
    const void* dataPtr = choice8.getArray8().data();
    TestChoice choice8Moved;
    choice8Moved = std::move(choice8);
    ASSERT_EQ(dataPtr, choice8Moved.getArray8().data());
}

TEST_F(ChoiceWithArrayTest, array8)
{
    TestChoice testChoice;
    testChoice.initialize(parameterExpressions8);
    vector_type<Data8> data8(4);
    const void* dataPtr = data8.data();
    testChoice.setArray8(data8);
    ASSERT_EQ(4, testChoice.getArray8().size());
    ASSERT_NE(dataPtr, testChoice.getArray8().data());

    testChoice.setArray8(std::move(data8));
    ASSERT_EQ(dataPtr, testChoice.getArray8().data());
}

TEST_F(ChoiceWithArrayTest, array16)
{
    TestChoice testChoice;
    testChoice.initialize(parameterExpressions16);
    vector_type<int16_t> array16(4);
    const void* dataPtr = array16.data();
    testChoice.setArray16(array16);
    ASSERT_EQ(4, testChoice.getArray16().size());
    ASSERT_NE(dataPtr, testChoice.getArray16().data());

    testChoice.setArray16(std::move(array16));
    ASSERT_EQ(dataPtr, testChoice.getArray16().data());
}

TEST_F(ChoiceWithArrayTest, writeReadFileArray8)
{
    TestChoice testChoice;
    vector_type<Data8> data8{Data8{1}, Data8{2}, Data8{3}, Data8{4}};
    testChoice.setArray8(data8);
    const std::string fileName = "language/choice_types/choice_with_array_array8.blob";
    zserio::serializeToFile(testChoice, fileName, parameterExpressions8);

    const auto readTestChoice = zserio::deserializeFromFile<TestChoice>(fileName, parameterExpressions8);
    ASSERT_EQ(testChoice, readTestChoice);
}

TEST_F(ChoiceWithArrayTest, writeReadFileArray16)
{
    TestChoice testChoice;
    vector_type<int16_t> array16{10, 20, 30, 40, 50};
    testChoice.setArray16(array16);
    const std::string fileName = "language/choice_types/choice_with_array_array16.blob";
    zserio::serializeToFile(testChoice, fileName, parameterExpressions16);

    const auto readTestChoice = zserio::deserializeFromFile<TestChoice>(fileName, parameterExpressions16);
    ASSERT_EQ(testChoice, readTestChoice);
}

} // namespace choice_with_array
} // namespace choice_types
