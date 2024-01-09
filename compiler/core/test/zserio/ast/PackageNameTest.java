package zserio.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class PackageNameTest
{
    @Test
    public void builderAddId()
    {
        final PackageName.Builder builder = new PackageName.Builder();
        builder.addId("top");
        builder.addId("level");
        builder.addId("package");
        assertEquals("top.level.package", builder.get().toString());
    }

    @Test
    public void builderAddIds()
    {
        final PackageName.Builder builder = new PackageName.Builder();
        final List<String> ids = new ArrayList<String>();
        ids.add("top");
        ids.add("level");
        ids.add("package");
        builder.addIds(ids);
        assertEquals("top.level.package", builder.get().toString());
    }

    @Test
    public void builderAppend()
    {
        final PackageName.Builder builderTop = new PackageName.Builder();
        builderTop.addId("top");
        final PackageName.Builder builderLevel = new PackageName.Builder();
        builderLevel.addId("level");
        final PackageName.Builder builderPackage = new PackageName.Builder();
        builderPackage.addId("package");
        final PackageName.Builder builder = new PackageName.Builder();
        builder.append(builderTop.get());
        builder.append(builderLevel.get());
        builder.append(builderPackage.get());
        assertEquals("top.level.package", builder.get().toString());
    }

    @Test
    public void builderRemoveLastId()
    {
        final PackageName.Builder builder = new PackageName.Builder();
        builder.addId("top");
        builder.addId("level");
        builder.addId("package");

        assertEquals("package", builder.removeLastId());
        assertEquals("top.level", builder.get().toString());

        assertEquals("level", builder.removeLastId());
        assertEquals("top", builder.get().toString());

        assertEquals("top", builder.removeLastId());
        assertEquals("", builder.get().toString());

        assertEquals(null, builder.removeLastId());
    }

    @Test
    public void builderRemoveFirstId()
    {
        final PackageName.Builder builder = new PackageName.Builder();
        builder.addId("top");
        builder.addId("level");
        builder.addId("package");

        assertEquals("top", builder.removeFirstId());
        assertEquals("level.package", builder.get().toString());

        assertEquals("level", builder.removeFirstId());
        assertEquals("package", builder.get().toString());

        assertEquals("package", builder.removeFirstId());
        assertEquals("", builder.get().toString());

        assertEquals(null, builder.removeFirstId());
    }
}
