package parameterized_types.array_element_param;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.SerializeUtil;

public class ArrayElementParamTest
{
    @Test
    public void writeRead() throws IOException
    {
        final Database database = createDatabase();
        final BitBuffer bitBuffer = SerializeUtil.serialize(database);
        checkDatabaseInBitBuffer(bitBuffer, database);
        final Database readDatabase = SerializeUtil.deserialize(Database.class, bitBuffer);
        assertEquals(database, readDatabase);
    }

    private Database createDatabase()
    {
        final BlockHeader[] blockHeaders = new BlockHeader[NUM_BLOCKS];
        final Block[] blocks = new Block[NUM_BLOCKS];
        for (int i = 0; i < NUM_BLOCKS; ++i)
        {
            final int numItems = i + 1;
            final BlockHeader blockHeader = new BlockHeader(numItems, 0);
            blockHeaders[i] = blockHeader;
            final long[] items = new long[numItems];
            for (int j = 0; j < numItems; ++j)
                items[j] = j * 2;

            blocks[i] = new Block(blockHeader, items);
        }

        final Database database = new Database(NUM_BLOCKS, blockHeaders, blocks);
        database.initializeOffsets();

        return database;
    }

    private void checkDatabaseInBitBuffer(BitBuffer bitBuffer, Database database) throws IOException
    {
        try (final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer))
        {
            final int numBlocks = database.getNumBlocks();
            assertEquals(numBlocks, reader.readBits(16));

            final BlockHeader[] headers = database.getHeaders();
            long expectedOffset = FIRST_BYTE_OFFSET;
            for (int i = 0; i < numBlocks; ++i)
            {
                final int numItems = (int)reader.readBits(16);
                assertEquals(headers[i].getNumItems(), numItems);
                assertEquals(expectedOffset, reader.readBits(32));
                expectedOffset += 8L * numItems;
            }

            final Block[] blocks = database.getBlocks();
            for (int i = 0; i < numBlocks; ++i)
            {
                final int numItems = headers[i].getNumItems();
                final long[] items = blocks[i].getItems();
                for (int j = 0; j < numItems; ++j)
                    assertEquals(items[j], reader.readSignedBits(64));
            }
        }
    }

    static final int NUM_BLOCKS = 3;
    static final long FIRST_BYTE_OFFSET = 2 + NUM_BLOCKS * (2 + 4);
}
