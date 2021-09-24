import unittest
import os
import zserio

from testutils import getZserioApi, getApiDir

class BitFieldUInt64LengthTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "builtin_types.zs").bitfield_uint64_length

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
