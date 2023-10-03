#include "gtest/gtest.h"

#include "array_types/packed_auto_array_removed_enum_item/PackedAutoArrayRemovedEnumItem.h"

#include "zserio/SerializeUtil.h"

namespace array_types
{
namespace packed_auto_array_removed_enum_item
{

class PackedAutoArrayRemovedEnumItemTest : public ::testing::Test
{
protected:
    static const std::string BLOB_NAME;
};

const std::string PackedAutoArrayRemovedEnumItemTest::BLOB_NAME =
        "language/array_types/packed_auto_array_removed_enum_item.blob";

TEST_F(PackedAutoArrayRemovedEnumItemTest, writeReadFile)
{
    PackedAutoArrayRemovedEnumItem packedAutoArrayRemovedEnumItem;
    auto& packedArray = packedAutoArrayRemovedEnumItem.getPackedArray();
    packedArray.push_back(Traffic::NONE);
    packedArray.push_back(Traffic::LIGHT);
    packedArray.push_back(Traffic::MID);

    zserio::serializeToFile(packedAutoArrayRemovedEnumItem, BLOB_NAME);
    auto readPackedAutoArrayRemovedEnumItem =
            zserio::deserializeFromFile<PackedAutoArrayRemovedEnumItem>(BLOB_NAME);
    ASSERT_EQ(packedAutoArrayRemovedEnumItem, readPackedAutoArrayRemovedEnumItem);
}

TEST_F(PackedAutoArrayRemovedEnumItemTest, writeRemovedException)
{
    PackedAutoArrayRemovedEnumItem packedAutoArrayRemovedEnumItem;
    auto& packedArray = packedAutoArrayRemovedEnumItem.getPackedArray();
    packedArray.push_back(Traffic::NONE);
    packedArray.push_back(Traffic::LIGHT);
    packedArray.push_back(Traffic::MID);
    packedArray.push_back(Traffic::ZSERIO_REMOVED_HEAVY);

    ASSERT_THROW(zserio::serialize(packedAutoArrayRemovedEnumItem), zserio::CppRuntimeException);
}

} // namespace packed_auto_array_removed_enum_item
} // namespace array_types
