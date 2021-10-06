#include "gtest/gtest.h"

#include "array_types/packed_auto_array_struct_with_unpacked_field/PackedAutoArray.h"

#include "zserio/SerializeUtil.h"

namespace array_types
{
namespace packed_auto_array_struct_with_unpacked_field
{

class PackedAutoArrayStructWithUnpackedFieldTest : public ::testing::Test
{
protected:
    PackedAutoArray createPackedAutoArray()
    {
        PackedAutoArray packedAutoArray;
        auto& array = packedAutoArray.getArray();
        for (size_t i = 0; i < sizeof(UINT8_FIELD); ++i)
            array.emplace_back(UINT8_FIELD[i], UNPACKED_FIELD[i]);

        return packedAutoArray;
    }

    static const uint8_t UINT8_FIELD[10];
    static const uint64_t UNPACKED_FIELD[10];

    static const uint8_t UINT8_MAX_BIT_NUMBER;
    static const size_t PACKED_AUTO_ARRAY_BIT_SIZE;

    static const std::string BLOB_NAME;
};

const uint8_t PackedAutoArrayStructWithUnpackedFieldTest::UINT8_FIELD[] = {0, 2, 4, 6, 8, 10, 12, 14, 16, 18};
const uint64_t PackedAutoArrayStructWithUnpackedFieldTest::UNPACKED_FIELD[] =
        {5000000, 0, 1, 0, 1, 0, 1, 0, 1, 0};

const uint8_t PackedAutoArrayStructWithUnpackedFieldTest::UINT8_MAX_BIT_NUMBER = 2;
const size_t PackedAutoArrayStructWithUnpackedFieldTest::PACKED_AUTO_ARRAY_BIT_SIZE =
        8 + // auto array size: varsize
        1 + // uint8Field packing descriptor: isPacked (true)
        6 + // uint8Field is packed: maxBitNumber
        1 + // unpackedField packing descriptor: isPacked (false)
        8 + // UINT8_FIELD[0]
        32 + // UNPACKED_FIELD[0] (4 bytes for the first value)
        9 * (UINT8_MAX_BIT_NUMBER + 1) + // deltas for uint8Field values
        9 * 8; // unpackedField varuint values (1 byte)

const std::string PackedAutoArrayStructWithUnpackedFieldTest::BLOB_NAME =
        "language/array_types/packed_auto_array_struct_with_unpacked_field.blob";

TEST_F(PackedAutoArrayStructWithUnpackedFieldTest, bitSizeOf)
{
    auto packedAutoArray = createPackedAutoArray();
    ASSERT_EQ(PACKED_AUTO_ARRAY_BIT_SIZE, packedAutoArray.bitSizeOf());
}

TEST_F(PackedAutoArrayStructWithUnpackedFieldTest, initializeOffsets)
{
    auto packedAutoArray = createPackedAutoArray();
    const size_t bitPosition = 2;
    const size_t expectedEndBitPosition = 2 + PACKED_AUTO_ARRAY_BIT_SIZE;
    ASSERT_EQ(expectedEndBitPosition, packedAutoArray.initializeOffsets(bitPosition));
}

TEST_F(PackedAutoArrayStructWithUnpackedFieldTest, writeReadFile)
{
    auto packedAutoArray = createPackedAutoArray();
    zserio::serializeToFile(packedAutoArray, BLOB_NAME);

    auto readPackedAutoArray = zserio::deserializeFromFile<PackedAutoArray>(BLOB_NAME);
    ASSERT_EQ(packedAutoArray, readPackedAutoArray);
}

} // namespace packed_auto_array_struct_with_unpacked_field
} // namespace array_types
