import os
import zserio

import ArrayTypesWarning

from testutils import getApiDir

class PackedArrayChoiceHasNoPackableFieldTest(ArrayTypesWarning.TestCase):
    def testWriteRead(self):
        packedArrayChoiceHasNoPackableField = self.api.PackedArrayChoiceHasNoPackableField(
            [
                self.api.StructWithPackable("A", 65),
                self.api.StructWithPackable("B", 66),
                self.api.StructWithPackable("C", 67)
            ],
            [
                self.api.UnionWithPackableField(),
                self.api.UnionWithPackableField(),
                self.api.UnionWithPackableField()
            ],
            [
                self.api.StructWithPackableArray("ABC", [65, 66, 67]),
                self.api.StructWithPackableArray("DEF", [68, 69, 70]),
                self.api.StructWithPackableArray("GHI", [71, 72, 73])
            ],
            [
                self.api.ChoiceWithoutPackableField(True),
                self.api.ChoiceWithoutPackableField(True),
                self.api.ChoiceWithoutPackableField(True)
            ]
        )

        packedArrayChoiceHasNoPackableField.array2[0].field2 = self.api.TestEnum.ONE
        packedArrayChoiceHasNoPackableField.array2[1].field2 = self.api.TestEnum.TWO
        packedArrayChoiceHasNoPackableField.array2[2].field2 = self.api.TestEnum.ONE

        packedArrayChoiceHasNoPackableField.array4[0].field1 = 4.0
        packedArrayChoiceHasNoPackableField.array4[1].field1 = 1.0
        packedArrayChoiceHasNoPackableField.array4[2].field1 = 0.0

        zserio.serialize_to_file(packedArrayChoiceHasNoPackableField, self.BLOB_NAME)
        readPackedArrayChoiceHasNoPackableField = zserio.deserialize_from_file(
            self.api.PackedArrayChoiceHasNoPackableField, self.BLOB_NAME
        )
        self.assertEqual(packedArrayChoiceHasNoPackableField, readPackedArrayChoiceHasNoPackableField)

    BLOB_NAME = os.path.join(getApiDir(os.path.dirname(__file__)),
                             "packed_array_choice_has_no_packable_field.blob")
