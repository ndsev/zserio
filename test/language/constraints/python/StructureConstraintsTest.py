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
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)

        structureConstraints = self.api.StructureConstraints()
        structureConstraints.read(reader)
        self.assertEqual(self.api.BasicColor.BLACK, structureConstraints.black_color)
        self.assertEqual(self.api.BasicColor.WHITE, structureConstraints.white_color)
        self.assertEqual(self.api.ExtendedColor.PURPLE, structureConstraints.purple_color)

    def testReadWrongBlackConstraint(self):
        writer = zserio.BitStreamWriter()
        self.__class__._write(writer, self.api.BasicColor.RED, self.api.BasicColor.WHITE,
                              self.api.ExtendedColor.PURPLE)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)

        structureConstraints = self.api.StructureConstraints()
        with self.assertRaises(zserio.PythonRuntimeException):
            structureConstraints.read(reader)

    def testReadWrongWhiteConstraint(self):
        writer = zserio.BitStreamWriter()
        self.__class__._write(writer, self.api.BasicColor.BLACK, self.api.BasicColor.RED,
                              self.api.ExtendedColor.PURPLE)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)

        structureConstraints = self.api.StructureConstraints()
        with self.assertRaises(zserio.PythonRuntimeException):
            structureConstraints.read(reader)

    def testReadWrongPurpleConstraint(self):
        writer = zserio.BitStreamWriter()
        self.__class__._write(writer, self.api.BasicColor.BLACK, self.api.BasicColor.WHITE,
                              self.api.ExtendedColor.LIME)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)

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
        self.assertEqual(self.api.BasicColor.BLACK, readStructureConstraints.black_color)
        self.assertEqual(self.api.BasicColor.WHITE, readStructureConstraints.white_color)
        self.assertEqual(self.api.ExtendedColor.PURPLE, readStructureConstraints.purple_color)
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
        writer.write_bits(blackColor.value, 8)
        writer.write_bool(True)
        writer.write_bits(whiteColor.value, 8)
        writer.write_bool(True)
        writer.write_bits(purpleColor.value, 16)
