package zserio.runtime.walker;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import zserio.runtime.typeinfo.FieldInfo;

import test_object.WalkerObject;

public class DefaultWalkObserverTest
{
    @Test
    public void allMethods()
    {
        final DefaultWalkObserver walkObserver = new DefaultWalkObserver();
        final WalkerObject walkerObject = TestObjectCreator.createWalkerObject();
        final FieldInfo walkerArrayFieldInfo = WalkerObject.typeInfo().getFields().get(3);
        final FieldInfo walkerCompoundFieldInfo = WalkerObject.typeInfo().getFields().get(1);
        final FieldInfo walkerFieldInfo = WalkerObject.typeInfo().getFields().get(0);

        assertDoesNotThrow(() -> walkObserver.beginRoot(walkerObject));
        assertDoesNotThrow(() -> walkObserver.endRoot(walkerObject));
        assertDoesNotThrow(() -> walkObserver.beginArray(walkerObject.getUnionArray(), walkerArrayFieldInfo));
        assertDoesNotThrow(() -> walkObserver.endArray(walkerObject.getUnionArray(), walkerArrayFieldInfo));
        assertDoesNotThrow(() -> walkObserver.beginCompound(walkerObject.getNested(), walkerCompoundFieldInfo,
                WalkerConst.NOT_ELEMENT));
        assertDoesNotThrow(() -> walkObserver.endCompound(walkerObject.getNested(), walkerCompoundFieldInfo,
                WalkerConst.NOT_ELEMENT));
        assertDoesNotThrow(() -> walkObserver.visitValue(walkerObject.getIdentifier(), walkerFieldInfo,
                WalkerConst.NOT_ELEMENT));
    }
}
