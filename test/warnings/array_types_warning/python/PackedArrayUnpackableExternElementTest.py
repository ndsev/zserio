import os
import zserio

import ArrayTypesWarning

from testutils import getApiDir

class PackedArrayUnpackableExternElementTest(ArrayTypesWarning.TestCase):
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
