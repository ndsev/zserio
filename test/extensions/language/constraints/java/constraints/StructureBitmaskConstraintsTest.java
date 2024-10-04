package constraints;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

import constraints.structure_bitmask_constraints.Availability;
import constraints.structure_bitmask_constraints.StructureBitmaskConstraints;

public class StructureBitmaskConstraintsTest
{
    @Test
    public void readCorrectConstraints() throws IOException, ZserioError
    {
        final Availability availability =
                Availability.Values.COORD_X.or(Availability.Values.COORD_Y).or(Availability.Values.COORD_Z);
        final BitBuffer bitBuffer =
                writeStructureBitmaskConstraintsToBitBuffer(availability, (short)1, (short)1, (short)1);
        final BitStreamReader stream = new ByteArrayBitStreamReader(bitBuffer);
        final StructureBitmaskConstraints structureBitmaskConstraints = new StructureBitmaskConstraints(stream);
        stream.close();
        assertEquals(1, structureBitmaskConstraints.getCoordX());
        assertEquals(1, structureBitmaskConstraints.getCoordY());
        assertEquals(1, structureBitmaskConstraints.getCoordZ());
    }

    @Test
    public void readWrongCoordZConstraint() throws IOException, ZserioError
    {
        final Availability availability = Availability.Values.COORD_X.or(Availability.Values.COORD_Y);
        final BitBuffer bitBuffer =
                writeStructureBitmaskConstraintsToBitBuffer(availability, (short)1, (short)1, (short)1);
        final BitStreamReader stream = new ByteArrayBitStreamReader(bitBuffer);
        assertThrows(ZserioError.class, () -> new StructureBitmaskConstraints(stream));
        stream.close();
    }

    @Test
    public void readWrongCoordYConstraint() throws IOException, ZserioError
    {
        final Availability availability = Availability.Values.COORD_X.or(Availability.Values.COORD_Z);
        final BitBuffer bitBuffer =
                writeStructureBitmaskConstraintsToBitBuffer(availability, (short)1, (short)1, (short)1);
        final BitStreamReader stream = new ByteArrayBitStreamReader(bitBuffer);
        assertThrows(ZserioError.class, () -> new StructureBitmaskConstraints(stream));
        stream.close();
    }

    @Test
    public void readWrongCoordXConstraint() throws IOException, ZserioError
    {
        final Availability availability = Availability.Values.COORD_Y.or(Availability.Values.COORD_Z);
        final BitBuffer bitBuffer =
                writeStructureBitmaskConstraintsToBitBuffer(availability, (short)1, (short)1, (short)1);
        final BitStreamReader stream = new ByteArrayBitStreamReader(bitBuffer);
        assertThrows(ZserioError.class, () -> new StructureBitmaskConstraints(stream));
        stream.close();
    }

    @Test
    public void writeReadCorrectStructureBitmaskConstraints() throws IOException, ZserioError
    {
        final StructureBitmaskConstraints structureBitmaskConstraints = new StructureBitmaskConstraints(
                Availability.Values.COORD_X.or(Availability.Values.COORD_Y), (short)1, (short)1, (short)0);
        final BitBuffer bitBuffer = SerializeUtil.serialize(structureBitmaskConstraints);

        final StructureBitmaskConstraints readStructureBitmaskConstraints =
                SerializeUtil.deserialize(StructureBitmaskConstraints.class, bitBuffer);
        assertEquals(1, readStructureBitmaskConstraints.getCoordX());
        assertEquals(1, readStructureBitmaskConstraints.getCoordY());
        assertEquals(0, readStructureBitmaskConstraints.getCoordZ());
        assertTrue(structureBitmaskConstraints.equals(readStructureBitmaskConstraints));
    }

    @Test
    public void writeWrongCoordZConstraint() throws IOException, ZserioError
    {
        final StructureBitmaskConstraints structureBitmaskConstraints = new StructureBitmaskConstraints(
                Availability.Values.COORD_X.or(Availability.Values.COORD_Y), (short)1, (short)1, (short)1);
        final BitStreamWriter writer = new ByteArrayBitStreamWriter();
        assertThrows(ZserioError.class, () -> structureBitmaskConstraints.write(writer));
        writer.close();
    }

    @Test
    public void writeWrongCoordYConstraint() throws IOException, ZserioError
    {
        final StructureBitmaskConstraints structureBitmaskConstraints = new StructureBitmaskConstraints(
                Availability.Values.COORD_X.or(Availability.Values.COORD_Z), (short)1, (short)1, (short)1);
        final BitStreamWriter writer = new ByteArrayBitStreamWriter();
        assertThrows(ZserioError.class, () -> structureBitmaskConstraints.write(writer));
        writer.close();
    }

    @Test
    public void writeWrongCoordXConstraint() throws IOException, ZserioError
    {
        final StructureBitmaskConstraints structureBitmaskConstraints = new StructureBitmaskConstraints(
                Availability.Values.COORD_Y.or(Availability.Values.COORD_Z), (short)1, (short)1, (short)1);
        final BitStreamWriter writer = new ByteArrayBitStreamWriter();
        assertThrows(ZserioError.class, () -> structureBitmaskConstraints.write(writer));
        writer.close();
    }

    private BitBuffer writeStructureBitmaskConstraintsToBitBuffer(Availability mask, short x, short y, short z)
            throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            writer.writeBits(mask.getValue(), 3);
            writer.writeBits(x, 8);
            writer.writeBits(y, 8);
            writer.writeBits(z, 8);

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }
}
