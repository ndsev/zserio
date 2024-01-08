#include <array>
#include <vector>

#include "array_types/packed_auto_array_empty_compounds/PackedAutoArray.h"
#include "gtest/gtest.h"
#include "zserio/RebindAlloc.h"
#include "zserio/SerializeUtil.h"

namespace array_types
{
namespace packed_auto_array_empty_compounds
{

using allocator_type = PackedAutoArray::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class PackedAutoArrayEmptyCompoundsTest : public ::testing::Test
{
protected:
    static const std::string BLOB_NAME;
};

const std::string PackedAutoArrayEmptyCompoundsTest::BLOB_NAME =
        "language/array_types/packed_auto_array_empty_compounds.blob";

TEST_F(PackedAutoArrayEmptyCompoundsTest, writeReadFile)
{
    auto packedAutoArray = PackedAutoArray({{EmptyStruct(), EmptyStruct(), EmptyStruct()}},
            {{EmptyUnion(), EmptyUnion(), EmptyUnion()}}, {{EmptyChoice(), EmptyChoice(), EmptyChoice()}},
            {{Main(), Main(), Main()}});
    packedAutoArray.getMainArray()[0].setParam(0);
    packedAutoArray.getMainArray()[1].setParam(1);
    packedAutoArray.getMainArray()[2].setParam(2);

    zserio::serializeToFile(packedAutoArray, BLOB_NAME);

    auto readPackedAutoArray = zserio::deserializeFromFile<PackedAutoArray>(BLOB_NAME);
    ASSERT_EQ(packedAutoArray, readPackedAutoArray);
}

} // namespace packed_auto_array_empty_compounds
} // namespace array_types
