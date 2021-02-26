import unittest

import zserio

from testutils import getZserioApi

class PackageNameConflictTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "package_name_conflict.zs")

    def testPackageNameConflict(self):
        # just test that PackageNameConflict uses correct Blob
        packageNameConflict = self.api.PackageNameConflict(self.api.Blob(13))
        bitBuffer = zserio.serialize(packageNameConflict)
        readPackageNameConflict = zserio.deserialize(self.api.PackageNameConflict, bitBuffer)

        self.assertEqual(13, packageNameConflict.blob.value)
        self.assertEqual(packageNameConflict.blob.value, readPackageNameConflict.blob.value)

    def testPackageNameConflictInner(self):
        # just test that PackageNameConflict uses correct Blob
        packageNameConflictInner = self.api.PackageNameConflictInner(
            self.api.package_name_conflict.Blob("test")
        )
        bitBuffer = zserio.serialize(packageNameConflictInner)
        readPackageNameConflictInner = zserio.deserialize(self.api.PackageNameConflictInner, bitBuffer)

        self.assertEqual("test", packageNameConflictInner.blob.value)
        self.assertEqual(packageNameConflictInner.blob.value,
                         readPackageNameConflictInner.blob.value)
