package zserio.runtime.walker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;

import test_object.WalkerBitmask;
import test_object.WalkerObject;

public class WalkerTest
{
    @Test
    public void walkNonCompound()
    {
        final DefaultWalkObserver defaultObserver = new DefaultWalkObserver();
        final DefaultWalkFilter defaultFilter = new DefaultWalkFilter();
        final Walker walker = new Walker(defaultObserver, defaultFilter);
        final WalkerBitmask walkerBitmask = new WalkerBitmask();

        assertThrows(ZserioError.class, () -> walker.walk(walkerBitmask));
    }

    @Test
    public void walk()
    {
        final TestWalkObserver observer = new TestWalkObserver();
        final DefaultWalkFilter defaultFilter = new DefaultWalkFilter();
        final Walker walker = new Walker(observer, defaultFilter);
        final WalkerObject walkerObject = TestObjectCreator.createWalkerObject();
        walker.walk(walkerObject);

        assertEquals(walkerObject, observer.getCaptures("beginRoot").get(0));
        assertEquals(walkerObject, observer.getCaptures("endRoot").get(0));

        assertEquals(2, observer.getCaptures("beginArray").size());
        assertEquals(walkerObject.getUnionArray(), observer.getCaptures("beginArray").get(0));
        assertEquals(walkerObject.getUnionArray()[2].getNestedArray(),
                observer.getCaptures("beginArray").get(1));

        assertEquals(2, observer.getCaptures("endArray").size());
        assertEquals(walkerObject.getUnionArray()[2].getNestedArray(), observer.getCaptures("endArray").get(0));
        assertEquals(walkerObject.getUnionArray(), observer.getCaptures("endArray").get(1));

        assertEquals(5, observer.getCaptures("beginCompound").size());
        assertEquals(walkerObject.getNested(), observer.getCaptures("beginCompound").get(0));
        assertEquals(walkerObject.getUnionArray()[0], observer.getCaptures("beginCompound").get(1));
        assertEquals(walkerObject.getUnionArray()[1], observer.getCaptures("beginCompound").get(2));
        assertEquals(walkerObject.getUnionArray()[2], observer.getCaptures("beginCompound").get(3));
        assertEquals(walkerObject.getUnionArray()[2].getNestedArray()[0],
                observer.getCaptures("beginCompound").get(4));

        assertEquals(5, observer.getCaptures("endCompound").size());
        assertEquals(walkerObject.getNested(), observer.getCaptures("endCompound").get(0));
        assertEquals(walkerObject.getUnionArray()[0], observer.getCaptures("endCompound").get(1));
        assertEquals(walkerObject.getUnionArray()[1], observer.getCaptures("endCompound").get(2));
        assertEquals(walkerObject.getUnionArray()[2].getNestedArray()[0],
                observer.getCaptures("endCompound").get(3));
        assertEquals(walkerObject.getUnionArray()[2], observer.getCaptures("endCompound").get(4));

        assertEquals(7, observer.getCaptures("visitValue").size());
        assertEquals((long)13, observer.getCaptures("visitValue").get(0));
        assertEquals("nested", observer.getCaptures("visitValue").get(1));
        assertEquals("test", observer.getCaptures("visitValue").get(2));
        assertEquals("1", observer.getCaptures("visitValue").get(3));
        assertEquals((long)2, observer.getCaptures("visitValue").get(4));
        assertEquals("nestedArray", observer.getCaptures("visitValue").get(5));
        assertEquals(null, observer.getCaptures("visitValue").get(6));
    }

