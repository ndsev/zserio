import unittest
import zserio

from testutils import getZserioApi

class OptionalMemberAlignmentTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "alignment.zs").optional_member_alignment

    def testBitSizeOfWithOptional(self):
        optionalMemberAlignment = self.api.OptionalMemberAlignment(True, 0x4433, 0x1122)
        self.assertEqual(self.WITH_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE, optionalMemberAlignment.bitsizeof())

    def testBitSizeOfWithoutOptional(self):
        optionalMemberAlignment = self.api.OptionalMemberAlignment(False, None, 0x7624)
        self.assertEqual(self.WITHOUT_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE, optionalMemberAlignment.bitsizeof())

    def testInitializeOffsetsWithOptional(self):
        optionalMemberAlignment = self.api.OptionalMemberAlignment(True, 0x1111, 0x3333)
        for bitPosition in range(32):
            self.assertEqual(self.WITH_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE,
                             optionalMemberAlignment.initialize_offsets(bitPosition))

        bitPosition = 32
        self.assertEqual(self.WITH_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE + bitPosition,
                         optionalMemberAlignment.initialize_offsets(bitPosition))

    def testInitializeOffsetsWithoutOptional(self):
        optionalMemberAlignment = self.api.OptionalMemberAlignment(field_=0x3334)
        bitPosition = 1
        self.assertEqual(self.WITHOUT_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE + bitPosition,
                         optionalMemberAlignment.initialize_offsets(bitPosition))

    def testReadWithOptional(self):
        hasOptional = True
        optionalField = 0x1234
        field = 0x7654
        writer = zserio.BitStreamWriter()
        OptionalMemberAlignmentTest._writeOptionalMemberAlignmentToStream(writer, hasOptional, optionalField,
                                                                          field)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        optionalMemberAlignment = self.api.OptionalMemberAlignment.from_reader(reader)
        self._checkOptionalMemberAlignment(optionalMemberAlignment, hasOptional, optionalField, field)

    def testReadWithoutOptional(self):
        hasOptional = False
        field = 0x2222
        writer = zserio.BitStreamWriter()
        OptionalMemberAlignmentTest._writeOptionalMemberAlignmentToStream(writer, hasOptional, None, field)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        optionalMemberAlignment = self.api.OptionalMemberAlignment.from_reader(reader)
        self._checkOptionalMemberAlignment(optionalMemberAlignment, hasOptional, None, field)

    def testWriteWithOptional(self):
        hasOptional = True
        optionalField = 0x9ADB
        field = 0x8ACD
        optionalMemberAlignment = self.api.OptionalMemberAlignment(hasOptional, optionalField, field)
        bitBuffer = zserio.serialize(optionalMemberAlignment)
        readOptionalMemberAlignment = zserio.deserialize(self.api.OptionalMemberAlignment, bitBuffer)
        self._checkOptionalMemberAlignment(readOptionalMemberAlignment, hasOptional, optionalField, field)
        self.assertTrue(optionalMemberAlignment == readOptionalMemberAlignment)

    def testWriteWithoutOptional(self):
        hasOptional = False
        field = 0x7ACF
        optionalMemberAlignment = self.api.OptionalMemberAlignment(has_optional_=hasOptional, field_=field)
        bitBuffer = zserio.serialize(optionalMemberAlignment)
        readOptionalMemberAlignment = zserio.deserialize(self.api.OptionalMemberAlignment, bitBuffer)
        self._checkOptionalMemberAlignment(readOptionalMemberAlignment, hasOptional, None, field)
        self.assertTrue(optionalMemberAlignment == readOptionalMemberAlignment)

    @staticmethod
    def _writeOptionalMemberAlignmentToStream(writer, hasOptional, optionalField, field):
        writer.write_bool(hasOptional)
        if hasOptional:
            writer.write_bits(0, 31)
            writer.write_bits(optionalField, 32)
        writer.write_bits(field, 32)

    def _checkOptionalMemberAlignment(self, optionalMemberAlignment, hasOptional, optionalField, field):
        self.assertEqual(hasOptional, optionalMemberAlignment.has_optional)
        if hasOptional:
            self.assertTrue(optionalMemberAlignment.is_optional_field_used())
            self.assertEqual(optionalField, optionalMemberAlignment.optional_field)
        else:
            self.assertFalse(optionalMemberAlignment.is_optional_field_used())
        self.assertEqual(field, optionalMemberAlignment.field)

    WITH_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE = 96
    WITHOUT_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE = 33
