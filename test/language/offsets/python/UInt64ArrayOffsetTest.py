import unittest
import zserio

from testutils import getZserioApi

class UIn64ArrayOffsetTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "offsets.zs").uint64_array_offset

    def testBitSizeOf(self):
        uint64ArrayOffset = self.api.UInt64ArrayOffset.from_reader(self._createReader(False))
        self.assertEqual(self.BIT_SIZE, uint64ArrayOffset.bitsizeof())

    def testBitSizeOfWithPosition(self):
        uint64ArrayOffset = self.api.UInt64ArrayOffset.from_reader(self._createReader(False))
        self.assertEqual(self.BIT_SIZE + 5, uint64ArrayOffset.bitsizeof(3))

    def testInitializeOffsets(self):
        uint64ArrayOffset = self.api.UInt64ArrayOffset()
        uint64ArrayOffset.offsets = list(range(self.VALUES_SIZE))
        uint64ArrayOffset.array = list(range(self.ARRAY_SIZE))
        uint64ArrayOffset.values = list(range(self.VALUES_SIZE))
        uint64ArrayOffset.initialize_offsets(0)
        self.assertEqual(self.FIRST_OFFSET, uint64ArrayOffset.offsets[0])

    def testInitializeOffsetsWithPosition(self):
        uint64ArrayOffset = self.api.UInt64ArrayOffset()
        uint64ArrayOffset.offsets = list(range(self.VALUES_SIZE))
        uint64ArrayOffset.array = list(range(self.ARRAY_SIZE))
        uint64ArrayOffset.values = list(range(self.VALUES_SIZE))
        uint64ArrayOffset.initialize_offsets(3)
        # 3 bits start position + 5 bits alignment -> + 1 byte
        self.assertEqual(self.FIRST_OFFSET + 1, uint64ArrayOffset.offsets[0])

    def testRead(self):
        reader = self._createReader(False)
        uint64ArrayOffset = self.api.UInt64ArrayOffset()
        uint64ArrayOffset.read(reader)
        self.assertEqual(self.FIRST_OFFSET, uint64ArrayOffset.offsets[0])

    def testReadWrongOffsets(self):
        reader = self._createReader(True)
        uint64ArrayOffset = self.api.UInt64ArrayOffset()
        with self.assertRaises(zserio.PythonRuntimeException):
            uint64ArrayOffset.read(reader)

    def testWrite(self):
        uint64ArrayOffset = self.api.UInt64ArrayOffset()
        uint64ArrayOffset.offsets = list(range(self.VALUES_SIZE))
        uint64ArrayOffset.array = list(range(self.ARRAY_SIZE))
        uint64ArrayOffset.values = list(range(self.VALUES_SIZE))
        writer = zserio.BitStreamWriter()
        uint64ArrayOffset.write(writer)
        self.assertEqual(self.FIRST_OFFSET, uint64ArrayOffset.offsets[0])
        self.assertEqual(zserio.bitposition.bits_to_bytes(self.BIT_SIZE), len(writer.byte_array))

    def testWriteWithPosition(self):
        uint64ArrayOffset = self.api.UInt64ArrayOffset()
        uint64ArrayOffset.offsets = list(range(self.VALUES_SIZE))
        uint64ArrayOffset.array = list(range(self.ARRAY_SIZE))
        uint64ArrayOffset.values = list(range(self.VALUES_SIZE))
        writer = zserio.BitStreamWriter()
        writer.write_bits(0, 3)
        uint64ArrayOffset.write(writer)
        self.assertEqual(self.FIRST_OFFSET + 1, uint64ArrayOffset.offsets[0])
        self.assertEqual(zserio.bitposition.bits_to_bytes(self.BIT_SIZE) + 1, len(writer.byte_array))

    def testWriteWrongOffsets(self):
        uint64ArrayOffset = self.api.UInt64ArrayOffset()
        uint64ArrayOffset.offsets = [self.FIRST_OFFSET + i * 4 + 1 if (i == self.VALUES_SIZE - 1) else 0
                                    for i in range(self.VALUES_SIZE)]
        uint64ArrayOffset.array = list(range(self.ARRAY_SIZE))
        uint64ArrayOffset.values = list(range(self.VALUES_SIZE))

        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            uint64ArrayOffset.write(writer, zserio_call_initialize_offsets=False)

    def _createReader(self, wrongOffset):
        writer = zserio.BitStreamWriter()

        # offset
        writer.write_varsize(self.VALUES_SIZE)
        for i in range(self.VALUES_SIZE):
            offset = self.FIRST_OFFSET + i * 4 + (wrongOffset and 1 if (i == self.VALUES_SIZE - 1) else 0)
            writer.write_bits(offset, 64)

        # array
        writer.write_varsize(self.ARRAY_SIZE)
        for i in range(self.ARRAY_SIZE):
            writer.write_signed_bits(0, 8)

        # values
        writer.write_varsize(self.VALUES_SIZE)
        for i in range(self.VALUES_SIZE):
            writer.write_signed_bits(0, 32)

        return zserio.BitStreamReader(writer.byte_array, writer.bitposition)

    ARRAY_SIZE = 13
    VALUES_SIZE = 42

    FIRST_OFFSET = 1 + 8 * 42 + 1 + 13 + 1
    BIT_SIZE = 8 * ((1 + 8 * 42 + 1 + 13 + 1) + 4 * 42)
