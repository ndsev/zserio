#include "gtest/gtest.h"

#include "indexed_offsets/optional_nested_indexed_offset_array/OptionalNestedIndexedOffsetArray.h"
#include "indexed_offsets/optional_nested_indexed_offset_array/Header.h"

#include "zserio/RebindAlloc.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

namespace indexed_offsets
{
namespace optional_nested_indexed_offset_array
{

using allocator_type = OptionalNestedIndexedOffsetArray::allocator_type;
using string_type = zserio::string<allocator_type>;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class OptionalNestedIndexedOffsetArrayTest : public ::testing::Test
{
public:
    OptionalNestedIndexedOffsetArrayTest()
    {
        const char* data[NUM_ELEMENTS] = {"Green", "Red", "Pink", "Blue", "Black"};
        for (uint8_t i = 0; i < NUM_ELEMENTS; ++i)
            m_data.push_back(data[i]);
    }
protected:
    void writeOptionalNestedIndexedOffsetArrayToByteArray(zserio::BitStreamWriter& writer, uint16_t length,
            bool writeWrongOffsets)
    {
        writer.writeBits(length, 16);

        if (length > 0)
        {
            const uint32_t wrongOffset = WRONG_OFFSET;
            uint32_t currentOffset = ELEMENT0_OFFSET;
            for (uint8_t i = 0; i < length; ++i)
            {
                if ((i + 1) == length && writeWrongOffsets)
                    writer.writeBits(wrongOffset, 32);
                else
                    writer.writeBits(currentOffset, 32);
                currentOffset += static_cast<uint32_t>(zserio::bitSizeOfString(m_data[i]) / 8);
            }

            // already aligned
            for (uint8_t i = 0; i < length; ++i)
                writer.writeString(m_data[i]);
        }

        writer.writeBits(FIELD_VALUE, 6);
    }

    void checkOffsets(const OptionalNestedIndexedOffsetArray& optionalNestedIndexedOffsetArray,
            uint16_t offsetShift)
    {
        const uint16_t length = optionalNestedIndexedOffsetArray.getHeader().getLength();
        const vector_type<uint32_t>& offsets = optionalNestedIndexedOffsetArray.getHeader().getOffsets();
        ASSERT_EQ(length, offsets.size());
        uint32_t expectedOffset = ELEMENT0_OFFSET + offsetShift;
        for (uint8_t i = 0; i < length; ++i)
        {
            ASSERT_EQ(expectedOffset, offsets[i]);
            expectedOffset += static_cast<uint32_t>(zserio::bitSizeOfString(m_data[i]) / 8);
        }
    }

    void checkOptionalNestedIndexedOffsetArray(
            const OptionalNestedIndexedOffsetArray& optionalNestedIndexedOffsetArray, int16_t length)
    {
        ASSERT_EQ(length, optionalNestedIndexedOffsetArray.getHeader().getLength());

        const uint16_t offsetShift = 0;
        checkOffsets(optionalNestedIndexedOffsetArray, offsetShift);

        if (length > 0)
        {
            const vector_type<string_type>& data = optionalNestedIndexedOffsetArray.getData();
            ASSERT_EQ(length, data.size());
            for (uint8_t i = 0; i < length; ++i)
                ASSERT_EQ(m_data[i], data[i]);
        }

        const uint8_t expectedField = FIELD_VALUE;
        ASSERT_EQ(expectedField, optionalNestedIndexedOffsetArray.getField());
    }

    void fillOptionalNestedIndexedOffsetArray(
            OptionalNestedIndexedOffsetArray& optionalNestedIndexedOffsetArray, uint16_t length,
            bool createWrongOffsets)
    {
        Header& header = optionalNestedIndexedOffsetArray.getHeader();
        header.setLength(length);
        vector_type<uint32_t>& offsets = header.getOffsets();
        offsets.reserve(length);
        const uint32_t wrongOffset = WRONG_OFFSET;
        uint32_t currentOffset = ELEMENT0_OFFSET;
        for (uint8_t i = 0; i < length; ++i)
        {
            if ((i + 1) == length && createWrongOffsets)
                offsets.push_back(wrongOffset);
            else
                offsets.push_back(currentOffset);
            currentOffset += static_cast<uint32_t>(zserio::bitSizeOfString(m_data[i]) / 8);
        }

        if (length > 0)
            optionalNestedIndexedOffsetArray.setData(m_data);

        optionalNestedIndexedOffsetArray.setField(FIELD_VALUE);
    }

    size_t getOptionalNestedIndexedOffsetArrayBitSize(uint16_t length)
    {
        size_t bitSize = sizeof(int16_t) * 8  + length * sizeof(uint32_t) * 8;
        if (length > 0)
        {
            // already aligned
            for (uint16_t i = 0; i < length; ++i)
                bitSize += zserio::bitSizeOfString(m_data[i]);
        }
        bitSize += 6;

        return bitSize;
    }

    static const uint8_t    NUM_ELEMENTS = 5;

    static const uint32_t   WRONG_OFFSET = 0;
    static const uint32_t   ELEMENT0_OFFSET = sizeof(int16_t) + NUM_ELEMENTS * sizeof(uint32_t);

    static const uint8_t    FIELD_VALUE = 63;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);

private:
    vector_type<string_type> m_data;
};

TEST_F(OptionalNestedIndexedOffsetArrayTest, readWithOptional)
{
    const uint16_t length = NUM_ELEMENTS;
    const bool writeWrongOffsets = false;
    zserio::BitStreamWriter writer(bitBuffer);
    writeOptionalNestedIndexedOffsetArrayToByteArray(writer, length, writeWrongOffsets);
    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());

