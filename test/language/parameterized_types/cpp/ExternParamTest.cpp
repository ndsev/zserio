#include "gtest/gtest.h"
#include "parameterized_types/extern_param/ExternParam.h"
#include "zserio/SerializeUtil.h"

namespace parameterized_types
{
namespace extern_param
{

using allocator_type = ExternParam::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;
using BitBuffer = zserio::BasicBitBuffer<allocator_type>;

TEST(ExternParamTest, writeRead)
{
    ExternParam bytesParam(BitBuffer(vector_type<uint8_t>{{0xCA, 0xFE}}, 15), Parameterized(13));

    auto bitBuffer = zserio::serialize(bytesParam);
    auto readExternParam = zserio::deserialize<ExternParam>(bitBuffer);

    ASSERT_EQ(bytesParam, readExternParam);
}

} // namespace extern_param
} // namespace parameterized_types
