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
        self.assertEqual(self.SCHOOL_BIT_SIZE, school.bitsizeof())

    def testBitSizeOfWithPosition(self):
        createWrongOffset = False
        school = self._createSchool(createWrongOffset)
        bitPosition = 2
        self.assertEqual(self.SCHOOL_BIT_SIZE + 8 - bitPosition, school.bitsizeof(bitPosition))

    def testInitializeOffsets(self):
        createWrongOffset = True
        school = self._createSchool(createWrongOffset)
        bitPosition = 0
        self.assertEqual(self.SCHOOL_BIT_SIZE, school.initialize_offsets(bitPosition))
        self._checkSchool(school)

    def testInitializeOffsetsWithPosition(self):
        createWrongOffset = True
        school = self._createSchool(createWrongOffset)
        bitPosition = 2
        self.assertEqual(self.SCHOOL_BIT_SIZE + 8, school.initialize_offsets(bitPosition))
        self._checkSchool(school, bitPosition)

    def testRead(self):
        writeWrongOffset = False
        writer = zserio.BitStreamWriter()
        self._writeSchoolToStream(writer, writeWrongOffset)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        school = self.api.School.from_reader(reader)
        self._checkSchool(school)

    def testReadWrongOffsets(self):
        writeWrongOffset = True
        writer = zserio.BitStreamWriter()
        self._writeSchoolToStream(writer, writeWrongOffset)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        with self.assertRaises(zserio.PythonRuntimeException):
            self.api.School.from_reader(reader)

    def testWrite(self):
        createWrongOffset = True
        school = self._createSchool(createWrongOffset)
        writer = zserio.BitStreamWriter()
        school.initialize_offsets(writer.bitposition)
        school.write(writer)
        self._checkSchool(school)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readSchool = self.api.School.from_reader(reader)
        self._checkSchool(readSchool)
        self.assertTrue(school == readSchool)

    def testWriteWithPosition(self):
        createWrongOffset = True
        school = self._createSchool(createWrongOffset)
        writer = zserio.BitStreamWriter()
        bitPosition = 2
        writer.write_bits(0, bitPosition)
        school.initialize_offsets(writer.bitposition)
        school.write(writer)
        self._checkSchool(school, bitPosition)

    def testWriteWrongOffset(self):
        createWrongOffset = True
        school = self._createSchool(createWrongOffset)
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            school.write(writer)

    def _writeSchoolToStream(self, writer, writeWrongOffset):
        writer.write_bits(self.SCHOOL_ID, 16)
        writer.write_bits(self.WRONG_ROOM_OFFSET if writeWrongOffset else self.ROOM_OFFSET, 32)
        writer.write_bits(self.ROOM_ID, 16)

    def _checkSchool(self, school, bitPosition=0):
        self.assertEqual(self.SCHOOL_ID, school.school_id)

        expectedRoomOffset = (self.ROOM_OFFSET if (bitPosition == 0) else
                              self.ROOM_OFFSET + (bitPosition // 8) + 1)
        self.assertEqual(expectedRoomOffset, school.offset_holder.room_offset)

        self.assertEqual(self.ROOM_ID, school.room.room_id)

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
