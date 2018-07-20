package without_writer_code;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.junit.Test;

public class WithoutWriterCodeTest
{
    @Test
    public void checkCompoundTypes() throws IOException
    {
        assertFalse(isWriteMethodInCompoundPresent("../gen/without_writer_code/Item.java"));
        assertFalse(isWriteMethodInCompoundPresent("../gen/without_writer_code/ItemChoice.java"));
        assertFalse(isWriteMethodInCompoundPresent("../gen/without_writer_code/ItemChoiceHolder.java"));
        assertFalse(isWriteMethodInCompoundPresent("../gen/without_writer_code/Tile.java"));
    }

    @Test
    public void checkSqlTableTypes() throws IOException
    {
        assertFalse(isWriteMethodInSqlTablePresent("../gen/without_writer_code/GeoMapTable.java"));
    }

    @Test
    public void checkSqlDatabaseTypes() throws IOException
    {
        assertFalse(isWriteMethodInSqlDatabasePresent("../gen/without_writer_code/WorldDb.java"));
        assertFalse(isWriteMethodInSqlDatabasePresent("../gen/without_writer_code/MasterDatabase.java"));
    }

    private boolean isMethodInFilePresent(String fileName, String methodName) throws IOException
    {
        final Scanner scanner = new Scanner(new File(fileName), "UTF-8");
        boolean isPresent = false;
        while (scanner.hasNextLine())
        {
            final String line = scanner.nextLine();
            if (line.contains(methodName))
            {
                isPresent = true;
                break;
            }
        }
        scanner.close();

        return isPresent;
    }

    private boolean isWriteMethodInCompoundPresent(String fileName) throws IOException
    {
        return (isMethodInFilePresent(fileName, "initializeOffsets(") ||
                isMethodInFilePresent(fileName, "write("));
    }

    private boolean isWriteMethodInSqlTablePresent(String fileName) throws IOException
    {
        return (isMethodInFilePresent(fileName, "createTable(") ||
                isMethodInFilePresent(fileName, "createOrdinaryRowIdTable(") ||
                isMethodInFilePresent(fileName, "deleteTable(") ||
                isMethodInFilePresent(fileName, "write(") ||
                isMethodInFilePresent(fileName, "update(") ||
                isMethodInFilePresent(fileName, "writeRow(") ||
                isMethodInFilePresent(fileName, "getCreateTableQuery("));
    }

    private boolean isWriteMethodInSqlDatabasePresent(String fileName) throws IOException
    {
        return (isMethodInFilePresent(fileName, "createSchema(") ||
                isMethodInFilePresent(fileName, "deleteSchema("));
    }
}
