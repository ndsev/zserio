#include <string>

#include "gtest/gtest.h"

#include "member_access/access_within_type/Message.h"

#include "zserio/RebindAlloc.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/StringConvertUtil.h"

namespace member_access
{
namespace access_within_type
{

using allocator_type = Message::allocator_type;
using string_type = zserio::string<zserio::RebindAlloc<allocator_type, char>>;
template <typename T>
using vector_type = std::vector<T, zserio::RebindAlloc<allocator_type, T>>;

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

        const vector_type<string_type>& sentences = message.getSentences();
        ASSERT_EQ(numSentences, sentences.size());
        for (uint16_t i = 0; i < numSentences; ++i)
        {
            const string_type& expectedSentence = getSentence(i);
            ASSERT_EQ(expectedSentence, sentences[i]);
        }
    }

    void fillMessage(Message& message, uint16_t numSentences, bool wrongArrayLength)
    {
        Header& header = message.getHeader();
        header.setVersion(VERSION_VALUE);
        header.setNumSentences(numSentences);

        const uint16_t numStrings = (wrongArrayLength) ? numSentences - 1 : numSentences;
        vector_type<string_type>& sentences = message.getSentences();
        sentences.reserve(numStrings);
        for (uint16_t i = 0; i < numStrings; ++i)
            sentences.push_back(getSentence(i));
    }

    string_type getSentence(uint16_t index)
    {
        return string_type("This is sentence #") + zserio::toString<allocator_type>(index);
    }

    static const uint16_t   VERSION_VALUE = 0xAB;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

TEST_F(AccessWithinTypeTest, read)
{
    const uint16_t numSentences = 10;
    const bool wrongArrayLength = false;
    zserio::BitStreamWriter writer(bitBuffer);
    writeMessageToByteArray(writer, numSentences, wrongArrayLength);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    Message message(reader);
    checkMessage(message, numSentences);
}

TEST_F(AccessWithinTypeTest, readWrongArrayLength)
{
    const uint16_t numSentences = 10;
    const bool wrongArrayLength = true;
    zserio::BitStreamWriter writer(bitBuffer);
    writeMessageToByteArray(writer, numSentences, wrongArrayLength);
    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());

    ASSERT_THROW(Message message(reader), zserio::CppRuntimeException);
}

TEST_F(AccessWithinTypeTest, write)
{
    const uint16_t numSentences = 13;
    const bool wrongArrayLength = false;
    Message message;
    fillMessage(message, numSentences, wrongArrayLength);

    zserio::BitStreamWriter writer(bitBuffer);
    message.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
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

    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(message.write(writer), zserio::CppRuntimeException);
}

} // namespace access_within_type
} // namespace member_access
