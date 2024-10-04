#include "gtest/gtest.h"
#include "templates/subtype_template_with_compound/SubtypeTemplateWithCompound.h"

namespace templates
{
namespace subtype_template_with_compound
{

TEST(SubtypeTemplateWithCompoundTest, readWrite)
{
    SubtypeTemplateWithCompound subtypeTemplateWithCompound;
    subtypeTemplateWithCompound.setValue1(Compound{13});
    subtypeTemplateWithCompound.setValue2(TemplateCompound_Compound{Compound{42}});

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    subtypeTemplateWithCompound.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    SubtypeTemplateWithCompound readSubtypeTemplateWithCompound(reader);

    ASSERT_TRUE(subtypeTemplateWithCompound == readSubtypeTemplateWithCompound);
}

} // namespace subtype_template_with_compound
} // namespace templates