    OptionalNestedIndexedOffsetArray optionalNestedIndexedOffsetArray(reader);
    checkOptionalNestedIndexedOffsetArray(optionalNestedIndexedOffsetArray, length);
}

TEST_F(OptionalNestedIndexedOffsetArrayTest, readWithoutOptional)
{
    const uint16_t length = 0;
    const bool writeWrongOffsets = false;
    zserio::BitStreamWriter writer(bitBuffer);
    writeOptionalNestedIndexedOffsetArrayToByteArray(writer, length, writeWrongOffsets);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    OptionalNestedIndexedOffsetArray optionalNestedIndexedOffsetArray(reader);
    checkOptionalNestedIndexedOffsetArray(optionalNestedIndexedOffsetArray, length);
}

TEST_F(OptionalNestedIndexedOffsetArrayTest, bitSizeOfWithOptional)
{
    const uint16_t length = NUM_ELEMENTS;
    const bool createWrongOffsets = false;
    OptionalNestedIndexedOffsetArray optionalNestedIndexedOffsetArray;
    fillOptionalNestedIndexedOffsetArray(optionalNestedIndexedOffsetArray, length, createWrongOffsets);

    const size_t expectedBitSize = getOptionalNestedIndexedOffsetArrayBitSize(length);
    ASSERT_EQ(expectedBitSize, optionalNestedIndexedOffsetArray.bitSizeOf());
}

TEST_F(OptionalNestedIndexedOffsetArrayTest, bitSizeOfWithoutOptional)
{
    const uint16_t length = 0;
    const bool createWrongOffsets = false;
    OptionalNestedIndexedOffsetArray optionalNestedIndexedOffsetArray;
    fillOptionalNestedIndexedOffsetArray(optionalNestedIndexedOffsetArray, length, createWrongOffsets);

    const size_t expectedBitSize = getOptionalNestedIndexedOffsetArrayBitSize(length);
    ASSERT_EQ(expectedBitSize, optionalNestedIndexedOffsetArray.bitSizeOf());
}

TEST_F(OptionalNestedIndexedOffsetArrayTest, initializeOffsetsWithOptional)
{
    const uint16_t length = NUM_ELEMENTS;
    const bool createWrongOffsets = true;
    OptionalNestedIndexedOffsetArray optionalNestedIndexedOffsetArray;
    fillOptionalNestedIndexedOffsetArray(optionalNestedIndexedOffsetArray, length, createWrongOffsets);

    const size_t bitPosition = 0;
    const size_t expectedBitSize = getOptionalNestedIndexedOffsetArrayBitSize(length);
    ASSERT_EQ(expectedBitSize, optionalNestedIndexedOffsetArray.initializeOffsets(bitPosition));
    checkOptionalNestedIndexedOffsetArray(optionalNestedIndexedOffsetArray, length);
}

TEST_F(OptionalNestedIndexedOffsetArrayTest, initializeOffsetsWithoutOptional)
{
    const uint16_t length = 0;
    const bool createWrongOffsets = true;
    OptionalNestedIndexedOffsetArray optionalNestedIndexedOffsetArray;
    fillOptionalNestedIndexedOffsetArray(optionalNestedIndexedOffsetArray, length, createWrongOffsets);

    const size_t bitPosition = 0;
    const size_t expectedBitSize = getOptionalNestedIndexedOffsetArrayBitSize(length);
    ASSERT_EQ(expectedBitSize, optionalNestedIndexedOffsetArray.initializeOffsets(bitPosition));
}

TEST_F(OptionalNestedIndexedOffsetArrayTest, writeWithOptional)
{
    const uint16_t length = NUM_ELEMENTS;
    const bool createWrongOffsets = false;
    OptionalNestedIndexedOffsetArray optionalNestedIndexedOffsetArray;
    fillOptionalNestedIndexedOffsetArray(optionalNestedIndexedOffsetArray, length, createWrongOffsets);

    zserio::BitStreamWriter writer(bitBuffer);
    optionalNestedIndexedOffsetArray.write(writer);
    checkOptionalNestedIndexedOffsetArray(optionalNestedIndexedOffsetArray, length);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    OptionalNestedIndexedOffsetArray readOptionalNestedIndexedOffsetArray(reader);
    checkOptionalNestedIndexedOffsetArray(readOptionalNestedIndexedOffsetArray, length);
    ASSERT_TRUE(optionalNestedIndexedOffsetArray == readOptionalNestedIndexedOffsetArray);
}

TEST_F(OptionalNestedIndexedOffsetArrayTest, writeWithoutOptional)
{
    const uint16_t length = 0;
    const bool createWrongOffsets = false;
    OptionalNestedIndexedOffsetArray optionalNestedIndexedOffsetArray;
    fillOptionalNestedIndexedOffsetArray(optionalNestedIndexedOffsetArray, length, createWrongOffsets);

    zserio::BitStreamWriter writer(bitBuffer);
    optionalNestedIndexedOffsetArray.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    OptionalNestedIndexedOffsetArray readOptionalNestedIndexedOffsetArray(reader);
    checkOptionalNestedIndexedOffsetArray(readOptionalNestedIndexedOffsetArray, length);
    ASSERT_TRUE(optionalNestedIndexedOffsetArray == readOptionalNestedIndexedOffsetArray);
}

} // namespace optional_nested_indexed_offset_array
} // namespace indexed_offsets
