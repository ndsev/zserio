import os
import zserio

import ArrayTypesWarning

from testutils import getApiDir


class PackedArrayStructHasNoPackableFieldTest(ArrayTypesWarning.TestCase):
    def testWriteRead(self):
        packedArrayStructHasNoPackableField = self.api.PackedArrayStructHasNoPackableField(
            [
                self.api.StructWithPackable("A", 65),
                self.api.StructWithPackable("B", 66),
                self.api.StructWithPackable("C", 67),
            ],
            [
                self.api.UnionWithPackableField(),
                self.api.UnionWithPackableField(),
                self.api.UnionWithPackableField(),
            ],
            [
                self.api.StructWithPackableArray("ABC", [65, 66, 67]),
                self.api.StructWithPackableArray("DEF", [68, 69, 70]),
                self.api.StructWithPackableArray("GHI", [71, 72, 73]),
            ],
            [
                self.api.StructWithoutPackable(
                    4.0, zserio.BitBuffer(bytes([0xF0]), 5), 0, "A", [0, 0, 0], [True, False, True]
                ),
                self.api.StructWithoutPackable(
                    1.0, zserio.BitBuffer(bytes([0xE0]), 5), 0, "B", [0, 0, 0], [True, False, True]
                ),
                self.api.StructWithoutPackable(
                    0.0, zserio.BitBuffer(bytes([0xD0]), 5), 0, "C", [0, 0, 0], [True, False, True]
                ),
            ],
            [self.api.EmptyStruct(), self.api.EmptyStruct(), self.api.EmptyStruct()],
        )

        packedArrayStructHasNoPackableField.array2[0].field2 = self.api.TestEnum.ONE
        packedArrayStructHasNoPackableField.array2[1].field2 = self.api.TestEnum.TWO
        packedArrayStructHasNoPackableField.array2[2].field2 = self.api.TestEnum.ONE

        zserio.serialize_to_file(packedArrayStructHasNoPackableField, self.BLOB_NAME)
        readPackedArrayStructHasNoPackableField = zserio.deserialize_from_file(
            self.api.PackedArrayStructHasNoPackableField, self.BLOB_NAME
        )
        self.assertEqual(packedArrayStructHasNoPackableField, readPackedArrayStructHasNoPackableField)

    BLOB_NAME = os.path.join(
        getApiDir(os.path.dirname(__file__)), "packed_array_struct_has_no_packable_field.blob"
    )
