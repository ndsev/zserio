#include "gtest/gtest.h"
#include "structure_types/structure_with_removed_enum_item_field/StructureWithRemovedEnumItemField.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/CppRuntimeException.h"
#include "zserio/DebugStringUtil.h"
#include "zserio/SerializeUtil.h"

namespace structure_types
{
namespace structure_with_removed_enum_item_field
{

using allocator_type = StructureWithRemovedEnumItemField::allocator_type;
using string_type = zserio::string<allocator_type>;

class StructureWithRemovedEnumItemFieldTest : public ::testing::Test
{
protected:
    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

TEST_F(StructureWithRemovedEnumItemFieldTest, emptyConstructor)
{
    StructureWithRemovedEnumItemField structureWithRemovedEnumItemField;
    ASSERT_EQ(Enumeration::ZSERIO_REMOVED_REMOVED, structureWithRemovedEnumItemField.getEnumeration());
}

TEST_F(StructureWithRemovedEnumItemFieldTest, fieldConstructor)
{
    StructureWithRemovedEnumItemField structureWithRemovedEnumItemField(Enumeration::ZSERIO_REMOVED_REMOVED);
    ASSERT_EQ(Enumeration::ZSERIO_REMOVED_REMOVED, structureWithRemovedEnumItemField.getEnumeration());
}

TEST_F(StructureWithRemovedEnumItemFieldTest, bitStreamReaderConstructor)
{
    zserio::BitBuffer bitBuffer(8);
    zserio::BitStreamWriter writer(bitBuffer);
    writer.writeBits(0, 8);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    StructureWithRemovedEnumItemField structureWithRemovedEnumItemField(reader);
    ASSERT_EQ(Enumeration::ZSERIO_REMOVED_REMOVED, structureWithRemovedEnumItemField.getEnumeration());
}

TEST_F(StructureWithRemovedEnumItemFieldTest, setter)
{
    StructureWithRemovedEnumItemField structureWithRemovedEnumItemField;
    structureWithRemovedEnumItemField.setEnumeration(Enumeration::ZSERIO_REMOVED_REMOVED);
    ASSERT_EQ(Enumeration::ZSERIO_REMOVED_REMOVED, structureWithRemovedEnumItemField.getEnumeration());
}

TEST_F(StructureWithRemovedEnumItemFieldTest, writeValid)
{
    StructureWithRemovedEnumItemField structureWithRemovedEnumItemField(Enumeration::VALID);
    ASSERT_NO_THROW(zserio::serialize(structureWithRemovedEnumItemField));
}

TEST_F(StructureWithRemovedEnumItemFieldTest, writeRemovedException)
{
    StructureWithRemovedEnumItemField structureWithRemovedEnumItemField(Enumeration::ZSERIO_REMOVED_REMOVED);
    ASSERT_THROW(zserio::serialize(structureWithRemovedEnumItemField), zserio::CppRuntimeException);
}

TEST_F(StructureWithRemovedEnumItemFieldTest, toJsonString)
{
    StructureWithRemovedEnumItemField structureWithRemovedEnumItemField(Enumeration::ZSERIO_REMOVED_REMOVED);
    string_type json = zserio::toJsonString(structureWithRemovedEnumItemField);
    ASSERT_EQ("{\n    \"enumeration\": \"REMOVED\"\n}", json);
}

TEST_F(StructureWithRemovedEnumItemFieldTest, fromJsonString)
{
    auto structureWithRemovedEnumItemField =
            zserio::fromJsonString<StructureWithRemovedEnumItemField>("{\n    \"enumeration\": \"REMOVED\"\n}");
    ASSERT_EQ(Enumeration::ZSERIO_REMOVED_REMOVED, structureWithRemovedEnumItemField.getEnumeration());
}

} // namespace structure_with_removed_enum_item_field
} // namespace structure_types
