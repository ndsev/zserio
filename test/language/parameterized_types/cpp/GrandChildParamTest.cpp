#include "gtest/gtest.h"
#include "parameterized_types/grand_child_param/GrandChildParam.h"
#include "zserio/SerializeUtil.h"

namespace parameterized_types
{
namespace grand_child_param
{

using allocator_type = GrandChildParam::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class GrandChildParamTest : public ::testing::Test
{
protected:
    void fillItemChoiceHolder(ItemChoiceHolder& itemChoiceHolder)
    {
        itemChoiceHolder.setHasItem(ITEM_CHOICE_HOLDER_HAS_ITEM);
        ItemChoice& itemChoice = itemChoiceHolder.getItemChoice();
        Item item;
        item.setParam(ITEM_PARAM);
        item.setExtraParam(ITEM_EXTRA_PARAM);
        itemChoice.setItem(item);
    }

    void fillGrandChildParam(GrandChildParam& grandChildParam)
    {
        ItemChoiceHolder& itemChoiceHolder = grandChildParam.getItemChoiceHolder();
        fillItemChoiceHolder(itemChoiceHolder);

        auto& itemChoiceHolderArray = grandChildParam.getItemChoiceHolderArray();
        itemChoiceHolderArray.push_back(itemChoiceHolder);

        vector_type<uint32_t> dummyArray(1);
        grandChildParam.setDummyArray(dummyArray);

        grandChildParam.initializeChildren();
    }

    void checkItemChoiceHolderInBitStream(
            zserio::BitStreamReader& reader, const ItemChoiceHolder& itemChoiceHolder)
    {
        ASSERT_EQ(itemChoiceHolder.getHasItem(), reader.readBool());

        const Item& item = itemChoiceHolder.getItemChoice().getItem();
        ASSERT_EQ(item.getParam(), reader.readBits(16));
        ASSERT_EQ(item.getExtraParam(), reader.readBits(32));
    }

    void checkGrandChildParamInBitStream(
            zserio::BitStreamReader& reader, const GrandChildParam& grandChildParam)
    {
        const ItemChoiceHolder& itemChoiceHolder = grandChildParam.getItemChoiceHolder();
        checkItemChoiceHolderInBitStream(reader, itemChoiceHolder);

        const auto& itemChoiceHolderArray = grandChildParam.getItemChoiceHolderArray();
        ASSERT_EQ(itemChoiceHolderArray.size(), reader.readVarSize());
        checkItemChoiceHolderInBitStream(reader, itemChoiceHolderArray[0]);

        const bool isDummyArrayUsed = grandChildParam.isDummyArrayUsed();
        ASSERT_EQ(isDummyArrayUsed, reader.readBool());
        if (isDummyArrayUsed)
        {
            const auto& dummyArray = grandChildParam.getDummyArray();
            ASSERT_EQ(dummyArray.size(), reader.readVarSize());
            ASSERT_EQ(dummyArray[0], reader.readBits(32));
        }
    }

    static const std::string BLOB_NAME;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);

private:
    static const bool ITEM_CHOICE_HOLDER_HAS_ITEM;
    static const uint16_t ITEM_PARAM;
    static const uint32_t ITEM_EXTRA_PARAM;
};

const std::string GrandChildParamTest::BLOB_NAME = "language/parameterized_types/grand_child_param.blob";

const bool GrandChildParamTest::ITEM_CHOICE_HOLDER_HAS_ITEM = true;
const uint16_t GrandChildParamTest::ITEM_PARAM = 0xAABB;
const uint32_t GrandChildParamTest::ITEM_EXTRA_PARAM = 0x11223344;

TEST_F(GrandChildParamTest, writeRead)
{
    GrandChildParam grandChildParam;
    fillGrandChildParam(grandChildParam);

    zserio::BitStreamWriter writer(bitBuffer);
    grandChildParam.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    checkGrandChildParamInBitStream(reader, grandChildParam);
    reader.setBitPosition(0);

    GrandChildParam readGrandChildParam(reader);
    ASSERT_EQ(grandChildParam, readGrandChildParam);
}

TEST_F(GrandChildParamTest, writeReadFile)
{
    GrandChildParam grandChildParam;
    fillGrandChildParam(grandChildParam);

    zserio::serializeToFile(grandChildParam, BLOB_NAME);

    const auto readGrandChildParam = zserio::deserializeFromFile<GrandChildParam>(BLOB_NAME);
    ASSERT_EQ(grandChildParam, readGrandChildParam);
}

} // namespace grand_child_param
} // namespace parameterized_types
