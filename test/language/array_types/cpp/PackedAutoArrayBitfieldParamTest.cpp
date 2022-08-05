#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/SerializeUtil.h"

#include "array_types/packed_auto_array_bitfield_param/ParameterizedBitfieldLength.h"

namespace array_types
{
namespace packed_auto_array_bitfield_param
{

using allocator_type = ParameterizedBitfieldLength::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class PackedAutoArrayBitfieldParamTest : public ::testing::Test
{
protected:
    void fillParameterizedBitfieldLength(ParameterizedBitfieldLength& parameterizedBitfieldLength)
    {
        // usage to none-const getter is intended to check old C++ bug
        vector_type<uint16_t>& dynamicBitfieldArray = parameterizedBitfieldLength.getDynamicBitfieldArray();
        for (uint16_t i = 0; i < DYNAMIC_BITFIELD_ARRAY_SIZE; ++i)
            dynamicBitfieldArray.push_back(i);
    }

    static const std::string BLOB_NAME;
    static const uint8_t NUM_BITS_PARAM;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);

private:
    static const size_t DYNAMIC_BITFIELD_ARRAY_SIZE;
};

const std::string PackedAutoArrayBitfieldParamTest::BLOB_NAME =
        "language/array_types/packed_auto_array_bitfield_param.blob";
const uint8_t PackedAutoArrayBitfieldParamTest::NUM_BITS_PARAM = 9;

const size_t PackedAutoArrayBitfieldParamTest::DYNAMIC_BITFIELD_ARRAY_SIZE = (1 << 9) - 1;

TEST_F(PackedAutoArrayBitfieldParamTest, writeRead)
{
    ParameterizedBitfieldLength parameterizedBitfieldLength;
    fillParameterizedBitfieldLength(parameterizedBitfieldLength);
    parameterizedBitfieldLength.initialize(NUM_BITS_PARAM);

    zserio::BitStreamWriter writer(bitBuffer);
    parameterizedBitfieldLength.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ParameterizedBitfieldLength readParameterizedBitfieldLength(reader, NUM_BITS_PARAM);
    ASSERT_EQ(parameterizedBitfieldLength, readParameterizedBitfieldLength);
}

TEST_F(PackedAutoArrayBitfieldParamTest, writeReadFile)
{
    ParameterizedBitfieldLength parameterizedBitfieldLength;
    fillParameterizedBitfieldLength(parameterizedBitfieldLength);

    zserio::serializeToFile(parameterizedBitfieldLength, BLOB_NAME, NUM_BITS_PARAM);

    const auto readParameterizedBitfieldLength =
            zserio::deserializeFromFile<ParameterizedBitfieldLength>(BLOB_NAME, NUM_BITS_PARAM);
    ASSERT_EQ(parameterizedBitfieldLength, readParameterizedBitfieldLength);
}

} // namespace packed_auto_array_bitfield_param
} // namespace array_types
