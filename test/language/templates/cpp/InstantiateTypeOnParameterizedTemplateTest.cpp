#include "gtest/gtest.h"

#include "templates/instantiate_type_on_parameterized_template/InstantiateTypeOnParameterizedTemplate.h"

#include "zserio/RebindAlloc.h"

namespace templates
{
namespace instantiate_type_on_parameterized_template
{

using allocator_type = InstantiateTypeOnParameterizedTemplate::allocator_type;
template <typename T>
using vector_type = std::vector<T, zserio::RebindAlloc<allocator_type, T>>;

TEST(InstantiateTypeOnParameterizedTemplateTest, readWrite)
{
    InstantiateTypeOnParameterizedTemplate instantiateTypeOnParameterizedTemplate{
            2, TestP{Parameterized{vector_type<uint32_t>{13, 42}}}
    };

    instantiateTypeOnParameterizedTemplate.initializeChildren();
    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    instantiateTypeOnParameterizedTemplate.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    InstantiateTypeOnParameterizedTemplate readInstantiateTypeOnParameterizedTemplate(reader);

    ASSERT_TRUE(instantiateTypeOnParameterizedTemplate == readInstantiateTypeOnParameterizedTemplate);
}

} // namespace instantiate_type_on_parameterized_template
} // namespace templates
