#include "gtest/gtest.h"

#include "templates/instantiate_type_as_parameter/InstantiateTypeAsParameter.h"

#include "zserio/RebindAlloc.h"

namespace templates
{
namespace instantiate_type_as_parameter
{

using allocator_type = InstantiateTypeAsParameter::allocator_type;
template <typename T>
using vector_type = std::vector<T, zserio::RebindAlloc<allocator_type, T>>;

TEST(InstantiateTypeAsParameterTest, readWrite)
{
    InstantiateTypeAsParameter instantiateTypeAsParameter{
            P32{2}, Parameterized_P32{vector_type<uint32_t>{13, 42}}
    };

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    instantiateTypeAsParameter.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    InstantiateTypeAsParameter readInstantiateTypeAsParameter(reader);

    ASSERT_TRUE(instantiateTypeAsParameter == readInstantiateTypeAsParameter);
}

} // namespace instantiate_type_as_parameter
} // namespace templates
