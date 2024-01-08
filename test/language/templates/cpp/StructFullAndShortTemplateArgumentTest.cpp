#include "gtest/gtest.h"
#include "templates/struct_full_and_short_template_argument/StructFullNameTemplateArgument.h"
#include "templates/struct_full_and_short_template_argument/templated_struct/StructShortNameTemplateArgument.h"

namespace templates
{
namespace struct_full_and_short_template_argument
{

TEST(StructFullAndShortTemplateArgumentTest, readWriteFull)
{
    StructFullNameTemplateArgument structFullNameTemplateArgument(
            templated_struct::TemplatedStruct_Storage(templated_struct::Storage("String")));

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    structFullNameTemplateArgument.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    StructFullNameTemplateArgument readStructFullNameTemplateArgument(reader);

    ASSERT_TRUE(structFullNameTemplateArgument == readStructFullNameTemplateArgument);
}

TEST(StructFullAndShortTemplateArgumentTest, readWriteShort)
{
    templated_struct::StructShortNameTemplateArgument structShortNameTemplateArgument(
            templated_struct::TemplatedStruct_Storage(templated_struct::Storage("String")));

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    structShortNameTemplateArgument.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    templated_struct::StructShortNameTemplateArgument readStructShortNameTemplateArgument(reader);

    ASSERT_TRUE(structShortNameTemplateArgument == readStructShortNameTemplateArgument);
}

} // namespace struct_full_and_short_template_argument
} // namespace templates
