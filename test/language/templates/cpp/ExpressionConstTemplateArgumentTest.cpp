#include "gtest/gtest.h"

#include "templates/expression_const_template_argument/ConstTemplateArgumentHolder.h"

namespace templates
{
namespace expression_const_template_argument
{

TEST(ExpressionConstTemplateArgumentTest, readWrite)
{
    ConstTemplateArgument_LENGTH constTemplateArgument_LENGTH;
    constTemplateArgument_LENGTH.setOffsetsField(std::vector<uint32_t>(20));
    constTemplateArgument_LENGTH.setArrayField(std::vector<uint8_t>(10));
    // initializerField will be default
    constTemplateArgument_LENGTH.setOptionalField(1);
    constTemplateArgument_LENGTH.setConstraintField(10);
    constTemplateArgument_LENGTH.setBitField(3);
    ASSERT_EQ(10, constTemplateArgument_LENGTH.getInitializerField());
    ASSERT_TRUE(constTemplateArgument_LENGTH.hasOptionalField());
    ASSERT_TRUE(constTemplateArgument_LENGTH.funcCheck());

    ConstTemplateArgumentHolder constTemplateArgumentHolder(constTemplateArgument_LENGTH);
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
