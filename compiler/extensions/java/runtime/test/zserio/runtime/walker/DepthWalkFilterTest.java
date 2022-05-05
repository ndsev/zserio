package zserio.runtime.walker;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import zserio.runtime.typeinfo.FieldInfo;

public class DepthWalkFilterTest
{
    @Test
    public void depth0()
    {
        final DepthWalkFilter walkFilter = new DepthWalkFilter(0);
        final TestObject.DummyObject dummyObject = TestObject.createDummyObject();
        final FieldInfo dummyArrayFieldInfo = TestObject.DummyObject.typeInfo().getFields().get(3);
        final FieldInfo dummyCompoundFieldInfo = TestObject.DummyObject.typeInfo().getFields().get(1);
        final FieldInfo dummyFieldInfo = TestObject.DummyObject.typeInfo().getFields().get(0);

        assertFalse(walkFilter.beforeArray(dummyObject.getUnionArray(), dummyArrayFieldInfo)); // 0
        assertTrue(walkFilter.afterArray(dummyObject.getUnionArray(), dummyArrayFieldInfo)); // 0

        assertFalse(walkFilter.beforeCompound(dummyObject.getNested(), dummyCompoundFieldInfo,
                WalkerConst.NOT_ELEMENT)); // 0
        assertTrue(walkFilter.afterCompound(dummyObject.getNested(), dummyCompoundFieldInfo,
                WalkerConst.NOT_ELEMENT)); // 0

        assertFalse(walkFilter.beforeValue(dummyObject.getIdentifier(), dummyFieldInfo,
                WalkerConst.NOT_ELEMENT)); // 0
        assertTrue(walkFilter.afterValue(dummyObject.getIdentifier(), dummyFieldInfo,
                WalkerConst.NOT_ELEMENT)); // 0
    }

    @Test
    public void depth1()
    {
        final DepthWalkFilter walkFilter = new DepthWalkFilter(1);
        final TestObject.DummyObject dummyObject = TestObject.createDummyObject();
        final FieldInfo dummyArrayFieldInfo = TestObject.DummyObject.typeInfo().getFields().get(3);
        final FieldInfo dummyCompoundFieldInfo = TestObject.DummyObject.typeInfo().getFields().get(1);
        final FieldInfo dummyFieldInfo = TestObject.DummyObject.typeInfo().getFields().get(0);

        assertTrue(walkFilter.beforeArray(dummyObject.getUnionArray(), dummyArrayFieldInfo)); // 0
        assertFalse(walkFilter.beforeArray(dummyObject.getUnionArray(), dummyArrayFieldInfo)); // 1
        assertTrue(walkFilter.afterArray(dummyObject.getUnionArray(), dummyArrayFieldInfo)); // 1
        assertFalse(walkFilter.beforeCompound(dummyObject.getNested(), dummyCompoundFieldInfo,
                WalkerConst.NOT_ELEMENT)); // 1
        assertTrue(walkFilter.afterCompound(dummyObject.getNested(), dummyCompoundFieldInfo,
                WalkerConst.NOT_ELEMENT)); // 1
        assertFalse(walkFilter.beforeValue(dummyObject.getIdentifier(), dummyFieldInfo,
                WalkerConst.NOT_ELEMENT)); // 1
        assertTrue(walkFilter.afterValue(dummyObject.getIdentifier(), dummyFieldInfo,
                WalkerConst.NOT_ELEMENT)); // 1
        assertTrue(walkFilter.afterArray(dummyObject.getUnionArray(), dummyArrayFieldInfo)); // 0

        assertTrue(walkFilter.beforeCompound(dummyObject.getNested(), dummyCompoundFieldInfo,
                WalkerConst.NOT_ELEMENT)); // 0
        assertFalse(walkFilter.beforeArray(dummyObject.getUnionArray(), dummyArrayFieldInfo)); // 1
        assertTrue(walkFilter.afterArray(dummyObject.getUnionArray(), dummyArrayFieldInfo)); // 1
        assertFalse(walkFilter.beforeCompound(dummyObject.getNested(), dummyCompoundFieldInfo,
                WalkerConst.NOT_ELEMENT)); // 1
        assertTrue(walkFilter.afterCompound(dummyObject.getNested(), dummyCompoundFieldInfo,
                WalkerConst.NOT_ELEMENT)); // 1
        assertFalse(walkFilter.beforeValue(dummyObject.getIdentifier(), dummyFieldInfo,
                WalkerConst.NOT_ELEMENT)); // 1
        assertTrue(walkFilter.afterValue(dummyObject.getIdentifier(), dummyFieldInfo,
                WalkerConst.NOT_ELEMENT)); // 1
        assertTrue(walkFilter.afterCompound(dummyObject.getNested(), dummyCompoundFieldInfo,
                WalkerConst.NOT_ELEMENT)); // 0

        assertTrue(walkFilter.beforeValue(dummyObject.getIdentifier(), dummyFieldInfo,
                WalkerConst.NOT_ELEMENT)); // 0
        assertTrue(walkFilter.afterValue(dummyObject.getIdentifier(), dummyFieldInfo,
                WalkerConst.NOT_ELEMENT)); // 0
    }
}
