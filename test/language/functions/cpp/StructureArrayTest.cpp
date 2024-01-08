#include <array>
#include <vector>

#include "functions/structure_array/Item.h"
#include "functions/structure_array/StructureArray.h"
#include "gtest/gtest.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"

namespace functions
{
namespace structure_array
{

class StructureArrayTest : public ::testing::Test
{
protected:
    void writeStructureArrayToByteArray(zserio::BitStreamWriter& writer, uint16_t pos)
    {
        writer.writeBits(ITEMS.size(), 16);

        for (Item item : ITEMS)
        {
            writer.writeBits(item.getA(), 8);
            writer.writeBits(item.getB(), 8);
        }

        writer.writeBits(pos, 16);
    }

    void createStructureArray(StructureArray& structureArray, uint16_t pos)
    {
        structureArray.setNumElements(ITEMS.size());

        auto& values = structureArray.getValues();
        values.assign(ITEMS.begin(), ITEMS.end());

        structureArray.setPos(pos);
    }

    void checkStructureArray(uint16_t pos)
    {
        StructureArray structureArray;
        createStructureArray(structureArray, pos);
        const Item readElement = structureArray.funcGetElement();
        ASSERT_EQ(ITEMS[pos], readElement);

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
    static const std::array<Item, 3> ITEMS;
};

const std::array<Item, 3> StructureArrayTest::ITEMS = {Item{1, 2}, Item{3, 4}, Item{5, 6}};

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
