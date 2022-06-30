#include "gtest/gtest.h"

#include <sstream>

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
    std::stringstream str("\n{\n   \"key\"  \n :\n10}");
    JsonTokenizer tokenizer(str, std::allocator<uint8_t>());

    ASSERT_EQ(JsonToken::BEGIN_OBJECT, tokenizer.next());
    ASSERT_EQ('{', tokenizer.getValue().get<char>());
    ASSERT_EQ(2, tokenizer.getLine());
    ASSERT_EQ(1, tokenizer.getColumn());

    ASSERT_EQ(JsonToken::VALUE, tokenizer.next());
    ASSERT_EQ("key", tokenizer.getValue().get<string<>>());
    ASSERT_EQ(3, tokenizer.getLine());
    ASSERT_EQ(4, tokenizer.getColumn());

    ASSERT_EQ(JsonToken::KEY_SEPARATOR, tokenizer.next());
    ASSERT_EQ(':', tokenizer.getValue().get<char>());
    ASSERT_EQ(4, tokenizer.getLine());
    ASSERT_EQ(2, tokenizer.getColumn());

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
        ASSERT_EQ(100000000, tokenizer.getValue().get<uint64_t>()) << "i=" << i;
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
    ASSERT_EQ(100000000, tokenizer.getValue().get<uint64_t>()) << "i=" << i;
    ASSERT_EQ(1 + i + 1, tokenizer.getLine()) << "i=" << i;
    ASSERT_EQ(10, tokenizer.getColumn()) << "i=" << i;

    ASSERT_EQ(JsonToken::END_OBJECT, tokenizer.next());
    ASSERT_EQ(1 + i + 2, tokenizer.getLine());
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
    ASSERT_THROW({
        try
        {
            tokenizer.next();
        }
        catch (const JsonParserException& e)
        {
            ASSERT_STREQ("JsonTokenizer:1:1: Unknown token!", e.what());
            throw;
        }
    }, JsonParserException);
}

TEST(JsonTokenizerTest, jsonTokenName)
{
    ASSERT_EQ(std::string("UNKNOWN"), jsonTokenName(JsonToken::UNKNOWN));
    ASSERT_EQ(std::string("BEGIN_OF_FILE"), jsonTokenName(JsonToken::BEGIN_OF_FILE));
    ASSERT_EQ(std::string("END_OF_FILE"), jsonTokenName(JsonToken::END_OF_FILE));
    ASSERT_EQ(std::string("BEGIN_OBJECT"), jsonTokenName(JsonToken::BEGIN_OBJECT));
    ASSERT_EQ(std::string("END_OBJECT"), jsonTokenName(JsonToken::END_OBJECT));
    ASSERT_EQ(std::string("BEGIN_ARRAY"), jsonTokenName(JsonToken::BEGIN_ARRAY));
    ASSERT_EQ(std::string("END_ARRAY"), jsonTokenName(JsonToken::END_ARRAY));
    ASSERT_EQ(std::string("KEY_SEPARATOR"), jsonTokenName(JsonToken::KEY_SEPARATOR));
    ASSERT_EQ(std::string("ITEM_SEPARATOR"), jsonTokenName(JsonToken::ITEM_SEPARATOR));
    ASSERT_EQ(std::string("VALUE"), jsonTokenName(JsonToken::VALUE));
}

} // namespace zserio
