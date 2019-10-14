#include "gtest/gtest.h"

#include "templates/expression_const_after_nested_template_argument/ConstAfterNested.h"

namespace templates
{
namespace expression_const_after_nested_template_argument
{

TEST(ExpressionConstAfterNestedTemplateArgumentTest, readWrite)
{

    ConstAfterNested constAfterNested;
    zserio::ObjectArray<Element_uint32>& array = constAfterNested.getCompound().getArray();
    array.resize(3);
    array[0].setValue(1);
    array[1].setValue(2);
    array[2].setValue(3);

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
