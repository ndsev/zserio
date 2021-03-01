import unittest

import zserio

from testutils import getZserioApi

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

        writer = zserio.BitStreamWriter()
        container.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readContainer = self.api.Container()
        readContainer.read(reader)
        self.assertEqual(container, readContainer)
