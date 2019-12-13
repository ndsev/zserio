#include "gtest/gtest.h"

#include "templates/struct_full_name_template_argument/StructFullNameTemplateArgument.h"

namespace templates
{
namespace struct_full_name_template_argument
{

TEST(StructFullNameTemplateArgumentTest, readWrite)
{
    StructFullNameTemplateArgument structFullNameTemplateArgument;
    structFullNameTemplateArgument.setStructExternal(
            TemplatedStruct_Storage_08C0ED6D{storage::Storage{42}});
    structFullNameTemplateArgument.setStructInternal(
            TemplatedStruct_Storage_A3A4B101{Storage{std::string{"string"}}});

    zserio::BitStreamWriter writer;
    structFullNameTemplateArgument.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    StructFullNameTemplateArgument readStructFullNameTemplateArgument(reader);

    ASSERT_TRUE(structFullNameTemplateArgument == readStructFullNameTemplateArgument);
}

} // namespace struct_full_name_template_argument
} // namespace templates
