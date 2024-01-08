#include "gtest/gtest.h"
#include "templates/expression_enum_template_argument/EnumTemplateArgumentHolder.h"

namespace templates
{
namespace expression_enum_template_argument
{

TEST(ExpressionEnumTemplateArgumentTest, readWrite)
{
    const EnumTemplateArgument_Color enumTemplateArgument_Color(false, 10);
    ASSERT_TRUE(enumTemplateArgument_Color.isExpressionFieldUsed());

    EnumTemplateArgumentHolder enumTemplateArgumentHolder(enumTemplateArgument_Color);
    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    enumTemplateArgumentHolder.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    const EnumTemplateArgumentHolder readEnumTemplateArgumentHolder(reader);

    ASSERT_TRUE(enumTemplateArgumentHolder == readEnumTemplateArgumentHolder);
}

} // namespace expression_enum_template_argument
} // namespace templates
