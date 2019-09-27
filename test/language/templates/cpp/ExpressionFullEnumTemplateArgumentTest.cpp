#include "gtest/gtest.h"

#include "templates/expression_full_enum_template_argument/FullEnumTemplateArgumentHolder.h"

namespace templates
{
namespace expression_full_enum_template_argument
{

TEST(ExpressionFullEnumTemplateArgumentTest, readWrite)
{
    const FullEnumTemplateArgument_Color colorInternal(false, 10);
    const FullEnumTemplateArgument_templates_expression_full_enum_template_argument_color_Color colorExternal(
            false, 10);
    FullEnumTemplateArgumentHolder fullEnumTemplateArgumentHolder(colorInternal, colorExternal);
    ASSERT_TRUE(fullEnumTemplateArgumentHolder.getEnumTemplateArgumentInternal().hasExpressionField());
    ASSERT_FALSE(fullEnumTemplateArgumentHolder.getEnumTemplateArgumentExternal().hasExpressionField());

    zserio::BitStreamWriter writer;
    fullEnumTemplateArgumentHolder.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    const FullEnumTemplateArgumentHolder readFullEnumTemplateArgumentHolder(reader);

    ASSERT_TRUE(fullEnumTemplateArgumentHolder == readFullEnumTemplateArgumentHolder);
}

} // namespace expression_full_enum_template_argument
} // namespace templates
