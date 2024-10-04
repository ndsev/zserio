#include <memory>

#include "gtest/gtest.h"
#include "parameterized_types/param_with_optional/Holder.h"
#include "zserio/SerializeUtil.h"

namespace parameterized_types
{
namespace param_with_optional
{

using allocator_type = Holder::allocator_type;

class ParamWithOptionalTest : public ::testing::Test
{
protected:
    void fillHolder(Holder& holder)
    {
        const Param param(HAS_EXTRA, EXTRA_PARAM);
        holder.setParam(param);
        Value value;
        value.setExtraValue(ExtraValue(EXTRA_VALUE));
        holder.setValue(value);
        holder.initializeChildren();
    }

    void writeHolderToByteArray(zserio::BitStreamWriter& writer)
    {
        writer.writeBool(HAS_EXTRA);
        writer.writeBits(EXTRA_PARAM, 7);
        writer.writeBits64(EXTRA_VALUE, 64);
    }

    void checkHolder(const Holder& holder)
    {
        ASSERT_EQ(HAS_EXTRA, holder.getParam().getHasExtra());
        ASSERT_EQ(EXTRA_PARAM, holder.getParam().getExtraParam());
        ASSERT_EQ(EXTRA_VALUE, holder.getValue().getExtraValue().getValue());
    }

    static const std::string BLOB_NAME;

    static const bool HAS_EXTRA;
    static const uint8_t EXTRA_PARAM;
    static const uint64_t EXTRA_VALUE;
    static const size_t HOLDER_BIT_SIZE;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const std::string ParamWithOptionalTest::BLOB_NAME = "language/parameterized_types/param_with_optional.blob";

const bool ParamWithOptionalTest::HAS_EXTRA = true;
const uint8_t ParamWithOptionalTest::EXTRA_PARAM = 0x00;
const uint64_t ParamWithOptionalTest::EXTRA_VALUE = 0xDEAD;
const size_t ParamWithOptionalTest::HOLDER_BIT_SIZE = 1 + 7 + 64;

TEST_F(ParamWithOptionalTest, copyConstructor)
{
    Holder holder;
    fillHolder(holder);

    const Holder holderCopy(holder);
    ASSERT_EQ(holder, holderCopy);

    ASSERT_TRUE(holderCopy.getValue().isInitialized());
    ASSERT_TRUE(holderCopy.getValue().getExtraValue().isInitialized());
}

TEST_F(ParamWithOptionalTest, moveConstructor)
{
    Holder holder;
    fillHolder(holder);

    const Holder holderCopy(holder);
    const Holder holderMove(std::move(holder));
    ASSERT_EQ(holderCopy, holderMove);

    ASSERT_TRUE(holderMove.getValue().isInitialized());
    ASSERT_TRUE(holderMove.getValue().getExtraValue().isInitialized());
}

TEST_F(ParamWithOptionalTest, propagateAllocatorCopyConstructor)
{
    Holder holder;
    fillHolder(holder);

    allocator_type allocator;
    const Holder holderCopy{zserio::PropagateAllocator, holder, allocator};
    ASSERT_EQ(holder, holderCopy);

    ASSERT_TRUE(holderCopy.getValue().isInitialized());
    ASSERT_TRUE(holderCopy.getValue().getExtraValue().isInitialized());
}

TEST_F(ParamWithOptionalTest, assignmentOperator)
{
    Holder holder;
    fillHolder(holder);

    const Holder holderAssignment = holder;
    ASSERT_EQ(holder, holderAssignment);

    ASSERT_TRUE(holderAssignment.getValue().isInitialized());
    ASSERT_TRUE(holderAssignment.getValue().getExtraValue().isInitialized());
}

TEST_F(ParamWithOptionalTest, moveAssignmentOperator)
{
    Holder holder;
    fillHolder(holder);

    const Holder holderCopy(holder);
    Holder holderMoveAssignment;
    holderMoveAssignment = std::move(holder);
    ASSERT_EQ(holderCopy, holderMoveAssignment);

    ASSERT_TRUE(holderMoveAssignment.getValue().isInitialized());
    ASSERT_TRUE(holderMoveAssignment.getValue().getExtraValue().isInitialized());
}

TEST_F(ParamWithOptionalTest, bitSizeOf)
{
    Holder holder;
    fillHolder(holder);

    const size_t bitPosition = 2;
    ASSERT_EQ(HOLDER_BIT_SIZE, holder.bitSizeOf(bitPosition));
}

TEST_F(ParamWithOptionalTest, initializeOffsets)
{
    Holder holder;
    fillHolder(holder);

    const size_t bitPosition = 2;
    ASSERT_EQ(bitPosition + HOLDER_BIT_SIZE, holder.initializeOffsets(bitPosition));
}

TEST_F(ParamWithOptionalTest, readConstructor)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writeHolderToByteArray(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    const Holder holder(reader);
    checkHolder(holder);
}

TEST_F(ParamWithOptionalTest, writeRead)
{
    Holder holder;
    fillHolder(holder);

    zserio::BitStreamWriter writer(bitBuffer);
    holder.write(writer);

    ASSERT_EQ(holder.bitSizeOf(), writer.getBitPosition());
    ASSERT_EQ(holder.initializeOffsets(), writer.getBitPosition());

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    const Holder readHolder(reader);
    checkHolder(readHolder);
}

TEST_F(ParamWithOptionalTest, writeReadFile)
{
    Holder holder;
    fillHolder(holder);

    zserio::serializeToFile(holder, BLOB_NAME);
    const Holder readHolder = zserio::deserializeFromFile<Holder>(BLOB_NAME);
    checkHolder(readHolder);
}

} // namespace param_with_optional
} // namespace parameterized_types
