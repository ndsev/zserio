import unittest
import os
import zserio

from testutils import getZserioApi, getApiDir
from ArrayTypesWarningTest import EXPECTED_WARNINGS

class PackedArrayTemplateHasNoPackableFieldTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "array_types_warning.zs",
                               expectedWarnings=EXPECTED_WARNINGS).packed_array_template_has_no_packable_field

    def testWriteReadU32(self):
        u32 = self.api.T_u32([0, 1, 2, 3, 4, 5])

        blobName = self.BLOB_NAME_BASE + "_u32.blob"
        zserio.serialize_to_file(u32, blobName)
        readU32 = zserio.deserialize_from_file(self.api.T_u32, blobName)
        self.assertEqual(u32, readU32)

    def testWriteReadStr(self):
        tStr = self.api.T_str(["A", "B", "C", "D", "E", "F"])

        blobName = self.BLOB_NAME_BASE + "_str.blob"
        zserio.serialize_to_file(tStr, blobName)
        readTStr = zserio.deserialize_from_file(self.api.T_str, blobName)
        self.assertEqual(tStr, readTStr)

    def testWriteReadPackable(self):
        packable = self.api.T_packable([
            self.api.Packable(0, 4.0, "A"),
            self.api.Packable(1, 1.0, "B"),
            self.api.Packable(2, 0.0, "C")
        ])

        blobName = self.BLOB_NAME_BASE + "_packable.blob"
        zserio.serialize_to_file(packable, blobName)
        readPackable = zserio.deserialize_from_file(self.api.T_packable, blobName)
        self.assertEqual(packable, readPackable)

    def testWriteReadUnpackable(self):
        unpackable = self.api.T_unpackable([
            self.api.Unpackable(4.0, "A"),
            self.api.Unpackable(1.0, "B"),
            self.api.Unpackable(0.0, "C")
        ])

        blobName = self.BLOB_NAME_BASE + "_unpackable.blob"
        zserio.serialize_to_file(unpackable, blobName)
        readUnpackable = zserio.deserialize_from_file(self.api.T_unpackable, blobName)
        self.assertEqual(unpackable, readUnpackable)

    BLOB_NAME_BASE = os.path.join(getApiDir(os.path.dirname(__file__)),
                                  "packed_array_template_has_no_packable_field")
