#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "parameterized_types/simple_param/Item.h"

namespace parameterized_types
{
namespace simple_param
{

class ParameterizedTypesSimpleParamTest : public ::testing::Test
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
            ASSERT_EQ(item.getExtraParam(), reader.readBits(32));
    }

    static const uint32_t LOWER_VERSION;
    static const uint32_t HIGHER_VERSION;

    static const uint16_t ITEM_PARAM;
    static const uint32_t ITEM_EXTRA_PARAM;

    static const size_t ITEM_BIT_SIZE_WITHOUT_OPTIONAL;
    static const size_t ITEM_BIT_SIZE_WITH_OPTIONAL;
};

const uint32_t ParameterizedTypesSimpleParamTest::LOWER_VERSION = 9;
const uint32_t ParameterizedTypesSimpleParamTest::HIGHER_VERSION = 10;

const uint16_t ParameterizedTypesSimpleParamTest::ITEM_PARAM = 0xAA;
const uint32_t ParameterizedTypesSimpleParamTest::ITEM_EXTRA_PARAM = 0xBB;

const size_t ParameterizedTypesSimpleParamTest::ITEM_BIT_SIZE_WITHOUT_OPTIONAL = 16;
const size_t ParameterizedTypesSimpleParamTest::ITEM_BIT_SIZE_WITH_OPTIONAL = 16 + 32;

TEST_F(ParameterizedTypesSimpleParamTest, emptyConstructor)
{
    const uint16_t version = LOWER_VERSION;
    Item item;
    item.initialize(version);
    ASSERT_EQ(version, item.getVersion());
    ASSERT_FALSE(item.hasExtraParam());
}

TEST_F(ParameterizedTypesSimpleParamTest, bitStreamReaderConstructor)
{
    const uint32_t version = HIGHER_VERSION;
    zserio::BitStreamWriter writer;
    const uint16_t itemParam = ITEM_PARAM;
    const uint32_t itemExtraParam = ITEM_EXTRA_PARAM;
    writeItemToByteArray(writer, version, itemParam, itemExtraParam);

    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    Item item(reader, version);
    ASSERT_EQ(version, item.getVersion());
    ASSERT_EQ(itemParam, item.getParam());
    ASSERT_TRUE(item.hasExtraParam());
    ASSERT_EQ(itemExtraParam, item.getExtraParam());
}

TEST_F(ParameterizedTypesSimpleParamTest, copyConstructor)
{
    Item item;
    item.setParam(ITEM_PARAM);
    item.setExtraParam(ITEM_EXTRA_PARAM);

    const Item itemCopy1(item);
    ASSERT_THROW(itemCopy1.getVersion(), zserio::CppRuntimeException);

    item.initialize(HIGHER_VERSION);
    const Item itemCopy2(item);
    ASSERT_EQ(item.getVersion(), itemCopy2.getVersion());
}

TEST_F(ParameterizedTypesSimpleParamTest, operatorAssignment)
{
    Item item;
    item.setParam(ITEM_PARAM);
    item.setExtraParam(ITEM_EXTRA_PARAM);

    const Item itemCopy1 = item;
    ASSERT_THROW(itemCopy1.getVersion(), zserio::CppRuntimeException);

    item.initialize(HIGHER_VERSION);
    const Item itemCopy2 = item;
    ASSERT_EQ(item.getVersion(), itemCopy2.getVersion());
}

TEST_F(ParameterizedTypesSimpleParamTest, bitSizeOf)
{
    Item item1;
    item1.initialize(LOWER_VERSION);
    item1.setParam(ITEM_PARAM);
    const size_t expectedBitSizeWithoutOptional = ITEM_BIT_SIZE_WITHOUT_OPTIONAL;
    ASSERT_EQ(expectedBitSizeWithoutOptional, item1.bitSizeOf());

    Item item2;
    item2.initialize(HIGHER_VERSION);
    item2.setParam(ITEM_PARAM);
    item2.setExtraParam(ITEM_EXTRA_PARAM);
    const size_t expectedBitSizeWithOptional = ITEM_BIT_SIZE_WITH_OPTIONAL;
    ASSERT_EQ(expectedBitSizeWithOptional, item2.bitSizeOf());
}

TEST_F(ParameterizedTypesSimpleParamTest, initializeOffsets)
{
    Item item1;
    item1.initialize(LOWER_VERSION);
    item1.setParam(ITEM_PARAM);
    const size_t bitPosition = 1;
    const size_t expectedBitPositionWithoutOptional = bitPosition + ITEM_BIT_SIZE_WITHOUT_OPTIONAL;
    ASSERT_EQ(expectedBitPositionWithoutOptional, item1.initializeOffsets(bitPosition));

    Item item2;
    item2.initialize(HIGHER_VERSION);
    item2.setParam(ITEM_PARAM);
    item2.setExtraParam(ITEM_EXTRA_PARAM);
    const size_t expectedBitPositionWithOptional = bitPosition + ITEM_BIT_SIZE_WITH_OPTIONAL;
    ASSERT_EQ(expectedBitPositionWithOptional, item2.initializeOffsets(bitPosition));
}

TEST_F(ParameterizedTypesSimpleParamTest, operatorEquality)
{
    Item item1;
    item1.initialize(LOWER_VERSION);
    Item item2;
    item2.initialize(LOWER_VERSION);
    ASSERT_TRUE(item1 == item2);

    Item item3;
    item3.initialize(HIGHER_VERSION);

    ASSERT_FALSE(item2 == item3);
}

TEST_F(ParameterizedTypesSimpleParamTest, hashCode)
{
    Item item1;
    item1.initialize(LOWER_VERSION);
    Item item2;
    item2.initialize(LOWER_VERSION);
    ASSERT_EQ(item1.hashCode(), item2.hashCode());

    Item item3;
    item3.initialize(HIGHER_VERSION);
    ASSERT_TRUE(item2.hashCode() != item3.hashCode());
}

TEST_F(ParameterizedTypesSimpleParamTest, write)
{
    const uint32_t version = HIGHER_VERSION;
    Item item;
    item.initialize(version);
    item.setParam(ITEM_PARAM);
    item.setExtraParam(ITEM_EXTRA_PARAM);

    zserio::BitStreamWriter writer;
    item.write(writer);

    size_t writerBufferByteSize;
    const uint8_t* writerBuffer = writer.getWriteBuffer(writerBufferByteSize);
    zserio::BitStreamReader reader(writerBuffer, writerBufferByteSize);
    checkItemInBitStream(reader, version, item);
    reader.setBitPosition(0);

    const Item readItem(reader, version);
    ASSERT_EQ(item, readItem);
}

} // namespace compound_param
} // namespace parameterized_types
