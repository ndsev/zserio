package zserio.runtime.walker;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import zserio.runtime.typeinfo.FieldInfo;

public class RegexWalkFilterTest
{
    @Test
    public void regexAllMatch()
    {
        final RegexWalkFilter walkFilter = new RegexWalkFilter(".*");
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
    public void regexPrefixMatch()
    {
        final RegexWalkFilter walkFilter = new RegexWalkFilter("nested\\..*");
        final TestObject.DummyObject dummyObject = TestObject.createDummyObject();

        final FieldInfo identifierFieldInfo = TestObject.DummyObject.typeInfo().getFields().get(0);
        assertFalse(walkFilter.beforeValue(dummyObject.getIdentifier(), identifierFieldInfo,
                WalkerConst.NOT_ELEMENT));
        assertTrue(walkFilter.afterValue(dummyObject.getIdentifier(), identifierFieldInfo,
                WalkerConst.NOT_ELEMENT));

        final FieldInfo nestedFieldInfo = TestObject.DummyObject.typeInfo().getFields().get(1);
        assertTrue(walkFilter.beforeCompound(dummyObject.getNested(), nestedFieldInfo,
                WalkerConst.NOT_ELEMENT));
        final FieldInfo textFieldInfo = nestedFieldInfo.getTypeInfo().getFields().get(0);
        assertTrue(walkFilter.beforeValue(dummyObject.getNested().getText(), textFieldInfo,
                WalkerConst.NOT_ELEMENT));
        assertTrue(walkFilter.afterValue(dummyObject.getNested().getText(), textFieldInfo,
                WalkerConst.NOT_ELEMENT));
        assertTrue(walkFilter.afterCompound(dummyObject.getNested(), nestedFieldInfo,
                WalkerConst.NOT_ELEMENT));

        // ignore text

        final FieldInfo unionArrayFieldInfo = TestObject.DummyObject.typeInfo().getFields().get(3);
        assertFalse(walkFilter.beforeArray(dummyObject.getUnionArray(), unionArrayFieldInfo));
        assertTrue(walkFilter.afterArray(dummyObject.getUnionArray(), unionArrayFieldInfo));
    }

    @Test
    public void regexArrayMatch()
    {
        final RegexWalkFilter walkFilter = new RegexWalkFilter("unionArray\\[\\d+\\]\\.nes.*");
        final TestObject.DummyObject dummyObject = TestObject.createDummyObject();

        final FieldInfo unionArrayFieldInfo = TestObject.DummyObject.typeInfo().getFields().get(3);
        assertTrue(walkFilter.beforeArray(dummyObject.getUnionArray(), unionArrayFieldInfo));

        assertFalse(walkFilter.beforeCompound(dummyObject.getUnionArray()[0], unionArrayFieldInfo, 0));
        assertTrue(walkFilter.afterCompound(dummyObject.getUnionArray()[0], unionArrayFieldInfo, 0));

        assertFalse(walkFilter.beforeCompound(dummyObject.getUnionArray()[1], unionArrayFieldInfo, 1));
        assertTrue(walkFilter.afterCompound(dummyObject.getUnionArray()[1], unionArrayFieldInfo, 1));

        assertTrue(walkFilter.beforeCompound(dummyObject.getUnionArray()[2], unionArrayFieldInfo, 2));
        assertTrue(walkFilter.afterCompound(dummyObject.getUnionArray()[2], unionArrayFieldInfo, 2));

        assertTrue(walkFilter.afterArray(dummyObject.getUnionArray(), unionArrayFieldInfo));
    }

    @Test
    public void regexArrayNoMatch()
    {
        final RegexWalkFilter walkFilter = new RegexWalkFilter("^unionArray\\[\\d*\\]\\.te.*");

        final TestObject.DummyUnion[] unionArray = new TestObject.DummyUnion[] { new TestObject.DummyUnion() };
        unionArray[0].setNestedArray(
                new TestObject.DummyNested[] { new TestObject.DummyNested("nestedArray") });
        final TestObject.DummyObject dummyObject =
                new TestObject.DummyObject(13, new TestObject.DummyNested("nested"), "test", unionArray, null);

        final FieldInfo unionArrayFieldInfo = TestObject.DummyObject.typeInfo().getFields().get(3);
        assertFalse(walkFilter.beforeArray(dummyObject.getUnionArray(), unionArrayFieldInfo));
        assertTrue(walkFilter.afterArray(dummyObject.getUnionArray(), unionArrayFieldInfo));
    }

    @Test
    public void regexNullCompoundMatch()
    {
        final RegexWalkFilter walkFilter = new RegexWalkFilter("nested");
        final TestObject.DummyObject dummyObject = TestObject.createDummyObject(0, false);

        final FieldInfo nestedFieldInfo = TestObject.DummyObject.typeInfo().getFields().get(1);
        assertEquals(null, dummyObject.getNested());
        // note that the null compounds are processed as values!
        assertTrue(walkFilter.beforeValue(dummyObject.getNested(), nestedFieldInfo, WalkerConst.NOT_ELEMENT));
        assertTrue(walkFilter.afterValue(dummyObject.getNested(), nestedFieldInfo, WalkerConst.NOT_ELEMENT));
    }

    @Test
    public void regexNullCompoundNoMatch()
    {
        final RegexWalkFilter walkFilter = new RegexWalkFilter("^nested\\.text$");
        final TestObject.DummyObject dummyObject = TestObject.createDummyObject(0, false);

        final FieldInfo nestedFieldInfo = TestObject.DummyObject.typeInfo().getFields().get(1);
        assertEquals(null, dummyObject.getNested());
        // note that the null compounds are processed as values!
        assertFalse(walkFilter.beforeValue(dummyObject.getNested(), nestedFieldInfo, WalkerConst.NOT_ELEMENT));
        assertTrue(walkFilter.afterValue(dummyObject.getNested(), nestedFieldInfo, WalkerConst.NOT_ELEMENT));
    }

    @Test
    public void regexNullArrayMatch()
    {
        final RegexWalkFilter walkFilter = new RegexWalkFilter("optionalUnionArray");
        final TestObject.DummyObject dummyObject = TestObject.createDummyObject();

        final FieldInfo optionalUnionArrayFieldInfo = TestObject.DummyObject.typeInfo().getFields().get(4);
        assertEquals(null, dummyObject.getOptionalUnionArray());
        // note that the null arrays are processed as values!
        assertTrue(walkFilter.beforeValue(dummyObject.getOptionalUnionArray(), optionalUnionArrayFieldInfo,
                WalkerConst.NOT_ELEMENT));
        assertTrue(walkFilter.afterValue(dummyObject.getOptionalUnionArray(), optionalUnionArrayFieldInfo,
                WalkerConst.NOT_ELEMENT));
    }

    @Test
    public void regexNullArrayNoMatch()
    {
        final RegexWalkFilter walkFilter =
                new RegexWalkFilter("^optionalUnionArray\\.\\[\\d+\\]\\.nestedArray.*");
        final TestObject.DummyObject dummyObject = TestObject.createDummyObject();

        final FieldInfo optionalUnionArrayFieldInfo = TestObject.DummyObject.typeInfo().getFields().get(4);
        assertEquals(null, dummyObject.getOptionalUnionArray());
        // note that the null arrays are processed as values!
        assertFalse(walkFilter.beforeValue(dummyObject.getOptionalUnionArray(), optionalUnionArrayFieldInfo,
                WalkerConst.NOT_ELEMENT));
        assertTrue(walkFilter.afterValue(dummyObject.getOptionalUnionArray(), optionalUnionArrayFieldInfo,
                WalkerConst.NOT_ELEMENT));
    }
}
