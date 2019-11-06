#include "gtest/gtest.h"

#include "templates/instantiate_type_as_parameter/InstantiateTypeAsParameter.h"

namespace templates
{
namespace instantiate_type_as_parameter
{

TEST(InstantiateTypeAsParameterTest, readWrite)
{
    InstantiateTypeAsParameter instantiateTypeAsParameter;
    P32 p32;
    p32.setValue(2);
    instantiateTypeAsParameter.setParameter(p32);
    Parameterized_P32& parameterized = instantiateTypeAsParameter.getParameterized();
    parameterized.getArr().push_back(13);
    parameterized.getArr().push_back(42);

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
