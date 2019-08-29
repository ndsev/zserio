#include "gtest/gtest.h"

#include "package_name_conflict/PackageNameConflict.h"
#include "package_name_conflict/PackageNameConflictInner.h"

namespace package_name_conflict
{

TEST(PackageNameConflictTest, packageNameConflict)
{
    // just test that PackageNameConflict includes correct Blob
    PackageNameConflict packageNameConflict{Blob{13}};

    zserio::BitStreamWriter writer;
    packageNameConflict.write(writer);

    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    PackageNameConflict readPackageNameConflict(reader);

    ASSERT_EQ(13, packageNameConflict.getBlob().getValue());
    ASSERT_EQ(packageNameConflict.getBlob().getValue(), readPackageNameConflict.getBlob().getValue());
}

TEST(PackageNameConflictTest, packageNameConflictInner)
{
    // just test that PackageNameConflictInner includes correct Blob
    PackageNameConflictInner packageNameConflictInner{
            ::package_name_conflict::package_name_conflict::Blob{"test"}};

    zserio::BitStreamWriter writer;
    packageNameConflictInner.write(writer);

    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    PackageNameConflictInner readPackageNameConflictInner(reader);

    ASSERT_EQ("test", packageNameConflictInner.getBlob().getValue());
    ASSERT_EQ(packageNameConflictInner.getBlob().getValue(), readPackageNameConflictInner.getBlob().getValue());
}

} // namespace package_name_conflict
