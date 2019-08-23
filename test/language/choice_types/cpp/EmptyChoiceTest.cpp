#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

#include "choice_types/empty_choice/EmptyChoice.h"

namespace choice_types
{
namespace empty_choice
{

TEST(EmptyChoiceTest, emptyConstructor)
{
    EmptyChoice emptyChoice;
    ASSERT_THROW(emptyChoice.getSelector(), zserio::CppRuntimeException);
}

TEST(EmptyChoiceTest, bitStreamReaderConstructor)
{
    const uint8_t selector = 1;
    zserio::BitStreamReader reader(NULL, 0);

    EmptyChoice emptyChoice(reader, selector);
    ASSERT_EQ(selector, emptyChoice.getSelector());
    ASSERT_EQ(0, emptyChoice.bitSizeOf());
}

TEST(EmptyChoiceTest, copyConstructor)
{
    const uint8_t selector = 1;

    EmptyChoice emptyChoice;
    emptyChoice.initialize(selector);
    const EmptyChoice emptyChoiceCopy(emptyChoice);
    ASSERT_EQ(selector, emptyChoiceCopy.getSelector());
    ASSERT_EQ(0, emptyChoiceCopy.bitSizeOf());
}

TEST(EmptyChoiceTest, assignmentOperator)
{
    const uint8_t selector = 1;

    EmptyChoice emptyChoice;
    emptyChoice.initialize(selector);
    EmptyChoice emptyChoiceCopy;
    emptyChoiceCopy = emptyChoice;
    ASSERT_EQ(selector, emptyChoiceCopy.getSelector());
    ASSERT_EQ(0, emptyChoiceCopy.bitSizeOf());
}

TEST(EmptyChoiceTest, moveConstructor)
{
    const uint8_t selector = 1;

    EmptyChoice emptyChoice;
    emptyChoice.initialize(selector);
    // note that it doesn't ensure that move ctor was called
    const EmptyChoice emptyChoiceMoved(std::move(emptyChoice));
    ASSERT_EQ(selector, emptyChoiceMoved.getSelector());
    ASSERT_EQ(0, emptyChoiceMoved.bitSizeOf());
}

TEST(EmptyChoiceTest, moveAssignmentOperator)
{
    const uint8_t selector = 1;

    EmptyChoice emptyChoice;
    emptyChoice.initialize(selector);
    // note that it doesn't ensure that move ctor was called
    EmptyChoice emptyChoiceMove;
    emptyChoiceMove = std::move(emptyChoice);
    ASSERT_EQ(selector, emptyChoiceMove.getSelector());
    ASSERT_EQ(0, emptyChoiceMove.bitSizeOf());
}

TEST(EmptyChoiceTest, initialize)
{
    const uint8_t selector = 1;

    EmptyChoice emptyChoice;
    emptyChoice.initialize(selector);
    ASSERT_EQ(selector, emptyChoice.getSelector());
}

TEST(EmptyChoiceTest, getSelector)
{
    const uint8_t selector = 1;

    EmptyChoice emptyChoice;
    emptyChoice.initialize(selector);
    ASSERT_EQ(selector, emptyChoice.getSelector());
}

TEST(EmptyChoiceTest, bitSizeOf)
{
    EmptyChoice emptyChoice;
    emptyChoice.initialize(1);
    ASSERT_EQ(0, emptyChoice.bitSizeOf(1));
}

TEST(EmptyChoiceTest, initializeOffsets)
{
    const size_t bitPosition = 1;

    EmptyChoice emptyChoice;
    emptyChoice.initialize(1);
    ASSERT_EQ(bitPosition, emptyChoice.initializeOffsets(bitPosition));
}

TEST(EmptyChoiceTest, operatorEquality)
{
    EmptyChoice emptyChoice1;
    emptyChoice1.initialize(1);
    EmptyChoice emptyChoice2;
    emptyChoice2.initialize(1);
    EmptyChoice emptyChoice3;
    emptyChoice3.initialize(0);
    ASSERT_TRUE(emptyChoice1 == emptyChoice2);
    ASSERT_FALSE(emptyChoice1 == emptyChoice3);
}

TEST(EmptyChoiceTest, hashCode)
{
    EmptyChoice emptyChoice1;
    emptyChoice1.initialize(1);
    EmptyChoice emptyChoice2;
    emptyChoice2.initialize(1);
    EmptyChoice emptyChoice3;
    emptyChoice3.initialize(0);
    ASSERT_EQ(emptyChoice1.hashCode(), emptyChoice2.hashCode());
    ASSERT_NE(emptyChoice1.hashCode(), emptyChoice3.hashCode());
}

TEST(EmptyChoiceTest, read)
{
    const uint8_t selector = 1;
    zserio::BitStreamReader reader(NULL, 0);
    const EmptyChoice emptyChoice(reader, selector);
    ASSERT_EQ(selector, emptyChoice.getSelector());
    ASSERT_EQ(0, emptyChoice.bitSizeOf());
}

TEST(EmptyChoiceTest, write)
{
    const uint8_t selector = 1;
    zserio::BitStreamWriter writer;
    EmptyChoice emptyChoice;
    emptyChoice.initialize(selector);
    emptyChoice.write(writer);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    ASSERT_EQ(0, writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    EmptyChoice readEmptyChoice(reader, selector);
    ASSERT_EQ(emptyChoice, readEmptyChoice);
}

} // namespace empty_choice
} // namespace choice_types