    @Test
    public void walkWrongOptionalCondition()
    {
        // use case: optional condition states that the optional is used, but it is not set!
        final TestWalkObserver observer = new TestWalkObserver();
        final DefaultWalkFilter defaultFilter = new DefaultWalkFilter();
        final Walker walker = new Walker(observer, defaultFilter);
        final WalkerObject walkerObject = TestObjectCreator.createWalkerObject(13, false);
        walker.walk(walkerObject);

        assertEquals(walkerObject, observer.getCaptures("beginRoot").get(0));
        assertEquals(walkerObject, observer.getCaptures("endRoot").get(0));

        assertEquals(2, observer.getCaptures("beginArray").size());
        assertEquals(walkerObject.getUnionArray(), observer.getCaptures("beginArray").get(0));
        assertEquals(walkerObject.getUnionArray()[2].getNestedArray(),
                observer.getCaptures("beginArray").get(1));

        assertEquals(2, observer.getCaptures("endArray").size());
        assertEquals(walkerObject.getUnionArray()[2].getNestedArray(), observer.getCaptures("endArray").get(0));
        assertEquals(walkerObject.getUnionArray(), observer.getCaptures("endArray").get(1));

        assertEquals(4, observer.getCaptures("beginCompound").size());
        assertEquals(walkerObject.getUnionArray()[0], observer.getCaptures("beginCompound").get(0));
        assertEquals(walkerObject.getUnionArray()[1], observer.getCaptures("beginCompound").get(1));
        assertEquals(walkerObject.getUnionArray()[2], observer.getCaptures("beginCompound").get(2));
        assertEquals(walkerObject.getUnionArray()[2].getNestedArray()[0],
                observer.getCaptures("beginCompound").get(3));

        assertEquals(4, observer.getCaptures("endCompound").size());
        assertEquals(walkerObject.getUnionArray()[0], observer.getCaptures("endCompound").get(0));
        assertEquals(walkerObject.getUnionArray()[1], observer.getCaptures("endCompound").get(1));
        assertEquals(walkerObject.getUnionArray()[2].getNestedArray()[0],
                observer.getCaptures("endCompound").get(2));
        assertEquals(walkerObject.getUnionArray()[2], observer.getCaptures("endCompound").get(3));

        assertEquals(7, observer.getCaptures("visitValue").size());
        assertEquals((long)13, observer.getCaptures("visitValue").get(0));
        assertEquals(null, observer.getCaptures("visitValue").get(1));
        assertEquals("test", observer.getCaptures("visitValue").get(2));
        assertEquals("1", observer.getCaptures("visitValue").get(3));
        assertEquals((long)2, observer.getCaptures("visitValue").get(4));
        assertEquals("nestedArray", observer.getCaptures("visitValue").get(5));
        assertEquals(null, observer.getCaptures("visitValue").get(6));
    }

    @Test
    public void walkSkipCompound()
    {
        final TestWalkObserver observer = new TestWalkObserver();
        final TestWalkFilter filter = new TestWalkFilter();
        filter.beforeCompound(false);
        final Walker walker = new Walker(observer, filter);
        final WalkerObject walkerObject = TestObjectCreator.createWalkerObject();
        walker.walk(walkerObject);

        assertEquals(walkerObject, observer.getCaptures("beginRoot").get(0));
        assertEquals(walkerObject, observer.getCaptures("endRoot").get(0));

        assertEquals(1, observer.getCaptures("beginArray").size());
        assertEquals(walkerObject.getUnionArray(), observer.getCaptures("beginArray").get(0));

        assertEquals(1, observer.getCaptures("endArray").size());
        assertEquals(walkerObject.getUnionArray(), observer.getCaptures("endArray").get(0));

        assertEquals(0, observer.getCaptures("beginCompound").size());
        assertEquals(0, observer.getCaptures("endCompound").size());

        assertEquals(3, observer.getCaptures("visitValue").size());
        assertEquals((long)13, observer.getCaptures("visitValue").get(0));
        assertEquals("test", observer.getCaptures("visitValue").get(1));
        assertEquals(null, observer.getCaptures("visitValue").get(2));
    }

