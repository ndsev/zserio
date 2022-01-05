package set_top_level_package;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import company.appl.SimpleStructure;
import company.appl.SimpleChoice;
import company.appl.SimpleTemplate_Enumeration;

public class SetTopLevelPackageTest
{
    @Test
    public void emptyConstructor()
    {
        final SimpleStructure simpleStructure = new SimpleStructure();
        simpleStructure.setSimpleChoice(new SimpleChoice(simpleStructure.getValue()));
        simpleStructure.setSimpleTemplate(new SimpleTemplate_Enumeration(true, (byte)0));
        assertEquals(SIMPLE_STRUCTURE_BIT_SIZE, simpleStructure.bitSizeOf());
    }

    private static final int SIMPLE_STRUCTURE_BIT_SIZE = 32;
}
