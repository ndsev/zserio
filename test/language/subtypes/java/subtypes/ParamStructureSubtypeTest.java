package subtypes;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import subtypes.param_structure_subtype.ParameterizedStruct;
import subtypes.param_structure_subtype.ParameterizedSubtypeStruct;

public class ParamStructureSubtypeTest
{
    @Test
    public void testSubtype()
    {
        // check that correct type is used instead of subtype (Java has nothing like a typedef)
        final ParameterizedStruct parameterizedStruct  = new ParameterizedStruct(10);

        final ParameterizedSubtypeStruct parameterizedSubtypeStruct = new ParameterizedSubtypeStruct();
        parameterizedSubtypeStruct.setLength(10);
        parameterizedSubtypeStruct.setParameterizedSubtype(parameterizedStruct);
        final ParameterizedStruct[] parameterizedStructArray = new ParameterizedStruct[] {
                new ParameterizedStruct(10)
        };
        parameterizedSubtypeStruct.setParameterizedSubtypeArray(parameterizedStructArray);

        // it's enough that the code above compiles ok, just check something
        final ParameterizedStruct parameterizedSubtype = parameterizedSubtypeStruct.getParameterizedSubtype();
        assertEquals(parameterizedSubtypeStruct.getLength(), parameterizedSubtype.getLength());
        final ParameterizedStruct[] parameterizedSubtypeArray =
                parameterizedSubtypeStruct.getParameterizedSubtypeArray();
        assertEquals(parameterizedSubtypeStruct.getLength(), parameterizedSubtypeArray[0].getLength());
    }
}
