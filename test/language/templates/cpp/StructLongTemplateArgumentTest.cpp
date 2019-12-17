#include "gtest/gtest.h"

#include "templates/struct_long_template_argument/StructLongTemplateArgument.h"

namespace templates
{
namespace struct_long_template_argument
{

TEST(StructLongTemplateArgumentTest, readWrite)
{
    const TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_ThisIsVeryVeryVeryLongName_ templ(
            ThisIsVeryVeryVeryLongNamedStructure("StringT"),
            ThisIsVeryVeryVeryLongNamedStructure("StringU"),
            ThisIsVeryVeryVeryLongNamedStructure("StringV"));
    StructLongTemplateArgument structLongTemplateArgument(templ);

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
