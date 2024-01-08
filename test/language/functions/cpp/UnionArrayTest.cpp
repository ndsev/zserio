#include <array>
#include <vector>

#include "functions/union_array/Inner.h"
#include "functions/union_array/Item.h"
#include "functions/union_array/ItemRef.h"
#include "functions/union_array/OuterArray.h"
#include "gtest/gtest.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"

namespace functions
{
namespace union_array
{

class UnionArrayTest : public ::testing::Test
{
protected:
    void writeInnerToByteArray(zserio::BitStreamWriter& writer, uint16_t pos)
    {
        writer.writeBits(ITEMS.size(), 16);

        for (Item item : ITEMS)
        {
            writer.writeBits(item.getA(), 8);
            writer.writeBits(item.getB(), 8);
        }

        const uint8_t isExplicit = (pos >= ITEMS.size()) ? 1 : 0;
        writer.writeVarSize(isExplicit != 0 ? 0 : 1); // choice tag
        if (isExplicit != 0)
        {
            writer.writeBits(EXPLICIT_ITEM.getA(), 8);
            writer.writeBits(EXPLICIT_ITEM.getB(), 8);
        }
        else
        {
            writer.writeBits(pos, 16);
        }
    }

    void fillInner(Inner& inner, uint16_t pos)
    {
        OuterArray outerArray;
        outerArray.setNumElements(ITEMS.size());
        auto& values = outerArray.getValues();
        values.assign(ITEMS.begin(), ITEMS.end());
        inner.setOuterArray(outerArray);

        const uint8_t isExplicit = (pos >= ITEMS.size()) ? 1 : 0;
        ItemRef& itemRef = inner.getRef();
        if (isExplicit != 0)
        {
            itemRef.setItem(EXPLICIT_ITEM);
        }
        else
        {
            itemRef.setPosition(pos);
        }

        inner.initializeChildren();
    }

    void checkUnionArrayFunction(uint16_t pos)
    {
        Inner inner;
        fillInner(inner, pos);
        const uint8_t isExplicit = (pos >= ITEMS.size()) ? 1 : 0;
        if (isExplicit != 0)
        {
            const Item readElement = inner.getRef().funcGetItem();
            ASSERT_EQ(EXPLICIT_ITEM, readElement);
        }
        else
        {
            const Item readElement = inner.getRef().funcGetElement();
            ASSERT_EQ(ITEMS[pos], readElement);
        }

        zserio::BitBuffer writtenBitBuffer = zserio::BitBuffer(1024 * 8);
        zserio::BitStreamWriter writtenWriter(writtenBitBuffer);
        inner.write(writtenWriter);

        zserio::BitBuffer expectedBitBuffer = zserio::BitBuffer(1024 * 8);
        zserio::BitStreamWriter expectedWriter(expectedBitBuffer);
        writeInnerToByteArray(expectedWriter, pos);

        ASSERT_EQ(expectedBitBuffer, writtenBitBuffer);

        zserio::BitStreamReader reader(writtenBitBuffer);
        const Inner readInner(reader);
        ASSERT_EQ(inner, readInner);
    }

    static const std::array<Item, 3> ITEMS;

private:
    static const Item EXPLICIT_ITEM;
};

const std::array<Item, 3> UnionArrayTest::ITEMS = {Item{12, 13}, Item{42, 18}, Item{17, 14}};

const Item UnionArrayTest::EXPLICIT_ITEM = {27, 29};

TEST_F(UnionArrayTest, checkUnionArrayFunctionElement0)
{
    checkUnionArrayFunction(0);
}

TEST_F(UnionArrayTest, checkUnionArrayFunctionElement1)
{
    checkUnionArrayFunction(1);
}

TEST_F(UnionArrayTest, checkUnionArrayFunctionElement2)
{
    checkUnionArrayFunction(2);
}

TEST_F(UnionArrayTest, checkUnionArrayFunctionExplicitElement)
{
    checkUnionArrayFunction(ITEMS.size());
}

} // namespace union_array
} // namespace functions
