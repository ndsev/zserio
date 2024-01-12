#include <sstream>

#include "gtest/gtest.h"
#include "zserio/JsonTokenizer.h"

namespace zserio
{

using JsonTokenizer = BasicJsonTokenizer<>;

TEST(JsonTokenizerTest, tokens)
{
    std::stringstream str("{\"array\":\n[\n{\"key\":\n10}]}");
    auto tokenizer = JsonTokenizer(str, std::allocator<uint8_t>());
    ASSERT_EQ(JsonToken::BEGIN_OBJECT, tokenizer.next());
    ASSERT_EQ('{', tokenizer.getValue().get<char>());
    ASSERT_EQ(JsonToken::VALUE, tokenizer.next());
    ASSERT_EQ("array", tokenizer.getValue().get<string<>>());
    ASSERT_EQ(JsonToken::KEY_SEPARATOR, tokenizer.next());
    ASSERT_EQ(':', tokenizer.getValue().get<char>());
    ASSERT_EQ(JsonToken::BEGIN_ARRAY, tokenizer.next());
    ASSERT_EQ('[', tokenizer.getValue().get<char>());
    ASSERT_EQ(JsonToken::BEGIN_OBJECT, tokenizer.next());
    ASSERT_EQ('{', tokenizer.getValue().get<char>());
    ASSERT_EQ(JsonToken::VALUE, tokenizer.next());
    ASSERT_EQ("key", tokenizer.getValue().get<string<>>());
    ASSERT_EQ(JsonToken::KEY_SEPARATOR, tokenizer.next());
    ASSERT_EQ(':', tokenizer.getValue().get<char>());
    ASSERT_EQ(JsonToken::VALUE, tokenizer.next());
    ASSERT_EQ(10, tokenizer.getValue().get<uint64_t>());
    ASSERT_EQ(JsonToken::END_OBJECT, tokenizer.next());
    ASSERT_EQ('}', tokenizer.getValue().get<char>());
    ASSERT_EQ(JsonToken::END_ARRAY, tokenizer.next());
    ASSERT_EQ(']', tokenizer.getValue().get<char>());
    ASSERT_EQ(JsonToken::END_OBJECT, tokenizer.next());
    ASSERT_EQ('}', tokenizer.getValue().get<char>());
    ASSERT_EQ(JsonToken::END_OF_FILE, tokenizer.next());
}

TEST(JsonTokenizerTest, lineColumn)
{
    std::stringstream str("\n\t{\r   \"key\"  \r\n\t :\n10}\r");
    JsonTokenizer tokenizer(str, std::allocator<uint8_t>());

    ASSERT_EQ(JsonToken::BEGIN_OBJECT, tokenizer.next());
    ASSERT_EQ('{', tokenizer.getValue().get<char>());
    ASSERT_EQ(2, tokenizer.getLine());
    ASSERT_EQ(2, tokenizer.getColumn());

    ASSERT_EQ(JsonToken::VALUE, tokenizer.next());
    ASSERT_EQ("key", tokenizer.getValue().get<string<>>());
    ASSERT_EQ(3, tokenizer.getLine());
    ASSERT_EQ(4, tokenizer.getColumn());

    ASSERT_EQ(JsonToken::KEY_SEPARATOR, tokenizer.next());
    ASSERT_EQ(':', tokenizer.getValue().get<char>());
    ASSERT_EQ(4, tokenizer.getLine());
    ASSERT_EQ(3, tokenizer.getColumn());

    ASSERT_EQ(JsonToken::VALUE, tokenizer.next());
    ASSERT_EQ(10, tokenizer.getValue().get<uint64_t>());
    ASSERT_EQ(5, tokenizer.getLine());
    ASSERT_EQ(1, tokenizer.getColumn());

    ASSERT_EQ(JsonToken::END_OBJECT, tokenizer.next());
    ASSERT_EQ('}', tokenizer.getValue().get<char>());
    ASSERT_EQ(5, tokenizer.getLine());
    ASSERT_EQ(3, tokenizer.getColumn());

    ASSERT_EQ(JsonToken::END_OF_FILE, tokenizer.next());
    ASSERT_FALSE(tokenizer.getValue().hasValue());
    ASSERT_EQ(5, tokenizer.getLine());
    ASSERT_EQ(4, tokenizer.getColumn());
}

TEST(JsonTokenizerTest, longInputSplitInNumber)
{
    std::stringstream str;
    str << "{\n"; // 2 chars
    for (size_t i = 0; i < 4000; ++i) // 20 x 4000 > 65534 to check reading by chunks
    {
        // BUFFER_SIZE is 65536, thus 65534 % 20 gives position within the string below
        // where the buffer will be split => 14, which is somewhere in the middle of the number
        //     |->            <-|
        str << "  \"key\": 100000000,\n"; // 20 chars
    }
    str << "  \"key\": 100000000\n";
    str << '}';

    JsonTokenizer tokenizer(str, std::allocator<uint8_t>());

    ASSERT_EQ(JsonToken::BEGIN_OBJECT, tokenizer.next());
    ASSERT_EQ('{', tokenizer.getValue().get<char>());
    ASSERT_EQ(1, tokenizer.getLine());
    ASSERT_EQ(1, tokenizer.getColumn());

    size_t number = 0;
    for (; number < 4000; ++number)
    {
        ASSERT_EQ(JsonToken::VALUE, tokenizer.next()) << "i=" << number;
        ASSERT_EQ("key", tokenizer.getValue().get<string<>>()) << "i=" << number;
        ASSERT_EQ(1 + number + 1, tokenizer.getLine()) << "i=" << number;
        ASSERT_EQ(3, tokenizer.getColumn()) << "i=" << number;

        ASSERT_EQ(JsonToken::KEY_SEPARATOR, tokenizer.next()) << "i=" << number;
        ASSERT_EQ(':', tokenizer.getValue().get<char>()) << "i=" << number;
        ASSERT_EQ(1 + number + 1, tokenizer.getLine()) << "i=" << number;
        ASSERT_EQ(8, tokenizer.getColumn()) << "i=" << number;

        ASSERT_EQ(JsonToken::VALUE, tokenizer.next()) << "i=" << number;
        ASSERT_EQ(100000000, tokenizer.getValue().get<uint64_t>()) << "i=" << number;
        ASSERT_EQ(1 + number + 1, tokenizer.getLine()) << "i=" << number;
        ASSERT_EQ(10, tokenizer.getColumn()) << "i=" << number;

        ASSERT_EQ(JsonToken::ITEM_SEPARATOR, tokenizer.next()) << "i=" << number;
        ASSERT_EQ(',', tokenizer.getValue().get<char>()) << "i=" << number;
        ASSERT_EQ(1 + number + 1, tokenizer.getLine()) << "i=" << number;
        ASSERT_EQ(19, tokenizer.getColumn()) << "i=" << number;
    }

    ASSERT_EQ(JsonToken::VALUE, tokenizer.next()) << "i=" << number;
    ASSERT_EQ("key", tokenizer.getValue().get<string<>>()) << "i=" << number;
    ASSERT_EQ(1 + number + 1, tokenizer.getLine()) << "i=" << number;
    ASSERT_EQ(3, tokenizer.getColumn()) << "i=" << number;

    ASSERT_EQ(JsonToken::KEY_SEPARATOR, tokenizer.next()) << "i=" << number;
    ASSERT_EQ(':', tokenizer.getValue().get<char>()) << "i=" << number;
    ASSERT_EQ(1 + number + 1, tokenizer.getLine()) << "i=" << number;
    ASSERT_EQ(8, tokenizer.getColumn()) << "i=" << number;

    ASSERT_EQ(JsonToken::VALUE, tokenizer.next()) << "i=" << number;
    ASSERT_EQ(100000000, tokenizer.getValue().get<uint64_t>()) << "i=" << number;
    ASSERT_EQ(1 + number + 1, tokenizer.getLine()) << "i=" << number;
    ASSERT_EQ(10, tokenizer.getColumn()) << "i=" << number;

    ASSERT_EQ(JsonToken::END_OBJECT, tokenizer.next());
    ASSERT_EQ(1 + number + 2, tokenizer.getLine());
    ASSERT_EQ(1, tokenizer.getColumn());
}

TEST(JsonTokenizerTest, longInputSplitInString)
{
    std::stringstream str;
    str << "{\n"; // 2 chars
    for (size_t i = 0; i < 4000; ++i) // 20 x 4000 > 65534 to check reading by chunks
    {
        // BUFFER_SIZE is 65536, thus 65534 % 20 gives position within the string below
        // where the buffer will be split => 14, which is somewhere in the middle of the number
        //     |->             <-|
        str << "  \"key\": \"1000000\",\n"; // 20 chars
    }
    str << "  \"key\": \"1000000\"\n";
    str << '}';

    JsonTokenizer tokenizer(str, std::allocator<uint8_t>());

    ASSERT_EQ(JsonToken::BEGIN_OBJECT, tokenizer.next());
    ASSERT_EQ('{', tokenizer.getValue().get<char>());
    ASSERT_EQ(1, tokenizer.getLine());
    ASSERT_EQ(1, tokenizer.getColumn());

    size_t i = 0;
    for (; i < 4000; ++i)
    {
        ASSERT_EQ(JsonToken::VALUE, tokenizer.next()) << "i=" << i;
        ASSERT_EQ("key", tokenizer.getValue().get<string<>>()) << "i=" << i;
        ASSERT_EQ(1 + i + 1, tokenizer.getLine()) << "i=" << i;
        ASSERT_EQ(3, tokenizer.getColumn()) << "i=" << i;

        ASSERT_EQ(JsonToken::KEY_SEPARATOR, tokenizer.next()) << "i=" << i;
        ASSERT_EQ(':', tokenizer.getValue().get<char>()) << "i=" << i;
        ASSERT_EQ(1 + i + 1, tokenizer.getLine()) << "i=" << i;
        ASSERT_EQ(8, tokenizer.getColumn()) << "i=" << i;

        ASSERT_EQ(JsonToken::VALUE, tokenizer.next()) << "i=" << i;
        ASSERT_EQ("1000000", tokenizer.getValue().get<string<>>()) << "i=" << i;
        ASSERT_EQ(1 + i + 1, tokenizer.getLine()) << "i=" << i;
        ASSERT_EQ(10, tokenizer.getColumn()) << "i=" << i;

        ASSERT_EQ(JsonToken::ITEM_SEPARATOR, tokenizer.next()) << "i=" << i;
        ASSERT_EQ(',', tokenizer.getValue().get<char>()) << "i=" << i;
        ASSERT_EQ(1 + i + 1, tokenizer.getLine()) << "i=" << i;
        ASSERT_EQ(19, tokenizer.getColumn()) << "i=" << i;
    }

    ASSERT_EQ(JsonToken::VALUE, tokenizer.next()) << "i=" << i;
    ASSERT_EQ("key", tokenizer.getValue().get<string<>>()) << "i=" << i;
    ASSERT_EQ(1 + i + 1, tokenizer.getLine()) << "i=" << i;
    ASSERT_EQ(3, tokenizer.getColumn()) << "i=" << i;

    ASSERT_EQ(JsonToken::KEY_SEPARATOR, tokenizer.next()) << "i=" << i;
    ASSERT_EQ(':', tokenizer.getValue().get<char>()) << "i=" << i;
    ASSERT_EQ(1 + i + 1, tokenizer.getLine()) << "i=" << i;
    ASSERT_EQ(8, tokenizer.getColumn()) << "i=" << i;

    ASSERT_EQ(JsonToken::VALUE, tokenizer.next()) << "i=" << i;
    ASSERT_EQ("1000000", tokenizer.getValue().get<string<>>()) << "i=" << i;
    ASSERT_EQ(1 + i + 1, tokenizer.getLine()) << "i=" << i;
    ASSERT_EQ(10, tokenizer.getColumn()) << "i=" << i;

    ASSERT_EQ(JsonToken::END_OBJECT, tokenizer.next());
    ASSERT_EQ(1 + i + 2, tokenizer.getLine());
    ASSERT_EQ(1, tokenizer.getColumn()) << "i=" << i;
}

TEST(JsonTokenizerTest, longInputSplitInDoubleAfterE)
{
    std::stringstream str;
    str << "{\n"; // 2 chars
    for (size_t i = 0; i < 4000; ++i) // 20 x 4000 > 65534 to check reading by chunks
    {
        // BUFFER_SIZE is 65536, thus 65534 % 20 gives position within the string below
        // where the buffer will be split => 14, which is somewhere in the middle of the number
        //     |->            <-|
        str << "  \"key\":    1e5   ,\n"; // 20 chars
    }
    str << "  \"key\":    1e5  \n";
    str << '}';

    JsonTokenizer tokenizer(str, std::allocator<uint8_t>());

    ASSERT_EQ(JsonToken::BEGIN_OBJECT, tokenizer.next());
    ASSERT_EQ('{', tokenizer.getValue().get<char>());
    ASSERT_EQ(1, tokenizer.getLine());
    ASSERT_EQ(1, tokenizer.getColumn());

    size_t i = 0;
    for (; i < 4000; ++i)
    {
        ASSERT_EQ(JsonToken::VALUE, tokenizer.next()) << "i=" << i;
        ASSERT_EQ("key", tokenizer.getValue().get<string<>>()) << "i=" << i;
        ASSERT_EQ(1 + i + 1, tokenizer.getLine()) << "i=" << i;
        ASSERT_EQ(3, tokenizer.getColumn()) << "i=" << i;

        ASSERT_EQ(JsonToken::KEY_SEPARATOR, tokenizer.next()) << "i=" << i;
        ASSERT_EQ(':', tokenizer.getValue().get<char>()) << "i=" << i;
        ASSERT_EQ(1 + i + 1, tokenizer.getLine()) << "i=" << i;
        ASSERT_EQ(8, tokenizer.getColumn()) << "i=" << i;

        ASSERT_EQ(JsonToken::VALUE, tokenizer.next()) << "i=" << i;
        ASSERT_EQ(1e5, tokenizer.getValue().get<double>()) << "i=" << i;
        ASSERT_EQ(1 + i + 1, tokenizer.getLine()) << "i=" << i;
        ASSERT_EQ(13, tokenizer.getColumn()) << "i=" << i;

        ASSERT_EQ(JsonToken::ITEM_SEPARATOR, tokenizer.next()) << "i=" << i;
        ASSERT_EQ(',', tokenizer.getValue().get<char>()) << "i=" << i;
        ASSERT_EQ(1 + i + 1, tokenizer.getLine()) << "i=" << i;
        ASSERT_EQ(19, tokenizer.getColumn()) << "i=" << i;
    }

    ASSERT_EQ(JsonToken::VALUE, tokenizer.next()) << "i=" << i;
    ASSERT_EQ("key", tokenizer.getValue().get<string<>>()) << "i=" << i;
    ASSERT_EQ(1 + i + 1, tokenizer.getLine()) << "i=" << i;
    ASSERT_EQ(3, tokenizer.getColumn()) << "i=" << i;

    ASSERT_EQ(JsonToken::KEY_SEPARATOR, tokenizer.next()) << "i=" << i;
    ASSERT_EQ(':', tokenizer.getValue().get<char>()) << "i=" << i;
    ASSERT_EQ(1 + i + 1, tokenizer.getLine()) << "i=" << i;
    ASSERT_EQ(8, tokenizer.getColumn()) << "i=" << i;

    ASSERT_EQ(JsonToken::VALUE, tokenizer.next()) << "i=" << i;
    ASSERT_EQ(1e5, tokenizer.getValue().get<double>()) << "i=" << i;
    ASSERT_EQ(1 + i + 1, tokenizer.getLine()) << "i=" << i;
    ASSERT_EQ(13, tokenizer.getColumn()) << "i=" << i;

    ASSERT_EQ(JsonToken::END_OBJECT, tokenizer.next());
    ASSERT_EQ(1 + i + 2, tokenizer.getLine());
    ASSERT_EQ(1, tokenizer.getColumn()) << "i=" << i;
}

TEST(JsonTokenizerTest, unknownToken)
{
    std::stringstream str("\\\n");
    JsonTokenizer tokenizer(str, std::allocator<uint8_t>());
    ASSERT_THROW(
            {
                try
                {
                    tokenizer.next();
                }
                catch (const JsonParserException& e)
                {
                    ASSERT_STREQ("JsonTokenizer:1:1: Unknown token!", e.what());
                    throw;
                }
            },
            JsonParserException);
}

TEST(JsonTokenizerTest, cppRuntimeExceptionOperator)
{
    ASSERT_STREQ("UNKNOWN", (JsonParserException() << (JsonToken::UNKNOWN)).what());
    ASSERT_STREQ("BEGIN_OF_FILE", (JsonParserException() << (JsonToken::BEGIN_OF_FILE)).what());
    ASSERT_STREQ("END_OF_FILE", (JsonParserException() << (JsonToken::END_OF_FILE)).what());
    ASSERT_STREQ("BEGIN_OBJECT", (JsonParserException() << (JsonToken::BEGIN_OBJECT)).what());
    ASSERT_STREQ("END_OBJECT", (JsonParserException() << (JsonToken::END_OBJECT)).what());
    ASSERT_STREQ("BEGIN_ARRAY", (JsonParserException() << (JsonToken::BEGIN_ARRAY)).what());
    ASSERT_STREQ("END_ARRAY", (JsonParserException() << (JsonToken::END_ARRAY)).what());
    ASSERT_STREQ("KEY_SEPARATOR", (JsonParserException() << (JsonToken::KEY_SEPARATOR)).what());
    ASSERT_STREQ("ITEM_SEPARATOR", (JsonParserException() << (JsonToken::ITEM_SEPARATOR)).what());
    ASSERT_STREQ("VALUE", (JsonParserException() << (JsonToken::VALUE)).what());
}

} // namespace zserio
