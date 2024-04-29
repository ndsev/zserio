import os
import zserio

import ArrayTypesWarning

from testutils import getApiDir


class PackedArrayUnpackableStringElementTest(ArrayTypesWarning.TestCase):
    def testWriteRead(self):
        packedArrayUnpackableStringElement = self.api.PackedArrayUnpackableStringElement(
            [10, 11, 12], ["A", "B", "C"]
        )

        zserio.serialize_to_file(packedArrayUnpackableStringElement, self.BLOB_NAME)
        readPackedArrayUnpackableStringElement = zserio.deserialize_from_file(
            self.api.PackedArrayUnpackableStringElement, self.BLOB_NAME
        )
        self.assertEqual(packedArrayUnpackableStringElement, readPackedArrayUnpackableStringElement)

    BLOB_NAME = os.path.join(
        getApiDir(os.path.dirname(__file__)), "packed_array_unpackable_string_element.blob"
    )
