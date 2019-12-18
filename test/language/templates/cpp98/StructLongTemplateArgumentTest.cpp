#include "gtest/gtest.h"

#include "templates/struct_long_template_argument/StructLongTemplateArgument.h"

namespace templates
{
namespace struct_long_template_argument
{

TEST(StructLongTemplateArgumentTest, readWrite)
{
    ThisIsVeryVeryVeryLongNamedStructure field1;
    field1.setData("StringT");
    ThisIsVeryVeryVeryLongNamedStructure field2;
    field2.setData("StringU");
    ThisIsVeryVeryVeryLongNamedStructure field3;
    field3.setData("StringV");
    TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_ThisIsVery_ templ;
    templ.setField1(field1);
    templ.setField1(field2);
    templ.setField1(field3);
    StructLongTemplateArgument structLongTemplateArgument;
    structLongTemplateArgument.setStructNameOverflow(templ);

    zserio::BitStreamWriter writer;
    structLongTemplateArgument.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    StructLongTemplateArgument readStructLongTemplateArgument(reader);

    ASSERT_TRUE(structLongTemplateArgument == readStructLongTemplateArgument);
}

} // namespace struct_long_template_argument
} // namespace templates
