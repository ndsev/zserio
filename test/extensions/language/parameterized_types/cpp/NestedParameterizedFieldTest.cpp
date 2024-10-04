#include <memory>

#include "gtest/gtest.h"
#include "parameterized_types/nested_parameterized_field/TopLevel.h"
#include "zserio/SerializeUtil.h"

namespace parameterized_types
{
namespace nested_parameterized_field
{

using allocator_type = TopLevel::allocator_type;

class NestedParameterizedFieldTest : public ::testing::Test
{
protected:
    void fillTopLevel(TopLevel& topLevel)
    {
        const Param param(VALUE, EXTRA_VALUE);
        ParamHolder paramHolder;
        paramHolder.setParam(param);
        topLevel.setParamHolder(paramHolder);
        topLevel.initializeChildren();
    }

    void writeTopLevelToByteArray(zserio::BitStreamWriter& writer)
    {
        writer.writeBits(PARAMETER, 16);
        writer.writeBits(VALUE, 16);
        writer.writeBits(EXTRA_VALUE, 32);
    }

    void checkTopLevel(const TopLevel& topLevel)
    {
        ASSERT_EQ(PARAMETER, topLevel.getParamHolder().getParameter());
        ASSERT_EQ(VALUE, topLevel.getParamHolder().getParam().getValue());
        ASSERT_EQ(EXTRA_VALUE, topLevel.getParamHolder().getParam().getExtraValue());
    }

    static const std::string BLOB_NAME;

    static const uint16_t PARAMETER;
    static const uint16_t VALUE;
    static const uint32_t EXTRA_VALUE;
    static const size_t HOLDER_BIT_SIZE;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const std::string NestedParameterizedFieldTest::BLOB_NAME =
        "language/parameterized_types/nested_parameterized_field.blob";

const uint16_t NestedParameterizedFieldTest::PARAMETER = 11;
const uint16_t NestedParameterizedFieldTest::VALUE = 0xAB;
const uint32_t NestedParameterizedFieldTest::EXTRA_VALUE = 0xDEAD;
const size_t NestedParameterizedFieldTest::HOLDER_BIT_SIZE = 16 + 16 + 32;

TEST_F(NestedParameterizedFieldTest, copyConstructor)
{
    TopLevel topLevel;
    fillTopLevel(topLevel);

    const TopLevel topLevelCopy(topLevel);
    ASSERT_EQ(topLevel, topLevelCopy);

    ASSERT_TRUE(topLevelCopy.getParamHolder().getParam().isInitialized());
    ASSERT_EQ(PARAMETER, topLevelCopy.getParamHolder().getParam().getParameter());
}

TEST_F(NestedParameterizedFieldTest, moveConstructor)
{
    TopLevel topLevel;
    fillTopLevel(topLevel);

    const TopLevel topLevelCopy(topLevel);
    const TopLevel topLevelMove(std::move(topLevel));
    ASSERT_EQ(topLevelCopy, topLevelMove);

    ASSERT_TRUE(topLevelMove.getParamHolder().getParam().isInitialized());
    ASSERT_EQ(PARAMETER, topLevelMove.getParamHolder().getParam().getParameter());
}

TEST_F(NestedParameterizedFieldTest, propagateAllocatorCopyConstructor)
{
    TopLevel topLevel;
    fillTopLevel(topLevel);

    allocator_type allocator;
    const TopLevel topLevelCopy{zserio::PropagateAllocator, topLevel, allocator};
    ASSERT_EQ(topLevel, topLevelCopy);

    ASSERT_TRUE(topLevelCopy.getParamHolder().getParam().isInitialized());
    ASSERT_EQ(PARAMETER, topLevelCopy.getParamHolder().getParam().getParameter());
}

TEST_F(NestedParameterizedFieldTest, assignmentOperator)
{
    TopLevel topLevel;
    fillTopLevel(topLevel);

    const TopLevel topLevelAssignment = topLevel;
    ASSERT_EQ(topLevel, topLevelAssignment);

    ASSERT_TRUE(topLevelAssignment.getParamHolder().getParam().isInitialized());
    ASSERT_EQ(PARAMETER, topLevelAssignment.getParamHolder().getParam().getParameter());
}

TEST_F(NestedParameterizedFieldTest, moveAssignmentOperator)
{
    TopLevel topLevel;
    fillTopLevel(topLevel);

    const TopLevel topLevelCopy(topLevel);
    TopLevel topLevelMoveAssignment;
    topLevelMoveAssignment = std::move(topLevel);
    ASSERT_EQ(topLevelCopy, topLevelMoveAssignment);

    ASSERT_TRUE(topLevelMoveAssignment.getParamHolder().getParam().isInitialized());
    ASSERT_EQ(PARAMETER, topLevelMoveAssignment.getParamHolder().getParam().getParameter());
}

TEST_F(NestedParameterizedFieldTest, bitSizeOf)
{
    TopLevel topLevel;
    fillTopLevel(topLevel);

    const size_t bitPosition = 2;
    ASSERT_EQ(HOLDER_BIT_SIZE, topLevel.bitSizeOf(bitPosition));
}

TEST_F(NestedParameterizedFieldTest, initializeOffsets)
{
    TopLevel topLevel;
    fillTopLevel(topLevel);

    const size_t bitPosition = 2;
    ASSERT_EQ(bitPosition + HOLDER_BIT_SIZE, topLevel.initializeOffsets(bitPosition));
}

TEST_F(NestedParameterizedFieldTest, readConstructor)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writeTopLevelToByteArray(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    const TopLevel topLevel(reader);
    checkTopLevel(topLevel);
}

TEST_F(NestedParameterizedFieldTest, writeRead)
{
    TopLevel topLevel;
    fillTopLevel(topLevel);

    zserio::BitStreamWriter writer(bitBuffer);
    topLevel.write(writer);

    ASSERT_EQ(topLevel.bitSizeOf(), writer.getBitPosition());
    ASSERT_EQ(topLevel.initializeOffsets(), writer.getBitPosition());

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    const TopLevel readTopLevel(reader);
    checkTopLevel(readTopLevel);
}

TEST_F(NestedParameterizedFieldTest, writeReadFile)
{
    TopLevel topLevel;
    fillTopLevel(topLevel);

    zserio::serializeToFile(topLevel, BLOB_NAME);
    const TopLevel readTopLevel = zserio::deserializeFromFile<TopLevel>(BLOB_NAME);
    checkTopLevel(readTopLevel);
}

} // namespace nested_parameterized_field
} // namespace parameterized_types
