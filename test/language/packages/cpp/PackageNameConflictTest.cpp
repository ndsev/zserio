#include "gtest/gtest.h"
#include "package_name_conflict/PackageNameConflictImported.h"
#include "package_name_conflict/PackageNameConflictLocal.h"

namespace package_name_conflict
{

TEST(PackageNameConflictTest, packageNameConflictLocal)
{
    // just test that PackageNameConflictLocal includes correct Blob
    PackageNameConflictLocal packageNameConflictLocal{Blob{13}};

    zserio::BitBuffer bitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    packageNameConflictLocal.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    PackageNameConflictLocal readPackageNameConflictLocal(reader);

    ASSERT_EQ(13, packageNameConflictLocal.getBlob().getValue());
    ASSERT_EQ(packageNameConflictLocal.getBlob().getValue(), readPackageNameConflictLocal.getBlob().getValue());
}

TEST(PackageNameConflictTest, packageNameConflictImported)
{
    // just test that PackageNameConflictImported includes correct Blob
    PackageNameConflictImported packageNameConflictImported{
            ::package_name_conflict::package_name_conflict::Blob{"test"}};

    zserio::BitBuffer bitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    packageNameConflictImported.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    PackageNameConflictImported readPackageNameConflictImported(reader);

    ASSERT_EQ("test", packageNameConflictImported.getBlob().getValue());
    ASSERT_EQ(packageNameConflictImported.getBlob().getValue(),
            readPackageNameConflictImported.getBlob().getValue());
}

} // namespace package_name_conflict
