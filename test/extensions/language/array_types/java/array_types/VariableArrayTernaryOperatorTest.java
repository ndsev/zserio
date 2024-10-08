package array_types;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.SerializeUtil;

import array_types.variable_array_ternary_operator.VariableArray;
import array_types.variable_array_ternary_operator.VariableArrayElement;

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
        final byte currentSize =
                (isFirstSizeUsed) ? variableArray.getFirstSize() : variableArray.getSecondSize();
        final int arraySize = (int)currentSize * (int)currentSize;
        final VariableArrayElement array[] = new VariableArrayElement[arraySize];
        for (int i = 0; i < arraySize; ++i)
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
        final int arraySize = (int)currentSize * (int)currentSize;
        for (int i = 0; i < arraySize; ++i)
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
        SerializeUtil.serializeToFile(variableArray, file);
        final VariableArray readVariableArray = SerializeUtil.deserializeFromFile(VariableArray.class, file);
        checkVariableArray(readVariableArray, isFirstSizeUsed);
        assertEquals(variableArray, readVariableArray);
    }

    private static final byte FIRST_SIZE = 10;
    private static final byte SECOND_SIZE = 20;

    private static final String BLOB_NAME_FIRST = "variable_array_ternary_operator1.blob";
    private static final String BLOB_NAME_SECOND = "variable_array_ternary_operator2.blob";
}
