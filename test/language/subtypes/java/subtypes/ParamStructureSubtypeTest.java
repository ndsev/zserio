package subtypes;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import subtypes.param_structure_subtype.AnotherParameterizedSubtype;
import subtypes.param_structure_subtype.ParameterizedStruct;
import subtypes.param_structure_subtype.ParameterizedSubtype;
import subtypes.param_structure_subtype.ParameterizedSubtypeStruct;

public class ParamStructureSubtypeTest
{
    @Test
    public void testBasetype()
    {
        // check that correct type is used instead of subtype (Java has nothing like a typedef)
        final ParameterizedSubtypeStruct parameterizedSubtypeStruct = new ParameterizedSubtypeStruct();
        parameterizedSubtypeStruct.setLength(10);
        parameterizedSubtypeStruct.setParameterizedSubtype(new ParameterizedStruct(10));
        final ParameterizedStruct[] parameterizedStructArray =
                new ParameterizedStruct[] {new ParameterizedStruct(10)};
        parameterizedSubtypeStruct.setAnotherParameterizedSubtypeArray(parameterizedStructArray);

        // it's enough that the code above compiles ok, just check something
        final ParameterizedStruct parameterizedSubtype = parameterizedSubtypeStruct.getParameterizedSubtype();
        assertEquals(parameterizedSubtypeStruct.getLength(), parameterizedSubtype.getLength());
        final ParameterizedStruct[] anotherParameterizedSubtypeArray =
                parameterizedSubtypeStruct.getAnotherParameterizedSubtypeArray();
        assertEquals(parameterizedSubtypeStruct.getLength(), anotherParameterizedSubtypeArray[0].getLength());
    }

    @Test
    public void testSubtype()
    {
        // check that generated subtypes can be used in setters
        final ParameterizedSubtypeStruct parameterizedSubtypeStruct = new ParameterizedSubtypeStruct();
        parameterizedSubtypeStruct.setLength(10);
        parameterizedSubtypeStruct.setParameterizedSubtype(new ParameterizedSubtype(10));
        final AnotherParameterizedSubtype[] parameterizedStructArray =
                new AnotherParameterizedSubtype[] {new AnotherParameterizedSubtype(10)};
        parameterizedSubtypeStruct.setAnotherParameterizedSubtypeArray(parameterizedStructArray);

        // it's enough that the code above compiles ok, just check something
        final ParameterizedStruct parameterizedSubtype = parameterizedSubtypeStruct.getParameterizedSubtype();
        assertEquals(parameterizedSubtypeStruct.getLength(), parameterizedSubtype.getLength());
        final ParameterizedStruct[] anotherParameterizedSubtypeArray =
                parameterizedSubtypeStruct.getAnotherParameterizedSubtypeArray();
        assertEquals(parameterizedSubtypeStruct.getLength(), anotherParameterizedSubtypeArray[0].getLength());
    }
}
