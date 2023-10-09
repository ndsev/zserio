import os
import zserio

import ArrayTypesWarning

from testutils import getApiDir

class PackedArrayUnpackableFloatElementTest(ArrayTypesWarning.TestCase):
    def testWriteRead(self):
        packedArrayUnpackableFloatElement = self.api.PackedArrayUnpackableFloatElement(
            [10, 11, 12],
            [4.0, 1.0, 0.0]
        )

        zserio.serialize_to_file(packedArrayUnpackableFloatElement, self.BLOB_NAME)
        readPackedArrayUnpackableFloatElement = zserio.deserialize_from_file(
            self.api.PackedArrayUnpackableFloatElement, self.BLOB_NAME
        )
        self.assertEqual(packedArrayUnpackableFloatElement, readPackedArrayUnpackableFloatElement)

    BLOB_NAME = os.path.join(getApiDir(os.path.dirname(__file__)),
                             "packed_array_unpackable_float_element.blob")
