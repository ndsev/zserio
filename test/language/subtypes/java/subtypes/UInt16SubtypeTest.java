package subtypes;

import static org.junit.Assert.*;

import org.junit.Test;;

import subtypes.uint16_subtype.TestStructure;

public class UInt16SubtypeTest
{
    @Test
    public void testSubtype()
    {
        final int identifier = 0xFFFF;
        final String name = "Name";
        final TestStructure testStructure = new TestStructure(identifier, name);
        final int readIdentifier = testStructure.getIdentifier();
        assertEquals(identifier, readIdentifier);
    }
}
