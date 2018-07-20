#include <vector>

#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/ObjectArray.h"

#include "functions/union_array/Inner.h"
#include "functions/union_array/Item.h"
#include "functions/union_array/ItemRef.h"
#include "functions/union_array/OuterArray.h"

namespace functions
{
namespace union_array
{

class FunctionsUnionArrayTest : public ::testing::Test
{
public:
    FunctionsUnionArrayTest()
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
        writer.writeVarUInt64(isExplicit != 0 ? 0 : 1); // choice tag
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
        zserio::ObjectArray<Item>& values = outerArray.getValues();
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
            itemRef.setPos(pos);
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
            const Item readElement = inner.getRef().getExplicitItem();
            ASSERT_EQ(m_explicitItem, readElement);
        }
        else
        {
            const Item readElement = inner.getRef().getElement();
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
};

TEST_F(FunctionsUnionArrayTest, checkUnionArrayFunctionElement0)
{
    checkUnionArrayFunction(0);
}

TEST_F(FunctionsUnionArrayTest, checkUnionArrayFunctionElement1)
{
    checkUnionArrayFunction(1);
}

TEST_F(FunctionsUnionArrayTest, checkUnionArrayFunctionElement2)
{
    checkUnionArrayFunction(2);
}

TEST_F(FunctionsUnionArrayTest, checkUnionArrayFunctionExplicitElement)
{
    checkUnionArrayFunction(NUM_ITEM_ELEMENTS);
}

} // namespace union_array
} // namespace functions
