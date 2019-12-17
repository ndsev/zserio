#include "gtest/gtest.h"

#include "templates/struct_long_template_argument_clash/StructLongTemplateArgumentClash.h"

namespace templates
{
namespace struct_long_template_argument_clash
{

TEST(StructLongTemplateArgumentClashTest, readWrite)
{
    const TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_ThisIsVeryVeryVery_619A1B35 t1(
            ThisIsVeryVeryVeryLongNamedStructure("StringT"),
            ThisIsVeryVeryVeryLongNamedStructure("StringU"),
            ThisIsVeryVeryVeryLongNamedStructure("StringV"));
    const TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_ThisIsVeryVeryVery_1B45EF08 t2(
            ThisIsVeryVeryVeryLongNamedStructure("StringT"),
            ThisIsVeryVeryVeryLongNamedStructure("StringU"),
            ThisIsVeryVeryVeryLongNamedStructure_(42));
    StructLongTemplateArgumentClash structLongTemplateArgumentClash(t1, t2);

    zserio::BitStreamWriter writer;
    structLongTemplateArgumentClash.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    StructLongTemplateArgumentClash readStructLongTemplateArgumentClash(reader);

    ASSERT_TRUE(structLongTemplateArgumentClash == readStructLongTemplateArgumentClash);
}

} // namespace struct_long_template_argument_clash
} // namespace templates
