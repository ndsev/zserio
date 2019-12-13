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
            templated_struct::TemplatedStruct_Storage("String"));

    zserio::BitStreamWriter writer;
    structFullNameTemplateArgument.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    StructFullNameTemplateArgument readStructFullNameTemplateArgument(reader);

    ASSERT_TRUE(structFullNameTemplateArgument == readStructFullNameTemplateArgument);
}

TEST(StructFullAndShortTemplateArgumentTest, readWriteShort)
{
    templated_struct::StructShortNameTemplateArgument structShortNameTemplateArgument(
            templated_struct::TemplatedStruct_Storage("String"));

    zserio::BitStreamWriter writer;
    structShortNameTemplateArgument.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    templated_struct::StructShortNameTemplateArgument readStructShortNameTemplateArgument(reader);

    ASSERT_TRUE(structShortNameTemplateArgument == readStructShortNameTemplateArgument);
}

} // namespace struct_full_and_short_template_argument
} // namespace templates
