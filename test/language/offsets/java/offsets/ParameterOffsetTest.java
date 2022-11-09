package offsets;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.File;

import offsets.parameter_offset.OffsetHolder;
import offsets.parameter_offset.Room;
import offsets.parameter_offset.School;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

public class ParameterOffsetTest
{
    @Test
    public void read() throws IOException, ZserioError
    {
        final boolean writeWrongOffset = false;
        final File file = new File("test.bin");
        writeSchoolToFile(file, writeWrongOffset);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final School school = new School(stream);
        stream.close();
        checkSchool(school);
    }

    @Test
    public void readWrongOffsets() throws IOException, ZserioError
    {
        final boolean writeWrongOffset = true;
        final File file = new File("test.bin");
        writeSchoolToFile(file, writeWrongOffset);
        final BitStreamReader stream = new FileBitStreamReader(file);
        assertThrows(ZserioError.class, () -> new School(stream));
        stream.close();
    }

    @Test
    public void bitSizeOf()
    {
        final boolean createWrongOffset = false;
        final School school = createSchool(createWrongOffset);
        assertEquals(SCHOOL_BIT_SIZE, school.bitSizeOf());
    }

    @Test
    public void bitSizeOfWithPosition()
    {
        final boolean createWrongOffset = false;
        final School school = createSchool(createWrongOffset);
        final int bitPosition = 2;
        assertEquals(SCHOOL_BIT_SIZE + 8 - bitPosition, school.bitSizeOf(bitPosition));
    }

    @Test
    public void initializeOffsets()
    {
        final boolean createWrongOffset = true;
        final School school = createSchool(createWrongOffset);
        final int bitPosition = 0;
        assertEquals(SCHOOL_BIT_SIZE, school.initializeOffsets(bitPosition));
        checkSchool(school);
    }

    @Test
    public void initializeOffsetsWithPosition()
    {
        final boolean createWrongOffset = true;
        final School school = createSchool(createWrongOffset);
        final int bitPosition = 2;
        assertEquals(SCHOOL_BIT_SIZE + 8, school.initializeOffsets(bitPosition));
        checkSchool(school, bitPosition);
    }

    @Test
    public void write() throws IOException, ZserioError
    {
        final boolean createWrongOffset = false;
        final School school = createSchool(createWrongOffset);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        school.write(writer);
        writer.close();
        checkSchool(school);
        final School readSchool = new School(file);
        checkSchool(readSchool);
        assertTrue(school.equals(readSchool));
    }

    @Test
    public void writeWithPosition() throws IOException, ZserioError
    {
        final boolean createWrongOffset = true;
        final School school = createSchool(createWrongOffset);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        final int bitPosition = 2;
        writer.writeBits(0, bitPosition);
        school.initializeOffsets(writer.getBitPosition());
        school.write(writer);
        writer.close();
        checkSchool(school, bitPosition);
    }

    @Test
    public void writeWrongOffset() throws ZserioError, IOException
    {
        final boolean createWrongOffset = true;
        final School school = createSchool(createWrongOffset);
        final BitStreamWriter writer = new ByteArrayBitStreamWriter();
        assertThrows(ZserioError.class, () -> school.write(writer));
        writer.close();
    }

    private void writeSchoolToFile(File file, boolean writeWrongOffset) throws IOException
    {
        final FileBitStreamWriter writer = new FileBitStreamWriter(file);
        writer.writeUnsignedShort(SCHOOL_ID);
        writer.writeUnsignedInt((writeWrongOffset) ? WRONG_ROOM_OFFSET : ROOM_OFFSET);
        writer.writeUnsignedShort(ROOM_ID);
        writer.close();
    }

    private void checkSchool(School school)
    {
        checkSchool(school, 0);
    }

    private void checkSchool(School school, int bitPosition)
    {
        assertEquals(SCHOOL_ID, school.getSchoolId());

        final long expectedRoomOffset = (bitPosition == 0) ? ROOM_OFFSET :
                ROOM_OFFSET + (bitPosition / 8) + 1;
        assertEquals(expectedRoomOffset, school.getOffsetHolder().getRoomOffset());

        assertEquals(ROOM_ID, school.getRoom().getRoomId());
    }

    private School createSchool(boolean createWrongOffset)
    {
        final long roomOffset = (createWrongOffset) ? WRONG_ROOM_OFFSET : ROOM_OFFSET;
        final OffsetHolder offsetHolder = new OffsetHolder(roomOffset);
        final Room room = new Room(offsetHolder, ROOM_ID);

        return new School(SCHOOL_ID, offsetHolder, room);
    }

    private static final int    SCHOOL_ID = 0x01;
    private static final int    ROOM_ID = 0x11;

    private static final long   WRONG_ROOM_OFFSET = 0;
    private static final long   ROOM_OFFSET = 6;

    private static final int    SCHOOL_BIT_SIZE = (int)((ROOM_OFFSET + 2) * 8);
}
