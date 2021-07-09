#include "gtest/gtest.h"

#include "templates/struct_templated_template_argument/StructTemplatedTemplateArgument.h"

namespace templates
{
namespace struct_templated_template_argument
{

TEST(StructTemplatedTemplateArgumentTest, readWrite)
{
    StructTemplatedTemplateArgument structTemplatedTemplateArgument;
    structTemplatedTemplateArgument.setCompoundField(Field_Compound_uint32{Compound_uint32{42}});

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    structTemplatedTemplateArgument.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    StructTemplatedTemplateArgument readStructTemplatedTemplateArgument(reader);

    ASSERT_TRUE(structTemplatedTemplateArgument == readStructTemplatedTemplateArgument);
}

} // namespace struct_templated_template_argument
} // namespace templates
