package array_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.File;

import array_types.variable_array_ternary_operator.VariableArray;
import array_types.variable_array_ternary_operator.VariableArrayElement;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

public class VariableArrayTernaryOperatorTest
{
    @Test
    public void firstWriteReadFile() throws IOException
    {
        final boolean isFirstOffsetUsed = true;
        testWriteReadFile(isFirstOffsetUsed);
    }

    @Test
    public void secondWriteReadFile() throws IOException
    {
        final boolean isFirstOffsetUsed = false;
        testWriteReadFile(isFirstOffsetUsed);
    }

    private VariableArray createVariableArray(boolean isFirstSizeUsed)
    {
        final VariableArray variableArray = new VariableArray();
        variableArray.setIsFirstSizeUsed(isFirstSizeUsed);
        final byte currentSize = (isFirstSizeUsed) ? variableArray.getFirstSize() :
            variableArray.getSecondSize();
        final VariableArrayElement array[] = new VariableArrayElement[currentSize * currentSize];
        for (int i = 0; i < array.length; ++i)
        {
            final VariableArrayElement variableArrayElement = new VariableArrayElement(currentSize);
            variableArrayElement.setElement(i);
            array[i] = variableArrayElement;
        }
        variableArray.setArray(array);

        return variableArray;
    }

    private void checkVariableArray(VariableArray variableArray, boolean isFirstSizeUsed)
    {
        assertEquals(isFirstSizeUsed, variableArray.getIsFirstSizeUsed());
        final byte currentSize = (isFirstSizeUsed) ? FIRST_SIZE : SECOND_SIZE;
        final VariableArrayElement array[] = variableArray.getArray();
        for (int i = 0; i < currentSize * currentSize; ++i)
        {
            final VariableArrayElement variableArrayElement = array[i];
            assertEquals(currentSize, variableArrayElement.getBitSize());
            assertEquals(i, variableArrayElement.getElement());
        }
    }

    private void testWriteReadFile(boolean isFirstSizeUsed) throws IOException
    {
        final VariableArray variableArray = createVariableArray(isFirstSizeUsed);
        final String blobName = (isFirstSizeUsed) ? BLOB_NAME_FIRST : BLOB_NAME_SECOND;
        final File file = new File(blobName);
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        variableArray.write(writer);
        writer.close();

        final VariableArray readVariableArray = new VariableArray(file);
        checkVariableArray(readVariableArray, isFirstSizeUsed);
        assertEquals(variableArray, readVariableArray);
    }

    private static final byte FIRST_SIZE = 10;
    private static final byte SECOND_SIZE = 20;

    private static final String BLOB_NAME_FIRST = "variable_array_ternary_operator1.blob";
    private static final String BLOB_NAME_SECOND = "variable_array_ternary_operator2.blob";
}
