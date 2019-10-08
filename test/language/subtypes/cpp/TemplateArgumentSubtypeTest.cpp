#include "gtest/gtest.h"

#include "subtypes/template_argument_subtype/TemplateArgumentStructure.h"

namespace subtypes
{
namespace template_argument_subtype
{

TEST(TemplateArgumentSubtypeTest, testSubtype)
{
    const Field_uint32 field_uint32(10);
    const Field_Compound field_compound(Compound(10));
    const TemplateArgumentStructure templateArgumentStructure(field_uint32, field_uint32, field_uint32,
            field_compound, field_compound, field_compound);
    ASSERT_EQ(192, templateArgumentStructure.bitSizeOf());
}

} // namespace template_argument_subtype
} // namespace subtypes
