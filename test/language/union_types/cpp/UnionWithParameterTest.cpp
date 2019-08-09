#include "gtest/gtest.h"

#include "union_types/union_with_parameter/TestUnion.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

namespace union_types
{
namespace union_with_parameter
{

TEST(UnionWithParameterTest, bitStreamReaderConstructor)
{
    TestUnion testUnion;
    testUnion.initialize(true);
    testUnion.setCase3Field(-1);
    zserio::BitStreamWriter writer;
    testUnion.write(writer);

    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    TestUnion readTestUnion(reader, true);
    ASSERT_EQ(testUnion.choiceTag(), readTestUnion.choiceTag());
    ASSERT_EQ(testUnion.getCase3Field(), readTestUnion.getCase3Field());
}

TEST(UnionWithParameterTest, fieldConstructor)
{
    int32_t test1 = 13;
    TestUnion testUnion(test1);
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

TEST(UnionWithParameterTest, initialize)
{
    zserio::BitStreamWriter writer;
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
