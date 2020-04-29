#include <string>

#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/StringConvertUtil.h"
#include "zserio/CppRuntimeException.h"

#include "member_access/access_within_type/Message.h"

namespace member_access
{
namespace access_within_type
{

class AccessWithinTypeTest : public ::testing::Test
{
protected:
    void writeMessageToByteArray(zserio::BitStreamWriter& writer, uint16_t numSentences,
            bool wrongArrayLength)
    {
        writer.writeBits(VERSION_VALUE, 16);
        writer.writeBits(numSentences, 16);

        const uint16_t numStrings = (wrongArrayLength) ? numSentences - 1 : numSentences;
        for (uint16_t i = 0; i < numStrings; ++i)
            writer.writeString(getSentence(i));
    }

    void checkMessage(const Message& message, uint16_t numSentences)
    {
        const uint16_t expectedVersionValue = VERSION_VALUE;
        ASSERT_EQ(expectedVersionValue, message.getHeader().getVersion());
        ASSERT_EQ(numSentences, message.getHeader().getNumSentences());

        const std::vector<std::string>& sentences = message.getSentences();
        ASSERT_EQ(numSentences, sentences.size());
        for (uint16_t i = 0; i < numSentences; ++i)
        {
            const std::string& expectedSentence = getSentence(i);
            ASSERT_EQ(expectedSentence, sentences[i]);
        }
    }

    void fillMessage(Message& message, uint16_t numSentences, bool wrongArrayLength)
    {
        Header& header = message.getHeader();
        header.setVersion(VERSION_VALUE);
        header.setNumSentences(numSentences);

        const uint16_t numStrings = (wrongArrayLength) ? numSentences - 1 : numSentences;
        std::vector<std::string>& sentences = message.getSentences();
        sentences.reserve(numStrings);
        for (uint16_t i = 0; i < numStrings; ++i)
            sentences.push_back(getSentence(i));
    }

    std::string getSentence(uint16_t index)
    {
        return std::string("This is sentence #") + zserio::convertToString(index);
    }

    static const uint16_t   VERSION_VALUE = 0xAB;
};

TEST_F(AccessWithinTypeTest, read)
{
    const uint16_t numSentences = 10;
    const bool wrongArrayLength = false;
    zserio::BitStreamWriter writer;
    writeMessageToByteArray(writer, numSentences, wrongArrayLength);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);

    Message message(reader);
    checkMessage(message, numSentences);
}

TEST_F(AccessWithinTypeTest, readWrongArrayLength)
{
    const uint16_t numSentences = 10;
    const bool wrongArrayLength = true;
    zserio::BitStreamWriter writer;
    writeMessageToByteArray(writer, numSentences, wrongArrayLength);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);

    ASSERT_THROW(Message message(reader), zserio::CppRuntimeException);
}

TEST_F(AccessWithinTypeTest, write)
{
    const uint16_t numSentences = 13;
    const bool wrongArrayLength = false;
    Message message;
    fillMessage(message, numSentences, wrongArrayLength);

    zserio::BitStreamWriter writer;
    message.write(writer);

    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    Message readMessage(reader);
    checkMessage(readMessage, numSentences);
    ASSERT_TRUE(message == readMessage);
}

TEST_F(AccessWithinTypeTest, writeWrongArrayLength)
{
    const uint16_t numSentences = 13;
    const bool wrongArrayLength = true;
    Message message;
    fillMessage(message, numSentences, wrongArrayLength);

    zserio::BitStreamWriter writer;
    ASSERT_THROW(message.write(writer), zserio::CppRuntimeException);
}

} // namespace access_within_type
} // namespace member_access
