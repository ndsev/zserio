import unittest
import zserio

from testutils import getZserioApi

class StructureBitmaskConstraintsTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "constraints.zs").structure_bitmask_constraints

    def testReadCorrectConstraints(self):
        writer = zserio.BitStreamWriter()
        availability = (
            self.api.Availability.Values.COORD_X |
            self.api.Availability.Values.COORD_Y |
            self.api.Availability.Values.COORD_Z
        )
        __class__._write(writer, availability, 1, 1, 1)
        reader = zserio.BitStreamReader(writer.getByteArray())

        structureBitmaskConstraints = self.api.StructureBitmaskConstraints()
        structureBitmaskConstraints.read(reader)
        self.assertEqual(1, structureBitmaskConstraints.getCoordX())
        self.assertEqual(1, structureBitmaskConstraints.getCoordY())
        self.assertEqual(1, structureBitmaskConstraints.getCoordZ())

    def testReadWrongCoordZConstraint(self):
        writer = zserio.BitStreamWriter()
        availability = self.api.Availability.Values.COORD_X | self.api.Availability.Values.COORD_Y
        __class__._write(writer, availability, 1, 1, 1)
        reader = zserio.BitStreamReader(writer.getByteArray())

        structureBitmaskConstraints = self.api.StructureBitmaskConstraints()
        with self.assertRaises(zserio.PythonRuntimeException):
            structureBitmaskConstraints.read(reader)

    def testReadWrongCoordYConstraint(self):
        writer = zserio.BitStreamWriter()
        availability = self.api.Availability.Values.COORD_X | self.api.Availability.Values.COORD_Z
        __class__._write(writer, availability, 1, 1, 1)
        reader = zserio.BitStreamReader(writer.getByteArray())

        structureBitmaskConstraints = self.api.StructureBitmaskConstraints()
        with self.assertRaises(zserio.PythonRuntimeException):
            structureBitmaskConstraints.read(reader)

    def testReadWrongCoordXConstraint(self):
        writer = zserio.BitStreamWriter()
        availability = self.api.Availability.Values.COORD_Y | self.api.Availability.Values.COORD_Z
        __class__._write(writer, availability, 1, 1, 1)
        reader = zserio.BitStreamReader(writer.getByteArray())

        structureBitmaskConstraints = self.api.StructureBitmaskConstraints()
        with self.assertRaises(zserio.PythonRuntimeException):
            structureBitmaskConstraints.read(reader)

    def testWriteCorrectConstraints(self):
        structureBitmaskConstraints = self.api.StructureBitmaskConstraints.fromFields(
            self.api.Availability.Values.COORD_X | self.api.Availability.Values.COORD_Y, 1, 1, 0
        )
        writer = zserio.BitStreamWriter()
        structureBitmaskConstraints.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readStructureBitmaskConstraints = self.api.StructureBitmaskConstraints.fromReader(reader)
        self.assertEqual(1, readStructureBitmaskConstraints.getCoordX())
        self.assertEqual(1, readStructureBitmaskConstraints.getCoordY())
        self.assertEqual(0, readStructureBitmaskConstraints.getCoordZ())
        self.assertEqual(structureBitmaskConstraints, readStructureBitmaskConstraints)

    def testWriteWrongCoordZConstraint(self):
        structureBitmaskConstraints = self.api.StructureBitmaskConstraints.fromFields(
            self.api.Availability.Values.COORD_X | self.api.Availability.Values.COORD_Y, 1, 1, 1
        )
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            structureBitmaskConstraints.write(writer)

    def testWriteWrongCoordYConstraint(self):
        structureBitmaskConstraints = self.api.StructureBitmaskConstraints.fromFields(
            self.api.Availability.Values.COORD_X | self.api.Availability.Values.COORD_Z, 1, 1, 1
        )
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            structureBitmaskConstraints.write(writer)

    def testWriteWrongCoordXConstraint(self):
        structureBitmaskConstraints = self.api.StructureBitmaskConstraints.fromFields(
            self.api.Availability.Values.COORD_Y | self.api.Availability.Values.COORD_Z, 1, 1, 1
        )
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            structureBitmaskConstraints.write(writer)

    @staticmethod
    def _write(writer, mask, x, y, z):
        writer.writeBits(mask.getValue(), 3)
        writer.writeBits(x, 8)
        writer.writeBits(y, 8)
        writer.writeBits(z, 8)
