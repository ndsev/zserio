import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DefaultPackageTest
{
    @Test
    public void defaultPackageStructure() throws Exception
    {
        // just test that DefaultPackageStructure is available in default package
        DefaultPackageStructure structure = new DefaultPackageStructure((short)4);
        structure.setValue(10);
        assertEquals(10, structure.getValue());
    }
};
