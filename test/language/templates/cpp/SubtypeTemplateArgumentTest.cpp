#include "gtest/gtest.h"
#include "templates/subtype_template_argument/SubtypeTemplateArgument.h"

namespace templates
{
namespace subtype_template_argument
{

TEST(SubtypeTemplateArgumentTest, bitSizeOf)
{
    const Field_uint32 field_uint32(10);
    const Field_Compound field_compound(Compound(10));
    const SubtypeTemplateArgument subtypeTemplateArgument(
            field_uint32, field_uint32, field_uint32, field_compound, field_compound, field_compound);
    ASSERT_EQ(192, subtypeTemplateArgument.bitSizeOf());
}

} // namespace subtype_template_argument
} // namespace templates
