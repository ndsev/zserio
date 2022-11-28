package zserio.runtime.creator;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;

import test_object.CreatorBitmask;
import test_object.CreatorEnum;
import test_object.CreatorObject;

public class ZserioTreeCreatorTest
{
    @Test
    public void createObject()
    {
        final ZserioTreeCreator creator = new ZserioTreeCreator(CreatorObject.typeInfo());
        creator.beginRoot();
        final Object obj = creator.endRoot();
        assertTrue(obj instanceof CreatorObject);
        assertNotNull(obj);
    }

    @Test
    public void createObjectSetFields()
    {
        final ZserioTreeCreator creator = new ZserioTreeCreator(CreatorObject.typeInfo());
        creator.beginRoot();
        creator.setValue("value", (long)13);
        creator.setValue("text", "test");
        final Object obj = creator.endRoot();
        assertTrue(obj instanceof CreatorObject);
        assertNotNull(obj);

        final CreatorObject creatorObject = (CreatorObject)obj;
        assertEquals((long)13, creatorObject.getValue());
        assertEquals("test", creatorObject.getText());
    }

    @Test
    public void createObjectFull()
    {
        final ZserioTreeCreator creator = new ZserioTreeCreator(CreatorObject.typeInfo());
        creator.beginRoot();
        creator.setValue("value", (long)13);
        creator.setValue("text", "test");
        creator.beginCompound("nested");
        creator.setValue("value", (long)10);
        creator.setValue("text", "nested");
        creator.setValue("externData", new BitBuffer(new byte[] {0x3c}, 6));
        creator.setValue("bytesData", new byte[] {(byte)0xff});
        creator.setValue("creatorEnum", CreatorEnum.ONE);
        creator.setValue("creatorBitmask", CreatorBitmask.Values.WRITE);
        creator.endCompound();
        creator.beginArray("nestedArray");
        creator.beginCompoundElement();
        creator.setValue("value", (long)5);
        creator.setValue("text", "nestedArray");
        creator.setValue("creatorEnum", CreatorEnum.TWO);
        creator.setValue("creatorBitmask", CreatorBitmask.Values.READ);
        creator.endCompoundElement();
        creator.endArray();
        creator.beginArray("textArray");
        creator.addValueElement("this");
        creator.addValueElement("is");
        creator.addValueElement("text");
        creator.addValueElement("array");
        creator.endArray();
        creator.beginArray("externArray");
        creator.addValueElement(new BitBuffer(new byte[] {0x0f}, 4));
        creator.endArray();
        creator.beginArray("bytesArray");
        creator.addValueElement(new byte[] {(byte)0xca, (byte)0xfe});
        creator.endArray();
        creator.setValue("optionalBool", false);
        creator.beginCompound("optionalNested");
        creator.setValue("text", "optionalNested");
        creator.endCompound();
        final Object obj = creator.endRoot();
        assertTrue(obj instanceof CreatorObject);
        assertNotNull(obj);

        final CreatorObject creatorObject = (CreatorObject)obj;
        assertEquals(13, creatorObject.getValue());
        assertEquals("test", creatorObject.getText());
        assertEquals(13, creatorObject.getNested().getParam());
        assertEquals(10, creatorObject.getNested().getValue());
        assertEquals("nested", creatorObject.getNested().getText());
        assertArrayEquals(new byte[] {0x3c}, creatorObject.getNested().getExternData().getBuffer());
        assertArrayEquals(new byte[] {(byte)0xff}, creatorObject.getNested().getBytesData());
        assertEquals(6, creatorObject.getNested().getExternData().getBitSize());
        assertEquals(CreatorEnum.ONE, creatorObject.getNested().getCreatorEnum());
        assertEquals(CreatorBitmask.Values.WRITE, creatorObject.getNested().getCreatorBitmask());
        assertEquals(1, creatorObject.getNestedArray().length);
        assertEquals(5, creatorObject.getNestedArray()[0].getValue());
        assertEquals("nestedArray", creatorObject.getNestedArray()[0].getText());
        assertEquals(CreatorEnum.TWO, creatorObject.getNestedArray()[0].getCreatorEnum());
        assertEquals(CreatorBitmask.Values.READ, creatorObject.getNestedArray()[0].getCreatorBitmask());
        assertArrayEquals(new String[] {"this", "is", "text", "array"}, creatorObject.getTextArray());
        assertEquals(1, creatorObject.getExternArray().length);
        assertArrayEquals(new byte[] {0x0f}, creatorObject.getExternArray()[0].getBuffer());
        assertEquals(1, creatorObject.getBytesArray().length);
        assertArrayEquals(new byte[] {(byte)0xca, (byte)0xfe}, creatorObject.getBytesArray()[0]);
        assertEquals(4, creatorObject.getExternArray()[0].getBitSize());
        assertEquals(false, creatorObject.getOptionalBool());
        assertEquals("optionalNested", creatorObject.getOptionalNested().getText());
    }

    @Test
    public void exceptionsBeforeRoot()
    {
        final ZserioTreeCreator creator = new ZserioTreeCreator(CreatorObject.typeInfo());

        assertThrows(ZserioError.class, () -> creator.endRoot());
        assertThrows(ZserioError.class, () -> creator.beginArray("nestedArray"));
        assertThrows(ZserioError.class, () -> creator.endArray());
        assertThrows(ZserioError.class, () -> creator.beginCompound("nested"));
        assertThrows(ZserioError.class, () -> creator.endCompound());
        assertThrows(ZserioError.class, () -> creator.setValue("value", 13));
        assertThrows(ZserioError.class, () -> creator.beginCompoundElement());
        assertThrows(ZserioError.class, () -> creator.endCompoundElement());
        assertThrows(ZserioError.class, () -> creator.addValueElement(13));
    }

