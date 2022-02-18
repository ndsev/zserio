#include "gtest/gtest.h"

#include "indexed_offsets/optional_indexed_offset_array/OptionalIndexedOffsetArray.h"

#include "zserio/RebindAlloc.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

namespace indexed_offsets
{
namespace optional_indexed_offset_array
{

using allocator_type = OptionalIndexedOffsetArray::allocator_type;
using string_type = zserio::string<zserio::RebindAlloc<allocator_type, char>>;
template <typename T>
using vector_type = std::vector<T, zserio::RebindAlloc<allocator_type, T>>;

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
            currentOffset += static_cast<uint32_t>(zserio::bitSizeOfString(m_data[i]) / 8);
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
        const vector_type<uint32_t>& offsets = optionalIndexedOffsetArray.getOffsets();
        const size_t expectedNumElements = NUM_ELEMENTS;
        ASSERT_EQ(expectedNumElements, offsets.size());
        uint32_t expectedOffset = ELEMENT0_OFFSET + offsetShift;
        for (uint8_t i = 0; i < NUM_ELEMENTS; ++i)
        {
            ASSERT_EQ(expectedOffset, offsets[i]);
            expectedOffset += static_cast<uint32_t>(zserio::bitSizeOfString(m_data[i]) / 8);
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
            const vector_type<string_type>& data = optionalIndexedOffsetArray.getData();
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
        vector_type<uint32_t>& offsets = optionalIndexedOffsetArray.getOffsets();
        offsets.reserve(NUM_ELEMENTS);
        const uint32_t wrongOffset = WRONG_OFFSET;
        uint32_t currentOffset = ELEMENT0_OFFSET;
        for (uint8_t i = 0; i < NUM_ELEMENTS; ++i)
        {
            if ((i + 1) == NUM_ELEMENTS && createWrongOffsets)
                offsets.push_back(wrongOffset);
            else
                offsets.push_back(currentOffset);
            currentOffset += static_cast<uint32_t>(zserio::bitSizeOfString(m_data[i]) / 8);
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
            for (size_t i = 0; i < NUM_ELEMENTS; ++i)
                bitSize += zserio::bitSizeOfString(m_data[i]);
        }
        bitSize += 6;

        return bitSize;
    }

    static const uint8_t    NUM_ELEMENTS = 5;

    static const uint32_t   WRONG_OFFSET = 0;
    static const uint32_t   ELEMENT0_OFFSET = NUM_ELEMENTS * sizeof(uint32_t) + sizeof(uint8_t);

    static const uint8_t    FIELD_VALUE = 63;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);

private:
    vector_type<string_type> m_data;
};

TEST_F(OptionalIndexedOffsetArrayTest, readWithOptional)
{
    const bool hasOptional = true;
    const bool writeWrongOffsets = false;
    zserio::BitStreamWriter writer(bitBuffer);
    writeOptionalIndexedOffsetArrayToByteArray(writer, hasOptional, writeWrongOffsets);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    OptionalIndexedOffsetArray optionalIndexedOffsetArray(reader);
    checkOptionalIndexedOffsetArray(optionalIndexedOffsetArray, hasOptional);
}

TEST_F(OptionalIndexedOffsetArrayTest, readWithoutOptional)
{
    const bool hasOptional = false;
    const bool writeWrongOffsets = false;
    zserio::BitStreamWriter writer(bitBuffer);
    writeOptionalIndexedOffsetArrayToByteArray(writer, hasOptional, writeWrongOffsets);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
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
    const size_t expectedBitSize = getOptionalIndexedOffsetArrayBitSize(hasOptional);
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
    const size_t expectedBitSize = getOptionalIndexedOffsetArrayBitSize(hasOptional);
    ASSERT_EQ(expectedBitSize, optionalIndexedOffsetArray.initializeOffsets(bitPosition));
}

TEST_F(OptionalIndexedOffsetArrayTest, writeWithOptional)
{
    const bool hasOptional = true;
    const bool createWrongOffsets = false;
    OptionalIndexedOffsetArray optionalIndexedOffsetArray;
    fillOptionalIndexedOffsetArray(optionalIndexedOffsetArray, hasOptional, createWrongOffsets);

    zserio::BitStreamWriter writer(bitBuffer);
    optionalIndexedOffsetArray.write(writer);
    checkOptionalIndexedOffsetArray(optionalIndexedOffsetArray, hasOptional);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
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

    zserio::BitStreamWriter writer(bitBuffer);
    optionalIndexedOffsetArray.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    OptionalIndexedOffsetArray readOptionalIndexedOffsetArray(reader);
    checkOptionalIndexedOffsetArray(readOptionalIndexedOffsetArray, hasOptional);
    ASSERT_TRUE(optionalIndexedOffsetArray == readOptionalIndexedOffsetArray);
}

} // namespace optional_indexed_offset_array
} // namespace indexed_offsets
