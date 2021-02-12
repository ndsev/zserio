import unittest
import zserio

from testutils import getZserioApi

class AutoOptionalMemberAlignmentTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "alignment.zs").auto_optional_member_alignment

    def testBitSizeOfWithOptional(self):
        autoOptionalMemberAlignment = self.api.AutoOptionalMemberAlignment(0x4433, 0x1122)
        self.assertEqual(self.WITH_AUTO_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE,
                         autoOptionalMemberAlignment.bitSizeOf())

    def testBitSizeOfWithoutOptional(self):
        autoOptionalMemberAlignment = self.api.AutoOptionalMemberAlignment(None, 0x7624)
        self.assertEqual(self.WITHOUT_AUTO_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE,
                         autoOptionalMemberAlignment.bitSizeOf())

    def testInitializeOffsetsWithOptional(self):
        autoOptionalMemberAlignment = self.api.AutoOptionalMemberAlignment(0x1111, 0x3333)
        for bitPosition in range(32):
            self.assertEqual(self.WITH_AUTO_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE,
                             autoOptionalMemberAlignment.initializeOffsets(bitPosition))
        bitPosition = 32
        self.assertEqual(self.WITH_AUTO_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE + bitPosition,
                         autoOptionalMemberAlignment.initializeOffsets(bitPosition))

    def testInitializeOffsetsWithoutOptional(self):
        autoOptionalMemberAlignment = self.api.AutoOptionalMemberAlignment(field_=0x3334)
        bitPosition = 1
        self.assertEqual(self.WITHOUT_AUTO_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE + bitPosition,
                         autoOptionalMemberAlignment.initializeOffsets(bitPosition))

    def testReadWithOptional(self):
        autoOptionalField = 0x1234
        field = 0x7654
        writer = zserio.BitStreamWriter()
        AutoOptionalMemberAlignmentTest._writeAutoOptionalMemberAlignmentToStream(writer, autoOptionalField,
                                                                                  field)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        autoOptionalMemberAlignment = self.api.AutoOptionalMemberAlignment.fromReader(reader)
        self._checkAutoOptionalMemberAlignment(autoOptionalMemberAlignment, autoOptionalField, field)

    def testReadWithoutOptional(self):
        field = 0x2222
        writer = zserio.BitStreamWriter()
        AutoOptionalMemberAlignmentTest._writeAutoOptionalMemberAlignmentToStream(writer, None, field)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        autoOptionalMemberAlignment = self.api.AutoOptionalMemberAlignment.fromReader(reader)
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
            writer.writeBool(True)
            writer.writeBits(0, 31)
            writer.writeSignedBits(autoOptionalField, 32)
        else:
            writer.writeBool(False)
        writer.writeSignedBits(field, 32)

    def _checkAutoOptionalMemberAlignment(self, autoOptionalMemberAlignment, autoOptionalField, field):
        if autoOptionalField is not None:
            self.assertTrue(autoOptionalMemberAlignment.isAutoOptionalFieldUsed())
            self.assertEqual(autoOptionalField, autoOptionalMemberAlignment.getAutoOptionalField())
        else:
            self.assertFalse(autoOptionalMemberAlignment.isAutoOptionalFieldUsed())
        self.assertEqual(field, autoOptionalMemberAlignment.getField())

    WITH_AUTO_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE = 96
    WITHOUT_AUTO_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE = 33
