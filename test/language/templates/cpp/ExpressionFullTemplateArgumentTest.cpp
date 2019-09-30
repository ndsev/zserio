#include "gtest/gtest.h"

#include "templates/expression_full_template_argument/FullTemplateArgumentHolder.h"

namespace templates
{
namespace expression_full_template_argument
{

TEST(ExpressionFullTemplateArgumentTest, readWrite)
{
    const FullTemplateArgument_Color colorInternal(false, 10);
    const FullTemplateArgument_templates_expression_full_template_argument_color_Color colorExternal(false, 10);
    FullTemplateArgumentHolder fullTemplateArgumentHolder(colorInternal, colorExternal);
    ASSERT_TRUE(fullTemplateArgumentHolder.getTemplateArgumentInternal().hasExpressionField());
    ASSERT_FALSE(fullTemplateArgumentHolder.getTemplateArgumentExternal().hasExpressionField());

    zserio::BitStreamWriter writer;
    fullTemplateArgumentHolder.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    const FullTemplateArgumentHolder readFullTemplateArgumentHolder(reader);

    ASSERT_TRUE(fullTemplateArgumentHolder == readFullTemplateArgumentHolder);
}

} // namespace expression_full_template_argument
} // namespace templates
