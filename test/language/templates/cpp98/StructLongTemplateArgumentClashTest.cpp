#include "gtest/gtest.h"

#include "templates/struct_long_template_argument_clash/StructLongTemplateArgumentClash.h"

namespace templates
{
namespace struct_long_template_argument_clash
{

TEST(StructLongTemplateArgumentClashTest, readWrite)
{
    ThisIsVeryVeryVeryLongNamedStructure field11;
    field11.setData("StringT");
    ThisIsVeryVeryVeryLongNamedStructure field12;
    field12.setData("StringU");
    ThisIsVeryVeryVeryLongNamedStructure field13;
    field13.setData("StringV");
    TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_Th_619A1B35 t1;
    t1.setField1(field11);
    t1.setField2(field12);
    t1.setField3(field13);

    ThisIsVeryVeryVeryLongNamedStructure field21;
    field21.setData("StringT");
    ThisIsVeryVeryVeryLongNamedStructure field22;
    field22.setData("StringU");
    ThisIsVeryVeryVeryLongNamedStructure_ field23;
    field23.setData(42);
    TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_Th_1B45EF08 t2;
    t2.setField1(field21);
    t2.setField2(field22);
    t2.setField3(field23);

    StructLongTemplateArgumentClash structLongTemplateArgumentClash;
    structLongTemplateArgumentClash.setStructNameOverflow(t1);
    structLongTemplateArgumentClash.setStructNameClash(t2);

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
