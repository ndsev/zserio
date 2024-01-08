#include "array_types_warning/packed_array_template_has_no_packable_field/T_packable.h"
#include "array_types_warning/packed_array_template_has_no_packable_field/T_str.h"
#include "array_types_warning/packed_array_template_has_no_packable_field/T_u32.h"
#include "array_types_warning/packed_array_template_has_no_packable_field/T_unpackable.h"
#include "gtest/gtest.h"
#include "zserio/SerializeUtil.h"

namespace array_types_warning
{
namespace packed_array_template_has_no_packable_field
{
using allocator_type = T_u32::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;
using string_type = zserio::string<allocator_type>;

class PackedArrayTemplateHasNoPackableFieldTest : public ::testing::Test
{
protected:
    static const std::string BLOB_NAME_BASE;
};

const std::string PackedArrayTemplateHasNoPackableFieldTest::BLOB_NAME_BASE =
        "warnings/array_types_warning/packed_array_template_has_no_packable_field";

TEST_F(PackedArrayTemplateHasNoPackableFieldTest, writeReadU32)
{
    T_u32 u32(vector_type<uint32_t>{{0, 1, 2, 3, 4, 5}});

    const std::string blobName = BLOB_NAME_BASE + "_u32.blob";
    zserio::serializeToFile(u32, blobName);
    auto readU32 = zserio::deserializeFromFile<T_u32>(blobName);
    ASSERT_EQ(u32, readU32);
}

TEST_F(PackedArrayTemplateHasNoPackableFieldTest, writeReadStr)
{
    T_str str(vector_type<string_type>{{"A", "B", "C", "D", "E", "F"}});

    const std::string blobName = BLOB_NAME_BASE + "_str.blob";
    zserio::serializeToFile(str, blobName);
    auto readStr = zserio::deserializeFromFile<T_str>(blobName);
    ASSERT_EQ(str, readStr);
}

TEST_F(PackedArrayTemplateHasNoPackableFieldTest, writeReadPackable)
{
    T_packable packable(vector_type<Packable>{{
            Packable(0, 4.0, "A"),
            Packable(1, 1.0, "B"),
            Packable(2, 0.0, "C"),
    }});

    const std::string blobName = BLOB_NAME_BASE + "_packable.blob";
    zserio::serializeToFile(packable, blobName);
    auto readPackable = zserio::deserializeFromFile<T_packable>(blobName);
    ASSERT_EQ(packable, readPackable);
}

TEST_F(PackedArrayTemplateHasNoPackableFieldTest, writeReadUnpackable)
{
    T_unpackable unpackable(vector_type<Unpackable>{{
            Unpackable(4.0, "A"),
            Unpackable(1.0, "B"),
            Unpackable(0.0, "C"),
    }});

    const std::string blobName = BLOB_NAME_BASE + "_unpackable.blob";
    zserio::serializeToFile(unpackable, blobName);
    auto readUnpackable = zserio::deserializeFromFile<T_unpackable>(blobName);
    ASSERT_EQ(unpackable, readUnpackable);
}

} // namespace packed_array_template_has_no_packable_field
} // namespace array_types_warning
