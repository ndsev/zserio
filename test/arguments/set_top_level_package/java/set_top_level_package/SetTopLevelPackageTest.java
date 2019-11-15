package set_top_level_package;

import static org.junit.Assert.*;

import org.junit.Test;

import company.appl.set_top_level_package.SimpleStructure;
import company.appl.set_top_level_package.SimpleChoice;

public class SetTopLevelPackageTest
{
    @Test
    public void emptyConstructor()
    {
        final SimpleStructure simpleStructure = new SimpleStructure();
        simpleStructure.setSimpleChoice(new SimpleChoice(simpleStructure.getE()));
        assertEquals(SIMPLE_STRUCTURE_BIT_SIZE, simpleStructure.bitSizeOf());
    }

    private static final int SIMPLE_STRUCTURE_BIT_SIZE = 26;
}
