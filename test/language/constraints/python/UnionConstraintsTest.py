import unittest
import zserio

from testutils import getZserioApi

class UnionConstraintsTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "constraints.zs").union_constraints

    def testReadCorrectConstraints(self):
        value8 = self.VALUE8_CORRECT_CONSTRAINT
        writer = zserio.BitStreamWriter()
        self._writeValue8(writer, value8)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)

        unionConstraints = self.api.UnionConstraints()
        unionConstraints.read(reader)
        self.assertEqual(self.api.UnionConstraints.CHOICE_value8, unionConstraints.choice_tag)
        self.assertEqual(value8, unionConstraints.value8)

    def testReadWrongValue8Constraint(self):
        value8 = self.VALUE8_WRONG_CONSTRAINT
        writer = zserio.BitStreamWriter()
        self._writeValue8(writer, value8)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)

        unionConstraints = self.api.UnionConstraints()
        with self.assertRaises(zserio.PythonRuntimeException):
            unionConstraints.read(reader)

    def testReadWrongValue16Constraint(self):
        value16 = self.VALUE16_WRONG_CONSTRAINT
        writer = zserio.BitStreamWriter()
        self._writeValue16(writer, value16)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)

        unionConstraints = self.api.UnionConstraints()
        with self.assertRaises(zserio.PythonRuntimeException):
            unionConstraints.read(reader)

    def testWriteCorrectConstraints(self):
        value16 = self.VALUE16_CORRECT_CONSTRAINT
        unionConstraints = self.api.UnionConstraints()
        unionConstraints.value16 = value16

        bitBuffer = zserio.serialize(unionConstraints)
        readUnionConstraints = zserio.deserialize(self.api.UnionConstraints, bitBuffer)
        self.assertEqual(self.api.UnionConstraints.CHOICE_value16, readUnionConstraints.choice_tag)
        self.assertEqual(value16, readUnionConstraints.value16)
        self.assertEqual(unionConstraints, readUnionConstraints)

    def testWriteWrongValue8Constraint(self):
        value8 = self.VALUE8_WRONG_CONSTRAINT
        unionConstraints = self.api.UnionConstraints()
        unionConstraints.value8 = value8

        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            unionConstraints.write(writer)

    def testWriteWrongValue16Constraint(self):
        value16 = self.VALUE16_WRONG_CONSTRAINT
        unionConstraints = self.api.UnionConstraints()
        unionConstraints.value16 = value16

        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            unionConstraints.write(writer)

    def _writeValue8(self, writer, value8):
        writer.write_varsize(self.api.UnionConstraints.CHOICE_value8)
        writer.write_bits(value8, 8)

    def _writeValue16(self, writer, value16):
        writer.write_varsize(self.api.UnionConstraints.CHOICE_value16)
        writer.write_bits(value16, 8)

    VALUE8_CORRECT_CONSTRAINT = 1
    VALUE8_WRONG_CONSTRAINT = 0

    VALUE16_CORRECT_CONSTRAINT = 256
    VALUE16_WRONG_CONSTRAINT = 255
