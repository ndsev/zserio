package without_writer_code;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.junit.Test;

public class WithoutWriterCodeTest
{
    @Test
    public void checkUserTypes() throws IOException
    {
        assertFalse(isWriteMethodInUserTypePresent("Item"));
        assertFalse(isWriteMethodInUserTypePresent("ItemChoice"));
        assertFalse(isWriteMethodInUserTypePresent("ItemChoiceHolder"));
        assertFalse(isWriteMethodInUserTypePresent("Tile"));
        assertFalse(isWriteMethodInUserTypePresent("ElementsUnion"));
        assertFalse(isWriteMethodInUserTypePresent("TypeEnum"));
    }

    @Test
    public void checkSqlTableTypes() throws IOException
    {
        assertFalse(isWriteMethodInSqlTablePresent("GeoMapTable"));
    }

    @Test
    public void checkSqlDatabaseTypes() throws IOException
    {
        assertFalse(isWriteMethodInSqlDatabasePresent("WorldDb"));
        assertFalse(isWriteMethodInSqlDatabasePresent("MasterDatabase"));
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

    private boolean isMethodInTypePresent(String typeName, String methodName) throws IOException
    {
        return isMethodInFilePresent(PATH + typeName + ".java", methodName);
    }

    private boolean isWriteMethodInUserTypePresent(String typeName) throws IOException
    {
        return (isMethodInTypePresent(typeName, "initializeOffsets(") ||
                isMethodInTypePresent(typeName, "write("));
    }

    private boolean isWriteMethodInSqlTablePresent(String typeName) throws IOException
    {
        return (isMethodInTypePresent(typeName, "createTable(") ||
                isMethodInTypePresent(typeName, "createOrdinaryRowIdTable(") ||
                isMethodInTypePresent(typeName, "deleteTable(") ||
                isMethodInTypePresent(typeName, "write(") ||
                isMethodInTypePresent(typeName, "update(") ||
                isMethodInTypePresent(typeName, "writeRow(") ||
                isMethodInTypePresent(typeName, "getCreateTableQuery("));
    }

    private boolean isWriteMethodInSqlDatabasePresent(String typeName) throws IOException
    {
        return (isMethodInTypePresent(typeName, "createSchema(") ||
                isMethodInTypePresent(typeName, "deleteSchema("));
    }

    private static final String PATH = "../gen/without_writer_code/";
}
