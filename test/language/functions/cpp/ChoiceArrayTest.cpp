#include <vector>

#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/ObjectArray.h"

#include "functions/choice_array/Inner.h"
#include "functions/choice_array/Item.h"
#include "functions/choice_array/ItemRef.h"
#include "functions/choice_array/OuterArray.h"

namespace functions
{
namespace choice_array
{

class FunctionsChoiceArrayTest : public ::testing::Test
{
public:
    FunctionsChoiceArrayTest()
    {
        uint8_t ElementsA[NUM_ITEM_ELEMENTS] = {12, ELEMENT_A_FOR_EXTRA_VALUE, 17};
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
        outerArray.setNumElements(NUM_ITEM_ELEMENTS);
        zserio::ObjectArray<Item>& values = outerArray.getValues();
        values.assign(&m_items[0], &m_items[NUM_ITEM_ELEMENTS]);
        inner.setOuterArray(outerArray);

        const uint8_t isExplicit = (pos >= NUM_ITEM_ELEMENTS) ? 1 : 0;
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
        const Item readElement = inner.getRef().getElement();
        if (pos >= NUM_ITEM_ELEMENTS)
        {
            ASSERT_EQ(m_explicitItem, readElement);
        }
        else
        {
            ASSERT_EQ(m_items[pos], readElement);
        }

        zserio::BitStreamWriter writtenWriter;
        inner.write(writtenWriter);
        size_t writtenWriterBufferByteSize;
        const uint8_t* writtenWriterBuffer = writtenWriter.getWriteBuffer(writtenWriterBufferByteSize);

        zserio::BitStreamWriter expectedWriter;
        writeInnerToByteArray(expectedWriter, pos);
        size_t expectedWriterBufferByteSize;
        const uint8_t* expectedWriterBuffer = expectedWriter.getWriteBuffer(expectedWriterBufferByteSize);

        std::vector<uint8_t> writtenWriterVector(writtenWriterBuffer,
                                                 writtenWriterBuffer + writtenWriterBufferByteSize);
        std::vector<uint8_t> expectedWriterVector(expectedWriterBuffer,
                                                  expectedWriterBuffer + expectedWriterBufferByteSize);
        ASSERT_EQ(expectedWriterVector, writtenWriterVector);

        zserio::BitStreamReader reader(writtenWriterBuffer, writtenWriterBufferByteSize);
        const Inner readInner(reader);
        ASSERT_EQ(inner, readInner);
    }

    static const uint16_t NUM_ITEM_ELEMENTS = 3;

private:
    Item m_items[NUM_ITEM_ELEMENTS];
    Item m_explicitItem;

    static const uint8_t ELEMENT_A_FOR_EXTRA_VALUE = 20;
    static const int32_t EXTRA_VALUE = 4711;
};

TEST_F(FunctionsChoiceArrayTest, checkChoiceArrayFunctionElement0)
{
    checkChoiceArrayFunction(0);
}

TEST_F(FunctionsChoiceArrayTest, checkChoiceArrayFunctionElement1)
{
    checkChoiceArrayFunction(1);
}

TEST_F(FunctionsChoiceArrayTest, checkChoiceArrayFunctionElement2)
{
    checkChoiceArrayFunction(2);
}

TEST_F(FunctionsChoiceArrayTest, checkChoiceArrayFunctionExplicitElement)
{
    checkChoiceArrayFunction(NUM_ITEM_ELEMENTS);
}

} // namespace choice_array
} // namespace functions
