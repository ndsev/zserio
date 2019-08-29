#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "indexed_offsets/optional_indexed_offset_array/OptionalIndexedOffsetArray.h"

namespace indexed_offsets
{
namespace optional_indexed_offset_array
{

class OptionalIndexedOffsetArrayTest : public ::testing::Test
{
public:
    OptionalIndexedOffsetArrayTest()
    {
        const char* data[NUM_ELEMENTS] = {"Green", "Red", "Pink", "Blue", "Black"};
        for (uint8_t i = 0; i < NUM_ELEMENTS; ++i)
            m_data.push_back(data[i]);
    }
protected:
    void writeOptionalIndexedOffsetArrayToByteArray(zserio::BitStreamWriter& writer, bool hasOptional,
            bool writeWrongOffsets)
    {
        const uint32_t wrongOffset = WRONG_OFFSET;
        uint32_t currentOffset = ELEMENT0_OFFSET;
        for (uint8_t i = 0; i < NUM_ELEMENTS; ++i)
        {
            if ((i + 1) == NUM_ELEMENTS && writeWrongOffsets)
                writer.writeBits(wrongOffset, 32);
            else
                writer.writeBits(currentOffset, 32);
            currentOffset += static_cast<uint32_t>(zserio::getBitSizeOfString(m_data[i]) / 8);
        }

        writer.writeBool(hasOptional);

        if (hasOptional)
        {
            writer.writeBits(0, 7);
            for (uint8_t i = 0; i < NUM_ELEMENTS; ++i)
                writer.writeString(m_data[i]);
        }

        writer.writeBits(FIELD_VALUE, 6);
    }

    void checkOffsets(const OptionalIndexedOffsetArray& optionalIndexedOffsetArray, uint16_t offsetShift)
    {
        const zserio::UInt32Array& offsets = optionalIndexedOffsetArray.getOffsets();
        const size_t expectedNumElements = NUM_ELEMENTS;
        ASSERT_EQ(expectedNumElements, offsets.size());
        uint32_t expectedOffset = ELEMENT0_OFFSET + offsetShift;
        for (uint8_t i = 0; i < NUM_ELEMENTS; ++i)
        {
            ASSERT_EQ(expectedOffset, offsets[i]);
            expectedOffset += static_cast<uint32_t>(zserio::getBitSizeOfString(m_data[i]) / 8);
        }
    }

    void checkOptionalIndexedOffsetArray(const OptionalIndexedOffsetArray& optionalIndexedOffsetArray,
            bool hasOptional)
    {
        const uint16_t offsetShift = 0;
        checkOffsets(optionalIndexedOffsetArray, offsetShift);

        ASSERT_EQ(hasOptional, optionalIndexedOffsetArray.getHasOptional());

        if (hasOptional)
        {
            const zserio::StringArray& data = optionalIndexedOffsetArray.getData();
            const size_t expectedNumElements = NUM_ELEMENTS;
            ASSERT_EQ(expectedNumElements, data.size());
            for (uint8_t i = 0; i < NUM_ELEMENTS; ++i)
                ASSERT_EQ(m_data[i], data[i]);
        }

        const uint8_t expectedField = FIELD_VALUE;
        ASSERT_EQ(expectedField, optionalIndexedOffsetArray.getField());
    }

    void fillOptionalIndexedOffsetArray(OptionalIndexedOffsetArray& optionalIndexedOffsetArray,
            bool hasOptional, bool createWrongOffsets)
    {
        zserio::UInt32Array& offsets = optionalIndexedOffsetArray.getOffsets();
        offsets.reserve(NUM_ELEMENTS);
        const uint32_t wrongOffset = WRONG_OFFSET;
        uint32_t currentOffset = ELEMENT0_OFFSET;
        for (uint8_t i = 0; i < NUM_ELEMENTS; ++i)
        {
            if ((i + 1) == NUM_ELEMENTS && createWrongOffsets)
                offsets.push_back(wrongOffset);
            else
                offsets.push_back(currentOffset);
            currentOffset += static_cast<uint32_t>(zserio::getBitSizeOfString(m_data[i]) / 8);
        }
        optionalIndexedOffsetArray.setHasOptional(hasOptional);

        if (hasOptional)
            optionalIndexedOffsetArray.setData(m_data);

        optionalIndexedOffsetArray.setField(FIELD_VALUE);
    }

    size_t getOptionalIndexedOffsetArrayBitSize(bool hasOptional)
    {
        size_t bitSize = NUM_ELEMENTS * sizeof(uint32_t) * 8 + 1;
        if (hasOptional)
        {
            bitSize += 7;
            for (short i = 0; i < NUM_ELEMENTS; ++i)
                bitSize += zserio::getBitSizeOfString(m_data[i]);
        }
        bitSize += 6;

        return bitSize;
    }

    static const uint8_t    NUM_ELEMENTS = 5;

    static const uint32_t   WRONG_OFFSET = 0;
    static const uint32_t   ELEMENT0_OFFSET = NUM_ELEMENTS * sizeof(uint32_t) + sizeof(uint8_t);

    static const uint8_t    FIELD_VALUE = 63;

