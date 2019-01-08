#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

#include "choice_types/empty_choice_with_case/EmptyChoiceWithCase.h"

namespace choice_types
{
namespace empty_choice_with_case
{

TEST(EmptyChoiceWithCaseTest, selectorConstructor)
{
    EmptyChoiceWithCase emptyChoiceWithCase;
    ASSERT_THROW(emptyChoiceWithCase.getSelector(), zserio::CppRuntimeException);
}

TEST(EmptyChoiceWithCaseTest, bitStreamReaderConstructor)
{
    const uint8_t selector = 1;
    zserio::BitStreamReader reader(NULL, 0);

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

TEST(EmptyChoiceWithCaseTest, operatorAssignment)
{
    const uint8_t selector = 1;

    EmptyChoiceWithCase emptyChoiceWithCase;
    emptyChoiceWithCase.initialize(selector);
    const EmptyChoiceWithCase emptyChoiceWithCaseCopy = emptyChoiceWithCase;
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
}

TEST(EmptyChoiceWithCaseTest, read)
{
    const uint8_t selector = 1;
    zserio::BitStreamReader reader(NULL, 0);

    EmptyChoiceWithCase emptyChoiceWithCase;
    emptyChoiceWithCase.initialize(selector);
    emptyChoiceWithCase.read(reader);
    ASSERT_EQ(selector, emptyChoiceWithCase.getSelector());
    ASSERT_EQ(0, emptyChoiceWithCase.bitSizeOf());
}

TEST(EmptyChoiceWithCaseTest, write)
{
    const uint8_t selector = 1;
    zserio::BitStreamWriter writer;
    EmptyChoiceWithCase emptyChoiceWithCase;
    emptyChoiceWithCase.initialize(selector);
    emptyChoiceWithCase.write(writer);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    ASSERT_EQ(0, writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    EmptyChoiceWithCase readEmptyChoiceWithCase(reader, selector);
    ASSERT_EQ(emptyChoiceWithCase, readEmptyChoiceWithCase);
}

} // namespace empty_choice_with_case
} // namespace choice_types
