#include "gtest/gtest.h"

#include "templates/expression_full_enum_template_argument/FullEnumTemplateArgumentHolder.h"

namespace templates
{
namespace expression_full_enum_template_argument
{

TEST(ExpressionFullEnumTemplateArgumentTest, readWrite)
{
    FullEnumTemplateArgument_Color colorInternal;
    colorInternal.setBoolField(false);
    colorInternal.setExpressionField(10);

    FullEnumTemplateArgument_templates_expression_full_enum_template_argument_color_Color colorExternal;
    colorExternal.setBoolField(false);
    colorExternal.setExpressionField(10);

    FullEnumTemplateArgumentHolder fullEnumTemplateArgumentHolder;
    fullEnumTemplateArgumentHolder.setEnumTemplateArgumentInternal(colorInternal);
    fullEnumTemplateArgumentHolder.setEnumTemplateArgumentExternal(colorExternal);
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
