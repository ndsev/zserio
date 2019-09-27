#include "gtest/gtest.h"

#include "templates/expression_const_template_argument/ConstTemplateArgumentHolder.h"

namespace templates
{
namespace expression_const_template_argument
{

TEST(ExpressionConstTemplateArgumentTest, readWrite)
{
    const ConstTemplateArgument_LENGTH constTemplateArgument_LENGTH(std::vector<uint8_t>(10), 10);
    ConstTemplateArgumentHolder constTemplateArgumentHolder(constTemplateArgument_LENGTH);
    ASSERT_TRUE(constTemplateArgumentHolder.getConstTemplateArgument().hasExtraField());

    zserio::BitStreamWriter writer;
    constTemplateArgumentHolder.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    const ConstTemplateArgumentHolder readConstTemplateArgumentHolder(reader);

    ASSERT_TRUE(constTemplateArgumentHolder == readConstTemplateArgumentHolder);
}

} // namespace expression_const_template_argument
} // namespace templates
