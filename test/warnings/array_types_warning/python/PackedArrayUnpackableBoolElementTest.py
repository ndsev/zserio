import os
import zserio

import ArrayTypesWarning

from testutils import getApiDir

class PackedArrayUnpackableBoolElementTest(ArrayTypesWarning.TestCase):
    def testWriteRead(self):
        packedArrayUnpackableBoolElement = self.api.PackedArrayUnpackableBoolElement(
            [0, 1, 2],
            [self.api.TestEnum.ONE, self.api.TestEnum.TWO, self.api.TestEnum.ONE],
            [
                self.api.TestBitmask.Values.BLACK,
                self.api.TestBitmask.Values.BLACK,
                self.api.TestBitmask.Values.BLACK
            ],
            [0, 1, 2],
            5,
            [0, -1, -2],
            [True, False, True]
        )

        zserio.serialize_to_file(packedArrayUnpackableBoolElement, self.BLOB_NAME)
        readPackedArrayUnpackableBoolElement = zserio.deserialize_from_file(
            self.api.PackedArrayUnpackableBoolElement, self.BLOB_NAME
        )
        self.assertEqual(packedArrayUnpackableBoolElement, readPackedArrayUnpackableBoolElement)

    BLOB_NAME = os.path.join(getApiDir(os.path.dirname(__file__)),
                             "packed_array_unpackable_bool_element.blob")
