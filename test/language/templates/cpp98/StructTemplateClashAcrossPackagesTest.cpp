#include "gtest/gtest.h"

#include "templates/struct_template_clash_across_packages/pkg1/InstantiationInPkg1.h"
#include "templates/struct_template_clash_across_packages/pkg2/InstantiationInPkg2.h"

namespace templates
{
namespace struct_template_clash_across_packages
{

namespace pkg1
{

TEST(StructTemplateClashAcrossPackagesTest, readWriteInPkg1)
{
    InstantiationInPkg1 instantiationInPkg1;
    instantiationInPkg1.getTest().getValue().setValue(42);

    zserio::BitStreamWriter writer;
    instantiationInPkg1.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    InstantiationInPkg1 readInstantiationInPkg1(reader);

    ASSERT_TRUE(instantiationInPkg1 == readInstantiationInPkg1);
}

} // pkg1

namespace pkg2
{

TEST(StructTemplateClashAcrossPackagesTest, readWriteInPkg2)
{
    InstantiationInPkg2 instantiationInPkg2;
    instantiationInPkg2.getTest().getValue().setValue("string");

    zserio::BitStreamWriter writer;
    instantiationInPkg2.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    InstantiationInPkg2 readInstantiationInPkg2(reader);

    ASSERT_TRUE(instantiationInPkg2 == readInstantiationInPkg2);
}

} // pkg2

} // namespace struct_template_clash_across_packages
} // namespace templates
