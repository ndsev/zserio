#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

#include "choice_types/empty_choice_with_case/EmptyChoiceWithCase.h"

namespace choice_types
{
namespace empty_choice_with_case
{

struct EmptyChoiceWithCaseTest : public ::testing::Test
{
    EmptyChoiceWithCase::ParameterExpressions parameterExpressions0 = {
            nullptr, 0, [](void*, size_t) { return static_cast<uint8_t>(0); } };
    EmptyChoiceWithCase::ParameterExpressions parameterExpressions1 = {
            nullptr, 0, [](void*, size_t) { return static_cast<uint8_t>(1); } };
};

TEST_F(EmptyChoiceWithCaseTest, emptyConstructor)
{
    {
        EmptyChoiceWithCase emptyChoiceWithCase;
        ASSERT_THROW(emptyChoiceWithCase.getSelector(), zserio::CppRuntimeException);
    }
    {
        EmptyChoiceWithCase emptyChoiceWithCase = {};
        ASSERT_THROW(emptyChoiceWithCase.getSelector(), zserio::CppRuntimeException);
    }
}

TEST_F(EmptyChoiceWithCaseTest, bitStreamReaderConstructor)
{
    zserio::BitStreamReader reader(nullptr, 0);

    EmptyChoiceWithCase emptyChoiceWithCase(reader, parameterExpressions1);
    ASSERT_EQ(1, emptyChoiceWithCase.getSelector());
    ASSERT_EQ(0, emptyChoiceWithCase.bitSizeOf());
}

TEST_F(EmptyChoiceWithCaseTest, copyConstructor)
{
    EmptyChoiceWithCase emptyChoiceWithCase;
    emptyChoiceWithCase.initialize(parameterExpressions1);
    const EmptyChoiceWithCase emptyChoiceWithCaseCopy(emptyChoiceWithCase);
    ASSERT_EQ(1, emptyChoiceWithCaseCopy.getSelector());
    ASSERT_EQ(0, emptyChoiceWithCaseCopy.bitSizeOf());
}

TEST_F(EmptyChoiceWithCaseTest, assignmentOperator)
{
    EmptyChoiceWithCase emptyChoiceWithCase;
    emptyChoiceWithCase.initialize(parameterExpressions1);
    EmptyChoiceWithCase emptyChoiceWithCaseCopy;
    emptyChoiceWithCaseCopy = emptyChoiceWithCase;
    ASSERT_EQ(1, emptyChoiceWithCaseCopy.getSelector());
    ASSERT_EQ(0, emptyChoiceWithCaseCopy.bitSizeOf());
}

TEST_F(EmptyChoiceWithCaseTest, moveConstructor)
{
    EmptyChoiceWithCase emptyChoiceWithCase;
    emptyChoiceWithCase.initialize(parameterExpressions1);
    // note that it doesn't ensure that move ctor was called
    const EmptyChoiceWithCase emptyChoiceWithCaseMoved(std::move(emptyChoiceWithCase));
    ASSERT_EQ(1, emptyChoiceWithCaseMoved.getSelector());
    ASSERT_EQ(0, emptyChoiceWithCaseMoved.bitSizeOf());
}

TEST_F(EmptyChoiceWithCaseTest, moveAssignmentOperator)
{
    EmptyChoiceWithCase emptyChoiceWithCase;
    emptyChoiceWithCase.initialize(parameterExpressions1);
    // note that it doesn't ensure that move ctor was called
    EmptyChoiceWithCase emptyChoiceWithCaseMoved;
    emptyChoiceWithCaseMoved = std::move(emptyChoiceWithCase);
    ASSERT_EQ(1, emptyChoiceWithCaseMoved.getSelector());
    ASSERT_EQ(0, emptyChoiceWithCaseMoved.bitSizeOf());
}

TEST_F(EmptyChoiceWithCaseTest, propagateAllocatorCopyConstructor)
{
    EmptyChoiceWithCase emptyChoiceWithCase;
    emptyChoiceWithCase.initialize(parameterExpressions1);
    const EmptyChoiceWithCase emptyChoiceWithCaseCopy(zserio::PropagateAllocator, emptyChoiceWithCase,
            EmptyChoiceWithCase::allocator_type());
    ASSERT_EQ(1, emptyChoiceWithCaseCopy.getSelector());
    ASSERT_EQ(0, emptyChoiceWithCaseCopy.bitSizeOf());
}

TEST_F(EmptyChoiceWithCaseTest, initialize)
{
    EmptyChoiceWithCase emptyChoiceWithCase;
    emptyChoiceWithCase.initialize(parameterExpressions1);
    ASSERT_EQ(1, emptyChoiceWithCase.getSelector());
}

TEST_F(EmptyChoiceWithCaseTest, getSelector)
{
    EmptyChoiceWithCase emptyChoiceWithCase;
    emptyChoiceWithCase.initialize(parameterExpressions1);
    ASSERT_EQ(1, emptyChoiceWithCase.getSelector());
}

TEST_F(EmptyChoiceWithCaseTest, choiceTag)
{
    EmptyChoiceWithCase emptyChoiceWithCase;
    emptyChoiceWithCase.initialize(parameterExpressions0);
    ASSERT_EQ(EmptyChoiceWithCase::UNDEFINED_CHOICE, emptyChoiceWithCase.choiceTag());

    emptyChoiceWithCase.initialize(parameterExpressions1);
    ASSERT_EQ(EmptyChoiceWithCase::UNDEFINED_CHOICE, emptyChoiceWithCase.choiceTag());
}

TEST_F(EmptyChoiceWithCaseTest, bitSizeOf)
{
    EmptyChoiceWithCase emptyChoiceWithCase;
    emptyChoiceWithCase.initialize(parameterExpressions1);
    ASSERT_EQ(0, emptyChoiceWithCase.bitSizeOf(1));
}

TEST_F(EmptyChoiceWithCaseTest, initializeOffsets)
{
    const size_t bitPosition = 1;

    EmptyChoiceWithCase emptyChoiceWithCase;
    emptyChoiceWithCase.initialize(parameterExpressions1);
    ASSERT_EQ(bitPosition, emptyChoiceWithCase.initializeOffsets(bitPosition));
}

TEST_F(EmptyChoiceWithCaseTest, operatorEquality)
{
    EmptyChoiceWithCase emptyChoiceWithCase1;
    emptyChoiceWithCase1.initialize(parameterExpressions1);
    EmptyChoiceWithCase emptyChoiceWithCase2;
    emptyChoiceWithCase2.initialize(parameterExpressions1);
    EmptyChoiceWithCase emptyChoiceWithCase3;
    emptyChoiceWithCase3.initialize(parameterExpressions0);
    ASSERT_TRUE(emptyChoiceWithCase1 == emptyChoiceWithCase2);
    ASSERT_FALSE(emptyChoiceWithCase1 == emptyChoiceWithCase3);
}

TEST_F(EmptyChoiceWithCaseTest, hashCode)
{
    EmptyChoiceWithCase emptyChoiceWithCase1;
    emptyChoiceWithCase1.initialize(parameterExpressions1);
    EmptyChoiceWithCase emptyChoiceWithCase2;
    emptyChoiceWithCase2.initialize(parameterExpressions1);
    EmptyChoiceWithCase emptyChoiceWithCase3;
    emptyChoiceWithCase3.initialize(parameterExpressions0);
    ASSERT_EQ(emptyChoiceWithCase1.hashCode(), emptyChoiceWithCase2.hashCode());
    ASSERT_NE(emptyChoiceWithCase1.hashCode(), emptyChoiceWithCase3.hashCode());

    // use hardcoded values to check that the hash code is stable
    ASSERT_EQ(852, emptyChoiceWithCase1.hashCode());
    ASSERT_EQ(851, emptyChoiceWithCase3.hashCode());
}

TEST_F(EmptyChoiceWithCaseTest, write)
{
    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    EmptyChoiceWithCase emptyChoiceWithCase;
    emptyChoiceWithCase.initialize(parameterExpressions1);
    emptyChoiceWithCase.write(writer);
    ASSERT_EQ(0, writer.getBitPosition());

    zserio::BitStreamReader reader(writer.getWriteBuffer(), 0);
    EmptyChoiceWithCase readEmptyChoiceWithCase(reader, parameterExpressions1);
    ASSERT_EQ(emptyChoiceWithCase, readEmptyChoiceWithCase);
}

} // namespace empty_choice_with_case
} // namespace choice_types
