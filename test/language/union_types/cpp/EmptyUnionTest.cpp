#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "union_types/empty_union/EmptyUnion.h"

namespace union_types
{
namespace empty_union
{

TEST(EmptyUnionTest, emptyConstructor)
{
    EmptyUnion emptyUnion;
    ASSERT_EQ(EmptyUnion::CHOICE_UNDEFINED, emptyUnion.choiceTag());
    ASSERT_EQ(0, emptyUnion.bitSizeOf());
}

TEST(EmptyUnionTest, bitStreamReaderConstructor)
{
    zserio::BitStreamReader reader(NULL, 0);

    EmptyUnion emptyUnion(reader);
    ASSERT_EQ(EmptyUnion::CHOICE_UNDEFINED, emptyUnion.choiceTag());
    ASSERT_EQ(0, emptyUnion.bitSizeOf());
}

TEST(EmptyUnionTest, choiceTag)
{
    EmptyUnion emptyUnion;
    ASSERT_EQ(EmptyUnion::CHOICE_UNDEFINED, emptyUnion.choiceTag());
}

TEST(EmptyUnionTest, bitSizeOf)
{
    EmptyUnion emptyUnion;
    const size_t bitPosition = 1;
    ASSERT_EQ(0, emptyUnion.bitSizeOf(bitPosition));
}

TEST(EmptyUnionTest, initializeOffsets)
{
    const size_t bitPosition = 1;
    EmptyUnion emptyUnion;
    ASSERT_EQ(bitPosition, emptyUnion.initializeOffsets(bitPosition));
}

TEST(EmptyUnionTest, operatorEquality)
{
    EmptyUnion emptyUnion1;
    EmptyUnion emptyUnion2;
    ASSERT_TRUE(emptyUnion1 == emptyUnion2);
}

TEST(EmptyUnionTest, hashCode)
{
    EmptyUnion emptyUnion1;
    EmptyUnion emptyUnion2;
    ASSERT_EQ(emptyUnion1.hashCode(), emptyUnion2.hashCode());
}

TEST(EmptyUnionTest, read)
{
    zserio::BitStreamReader reader(NULL, 0);
    EmptyUnion emptyUnion;
    emptyUnion.read(reader);
    ASSERT_EQ(EmptyUnion::CHOICE_UNDEFINED, emptyUnion.choiceTag());
    ASSERT_EQ(0, emptyUnion.bitSizeOf());
}

TEST(EmptyUnionTest, write)
{
    EmptyUnion emptyUnion;

    zserio::BitStreamWriter writer;
    emptyUnion.write(writer);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);

    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    EmptyUnion readEmptyUnion(reader);
    ASSERT_TRUE(emptyUnion == readEmptyUnion);
}

} // namespace empty_union
} // namespace union_types
