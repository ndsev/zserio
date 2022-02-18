#include <vector>

#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "functions/structure_value/CustomVarInt.h"
#include "functions/structure_value/CustomVarList.h"

namespace functions
{
namespace structure_value
{

class StructureValueTest : public ::testing::Test
{
protected:
    void writeCustomVarIntToByteArray(zserio::BitStreamWriter& writer, uint32_t value)
    {
        if (value <= MAX_ONE_BYTE_VALUE)
        {
            writer.writeBits(value, 8);
        }
        else if (value <= 0xFFFF)
        {
            writer.writeBits(TWO_BYTES_INDICATOR, 8);
            writer.writeBits(value, 16);
        }
        else
        {
            writer.writeBits(FOUR_BYTES_INDICATOR, 8);
            writer.writeBits(value, 32);
        }
    }

    void createCustomVarInt(CustomVarInt& customVarInt, uint32_t value)
    {
        if (value <= MAX_ONE_BYTE_VALUE)
        {
            customVarInt.setVal1(static_cast<uint8_t>(value));
        }
        else if (value <= 0xFFFF)
        {
            customVarInt.setVal1(TWO_BYTES_INDICATOR);
            customVarInt.setVal2(static_cast<uint16_t>(value));
        }
        else
        {
            customVarInt.setVal1(FOUR_BYTES_INDICATOR);
            customVarInt.setVal3(value);
        }
    }

    void checkCustomVarInt(uint32_t value)
    {
        CustomVarInt customVarInt;
        createCustomVarInt(customVarInt, value);
        const uint32_t readValue = customVarInt.funcGetValue();
        ASSERT_EQ(value, readValue);

        zserio::BitBuffer writtenBitBuffer = zserio::BitBuffer(1024 * 8);
        zserio::BitStreamWriter writtenWriter(writtenBitBuffer);
        customVarInt.write(writtenWriter);

        zserio::BitBuffer expectedBitBuffer = zserio::BitBuffer(1024 * 8);
        zserio::BitStreamWriter expectedWriter(expectedBitBuffer);
        writeCustomVarIntToByteArray(expectedWriter, value);

        ASSERT_EQ(expectedBitBuffer, writtenBitBuffer);

        zserio::BitStreamReader reader(writtenBitBuffer);
        const CustomVarInt readcustomVarInt(reader);
        ASSERT_EQ(customVarInt, readcustomVarInt);
    }

    static const uint32_t   MAX_ONE_BYTE_VALUE = 253;
    static const uint8_t    TWO_BYTES_INDICATOR = 255;
    static const uint8_t    FOUR_BYTES_INDICATOR = 254;
};

TEST_F(StructureValueTest, checkCustomVarIntValue42)
{
    checkCustomVarInt(42);
}

TEST_F(StructureValueTest, checkCustomVarIntValue253)
{
    checkCustomVarInt(MAX_ONE_BYTE_VALUE);
}

TEST_F(StructureValueTest, checkCustomVarIntValue255)
{
    checkCustomVarInt(TWO_BYTES_INDICATOR);
}

TEST_F(StructureValueTest, checkCustomVarIntValue254)
{
    checkCustomVarInt(FOUR_BYTES_INDICATOR);
}

TEST_F(StructureValueTest, checkCustomVarIntValue1000)
{
    checkCustomVarInt(1000);
}

TEST_F(StructureValueTest, checkCustomVarIntValue87654)
{
    checkCustomVarInt(87654);
}

} // namespace structure_value
} // namespace functions
