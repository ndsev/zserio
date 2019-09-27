#include "gtest/gtest.h"

#include "templates/expression_enum_template_argument/EnumTemplateArgumentHolder.h"

namespace templates
{
namespace expression_enum_template_argument
{

TEST(ExpressionEnumTemplateArgumentTest, readWrite)
{
    EnumTemplateArgument_Color enumTemplateArgument_Color;
    enumTemplateArgument_Color.setBoolField(false);
    enumTemplateArgument_Color.setExpressionField(10);
    EnumTemplateArgumentHolder enumTemplateArgumentHolder;
    enumTemplateArgumentHolder.setEnumTemplateArgument(enumTemplateArgument_Color);
    ASSERT_TRUE(enumTemplateArgumentHolder.getEnumTemplateArgument().hasExpressionField());

    zserio::BitStreamWriter writer;
    enumTemplateArgumentHolder.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    const EnumTemplateArgumentHolder readEnumTemplateArgumentHolder(reader);

    ASSERT_TRUE(enumTemplateArgumentHolder == readEnumTemplateArgumentHolder);
}

} // namespace expression_enum_template_argument
} // namespace templates
