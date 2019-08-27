import unittest

import zserio

from testutils import getZserioApi

class PackageNameConflictTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "package_name_conflict.zs")

    def testPackageNameConflict(self):
        # just test that PackageNameConflict uses correct Blob
        packageNameConflict = self.api.PackageNameConflict.fromFields(self.api.Blob.fromFields(13))
        writer = zserio.BitStreamWriter()
        packageNameConflict.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readPackageNameConflict = self.api.PackageNameConflict.fromReader(reader)

        self.assertEqual(13, packageNameConflict.getBlob().getValue())
        self.assertEqual(packageNameConflict.getBlob().getValue(), readPackageNameConflict.getBlob().getValue())

    def testPackageNameConflictInner(self):
        # just test that PackageNameConflict uses correct Blob
        packageNameConflictInner = self.api.PackageNameConflictInner.fromFields(
            self.api.package_name_conflict.Blob.fromFields("test"))
        writer = zserio.BitStreamWriter()
        packageNameConflictInner.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readPackageNameConflictInner = self.api.PackageNameConflictInner.fromReader(reader)

        self.assertEqual("test", packageNameConflictInner.getBlob().getValue())
        self.assertEqual(packageNameConflictInner.getBlob().getValue(),
                         readPackageNameConflictInner.getBlob().getValue())
