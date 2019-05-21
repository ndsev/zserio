#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "parameterized_types/subtyped_bitfield_param/SubtypedBitfieldParamHolder.h"

namespace parameterized_types
{
namespace subtyped_bitfield_param
{

class SubtypedBitfieldParamTest : public ::testing::Test
{
protected:
    void fillSubtypedBitfieldParamHolder(SubtypedBitfieldParamHolder& subtypedBitfieldParamHolder)
    {
        SubtypedBitfieldParam& subtypedBitfieldParam = subtypedBitfieldParamHolder.getSubtypedBitfieldParam();
        subtypedBitfieldParam.setValue(SUBTYPED_BITFIELD_PARAM_VALUE);
        subtypedBitfieldParam.setExtraValue(SUBTYPED_BITFIELD_PARAM_EXTRA_VALUE);
        subtypedBitfieldParamHolder.initializeChildren();
    }

    void checkSubtypedBitfieldParamHolderInBitStream(zserio::BitStreamReader& reader,
            const SubtypedBitfieldParamHolder& subtypedBitfieldParamHolder)
    {
        const SubtypedBitfieldParam& subtypedBitfieldParam =
                subtypedBitfieldParamHolder.getSubtypedBitfieldParam();

        ASSERT_EQ(subtypedBitfieldParam.getParam(), SUBTYPED_BITFIELD_PARAM);
        ASSERT_EQ(subtypedBitfieldParam.getValue(), reader.readBits(16));
        ASSERT_EQ(subtypedBitfieldParam.getExtraValue(), reader.readBits(32));
    }

private:
    static const ParamType SUBTYPED_BITFIELD_PARAM;
    static const uint16_t SUBTYPED_BITFIELD_PARAM_VALUE;
    static const uint32_t SUBTYPED_BITFIELD_PARAM_EXTRA_VALUE;
};

const ParamType SubtypedBitfieldParamTest::SUBTYPED_BITFIELD_PARAM = 11;
const uint16_t SubtypedBitfieldParamTest::SUBTYPED_BITFIELD_PARAM_VALUE = 0x0BED;
const uint32_t SubtypedBitfieldParamTest::SUBTYPED_BITFIELD_PARAM_EXTRA_VALUE = 0x0BEDDEAD;

TEST_F(SubtypedBitfieldParamTest, write)
{
    SubtypedBitfieldParamHolder subtypedBitfieldParamHolder;
    fillSubtypedBitfieldParamHolder(subtypedBitfieldParamHolder);

    zserio::BitStreamWriter writer;
    subtypedBitfieldParamHolder.write(writer);

    size_t writerBufferByteSize;
    const uint8_t* writerBuffer = writer.getWriteBuffer(writerBufferByteSize);
    zserio::BitStreamReader reader(writerBuffer, writerBufferByteSize);
    checkSubtypedBitfieldParamHolderInBitStream(reader, subtypedBitfieldParamHolder);
    reader.setBitPosition(0);

    SubtypedBitfieldParamHolder readSubtypedBitfieldParamHolder(reader);
    ASSERT_EQ(subtypedBitfieldParamHolder, readSubtypedBitfieldParamHolder);
}

} // namespace subtyped_bitfield_param
} // namespace parameterized_types
