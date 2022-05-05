package zserio.runtime.walker;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import zserio.runtime.typeinfo.FieldInfo;

public class ArrayLengthWalkFilterTest
{
    @Test
    public void length0()
    {
        final ArrayLengthWalkFilter walkFilter = new ArrayLengthWalkFilter(0);
        final TestObject.DummyObject dummyObject = TestObject.createDummyObject();
        final FieldInfo dummyArrayFieldInfo = TestObject.DummyObject.typeInfo().getFields().get(3);
        final FieldInfo dummyCompoundFieldInfo = TestObject.DummyObject.typeInfo().getFields().get(1);
        final FieldInfo dummyFieldInfo = TestObject.DummyObject.typeInfo().getFields().get(0);

        assertTrue(walkFilter.beforeArray(dummyObject.getUnionArray(), dummyArrayFieldInfo));
        assertFalse(walkFilter.beforeCompound(dummyObject.getUnionArray()[0], dummyArrayFieldInfo, 0));
        assertFalse(walkFilter.afterCompound(dummyObject.getUnionArray()[0], dummyArrayFieldInfo, 0));
        assertFalse(walkFilter.beforeValue(dummyObject.getUnionArray()[1], dummyArrayFieldInfo, 1));
        assertFalse(walkFilter.afterValue(dummyObject.getUnionArray()[1], dummyArrayFieldInfo, 1));
        assertTrue(walkFilter.afterArray(dummyObject.getUnionArray(), dummyArrayFieldInfo));

        assertTrue(walkFilter.beforeCompound(dummyObject.getNested(), dummyCompoundFieldInfo,
                WalkerConst.NOT_ELEMENT));
        assertTrue(walkFilter.beforeValue(dummyObject.getIdentifier(), dummyFieldInfo,
                WalkerConst.NOT_ELEMENT));
        assertTrue(walkFilter.afterValue(dummyObject.getIdentifier(), dummyFieldInfo,
                WalkerConst.NOT_ELEMENT));
        assertTrue(walkFilter.beforeArray(dummyObject.getUnionArray(), dummyArrayFieldInfo));
        assertFalse(walkFilter.beforeValue(dummyObject.getUnionArray()[0], dummyArrayFieldInfo, 0));
        assertFalse(walkFilter.afterValue(dummyObject.getUnionArray()[0], dummyArrayFieldInfo, 0));
        assertTrue(walkFilter.afterArray(dummyObject.getUnionArray(), dummyArrayFieldInfo));
        assertTrue(walkFilter.afterCompound(dummyObject.getNested(), dummyCompoundFieldInfo,
                WalkerConst.NOT_ELEMENT));
    }
}
