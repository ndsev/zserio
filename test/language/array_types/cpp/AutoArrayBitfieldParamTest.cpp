#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/SerializeUtil.h"

#include "array_types/auto_array_bitfield_param/ParameterizedBitfieldLength.h"

namespace array_types
{
namespace auto_array_bitfield_param
{

using allocator_type = ParameterizedBitfieldLength::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class AutoArrayBitfieldParamTest : public ::testing::Test
{
protected:
    void fillParameterizedBitfieldLength(ParameterizedBitfieldLength& parameterizedBitfieldLength)
    {
        // usage to none-const getter is intended to check old C++ bug
        vector_type<uint16_t>& dynamicBitfieldArray = parameterizedBitfieldLength.getDynamicBitfieldArray();
        for (uint16_t i = 0; i < DYNAMIC_BITFIELD_ARRAY_SIZE; ++i)
            dynamicBitfieldArray.push_back(i);
    }

    void checkParameterizedBitfieldLengthInBitStream(zserio::BitStreamReader& reader,
            const ParameterizedBitfieldLength& parameterizedBitfieldLength)
    {
        ASSERT_EQ(NUM_BITS_PARAM, parameterizedBitfieldLength.getNumBits());
        ASSERT_EQ(DYNAMIC_BITFIELD_ARRAY_SIZE, reader.readVarSize());
        for (uint16_t i = 0; i < DYNAMIC_BITFIELD_ARRAY_SIZE; ++i)
            ASSERT_EQ(i, reader.readBits(NUM_BITS_PARAM));
    }

    static const std::string BLOB_NAME;
    static const uint8_t NUM_BITS_PARAM;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);

private:
    static const size_t DYNAMIC_BITFIELD_ARRAY_SIZE;
};

const std::string AutoArrayBitfieldParamTest::BLOB_NAME = "language/array_types/auto_array_bitfield_param.blob";
const uint8_t AutoArrayBitfieldParamTest::NUM_BITS_PARAM = 9;

const size_t AutoArrayBitfieldParamTest::DYNAMIC_BITFIELD_ARRAY_SIZE = (1U << 9U) - 1;

TEST_F(AutoArrayBitfieldParamTest, writeRead)
{
    ParameterizedBitfieldLength parameterizedBitfieldLength;
    fillParameterizedBitfieldLength(parameterizedBitfieldLength);
    parameterizedBitfieldLength.initialize(NUM_BITS_PARAM);

    zserio::BitStreamWriter writer(bitBuffer);
    parameterizedBitfieldLength.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    checkParameterizedBitfieldLengthInBitStream(reader, parameterizedBitfieldLength);
    reader.setBitPosition(0);

    ParameterizedBitfieldLength readParameterizedBitfieldLength(reader, NUM_BITS_PARAM);
    ASSERT_EQ(parameterizedBitfieldLength, readParameterizedBitfieldLength);
}

TEST_F(AutoArrayBitfieldParamTest, writeReadFile)
{
    ParameterizedBitfieldLength parameterizedBitfieldLength;
    fillParameterizedBitfieldLength(parameterizedBitfieldLength);

    zserio::serializeToFile(parameterizedBitfieldLength, BLOB_NAME, NUM_BITS_PARAM);

    const auto readParameterizedBitfieldLength =
            zserio::deserializeFromFile<ParameterizedBitfieldLength>(BLOB_NAME, NUM_BITS_PARAM);
    ASSERT_EQ(parameterizedBitfieldLength, readParameterizedBitfieldLength);
}

} // namespace auto_array_bitfield_param
} // namespace array_types
