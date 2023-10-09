import zserio

import Alignment

class AutoOptionalMemberAlignmentTest(Alignment.TestCase):
    def testBitSizeOfWithOptional(self):
        autoOptionalMemberAlignment = self.api.AutoOptionalMemberAlignment(0x4433, 0x1122)
        self.assertEqual(self.WITH_AUTO_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE,
                         autoOptionalMemberAlignment.bitsizeof())

    def testBitSizeOfWithoutOptional(self):
        autoOptionalMemberAlignment = self.api.AutoOptionalMemberAlignment(None, 0x7624)
        self.assertEqual(self.WITHOUT_AUTO_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE,
                         autoOptionalMemberAlignment.bitsizeof())

    def testInitializeOffsetsWithOptional(self):
        autoOptionalMemberAlignment = self.api.AutoOptionalMemberAlignment(0x1111, 0x3333)
        for bitPosition in range(32):
            self.assertEqual(self.WITH_AUTO_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE,
                             autoOptionalMemberAlignment.initialize_offsets(bitPosition))
        bitPosition = 32
        self.assertEqual(self.WITH_AUTO_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE + bitPosition,
                         autoOptionalMemberAlignment.initialize_offsets(bitPosition))

    def testInitializeOffsetsWithoutOptional(self):
        autoOptionalMemberAlignment = self.api.AutoOptionalMemberAlignment(field_=0x3334)
        bitPosition = 1
        self.assertEqual(self.WITHOUT_AUTO_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE + bitPosition,
                         autoOptionalMemberAlignment.initialize_offsets(bitPosition))

    def testReadWithOptional(self):
        autoOptionalField = 0x1234
        field = 0x7654
        writer = zserio.BitStreamWriter()
        AutoOptionalMemberAlignmentTest._writeAutoOptionalMemberAlignmentToStream(writer, autoOptionalField,
                                                                                  field)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        autoOptionalMemberAlignment = self.api.AutoOptionalMemberAlignment.from_reader(reader)
        self._checkAutoOptionalMemberAlignment(autoOptionalMemberAlignment, autoOptionalField, field)

    def testReadWithoutOptional(self):
        field = 0x2222
        writer = zserio.BitStreamWriter()
        AutoOptionalMemberAlignmentTest._writeAutoOptionalMemberAlignmentToStream(writer, None, field)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        autoOptionalMemberAlignment = self.api.AutoOptionalMemberAlignment.from_reader(reader)
        self._checkAutoOptionalMemberAlignment(autoOptionalMemberAlignment, None, field)

    def testWriteWithOptional(self):
        autoOptionalField = 0x9ADB
        field = 0x8ACD
        autoOptionalMemberAlignment = self.api.AutoOptionalMemberAlignment(autoOptionalField, field)
        bitBuffer = zserio.serialize(autoOptionalMemberAlignment)
        readAutoOptionalMemberAlignment = zserio.deserialize(self.api.AutoOptionalMemberAlignment, bitBuffer)
        self._checkAutoOptionalMemberAlignment(readAutoOptionalMemberAlignment, autoOptionalField, field)
        self.assertTrue(autoOptionalMemberAlignment == readAutoOptionalMemberAlignment)

    def testWriteWithoutOptional(self):
        field = 0x7ACF
        autoOptionalMemberAlignment = self.api.AutoOptionalMemberAlignment(field_=field)
        bitBuffer = zserio.serialize(autoOptionalMemberAlignment)
        readAutoOptionalMemberAlignment = zserio.deserialize(self.api.AutoOptionalMemberAlignment, bitBuffer)
        self._checkAutoOptionalMemberAlignment(readAutoOptionalMemberAlignment, None, field)
        self.assertTrue(autoOptionalMemberAlignment == readAutoOptionalMemberAlignment)

    @staticmethod
    def _writeAutoOptionalMemberAlignmentToStream(writer, autoOptionalField, field):
        if autoOptionalField is not None:
            writer.write_bool(True)
            writer.write_bits(0, 31)
            writer.write_signed_bits(autoOptionalField, 32)
        else:
            writer.write_bool(False)
        writer.write_signed_bits(field, 32)

    def _checkAutoOptionalMemberAlignment(self, autoOptionalMemberAlignment, autoOptionalField, field):
        if autoOptionalField is not None:
            self.assertTrue(autoOptionalMemberAlignment.is_auto_optional_field_used())
            self.assertEqual(autoOptionalField, autoOptionalMemberAlignment.auto_optional_field)
        else:
            self.assertFalse(autoOptionalMemberAlignment.is_auto_optional_field_used())
        self.assertEqual(field, autoOptionalMemberAlignment.field)

    WITH_AUTO_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE = 96
    WITHOUT_AUTO_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE = 33
