import unittest
import zserio

from testutils import getZserioApi

class StructureConstraintsTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "constraints.zs").structure_constraints

    def testReadCorrectColors(self):
        writer = zserio.BitStreamWriter()
        self.__class__._write(writer, self.api.BasicColor.BLACK, self.api.BasicColor.WHITE,
                              self.api.ExtendedColor.PURPLE)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())

        structureConstraints = self.api.StructureConstraints()
        structureConstraints.read(reader)
        self.assertEqual(self.api.BasicColor.BLACK, structureConstraints.getBlackColor())
        self.assertEqual(self.api.BasicColor.WHITE, structureConstraints.getWhiteColor())
        self.assertEqual(self.api.ExtendedColor.PURPLE, structureConstraints.getPurpleColor())

    def testReadWrongBlackConstraint(self):
        writer = zserio.BitStreamWriter()
        self.__class__._write(writer, self.api.BasicColor.RED, self.api.BasicColor.WHITE,
                              self.api.ExtendedColor.PURPLE)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())

        structureConstraints = self.api.StructureConstraints()
        with self.assertRaises(zserio.PythonRuntimeException):
            structureConstraints.read(reader)

    def testReadWrongWhiteConstraint(self):
        writer = zserio.BitStreamWriter()
        self.__class__._write(writer, self.api.BasicColor.BLACK, self.api.BasicColor.RED,
                              self.api.ExtendedColor.PURPLE)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())

        structureConstraints = self.api.StructureConstraints()
        with self.assertRaises(zserio.PythonRuntimeException):
            structureConstraints.read(reader)

    def testReadWrongPurpleConstraint(self):
        writer = zserio.BitStreamWriter()
        self.__class__._write(writer, self.api.BasicColor.BLACK, self.api.BasicColor.WHITE,
                              self.api.ExtendedColor.LIME)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())

        structureConstraints = self.api.StructureConstraints()
        with self.assertRaises(zserio.PythonRuntimeException):
            structureConstraints.read(reader)

    def testWriteCorrectConstraints(self):
        structureConstraints = self.api.StructureConstraints(self.api.BasicColor.BLACK,
                                                             self.api.BasicColor.WHITE,
                                                             True,
                                                             self.api.ExtendedColor.PURPLE)
        bitBuffer = zserio.serialize(structureConstraints)
        readStructureConstraints = zserio.deserialize(self.api.StructureConstraints, bitBuffer)
        self.assertEqual(self.api.BasicColor.BLACK, readStructureConstraints.getBlackColor())
        self.assertEqual(self.api.BasicColor.WHITE, readStructureConstraints.getWhiteColor())
        self.assertEqual(self.api.ExtendedColor.PURPLE, readStructureConstraints.getPurpleColor())
        self.assertEqual(structureConstraints, readStructureConstraints)

    def testWriteWrongBlackConstraint(self):
        structureConstraints = self.api.StructureConstraints(self.api.BasicColor.RED,
                                                             self.api.BasicColor.WHITE,
                                                             True,
                                                             self.api.ExtendedColor.PURPLE)
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            structureConstraints.write(writer)

    def testWriteWrongWhiteConstraint(self):
        structureConstraints = self.api.StructureConstraints(self.api.BasicColor.BLACK,
                                                             self.api.BasicColor.RED,
                                                             True,
                                                             self.api.ExtendedColor.PURPLE)
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            structureConstraints.write(writer)

    def testWriteWrongPurpleConstraint(self):
        structureConstraints = self.api.StructureConstraints(self.api.BasicColor.BLACK,
                                                             self.api.BasicColor.WHITE,
                                                             True,
                                                             self.api.ExtendedColor.LIME)
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            structureConstraints.write(writer)

    @staticmethod
    def _write(writer, blackColor, whiteColor, purpleColor):
        writer.writeBits(blackColor.value, 8)
        writer.writeBool(True)
        writer.writeBits(whiteColor.value, 8)
        writer.writeBool(True)
        writer.writeBits(purpleColor.value, 16)
