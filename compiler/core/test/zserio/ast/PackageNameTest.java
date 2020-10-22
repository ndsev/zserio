package zserio.ast;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

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
        builder.addId("nested");
        assertEquals("nested", builder.removeLastId());
        assertEquals("top.level.package", builder.get().toString());
    }

    @Test
    public void builderRemoveFirstId()
    {
        final PackageName.Builder builder = new PackageName.Builder();
        builder.addId("company");
        builder.addId("top");
        builder.addId("level");
        builder.addId("package");
        assertEquals("company", builder.removeFirstId());
        assertEquals("top.level.package", builder.get().toString());
    }
}
