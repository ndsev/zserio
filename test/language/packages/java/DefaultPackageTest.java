import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;


public class DefaultPackageTest
{
    @Test
    public void defaultPackageStructure() throws Exception
    {
        // just test that DefaultPackageStructure is available in default package
        DefaultPackageStructure structure = new DefaultPackageStructure((short)4);
        structure.setValue(BigInteger.valueOf(10));
        structure.setTopStructure(new default_package_import.top.TopStructure((short)1, 1234));
        structure.setChildStructure(new Child(0xdeadbeefL));
        assertEquals(BigInteger.valueOf(10), structure.getValue());
        assertEquals(1, structure.getTopStructure().getType());
        assertEquals(1234, structure.getTopStructure().getData());
        assertEquals(0xdeadbeefL, structure.getChildStructure().getValue());
    }
};
