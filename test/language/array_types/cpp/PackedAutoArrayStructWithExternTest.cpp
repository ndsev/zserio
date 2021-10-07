#include "gtest/gtest.h"

#include "array_types/packed_auto_array_struct_with_extern/PackedAutoArray.h"

#include "zserio/SerializeUtil.h"

#include <vector>

namespace array_types
{
namespace packed_auto_array_struct_with_extern
{

class PackedAutoArrayStructWithExternTest : public ::testing::Test
{
protected:
    PackedAutoArray createPackedAutoArray()
    {
        PackedAutoArray packedAutoArray;
        auto& array = packedAutoArray.getArray();
        for (size_t i = 0; i < sizeof(UINT8_FIELD); ++i)
            array.emplace_back(UINT32_FIELD[i], zserio::BitBuffer{EXTERN_FIELD[i]}, UINT8_FIELD[i]);

        return packedAutoArray;
    }

    static const uint32_t UINT32_FIELD[10];
    static const std::vector<uint8_t> EXTERN_FIELD[10];
    static const uint8_t UINT8_FIELD[10];

    static const uint8_t UINT8_MAX_BIT_NUMBER;
    static const size_t PACKED_AUTO_ARRAY_BIT_SIZE;

    static const std::string BLOB_NAME;
};

const uint32_t PackedAutoArrayStructWithExternTest::UINT32_FIELD[] = {
        100000, 110000, 120000, 130000, 140000, 150000, 160000, 170000, 180000, 190000};
const std::vector<uint8_t> PackedAutoArrayStructWithExternTest::EXTERN_FIELD[] =
        {{0xAB, 0xCD, 0xEF}, {0x00}, {0x01}, {0x00}, {0x01}, {0x00}, {0x01}, {0x00}, {0x01}, {0x00}};
const uint8_t PackedAutoArrayStructWithExternTest::UINT8_FIELD[] = {0, 2, 4, 6, 8, 10, 12, 14, 16, 18};

const std::string PackedAutoArrayStructWithExternTest::BLOB_NAME =
        "language/array_types/packed_auto_array_struct_with_extern.blob";

TEST_F(PackedAutoArrayStructWithExternTest, writeRead)
{
    auto packedAutoArray = createPackedAutoArray();
    zserio::BitBuffer bitBuffer(8 * 1024);
    zserio::BitStreamWriter writer(bitBuffer);
    packedAutoArray.write(writer);

    ASSERT_EQ(writer.getBitPosition(), packedAutoArray.bitSizeOf());
    ASSERT_EQ(writer.getBitPosition(), packedAutoArray.initializeOffsets(0));

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    auto readPackedAutoArray = PackedAutoArray(reader);
    ASSERT_EQ(packedAutoArray, readPackedAutoArray);
}

TEST_F(PackedAutoArrayStructWithExternTest, writeReadFile)
{
    auto packedAutoArray = createPackedAutoArray();
    zserio::serializeToFile(packedAutoArray, BLOB_NAME);

    auto readPackedAutoArray = zserio::deserializeFromFile<PackedAutoArray>(BLOB_NAME);
    ASSERT_EQ(packedAutoArray, readPackedAutoArray);
}

} // namespace packed_auto_array_struct_with_extern
} // namespace array_types
