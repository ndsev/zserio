package zserio.runtime.walker;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import zserio.runtime.typeinfo.FieldInfo;

import test_object.WalkerObject;

public class ArrayLengthWalkFilterTest
{
    @Test
    public void length0()
    {
        final ArrayLengthWalkFilter walkFilter = new ArrayLengthWalkFilter(0);
        final WalkerObject walkerObject = TestObjectCreator.createWalkerObject();
        final FieldInfo walkerArrayFieldInfo = WalkerObject.typeInfo().getFields().get(3);
        final FieldInfo walkerCompoundFieldInfo = WalkerObject.typeInfo().getFields().get(1);
        final FieldInfo walkerFieldInfo = WalkerObject.typeInfo().getFields().get(0);

        assertTrue(walkFilter.beforeArray(walkerObject.getUnionArray(), walkerArrayFieldInfo));
        assertFalse(walkFilter.beforeCompound(walkerObject.getUnionArray()[0], walkerArrayFieldInfo, 0));
        assertFalse(walkFilter.afterCompound(walkerObject.getUnionArray()[0], walkerArrayFieldInfo, 0));
        assertFalse(walkFilter.beforeValue(walkerObject.getUnionArray()[1], walkerArrayFieldInfo, 1));
        assertFalse(walkFilter.afterValue(walkerObject.getUnionArray()[1], walkerArrayFieldInfo, 1));
        assertTrue(walkFilter.afterArray(walkerObject.getUnionArray(), walkerArrayFieldInfo));

        assertTrue(walkFilter.beforeCompound(walkerObject.getNested(), walkerCompoundFieldInfo,
                WalkerConst.NOT_ELEMENT));
        assertTrue(walkFilter.beforeValue(walkerObject.getIdentifier(), walkerFieldInfo,
                WalkerConst.NOT_ELEMENT));
        assertTrue(walkFilter.afterValue(walkerObject.getIdentifier(), walkerFieldInfo,
                WalkerConst.NOT_ELEMENT));
        assertTrue(walkFilter.beforeArray(walkerObject.getUnionArray(), walkerArrayFieldInfo));
        assertFalse(walkFilter.beforeValue(walkerObject.getUnionArray()[0], walkerArrayFieldInfo, 0));
        assertFalse(walkFilter.afterValue(walkerObject.getUnionArray()[0], walkerArrayFieldInfo, 0));
        assertTrue(walkFilter.afterArray(walkerObject.getUnionArray(), walkerArrayFieldInfo));
        assertTrue(walkFilter.afterCompound(walkerObject.getNested(), walkerCompoundFieldInfo,
                WalkerConst.NOT_ELEMENT));
    }
}
