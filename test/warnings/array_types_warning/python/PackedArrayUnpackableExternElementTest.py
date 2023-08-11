import unittest
import os
import zserio

from testutils import getZserioApi, getApiDir
from ArrayTypesWarningTest import EXPECTED_WARNINGS

class PackedArrayUnpackableExternElementTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "array_types_warning.zs",
                               expectedWarnings=EXPECTED_WARNINGS).packed_array_unpackable_extern_element

    def testWriteRead(self):
        packedArrayUnpackableExternElement = self.api.PackedArrayUnpackableExternElement(
            [10, 11, 12],
            [
                zserio.BitBuffer(bytes([0xff, 0xc0]), 10),
                zserio.BitBuffer(bytes([0xff, 0x80]), 10),
                zserio.BitBuffer(bytes([0xff, 0x40]), 10)
            ]
        )

        zserio.serialize_to_file(packedArrayUnpackableExternElement, self.BLOB_NAME)
        readPackedArrayUnpackableExternElement = zserio.deserialize_from_file(
            self.api.PackedArrayUnpackableExternElement, self.BLOB_NAME
        )
        self.assertEqual(packedArrayUnpackableExternElement, readPackedArrayUnpackableExternElement)

    BLOB_NAME = os.path.join(getApiDir(os.path.dirname(__file__)),
                             "packed_array_unpackable_extern_element.blob")
