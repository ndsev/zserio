#include "gtest/gtest.h"

#include "templates/expression_full_template_argument/FullTemplateArgumentHolder.h"

namespace templates
{
namespace expression_full_template_argument
{

TEST(ExpressionFullTemplateArgumentTest, readWrite)
{
    const FullTemplateArgument_Color colorInternal(false, 10);
    ASSERT_TRUE(colorInternal.hasExpressionField());
    const FullTemplateArgument_templates_expression_full_template_argument_color_Color colorExternal(false, 10);
    ASSERT_FALSE(colorExternal.hasExpressionField());

    FullTemplateArgumentHolder fullTemplateArgumentHolder(colorInternal, colorExternal);
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
