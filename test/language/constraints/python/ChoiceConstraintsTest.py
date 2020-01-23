import unittest
import zserio

from testutils import getZserioApi

class ChoiceConstraintsTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "constraints.zs").choice_constraints

    def testReadCorrectConstraints(self):
        selector = True
        value8 = self.VALUE8_CORRECT_CONSTRAINT
        writer = zserio.BitStreamWriter()
        self.__class__._write(writer, selector, value8, 0)
        reader = zserio.BitStreamReader(writer.getByteArray())

        choiceConstraints = self.api.ChoiceConstraints(selector)
        choiceConstraints.read(reader)
        self.assertEqual(selector, choiceConstraints.getSelector())
        self.assertEqual(value8, choiceConstraints.getValue8())

    def testReadWrongValue8Constraint(self):
        selector = True
        value8 = self.VALUE8_WRONG_CONSTRAINT
        writer = zserio.BitStreamWriter()
        self.__class__._write(writer, selector, value8, 0)
        reader = zserio.BitStreamReader(writer.getByteArray())

        choiceConstraints = self.api.ChoiceConstraints(selector)
        with self.assertRaises(zserio.PythonRuntimeException):
            choiceConstraints.read(reader)

    def testReadWrongValue16Constraint(self):
        selector = False
        value16 = self.VALUE16_WRONG_CONSTRAINT
        writer = zserio.BitStreamWriter()
        self.__class__._write(writer, selector, 0, value16)
        reader = zserio.BitStreamReader(writer.getByteArray())

        choiceConstraints = self.api.ChoiceConstraints(selector)
        with self.assertRaises(zserio.PythonRuntimeException):
            choiceConstraints.read(reader)

    def testWriteCorrectConstraints(self):
        selector = False
        value16 = self.VALUE16_CORRECT_CONSTRAINT
        choiceConstraints = self.api.ChoiceConstraints(selector)
        choiceConstraints.setValue16(value16)

        writer = zserio.BitStreamWriter()
        choiceConstraints.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readChoiceConstraints = self.api.ChoiceConstraints.fromReader(reader, selector)
        self.assertEqual(selector, readChoiceConstraints.getSelector())
        self.assertEqual(value16, readChoiceConstraints.getValue16())
        self.assertEqual(choiceConstraints, readChoiceConstraints)

    def testWriteWrongValue8Constraint(self):
        selector = True
        value8 = self.VALUE8_WRONG_CONSTRAINT
        choiceConstraints = self.api.ChoiceConstraints(selector)
        choiceConstraints.setValue8(value8)

        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            choiceConstraints.write(writer)

    def testWriteWrongValue16Constraint(self):
        selector = False
        value16 = self.VALUE16_WRONG_CONSTRAINT
        choiceConstraints = self.api.ChoiceConstraints(selector)
        choiceConstraints.setValue16(value16)

        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            choiceConstraints.write(writer)

    @staticmethod
    def _write(writer, selector, value8, value16):
        writer.writeBits(value8 if selector else value16, 8)

    VALUE8_CORRECT_CONSTRAINT = 1
    VALUE8_WRONG_CONSTRAINT = 0

    VALUE16_CORRECT_CONSTRAINT = 256
    VALUE16_WRONG_CONSTRAINT = 255
