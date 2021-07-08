#include "gtest/gtest.h"

#include "union_types/union_with_parameter/TestUnion.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

namespace union_types
{
namespace union_with_parameter
{

TEST(UnionWithParameterTest, emptyConstructor)
{
    TestUnion testUnion;
    ASSERT_THROW(testUnion.getCase1Allowed(), zserio::CppRuntimeException);
}

TEST(UnionWithParameterTest, bitStreamReaderConstructor)
{
    TestUnion testUnion;
    testUnion.initialize(true);
    testUnion.setCase3Field(-1);
    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    testUnion.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    TestUnion readTestUnion(reader, true);
    ASSERT_EQ(testUnion.choiceTag(), readTestUnion.choiceTag());
    ASSERT_EQ(testUnion.getCase3Field(), readTestUnion.getCase3Field());
}

TEST(UnionWithParameterTest, fieldConstructor)
{
    int32_t test1 = 13;
    TestUnion testUnion(test1);
    ASSERT_THROW(testUnion.getCase1Allowed(), zserio::CppRuntimeException);
    testUnion.initialize(true);

    ASSERT_EQ(true, testUnion.getCase1Allowed());
    ASSERT_EQ(test1, testUnion.getCase1Field());
}

TEST(UnionWithParameter, copyConstructor)
{
    TestUnion testUnion;
    TestUnion testUnionCopy1(testUnion);
    ASSERT_THROW(testUnion.getCase1Allowed(), zserio::CppRuntimeException);
    ASSERT_THROW(testUnionCopy1.getCase1Allowed(), zserio::CppRuntimeException);

    testUnion.initialize(true);
    testUnion.setCase1Field(33);
    TestUnion testUnionCopy2(testUnion);
    ASSERT_EQ(testUnion.getCase1Allowed(), testUnionCopy2.getCase1Allowed());
    ASSERT_EQ(testUnion.getCase1Field(), testUnionCopy2.getCase1Field());

    testUnion.initialize(false);
    testUnion.setCase2Field(13);
    TestUnion testUnionCopy3(testUnion);
    ASSERT_EQ(testUnion.getCase1Allowed(), testUnionCopy3.getCase1Allowed());
    ASSERT_EQ(testUnion.getCase2Field(), testUnionCopy3.getCase2Field());
}

TEST(UnionWithParameter, assignmentOperator)
{
    TestUnion testUnion;
    TestUnion testUnionAssign;
    testUnionAssign = testUnion;
    ASSERT_THROW(testUnion.getCase1Allowed(), zserio::CppRuntimeException);
    ASSERT_THROW(testUnionAssign.getCase1Allowed(), zserio::CppRuntimeException);

    testUnion.initialize(true);
    testUnion.setCase1Field(33);
    testUnionAssign = testUnion;
    ASSERT_EQ(testUnion.getCase1Allowed(), testUnionAssign.getCase1Allowed());
    ASSERT_EQ(testUnion.getCase1Field(), testUnionAssign.getCase1Field());

    testUnion.initialize(false);
    testUnion.setCase2Field(13);
    testUnionAssign = testUnion;
    ASSERT_EQ(testUnion.getCase1Allowed(), testUnionAssign.getCase1Allowed());
    ASSERT_EQ(testUnion.getCase2Field(), testUnionAssign.getCase2Field());
}

TEST(UnionWithParameter, moveConstructor)
{
    {
        TestUnion testUnion;
        TestUnion testUnionMoved1(std::move(testUnion));
        ASSERT_THROW(testUnionMoved1.getCase1Allowed(), zserio::CppRuntimeException);
    }

    {
        TestUnion testUnion;
        testUnion.initialize(true);
        testUnion.setCase1Field(33);
        TestUnion testUnionMoved2(std::move(testUnion));
        ASSERT_EQ(true, testUnionMoved2.getCase1Allowed());
        ASSERT_EQ(33, testUnionMoved2.getCase1Field());
    }

    {
        TestUnion testUnion;
        testUnion.initialize(false);
        testUnion.setCase2Field(13);
        TestUnion testUnionMoved3(std::move(testUnion));
        ASSERT_EQ(false, testUnionMoved3.getCase1Allowed());
        ASSERT_EQ(13, testUnionMoved3.getCase2Field());
    }
}

TEST(UnionWithParameter, moveAssignmentOperator)
{
    TestUnion testUnionMoved;

    {
        TestUnion testUnion;
        testUnionMoved = std::move(testUnion);
        ASSERT_THROW(testUnionMoved.getCase1Allowed(), zserio::CppRuntimeException);
    }

    {
        TestUnion testUnion;
        testUnion.initialize(true);
        testUnion.setCase1Field(33);
        testUnionMoved = testUnion;
        ASSERT_EQ(true, testUnionMoved.getCase1Allowed());
        ASSERT_EQ(33, testUnionMoved.getCase1Field());
    }

    {
        TestUnion testUnion;
        testUnion.initialize(false);
        testUnion.setCase2Field(13);
        testUnionMoved = testUnion;
        ASSERT_EQ(false, testUnionMoved.getCase1Allowed());
        ASSERT_EQ(13, testUnionMoved.getCase2Field());
    }
}

TEST(UnionWithParameter, propagateAllocatorCopyConstructor)
{
    TestUnion testUnion;
    TestUnion testUnionCopy1(zserio::PropagateAllocator, testUnion, TestUnion::allocator_type());
    ASSERT_THROW(testUnion.getCase1Allowed(), zserio::CppRuntimeException);
    ASSERT_THROW(testUnionCopy1.getCase1Allowed(), zserio::CppRuntimeException);

    testUnion.initialize(true);
    testUnion.setCase1Field(33);
    TestUnion testUnionCopy2(zserio::PropagateAllocator, testUnion, TestUnion::allocator_type());
    ASSERT_EQ(testUnion.getCase1Allowed(), testUnionCopy2.getCase1Allowed());
    ASSERT_EQ(testUnion.getCase1Field(), testUnionCopy2.getCase1Field());

    testUnion.initialize(false);
    testUnion.setCase2Field(13);
    TestUnion testUnionCopy3(zserio::PropagateAllocator, testUnion, TestUnion::allocator_type());
    ASSERT_EQ(testUnion.getCase1Allowed(), testUnionCopy3.getCase1Allowed());
    ASSERT_EQ(testUnion.getCase2Field(), testUnionCopy3.getCase2Field());
}

TEST(UnionWithParameterTest, initialize)
{
    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    TestUnion testUnion;
    ASSERT_THROW(testUnion.getCase1Allowed(), zserio::CppRuntimeException);
    ASSERT_THROW(testUnion.write(writer), zserio::CppRuntimeException);

    testUnion.setCase2Field(33);
    // not initialized but doesn't touch parameter - OK
    ASSERT_NO_THROW(testUnion.write(writer));

    testUnion.setCase1Field(13);
    // not initialized but touch parameter!
    ASSERT_THROW(testUnion.write(writer), zserio::CppRuntimeException);

    testUnion.initialize(true);
    ASSERT_TRUE(testUnion.getCase1Allowed());
    ASSERT_NO_THROW(testUnion.write(writer));
}

TEST(UnionWithParameterTestTest, isInitialized)
{
    TestUnion testUnion;
    ASSERT_FALSE(testUnion.isInitialized());
    testUnion.initialize(true);
    ASSERT_TRUE(testUnion.isInitialized());
}


} //namespace union_with_parameter
} // namespace union_types
