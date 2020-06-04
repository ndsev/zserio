#include "math.h"

#include "gtest/gtest.h"

#include "expressions/string_type/StringTypeExpression.h"
#include "expressions/string_type/STRING_CONSTANT.h"

namespace expressions
{
namespace string_type
{

TEST(StringTypeTest, append)
{
    StringTypeExpression stringTypeExpression;
    const std::string value = "value";
    stringTypeExpression.setValue(value);
    ASSERT_EQ(value, stringTypeExpression.funcReturnValue());
    ASSERT_EQ(std::string("appendix"), stringTypeExpression.funcAppendix());
    ASSERT_EQ(STRING_CONSTANT + std::string("_appendix"), stringTypeExpression.funcAppendToConst());
}

} // namespace string_type
} // namespace expressions
