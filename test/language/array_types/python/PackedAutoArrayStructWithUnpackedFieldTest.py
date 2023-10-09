import os
import zserio

import ArrayTypes

from testutils import getApiDir

class PackedAutoArrayStructWithUnpackedFieldTest(ArrayTypes.TestCase):
    def testBitSizeOf(self):
        packedAutoArray = self._createPackedAutoArray()
        self.assertEqual(self.PACKED_AUTO_ARRAY_BIT_SIZE, packedAutoArray.bitsizeof())

    def testInitializeOffsets(self):
        packedAutoArray = self._createPackedAutoArray()
        bitPosition = 2
        expectedEndBitPosition = bitPosition + self.PACKED_AUTO_ARRAY_BIT_SIZE
        self.assertEqual(expectedEndBitPosition, packedAutoArray.initialize_offsets(bitPosition))

    def testWriteReadFile(self):
        packedAutoArray = self._createPackedAutoArray()
        zserio.serialize_to_file(packedAutoArray, self.BLOB_NAME)

        readPackedAutoArray = zserio.deserialize_from_file(self.api.PackedAutoArray, self.BLOB_NAME)
        self.assertEqual(packedAutoArray, readPackedAutoArray)

    def _createPackedAutoArray(self):
        return self.api.PackedAutoArray(
            [self.api.TestStructure(uint8, unpacked) for uint8, unpacked in zip(self.UINT8_FIELD,
                                                                                self.UNPACKED_FIELD)]
        )

    UINT8_FIELD = list(range(0, 20, 2))
    UNPACKED_FIELD = [5000000, 0, 1, 0, 1, 0, 1, 0, 1, 0]

    UINT8_MAX_BIT_NUMBER = 2
    PACKED_AUTO_ARRAY_BIT_SIZE = (
        8 + # auto array size: varsize
        1 + # uint8Field packing descriptor: isPacked (true)
        6 + # uint8Field is packed: maxBitNumber
        1 + # unpackedField packing descriptor: isPacked (false)
        8 + # uint8Field[0]
        32 + # unpackedField[0] (4 bytes for the first value)
        9 * (UINT8_MAX_BIT_NUMBER + 1) + # deltas for uint8Field values
        9 * 8 # unpackedField varuint values (1 byte)
    )

    BLOB_NAME = os.path.join(getApiDir(os.path.dirname(__file__)),
                             "packed_auto_array_struct_with_unpacked_field.blob")
