#include <vector>

#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "functions/structure_optional/ValueConsumerCreator.h"

namespace functions
{
namespace structure_optional
{

class FunctionsStructureOptionalTest : public ::testing::Test
{
protected:
    uint8_t calculateValue(uint8_t defaultValue, uint8_t externalValue)
    {
        return (defaultValue != INVALID_DEFAULT_VALUE) ? defaultValue : externalValue;
    }

    void writeValueConsumerCreatorToByteArray(zserio::BitStreamWriter& writer, uint8_t defaultValue,
            uint8_t externalValue)
    {
        writer.writeBits(defaultValue, 4);
        if (defaultValue == INVALID_DEFAULT_VALUE)
            writer.writeBits(externalValue, 4);

        writer.writeBool((calculateValue(defaultValue, externalValue) < SMALL_VALUE_THRESHOLD));
    }

    void createValueConsumerCreator(ValueConsumerCreator& valueConsumerCreator, uint8_t defaultValue,
            uint8_t externalValue)
    {
        ValueCalculator& valueCalculator = valueConsumerCreator.getValueCalculator();
        valueCalculator.setDefaultValue(defaultValue);
        if (defaultValue == INVALID_DEFAULT_VALUE)
            valueCalculator.setExternalValue(externalValue);

        ValueConsumer& valueConsumer = valueConsumerCreator.getValueConsumer();
        valueConsumer.setIsSmall(calculateValue(defaultValue, externalValue) < SMALL_VALUE_THRESHOLD);
    }

    void checkValueConsumerCreator(uint8_t defaultValue, uint8_t externalValue)
    {
        ValueConsumerCreator valueConsumerCreator;
        createValueConsumerCreator(valueConsumerCreator, defaultValue, externalValue);
        const uint8_t expectedValue = calculateValue(defaultValue, externalValue);
        ASSERT_EQ(expectedValue, valueConsumerCreator.getValueCalculator().value());

        zserio::BitStreamWriter writtenWriter;
        valueConsumerCreator.write(writtenWriter);
        size_t writtenWriterBufferByteSize;
        const uint8_t* writtenWriterBuffer = writtenWriter.getWriteBuffer(writtenWriterBufferByteSize);

        zserio::BitStreamWriter expectedWriter;
        writeValueConsumerCreatorToByteArray(expectedWriter, defaultValue, externalValue);
        size_t expectedWriterBufferByteSize;
        const uint8_t* expectedWriterBuffer = expectedWriter.getWriteBuffer(expectedWriterBufferByteSize);

        std::vector<uint8_t> writtenWriterVector(writtenWriterBuffer,
                                                 writtenWriterBuffer + writtenWriterBufferByteSize);
        std::vector<uint8_t> expectedWriterVector(expectedWriterBuffer,
                                                  expectedWriterBuffer + expectedWriterBufferByteSize);
        ASSERT_EQ(expectedWriterVector, writtenWriterVector);

        zserio::BitStreamReader reader(writtenWriterBuffer, writtenWriterBufferByteSize);
        const ValueConsumerCreator readValueConsumerCreator(reader);
        ASSERT_EQ(valueConsumerCreator, readValueConsumerCreator);
    }

    static const uint8_t    INVALID_DEFAULT_VALUE = 0;
    static const uint8_t    DEFAULT_VALUE = 1;
    static const uint8_t    EXTERNAL_VALUE = 2;
    static const uint8_t    SMALL_VALUE_THRESHOLD = 8;
};

TEST_F(FunctionsStructureOptionalTest, checkDefaultValueConsumerCreator)
{
    checkValueConsumerCreator(DEFAULT_VALUE, EXTERNAL_VALUE);
}

TEST_F(FunctionsStructureOptionalTest, checkExternalValueConsumerCreator)
{
    checkValueConsumerCreator(INVALID_DEFAULT_VALUE, EXTERNAL_VALUE);
}

} // namespace structure_optional
} // namespace functions
