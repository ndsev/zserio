import os
import zserio

import ArrayTypes

from testutils import getApiDir

class PackedAutoArrayStructWithBytesTest(ArrayTypes.TestCase):
    def testWriteRead(self):
        packedAutoArray = self._createPackedAutoArray()
        bitBuffer = zserio.serialize(packedAutoArray)

        self.assertEqual(bitBuffer.bitsize, packedAutoArray.bitsizeof())
        self.assertEqual(bitBuffer.bitsize, packedAutoArray.initialize_offsets())

        readPackedAutoArray = zserio.deserialize(self.api.PackedAutoArray, bitBuffer)
        self.assertEqual(packedAutoArray, readPackedAutoArray)

    def testWriteReadFile(self):
        packedAutoArray = self._createPackedAutoArray()
        zserio.serialize_to_file(packedAutoArray, self.BLOB_NAME)

        readPackedAutoArray = zserio.deserialize_from_file(self.api.PackedAutoArray, self.BLOB_NAME)
        self.assertEqual(packedAutoArray, readPackedAutoArray)

    def _createPackedAutoArray(self):
        return self.api.PackedAutoArray(
            [self.api.TestStructure(uint32, bytes_field, uint8)
             for uint32, bytes_field, uint8 in zip(self.UINT32_FIELD, self.BYTES_FIELD, self.UINT8_FIELD)]
        )

    UINT32_FIELD = list(range(100000, 200000, 10000))
    BYTES_FIELD = [b'\xAB\xCD\xEF', b'\x00', b'\x01', b'\x00', b'\x01',
                    b'\x00', b'\x01', b'\x00', b'\x01', b'\x00']
    UINT8_FIELD = list(range(0, 20, 2))

    BLOB_NAME = os.path.join(getApiDir(os.path.dirname(__file__)),
                             "packed_auto_array_struct_with_bytes.blob")
