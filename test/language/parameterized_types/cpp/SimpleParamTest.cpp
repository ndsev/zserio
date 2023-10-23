#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

#include "parameterized_types/simple_param/Item.h"

namespace parameterized_types
{
namespace simple_param
{

class SimpleParamTest : public ::testing::Test
{
protected:
    void writeItemToByteArray(zserio::BitStreamWriter& writer, uint32_t version, uint16_t param,
            uint32_t extraParam)
    {
        writer.writeBits(param, 16);
        if (version >= HIGHER_VERSION)
            writer.writeBits(extraParam, 32);
    }

    void checkItemInBitStream(zserio::BitStreamReader& reader, uint32_t version, const Item& item)
    {
        ASSERT_EQ(item.getParam(), reader.readBits(16));
        if (version >= HIGHER_VERSION)
        {
            ASSERT_EQ(item.getExtraParam(), reader.readBits(32));
        }
    }

    static const uint32_t LOWER_VERSION;
    static const uint32_t HIGHER_VERSION;

    static const uint16_t ITEM_PARAM;
    static const uint32_t ITEM_EXTRA_PARAM;

    static const size_t ITEM_BIT_SIZE_WITHOUT_OPTIONAL;
    static const size_t ITEM_BIT_SIZE_WITH_OPTIONAL;

