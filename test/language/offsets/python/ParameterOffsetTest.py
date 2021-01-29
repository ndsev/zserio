import unittest
import zserio

from testutils import getZserioApi

class ParameterOffsetTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "offsets.zs").parameter_offset

    def testBitSizeOf(self):
        createWrongOffset = False
        school = self._createSchool(createWrongOffset)
        self.assertEqual(self.SCHOOL_BIT_SIZE, school.bitSizeOf())

    def testBitSizeOfWithPosition(self):
        createWrongOffset = False
        school = self._createSchool(createWrongOffset)
        bitPosition = 2
        self.assertEqual(self.SCHOOL_BIT_SIZE + 8 - bitPosition, school.bitSizeOf(bitPosition))

    def testInitializeOffsets(self):
        createWrongOffset = True
        school = self._createSchool(createWrongOffset)
        bitPosition = 0
        self.assertEqual(self.SCHOOL_BIT_SIZE, school.initializeOffsets(bitPosition))
        self._checkSchool(school)

    def testInitializeOffsetsWithPosition(self):
        createWrongOffset = True
        school = self._createSchool(createWrongOffset)
        bitPosition = 2
        self.assertEqual(self.SCHOOL_BIT_SIZE + 8, school.initializeOffsets(bitPosition))
        self._checkSchool(school, bitPosition)

    def testRead(self):
        writeWrongOffset = False
        writer = zserio.BitStreamWriter()
        self._writeSchoolToStream(writer, writeWrongOffset)
        reader = zserio.BitStreamReader(writer.getByteArray())
        school = self.api.School.fromReader(reader)
        self._checkSchool(school)

    def testReadWrongOffsets(self):
        writeWrongOffset = True
        writer = zserio.BitStreamWriter()
        self._writeSchoolToStream(writer, writeWrongOffset)
        reader = zserio.BitStreamReader(writer.getByteArray())
        with self.assertRaises(zserio.PythonRuntimeException):
            self.api.School.fromReader(reader)

    def testWrite(self):
        createWrongOffset = True
        school = self._createSchool(createWrongOffset)
        writer = zserio.BitStreamWriter()
        school.write(writer)
        self._checkSchool(school)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readSchool = self.api.School.fromReader(reader)
        self._checkSchool(readSchool)
        self.assertTrue(school == readSchool)

    def testWriteWithPosition(self):
        createWrongOffset = True
        school = self._createSchool(createWrongOffset)
        writer = zserio.BitStreamWriter()
        bitPosition = 2
        writer.writeBits(0, bitPosition)
        school.write(writer)
        self._checkSchool(school, bitPosition)

    def testWriteWrongOffset(self):
        createWrongOffset = True
        school = self._createSchool(createWrongOffset)
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            school.write(writer, callInitializeOffsets=False)

    def _writeSchoolToStream(self, writer, writeWrongOffset):
        writer.writeBits(self.SCHOOL_ID, 16)
        writer.writeBits(self.WRONG_ROOM_OFFSET if writeWrongOffset else self.ROOM_OFFSET, 32)
        writer.writeBits(self.ROOM_ID, 16)

    def _checkSchool(self, school, bitPosition=0):
        self.assertEqual(self.SCHOOL_ID, school.getSchoolId())

        expectedRoomOffset = (self.ROOM_OFFSET if (bitPosition == 0) else
                              self.ROOM_OFFSET + (bitPosition // 8) + 1)
        self.assertEqual(expectedRoomOffset, school.getOffsetHolder().getRoomOffset())

        self.assertEqual(self.ROOM_ID, school.getRoom().getRoomId())

    def _createSchool(self, createWrongOffset):
        roomOffset = self.WRONG_ROOM_OFFSET if createWrongOffset else self.ROOM_OFFSET
        offsetHolder = self.api.OffsetHolder(roomOffset)
        room = self.api.Room(offsetHolder, self.ROOM_ID)

        return self.api.School(self.SCHOOL_ID, offsetHolder, room)

    SCHOOL_ID = 0x01
    ROOM_ID = 0x11

    WRONG_ROOM_OFFSET = 0
    ROOM_OFFSET = 6

    SCHOOL_BIT_SIZE = (6 + 2) * 8
