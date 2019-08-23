#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

#include "structure_types/empty_structure_with_parameter/EmptyStructureWithParameter.h"

namespace structure_types
{
namespace empty_structure_with_parameter
{

TEST(EmptyStructureWithParameterTest, emptyConstructor)
{
    EmptyStructureWithParameter emptyStructureWithParameter;
    ASSERT_EQ(0, emptyStructureWithParameter.bitSizeOf());
    ASSERT_THROW(emptyStructureWithParameter.getParam(), zserio::CppRuntimeException);
}

TEST(EmptyStructureWithParameterTest, fieldConstructor)
{
    const int32_t param = 10;
    EmptyStructureWithParameter emptyStructureWithParameter;
    emptyStructureWithParameter.initialize(param);
    ASSERT_EQ(0, emptyStructureWithParameter.bitSizeOf());
    ASSERT_EQ(param, emptyStructureWithParameter.getParam());
}

TEST(EmptyStructureWithParameterTest, copyConstructor)
{
    EmptyStructureWithParameter testStructureWithParameter;
    EmptyStructureWithParameter testStructureWithParameterCopy1(testStructureWithParameter);

    ASSERT_THROW(testStructureWithParameter.getParam(), zserio::CppRuntimeException);
    ASSERT_THROW(testStructureWithParameterCopy1.getParam(), zserio::CppRuntimeException);

    testStructureWithParameter.initialize(1);
    EmptyStructureWithParameter testStructureWithParameterCopy2(testStructureWithParameter);
    ASSERT_EQ(testStructureWithParameter.getParam(), testStructureWithParameterCopy2.getParam());
}

TEST(EmptyStructureWithParameterTest, assignmentOperator)
{
    EmptyStructureWithParameter testStructureWithParameter;
    EmptyStructureWithParameter testStructureWithParameterAssign;
    testStructureWithParameterAssign = testStructureWithParameter;

    ASSERT_THROW(testStructureWithParameter.getParam(), zserio::CppRuntimeException);
    ASSERT_THROW(testStructureWithParameterAssign.getParam(), zserio::CppRuntimeException);

    testStructureWithParameter.initialize(1);
    testStructureWithParameterAssign = testStructureWithParameter;
    ASSERT_EQ(testStructureWithParameter.getParam(), testStructureWithParameterAssign.getParam());
}

TEST(EmptyStructureWithParameterTest, moveConstructor)
{
    {
        EmptyStructureWithParameter testStructureWithParameter;
        ASSERT_THROW(testStructureWithParameter.getParam(), zserio::CppRuntimeException);

        EmptyStructureWithParameter testStructureWithParameterMoved(std::move(testStructureWithParameter));
        ASSERT_THROW(testStructureWithParameterMoved.getParam(), zserio::CppRuntimeException);
    }

    {
        int32_t param = 1;
        EmptyStructureWithParameter testStructureWithParameter;
        testStructureWithParameter.initialize(param);
        ASSERT_EQ(param, testStructureWithParameter.getParam());

        EmptyStructureWithParameter testStructureWithParameterMoved(std::move(testStructureWithParameter));
        ASSERT_EQ(param, testStructureWithParameterMoved.getParam());
    }
}

TEST(EmptyStructureWithParameterTest, moveAssignmentOperator)
{
    {
        EmptyStructureWithParameter testStructureWithParameter;
        ASSERT_THROW(testStructureWithParameter.getParam(), zserio::CppRuntimeException);

        EmptyStructureWithParameter testStructureWithParameterMoved;
        testStructureWithParameterMoved = std::move(testStructureWithParameter);
        ASSERT_THROW(testStructureWithParameterMoved.getParam(), zserio::CppRuntimeException);
    }

    {
        int32_t param = 1;
        EmptyStructureWithParameter testStructureWithParameter;
        testStructureWithParameter.initialize(param);
        ASSERT_EQ(param, testStructureWithParameter.getParam());

        EmptyStructureWithParameter testStructureWithParameterMoved;
        testStructureWithParameterMoved = std::move(testStructureWithParameter);
        ASSERT_EQ(param, testStructureWithParameterMoved.getParam());
    }
}

TEST(EmptyStructureWithParameterTest, initialize)
{
    EmptyStructureWithParameter emptyStructureWithParameter;
    emptyStructureWithParameter.initialize(1);
    ASSERT_EQ(1, emptyStructureWithParameter.getParam());
}

TEST(EmptyStructureWithParameterTest, bitStreamReaderConstructor)
{
    zserio::BitStreamReader reader(NULL, 0);

    EmptyStructureWithParameter emptyStructureWithParameter(reader, 1);
    ASSERT_EQ(1, emptyStructureWithParameter.getParam());
    ASSERT_EQ(0, emptyStructureWithParameter.bitSizeOf());
}

TEST(EmptyStructureWithParameterTest, bitSizeOf)
{
    EmptyStructureWithParameter emptyStructureWithParameter;
    const size_t bitPosition = 1;
    ASSERT_EQ(0, emptyStructureWithParameter.bitSizeOf(bitPosition));
}

TEST(EmptyStructureWithParameterTest, initializeOffsets)
{
    EmptyStructureWithParameter emptyStructureWithParameter;
    const size_t bitPosition = 1;
    ASSERT_EQ(bitPosition, emptyStructureWithParameter.initializeOffsets(bitPosition));
}

TEST(EmptyStructureWithParameterTest, operatorEquality)
{
    EmptyStructureWithParameter emptyStructureWithParameter1;
    EmptyStructureWithParameter emptyStructureWithParameter2;
    ASSERT_THROW(emptyStructureWithParameter1 == emptyStructureWithParameter2, zserio::CppRuntimeException);

    emptyStructureWithParameter1.initialize(1);
    ASSERT_THROW(emptyStructureWithParameter1 == emptyStructureWithParameter2, zserio::CppRuntimeException);

    emptyStructureWithParameter2.initialize(1);
    ASSERT_TRUE(emptyStructureWithParameter1 == emptyStructureWithParameter2);

    emptyStructureWithParameter2.initialize(2);
    ASSERT_FALSE(emptyStructureWithParameter1 == emptyStructureWithParameter2);
}

TEST(EmptyStructureWithParameterTest, hashCode)
{
    EmptyStructureWithParameter emptyStructureWithParameter1;
    EmptyStructureWithParameter emptyStructureWithParameter2;
    ASSERT_THROW(emptyStructureWithParameter1.hashCode(), zserio::CppRuntimeException);
    ASSERT_THROW(emptyStructureWithParameter2.hashCode(), zserio::CppRuntimeException);

    emptyStructureWithParameter1.initialize(1);
    ASSERT_NO_THROW(emptyStructureWithParameter1.hashCode());

    emptyStructureWithParameter2.initialize(1);
    ASSERT_EQ(emptyStructureWithParameter1.hashCode(), emptyStructureWithParameter2.hashCode());

    emptyStructureWithParameter2.initialize(2);
    ASSERT_NE(emptyStructureWithParameter1.hashCode(), emptyStructureWithParameter2.hashCode());
}

TEST(EmptyStructureWithParameterTest, read)
{
    zserio::BitStreamReader reader(NULL, 0);
    const EmptyStructureWithParameter emptyStructureWithParameter(reader, 1);
    ASSERT_EQ(0, emptyStructureWithParameter.bitSizeOf());
}

TEST(EmptyStructureWithParameterTest, write)
{
    EmptyStructureWithParameter emptyStructureWithParameter;
    emptyStructureWithParameter.initialize(1);

    zserio::BitStreamWriter writer;
    emptyStructureWithParameter.write(writer);

    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    ASSERT_EQ(0, writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    EmptyStructureWithParameter readEmptyStructureWithParameter(reader, 1);
    ASSERT_TRUE(emptyStructureWithParameter == readEmptyStructureWithParameter);
}

} // namespace empty_structure_with_parameter
} // namespace structure_types