    Item::ParameterExpressions parameterExpressionsLower = {
            nullptr, 0, [](void*, size_t) { return LOWER_VERSION; } };
    Item::ParameterExpressions parameterExpressionsHigher = {
            nullptr, 0, [](void*, size_t) { return HIGHER_VERSION; } };

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const uint32_t SimpleParamTest::LOWER_VERSION = 9;
const uint32_t SimpleParamTest::HIGHER_VERSION = 10;

const uint16_t SimpleParamTest::ITEM_PARAM = 0xAA;
const uint32_t SimpleParamTest::ITEM_EXTRA_PARAM = 0xBB;

const size_t SimpleParamTest::ITEM_BIT_SIZE_WITHOUT_OPTIONAL = 16;
const size_t SimpleParamTest::ITEM_BIT_SIZE_WITH_OPTIONAL = 16 + 32;

TEST_F(SimpleParamTest, emptyConstructor)
{
    {
        Item item;
        item.initialize(parameterExpressionsLower);
        ASSERT_EQ(parameterExpressions.version, item.getVersion());
        ASSERT_FALSE(item.isExtraParamUsed());
    }

    {
        Item item = {};
        item.initialize(parameterExpressionsLower);
        ASSERT_EQ(parameterExpressions.version, item.getVersion());
        ASSERT_EQ(0, item.getParam());
        ASSERT_FALSE(item.isExtraParamUsed());
    }
}

TEST_F(SimpleParamTest, fieldConstructor)
{
    {
        Item item(ITEM_PARAM, zserio::NullOpt);
        ASSERT_FALSE(item.isInitialized());
        item.initialize(parameterExpressionsLower);
        ASSERT_FALSE(item.isExtraParamUsed());
    }

    {
        Item item(ITEM_PARAM, ITEM_EXTRA_PARAM);
        ASSERT_FALSE(item.isInitialized());
        item.initialize(parameterExpressionsHigher);
        ASSERT_TRUE(item.isExtraParamUsed());
        ASSERT_EQ(ITEM_EXTRA_PARAM, item.getExtraParam());
    }

    {
        Item item({}, {});
        ASSERT_FALSE(item.isInitialized());
        item.initialize(parameterExpressionsHigher);
        ASSERT_TRUE(item.isExtraParamUsed());
        ASSERT_EQ(0, item.getParam());
        ASSERT_EQ(0, item.getExtraParam());
    }
}

TEST_F(SimpleParamTest, bitStreamReaderConstructor)
{
    zserio::BitStreamWriter writer(bitBuffer);
    const uint16_t itemParam = ITEM_PARAM;
    const uint32_t itemExtraParam = ITEM_EXTRA_PARAM;
    writeItemToByteArray(writer, HIGHER_VERSION, itemParam, itemExtraParam);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    Item item(reader, parameterExpressionsHigher);
    ASSERT_EQ(HIGHER_VERSION, item.getVersion());
    ASSERT_EQ(itemParam, item.getParam());
    ASSERT_TRUE(item.isExtraParamUsed());
    ASSERT_EQ(itemExtraParam, item.getExtraParam());
}

TEST_F(SimpleParamTest, copyConstructor)
{
    Item item;
    item.setParam(ITEM_PARAM);
    item.setExtraParam(ITEM_EXTRA_PARAM);

    const Item itemCopy1(item);
    ASSERT_THROW(itemCopy1.getVersion(), zserio::CppRuntimeException);

    item.initialize(parameterExpressionsHigher);
    const Item itemCopy2(item);
    ASSERT_EQ(item.getVersion(), itemCopy2.getVersion());
}

TEST_F(SimpleParamTest, assignmentOperator)
{
    Item item;
    item.setParam(ITEM_PARAM);
    item.setExtraParam(ITEM_EXTRA_PARAM);

    Item itemCopy1;
    itemCopy1 = item;
    ASSERT_THROW(itemCopy1.getVersion(), zserio::CppRuntimeException);

    item.initialize(parameterExpressionsHigher);
    Item itemCopy2;
    itemCopy2 = item;
    ASSERT_EQ(item.getVersion(), itemCopy2.getVersion());
}

TEST_F(SimpleParamTest, moveConstructor)
{
    Item item(ITEM_PARAM, zserio::NullOpt);
    item.initialize(parameterExpressionsLower);

    Item movedItem(std::move(item));
    ASSERT_EQ(ITEM_PARAM, movedItem.getParam());
    ASSERT_FALSE(movedItem.isExtraParamUsed());
}

TEST_F(SimpleParamTest, moveAssignmentOperator)
{
    Item item(ITEM_PARAM, ITEM_EXTRA_PARAM);
    item.initialize(parameterExpressionsHigher);

    Item movedItem;
    movedItem = std::move(item);
    ASSERT_EQ(ITEM_PARAM, movedItem.getParam());
    ASSERT_TRUE(movedItem.isExtraParamUsed());
    ASSERT_EQ(ITEM_EXTRA_PARAM, movedItem.getExtraParam());
}

TEST_F(SimpleParamTest, propagateAllocatorCopyConstructor)
{
    Item item;
    item.setParam(ITEM_PARAM);
    item.setExtraParam(ITEM_EXTRA_PARAM);

    const Item itemCopy1(zserio::PropagateAllocator, item, Item::allocator_type());
    ASSERT_THROW(itemCopy1.getVersion(), zserio::CppRuntimeException);

    item.initialize(parameterExpressionsHigher);
    const Item itemCopy2(zserio::PropagateAllocator, item, Item::allocator_type());
    ASSERT_EQ(item.getVersion(), itemCopy2.getVersion());
}

TEST_F(SimpleParamTest, bitSizeOf)
{
    Item item1;
    item1.initialize(parameterExpressionsLower);
    item1.setParam(ITEM_PARAM);
    const size_t expectedBitSizeWithoutOptional = ITEM_BIT_SIZE_WITHOUT_OPTIONAL;
    ASSERT_EQ(expectedBitSizeWithoutOptional, item1.bitSizeOf());

    Item item2;
    item2.initialize(parameterExpressionsHigher);
    item2.setParam(ITEM_PARAM);
    item2.setExtraParam(ITEM_EXTRA_PARAM);
    const size_t expectedBitSizeWithOptional = ITEM_BIT_SIZE_WITH_OPTIONAL;
    ASSERT_EQ(expectedBitSizeWithOptional, item2.bitSizeOf());
}

TEST_F(SimpleParamTest, initializeOffsets)
{
    Item item1;
    item1.initialize(parameterExpressionsLower);
    item1.setParam(ITEM_PARAM);
    const size_t bitPosition = 1;
    const size_t expectedBitPositionWithoutOptional = bitPosition + ITEM_BIT_SIZE_WITHOUT_OPTIONAL;
    ASSERT_EQ(expectedBitPositionWithoutOptional, item1.initializeOffsets(bitPosition));

    Item item2;
    item2.initialize(parameterExpressionsHigher);
    item2.setParam(ITEM_PARAM);
    item2.setExtraParam(ITEM_EXTRA_PARAM);
    const size_t expectedBitPositionWithOptional = bitPosition + ITEM_BIT_SIZE_WITH_OPTIONAL;
    ASSERT_EQ(expectedBitPositionWithOptional, item2.initializeOffsets(bitPosition));
}

TEST_F(SimpleParamTest, operatorEquality)
{
    Item item1(ITEM_PARAM, ITEM_EXTRA_PARAM);
    item1.initialize(parameterExpressionsLower);
    Item item2(ITEM_PARAM, ITEM_EXTRA_PARAM);
    item2.initialize(parameterExpressionsLower);
    ASSERT_TRUE(item1 == item2);

    Item item3(ITEM_PARAM, ITEM_EXTRA_PARAM);
    item3.initialize(parameterExpressionsHigher);

    ASSERT_FALSE(item2 == item3);
}

TEST_F(SimpleParamTest, hashCode)
{
    Item item1(ITEM_PARAM, ITEM_EXTRA_PARAM);
    item1.initialize(parameterExpressionsLower);
    Item item2(ITEM_PARAM, ITEM_EXTRA_PARAM);
    item2.initialize(parameterExpressionsLower);
    ASSERT_EQ(item1.hashCode(), item2.hashCode());

    Item item3(ITEM_PARAM, ITEM_EXTRA_PARAM);
    item3.initialize(parameterExpressionsHigher);
    ASSERT_TRUE(item2.hashCode() != item3.hashCode());
}

TEST_F(SimpleParamTest, write)
{
    const uint32_t version = HIGHER_VERSION;
    Item item;
    item.initialize(parameterExpressionsHigher);
    item.setParam(ITEM_PARAM);
    item.setExtraParam(ITEM_EXTRA_PARAM);

    zserio::BitStreamWriter writer(bitBuffer);
    item.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    checkItemInBitStream(reader, version, item);
    reader.setBitPosition(0);

    const Item readItem(reader, parameterExpressionsHigher);
    ASSERT_EQ(item, readItem);
}

} // namespace simple_param
} // namespace parameterized_types
