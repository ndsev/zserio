package zserio.runtime.walker;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import zserio.runtime.typeinfo.FieldInfo;

public class DefaultWalkObserverTest
{
    @Test
    public void allMethods()
    {
        final DefaultWalkObserver walkObserver = new DefaultWalkObserver();
        final TestObject.DummyObject dummyObject = TestObject.createDummyObject();
        final FieldInfo dummyArrayFieldInfo = TestObject.DummyObject.typeInfo().getFields().get(3);
        final FieldInfo dummyCompoundFieldInfo = TestObject.DummyObject.typeInfo().getFields().get(1);
        final FieldInfo dummyFieldInfo = TestObject.DummyObject.typeInfo().getFields().get(0);

        assertDoesNotThrow(() -> walkObserver.beginRoot(dummyObject));
        assertDoesNotThrow(() -> walkObserver.endRoot(dummyObject));
        assertDoesNotThrow(() -> walkObserver.beginArray(dummyObject.getUnionArray(), dummyArrayFieldInfo));
        assertDoesNotThrow(() -> walkObserver.endArray(dummyObject.getUnionArray(), dummyArrayFieldInfo));
        assertDoesNotThrow(() -> walkObserver.beginCompound(dummyObject.getNested(), dummyCompoundFieldInfo,
                WalkerConst.NOT_ELEMENT));
        assertDoesNotThrow(() -> walkObserver.endCompound(dummyObject.getNested(), dummyCompoundFieldInfo,
                WalkerConst.NOT_ELEMENT));
        assertDoesNotThrow(() -> walkObserver.visitValue(dummyObject.getIdentifier(), dummyFieldInfo,
                WalkerConst.NOT_ELEMENT));
    }
}
