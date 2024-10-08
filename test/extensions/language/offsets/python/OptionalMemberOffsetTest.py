import zserio

import Offsets


class OptionalMemberOffsetTest(Offsets.TestCase):
    def testBitSizeOfWithOptional(self):
        optionalMemberOffset = self.api.OptionalMemberOffset(True, self.OPTIONAL_FIELD_OFFSET, 0x1010, 0x2020)
        self.assertEqual(self.WITH_OPTIONAL_MEMBER_OFFSET_BIT_SIZE, optionalMemberOffset.bitsizeof())

    def testBitSizeOfWithoutOptional(self):
        optionalMemberOffset = self.api.OptionalMemberOffset(False, 0xDEAD, None, 0xBEEF)
        self.assertEqual(self.WITHOUT_OPTIONAL_MEMBER_OFFSET_BIT_SIZE, optionalMemberOffset.bitsizeof())

    def testInitializeOffsetsWithOptional(self):
        hasOptional = True
        optionalField = 0x1010
        field = 0x2020
        optionalMemberOffset = self.api.OptionalMemberOffset(
            hasOptional, self.WRONG_OPTIONAL_FIELD_OFFSET, optionalField, field
        )
        bitPosition = 2
        self.assertEqual(
            self.WITH_OPTIONAL_MEMBER_OFFSET_BIT_SIZE, optionalMemberOffset.initialize_offsets(bitPosition)
        )
        self._checkOptionalMemberOffset(
            optionalMemberOffset, hasOptional, self.OPTIONAL_FIELD_OFFSET, optionalField, field
        )

    def testInitializeOffsetsWithoutOptional(self):
        hasOptional = False
        optionalFieldOffset = 0xABCD
        field = 0x2020
        optionalMemberOffset = self.api.OptionalMemberOffset(hasOptional, optionalFieldOffset, None, field)
        bitPosition = 2
        self.assertEqual(
            self.WITHOUT_OPTIONAL_MEMBER_OFFSET_BIT_SIZE + bitPosition,
            optionalMemberOffset.initialize_offsets(bitPosition),
        )
        self._checkOptionalMemberOffset(optionalMemberOffset, hasOptional, optionalFieldOffset, None, field)

    def testReadWithOptional(self):
        hasOptional = True
        optionalField = 0x1212
        field = 0x2121
        writer = zserio.BitStreamWriter()
        OptionalMemberOffsetTest._writeOptionalMemberOffsetToStream(
            writer, hasOptional, self.OPTIONAL_FIELD_OFFSET, optionalField, field
        )
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        optionalMemberOffset = self.api.OptionalMemberOffset.from_reader(reader)
        self._checkOptionalMemberOffset(
            optionalMemberOffset, hasOptional, self.OPTIONAL_FIELD_OFFSET, optionalField, field
        )

    def testReadWithoutOptional(self):
        hasOptional = False
        optionalFieldOffset = 0xABCD
        field = 0x2121
        writer = zserio.BitStreamWriter()
        OptionalMemberOffsetTest._writeOptionalMemberOffsetToStream(
            writer, hasOptional, optionalFieldOffset, None, field
        )
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        optionalMemberOffset = self.api.OptionalMemberOffset.from_reader(reader)
        self._checkOptionalMemberOffset(optionalMemberOffset, hasOptional, optionalFieldOffset, None, field)

    def testWriteWithOptional(self):
        hasOptional = True
        optionalField = 0x1A1A
        field = 0xA1A1
        optionalMemberOffset = self.api.OptionalMemberOffset(
            hasOptional, self.WRONG_OPTIONAL_FIELD_OFFSET, optionalField, field
        )
        writer = zserio.BitStreamWriter()
        optionalMemberOffset.initialize_offsets(writer.bitposition)
        optionalMemberOffset.write(writer)
        self._checkOptionalMemberOffset(
            optionalMemberOffset, hasOptional, self.OPTIONAL_FIELD_OFFSET, optionalField, field
        )
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readOptionalMemberOffset = self.api.OptionalMemberOffset.from_reader(reader)
        self._checkOptionalMemberOffset(
            readOptionalMemberOffset, hasOptional, self.OPTIONAL_FIELD_OFFSET, optionalField, field
        )
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
        writer.write_bool(hasOptional)
        writer.write_bits(optionalFieldOffset, 32)
        if hasOptional:
            writer.write_bits(0, 7)
            writer.write_signed_bits(optionalField, 32)
        writer.write_signed_bits(field, 32)

    def _checkOptionalMemberOffset(
        self, optionalMemberOffset, hasOptional, optionalFieldOffset, optionalField, field
    ):
        self.assertEqual(hasOptional, optionalMemberOffset.has_optional)
        self.assertEqual(optionalFieldOffset, optionalMemberOffset.optional_field_offset)
        if hasOptional:
            self.assertEqual(optionalField, optionalMemberOffset.optional_field)
            self.assertTrue(optionalMemberOffset.is_optional_field_used())
        else:
            self.assertFalse(optionalMemberOffset.is_optional_field_used())
        self.assertEqual(field, optionalMemberOffset.field)

    WITH_OPTIONAL_MEMBER_OFFSET_BIT_SIZE = 104
    WITHOUT_OPTIONAL_MEMBER_OFFSET_BIT_SIZE = 65

    OPTIONAL_FIELD_OFFSET = 5
    WRONG_OPTIONAL_FIELD_OFFSET = 0
