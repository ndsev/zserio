#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "parameterized_types/parameterized_param/ParameterizedParamHolder.h"

namespace parameterized_types
{
namespace parameterized_param
{

class ParameterizedParamTest : public ::testing::Test
{
protected:
    void fillParameterizedParamHolder(ParameterizedParamHolder& parameterizedParamHolder)
    {
        Param& param = parameterizedParamHolder.getParam();
        param.setValue(PARAM_VALUE);
        param.setExtraValue(PARAM_EXTRA_VALUE);

        ParameterizedParam& parameterizedParam = parameterizedParamHolder.getParameterizedParam();
        parameterizedParam.setValue(PARAMETERIZED_PARAM_VALUE);
        parameterizedParam.setExtraValue(PARAMETERIZED_PARAM_EXTRA_VALUE);

        parameterizedParamHolder.initializeChildren();
    }

    void checkParameterizedParamHolderInBitStream(zserio::BitStreamReader& reader,
            const ParameterizedParamHolder& parameterizedParamHolder)
    {
        ASSERT_EQ(parameterizedParamHolder.getParameter(), reader.readBits(16));

        const Param& param = parameterizedParamHolder.getParam();
        ASSERT_EQ(param.getParameter(), PARAMETER);
        ASSERT_EQ(param.getValue(), reader.readBits(16));
        ASSERT_EQ(param.getExtraValue(), reader.readBits(32));

        const ParameterizedParam& parameterizedParam = parameterizedParamHolder.getParameterizedParam();
        ASSERT_EQ(parameterizedParam.getParam(), param);
        ASSERT_EQ(parameterizedParam.getValue(), reader.readBits(16));
        ASSERT_EQ(parameterizedParam.getExtraValue(), reader.readBits(32));
    }

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);

private:
    static const uint16_t PARAMETER;
    static const uint16_t PARAM_VALUE;
    static const uint32_t PARAM_EXTRA_VALUE;
    static const uint16_t PARAMETERIZED_PARAM_VALUE;
    static const uint32_t PARAMETERIZED_PARAM_EXTRA_VALUE;
};

const uint16_t ParameterizedParamTest::PARAMETER = 11;
const uint16_t ParameterizedParamTest::PARAM_VALUE = 0x0BED;
const uint32_t ParameterizedParamTest::PARAM_EXTRA_VALUE = 0x0BEDDEAD;
const uint16_t ParameterizedParamTest::PARAMETERIZED_PARAM_VALUE = 0x0BAD;
const uint32_t ParameterizedParamTest::PARAMETERIZED_PARAM_EXTRA_VALUE = 0x0BADDEAD;

TEST_F(ParameterizedParamTest, write)
{
    ParameterizedParamHolder parameterizedParamHolder;
    fillParameterizedParamHolder(parameterizedParamHolder);

    zserio::BitStreamWriter writer(bitBuffer);
    parameterizedParamHolder.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    checkParameterizedParamHolderInBitStream(reader, parameterizedParamHolder);
    reader.setBitPosition(0);

    ParameterizedParamHolder readParameterizedParamHolder(reader);
    ASSERT_EQ(parameterizedParamHolder, readParameterizedParamHolder);
}

} // namespace parameterized_param
} // namespace parameterized_types
