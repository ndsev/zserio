import unittest
import os
import zserio

from testutils import getZserioApi, getApiDir
from ArrayTypesWarningTest import EXPECTED_WARNINGS

class PackedArrayUnpackableBytesElementTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "array_types_warning.zs",
                               expectedWarnings=EXPECTED_WARNINGS).packed_array_unpackable_bytes_element

    def testWriteRead(self):
        packedArrayUnpackableBytesElement = self.api.PackedArrayUnpackableBytesElement(
            [10, 11, 12],
            [bytes([0, 1, 2]), bytes([11, 12, 13]), bytes([100, 101, 102])]
        )

        zserio.serialize_to_file(packedArrayUnpackableBytesElement, self.BLOB_NAME)
        readPackedArrayUnpackableBytesElement = zserio.deserialize_from_file(
            self.api.PackedArrayUnpackableBytesElement, self.BLOB_NAME
        )
        self.assertEqual(packedArrayUnpackableBytesElement, readPackedArrayUnpackableBytesElement)

    BLOB_NAME = os.path.join(getApiDir(os.path.dirname(__file__)),
                             "packed_array_unpackable_bytes_element.blob")
