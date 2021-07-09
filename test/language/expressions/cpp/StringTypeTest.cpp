#include "math.h"

#include "gtest/gtest.h"

#include "expressions/string_type/StringTypeExpression.h"
#include "expressions/string_type/STRING_CONSTANT.h"

#include "zserio/RebindAlloc.h"

using namespace zserio::literals;

namespace expressions
{
namespace string_type
{

using allocator_type = StringTypeExpression::allocator_type;
using string_type = zserio::string<zserio::RebindAlloc<allocator_type, char>>;

TEST(StringTypeTest, append)
{
    StringTypeExpression stringTypeExpression;
    const zserio::StringView value = "value"_sv;
    stringTypeExpression.setValue(zserio::stringViewToString(value, allocator_type()));
    ASSERT_EQ(value, stringTypeExpression.funcReturnValue());
    ASSERT_EQ("appendix"_sv, stringTypeExpression.funcAppendix());
    ASSERT_EQ("CONSTANT_appendix"_sv, stringTypeExpression.funcAppendToConst());
}

} // namespace string_type
} // namespace expressions