    @Test
    public void exceptionsInRoot()
    {
        final ZserioTreeCreator creator = new ZserioTreeCreator(CreatorObject.typeInfo());
        creator.beginRoot();

        assertThrows(ZserioError.class, () -> creator.beginRoot());
        assertThrows(ZserioError.class, () -> creator.beginArray("nonexistent"));
        assertThrows(ZserioError.class, () -> creator.beginArray("nested")); // not an array
        assertThrows(ZserioError.class, () -> creator.endArray());
        assertThrows(ZserioError.class, () -> creator.beginCompound("nonexistent"));
        assertThrows(ZserioError.class, () -> creator.beginCompound("nestedArray")); // is array
        assertThrows(ZserioError.class, () -> creator.endCompound());
        assertThrows(ZserioError.class, () -> creator.setValue("nonexistent", 13));
        assertThrows(ZserioError.class, () -> creator.setValue("nestedArray", 13)); // is array
        assertThrows(ZserioError.class, () -> creator.beginCompoundElement());
        assertThrows(ZserioError.class, () -> creator.endCompoundElement());
        assertThrows(ZserioError.class, () -> creator.addValueElement(13));
    }

    @Test
    public void exceptionsInCompound()
    {
        final ZserioTreeCreator creator = new ZserioTreeCreator(CreatorObject.typeInfo());
        creator.beginRoot();
        creator.beginCompound("nested");

        assertThrows(ZserioError.class, () -> creator.beginRoot());
        assertThrows(ZserioError.class, () -> creator.endRoot());
        assertThrows(ZserioError.class, () -> creator.beginArray("nonexistent"));
        assertThrows(ZserioError.class, () -> creator.beginArray("value")); // not an array
        assertThrows(ZserioError.class, () -> creator.endArray());
        assertThrows(ZserioError.class, () -> creator.beginCompound("nonexistent"));
        assertThrows(ZserioError.class, () -> creator.beginCompound("text")); // not a compound
        assertThrows(ZserioError.class, () -> creator.setValue("nonexistent", "test"));
        assertThrows(ZserioError.class, () -> creator.setValue("value", "test")); // wrong type
        assertThrows(ZserioError.class, () -> creator.beginCompoundElement());
        assertThrows(ZserioError.class, () -> creator.endCompoundElement());
        assertThrows(ZserioError.class, () -> creator.addValueElement(13));
        assertThrows(ZserioError.class, () -> creator.getElementType());
    }

    @Test
    public void exceptionsInCompoundArray()
    {
        final ZserioTreeCreator creator = new ZserioTreeCreator(CreatorObject.typeInfo());
        creator.beginRoot();
        creator.beginArray("nestedArray");

        assertThrows(ZserioError.class, () -> creator.beginRoot());
        assertThrows(ZserioError.class, () -> creator.endRoot());
        assertThrows(ZserioError.class, () -> creator.beginArray("nonexistent"));
        assertThrows(ZserioError.class, () -> creator.beginCompound("nonexistent"));
        assertThrows(ZserioError.class, () -> creator.endCompound());
        assertThrows(ZserioError.class, () -> creator.setValue("nonexistent", 13));
        assertThrows(ZserioError.class, () -> creator.endCompoundElement());
        assertThrows(ZserioError.class, () -> creator.addValueElement(13));
        assertThrows(ZserioError.class, () -> creator.getFieldType("nonexistent"));
    }

    @Test
    public void exceptionsInSimpleArray()
    {
        final ZserioTreeCreator creator = new ZserioTreeCreator(CreatorObject.typeInfo());
        creator.beginRoot();
        creator.beginArray("textArray");

        assertThrows(ZserioError.class, () -> creator.beginRoot());
        assertThrows(ZserioError.class, () -> creator.endRoot());
        assertThrows(ZserioError.class, () -> creator.beginArray("nonexistent"));
        assertThrows(ZserioError.class, () -> creator.beginCompound("nonexistent"));
        assertThrows(ZserioError.class, () -> creator.endCompound());
        assertThrows(ZserioError.class, () -> creator.setValue("nonexistent", 13));
        assertThrows(ZserioError.class, () -> creator.beginCompoundElement());
        assertThrows(ZserioError.class, () -> creator.endCompoundElement());
        assertThrows(ZserioError.class, () -> creator.beginCompoundElement());
        assertThrows(ZserioError.class, () -> creator.addValueElement(13)); // wrong type
        assertThrows(ZserioError.class, () -> creator.getFieldType("nonexistent"));
    }

    @Test
    public void exceptionsInCompoundElement()
    {
        final ZserioTreeCreator creator = new ZserioTreeCreator(CreatorObject.typeInfo());
        creator.beginRoot();
        creator.beginArray("nestedArray");
        creator.beginCompoundElement();

        assertThrows(ZserioError.class, () -> creator.beginRoot());
        assertThrows(ZserioError.class, () -> creator.endRoot());
        assertThrows(ZserioError.class, () -> creator.beginArray("nonexistent"));
        assertThrows(ZserioError.class, () -> creator.endArray());
        assertThrows(ZserioError.class, () -> creator.beginCompound("nonexistent"));
        assertThrows(ZserioError.class, () -> creator.endCompound());
        assertThrows(ZserioError.class, () -> creator.setValue("nonexistent", 13));
        assertThrows(ZserioError.class, () -> creator.beginCompoundElement());
        assertThrows(ZserioError.class, () -> creator.addValueElement(13));
    }
}
