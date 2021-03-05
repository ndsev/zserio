import unittest

import zserio

from testutils import getZserioApi

class PackageNameConflictTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "package_name_conflict.zs")

    def testPackageNameConflictLocal(self):
        # just test that PackageNameConflictLocal uses correct Blob
        packageNameConflictLocal = self.api.PackageNameConflictLocal(self.api.Blob(13))
        bitBuffer = zserio.serialize(packageNameConflictLocal)
        readPackageNameConflictLocal = zserio.deserialize(self.api.PackageNameConflictLocal, bitBuffer)

        self.assertEqual(13, packageNameConflictLocal.blob.value)
        self.assertEqual(packageNameConflictLocal.blob.value, readPackageNameConflictLocal.blob.value)

    def testPackageNameConflictImported(self):
        # just test that PackageNameConflictImported uses correct Blob
        packageNameConflictImported = self.api.PackageNameConflictImported(
            self.api.package_name_conflict.Blob("test")
        )
        bitBuffer = zserio.serialize(packageNameConflictImported)
        readPackageNameConflictImported = zserio.deserialize(self.api.PackageNameConflictImported, bitBuffer)

        self.assertEqual("test", packageNameConflictImported.blob.value)
        self.assertEqual(packageNameConflictImported.blob.value,
                         readPackageNameConflictImported.blob.value)
