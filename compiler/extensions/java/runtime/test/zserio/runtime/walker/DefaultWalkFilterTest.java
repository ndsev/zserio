package zserio.runtime.walker;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import zserio.runtime.typeinfo.FieldInfo;

public class DefaultWalkFilterTest
{
    @Test
    public void allMethods()
    {
        final DefaultWalkFilter walkFilter = new DefaultWalkFilter();
        final TestObject.DummyObject dummyObject = TestObject.createDummyObject();
        final FieldInfo dummyArrayFieldInfo = TestObject.DummyObject.typeInfo().getFields().get(3);
        final FieldInfo dummyCompoundFieldInfo = TestObject.DummyObject.typeInfo().getFields().get(1);
        final FieldInfo dummyFieldInfo = TestObject.DummyObject.typeInfo().getFields().get(0);

        assertTrue(walkFilter.beforeArray(dummyObject.getUnionArray(), dummyArrayFieldInfo));
        assertTrue(walkFilter.afterArray(dummyObject.getUnionArray(), dummyArrayFieldInfo));

        assertTrue(walkFilter.beforeCompound(dummyObject.getNested(), dummyCompoundFieldInfo,
                WalkerConst.NOT_ELEMENT));
        assertTrue(walkFilter.afterCompound(dummyObject.getNested(), dummyCompoundFieldInfo,
                WalkerConst.NOT_ELEMENT));

        assertTrue(walkFilter.beforeValue(dummyObject.getIdentifier(), dummyFieldInfo,
                WalkerConst.NOT_ELEMENT));
        assertTrue(walkFilter.afterValue(dummyObject.getIdentifier(), dummyFieldInfo,
                WalkerConst.NOT_ELEMENT));
    }
}
