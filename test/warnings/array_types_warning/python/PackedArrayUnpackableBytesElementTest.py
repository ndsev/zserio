import os
import zserio

import ArrayTypesWarning

from testutils import getApiDir

class PackedArrayUnpackableBytesElementTest(ArrayTypesWarning.TestCase):
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
