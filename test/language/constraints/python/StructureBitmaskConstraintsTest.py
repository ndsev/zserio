import zserio

import Constraints

class StructureBitmaskConstraintsTest(Constraints.TestCase):
    def testReadCorrectConstraints(self):
        writer = zserio.BitStreamWriter()
        availability = (
            self.api.Availability.Values.COORD_X |
            self.api.Availability.Values.COORD_Y |
            self.api.Availability.Values.COORD_Z
        )
        self.__class__._write(writer, availability, 1, 1, 1)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)

        structureBitmaskConstraints = self.api.StructureBitmaskConstraints()
        structureBitmaskConstraints.read(reader)
        self.assertEqual(1, structureBitmaskConstraints.coord_x)
        self.assertEqual(1, structureBitmaskConstraints.coord_y)
        self.assertEqual(1, structureBitmaskConstraints.coord_z)

    def testReadWrongCoordZConstraint(self):
        writer = zserio.BitStreamWriter()
        availability = self.api.Availability.Values.COORD_X | self.api.Availability.Values.COORD_Y
        self.__class__._write(writer, availability, 1, 1, 1)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)

        structureBitmaskConstraints = self.api.StructureBitmaskConstraints()
        with self.assertRaises(zserio.PythonRuntimeException):
            structureBitmaskConstraints.read(reader)

    def testReadWrongCoordYConstraint(self):
        writer = zserio.BitStreamWriter()
        availability = self.api.Availability.Values.COORD_X | self.api.Availability.Values.COORD_Z
        self.__class__._write(writer, availability, 1, 1, 1)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)

        structureBitmaskConstraints = self.api.StructureBitmaskConstraints()
        with self.assertRaises(zserio.PythonRuntimeException):
            structureBitmaskConstraints.read(reader)

    def testReadWrongCoordXConstraint(self):
        writer = zserio.BitStreamWriter()
        availability = self.api.Availability.Values.COORD_Y | self.api.Availability.Values.COORD_Z
        self.__class__._write(writer, availability, 1, 1, 1)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)

        structureBitmaskConstraints = self.api.StructureBitmaskConstraints()
        with self.assertRaises(zserio.PythonRuntimeException):
            structureBitmaskConstraints.read(reader)

    def testWriteCorrectConstraints(self):
        structureBitmaskConstraints = self.api.StructureBitmaskConstraints(
            self.api.Availability.Values.COORD_X | self.api.Availability.Values.COORD_Y, 1, 1, 0
        )
        bitBuffer = zserio.serialize(structureBitmaskConstraints)
        readStructureBitmaskConstraints = zserio.deserialize(self.api.StructureBitmaskConstraints, bitBuffer)
        self.assertEqual(1, readStructureBitmaskConstraints.coord_x)
        self.assertEqual(1, readStructureBitmaskConstraints.coord_y)
        self.assertEqual(0, readStructureBitmaskConstraints.coord_z)
        self.assertEqual(structureBitmaskConstraints, readStructureBitmaskConstraints)

    def testWriteWrongCoordZConstraint(self):
        structureBitmaskConstraints = self.api.StructureBitmaskConstraints(
            self.api.Availability.Values.COORD_X | self.api.Availability.Values.COORD_Y, 1, 1, 1
        )
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            structureBitmaskConstraints.write(writer)

    def testWriteWrongCoordYConstraint(self):
        structureBitmaskConstraints = self.api.StructureBitmaskConstraints(
            self.api.Availability.Values.COORD_X | self.api.Availability.Values.COORD_Z, 1, 1, 1
        )
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            structureBitmaskConstraints.write(writer)

    def testWriteWrongCoordXConstraint(self):
        structureBitmaskConstraints = self.api.StructureBitmaskConstraints(
            self.api.Availability.Values.COORD_Y | self.api.Availability.Values.COORD_Z, 1, 1, 1
        )
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            structureBitmaskConstraints.write(writer)

    @staticmethod
    def _write(writer, mask, x, y, z):
        writer.write_bits(mask.value, 3)
        writer.write_bits(x, 8)
        writer.write_bits(y, 8)
        writer.write_bits(z, 8)
