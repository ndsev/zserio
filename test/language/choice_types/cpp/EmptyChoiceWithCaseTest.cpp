#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

#include "choice_types/empty_choice_with_case/EmptyChoiceWithCase.h"

namespace choice_types
{
namespace empty_choice_with_case
{

TEST(EmptyChoiceWithCaseTest, emptyConstructor)
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

TEST(EmptyChoiceWithCaseTest, bitStreamReaderConstructor)
{
    const uint8_t selector = 1;
    zserio::BitStreamReader reader(nullptr, 0);

    EmptyChoiceWithCase emptyChoiceWithCase(reader, selector);
    ASSERT_EQ(selector, emptyChoiceWithCase.getSelector());
    ASSERT_EQ(0, emptyChoiceWithCase.bitSizeOf());
}

TEST(EmptyChoiceWithCaseTest, copyConstructor)
{
    const uint8_t selector = 1;

    EmptyChoiceWithCase emptyChoiceWithCase;
    emptyChoiceWithCase.initialize(selector);
    const EmptyChoiceWithCase emptyChoiceWithCaseCopy(emptyChoiceWithCase);
    ASSERT_EQ(selector, emptyChoiceWithCaseCopy.getSelector());
    ASSERT_EQ(0, emptyChoiceWithCaseCopy.bitSizeOf());
}

TEST(EmptyChoiceWithCaseTest, assignmentOperator)
{
    const uint8_t selector = 1;

    EmptyChoiceWithCase emptyChoiceWithCase;
    emptyChoiceWithCase.initialize(selector);
    EmptyChoiceWithCase emptyChoiceWithCaseCopy;
    emptyChoiceWithCaseCopy = emptyChoiceWithCase;
    ASSERT_EQ(selector, emptyChoiceWithCaseCopy.getSelector());
    ASSERT_EQ(0, emptyChoiceWithCaseCopy.bitSizeOf());
}

TEST(EmptyChoiceWithCaseTest, moveConstructor)
{
    const uint8_t selector = 1;

    EmptyChoiceWithCase emptyChoiceWithCase;
    emptyChoiceWithCase.initialize(selector);
    // note that it doesn't ensure that move ctor was called
    const EmptyChoiceWithCase emptyChoiceWithCaseMoved(std::move(emptyChoiceWithCase));
    ASSERT_EQ(selector, emptyChoiceWithCaseMoved.getSelector());
    ASSERT_EQ(0, emptyChoiceWithCaseMoved.bitSizeOf());
}

TEST(EmptyChoiceWithCaseTest, moveAssignmentOperator)
{
    const uint8_t selector = 1;

    EmptyChoiceWithCase emptyChoiceWithCase;
    emptyChoiceWithCase.initialize(selector);
    // note that it doesn't ensure that move ctor was called
    EmptyChoiceWithCase emptyChoiceWithCaseMoved;
    emptyChoiceWithCaseMoved = std::move(emptyChoiceWithCase);
    ASSERT_EQ(selector, emptyChoiceWithCaseMoved.getSelector());
    ASSERT_EQ(0, emptyChoiceWithCaseMoved.bitSizeOf());
}

TEST(EmptyChoiceWithCaseTest, propagateAllocatorCopyConstructor)
{
    const uint8_t selector = 1;

    EmptyChoiceWithCase emptyChoiceWithCase;
    emptyChoiceWithCase.initialize(selector);
    const EmptyChoiceWithCase emptyChoiceWithCaseCopy(zserio::PropagateAllocator, emptyChoiceWithCase,
            EmptyChoiceWithCase::allocator_type());
    ASSERT_EQ(selector, emptyChoiceWithCaseCopy.getSelector());
    ASSERT_EQ(0, emptyChoiceWithCaseCopy.bitSizeOf());
}

TEST(EmptyChoiceWithCaseTest, initialize)
{
    const uint8_t selector = 1;

    EmptyChoiceWithCase emptyChoiceWithCase;
    emptyChoiceWithCase.initialize(selector);
    ASSERT_EQ(selector, emptyChoiceWithCase.getSelector());
}

TEST(EmptyChoiceWithCaseTest, getSelector)
{
    const uint8_t selector = 1;

    EmptyChoiceWithCase emptyChoiceWithCase;
    emptyChoiceWithCase.initialize(selector);
    ASSERT_EQ(selector, emptyChoiceWithCase.getSelector());
}

TEST(EmptyChoiceWithCaseTest, choiceTag)
{
    EmptyChoiceWithCase emptyChoiceWithCase;
    emptyChoiceWithCase.initialize(0);
    ASSERT_EQ(EmptyChoiceWithCase::UNDEFINED_CHOICE, emptyChoiceWithCase.choiceTag());

    emptyChoiceWithCase.initialize(1);
    ASSERT_EQ(EmptyChoiceWithCase::UNDEFINED_CHOICE, emptyChoiceWithCase.choiceTag());
}

TEST(EmptyChoiceWithCaseTest, bitSizeOf)
{
    EmptyChoiceWithCase emptyChoiceWithCase;
    emptyChoiceWithCase.initialize(1);
    ASSERT_EQ(0, emptyChoiceWithCase.bitSizeOf(1));
}

TEST(EmptyChoiceWithCaseTest, initializeOffsets)
{
    const size_t bitPosition = 1;

    EmptyChoiceWithCase emptyChoiceWithCase;
    emptyChoiceWithCase.initialize(1);
    ASSERT_EQ(bitPosition, emptyChoiceWithCase.initializeOffsets(bitPosition));
}

TEST(EmptyChoiceWithCaseTest, operatorEquality)
{
    EmptyChoiceWithCase emptyChoiceWithCase1;
    emptyChoiceWithCase1.initialize(1);
    EmptyChoiceWithCase emptyChoiceWithCase2;
    emptyChoiceWithCase2.initialize(1);
    EmptyChoiceWithCase emptyChoiceWithCase3;
    emptyChoiceWithCase3.initialize(0);
    ASSERT_TRUE(emptyChoiceWithCase1 == emptyChoiceWithCase2);
    ASSERT_FALSE(emptyChoiceWithCase1 == emptyChoiceWithCase3);
}

TEST(EmptyChoiceWithCaseTest, hashCode)
{
    EmptyChoiceWithCase emptyChoiceWithCase1;
    emptyChoiceWithCase1.initialize(1);
    EmptyChoiceWithCase emptyChoiceWithCase2;
    emptyChoiceWithCase2.initialize(1);
    EmptyChoiceWithCase emptyChoiceWithCase3;
    emptyChoiceWithCase3.initialize(0);
    ASSERT_EQ(emptyChoiceWithCase1.hashCode(), emptyChoiceWithCase2.hashCode());
    ASSERT_NE(emptyChoiceWithCase1.hashCode(), emptyChoiceWithCase3.hashCode());

    // use hardcoded values to check that the hash code is stable
    ASSERT_EQ(852, emptyChoiceWithCase1.hashCode());
    ASSERT_EQ(851, emptyChoiceWithCase3.hashCode());
}

TEST(EmptyChoiceWithCaseTest, write)
{
    const uint8_t selector = 1;
    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    EmptyChoiceWithCase emptyChoiceWithCase;
    emptyChoiceWithCase.initialize(selector);
    emptyChoiceWithCase.write(writer);
    ASSERT_EQ(0, writer.getBitPosition());

    zserio::BitStreamReader reader(writer.getWriteBuffer(), 0);
    EmptyChoiceWithCase readEmptyChoiceWithCase(reader, selector);
    ASSERT_EQ(emptyChoiceWithCase, readEmptyChoiceWithCase);
}

} // namespace empty_choice_with_case
} // namespace choice_types
