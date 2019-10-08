package templates;

import static org.junit.Assert.*;

import org.junit.Test;

import templates.subtype_template_argument.Field_uint32;
import templates.subtype_template_argument.Compound;
import templates.subtype_template_argument.Field_Compound;
import templates.subtype_template_argument.SubtypeTemplateArgument;

public class SubtypeTemplateArgumentTest
{
    @Test
    public void bitSizeOf()
    {
        final Field_uint32 field_uint32 = new Field_uint32(10);
        final Field_Compound field_compound = new Field_Compound(new Compound(10));
        final SubtypeTemplateArgument subtypeTemplateArgument = new SubtypeTemplateArgument(field_uint32,
                field_uint32, field_uint32, field_compound, field_compound, field_compound);
        assertEquals(192, subtypeTemplateArgument.bitSizeOf());
    }
}
