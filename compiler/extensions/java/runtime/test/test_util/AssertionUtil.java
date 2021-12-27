package test_util;

import static org.junit.Assert.assertTrue;

public class AssertionUtil
{
    public interface Executable
    {
        public void execute() throws Throwable;
    }

    public static <T extends Exception> void assertThrows(Class<T> expectedException, Executable executable)
    {
        boolean thrown = false;
        try
        {
            executable.execute();
        }
        catch (Throwable e)
        {
            if (expectedException.isInstance(e))
            {
                thrown = true;
            }
            else
            {
                throw new AssertionError(
                        "Unexpected exception '" + e.getClass().getSimpleName() + "' thrown, " +
                        "expected '" + expectedException.getSimpleName() + "'!", e);
            }
        }

        assertTrue("Exception '" + expectedException.getSimpleName() + "' not thrown!", thrown);
    }
}
