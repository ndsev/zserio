#include <vector>

#include "functions/structure_param/MetresConverter.h"
#include "functions/structure_param/MetresConverterCaller.h"
#include "gtest/gtest.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"

namespace functions
{
namespace structure_param
{

class StructureParamTest : public ::testing::Test
{
protected:
    void writeMetresConverterCallerToByteArray(zserio::BitStreamWriter& writer)
    {
        writer.writeBits(VALUE_A, 16);
        writer.writeBits(CONVERTED_CM_VALUE, 16);
    }

    void createMetresConverterCaller(MetresConverterCaller& metresConverterCaller)
    {
        MetresConverter& metresConverter = metresConverterCaller.getMetresConverter();
        metresConverter.setValueA(VALUE_A);
        metresConverterCaller.setCentimeters(CONVERTED_CM_VALUE);

        metresConverterCaller.initializeChildren();
    }

    static const uint16_t VALUE_A = 0xABCD;
    static const uint16_t M_VALUE_TO_CONVERT = 2;
    static const uint16_t CONVERTED_CM_VALUE = M_VALUE_TO_CONVERT * 100;
};

TEST_F(StructureParamTest, checkMetresConverterCaller)
{
    MetresConverterCaller metresConverterCaller;
    createMetresConverterCaller(metresConverterCaller);
    const uint16_t expectedCm = CONVERTED_CM_VALUE;
    ASSERT_EQ(expectedCm, metresConverterCaller.getCentimeters());

    zserio::BitBuffer writtenBitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writtenWriter(writtenBitBuffer);
    metresConverterCaller.write(writtenWriter);

    zserio::BitBuffer expectedBitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter expectedWriter(expectedBitBuffer);
    writeMetresConverterCallerToByteArray(expectedWriter);

    ASSERT_EQ(expectedBitBuffer, writtenBitBuffer);

    zserio::BitStreamReader reader(writtenBitBuffer);
    const MetresConverterCaller readMetresConverterCaller(reader);
    ASSERT_EQ(metresConverterCaller, readMetresConverterCaller);
}

} // namespace structure_param
} // namespace functions
