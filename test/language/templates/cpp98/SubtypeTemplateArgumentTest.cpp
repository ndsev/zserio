#include "gtest/gtest.h"

#include "templates/subtype_template_argument/SubtypeTemplateArgument.h"

namespace templates
{
namespace subtype_template_argument
{

TEST(SubtypeTemplateArgumentTest, bitSizeOf)
{
    Field_uint32 field_uint32;
    field_uint32.setValue(10);

    Compound compound;
    Field_Compound field_compound;
    field_compound.setValue(compound);

    SubtypeTemplateArgument subtypeTemplateArgument;
    subtypeTemplateArgument.setAnotherUint32TypeField(field_uint32);
    subtypeTemplateArgument.setUint32TypeField(field_uint32);
    subtypeTemplateArgument.setUint32Field(field_uint32);
    subtypeTemplateArgument.setAnotherCompoundTypeField(field_compound);
    subtypeTemplateArgument.setCompoundTypeField(field_compound);
    subtypeTemplateArgument.setCompoundField(field_compound);
    ASSERT_EQ(192, subtypeTemplateArgument.bitSizeOf());
}

} // namespace subtype_template_argument
} // namespace templates
