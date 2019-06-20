#include <vector>

#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/ObjectArray.h"

#include "functions/structure_param/MetresConverter.h"
#include "functions/structure_param/MetresConverterCaller.h"

namespace functions
{
namespace structure_param
{

class FunctionsStructureParamTest : public ::testing::Test
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
        metresConverter.setA(VALUE_A);
        metresConverterCaller.setCm(CONVERTED_CM_VALUE);
   }

    static const uint16_t VALUE_A = 0xABCD;
    static const uint16_t M_VALUE_TO_CONVERT = 2;
    static const uint16_t CONVERTED_CM_VALUE = M_VALUE_TO_CONVERT * 100;
};

TEST_F(FunctionsStructureParamTest, checkMetresConverterCaller)
{
    MetresConverterCaller metresConverterCaller;
    createMetresConverterCaller(metresConverterCaller);
    const uint16_t expectedCm = CONVERTED_CM_VALUE;
    ASSERT_EQ(expectedCm, metresConverterCaller.getCm());

    zserio::BitStreamWriter writtenWriter;
    metresConverterCaller.write(writtenWriter);
    size_t writtenWriterBufferByteSize;
    const uint8_t* writtenWriterBuffer = writtenWriter.getWriteBuffer(writtenWriterBufferByteSize);

    zserio::BitStreamWriter expectedWriter;
    writeMetresConverterCallerToByteArray(expectedWriter);
    size_t expectedWriterBufferByteSize;
    const uint8_t* expectedWriterBuffer = expectedWriter.getWriteBuffer(expectedWriterBufferByteSize);

    std::vector<uint8_t> writtenWriterVector(writtenWriterBuffer,
                                             writtenWriterBuffer + writtenWriterBufferByteSize);
    std::vector<uint8_t> expectedWriterVector(expectedWriterBuffer,
                                              expectedWriterBuffer + expectedWriterBufferByteSize);
    ASSERT_EQ(expectedWriterVector, writtenWriterVector);

    zserio::BitStreamReader reader(writtenWriterBuffer, writtenWriterBufferByteSize);
    const MetresConverterCaller readMetresConverterCaller(reader);
    ASSERT_EQ(metresConverterCaller, readMetresConverterCaller);
}

} // namespace structure_param
} // namespace functions
