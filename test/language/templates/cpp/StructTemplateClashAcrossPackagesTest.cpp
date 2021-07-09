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
    instantiationInPkg1.setTest(
            ::templates::struct_template_clash_across_packages::test_struct::TestStruct_Test_639610D0(
                    pkg1::Test(42)));

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    instantiationInPkg1.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    InstantiationInPkg1 readInstantiationInPkg1(reader);

    ASSERT_TRUE(instantiationInPkg1 == readInstantiationInPkg1);
}

} // pkg1

namespace pkg2
{

TEST(StructTemplateClashAcrossPackagesTest, readWriteInPkg2)
{
    InstantiationInPkg2 instantiationInPkg2;
    instantiationInPkg2.setTest(
            ::templates::struct_template_clash_across_packages::test_struct::TestStruct_Test_67B82BA5(
                    pkg2::Test("string")));

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    instantiationInPkg2.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    InstantiationInPkg2 readInstantiationInPkg2(reader);

    ASSERT_TRUE(instantiationInPkg2 == readInstantiationInPkg2);
}

} // pkg2

} // namespace struct_template_clash_across_packages
} // namespace templates
