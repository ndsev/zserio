#include "gtest/gtest.h"

#include "subtypes/template_argument_subtype/TemplateArgumentStructure.h"

namespace subtypes
{
namespace template_argument_subtype
{

TEST(TemplateArgumentSubtypeTest, testSubtype)
{
    Field_uint32 field_uint32;
    field_uint32.setValue(10);

    Compound compound;
    Field_Compound field_compound;
    field_compound.setValue(compound);

    TemplateArgumentStructure templateArgumentStructure;
    templateArgumentStructure.setAnotherUint32TypeField(field_uint32);
    templateArgumentStructure.setUint32TypeField(field_uint32);
    templateArgumentStructure.setUint32Field(field_uint32);
    templateArgumentStructure.setAnotherCompoundTypeField(field_compound);
    templateArgumentStructure.setCompoundTypeField(field_compound);
    templateArgumentStructure.setCompoundField(field_compound);
    ASSERT_EQ(192, templateArgumentStructure.bitSizeOf());
}

} // namespace template_argument_subtype
} // namespace subtypes
