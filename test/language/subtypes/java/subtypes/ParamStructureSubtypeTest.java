package subtypes;

import static org.junit.Assert.*;

import org.junit.Test;

import subtypes.param_structure_subtype.ParameterizedStruct;
import subtypes.param_structure_subtype.ParameterizedSubtypeStruct;

import zserio.runtime.array.ObjectArray;

public class ParamStructureSubtypeTest
{
    @Test
    public void testSubtype()
    {
        // check that correct type is used instead of subtype (Java has nothing like a typedef)
        ParameterizedStruct parameterizedStruct  = new ParameterizedStruct(10);

        ParameterizedSubtypeStruct parameterizedSubtypeStruct = new ParameterizedSubtypeStruct();
        parameterizedSubtypeStruct.setLength(10);
        parameterizedSubtypeStruct.setParameterizedSubtype(parameterizedStruct);
        ObjectArray<ParameterizedStruct> parameterizedStructArray = new ObjectArray<ParameterizedStruct>(1);
        parameterizedStructArray.setElementAt(new ParameterizedStruct(10), 0);
        parameterizedSubtypeStruct.setParameterizedSubtypeArray(parameterizedStructArray);

        // it's enough that the code above compiles ok, just check something
        ParameterizedStruct parameterizedSubtype = parameterizedSubtypeStruct.getParameterizedSubtype();
        assertEquals(parameterizedSubtypeStruct.getLength(), parameterizedSubtype.getLength());
        ObjectArray<ParameterizedStruct> parameterizedSubtypeArray =
                parameterizedSubtypeStruct.getParameterizedSubtypeArray();
        assertEquals(parameterizedSubtypeStruct.getLength(),
                parameterizedSubtypeArray.elementAt(0).getLength());
    }
}
