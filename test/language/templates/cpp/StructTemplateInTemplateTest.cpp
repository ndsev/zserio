#include "gtest/gtest.h"

#include "templates/struct_template_in_template/StructTemplateInTemplate.h"

namespace templates
{
namespace struct_template_in_template
{

TEST(StructTemplateInTemplateTest, readWrite)
{
    StructTemplateInTemplate structTemplateInTemplate;
    structTemplateInTemplate.setUint32Field(Field_uint32{Compound_uint32{42}});
    structTemplateInTemplate.setStringField(Field_string{Compound_string{std::string{"string"}}});

    zserio::BitStreamWriter writer;
    structTemplateInTemplate.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    StructTemplateInTemplate readStructTemplateInTemplate(reader);

    ASSERT_TRUE(structTemplateInTemplate == readStructTemplateInTemplate);
}

} // namespace struct_template_in_template
} // namespace templates
