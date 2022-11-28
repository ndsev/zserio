package zserio.runtime.walker;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import zserio.runtime.typeinfo.FieldInfo;

import test_object.WalkerObject;

public class DepthWalkFilterTest
{
    @Test
    public void depth0()
    {
        final DepthWalkFilter walkFilter = new DepthWalkFilter(0);
        final WalkerObject walkerObject = TestObjectCreator.createWalkerObject();
        final FieldInfo walkerArrayFieldInfo = WalkerObject.typeInfo().getFields().get(3);
        final FieldInfo walkerCompoundFieldInfo = WalkerObject.typeInfo().getFields().get(1);
        final FieldInfo walkerFieldInfo = WalkerObject.typeInfo().getFields().get(0);

        assertFalse(walkFilter.beforeArray(walkerObject.getUnionArray(), walkerArrayFieldInfo)); // 0
        assertTrue(walkFilter.afterArray(walkerObject.getUnionArray(), walkerArrayFieldInfo)); // 0

        assertFalse(walkFilter.beforeCompound(walkerObject.getNested(), walkerCompoundFieldInfo,
                WalkerConst.NOT_ELEMENT)); // 0
        assertTrue(walkFilter.afterCompound(walkerObject.getNested(), walkerCompoundFieldInfo,
                WalkerConst.NOT_ELEMENT)); // 0

        assertFalse(walkFilter.beforeValue(walkerObject.getIdentifier(), walkerFieldInfo,
                WalkerConst.NOT_ELEMENT)); // 0
        assertTrue(walkFilter.afterValue(walkerObject.getIdentifier(), walkerFieldInfo,
                WalkerConst.NOT_ELEMENT)); // 0
    }

    @Test
    public void depth1()
    {
        final DepthWalkFilter walkFilter = new DepthWalkFilter(1);
        final WalkerObject walkerObject = TestObjectCreator.createWalkerObject();
        final FieldInfo walkerArrayFieldInfo = WalkerObject.typeInfo().getFields().get(3);
        final FieldInfo walkerCompoundFieldInfo = WalkerObject.typeInfo().getFields().get(1);
        final FieldInfo walkerFieldInfo = WalkerObject.typeInfo().getFields().get(0);

        assertTrue(walkFilter.beforeArray(walkerObject.getUnionArray(), walkerArrayFieldInfo)); // 0
        assertFalse(walkFilter.beforeArray(walkerObject.getUnionArray(), walkerArrayFieldInfo)); // 1
        assertTrue(walkFilter.afterArray(walkerObject.getUnionArray(), walkerArrayFieldInfo)); // 1
        assertFalse(walkFilter.beforeCompound(walkerObject.getNested(), walkerCompoundFieldInfo,
                WalkerConst.NOT_ELEMENT)); // 1
        assertTrue(walkFilter.afterCompound(walkerObject.getNested(), walkerCompoundFieldInfo,
                WalkerConst.NOT_ELEMENT)); // 1
        assertFalse(walkFilter.beforeValue(walkerObject.getIdentifier(), walkerFieldInfo,
                WalkerConst.NOT_ELEMENT)); // 1
        assertTrue(walkFilter.afterValue(walkerObject.getIdentifier(), walkerFieldInfo,
                WalkerConst.NOT_ELEMENT)); // 1
        assertTrue(walkFilter.afterArray(walkerObject.getUnionArray(), walkerArrayFieldInfo)); // 0

        assertTrue(walkFilter.beforeCompound(walkerObject.getNested(), walkerCompoundFieldInfo,
                WalkerConst.NOT_ELEMENT)); // 0
        assertFalse(walkFilter.beforeArray(walkerObject.getUnionArray(), walkerArrayFieldInfo)); // 1
        assertTrue(walkFilter.afterArray(walkerObject.getUnionArray(), walkerArrayFieldInfo)); // 1
        assertFalse(walkFilter.beforeCompound(walkerObject.getNested(), walkerCompoundFieldInfo,
                WalkerConst.NOT_ELEMENT)); // 1
        assertTrue(walkFilter.afterCompound(walkerObject.getNested(), walkerCompoundFieldInfo,
                WalkerConst.NOT_ELEMENT)); // 1
        assertFalse(walkFilter.beforeValue(walkerObject.getIdentifier(), walkerFieldInfo,
                WalkerConst.NOT_ELEMENT)); // 1
        assertTrue(walkFilter.afterValue(walkerObject.getIdentifier(), walkerFieldInfo,
                WalkerConst.NOT_ELEMENT)); // 1
        assertTrue(walkFilter.afterCompound(walkerObject.getNested(), walkerCompoundFieldInfo,
                WalkerConst.NOT_ELEMENT)); // 0

        assertTrue(walkFilter.beforeValue(walkerObject.getIdentifier(), walkerFieldInfo,
                WalkerConst.NOT_ELEMENT)); // 0
        assertTrue(walkFilter.afterValue(walkerObject.getIdentifier(), walkerFieldInfo,
                WalkerConst.NOT_ELEMENT)); // 0
    }
}
