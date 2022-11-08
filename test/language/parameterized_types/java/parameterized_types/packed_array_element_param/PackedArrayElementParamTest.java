package parameterized_types.packed_array_element_param;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.File;
import java.math.BigInteger;

import zserio.runtime.ZserioError;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.FileBitStreamWriter;

public class PackedArrayElementParamTest
{
    @Test
    public void bitSizeOfLength1() throws IOException, ZserioError
    {
        checkBitSizeOf(NUM_BLOCKS1);
    }

    @Test
    public void bitSizeOfLength2() throws IOException, ZserioError
    {
        checkBitSizeOf(NUM_BLOCK2);
    }

    @Test
    public void bitSizeOfLength3() throws IOException, ZserioError
    {
        checkBitSizeOf(NUM_BLOCK3);
    }

    @Test
    public void writeReadLength1() throws IOException, ZserioError
    {
        checkWriteRead(NUM_BLOCKS1);
    }

    @Test
    public void writeReadLength2() throws IOException, ZserioError
    {
        checkWriteRead(NUM_BLOCK2);
    }

    @Test
    public void writeReadLength3() throws IOException, ZserioError
    {
        checkWriteRead(NUM_BLOCK3);
    }

    @Test
    public void writeReadFileLength1() throws IOException, ZserioError
    {
        checkWriteReadFile(NUM_BLOCKS1);
    }

    @Test
    public void writeReadFileLength2() throws IOException, ZserioError
    {
        checkWriteReadFile(NUM_BLOCK2);
    }

    @Test
    public void writeReadFileLength3() throws IOException, ZserioError
    {
        checkWriteReadFile(NUM_BLOCK3);
    }

    private void checkBitSizeOf(int numBlocks) throws IOException, ZserioError
    {
        final Database database = createDatabase(numBlocks);
        final int unpackedBitsizeOf = calcUnpackedDatabaseBitSize(numBlocks);
        final int packedBitsizeOf = database.bitSizeOf();
        final double minCompressionRatio = 0.12;

        assertTrue(unpackedBitsizeOf * minCompressionRatio > packedBitsizeOf, () ->
                "Unpacked array has " + unpackedBitsizeOf + " bits, packed array has " + packedBitsizeOf +
                " bits, " + "compression ratio is " + packedBitsizeOf * 100.0 / unpackedBitsizeOf + "%!");
    }

    private void checkWriteRead(int numBlocks) throws IOException, ZserioError
    {
        final Database database = createDatabase(numBlocks);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        database.write(writer);
        writer.close();

        final long writtenBitPosition = writer.getBitPosition();
        assertEquals(database.bitSizeOf(), writtenBitPosition);
        assertEquals(database.initializeOffsets(), writtenBitPosition);

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        final Database readDatabase = new Database(reader);
        assertEquals(database, readDatabase);
    }

    private void checkWriteReadFile(int numBlocks) throws IOException, ZserioError
    {
        final Database database = createDatabase(numBlocks);
        final File file = new File(BLOB_NAME_BASE + numBlocks + ".blob");
        final FileBitStreamWriter writer = new FileBitStreamWriter(file);
        database.write(writer);
        writer.close();

        final Database readDatabase = new Database(file);
        assertEquals(database, readDatabase);
    }

    private Database createDatabase(int numBlocks)
    {
        final BlockHeader[] blockHeaders = new BlockHeader[numBlocks];
        final Block[] blocks = new Block[numBlocks];
        for (int i = 0; i < numBlocks; ++i)
        {
            final int numItems = i + 1;
            final BlockHeader blockHeader = new BlockHeader(numItems, 0);
            blockHeaders[i] = blockHeader;
            final long[] items = new long[numItems];
            for (int j = 0; j < numItems; ++j)
                items[j] = j * 2;

            blocks[i] = new Block(blockHeader, BigInteger.valueOf(numItems), items);
        }

        return new Database(numBlocks, blockHeaders, blocks);
    }

    private int calcUnpackedDatabaseBitSize(int numBlocks)
    {
        int bitSize = 16; // numBlocks
        bitSize += numBlocks * (16 + 32); // headers
        for (int i = 0; i < numBlocks; ++i)
        {
            final int numItems = i + 1;
            bitSize += 64 + numItems * 64; // blocks[i]
        }

        return bitSize;
    }

    private static final String BLOB_NAME_BASE = "packed_array_element_param_";
    private static final int NUM_BLOCKS1 = 50;
    private static final int NUM_BLOCK2 = 100;
    private static final int NUM_BLOCK3 = 1000;
}
