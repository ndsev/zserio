#include "gtest/gtest.h"

#include "templates/expression_full_template_argument/FullTemplateArgumentHolder.h"

namespace templates
{
namespace expression_full_template_argument
{

TEST(ExpressionFullTemplateArgumentTest, readWrite)
{
    const FullTemplateArgument_Color_7C6F461F colorInternal(false, 10);
    ASSERT_TRUE(colorInternal.isExpressionFieldUsed());
    const FullTemplateArgument_Color_F30EBCB3 colorExternal(false, 10);
    ASSERT_FALSE(colorExternal.isExpressionFieldUsed());

    FullTemplateArgumentHolder fullTemplateArgumentHolder(colorInternal, colorExternal);
    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    fullTemplateArgumentHolder.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    const FullTemplateArgumentHolder readFullTemplateArgumentHolder(reader);

    ASSERT_TRUE(fullTemplateArgumentHolder == readFullTemplateArgumentHolder);
}

} // namespace expression_full_template_argument
} // namespace templates
