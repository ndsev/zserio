#include "gtest/gtest.h"

#include "templates/struct_long_template_argument_clash/StructLongTemplateArgumentClash.h"

namespace templates
{
namespace struct_long_template_argument_clash
{

TEST(StructLongTemplateArgumentClashTest, readWrite)
{
    const TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_Th_619A1B35 t1(
            ThisIsVeryVeryVeryLongNamedStructure("StringT"),
            ThisIsVeryVeryVeryLongNamedStructure("StringU"),
            ThisIsVeryVeryVeryLongNamedStructure("StringV"));
    const TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_Th_1B45EF08 t2(
            ThisIsVeryVeryVeryLongNamedStructure("StringT"),
            ThisIsVeryVeryVeryLongNamedStructure("StringU"),
            ThisIsVeryVeryVeryLongNamedStructure_(42));
    StructLongTemplateArgumentClash structLongTemplateArgumentClash(t1, t2);

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    structLongTemplateArgumentClash.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    StructLongTemplateArgumentClash readStructLongTemplateArgumentClash(reader);

    ASSERT_TRUE(structLongTemplateArgumentClash == readStructLongTemplateArgumentClash);
}

} // namespace struct_long_template_argument_clash
} // namespace templates
