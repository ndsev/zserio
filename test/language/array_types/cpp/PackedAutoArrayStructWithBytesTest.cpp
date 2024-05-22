#include <array>
#include <vector>

#include "array_types/packed_auto_array_struct_with_bytes/PackedAutoArray.h"
#include "gtest/gtest.h"
#include "zserio/RebindAlloc.h"
#include "zserio/SerializeUtil.h"

namespace array_types
{
namespace packed_auto_array_struct_with_bytes
{

using allocator_type = PackedAutoArray::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class PackedAutoArrayStructWithBytesTest : public ::testing::Test
{
protected:
    PackedAutoArray createPackedAutoArray()
    {
        PackedAutoArray packedAutoArray;
        auto& array = packedAutoArray.getArray();
        for (size_t i = 0; i < UINT8_FIELD.size(); ++i)
        {
            array.emplace_back(UINT32_FIELD[i], BYTES_FIELD[i], UINT8_FIELD[i]);
        }

        return packedAutoArray;
    }

    static const std::array<uint32_t, 10> UINT32_FIELD;
    static const std::array<vector_type<uint8_t>, 10> BYTES_FIELD;
    static const std::array<uint8_t, 10> UINT8_FIELD;

    static const uint8_t UINT8_MAX_BIT_NUMBER;
    static const size_t PACKED_AUTO_ARRAY_BIT_SIZE;

    static const std::string BLOB_NAME;
};

const std::array<uint32_t, 10> PackedAutoArrayStructWithBytesTest::UINT32_FIELD = {
        100000, 110000, 120000, 130000, 140000, 150000, 160000, 170000, 180000, 190000};
const std::array<vector_type<uint8_t>, 10> PackedAutoArrayStructWithBytesTest::BYTES_FIELD = {
        vector_type<uint8_t>{0xAB, 0xCD, 0xEF}, vector_type<uint8_t>{0x00}, vector_type<uint8_t>{0x01},
        vector_type<uint8_t>{0x00}, vector_type<uint8_t>{0x01}, vector_type<uint8_t>{0x00},
        vector_type<uint8_t>{0x01}, vector_type<uint8_t>{0x00}, vector_type<uint8_t>{0x01},
        vector_type<uint8_t>{0x00}};
const std::array<uint8_t, 10> PackedAutoArrayStructWithBytesTest::UINT8_FIELD = {
        0, 2, 4, 6, 8, 10, 12, 14, 16, 18};

const std::string PackedAutoArrayStructWithBytesTest::BLOB_NAME =
        "language/array_types/packed_auto_array_struct_with_bytes.blob";

TEST_F(PackedAutoArrayStructWithBytesTest, writeRead)
{
    auto packedAutoArray = createPackedAutoArray();
    zserio::BitBuffer bitBuffer(8 * 1024);
    zserio::BitStreamWriter writer(bitBuffer);
    packedAutoArray.write(writer);

    ASSERT_EQ(writer.getBitPosition(), packedAutoArray.bitSizeOf());
    ASSERT_EQ(writer.getBitPosition(), packedAutoArray.initializeOffsets());

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    auto readPackedAutoArray = PackedAutoArray(reader);
    ASSERT_EQ(packedAutoArray, readPackedAutoArray);
}

TEST_F(PackedAutoArrayStructWithBytesTest, writeReadFile)
{
    auto packedAutoArray = createPackedAutoArray();
    zserio::serializeToFile(packedAutoArray, BLOB_NAME);

    auto readPackedAutoArray = zserio::deserializeFromFile<PackedAutoArray>(BLOB_NAME);
    ASSERT_EQ(packedAutoArray, readPackedAutoArray);
}

} // namespace packed_auto_array_struct_with_bytes
} // namespace array_types