    zserio::StringArray m_data;
};

TEST_F(OptionalIndexedOffsetArrayTest, readWithOptional)
{
    const bool hasOptional = true;
    const bool writeWrongOffsets = false;
    zserio::BitStreamWriter writer;
    writeOptionalIndexedOffsetArrayToByteArray(writer, hasOptional, writeWrongOffsets);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);

    OptionalIndexedOffsetArray optionalIndexedOffsetArray(reader);
    checkOptionalIndexedOffsetArray(optionalIndexedOffsetArray, hasOptional);
}

TEST_F(OptionalIndexedOffsetArrayTest, readWithoutOptional)
{
    const bool hasOptional = false;
    const bool writeWrongOffsets = false;
    zserio::BitStreamWriter writer;
    writeOptionalIndexedOffsetArrayToByteArray(writer, hasOptional, writeWrongOffsets);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);

    OptionalIndexedOffsetArray optionalIndexedOffsetArray(reader);
    checkOptionalIndexedOffsetArray(optionalIndexedOffsetArray, hasOptional);
}

TEST_F(OptionalIndexedOffsetArrayTest, bitSizeOfWithOptional)
{
    const bool hasOptional = true;
    const bool createWrongOffsets = false;
    OptionalIndexedOffsetArray optionalIndexedOffsetArray;
    fillOptionalIndexedOffsetArray(optionalIndexedOffsetArray, hasOptional, createWrongOffsets);

    const size_t expectedBitSize = getOptionalIndexedOffsetArrayBitSize(hasOptional);
    ASSERT_EQ(expectedBitSize, optionalIndexedOffsetArray.bitSizeOf());
}

TEST_F(OptionalIndexedOffsetArrayTest, bitSizeOfWithoutOptional)
{
    const bool hasOptional = false;
    const bool createWrongOffsets = false;
    OptionalIndexedOffsetArray optionalIndexedOffsetArray;
    fillOptionalIndexedOffsetArray(optionalIndexedOffsetArray, hasOptional, createWrongOffsets);

    const size_t expectedBitSize = getOptionalIndexedOffsetArrayBitSize(hasOptional);
    ASSERT_EQ(expectedBitSize, optionalIndexedOffsetArray.bitSizeOf());
}

TEST_F(OptionalIndexedOffsetArrayTest, initializeOffsetsWithOptional)
{
    const bool hasOptional = true;
    const bool createWrongOffsets = true;
    OptionalIndexedOffsetArray optionalIndexedOffsetArray;
    fillOptionalIndexedOffsetArray(optionalIndexedOffsetArray, hasOptional, createWrongOffsets);

    const size_t bitPosition = 0;
    const size_t expectedBitSize = getOptionalIndexedOffsetArrayBitSize(hasOptional);;
    ASSERT_EQ(expectedBitSize, optionalIndexedOffsetArray.initializeOffsets(bitPosition));
    checkOptionalIndexedOffsetArray(optionalIndexedOffsetArray, hasOptional);
}

TEST_F(OptionalIndexedOffsetArrayTest, initializeOffsetsWithoutOptional)
{
    const bool hasOptional = false;
    const bool createWrongOffsets = true;
    OptionalIndexedOffsetArray optionalIndexedOffsetArray;
    fillOptionalIndexedOffsetArray(optionalIndexedOffsetArray, hasOptional, createWrongOffsets);

    const size_t bitPosition = 0;
    const size_t expectedBitSize = getOptionalIndexedOffsetArrayBitSize(hasOptional);;
    ASSERT_EQ(expectedBitSize, optionalIndexedOffsetArray.initializeOffsets(bitPosition));
}

TEST_F(OptionalIndexedOffsetArrayTest, writeWithOptional)
{
    const bool hasOptional = true;
    const bool createWrongOffsets = true;
    OptionalIndexedOffsetArray optionalIndexedOffsetArray;
    fillOptionalIndexedOffsetArray(optionalIndexedOffsetArray, hasOptional, createWrongOffsets);

    zserio::BitStreamWriter writer;
    optionalIndexedOffsetArray.write(writer);
    checkOptionalIndexedOffsetArray(optionalIndexedOffsetArray, hasOptional);

    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    OptionalIndexedOffsetArray readOptionalIndexedOffsetArray(reader);
    checkOptionalIndexedOffsetArray(readOptionalIndexedOffsetArray, hasOptional);
    ASSERT_TRUE(optionalIndexedOffsetArray == readOptionalIndexedOffsetArray);
}

TEST_F(OptionalIndexedOffsetArrayTest, writeWithoutOptional)
{
    const bool hasOptional = false;
    const bool createWrongOffsets = false;
    OptionalIndexedOffsetArray optionalIndexedOffsetArray;
    fillOptionalIndexedOffsetArray(optionalIndexedOffsetArray, hasOptional, createWrongOffsets);

    zserio::BitStreamWriter writer;
    optionalIndexedOffsetArray.write(writer);

    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    OptionalIndexedOffsetArray readOptionalIndexedOffsetArray(reader);
    checkOptionalIndexedOffsetArray(readOptionalIndexedOffsetArray, hasOptional);
    ASSERT_TRUE(optionalIndexedOffsetArray == readOptionalIndexedOffsetArray);
}

} // namespace optional_indexed_offset_array
} // namespace indexed_offsets
