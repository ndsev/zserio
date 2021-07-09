#include "gtest/gtest.h"

#include "subtypes/subtype_imported/SubtypeImported.h"

namespace subtypes
{
namespace subtype_imported
{

TEST(SubtypeImportedTest, readWrite)
{
    SubtypeImported subtypeImported{pkg::SubTest{42}};

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    subtypeImported.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    SubtypeImported readSubtypeImported(reader);

    ASSERT_TRUE(subtypeImported == readSubtypeImported);
}

} // namespace subtype_imported
} // namespace subtypes
