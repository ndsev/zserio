#include <vector>
#include <array>

#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "functions/choice_array/Inner.h"
#include "functions/choice_array/Item.h"
#include "functions/choice_array/ItemRef.h"
#include "functions/choice_array/OuterArray.h"

namespace functions
{
namespace choice_array
{

class ChoiceArrayTest : public ::testing::Test
{
public:
    ChoiceArrayTest()
    {
        const std::array<uint8_t, 3> elementsA = {12, ELEMENT_A_FOR_EXTRA_VALUE, 17};
        const std::array<uint8_t, 3> elementsB = {13, 18, 14};
        for (size_t i = 0; i < m_items.size(); ++i)
        {
            m_items[i].setA(elementsA[i]);
            m_items[i].setB(elementsB[i]);
        }

        m_explicitItem.setA(27);
        m_explicitItem.setB(29);
    }

protected:
    void writeInnerToByteArray(zserio::BitStreamWriter& writer, uint16_t pos)
    {
        writer.writeBits(static_cast<uint32_t>(m_items.size()), 16);

        for (Item item : m_items)
        {
            writer.writeBits(item.getA(), 8);
            writer.writeBits(item.getB(), 8);
        }

        const uint8_t isExplicit = (pos >= m_items.size()) ? 1 : 0;
        writer.writeBits(isExplicit, 8);
        uint8_t elementA;
        if (isExplicit != 0)
        {
            writer.writeBits(m_explicitItem.getA(), 8);
            writer.writeBits(m_explicitItem.getB(), 8);
            elementA = m_explicitItem.getA();
        }
        else
        {
            writer.writeBits(pos, 16);
            elementA = m_items[pos].getA();
        }

        if (elementA == ELEMENT_A_FOR_EXTRA_VALUE)
            writer.writeSignedBits(EXTRA_VALUE, 32);
    }

    void fillInner(Inner& inner, uint16_t pos)
    {
        OuterArray outerArray;
        outerArray.setNumElements(static_cast<uint16_t>(m_items.size()));
        auto& values = outerArray.getValues();
        values.assign(m_items.begin(), m_items.end());
        inner.setOuterArray(outerArray);

        const uint8_t isExplicit = (pos >= m_items.size()) ? 1 : 0;
        inner.setIsExplicit(isExplicit);
        ItemRef& itemRef = inner.getRef();
        uint8_t elementA;
        if (isExplicit != 0)
        {
            itemRef.setItem(m_explicitItem);
            elementA = m_explicitItem.getA();
        }
        else
        {
            itemRef.setPos(pos);
            elementA = m_items[pos].getA();
        }
        if (elementA == ELEMENT_A_FOR_EXTRA_VALUE)
            inner.setExtra(EXTRA_VALUE);

        inner.initializeChildren();
    }

    void checkChoiceArrayFunction(uint16_t pos)
    {
        Inner inner;
        fillInner(inner, pos);
        const Item& readElement = inner.getRef().funcGetElement();
        if (pos >= m_items.size())
        {
            ASSERT_EQ(m_explicitItem, readElement);
        }
        else
        {
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

    std::array<Item, 3> m_items;

private:
    Item m_explicitItem;

    static const uint8_t ELEMENT_A_FOR_EXTRA_VALUE;
    static const int32_t EXTRA_VALUE;
};

const uint8_t ChoiceArrayTest::ELEMENT_A_FOR_EXTRA_VALUE = 20;
const int32_t ChoiceArrayTest::EXTRA_VALUE = 4711;

TEST_F(ChoiceArrayTest, checkChoiceArrayFunctionElement0)
{
    checkChoiceArrayFunction(0);
}

TEST_F(ChoiceArrayTest, checkChoiceArrayFunctionElement1)
{
    checkChoiceArrayFunction(1);
}

TEST_F(ChoiceArrayTest, checkChoiceArrayFunctionElement2)
{
    checkChoiceArrayFunction(2);
}

TEST_F(ChoiceArrayTest, checkChoiceArrayFunctionExplicitElement)
{
    checkChoiceArrayFunction(static_cast<uint16_t>(m_items.size()));
}

} // namespace choice_array
} // namespace functions
