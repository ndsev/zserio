package zserio.runtime.walker;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import zserio.runtime.typeinfo.FieldInfo;

public class AndWalkFilterTest
{
    @Test
    public void empty()
    {
        final AndWalkFilter walkFilter = new AndWalkFilter(new ArrayList<WalkFilter>());
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

    @Test
    public void trueTrue()
    {
        final ArrayList<WalkFilter> walkFilters = new ArrayList<WalkFilter>();
        walkFilters.add(new TestWalkFilter());
        walkFilters.add(new TestWalkFilter());
        final AndWalkFilter walkFilter = new AndWalkFilter(walkFilters);
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

    @Test
    public void falseFalse()
    {
        final ArrayList<WalkFilter> walkFilters = new ArrayList<WalkFilter>();
        final TestWalkFilter falseFilter1 = new TestWalkFilter();
        falseFilter1.beforeArray(false);
        falseFilter1.afterArray(false);
        falseFilter1.beforeCompound(false);
        falseFilter1.afterCompound(false);
        falseFilter1.beforeValue(false);
        falseFilter1.afterValue(false);
        walkFilters.add(falseFilter1);
        final TestWalkFilter falseFilter2 = new TestWalkFilter();
        falseFilter2.beforeArray(false);
        falseFilter2.afterArray(false);
        falseFilter2.beforeCompound(false);
        falseFilter2.afterCompound(false);
        falseFilter2.beforeValue(false);
        falseFilter2.afterValue(false);
        walkFilters.add(falseFilter2);
        final AndWalkFilter walkFilter = new AndWalkFilter(walkFilters);
        final TestObject.DummyObject dummyObject = TestObject.createDummyObject();
        final FieldInfo dummyArrayFieldInfo = TestObject.DummyObject.typeInfo().getFields().get(3);
        final FieldInfo dummyCompoundFieldInfo = TestObject.DummyObject.typeInfo().getFields().get(1);
        final FieldInfo dummyFieldInfo = TestObject.DummyObject.typeInfo().getFields().get(0);

        assertFalse(walkFilter.beforeArray(dummyObject.getUnionArray(), dummyArrayFieldInfo));
        assertFalse(walkFilter.afterArray(dummyObject.getUnionArray(), dummyArrayFieldInfo));
        assertFalse(walkFilter.beforeCompound(dummyObject.getNested(), dummyCompoundFieldInfo,
                WalkerConst.NOT_ELEMENT));
        assertFalse(walkFilter.afterCompound(dummyObject.getNested(), dummyCompoundFieldInfo,
                WalkerConst.NOT_ELEMENT));
        assertFalse(walkFilter.beforeValue(dummyObject.getIdentifier(), dummyFieldInfo,
                WalkerConst.NOT_ELEMENT));
        assertFalse(walkFilter.afterValue(dummyObject.getIdentifier(), dummyFieldInfo,
                WalkerConst.NOT_ELEMENT));
    }

    @Test
    public void trueFalse()
    {
        final ArrayList<WalkFilter> walkFilters = new ArrayList<WalkFilter>();
        final TestWalkFilter trueFilter1 = new TestWalkFilter();
        walkFilters.add(trueFilter1);
        final TestWalkFilter falseFilter2 = new TestWalkFilter();
        falseFilter2.beforeArray(false);
        falseFilter2.afterArray(false);
        falseFilter2.beforeCompound(false);
        falseFilter2.afterCompound(false);
        falseFilter2.beforeValue(false);
        falseFilter2.afterValue(false);
        walkFilters.add(falseFilter2);
        final AndWalkFilter walkFilter = new AndWalkFilter(walkFilters);
        final TestObject.DummyObject dummyObject = TestObject.createDummyObject();
        final FieldInfo dummyArrayFieldInfo = TestObject.DummyObject.typeInfo().getFields().get(3);
        final FieldInfo dummyCompoundFieldInfo = TestObject.DummyObject.typeInfo().getFields().get(1);
        final FieldInfo dummyFieldInfo = TestObject.DummyObject.typeInfo().getFields().get(0);

        assertFalse(walkFilter.beforeArray(dummyObject.getUnionArray(), dummyArrayFieldInfo));
        assertFalse(walkFilter.afterArray(dummyObject.getUnionArray(), dummyArrayFieldInfo));
        assertFalse(walkFilter.beforeCompound(dummyObject.getNested(), dummyCompoundFieldInfo,
                WalkerConst.NOT_ELEMENT));
        assertFalse(walkFilter.afterCompound(dummyObject.getNested(), dummyCompoundFieldInfo,
                WalkerConst.NOT_ELEMENT));
        assertFalse(walkFilter.beforeValue(dummyObject.getIdentifier(), dummyFieldInfo,
                WalkerConst.NOT_ELEMENT));
        assertFalse(walkFilter.afterValue(dummyObject.getIdentifier(), dummyFieldInfo,
                WalkerConst.NOT_ELEMENT));
    }
}
