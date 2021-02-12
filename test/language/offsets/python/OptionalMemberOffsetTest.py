import unittest
import zserio

from testutils import getZserioApi

class OptionalMemberOffsetTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "offsets.zs").optional_member_offset

    def testBitSizeOfWithOptional(self):
        optionalMemberOffset = self.api.OptionalMemberOffset(True, self.OPTIONAL_FIELD_OFFSET, 0x1010, 0x2020)
        self.assertEqual(self.WITH_OPTIONAL_MEMBER_OFFSET_BIT_SIZE, optionalMemberOffset.bitSizeOf())

    def testBitSizeOfWithoutOptional(self):
        optionalMemberOffset = self.api.OptionalMemberOffset(False, 0xDEAD, None, 0xBEEF)
        self.assertEqual(self.WITHOUT_OPTIONAL_MEMBER_OFFSET_BIT_SIZE, optionalMemberOffset.bitSizeOf())

    def testInitializeOffsetsWithOptional(self):
        hasOptional = True
        optionalField = 0x1010
        field = 0x2020
        optionalMemberOffset = self.api.OptionalMemberOffset(hasOptional,
                                                             self.WRONG_OPTIONAL_FIELD_OFFSET,
                                                             optionalField, field)
        bitPosition = 2
        self.assertEqual(self.WITH_OPTIONAL_MEMBER_OFFSET_BIT_SIZE,
                         optionalMemberOffset.initializeOffsets(bitPosition))
        self._checkOptionalMemberOffset(optionalMemberOffset, hasOptional, self.OPTIONAL_FIELD_OFFSET,
                                        optionalField, field)

    def testInitializeOffsetsWithoutOptional(self):
        hasOptional = False
        optionalFieldOffset = 0xABCD
        field = 0x2020
        optionalMemberOffset = self.api.OptionalMemberOffset(hasOptional, optionalFieldOffset, None, field)
        bitPosition = 2
        self.assertEqual(self.WITHOUT_OPTIONAL_MEMBER_OFFSET_BIT_SIZE + bitPosition,
                         optionalMemberOffset.initializeOffsets(bitPosition))
        self._checkOptionalMemberOffset(optionalMemberOffset, hasOptional, optionalFieldOffset, None, field)

    def testReadWithOptional(self):
        hasOptional = True
        optionalField = 0x1212
        field = 0x2121
        writer = zserio.BitStreamWriter()
        OptionalMemberOffsetTest._writeOptionalMemberOffsetToStream(writer, hasOptional,
                                                                    self.OPTIONAL_FIELD_OFFSET, optionalField,
                                                                    field)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        optionalMemberOffset = self.api.OptionalMemberOffset.fromReader(reader)
        self._checkOptionalMemberOffset(optionalMemberOffset, hasOptional, self.OPTIONAL_FIELD_OFFSET,
                                        optionalField, field)

    def testReadWithoutOptional(self):
        hasOptional = False
        optionalFieldOffset = 0xABCD
        field = 0x2121
        writer = zserio.BitStreamWriter()
        OptionalMemberOffsetTest._writeOptionalMemberOffsetToStream(writer, hasOptional, optionalFieldOffset,
                                                                    None, field)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        optionalMemberOffset = self.api.OptionalMemberOffset.fromReader(reader)
        self._checkOptionalMemberOffset(optionalMemberOffset, hasOptional, optionalFieldOffset, None, field)

    def testWriteWithOptional(self):
        hasOptional = True
        optionalField = 0x1A1A
        field = 0xA1A1
        optionalMemberOffset = self.api.OptionalMemberOffset(hasOptional,
                                                             self.WRONG_OPTIONAL_FIELD_OFFSET,
                                                             optionalField, field)
        writer = zserio.BitStreamWriter()
        optionalMemberOffset.write(writer)
        self._checkOptionalMemberOffset(optionalMemberOffset, hasOptional, self.OPTIONAL_FIELD_OFFSET,
                                        optionalField, field)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        readOptionalMemberOffset = self.api.OptionalMemberOffset.fromReader(reader)
        self._checkOptionalMemberOffset(readOptionalMemberOffset, hasOptional, self.OPTIONAL_FIELD_OFFSET,
                                        optionalField, field)
        self.assertTrue(optionalMemberOffset == readOptionalMemberOffset)

    def testWriteWithoutOptional(self):
        hasOptional = False
        optionalFieldOffset = 0xABCE
        field = 0x7ACF
        optionalMemberOffset = self.api.OptionalMemberOffset(hasOptional, optionalFieldOffset, None, field)
        bitBuffer = zserio.serialize(optionalMemberOffset)
        readOptionalMemberOffset = zserio.deserialize(self.api.OptionalMemberOffset, bitBuffer)
        self._checkOptionalMemberOffset(readOptionalMemberOffset, hasOptional, optionalFieldOffset, None, field)
        self.assertTrue(optionalMemberOffset == readOptionalMemberOffset)

    @staticmethod
    def _writeOptionalMemberOffsetToStream(writer, hasOptional, optionalFieldOffset, optionalField, field):
        writer.writeBool(hasOptional)
        writer.writeBits(optionalFieldOffset, 32)
        if hasOptional:
            writer.writeBits(0, 7)
            writer.writeSignedBits(optionalField, 32)
        writer.writeSignedBits(field, 32)

    def _checkOptionalMemberOffset(self, optionalMemberOffset, hasOptional, optionalFieldOffset, optionalField,
                                   field):
        self.assertEqual(hasOptional, optionalMemberOffset.getHasOptional())
        self.assertEqual(optionalFieldOffset, optionalMemberOffset.getOptionalFieldOffset())
        if hasOptional:
            self.assertEqual(optionalField, optionalMemberOffset.getOptionalField())
            self.assertTrue(optionalMemberOffset.isOptionalFieldUsed())
        else:
            self.assertFalse(optionalMemberOffset.isOptionalFieldUsed())
        self.assertEqual(field, optionalMemberOffset.getField())

    WITH_OPTIONAL_MEMBER_OFFSET_BIT_SIZE = 104
    WITHOUT_OPTIONAL_MEMBER_OFFSET_BIT_SIZE = 65

    OPTIONAL_FIELD_OFFSET = 5
    WRONG_OPTIONAL_FIELD_OFFSET = 0
