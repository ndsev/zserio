package parameterized_types.array_element_param;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageInputStream;

public class ArrayElementParamTest
{
    @Test
    public void fileWrite() throws IOException
    {
        final Database database = createDatabase();
        final File file = new File("test.bin");
        database.write(file);
        checkDatabaseInFile(file, database);
        final Database readDatabase = new Database(file);
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

        return new Database(NUM_BLOCKS, blockHeaders, blocks);
    }

    private void checkDatabaseInFile(File file, Database database) throws IOException
    {
        final FileImageInputStream stream = new FileImageInputStream(file);
        final int numBlocks = database.getNumBlocks();
        assertEquals(numBlocks, stream.readUnsignedShort());

        final BlockHeader[] headers = database.getHeaders();
        long expectedOffset = FIRST_BYTE_OFFSET;
        for (int i = 0; i < numBlocks; ++i)
        {
            final int numItems = stream.readUnsignedShort();
            assertEquals(headers[i].getNumItems(), numItems);
            assertEquals(expectedOffset, stream.readUnsignedInt());
            expectedOffset += 8L * numItems;
        }

        final Block[] blocks = database.getBlocks();
        for (int i = 0; i < numBlocks; ++i)
        {
            final int numItems = headers[i].getNumItems();
            final long[] items = blocks[i].getItems();
            for (int j = 0; j < numItems; ++j)
                assertEquals(items[j], stream.readLong());
        }

        stream.close();
    }

    static final int NUM_BLOCKS = 3;
    static final long FIRST_BYTE_OFFSET = 2 + NUM_BLOCKS * (2 + 4);
}
