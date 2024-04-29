import os
import zserio

import BuiltInTypes

from testutils import getApiDir


class BitFieldUInt64LengthTest(BuiltInTypes.TestCase):
    def testBitSizeOf(self):
        container = self.api.Container()
        bitFieldLength = 33
        container.length = bitFieldLength
        container.unsigned_bit_field = zserio.limits.UINT32_MAX + 1
        container.signed_bit_field = zserio.limits.INT32_MAX + 1

        expectedBitSizeOfContainer = 64 + 33 + 33
        self.assertEqual(expectedBitSizeOfContainer, container.bitsizeof())

    def testReadWrite(self):
        container = self.api.Container()
        bitFieldLength = 33
        container.length = bitFieldLength
        container.unsigned_bit_field = zserio.limits.UINT32_MAX + 1
        container.signed_bit_field = zserio.limits.INT32_MAX + 1

        zserio.serialize_to_file(container, self.BLOB_NAME)

        readContainer = zserio.deserialize_from_file(self.api.Container, self.BLOB_NAME)
        self.assertEqual(container, readContainer)

    BLOB_NAME = os.path.join(getApiDir(os.path.dirname(__file__)), "bit_field_uint64_length.blob")
