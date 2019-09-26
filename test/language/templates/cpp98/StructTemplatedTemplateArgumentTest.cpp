#include "gtest/gtest.h"

#include "templates/struct_templated_template_argument/StructTemplatedTemplateArgument.h"

namespace templates
{
namespace struct_templated_template_argument
{

TEST(StructTemplatedTemplateArgumentTest, readWrite)
{
    StructTemplatedTemplateArgument structTemplatedTemplateArgument;
    structTemplatedTemplateArgument.getCompoundField().getValue().setValue(42);

    zserio::BitStreamWriter writer;
    structTemplatedTemplateArgument.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    StructTemplatedTemplateArgument readStructTemplatedTemplateArgument(reader);

    ASSERT_TRUE(structTemplatedTemplateArgument == readStructTemplatedTemplateArgument);
}

} // namespace struct_templated_template_argument
} // namespace templates
