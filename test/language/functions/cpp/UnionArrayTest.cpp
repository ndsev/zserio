#include <vector>

#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "functions/union_array/Inner.h"
#include "functions/union_array/Item.h"
#include "functions/union_array/ItemRef.h"
#include "functions/union_array/OuterArray.h"

namespace functions
{
namespace union_array
{

class UnionArrayTest : public ::testing::Test
{
public:
    UnionArrayTest()
    {
        uint8_t ElementsA[NUM_ITEM_ELEMENTS] = {12, 42, 17};
        uint8_t ElementsB[NUM_ITEM_ELEMENTS] = {13, 18, 14};

        for (uint16_t i = 0; i < NUM_ITEM_ELEMENTS; ++i)
        {
            m_items[i].setA(ElementsA[i]);
            m_items[i].setB(ElementsB[i]);
        }

        m_explicitItem.setA(27);
        m_explicitItem.setB(29);
    }

protected:
    void writeInnerToByteArray(zserio::BitStreamWriter& writer, uint16_t pos)
    {
        writer.writeBits(NUM_ITEM_ELEMENTS, 16);

        for (uint16_t i = 0; i < NUM_ITEM_ELEMENTS; ++i)
        {
            writer.writeBits(m_items[i].getA(), 8);
            writer.writeBits(m_items[i].getB(), 8);
        }

        const uint8_t isExplicit = (pos >= NUM_ITEM_ELEMENTS) ? 1 : 0;
        writer.writeVarSize(isExplicit != 0 ? 0 : 1); // choice tag
        if (isExplicit != 0)
        {
            writer.writeBits(m_explicitItem.getA(), 8);
            writer.writeBits(m_explicitItem.getB(), 8);
        }
        else
        {
            writer.writeBits(pos, 16);
        }
    }

    void fillInner(Inner& inner, uint16_t pos)
    {
        OuterArray outerArray;
        outerArray.setNumElements(NUM_ITEM_ELEMENTS);
        auto& values = outerArray.getValues();
        values.assign(&m_items[0], &m_items[NUM_ITEM_ELEMENTS]);
        inner.setOuterArray(outerArray);

        const uint8_t isExplicit = (pos >= NUM_ITEM_ELEMENTS) ? 1 : 0;
        ItemRef& itemRef = inner.getRef();
        if (isExplicit != 0)
        {
            itemRef.setItem(m_explicitItem);
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
        const uint8_t isExplicit = (pos >= NUM_ITEM_ELEMENTS) ? 1 : 0;
        if (isExplicit != 0)
        {
            const Item readElement = inner.getRef().funcGetItem();
            ASSERT_EQ(m_explicitItem, readElement);
        }
        else
        {
            const Item readElement = inner.getRef().funcGetElement();
            ASSERT_EQ(m_items[pos], readElement);
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

    static const uint16_t NUM_ITEM_ELEMENTS = 3;

private:
    Item m_items[NUM_ITEM_ELEMENTS];
    Item m_explicitItem;
};

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
    checkUnionArrayFunction(NUM_ITEM_ELEMENTS);
}

} // namespace union_array
} // namespace functions
