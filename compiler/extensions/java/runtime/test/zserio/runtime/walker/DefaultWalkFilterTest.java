package zserio.runtime.walker;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import zserio.runtime.typeinfo.FieldInfo;

import test_object.WalkerObject;

public class DefaultWalkFilterTest
{
    @Test
    public void allMethods()
    {
        final DefaultWalkFilter walkFilter = new DefaultWalkFilter();
        final WalkerObject walkerObject = TestObjectCreator.createWalkerObject();
        final FieldInfo walkerArrayFieldInfo = WalkerObject.typeInfo().getFields().get(3);
        final FieldInfo walkerCompoundFieldInfo = WalkerObject.typeInfo().getFields().get(1);
        final FieldInfo walkerFieldInfo = WalkerObject.typeInfo().getFields().get(0);

        assertTrue(walkFilter.beforeArray(walkerObject.getUnionArray(), walkerArrayFieldInfo));
        assertTrue(walkFilter.afterArray(walkerObject.getUnionArray(), walkerArrayFieldInfo));

        assertTrue(walkFilter.beforeCompound(
                walkerObject.getNested(), walkerCompoundFieldInfo, WalkerConst.NOT_ELEMENT));
        assertTrue(walkFilter.afterCompound(
                walkerObject.getNested(), walkerCompoundFieldInfo, WalkerConst.NOT_ELEMENT));

        assertTrue(
                walkFilter.beforeValue(walkerObject.getIdentifier(), walkerFieldInfo, WalkerConst.NOT_ELEMENT));
        assertTrue(
                walkFilter.afterValue(walkerObject.getIdentifier(), walkerFieldInfo, WalkerConst.NOT_ELEMENT));
    }
}
