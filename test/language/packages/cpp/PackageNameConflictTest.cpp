#include "gtest/gtest.h"

#include "package_name_conflict/PackageNameConflictLocal.h"
#include "package_name_conflict/PackageNameConflictImported.h"

namespace package_name_conflict
{

TEST(PackageNameConflictTest, packageNameConflictLocal)
{
    // just test that PackageNameConflictLocal includes correct Blob
    PackageNameConflictLocal packageNameConflictLocal{Blob{13}};

    zserio::BitStreamWriter writer;
    packageNameConflictLocal.write(writer);

    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    PackageNameConflictLocal readPackageNameConflictLocal(reader);

    ASSERT_EQ(13, packageNameConflictLocal.getBlob().getValue());
    ASSERT_EQ(packageNameConflictLocal.getBlob().getValue(), readPackageNameConflictLocal.getBlob().getValue());
}

TEST(PackageNameConflictTest, packageNameConflictImported)
{
    // just test that PackageNameConflictImported includes correct Blob
    PackageNameConflictImported packageNameConflictImported{
            ::package_name_conflict::package_name_conflict::Blob{"test"}};

    zserio::BitStreamWriter writer;
    packageNameConflictImported.write(writer);

    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    PackageNameConflictImported readPackageNameConflictImported(reader);

    ASSERT_EQ("test", packageNameConflictImported.getBlob().getValue());
    ASSERT_EQ(packageNameConflictImported.getBlob().getValue(),
            readPackageNameConflictImported.getBlob().getValue());
}

} // namespace package_name_conflict
