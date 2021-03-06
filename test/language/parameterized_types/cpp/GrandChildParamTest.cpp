#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "parameterized_types/grand_child_param/GrandChildParam.h"

namespace parameterized_types
{
namespace grand_child_param
{

class ParameterizedTypesGrandChildParamTest : public ::testing::Test
{
protected:
    void fillGrandChildParam(GrandChildParam& grandChildParam)
    {
        ItemChoiceHolder& itemChoiceHolder = grandChildParam.getItemChoiceHolder();
        itemChoiceHolder.setHasItem(ITEM_CHOICE_HOLDER_HAS_ITEM);
        ItemChoice& itemChoice = itemChoiceHolder.getItemChoice();
        Item item;
        item.setParam(ITEM_PARAM);
        item.setExtraParam(ITEM_EXTRA_PARAM);
        itemChoice.setItem(item);
    }

    void checkGrandChildParamInBitStream(zserio::BitStreamReader& reader,
            const GrandChildParam& grandChildParam)
    {
        const ItemChoiceHolder& itemChoiceHolder = grandChildParam.getItemChoiceHolder();

        ASSERT_EQ(itemChoiceHolder.getHasItem(), reader.readBool());

        const Item& item = itemChoiceHolder.getItemChoice().getItem();
        ASSERT_EQ(item.getParam(), reader.readBits(16));
        ASSERT_EQ(item.getExtraParam(), reader.readBits(32));
    }

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);

private:
    static const bool     ITEM_CHOICE_HOLDER_HAS_ITEM;
    static const uint16_t ITEM_PARAM;
    static const uint32_t ITEM_EXTRA_PARAM;
};

const bool     ParameterizedTypesGrandChildParamTest::ITEM_CHOICE_HOLDER_HAS_ITEM = true;
const uint16_t ParameterizedTypesGrandChildParamTest::ITEM_PARAM = 0xAABB;
const uint32_t ParameterizedTypesGrandChildParamTest::ITEM_EXTRA_PARAM = 0x11223344;

TEST_F(ParameterizedTypesGrandChildParamTest, write)
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

} // namespace grand_child_param
} // namespace parameterized_types
