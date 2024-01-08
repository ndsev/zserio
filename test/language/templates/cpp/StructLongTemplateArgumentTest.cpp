#include "gtest/gtest.h"
#include "templates/struct_long_template_argument/StructLongTemplateArgument.h"

namespace templates
{
namespace struct_long_template_argument
{

TEST(StructLongTemplateArgumentTest, readWrite)
{
    const TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_ThisIsVery_ templ(
            ThisIsVeryVeryVeryLongNamedStructure("StringT"), ThisIsVeryVeryVeryLongNamedStructure("StringU"),
            ThisIsVeryVeryVeryLongNamedStructure("StringV"));
    StructLongTemplateArgument structLongTemplateArgument(templ);

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    structLongTemplateArgument.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    StructLongTemplateArgument readStructLongTemplateArgument(reader);

    ASSERT_TRUE(structLongTemplateArgument == readStructLongTemplateArgument);
}

} // namespace struct_long_template_argument
} // namespace templates
