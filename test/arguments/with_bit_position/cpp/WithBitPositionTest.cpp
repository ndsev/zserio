#include <type_traits>
#include <vector>

#include "gtest/gtest.h"
#include "test_utils/Assertions.h"
#include "with_bit_position/Item.h"
#include "with_bit_position/ItemHolder.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/Vector.h"

namespace with_bit_position
{

class WithBitPosition : public ::testing::Test
{
protected:
    Item readItem()
    {
        std::vector<uint8_t> buffer;
        buffer.resize(16);

        zserio::BitStreamWriter writer(buffer.data(), buffer.size());

        Item tmp;
        tmp.write(writer);

        zserio::BitStreamReader reader(buffer.data(), buffer.size());

        return Item(reader);
    }

    ItemHolder readItemHolder()
    {
        std::vector<uint8_t> buffer;
        buffer.resize(32);

        zserio::BitStreamWriter writer(buffer.data(), buffer.size());

        using ArrayType = typename std::decay<decltype(std::declval<ItemHolder>().getItems())>::type;

        ArrayType items;
        items.emplace_back();
        items.emplace_back();

        ItemHolder tmp;
        tmp.setSize(static_cast<decltype(tmp.getSize())>(items.size()));
        tmp.setItems(std::move(items));
        tmp.write(writer);

        zserio::BitStreamReader reader(buffer.data(), buffer.size());

        return ItemHolder(reader);
    }
};

static const char* const PATH = "arguments/with_bit_position/gen/with_bit_position/";

TEST_F(WithBitPosition, checkItemTypeMethods)
{
    ASSERT_METHOD_PRESENT(PATH, "Item", "size_t bitPosition(", "size_t bitPosition(");
    ASSERT_METHOD_PRESENT(PATH, "ItemHolder", "size_t bitPosition(", "size_t bitPosition(");
}

TEST_F(WithBitPosition, readRootPosition)
{
    auto item = readItem();

    ASSERT_EQ(item.bitPosition(), 0);
}

TEST_F(WithBitPosition, readChildPosition)
{
    auto holder = readItemHolder();

    ASSERT_EQ(holder.bitPosition(), 0);

    size_t offset = 16; // The size field
    ASSERT_EQ(holder.getItems().at(0).bitPosition(), offset + 0);
    ASSERT_EQ(holder.getItems().at(1).bitPosition(), offset + holder.getItems().at(0).bitSizeOf());
}

} // namespace with_bit_position
