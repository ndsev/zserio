#include "gtest/gtest.h"
#include "parameterized_types/bytes_param/BytesParam.h"
#include "zserio/SerializeUtil.h"

namespace parameterized_types
{
namespace bytes_param
{

using allocator_type = BytesParam::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

TEST(BytesParamTest, writeRead)
{
    BytesParam bytesParam(vector_type<uint8_t>{{0xCA, 0xFE}}, Parameterized(13));

    auto bitBuffer = zserio::serialize(bytesParam);
    auto readBytesParam = zserio::deserialize<BytesParam>(bitBuffer);

    ASSERT_EQ(bytesParam, readBytesParam);
}

} // namespace bytes_param
} // namespace parameterized_types
