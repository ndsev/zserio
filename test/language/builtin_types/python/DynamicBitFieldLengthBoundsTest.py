import zserio

import BuiltInTypes


class DynamicBitFieldLengthBoundsTest(BuiltInTypes.TestCase):
    def testWriteRead(self):
        container = self.api.Container(
            self.UNSIGNED_BIT_LENGTH,
            self.UNSIGNED_VALUE,
            self.UNSIGNED_BIG_BIT_LENGTH,
            self.UNSIGNED_BIG_VALUE,
            self.SIGNED_BIT_LENGTH,
            self.SIGNED_VALUE,
        )

        bitBuffer = zserio.serialize(container)
        readContainer = zserio.deserialize(self.api.Container, bitBuffer)
        self.assertEqual(container, readContainer)

        byteArray = self._writeContainer(container)
        reader = zserio.BitStreamReader(byteArray)
        readContainer2 = self.api.Container.from_reader(reader)
        self.assertEqual(container, readContainer2)

    def testUnsignedBitLengthZero(self):
        container = self.api.Container(
            0,
            self.UNSIGNED_VALUE,
            self.UNSIGNED_BIG_BIT_LENGTH,
            self.UNSIGNED_BIG_VALUE,
            self.SIGNED_BIT_LENGTH,
            self.SIGNED_VALUE,
        )
        with self.assertRaises(zserio.PythonRuntimeException):
            zserio.serialize(container)

        byteArray = self._writeContainer(container)
        reader = zserio.BitStreamReader(byteArray)
        with self.assertRaises(zserio.PythonRuntimeException):
            self.api.Container.from_reader(reader)

    def testUnsignedBitLengthZeroValueZero(self):
        container = self.api.Container(
            0,
            0,
            self.UNSIGNED_BIG_BIT_LENGTH,
            self.UNSIGNED_BIG_VALUE,
            self.SIGNED_BIT_LENGTH,
            self.SIGNED_VALUE,
        )
        with self.assertRaises(zserio.PythonRuntimeException):
            zserio.serialize(container)

        byteArray = self._writeContainer(container)
        reader = zserio.BitStreamReader(byteArray)
        with self.assertRaises(zserio.PythonRuntimeException):
            self.api.Container.from_reader(reader)

    def testUnsignedBigBitLengthZero(self):
        container = self.api.Container(
            self.UNSIGNED_BIT_LENGTH,
            self.UNSIGNED_VALUE,
            0,
            self.UNSIGNED_BIG_VALUE,
            self.SIGNED_BIT_LENGTH,
            self.SIGNED_VALUE,
        )
        with self.assertRaises(zserio.PythonRuntimeException):
            zserio.serialize(container)

        byteArray = self._writeContainer(container)
        reader = zserio.BitStreamReader(byteArray)
        with self.assertRaises(zserio.PythonRuntimeException):
            self.api.Container.from_reader(reader)

    def testUnsignedBigBitLengthZeroValueZero(self):
        container = self.api.Container(
            self.UNSIGNED_BIT_LENGTH,
            self.UNSIGNED_VALUE,
            0,
            0,
            self.SIGNED_BIT_LENGTH,
            self.SIGNED_VALUE,
        )
        with self.assertRaises(zserio.PythonRuntimeException):
            zserio.serialize(container)

        byteArray = self._writeContainer(container)
        reader = zserio.BitStreamReader(byteArray)
        with self.assertRaises(zserio.PythonRuntimeException):
            self.api.Container.from_reader(reader)

    def testUnsignedBigBitLengthOverMax(self):
        container = self.api.Container(
            self.UNSIGNED_BIT_LENGTH,
            self.UNSIGNED_VALUE,
            65,
            self.UNSIGNED_BIG_VALUE,
            self.SIGNED_BIT_LENGTH,
            self.SIGNED_VALUE,
        )
        with self.assertRaises(zserio.PythonRuntimeException):
            zserio.serialize(container)

        byteArray = self._writeContainer(container)
        reader = zserio.BitStreamReader(byteArray)
        with self.assertRaises(zserio.PythonRuntimeException):
            self.api.Container.from_reader(reader)

    def testSignedBitLengthZero(self):
        container = self.api.Container(
            self.UNSIGNED_BIT_LENGTH,
            self.UNSIGNED_VALUE,
            self.UNSIGNED_BIG_BIT_LENGTH,
            self.UNSIGNED_BIG_VALUE,
            0,
            self.SIGNED_VALUE,
        )
        with self.assertRaises(zserio.PythonRuntimeException):
            zserio.serialize(container)

        byteArray = self._writeContainer(container)
        reader = zserio.BitStreamReader(byteArray)
        with self.assertRaises(zserio.PythonRuntimeException):
            self.api.Container.from_reader(reader)

    def testSignedBitLengthZeroValueZero(self):
        container = self.api.Container(
            self.UNSIGNED_BIT_LENGTH,
            self.UNSIGNED_VALUE,
            self.UNSIGNED_BIG_BIT_LENGTH,
            self.UNSIGNED_BIG_VALUE,
            0,
            0,
        )
        with self.assertRaises(zserio.PythonRuntimeException):
            zserio.serialize(container)

        byteArray = self._writeContainer(container)
        reader = zserio.BitStreamReader(byteArray)
        with self.assertRaises(zserio.PythonRuntimeException):
            self.api.Container.from_reader(reader)

    def testSignedBitLengthOverMax(self):
        container = self.api.Container(
            self.UNSIGNED_BIT_LENGTH,
            self.UNSIGNED_VALUE,
            self.UNSIGNED_BIG_BIT_LENGTH,
            self.UNSIGNED_BIG_VALUE,
            65,
            self.SIGNED_VALUE,
        )
        with self.assertRaises(zserio.PythonRuntimeException):
            zserio.serialize(container)

        byteArray = self._writeContainer(container)
        reader = zserio.BitStreamReader(byteArray)
        with self.assertRaises(zserio.PythonRuntimeException):
            self.api.Container.from_reader(reader)

    @staticmethod
    def _writeContainer(container):
        writer = zserio.BitStreamWriter()
        writer.write_bits(container.unsigned_bit_length, 4)
        if container.unsigned_bit_length == 0:
            return writer.byte_array
        writer.write_bits(container.unsigned_value, container.unsigned_bit_length)
        writer.write_bits(container.unsigned_big_bit_length, 8)
        if container.unsigned_big_bit_length < 1 or container.unsigned_big_bit_length > 64:
            return writer.byte_array
        writer.write_bits(container.unsigned_big_value, container.unsigned_big_bit_length)
        writer.write_bits(container.signed_bit_length, 64)
        if container.signed_bit_length < 1 or container.signed_bit_length > 64:
            return writer.byte_array
        writer.write_signed_bits(container.signed_value, container.signed_bit_length)

        return writer.byte_array

    UNSIGNED_BIT_LENGTH = 15
    UNSIGNED_VALUE = (1 << UNSIGNED_BIT_LENGTH) - 1
    UNSIGNED_BIG_BIT_LENGTH = 13
    UNSIGNED_BIG_VALUE = (1 << UNSIGNED_BIG_BIT_LENGTH) - 1
    SIGNED_BIT_LENGTH = 7
    SIGNED_VALUE = -(1 << (SIGNED_BIT_LENGTH - 1))
