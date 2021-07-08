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
    ASSERT_EQ(EmptyUnion::UNDEFINED_CHOICE, emptyUnion.choiceTag());
    ASSERT_EQ(0, emptyUnion.bitSizeOf());
}

TEST(EmptyUnionTest, bitStreamReaderConstructor)
{
    zserio::BitStreamReader reader(nullptr, 0);

    EmptyUnion emptyUnion(reader);
    ASSERT_EQ(EmptyUnion::UNDEFINED_CHOICE, emptyUnion.choiceTag());
    ASSERT_EQ(0, emptyUnion.bitSizeOf());
}

TEST(EmptyUnionTest, copyConstructor)
{
    EmptyUnion emptyUnion;
    EmptyUnion emptyUnionCopy(emptyUnion);
    ASSERT_EQ(EmptyUnion::UNDEFINED_CHOICE, emptyUnionCopy.choiceTag());
    ASSERT_EQ(0, emptyUnionCopy.bitSizeOf());
}

TEST(EmptyUnionTest, assignmentOperator)
{
    EmptyUnion emptyUnion;
    EmptyUnion emptyUnionCopy;
    emptyUnionCopy = emptyUnion;
    ASSERT_EQ(EmptyUnion::UNDEFINED_CHOICE, emptyUnionCopy.choiceTag());
    ASSERT_EQ(0, emptyUnionCopy.bitSizeOf());
}

TEST(EmptyUnionTest, moveConstructor)
{
    EmptyUnion emptyUnion;
    EmptyUnion emptyUnionMoved(std::move(emptyUnion));
    ASSERT_EQ(EmptyUnion::UNDEFINED_CHOICE, emptyUnionMoved.choiceTag());
    ASSERT_EQ(0, emptyUnionMoved.bitSizeOf());
}

TEST(EmptyUnionTest, moveAssignmentOperator)
{
    EmptyUnion emptyUnion;
    EmptyUnion emptyUnionMoved;
    emptyUnionMoved = std::move(emptyUnion);
    ASSERT_EQ(EmptyUnion::UNDEFINED_CHOICE, emptyUnionMoved.choiceTag());
    ASSERT_EQ(0, emptyUnionMoved.bitSizeOf());
}

TEST(EmptyUnionTest, propagateAllocatorCopyConstructor)
{
    EmptyUnion emptyUnion;
    EmptyUnion emptyUnionCopy(zserio::PropagateAllocator, emptyUnion, EmptyUnion::allocator_type());
    ASSERT_EQ(EmptyUnion::UNDEFINED_CHOICE, emptyUnionCopy.choiceTag());
    ASSERT_EQ(0, emptyUnionCopy.bitSizeOf());
}

TEST(EmptyUnionTest, choiceTag)
{
    EmptyUnion emptyUnion;
    ASSERT_EQ(EmptyUnion::UNDEFINED_CHOICE, emptyUnion.choiceTag());
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

TEST(EmptyUnionTest, write)
{
    EmptyUnion emptyUnion;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    emptyUnion.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    EmptyUnion readEmptyUnion(reader);
    ASSERT_TRUE(emptyUnion == readEmptyUnion);
}

} // namespace empty_union
} // namespace union_types
