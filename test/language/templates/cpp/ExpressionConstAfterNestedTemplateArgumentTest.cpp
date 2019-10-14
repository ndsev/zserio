#include "gtest/gtest.h"

#include "templates/expression_const_after_nested_template_argument/ConstAfterNested.h"

namespace templates
{
namespace expression_const_after_nested_template_argument
{

TEST(ExpressionConstAfterNestedTemplateArgumentTest, readWrite)
{

    ConstAfterNested constAfterNested;
    constAfterNested.setCompound(Compound_Element_uint32_SIZE{
        std::vector<Element_uint32>{Element_uint32{1}, Element_uint32{2}, Element_uint32{3}}
    });

    zserio::BitStreamWriter writer;
    constAfterNested.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    const ConstAfterNested readConstAfterNested(reader);

    ASSERT_TRUE(constAfterNested == readConstAfterNested);
}

} // namespace expression_const_after_nested_template_argument
} // namespace templates
