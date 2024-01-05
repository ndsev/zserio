package zserio.runtime.walker;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import zserio.runtime.typeinfo.FieldInfo;

import test_object.WalkerNested;
import test_object.WalkerObject;
import test_object.WalkerUnion;

public class RegexWalkFilterTest
{
    @Test
    public void regexAllMatch()
    {
        final RegexWalkFilter walkFilter = new RegexWalkFilter(".*");
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

    @Test
    public void regexPrefixMatch()
    {
        final RegexWalkFilter walkFilter = new RegexWalkFilter("nested\\..*");
        final WalkerObject walkerObject = TestObjectCreator.createWalkerObject();

        final FieldInfo identifierFieldInfo = WalkerObject.typeInfo().getFields().get(0);
        assertFalse(walkFilter.beforeValue(
                walkerObject.getIdentifier(), identifierFieldInfo, WalkerConst.NOT_ELEMENT));
        assertTrue(walkFilter.afterValue(
                walkerObject.getIdentifier(), identifierFieldInfo, WalkerConst.NOT_ELEMENT));

        final FieldInfo nestedFieldInfo = WalkerObject.typeInfo().getFields().get(1);
        assertTrue(
                walkFilter.beforeCompound(walkerObject.getNested(), nestedFieldInfo, WalkerConst.NOT_ELEMENT));
        final FieldInfo textFieldInfo = nestedFieldInfo.getTypeInfo().getFields().get(0);
        assertTrue(walkFilter.beforeValue(
                walkerObject.getNested().getText(), textFieldInfo, WalkerConst.NOT_ELEMENT));
        assertTrue(walkFilter.afterValue(
                walkerObject.getNested().getText(), textFieldInfo, WalkerConst.NOT_ELEMENT));
        assertTrue(
                walkFilter.afterCompound(walkerObject.getNested(), nestedFieldInfo, WalkerConst.NOT_ELEMENT));

        // ignore text

        final FieldInfo unionArrayFieldInfo = WalkerObject.typeInfo().getFields().get(3);
        assertFalse(walkFilter.beforeArray(walkerObject.getUnionArray(), unionArrayFieldInfo));
        assertTrue(walkFilter.afterArray(walkerObject.getUnionArray(), unionArrayFieldInfo));
    }

    @Test
    public void regexArrayMatch()
    {
        final RegexWalkFilter walkFilter = new RegexWalkFilter("unionArray\\[\\d+\\]\\.nes.*");
        final WalkerObject walkerObject = TestObjectCreator.createWalkerObject();

        final FieldInfo unionArrayFieldInfo = WalkerObject.typeInfo().getFields().get(3);
        assertTrue(walkFilter.beforeArray(walkerObject.getUnionArray(), unionArrayFieldInfo));

        assertFalse(walkFilter.beforeCompound(walkerObject.getUnionArray()[0], unionArrayFieldInfo, 0));
        assertTrue(walkFilter.afterCompound(walkerObject.getUnionArray()[0], unionArrayFieldInfo, 0));

        assertFalse(walkFilter.beforeCompound(walkerObject.getUnionArray()[1], unionArrayFieldInfo, 1));
        assertTrue(walkFilter.afterCompound(walkerObject.getUnionArray()[1], unionArrayFieldInfo, 1));

        assertTrue(walkFilter.beforeCompound(walkerObject.getUnionArray()[2], unionArrayFieldInfo, 2));
        assertTrue(walkFilter.afterCompound(walkerObject.getUnionArray()[2], unionArrayFieldInfo, 2));

        assertTrue(walkFilter.afterArray(walkerObject.getUnionArray(), unionArrayFieldInfo));
    }

    @Test
    public void regexArrayNoMatch()
    {
        final RegexWalkFilter walkFilter = new RegexWalkFilter("^unionArray\\[\\d*\\]\\.te.*");

        final WalkerUnion[] unionArray = new WalkerUnion[] {new WalkerUnion()};
        unionArray[0].setNestedArray(new WalkerNested[] {new WalkerNested("nestedArray")});
        final WalkerObject walkerObject =
                new WalkerObject(13, new WalkerNested("nested"), "test", unionArray, null);

        final FieldInfo unionArrayFieldInfo = WalkerObject.typeInfo().getFields().get(3);
        assertFalse(walkFilter.beforeArray(walkerObject.getUnionArray(), unionArrayFieldInfo));
        assertTrue(walkFilter.afterArray(walkerObject.getUnionArray(), unionArrayFieldInfo));
    }

    @Test
    public void regexNullCompoundMatch()
    {
        final RegexWalkFilter walkFilter = new RegexWalkFilter("nested");
        final WalkerObject walkerObject = TestObjectCreator.createWalkerObject(0, false);

        final FieldInfo nestedFieldInfo = WalkerObject.typeInfo().getFields().get(1);
        assertEquals(null, walkerObject.getNested());
        // note that the null compounds are processed as values!
        assertTrue(walkFilter.beforeValue(walkerObject.getNested(), nestedFieldInfo, WalkerConst.NOT_ELEMENT));
        assertTrue(walkFilter.afterValue(walkerObject.getNested(), nestedFieldInfo, WalkerConst.NOT_ELEMENT));
    }

    @Test
    public void regexNullCompoundNoMatch()
    {
        final RegexWalkFilter walkFilter = new RegexWalkFilter("^nested\\.text$");
        final WalkerObject walkerObject = TestObjectCreator.createWalkerObject(0, false);

        final FieldInfo nestedFieldInfo = WalkerObject.typeInfo().getFields().get(1);
        assertEquals(null, walkerObject.getNested());
        // note that the null compounds are processed as values!
        assertFalse(walkFilter.beforeValue(walkerObject.getNested(), nestedFieldInfo, WalkerConst.NOT_ELEMENT));
        assertTrue(walkFilter.afterValue(walkerObject.getNested(), nestedFieldInfo, WalkerConst.NOT_ELEMENT));
    }

    @Test
    public void regexNullArrayMatch()
    {
        final RegexWalkFilter walkFilter = new RegexWalkFilter("optionalUnionArray");
        final WalkerObject walkerObject = TestObjectCreator.createWalkerObject();

        final FieldInfo optionalUnionArrayFieldInfo = WalkerObject.typeInfo().getFields().get(4);
        assertEquals(null, walkerObject.getOptionalUnionArray());
        // note that the null arrays are processed as values!
        assertTrue(walkFilter.beforeValue(
                walkerObject.getOptionalUnionArray(), optionalUnionArrayFieldInfo, WalkerConst.NOT_ELEMENT));
        assertTrue(walkFilter.afterValue(
                walkerObject.getOptionalUnionArray(), optionalUnionArrayFieldInfo, WalkerConst.NOT_ELEMENT));
    }

    @Test
    public void regexNullArrayNoMatch()
    {
        final RegexWalkFilter walkFilter =
                new RegexWalkFilter("^optionalUnionArray\\.\\[\\d+\\]\\.nestedArray.*");
        final WalkerObject walkerObject = TestObjectCreator.createWalkerObject();

        final FieldInfo optionalUnionArrayFieldInfo = WalkerObject.typeInfo().getFields().get(4);
        assertEquals(null, walkerObject.getOptionalUnionArray());
        // note that the null arrays are processed as values!
        assertFalse(walkFilter.beforeValue(
                walkerObject.getOptionalUnionArray(), optionalUnionArrayFieldInfo, WalkerConst.NOT_ELEMENT));
        assertTrue(walkFilter.afterValue(
                walkerObject.getOptionalUnionArray(), optionalUnionArrayFieldInfo, WalkerConst.NOT_ELEMENT));
    }
}
