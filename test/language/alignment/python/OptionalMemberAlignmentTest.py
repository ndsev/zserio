import unittest
import zserio

from testutils import getZserioApi

class OptionalMemberAlignmentTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "alignment.zs").optional_member_alignment

    def testBitSizeOfWithOptional(self):
        optionalMemberAlignment = self.api.OptionalMemberAlignment.fromFields(True, 0x4433, 0x1122)
        self.assertEqual(self.WITH_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE, optionalMemberAlignment.bitSizeOf())

    def testBitSizeOfWithoutOptional(self):
        optionalMemberAlignment = self.api.OptionalMemberAlignment.fromFields(False, None, 0x7624)
        self.assertEqual(self.WITHOUT_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE, optionalMemberAlignment.bitSizeOf())

    def testInitializeOffsetsWithOptional(self):
        optionalMemberAlignment = self.api.OptionalMemberAlignment.fromFields(True, 0x1111, 0x3333)
        for bitPosition in range(32):
            self.assertEqual(self.WITH_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE,
                             optionalMemberAlignment.initializeOffsets(bitPosition))

        bitPosition = 32
        self.assertEqual(self.WITH_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE + bitPosition,
                         optionalMemberAlignment.initializeOffsets(bitPosition))

    def testInitializeOffsetsWithoutOptional(self):
        optionalMemberAlignment = self.api.OptionalMemberAlignment.fromFields(False, None, 0x3334)
        bitPosition = 1
        self.assertEqual(self.WITHOUT_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE + bitPosition,
                         optionalMemberAlignment.initializeOffsets(bitPosition))

    def testReadWithOptional(self):
        hasOptional = True
        optionalField = 0x1234
        field = 0x7654
        writer = zserio.BitStreamWriter()
        OptionalMemberAlignmentTest._writeOptionalMemberAlignmentToStream(writer, hasOptional, optionalField,
                                                                          field)
        reader = zserio.BitStreamReader(writer.getByteArray())
        optionalMemberAlignment = self.api.OptionalMemberAlignment.fromReader(reader)
        self._checkOptionalMemberAlignment(optionalMemberAlignment, hasOptional, optionalField, field)

    def testReadWithoutOptional(self):
        hasOptional = False
        field = 0x2222
        writer = zserio.BitStreamWriter()
        OptionalMemberAlignmentTest._writeOptionalMemberAlignmentToStream(writer, hasOptional, None, field)
        reader = zserio.BitStreamReader(writer.getByteArray())
        optionalMemberAlignment = self.api.OptionalMemberAlignment.fromReader(reader)
        self._checkOptionalMemberAlignment(optionalMemberAlignment, hasOptional, None, field)

    def testWriteWithOptional(self):
        hasOptional = True
        optionalField = 0x9ADB
        field = 0x8ACD
        optionalMemberAlignment = self.api.OptionalMemberAlignment.fromFields(hasOptional, optionalField, field)
        writer = zserio.BitStreamWriter()
        optionalMemberAlignment.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readOptionalMemberAlignment = self.api.OptionalMemberAlignment.fromReader(reader)
        self._checkOptionalMemberAlignment(readOptionalMemberAlignment, hasOptional, optionalField, field)
        self.assertTrue(optionalMemberAlignment == readOptionalMemberAlignment)

    def testWriteWithoutOptional(self):
        hasOptional = False
        field = 0x7ACF
        optionalMemberAlignment = self.api.OptionalMemberAlignment.fromFields(hasOptional, None, field)
        writer = zserio.BitStreamWriter()
        optionalMemberAlignment.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readOptionalMemberAlignment = self.api.OptionalMemberAlignment.fromReader(reader)
        self._checkOptionalMemberAlignment(readOptionalMemberAlignment, hasOptional, None, field)
        self.assertTrue(optionalMemberAlignment == readOptionalMemberAlignment)

    @staticmethod
    def _writeOptionalMemberAlignmentToStream(writer, hasOptional, optionalField, field):
        writer.writeBool(hasOptional)
        if hasOptional:
            writer.writeBits(0, 31)
            writer.writeBits(optionalField, 32)
        writer.writeBits(field, 32)

    def _checkOptionalMemberAlignment(self, optionalMemberAlignment, hasOptional, optionalField, field):
        self.assertEqual(hasOptional, optionalMemberAlignment.getHasOptional())
        if hasOptional:
            self.assertTrue(optionalMemberAlignment.hasOptionalField())
            self.assertEqual(optionalField, optionalMemberAlignment.getOptionalField())
        else:
            self.assertFalse(optionalMemberAlignment.hasOptionalField())
        self.assertEqual(field, optionalMemberAlignment.getField())

    WITH_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE = 96
    WITHOUT_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE = 33
