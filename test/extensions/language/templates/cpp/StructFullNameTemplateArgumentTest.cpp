#include "gtest/gtest.h"
#include "templates/struct_full_name_template_argument/StructFullNameTemplateArgument.h"
#include "zserio/RebindAlloc.h"

namespace templates
{
namespace struct_full_name_template_argument
{

using allocator_type = StructFullNameTemplateArgument::allocator_type;
using string_type = zserio::string<allocator_type>;

TEST(StructFullNameTemplateArgumentTest, readWrite)
{
    StructFullNameTemplateArgument structFullNameTemplateArgument;
    structFullNameTemplateArgument.setStructExternal(
            TemplatedStruct_Storage_C76E422F{import_storage::Storage{42}});
    structFullNameTemplateArgument.setStructInternal(
            TemplatedStruct_Storage_A3A4B101{Storage{string_type{"string"}}});

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    structFullNameTemplateArgument.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    StructFullNameTemplateArgument readStructFullNameTemplateArgument(reader);

    ASSERT_TRUE(structFullNameTemplateArgument == readStructFullNameTemplateArgument);
}

} // namespace struct_full_name_template_argument
} // namespace templates
