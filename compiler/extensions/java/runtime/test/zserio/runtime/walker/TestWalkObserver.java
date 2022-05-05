package zserio.runtime.walker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import zserio.runtime.typeinfo.FieldInfo;
import zserio.runtime.walker.WalkObserver;

public class TestWalkObserver implements WalkObserver
{
    public TestWalkObserver()
    {
        // initialize empty captures
        captures = new HashMap<String, ArrayList<Object>>();
        captures.put("beginRoot", new ArrayList<Object>());
        captures.put("endRoot", new ArrayList<Object>());
        captures.put("beginArray", new ArrayList<Object>());
        captures.put("endArray", new ArrayList<Object>());
        captures.put("beginCompound", new ArrayList<Object>());
        captures.put("endCompound", new ArrayList<Object>());
        captures.put("visitValue", new ArrayList<Object>());
    }

    @Override
    public void beginRoot(Object compound)
    {
        captures.get("beginRoot").add(compound);
    }

    @Override
    public void endRoot(Object compound)
    {
        captures.get("endRoot").add(compound);

    }

    @Override
    public void beginArray(Object array, FieldInfo fieldInfo)
    {
        captures.get("beginArray").add(array);
    }

    @Override
    public void endArray(Object array, FieldInfo fieldInfo)
    {
        captures.get("endArray").add(array);
    }

    @Override
    public void beginCompound(Object compound, FieldInfo fieldInfo, int elementIndex)
    {
        captures.get("beginCompound").add(compound);
    }

    @Override
    public void endCompound(Object compound, FieldInfo fieldInfo, int elementIndex)
    {
        captures.get("endCompound").add(compound);
    }

    @Override
    public void visitValue(Object value, FieldInfo fieldInfo, int elementIndex)
    {
        captures.get("visitValue").add(value);
    }

    public List<Object> getCaptures(String captureName)
    {
        return captures.get(captureName);
    }

    private final HashMap<String, ArrayList<Object>> captures;
};
