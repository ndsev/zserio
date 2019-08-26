#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "choice_types/default_empty_choice/DefaultEmptyChoice.h"

namespace choice_types
{
namespace default_empty_choice
{

class DefaultEmptyChoiceTest : public ::testing::Test
{
protected:
    void writeDefaultEmptyChoiceToByteArray(zserio::BitStreamWriter& writer, uint8_t tag, int32_t value)
    {
        switch (tag)
        {
        case 1:
            writer.writeSignedBits(value, 8);
            break;

        case 2:
            writer.writeSignedBits(value, 16);
            break;

        default:
            break;
        }
    }

    static const uint8_t VARIANT_A_SELECTOR;
    static const uint8_t VARIANT_B_SELECTOR;
    static const uint8_t DEFAULT_SELECTOR;
};

const uint8_t DefaultEmptyChoiceTest::VARIANT_A_SELECTOR = 1;
const uint8_t DefaultEmptyChoiceTest::VARIANT_B_SELECTOR = 2;
const uint8_t DefaultEmptyChoiceTest::DEFAULT_SELECTOR = 3;

TEST_F(DefaultEmptyChoiceTest, bitStreamReaderConstructor)
{
    const uint8_t tag = VARIANT_A_SELECTOR;
    zserio::BitStreamWriter writer;
    const int8_t value = 99;
    writeDefaultEmptyChoiceToByteArray(writer, tag, value);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);

    const DefaultEmptyChoice defaultEmptyChoice(reader, tag);
    ASSERT_EQ(tag, defaultEmptyChoice.getTag());
    ASSERT_EQ(value, defaultEmptyChoice.getA());
}

TEST_F(DefaultEmptyChoiceTest, read)
{
    const uint8_t tag = VARIANT_A_SELECTOR;
    zserio::BitStreamWriter writer;
    const int8_t value = 99;
    writeDefaultEmptyChoiceToByteArray(writer, tag, value);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    DefaultEmptyChoice defaultEmptyChoice;
    defaultEmptyChoice.initialize(tag);
    defaultEmptyChoice.read(reader);

    ASSERT_EQ(tag, defaultEmptyChoice.getTag());
    ASSERT_EQ(value, defaultEmptyChoice.getA());
}

TEST_F(DefaultEmptyChoiceTest, write)
{
    const uint8_t tagA = VARIANT_A_SELECTOR;
    DefaultEmptyChoice defaultEmptyChoice;
    defaultEmptyChoice.initialize(tagA);

    const VariantA valueA = 99;
    defaultEmptyChoice.setA(valueA);
    zserio::BitStreamWriter writerA;
    defaultEmptyChoice.write(writerA);
    size_t writeBufferByteSizeA;
    const uint8_t* writeBufferA = writerA.getWriteBuffer(writeBufferByteSizeA);
    zserio::BitStreamReader readerA(writeBufferA, writeBufferByteSizeA);
    DefaultEmptyChoice readDefaultEmptyChoiceA(readerA, tagA);
    ASSERT_EQ(valueA, readDefaultEmptyChoiceA.getA());

    const uint8_t tagB = VARIANT_B_SELECTOR;
    defaultEmptyChoice.initialize(tagB);
    const VariantB valueB = 234;
    defaultEmptyChoice.setB(valueB);
    zserio::BitStreamWriter writerB;
    defaultEmptyChoice.write(writerB);
    size_t writeBufferByteSizeB;
    const uint8_t* writeBufferB = writerB.getWriteBuffer(writeBufferByteSizeB);
    zserio::BitStreamReader readerB(writeBufferB, writeBufferByteSizeB);
    DefaultEmptyChoice readDefaultEmptyChoiceB(readerB, tagB);
    ASSERT_EQ(valueB, readDefaultEmptyChoiceB.getB());

    const uint8_t tagDefault= DEFAULT_SELECTOR;
    defaultEmptyChoice.initialize(tagDefault);
    zserio::BitStreamWriter writerDefault;
    defaultEmptyChoice.write(writerDefault);
    size_t writeBufferByteSizeDefault;
    const uint8_t* writeBufferDefault = writerDefault.getWriteBuffer(writeBufferByteSizeDefault);
    zserio::BitStreamReader readerDefault(writeBufferDefault, writeBufferByteSizeDefault);
    DefaultEmptyChoice readDefaultEmptyChoiceDefault(readerDefault, tagDefault);
    ASSERT_EQ(tagDefault, readDefaultEmptyChoiceDefault.getTag());
}

} // namespace default_empty_choice
} // namespace choice_types
