package parameterized_types.array_element_param;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.stream.FileImageInputStream;

import org.junit.Test;

import zserio.runtime.array.LongArray;
import zserio.runtime.array.ObjectArray;

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
        final List<BlockHeader> blockHeaderList = new ArrayList<BlockHeader>();
        final List<Block> blockList = new ArrayList<Block>();
        for (int i = 0; i < NUM_BLOCKS; ++i)
        {
            final int numItems = i + 1;
            final BlockHeader blockHeader = new BlockHeader(numItems, 0);
            blockHeaderList.add(blockHeader);
            final long[] itemList = new long[numItems];
            for (int j = 0; j < numItems; ++j)
                itemList[j] = j * 2;

            blockList.add(new Block(blockHeader, new LongArray(itemList, 0, itemList.length)));
        }

        return new Database(NUM_BLOCKS, new ObjectArray<BlockHeader>(blockHeaderList),
                new ObjectArray<Block>(blockList));
    }

    private void checkDatabaseInFile(File file, Database database) throws IOException
    {
        final FileImageInputStream stream = new FileImageInputStream(file);
        final int numBlocks = database.getNumBlocks();
        assertEquals(numBlocks, stream.readUnsignedShort());

        final ObjectArray<BlockHeader> headers = database.getHeaders();
        long expectedOffset = FIRST_BYTE_OFFSET;
        for (int i = 0; i < numBlocks; ++i)
        {
            final int numItems = stream.readUnsignedShort();
            assertEquals(headers.elementAt(i).getNumItems(), numItems);
            assertEquals(expectedOffset, stream.readUnsignedInt());
            expectedOffset += 8L * numItems;
        }

        final ObjectArray<Block> blocks = database.getBlocks();
        for (int i = 0; i < numBlocks; ++i)
        {
            final int numItems = headers.elementAt(i).getNumItems();
            final LongArray items = blocks.elementAt(i).getItems();
            for (int j = 0; j < numItems; ++j)
                assertEquals(items.elementAt(j), stream.readLong());
        }

        stream.close();
    }

    static final int NUM_BLOCKS = 3;
    static final long FIRST_BYTE_OFFSET = 2 + NUM_BLOCKS * (2 + 4);
}
