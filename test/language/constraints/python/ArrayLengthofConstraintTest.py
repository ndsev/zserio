import unittest
import zserio

from testutils import getZserioApi

class ArrayLengthofConstraintTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "constraints.zs").array_lengthof_constraint

    def testReadCorrectLength(self):
        writer = zserio.BitStreamWriter()
        __class__._write(writer, self.CORRECT_LENGTH)
        reader = zserio.BitStreamReader(writer.getByteArray())

        arrayLengthofConstraint = self.api.ArrayLengthofConstraint()
        arrayLengthofConstraint.read(reader)
        self.assertEqual(self.CORRECT_LENGTH, len(arrayLengthofConstraint.getArray()))

    def testReadWrongLengthLess(self):
        writer = zserio.BitStreamWriter()
        __class__._write(writer, self.WRONG_LENGTH_LESS)
        reader = zserio.BitStreamReader(writer.getByteArray())

        arrayLengthofConstraint = self.api.ArrayLengthofConstraint()
        with self.assertRaises(zserio.PythonRuntimeException):
            arrayLengthofConstraint.read(reader)

    def testReadWrongLengthGreater(self):
        writer = zserio.BitStreamWriter()
        __class__._write(writer, self.WRONG_LENGTH_GREATER)
        reader = zserio.BitStreamReader(writer.getByteArray())

        arrayLengthofConstraint = self.api.ArrayLengthofConstraint()
        with self.assertRaises(zserio.PythonRuntimeException):
            arrayLengthofConstraint.read(reader)

    def testWriteCorrectConstraints(self):
        arrayLengthofConstraint = self.api.ArrayLengthofConstraint()
        arrayLengthofConstraint.setArray([i for i in range(self.CORRECT_LENGTH)])

        writer = zserio.BitStreamWriter()
        arrayLengthofConstraint.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readArrayLengthofConstraint = self.api.ArrayLengthofConstraint.fromReader(reader)
        self.assertEqual(self.CORRECT_LENGTH, len(readArrayLengthofConstraint.getArray()))
        self.assertEqual(arrayLengthofConstraint, readArrayLengthofConstraint)

    def testWriteWrongLengthLess(self):
        arrayLengthofConstraint = self.api.ArrayLengthofConstraint()
        arrayLengthofConstraint.setArray([i for i in range(self.WRONG_LENGTH_LESS)])

        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            arrayLengthofConstraint.write(writer)

    def testWriteWrongLengthGreater(self):
        arrayLengthofConstraint = self.api.ArrayLengthofConstraint()
        arrayLengthofConstraint.setArray([i for i in range(self.WRONG_LENGTH_GREATER)])

        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            arrayLengthofConstraint.write(writer)

    @staticmethod
    def _write(writer, length):
        writer.writeBits(length, 8) # all lengths in this test fits in a single byte
        for i in range(length):
            writer.writeBits(i, 32)

    CORRECT_LENGTH = 6
    WRONG_LENGTH_LESS = 3
    WRONG_LENGTH_GREATER = 12
