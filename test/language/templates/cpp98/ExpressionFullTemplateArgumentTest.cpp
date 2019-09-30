#include "gtest/gtest.h"

#include "templates/expression_full_template_argument/FullTemplateArgumentHolder.h"

namespace templates
{
namespace expression_full_template_argument
{

TEST(ExpressionFullTemplateArgumentTest, readWrite)
{
    FullTemplateArgument_Color colorInternal;
    colorInternal.setBoolField(false);
    colorInternal.setExpressionField(10);

    FullTemplateArgument_templates_expression_full_template_argument_color_Color colorExternal;
    colorExternal.setBoolField(false);
    colorExternal.setExpressionField(10);

    FullTemplateArgumentHolder fullTemplateArgumentHolder;
    fullTemplateArgumentHolder.setTemplateArgumentInternal(colorInternal);
    fullTemplateArgumentHolder.setTemplateArgumentExternal(colorExternal);
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
