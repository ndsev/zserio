import unittest
import zserio

from testutils import getZserioApi

class SimpleUnionTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "union_types.zs").simple_union

    def testEmptyConstructor(self):
        simpleUnion = self.api.SimpleUnion()
        self.assertEqual(self.api.SimpleUnion.UNDEFINED_CHOICE, simpleUnion.choiceTag())

    def testEmptyConstructorBitSizeof(self):
        simpleUnion = self.api.SimpleUnion()
        with self.assertRaises(zserio.PythonRuntimeException):
            simpleUnion.bitSizeOf()

    def testFromReader(self):
        writer = zserio.BitStreamWriter()
        self._writeSimpleUnionToStream(writer, self.api.SimpleUnion.CHOICE_case1Field)
        reader = zserio.BitStreamReader(writer.getByteArray())
        simpleUnion = self.api.SimpleUnion.fromReader(reader)
        self.assertEqual(self.CASE1_FIELD, simpleUnion.getCase1Field())

        writer = zserio.BitStreamWriter()
        self._writeSimpleUnionToStream(writer, self.api.SimpleUnion.CHOICE_case2Field)
        reader = zserio.BitStreamReader(writer.getByteArray())
        simpleUnion = self.api.SimpleUnion.fromReader(reader)
        self.assertEqual(self.CASE2_FIELD, simpleUnion.getCase2Field())

        writer = zserio.BitStreamWriter()
        self._writeSimpleUnionToStream(writer, self.api.SimpleUnion.CHOICE_case3Field)
        reader = zserio.BitStreamReader(writer.getByteArray())
        simpleUnion = self.api.SimpleUnion.fromReader(reader)
        self.assertEqual(self.CASE3_FIELD, simpleUnion.getCase3Field())

        writer = zserio.BitStreamWriter()
        self._writeSimpleUnionToStream(writer, self.api.SimpleUnion.CHOICE_case4Field)
        reader = zserio.BitStreamReader(writer.getByteArray())
        simpleUnion = self.api.SimpleUnion.fromReader(reader)
        self.assertEqual(self.CASE4_FIELD, simpleUnion.getCase4Field())

    def testEq(self):
        simpleUnion11 = self.api.SimpleUnion()
        simpleUnion12 = self.api.SimpleUnion()
        simpleUnion13 = self.api.SimpleUnion()
        self.assertTrue(simpleUnion11 == simpleUnion12)
        simpleUnion11.setCase1Field(self.CASE1_FIELD)
        simpleUnion12.setCase1Field(self.CASE1_FIELD)
        simpleUnion13.setCase1Field(self.CASE1_FIELD + 1)
        self.assertTrue(simpleUnion11 == simpleUnion12)
        self.assertFalse(simpleUnion11 == simpleUnion13)

        simpleUnion21 = self.api.SimpleUnion()
        simpleUnion21.setCase2Field(self.CASE2_FIELD)
        simpleUnion22 = self.api.SimpleUnion()
        simpleUnion22.setCase2Field(self.CASE2_FIELD)
        simpleUnion23 = self.api.SimpleUnion()
        simpleUnion23.setCase2Field(self.CASE2_FIELD - 1)
        self.assertTrue(simpleUnion21 == simpleUnion22)
        self.assertFalse(simpleUnion21 == simpleUnion23)
        self.assertFalse(simpleUnion21 == simpleUnion11)

        simpleUnion4 = self.api.SimpleUnion()
        simpleUnion4.setCase4Field(self.CASE1_FIELD) # same value as simpleUnion11, but different choice
        self.assertFalse(simpleUnion11 == simpleUnion4)

    def testHash(self):
        simpleUnion1 = self.api.SimpleUnion()
        simpleUnion2 = self.api.SimpleUnion()
        self.assertEqual(hash(simpleUnion1), hash(simpleUnion2))
        simpleUnion1.setCase1Field(self.CASE1_FIELD)
        self.assertNotEqual(hash(simpleUnion1), hash(simpleUnion2))
        simpleUnion2.setCase4Field(self.CASE4_FIELD)
        self.assertNotEqual(hash(simpleUnion1), hash(simpleUnion2))
        simpleUnion2.setCase4Field(self.CASE1_FIELD) # same value as simpleUnion1
        self.assertNotEqual(hash(simpleUnion1), hash(simpleUnion2))
        simpleUnion1.setCase4Field(self.CASE1_FIELD) # same value as simpleUnion2
        self.assertEqual(hash(simpleUnion1), hash(simpleUnion2))

    def testGetSetCase1Field(self):
        simpleUnion = self.api.SimpleUnion()
        simpleUnion.setCase1Field(self.CASE1_FIELD)
        self.assertEqual(self.CASE1_FIELD, simpleUnion.getCase1Field())

    def testGetSetCase2Field(self):
        simpleUnion = self.api.SimpleUnion()
        simpleUnion.setCase2Field(self.CASE2_FIELD)
        self.assertEqual(self.CASE2_FIELD, simpleUnion.getCase2Field())

    def testGetSetCase3Field(self):
        simpleUnion = self.api.SimpleUnion()
        simpleUnion.setCase3Field(self.CASE3_FIELD)
        self.assertEqual(self.CASE3_FIELD, simpleUnion.getCase3Field())

    def testGetSetCase4Field(self):
        simpleUnion = self.api.SimpleUnion()
        simpleUnion.setCase4Field(self.CASE4_FIELD)
        self.assertEqual(self.CASE4_FIELD, simpleUnion.getCase4Field())

    def testChoiceTag(self):
        simpleUnion = self.api.SimpleUnion()
        self.assertEqual(self.api.SimpleUnion.UNDEFINED_CHOICE, simpleUnion.choiceTag())
        simpleUnion.setCase1Field(self.CASE1_FIELD)
        self.assertEqual(self.api.SimpleUnion.CHOICE_case1Field, simpleUnion.choiceTag())
        simpleUnion.setCase2Field(self.CASE2_FIELD)
        self.assertEqual(self.api.SimpleUnion.CHOICE_case2Field, simpleUnion.choiceTag())
        simpleUnion.setCase3Field(self.CASE3_FIELD)
        self.assertEqual(self.api.SimpleUnion.CHOICE_case3Field, simpleUnion.choiceTag())
        simpleUnion.setCase4Field(self.CASE4_FIELD)
        self.assertEqual(self.api.SimpleUnion.CHOICE_case4Field, simpleUnion.choiceTag())

    def testBitSizeOf(self):
        simpleUnion = self.api.SimpleUnion()

        simpleUnion.setCase1Field(self.CASE1_FIELD)
        self.assertEqual(self.UNION_CASE1_BIT_SIZE, simpleUnion.bitSizeOf())

        simpleUnion.setCase2Field(self.CASE2_FIELD)
        self.assertEqual(self.UNION_CASE2_BIT_SIZE, simpleUnion.bitSizeOf())

        simpleUnion.setCase3Field(self.CASE3_FIELD)
        self.assertEqual(self.UNION_CASE3_BIT_SIZE, simpleUnion.bitSizeOf())

        simpleUnion.setCase4Field(self.CASE4_FIELD)
        self.assertEqual(self.UNION_CASE4_BIT_SIZE, simpleUnion.bitSizeOf())

    def testInitializeOffsets(self):
        bitPosition = 1
        simpleUnion = self.api.SimpleUnion()
        simpleUnion.setCase1Field(self.CASE1_FIELD)
        self.assertEqual(bitPosition + self.UNION_CASE1_BIT_SIZE, simpleUnion.initializeOffsets(bitPosition))

        simpleUnion.setCase2Field(self.CASE2_FIELD)
        self.assertEqual(bitPosition + self.UNION_CASE2_BIT_SIZE, simpleUnion.initializeOffsets(bitPosition))

        simpleUnion.setCase3Field(self.CASE3_FIELD)
        self.assertEqual(bitPosition + self.UNION_CASE3_BIT_SIZE, simpleUnion.initializeOffsets(bitPosition))

        simpleUnion.setCase4Field(self.CASE4_FIELD)
        self.assertEqual(bitPosition + self.UNION_CASE4_BIT_SIZE, simpleUnion.initializeOffsets(bitPosition))

    def testRead(self):
        writer = zserio.BitStreamWriter()
        self._writeSimpleUnionToStream(writer, self.api.SimpleUnion.CHOICE_case1Field)
        reader = zserio.BitStreamReader(writer.getByteArray())
        simpleUnion = self.api.SimpleUnion()
        simpleUnion.read(reader)
        self.assertEqual(self.CASE1_FIELD, simpleUnion.getCase1Field())

        writer = zserio.BitStreamWriter()
        self._writeSimpleUnionToStream(writer, self.api.SimpleUnion.CHOICE_case2Field)
        reader = zserio.BitStreamReader(writer.getByteArray())
        simpleUnion = self.api.SimpleUnion()
        simpleUnion.read(reader)
        self.assertEqual(self.CASE2_FIELD, simpleUnion.getCase2Field())

        writer = zserio.BitStreamWriter()
        self._writeSimpleUnionToStream(writer, self.api.SimpleUnion.CHOICE_case3Field)
        reader = zserio.BitStreamReader(writer.getByteArray())
        simpleUnion = self.api.SimpleUnion()
        simpleUnion.read(reader)
        self.assertEqual(self.CASE3_FIELD, simpleUnion.getCase3Field())

        writer = zserio.BitStreamWriter()
        self._writeSimpleUnionToStream(writer, self.api.SimpleUnion.CHOICE_case4Field)
        reader = zserio.BitStreamReader(writer.getByteArray())
        simpleUnion = self.api.SimpleUnion()
        simpleUnion.read(reader)
        self.assertEqual(self.CASE4_FIELD, simpleUnion.getCase4Field())

    def testWrite(self):
        simpleUnion = self.api.SimpleUnion()
        simpleUnion.setCase1Field(self.CASE1_FIELD)
        writer = zserio.BitStreamWriter()
        simpleUnion.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readSimpleUnion = self.api.SimpleUnion.fromReader(reader)
        self.assertTrue(simpleUnion == readSimpleUnion)

        simpleUnion = self.api.SimpleUnion()
        simpleUnion.setCase2Field(self.CASE2_FIELD)
        writer = zserio.BitStreamWriter()
        simpleUnion.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readSimpleUnion = self.api.SimpleUnion.fromReader(reader)
        self.assertTrue(simpleUnion == readSimpleUnion)

        simpleUnion = self.api.SimpleUnion()
        simpleUnion.setCase3Field(self.CASE3_FIELD)
        writer = zserio.BitStreamWriter()
        simpleUnion.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readSimpleUnion = self.api.SimpleUnion.fromReader(reader)
        self.assertTrue(simpleUnion == readSimpleUnion)

        simpleUnion = self.api.SimpleUnion()
        simpleUnion.setCase4Field(self.CASE4_FIELD)
        writer = zserio.BitStreamWriter()
        simpleUnion.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readSimpleUnion = self.api.SimpleUnion.fromReader(reader)
        self.assertTrue(simpleUnion == readSimpleUnion)

    def _writeSimpleUnionToStream(self, writer, choiceTag):
        writer.writeVarSize(choiceTag)
        if choiceTag == self.api.SimpleUnion.CHOICE_case1Field:
            writer.writeSignedBits(self.CASE1_FIELD, 8)
        elif choiceTag == self.api.SimpleUnion.CHOICE_case2Field:
            writer.writeBits(self.CASE2_FIELD, 16)
        elif choiceTag == self.api.SimpleUnion.CHOICE_case3Field:
            writer.writeString(self.CASE3_FIELD)
        elif choiceTag == self.api.SimpleUnion.CHOICE_case4Field:
            writer.writeSignedBits(self.CASE4_FIELD, 8)
        else:
            raise zserio.PythonRuntimeException("_writeSimpleUnionToStream - unknown choiceTag %d!" % choiceTag)

    CASE1_FIELD = 13
    CASE2_FIELD = 65535
    CASE3_FIELD = "SimpleUnion"
    CASE4_FIELD = 42

    UNION_CASE1_BIT_SIZE = 8 + 8
    UNION_CASE2_BIT_SIZE = 8 + 16
    UNION_CASE3_BIT_SIZE = 8 + zserio.bitsizeof.getBitSizeOfString(CASE3_FIELD)
    UNION_CASE4_BIT_SIZE = 8 + 8
