#include "gtest/gtest.h"

#include "templates/instantiate_type_on_parameterized_template/InstantiateTypeOnParameterizedTemplate.h"

namespace templates
{
namespace instantiate_type_on_parameterized_template
{

TEST(InstantiateTypeOnParameterizedTemplateTest, readWrite)
{
    InstantiateTypeOnParameterizedTemplate instantiateTypeOnParameterizedTemplate{
            2, TestP{Parameterized{std::vector<uint32_t>{13, 42}}}
    };

    zserio::BitStreamWriter writer;
    instantiateTypeOnParameterizedTemplate.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    InstantiateTypeOnParameterizedTemplate readInstantiateTypeOnParameterizedTemplate(reader);

    ASSERT_TRUE(instantiateTypeOnParameterizedTemplate == readInstantiateTypeOnParameterizedTemplate);
}

} // namespace instantiate_type_on_parameterized_template
} // namespace templates
