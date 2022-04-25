#include "gtest/gtest.h"

#include "templates/struct_templated_type_argument/StructTemplatedTypeArgument.h"

#include "zserio/RebindAlloc.h"

namespace templates
{
namespace struct_templated_type_argument
{

using allocator_type = StructTemplatedTypeArgument::allocator_type;
using string_type = zserio::string<allocator_type>;

TEST(StructTemplatedTypeArgumentTest, readWrite)
{
    StructTemplatedTypeArgument structTemplatedTypeArgument;
    structTemplatedTypeArgument.setParamHolder(ParamHolder_uint32{42});
    structTemplatedTypeArgument.setParameterized(Parameterized_uint32{string_type{"description"}, 13});
    structTemplatedTypeArgument.initializeChildren();
    structTemplatedTypeArgument.initializeOffsets();

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    structTemplatedTypeArgument.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    StructTemplatedTypeArgument readStructTemplatedTypeArgument(reader);

    ASSERT_TRUE(structTemplatedTypeArgument == readStructTemplatedTypeArgument);
}

} // namespace struct_templated_type_argument
} // namespace templates
