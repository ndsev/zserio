#include "gtest/gtest.h"
#include "parameterized_types/dynamic_bitfield_param/DynamicBitfieldParamHolder.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"

namespace parameterized_types
{
namespace dynamic_bitfield_param
{

class DynamicBitfieldParamTest : public ::testing::Test
{
protected:
    void fillDynamicBitfieldParamHolder(DynamicBitfieldParamHolder& dynamicBitfieldParamHolder)
    {
        DynamicBitfieldParam& dynamicBitfieldParam = dynamicBitfieldParamHolder.getDynamicBitfieldParam();
        dynamicBitfieldParam.setValue(DYNAMIC_BITFIELD_PARAM_VALUE);
        dynamicBitfieldParam.setExtraValue(DYNAMIC_BITFIELD_PARAM_EXTRA_VALUE);

        dynamicBitfieldParamHolder.initializeChildren();
    }

    void checkDynamicBitfieldParamHolderInBitStream(
            zserio::BitStreamReader& reader, const DynamicBitfieldParamHolder& dynamicBitfieldParamHolder)
    {
        ASSERT_EQ(dynamicBitfieldParamHolder.getLength(), reader.readBits(4));
        ASSERT_EQ(dynamicBitfieldParamHolder.getBitfield(), reader.readSignedBits(LENGTH));

        const DynamicBitfieldParam& dynamicBitfieldParam = dynamicBitfieldParamHolder.getDynamicBitfieldParam();
        ASSERT_EQ(dynamicBitfieldParam.getParam(), BITFIELD);
        ASSERT_EQ(dynamicBitfieldParam.getValue(), reader.readBits(16));
        ASSERT_EQ(dynamicBitfieldParam.getExtraValue(), reader.readBits(32));
    }

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);

private:
    static const uint8_t LENGTH;
    static const uint16_t BITFIELD;
    static const uint16_t DYNAMIC_BITFIELD_PARAM_VALUE;
    static const uint32_t DYNAMIC_BITFIELD_PARAM_EXTRA_VALUE;
};

const uint8_t DynamicBitfieldParamTest::LENGTH = 5;
const uint16_t DynamicBitfieldParamTest::BITFIELD = 11;
const uint16_t DynamicBitfieldParamTest::DYNAMIC_BITFIELD_PARAM_VALUE = 0x0BED;
const uint32_t DynamicBitfieldParamTest::DYNAMIC_BITFIELD_PARAM_EXTRA_VALUE = 0x0BEDDEAD;

TEST_F(DynamicBitfieldParamTest, write)
{
    DynamicBitfieldParamHolder dynamicBitfieldParamHolder;
    fillDynamicBitfieldParamHolder(dynamicBitfieldParamHolder);

    zserio::BitStreamWriter writer(bitBuffer);
    dynamicBitfieldParamHolder.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    checkDynamicBitfieldParamHolderInBitStream(reader, dynamicBitfieldParamHolder);
    reader.setBitPosition(0);

    DynamicBitfieldParamHolder readDynamicBitfieldParamHolder(reader);
    ASSERT_EQ(dynamicBitfieldParamHolder, readDynamicBitfieldParamHolder);
}

} // namespace dynamic_bitfield_param
} // namespace parameterized_types
