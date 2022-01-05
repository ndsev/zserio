package constraints;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import constraints.structure_bitmask_constraints.Availability;
import constraints.structure_bitmask_constraints.StructureBitmaskConstraints;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

public class StructureBitmaskConstraintsTest
{
    @Test
    public void readCorrectConstraints() throws IOException, ZserioError
    {
        final File file = new File("test.bin");
        final Availability availability = Availability.Values.COORD_X.or(Availability.Values.COORD_Y).or(
                Availability.Values.COORD_Z);
        writeStructureBitmaskConstraintsToFile(file, availability, (short)1, (short)1, (short)1);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final StructureBitmaskConstraints structureBitmaskConstraints = new StructureBitmaskConstraints(stream);
        stream.close();
        assertEquals(1, structureBitmaskConstraints.getCoordX());
        assertEquals(1, structureBitmaskConstraints.getCoordY());
        assertEquals(1, structureBitmaskConstraints.getCoordZ());
    }

    @Test
    public void readWrongCoordZConstraint() throws IOException, ZserioError
    {
        final File file = new File("test.bin");
        final Availability availability = Availability.Values.COORD_X.or(Availability.Values.COORD_Y);
        writeStructureBitmaskConstraintsToFile(file, availability, (short)1, (short)1, (short)1);
        final BitStreamReader stream = new FileBitStreamReader(file);
        assertThrows(ZserioError.class, () -> new StructureBitmaskConstraints(stream));
        stream.close();
    }

    @Test
    public void readWrongCoordYConstraint() throws IOException, ZserioError
    {
        final File file = new File("test.bin");
        final Availability availability = Availability.Values.COORD_X.or(Availability.Values.COORD_Z);
        writeStructureBitmaskConstraintsToFile(file, availability, (short)1, (short)1, (short)1);
        final BitStreamReader stream = new FileBitStreamReader(file);
        assertThrows(ZserioError.class, () -> new StructureBitmaskConstraints(stream));
        stream.close();
    }

    @Test
    public void readWrongCoordXConstraint() throws IOException, ZserioError
    {
        final File file = new File("test.bin");
        final Availability availability = Availability.Values.COORD_Y.or(Availability.Values.COORD_Z);
        writeStructureBitmaskConstraintsToFile(file, availability, (short)1, (short)1, (short)1);
        final BitStreamReader stream = new FileBitStreamReader(file);
        assertThrows(ZserioError.class, () -> new StructureBitmaskConstraints(stream));
        stream.close();
    }

    @Test
    public void writeCorrectStructureBitmaskConstraints() throws IOException, ZserioError
    {
        final StructureBitmaskConstraints structureBitmaskConstraints =
                new StructureBitmaskConstraints(Availability.Values.COORD_X.or(Availability.Values.COORD_Y),
                        (short)1, (short)1, (short)0);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        structureBitmaskConstraints.write(writer);
        writer.close();
        final StructureBitmaskConstraints readStructureBitmaskConstraints = new StructureBitmaskConstraints(file);
        assertEquals(1, readStructureBitmaskConstraints.getCoordX());
        assertEquals(1, readStructureBitmaskConstraints.getCoordY());
        assertEquals(0, readStructureBitmaskConstraints.getCoordZ());
        assertTrue(structureBitmaskConstraints.equals(readStructureBitmaskConstraints));
    }

    @Test
    public void writeWrongCoordZConstraint() throws IOException, ZserioError
    {
        final StructureBitmaskConstraints structureBitmaskConstraints =
                new StructureBitmaskConstraints(Availability.Values.COORD_X.or(Availability.Values.COORD_Y),
                        (short)1, (short)1, (short)1);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        assertThrows(ZserioError.class, () -> structureBitmaskConstraints.write(writer));
        writer.close();
    }

    @Test
    public void writeWrongCoordYConstraint() throws IOException, ZserioError
    {
        final StructureBitmaskConstraints structureBitmaskConstraints =
                new StructureBitmaskConstraints(Availability.Values.COORD_X.or(Availability.Values.COORD_Z),
                        (short)1, (short)1, (short)1);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        assertThrows(ZserioError.class, () -> structureBitmaskConstraints.write(writer));
        writer.close();
    }

    @Test
    public void writeWrongCoordXConstraint() throws IOException, ZserioError
    {
        final StructureBitmaskConstraints structureBitmaskConstraints =
                new StructureBitmaskConstraints(Availability.Values.COORD_Y.or(Availability.Values.COORD_Z),
                        (short)1, (short)1, (short)1);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        assertThrows(ZserioError.class, () -> structureBitmaskConstraints.write(writer));
        writer.close();
    }

    private void writeStructureBitmaskConstraintsToFile(File file, Availability mask,
            short x, short y, short z) throws IOException
    {
        final FileImageOutputStream stream = new FileImageOutputStream(file);

        stream.writeBits(mask.getValue(), 3);
        stream.writeBits(x, 8);
        stream.writeBits(y, 8);
        stream.writeBits(z, 8);

        stream.close();
    }
}
