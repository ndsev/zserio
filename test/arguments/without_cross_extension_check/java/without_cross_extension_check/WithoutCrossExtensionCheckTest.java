package without_cross_extension_check;

import static org.junit.Assert.*;

import org.junit.Test;

public class WithoutCrossExtensionCheckTest
{
    @Test
    public void invalidInCpp()
    {
        final invalid_in_cpp.Test test = new invalid_in_cpp.Test();
        test.setDynamic_cast("dynamic_cast");
        assertEquals("dynamic_cast", test.getDynamic_cast());
    }

    @Test
    public void invalidInPython()
    {
        final invalid_in_python.Test test = new invalid_in_python.Test();
        test.setSomeField(13);
        test.setSome_field(42);
        test.setDef("def");
        assertEquals(13, test.getSomeField());
        assertEquals(42, test.getSome_field());
        assertEquals("def", test.getDef());
    }
};
