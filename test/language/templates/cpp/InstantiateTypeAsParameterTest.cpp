#include "gtest/gtest.h"

#include "templates/instantiate_type_as_parameter/InstantiateTypeAsParameter.h"

namespace templates
{
namespace instantiate_type_as_parameter
{

TEST(InstantiateTypeAsParameterTest, readWrite)
{
    InstantiateTypeAsParameter instantiateTypeAsParameter{
            P32{2}, Parameterized_P32{std::vector<uint32_t>{13, 42}}
    };

    zserio::BitStreamWriter writer;
    instantiateTypeAsParameter.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    InstantiateTypeAsParameter readInstantiateTypeAsParameter(reader);

    ASSERT_TRUE(instantiateTypeAsParameter == readInstantiateTypeAsParameter);
}

} // namespace instantiate_type_as_parameter
} // namespace templates
