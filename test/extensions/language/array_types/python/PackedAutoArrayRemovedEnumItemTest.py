import os
import zserio

import ArrayTypes

from testutils import getApiDir


class PackedAutoArrayRemovedEnumItemTest(ArrayTypes.TestCase):
    def testWriteReadFile(self):
        packedAutoArrayRemovedEnumItem = self.api.PackedAutoArrayRemovedEnumItem(
            [self.api.Traffic.NONE, self.api.Traffic.LIGHT, self.api.Traffic.MID]
        )

        zserio.serialize_to_file(packedAutoArrayRemovedEnumItem, self.BLOB_NAME)

        readPackedAutoArrayRemovedEnumItem = zserio.deserialize_from_file(
            self.api.PackedAutoArrayRemovedEnumItem, self.BLOB_NAME
        )
        self.assertEqual(packedAutoArrayRemovedEnumItem, readPackedAutoArrayRemovedEnumItem)

    def testWriteReadRemovedException(self):
        packedAutoArrayRemovedEnumItem = self.api.PackedAutoArrayRemovedEnumItem(
            [
                self.api.Traffic.NONE,
                self.api.Traffic.LIGHT,
                self.api.Traffic.MID,
                self.api.Traffic.ZSERIO_REMOVED_HEAVY,
            ]
        )

        with self.assertRaises(zserio.PythonRuntimeException):
            zserio.serialize(packedAutoArrayRemovedEnumItem)

    BLOB_NAME = os.path.join(getApiDir(os.path.dirname(__file__)), "packed_auto_array_removed_enum_item.blob")
