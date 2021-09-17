package cpp_error;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import test_utils.ZserioErrors;

public class CppErrorTest
{
    @BeforeClass
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void wrongSetCppAllocator()
    {
        final String error = "[ERROR] C++11 Generator: The specified option 'setCppAllocator' has unknown " +
                "allocator 'wrong'!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
