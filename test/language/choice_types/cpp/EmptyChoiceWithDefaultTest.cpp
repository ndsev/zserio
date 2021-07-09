#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

#include "choice_types/empty_choice_with_default/EmptyChoiceWithDefault.h"

namespace choice_types
{
namespace empty_choice_with_default
{

TEST(EmptyChoiceWithDefaultTest, emptyConstructor)
{
    EmptyChoiceWithDefault emptyChoiceWithDefault;
    ASSERT_THROW(emptyChoiceWithDefault.getSelector(), zserio::CppRuntimeException);
}

TEST(EmptyChoiceWithDefaultTest, bitStreamReaderConstructor)
{
    const uint8_t selector = 1;
    zserio::BitStreamReader reader(nullptr, 0);

    EmptyChoiceWithDefault emptyChoiceWithDefault(reader, selector);
    ASSERT_EQ(selector, emptyChoiceWithDefault.getSelector());
    ASSERT_EQ(0, emptyChoiceWithDefault.bitSizeOf());
}

TEST(EmptyChoiceWithDefaultTest, copyConstructor)
{
    const uint8_t selector = 1;

    EmptyChoiceWithDefault emptyChoiceWithDefault;
    emptyChoiceWithDefault.initialize(selector);
    const EmptyChoiceWithDefault emptyChoiceWithDefaultCopy(emptyChoiceWithDefault);
    ASSERT_EQ(selector, emptyChoiceWithDefaultCopy.getSelector());
    ASSERT_EQ(0, emptyChoiceWithDefaultCopy.bitSizeOf());
}

TEST(EmptyChoiceWithDefaultTest, assignmentOperator)
{
    const uint8_t selector = 1;

    EmptyChoiceWithDefault emptyChoiceWithDefault;
    emptyChoiceWithDefault.initialize(selector);
    EmptyChoiceWithDefault emptyChoiceWithDefaultCopy;
    emptyChoiceWithDefaultCopy = emptyChoiceWithDefault;
    ASSERT_EQ(selector, emptyChoiceWithDefaultCopy.getSelector());
    ASSERT_EQ(0, emptyChoiceWithDefaultCopy.bitSizeOf());
}

TEST(EmptyChoiceWithDefaultTest, moveConstructor)
{
    const uint8_t selector = 1;

    EmptyChoiceWithDefault emptyChoiceWithDefault;
    emptyChoiceWithDefault.initialize(selector);
    // note that it doesn't ensure that move ctor was called
    const EmptyChoiceWithDefault emptyChoiceWithDefaultMoved(std::move(emptyChoiceWithDefault));
    ASSERT_EQ(selector, emptyChoiceWithDefaultMoved.getSelector());
    ASSERT_EQ(0, emptyChoiceWithDefaultMoved.bitSizeOf());
}

TEST(EmptyChoiceWithDefaultTest, moveAssignmentOperator)
{
    const uint8_t selector = 1;

    EmptyChoiceWithDefault emptyChoiceWithDefault;
    emptyChoiceWithDefault.initialize(selector);
    // note that it doesn't ensure that move ctor was called
    EmptyChoiceWithDefault emptyChoiceWithDefaultMoved;
    emptyChoiceWithDefaultMoved = std::move(emptyChoiceWithDefault);
    ASSERT_EQ(selector, emptyChoiceWithDefaultMoved.getSelector());
    ASSERT_EQ(0, emptyChoiceWithDefaultMoved.bitSizeOf());
}

TEST(EmptyChoiceWithDefaultTest, propagateAllocatorCopyConstructor)
{
    const uint8_t selector = 1;

    EmptyChoiceWithDefault emptyChoiceWithDefault;
    emptyChoiceWithDefault.initialize(selector);
    const EmptyChoiceWithDefault emptyChoiceWithDefaultCopy(zserio::PropagateAllocator, emptyChoiceWithDefault,
            EmptyChoiceWithDefault::allocator_type());
    ASSERT_EQ(selector, emptyChoiceWithDefaultCopy.getSelector());
    ASSERT_EQ(0, emptyChoiceWithDefaultCopy.bitSizeOf());
}

TEST(EmptyChoiceWithDefaultTest, initialize)
{
    const uint8_t selector = 1;

    EmptyChoiceWithDefault emptyChoiceWithDefault;
    emptyChoiceWithDefault.initialize(selector);
    ASSERT_EQ(selector, emptyChoiceWithDefault.getSelector());
}

TEST(EmptyChoiceWithDefaultTest, getSelector)
{
    const uint8_t selector = 1;

    EmptyChoiceWithDefault emptyChoiceWithDefault;
    emptyChoiceWithDefault.initialize(selector);
    ASSERT_EQ(selector, emptyChoiceWithDefault.getSelector());
}

TEST(EmptyChoiceWithDefaultTest, bitSizeOf)
{
    EmptyChoiceWithDefault emptyChoiceWithDefault;
    emptyChoiceWithDefault.initialize(1);
    ASSERT_EQ(0, emptyChoiceWithDefault.bitSizeOf(1));
}

TEST(EmptyChoiceWithDefaultTest, initializeOffsets)
{
    const size_t bitPosition = 1;

    EmptyChoiceWithDefault emptyChoiceWithDefault;
    emptyChoiceWithDefault.initialize(1);
    ASSERT_EQ(bitPosition, emptyChoiceWithDefault.initializeOffsets(bitPosition));
}

TEST(EmptyChoiceWithDefaultTest, operatorEquality)
{
    EmptyChoiceWithDefault emptyChoiceWithDefault1;
    emptyChoiceWithDefault1.initialize(1);
    EmptyChoiceWithDefault emptyChoiceWithDefault2;
    emptyChoiceWithDefault2.initialize(1);
    EmptyChoiceWithDefault emptyChoiceWithDefault3;
    emptyChoiceWithDefault3.initialize(0);
    ASSERT_TRUE(emptyChoiceWithDefault1 == emptyChoiceWithDefault2);
    ASSERT_FALSE(emptyChoiceWithDefault1 == emptyChoiceWithDefault3);
}

TEST(EmptyChoiceWithDefaultTest, hashCode)
{
    EmptyChoiceWithDefault emptyChoiceWithDefault1;
    emptyChoiceWithDefault1.initialize(1);
    EmptyChoiceWithDefault emptyChoiceWithDefault2;
    emptyChoiceWithDefault2.initialize(1);
    EmptyChoiceWithDefault emptyChoiceWithDefault3;
    emptyChoiceWithDefault3.initialize(0);
    ASSERT_EQ(emptyChoiceWithDefault1.hashCode(), emptyChoiceWithDefault2.hashCode());
    ASSERT_NE(emptyChoiceWithDefault1.hashCode(), emptyChoiceWithDefault3.hashCode());
}

TEST(EmptyChoiceWithDefaultTest, write)
{
    const uint8_t selector = 1;
    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    EmptyChoiceWithDefault emptyChoiceWithDefault;
    emptyChoiceWithDefault.initialize(selector);
    emptyChoiceWithDefault.write(writer);
    ASSERT_EQ(0, writer.getBitPosition());

    zserio::BitStreamReader reader(writer.getWriteBuffer(), 0);
    EmptyChoiceWithDefault readEmptyChoiceWithDefault(reader, selector);
    ASSERT_EQ(emptyChoiceWithDefault, readEmptyChoiceWithDefault);
}

} // namespace empty_choice_with_default
} // namespace choice_types
