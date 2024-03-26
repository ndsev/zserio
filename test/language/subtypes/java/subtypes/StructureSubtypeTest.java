package subtypes;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import subtypes.structure_subtype.Student;
import subtypes.structure_subtype.SubtypeStructure;
import subtypes.structure_subtype.TestStructure;

public class StructureSubtypeTest
{
    @Test
    public void testBasetype()
    {
        final int identifier = 0xFFFF;
        final String name = "Name";
        final TestStructure testStructure = new TestStructure(identifier, name);
        final SubtypeStructure subtypeStructure = new SubtypeStructure(testStructure);
        final TestStructure readTestStructure = subtypeStructure.getStudent();
        assertEquals(testStructure, readTestStructure);
    }

    @Test
    public void testSubtype()
    {
        final int identifier = 0xFFFF;
        final String name = "Name";
        final Student student = new Student(identifier, name);
        final SubtypeStructure subtypeStructure = new SubtypeStructure(student);
        final TestStructure readTestStructure = subtypeStructure.getStudent();
        assertEquals(student, readTestStructure);
    }
}
