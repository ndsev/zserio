#include "expressions/string_type/CHOOSER.h"
#include "expressions/string_type/STRING_CONSTANT.h"
#include "expressions/string_type/StringTypeExpression.h"
#include "gtest/gtest.h"
#include "zserio/RebindAlloc.h"

using namespace zserio::literals;

namespace expressions
{
namespace string_type
{

using allocator_type = StringTypeExpression::allocator_type;
using string_type = zserio::string<allocator_type>;

class StringTypeTest : public ::testing::Test
{
protected:
    static StringTypeExpression createStringTypeExpression(bool hasValue)
    {
        StringTypeExpression stringTypeExpression;
        stringTypeExpression.setHasValue(hasValue);
        if (hasValue)
            stringTypeExpression.setValue(zserio::toString<allocator_type>(VALUE));
        return stringTypeExpression;
    }

    static const zserio::StringView VALUE;
    static const zserio::StringView APPEND;
    static const zserio::StringView IX;
    static const zserio::StringView LITERAL;
    static const zserio::StringView EXPRESSION;
    static const zserio::StringView FALSE;
    static const zserio::StringView CHOSEN;
    static const zserio::StringView SPACE;
    static const zserio::StringView UNDERSCORE;
};

const zserio::StringView StringTypeTest::VALUE = "value"_sv;
const zserio::StringView StringTypeTest::APPEND = "append"_sv;
const zserio::StringView StringTypeTest::IX = "ix"_sv;
const zserio::StringView StringTypeTest::LITERAL = "literal"_sv;
const zserio::StringView StringTypeTest::EXPRESSION = "expression"_sv;
const zserio::StringView StringTypeTest::FALSE = "false"_sv;
const zserio::StringView StringTypeTest::CHOSEN = "chosen"_sv;
const zserio::StringView StringTypeTest::SPACE = " "_sv;
const zserio::StringView StringTypeTest::UNDERSCORE = "_"_sv;

TEST_F(StringTypeTest, returnValue)
{
    const StringTypeExpression stringTypeExpression = createStringTypeExpression(true);
    ASSERT_EQ(VALUE, stringTypeExpression.funcReturnValue());
}

TEST_F(StringTypeTest, returnDefaultValue)
{
    const StringTypeExpression stringTypeExpression = createStringTypeExpression(true);
    ASSERT_EQ(CHOOSER ? zserio::toString(STRING_CONSTANT)
                      : zserio::toString(FALSE) + zserio::toString(SPACE) + zserio::toString(STRING_CONSTANT),
            zserio::toString(stringTypeExpression.funcReturnDefaultValue()));
}

TEST_F(StringTypeTest, returnDefaultChosen)
{
    const StringTypeExpression stringTypeExpression = createStringTypeExpression(true);
    ASSERT_EQ(CHOOSER ? zserio::toString(CHOSEN) + zserio::toString(SPACE) + zserio::toString(STRING_CONSTANT)
                      : std::string(),
            zserio::toString(stringTypeExpression.funcReturnDefaultChosen()));
}

TEST_F(StringTypeTest, appendix)
{
    const StringTypeExpression stringTypeExpression = createStringTypeExpression(false);
    ASSERT_EQ(zserio::toString(APPEND) + zserio::toString(IX),
            zserio::toString(stringTypeExpression.funcAppendix()));
}

TEST_F(StringTypeTest, appendToConst)
{
    const StringTypeExpression stringTypeExpression = createStringTypeExpression(false);
    ASSERT_EQ(zserio::toString(STRING_CONSTANT) + zserio::toString(UNDERSCORE) + zserio::toString(APPEND) +
                    zserio::toString(IX),
            zserio::toString(stringTypeExpression.funcAppendToConst()));
}

TEST_F(StringTypeTest, valueOrLiteral)
{
    StringTypeExpression stringTypeExpression1 = createStringTypeExpression(true);
    ASSERT_EQ(VALUE, stringTypeExpression1.funcValueOrLiteral());
    StringTypeExpression stringTypeExpression2 = createStringTypeExpression(false);
    ASSERT_EQ(LITERAL, stringTypeExpression2.funcValueOrLiteral());
}

TEST_F(StringTypeTest, valueOrLiteralExpression)
{
    StringTypeExpression stringTypeExpression1 = createStringTypeExpression(true);
    ASSERT_EQ(VALUE, stringTypeExpression1.funcValueOrLiteralExpression());
    StringTypeExpression stringTypeExpression2 = createStringTypeExpression(false);
    ASSERT_EQ(zserio::toString(LITERAL) + zserio::toString(SPACE) + zserio::toString(EXPRESSION),
            zserio::toString(stringTypeExpression2.funcValueOrLiteralExpression()));
}

TEST_F(StringTypeTest, valueOrConst)
{
    StringTypeExpression stringTypeExpression1 = createStringTypeExpression(true);
    ASSERT_EQ(VALUE, stringTypeExpression1.funcValueOrConst());
    StringTypeExpression stringTypeExpression2 = createStringTypeExpression(false);
    ASSERT_EQ(STRING_CONSTANT, stringTypeExpression2.funcValueOrConst());
}

TEST_F(StringTypeTest, valueOrConstExpression)
{
    StringTypeExpression stringTypeExpression1 = createStringTypeExpression(true);
    ASSERT_EQ(VALUE, stringTypeExpression1.funcValueOrConstExpression());
    StringTypeExpression stringTypeExpression2 = createStringTypeExpression(false);
    ASSERT_EQ(zserio::toString(STRING_CONSTANT) + zserio::toString(SPACE) + zserio::toString(EXPRESSION),
            zserio::toString(stringTypeExpression2.funcValueOrConstExpression()));
}

} // namespace string_type
} // namespace expressions
