import zserio

import Constraints


class ArrayLengthofConstraintTest(Constraints.TestCase):
    def testReadCorrectLength(self):
        writer = zserio.BitStreamWriter()
        self.__class__._write(writer, self.CORRECT_LENGTH)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)

        arrayLengthofConstraint = self.api.ArrayLengthofConstraint()
        arrayLengthofConstraint.read(reader)
        self.assertEqual(self.CORRECT_LENGTH, len(arrayLengthofConstraint.array))

    def testReadWrongLengthLess(self):
        writer = zserio.BitStreamWriter()
        self.__class__._write(writer, self.WRONG_LENGTH_LESS)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)

        arrayLengthofConstraint = self.api.ArrayLengthofConstraint()
        with self.assertRaises(zserio.PythonRuntimeException):
            arrayLengthofConstraint.read(reader)

    def testReadWrongLengthGreater(self):
        writer = zserio.BitStreamWriter()
        self.__class__._write(writer, self.WRONG_LENGTH_GREATER)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)

        arrayLengthofConstraint = self.api.ArrayLengthofConstraint()
        with self.assertRaises(zserio.PythonRuntimeException):
            arrayLengthofConstraint.read(reader)

    def testWriteCorrectConstraints(self):
        arrayLengthofConstraint = self.api.ArrayLengthofConstraint()
        arrayLengthofConstraint.array = list(range(self.CORRECT_LENGTH))

        bitBuffer = zserio.serialize(arrayLengthofConstraint)
        readArrayLengthofConstraint = zserio.deserialize(self.api.ArrayLengthofConstraint, bitBuffer)
        self.assertEqual(self.CORRECT_LENGTH, len(readArrayLengthofConstraint.array))
        self.assertEqual(arrayLengthofConstraint, readArrayLengthofConstraint)

    def testWriteWrongLengthLess(self):
        arrayLengthofConstraint = self.api.ArrayLengthofConstraint()
        arrayLengthofConstraint.array = list(range(self.WRONG_LENGTH_LESS))

        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            arrayLengthofConstraint.write(writer)

    def testWriteWrongLengthGreater(self):
        arrayLengthofConstraint = self.api.ArrayLengthofConstraint()
        arrayLengthofConstraint.array = list(range(self.WRONG_LENGTH_GREATER))

        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            arrayLengthofConstraint.write(writer)

    @staticmethod
    def _write(writer, length):
        writer.write_bits(length, 8)  # all lengths in this test fits in a single byte
        for i in range(length):
            writer.write_bits(i, 32)

    CORRECT_LENGTH = 6
    WRONG_LENGTH_LESS = 3
    WRONG_LENGTH_GREATER = 12
