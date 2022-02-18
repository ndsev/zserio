#include <vector>

#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "functions/structure_array/Item.h"
#include "functions/structure_array/StructureArray.h"

namespace functions
{
namespace structure_array
{

class StructureArrayTest : public ::testing::Test
{
public:
    StructureArrayTest()
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

        auto& values = structureArray.getValues();
        values.assign(&m_items[0], &m_items[NUM_ITEM_ELEMENTS]);

        structureArray.setPos(pos);
    }

    void checkStructureArray(uint16_t pos)
    {
        StructureArray structureArray;
        createStructureArray(structureArray, pos);
        const Item readElement = structureArray.funcGetElement();
        ASSERT_EQ(m_items[pos], readElement);

        zserio::BitBuffer writtenBitBuffer = zserio::BitBuffer(1024 * 8);
        zserio::BitStreamWriter writtenWriter(writtenBitBuffer);
        structureArray.write(writtenWriter);

        zserio::BitBuffer expectedBitBuffer = zserio::BitBuffer(1024 * 8);
        zserio::BitStreamWriter expectedWriter(expectedBitBuffer);
        writeStructureArrayToByteArray(expectedWriter, pos);

        ASSERT_EQ(expectedBitBuffer, writtenBitBuffer);

        zserio::BitStreamReader reader(writtenBitBuffer);
        const StructureArray readStructureArray(reader);
        ASSERT_EQ(structureArray, readStructureArray);
    }

private:
    static const uint16_t NUM_ITEM_ELEMENTS = 3;

    Item m_items[NUM_ITEM_ELEMENTS];
};

TEST_F(StructureArrayTest, checkStructureArrayElement0)
{
    checkStructureArray(0);
}

TEST_F(StructureArrayTest, checkStructureArrayElement1)
{
    checkStructureArray(1);
}

TEST_F(StructureArrayTest, checkStructureArrayElement2)
{
    checkStructureArray(2);
}

} // namespace structure_array
} // namespace functions
