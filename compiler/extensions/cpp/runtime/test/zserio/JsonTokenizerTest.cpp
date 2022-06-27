#include "gtest/gtest.h"

#include <sstream>

#include "zserio/JsonTokenizer.h"

namespace zserio
{

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
