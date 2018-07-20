#include <vector>

#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/ObjectArray.h"

#include "functions/structure_array/Item.h"
#include "functions/structure_array/StructureArray.h"

namespace functions
{
namespace structure_array
{

class FunctionsStructureArrayTest : public ::testing::Test
{
public:
    FunctionsStructureArrayTest()
    {
        uint8_t ElementsA[NUM_ITEM_ELEMENTS] = {1, 3, 5};
        uint8_t ElementsB[NUM_ITEM_ELEMENTS] = {2, 4, 6};

        for (uint16_t i = 0; i < NUM_ITEM_ELEMENTS; ++i)
        {
            m_items[i].setA(ElementsA[i]);
            m_items[i].setB(ElementsB[i]);
        }
    }

protected:
    void writeStructureArrayToByteArray(zserio::BitStreamWriter& writer, uint16_t pos)
    {
        writer.writeBits(NUM_ITEM_ELEMENTS, 16);

        for (uint16_t i = 0; i < NUM_ITEM_ELEMENTS; ++i)
        {
            writer.writeBits(m_items[i].getA(), 8);
            writer.writeBits(m_items[i].getB(), 8);
        }

        writer.writeBits(pos, 16);
    }

    void createStructureArray(StructureArray& structureArray, uint16_t pos)
    {
        structureArray.setNumElements(NUM_ITEM_ELEMENTS);

        zserio::ObjectArray<Item>& values = structureArray.getValues();
        values.assign(&m_items[0], &m_items[NUM_ITEM_ELEMENTS]);

        structureArray.setPos(pos);
    }

    void checkStructureArray(uint16_t pos)
    {
        StructureArray structureArray;
        createStructureArray(structureArray, pos);
        const Item readElement = structureArray.getElement();
        ASSERT_EQ(m_items[pos], readElement);

        zserio::BitStreamWriter writtenWriter;
        structureArray.write(writtenWriter);
        size_t writtenWriterBufferByteSize;
        const uint8_t* writtenWriterBuffer = writtenWriter.getWriteBuffer(writtenWriterBufferByteSize);

        zserio::BitStreamWriter expectedWriter;
        writeStructureArrayToByteArray(expectedWriter, pos);
        size_t expectedWriterBufferByteSize;
        const uint8_t* expectedWriterBuffer = expectedWriter.getWriteBuffer(expectedWriterBufferByteSize);

        std::vector<uint8_t> writtenWriterVector(writtenWriterBuffer,
                                                 writtenWriterBuffer + writtenWriterBufferByteSize);
        std::vector<uint8_t> expectedWriterVector(expectedWriterBuffer,
                                                  expectedWriterBuffer + expectedWriterBufferByteSize);
        ASSERT_EQ(expectedWriterVector, writtenWriterVector);

        zserio::BitStreamReader reader(writtenWriterBuffer, writtenWriterBufferByteSize);
        const StructureArray readStructureArray(reader);
        ASSERT_EQ(structureArray, readStructureArray);
    }

private:
    static const uint16_t NUM_ITEM_ELEMENTS = 3;

    Item m_items[NUM_ITEM_ELEMENTS];
};

TEST_F(FunctionsStructureArrayTest, checkStructureArrayElement0)
{
    checkStructureArray(0);
}

TEST_F(FunctionsStructureArrayTest, checkStructureArrayElement1)
{
    checkStructureArray(1);
}

TEST_F(FunctionsStructureArrayTest, checkStructureArrayElement2)
{
    checkStructureArray(2);
}

} // namespace structure_array
} // namespace functions
