#include "gtest/gtest.h"

#include "templates/struct_template_in_template/StructTemplateInTemplate.h"

#include "zserio/RebindAlloc.h"

namespace templates
{
namespace struct_template_in_template
{

using allocator_type = StructTemplateInTemplate::allocator_type;
using string_type = zserio::string<allocator_type>;

TEST(StructTemplateInTemplateTest, readWrite)
{
    StructTemplateInTemplate structTemplateInTemplate;
    structTemplateInTemplate.setUint32Field(Field_uint32{Compound_uint32{42}});
    structTemplateInTemplate.setStringField(Field_string{Compound_string{string_type{"string"}}});

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    structTemplateInTemplate.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    StructTemplateInTemplate readStructTemplateInTemplate(reader);

    ASSERT_TRUE(structTemplateInTemplate == readStructTemplateInTemplate);
}

} // namespace struct_template_in_template
} // namespace templates
