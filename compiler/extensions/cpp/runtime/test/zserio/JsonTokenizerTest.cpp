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

} // namespace zserio