    @Test
    public void walkSkipSiblings()
    {
        final TestWalkObserver observer = new TestWalkObserver();
        final TestWalkFilter filter = new TestWalkFilter();
        filter.afterValue(false);
        final Walker walker = new Walker(observer, filter);
        final WalkerObject walkerObject = TestObjectCreator.createWalkerObject();
        walker.walk(walkerObject);

        assertEquals(walkerObject, observer.getCaptures("beginRoot").get(0));
        assertEquals(walkerObject, observer.getCaptures("endRoot").get(0));

        assertEquals(0, observer.getCaptures("beginArray").size());
        assertEquals(0, observer.getCaptures("endArray").size());

        assertEquals(0, observer.getCaptures("beginCompound").size());
        assertEquals(0, observer.getCaptures("endCompound").size());

        assertEquals(1, observer.getCaptures("visitValue").size());
        assertEquals((long)13, observer.getCaptures("visitValue").get(0));
    }

    @Test
    public void walkSkipAfterNested()
    {
        final TestWalkObserver observer = new TestWalkObserver();
        final TestWalkFilter filter = new TestWalkFilter();
        filter.afterCompound(false);
        final Walker walker = new Walker(observer, filter);
        final WalkerObject walkerObject = TestObjectCreator.createWalkerObject();
        walker.walk(walkerObject);

        assertEquals(walkerObject, observer.getCaptures("beginRoot").get(0));
        assertEquals(walkerObject, observer.getCaptures("endRoot").get(0));

        assertEquals(0, observer.getCaptures("beginArray").size());
        assertEquals(0, observer.getCaptures("endArray").size());

        assertEquals(1, observer.getCaptures("beginCompound").size());
        assertEquals(walkerObject.getNested(), observer.getCaptures("beginCompound").get(0));

        assertEquals(1, observer.getCaptures("endCompound").size());
        assertEquals(walkerObject.getNested(), observer.getCaptures("endCompound").get(0));

        assertEquals(2, observer.getCaptures("visitValue").size());
        assertEquals((long)13, observer.getCaptures("visitValue").get(0));
        assertEquals("nested", observer.getCaptures("visitValue").get(1));
    }

    @Test
    public void walkOnlyFirstElement()
    {
        final TestWalkObserver observer = new TestWalkObserver();
        final TestWalkFilter filter = new TestWalkFilter();
        filter.onlyFirstElement(true);
        final Walker walker = new Walker(observer, filter);
        final WalkerObject walkerObject = TestObjectCreator.createWalkerObject();
        walker.walk(walkerObject);

        assertEquals(walkerObject, observer.getCaptures("beginRoot").get(0));
        assertEquals(walkerObject, observer.getCaptures("endRoot").get(0));

        assertEquals(1, observer.getCaptures("beginArray").size());
        assertEquals(walkerObject.getUnionArray(), observer.getCaptures("beginArray").get(0));

        assertEquals(1, observer.getCaptures("endArray").size());
        assertEquals(walkerObject.getUnionArray(), observer.getCaptures("endArray").get(0));

        assertEquals(2, observer.getCaptures("beginCompound").size());
        assertEquals(walkerObject.getNested(), observer.getCaptures("beginCompound").get(0));
        assertEquals(walkerObject.getUnionArray()[0], observer.getCaptures("beginCompound").get(1));

        assertEquals(2, observer.getCaptures("endCompound").size());
        assertEquals(walkerObject.getNested(), observer.getCaptures("endCompound").get(0));
        assertEquals(walkerObject.getUnionArray()[0], observer.getCaptures("endCompound").get(1));

        assertEquals(5, observer.getCaptures("visitValue").size());
        assertEquals((long)13, observer.getCaptures("visitValue").get(0));
        assertEquals("nested", observer.getCaptures("visitValue").get(1));
        assertEquals("test", observer.getCaptures("visitValue").get(2));
        assertEquals("1", observer.getCaptures("visitValue").get(3));
        assertEquals(null, observer.getCaptures("visitValue").get(4));
    }
}
