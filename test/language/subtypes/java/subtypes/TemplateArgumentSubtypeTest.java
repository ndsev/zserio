package subtypes;

import static org.junit.Assert.*;

import org.junit.Test;

import subtypes.template_argument_subtype.Field_uint32;
import subtypes.template_argument_subtype.Compound;
import subtypes.template_argument_subtype.Field_Compound;
import subtypes.template_argument_subtype.TemplateArgumentStructure;

public class TemplateArgumentSubtypeTest
{
    @Test
    public void testSubtype()
    {
        final Field_uint32 field_uint32 = new Field_uint32(10);
        final Field_Compound field_compound = new Field_Compound(new Compound(10));
        final TemplateArgumentStructure templateArgumentStructure = new TemplateArgumentStructure(field_uint32,
                field_uint32, field_uint32, field_compound, field_compound, field_compound);
        assertEquals(192, templateArgumentStructure.bitSizeOf());
    }
}
