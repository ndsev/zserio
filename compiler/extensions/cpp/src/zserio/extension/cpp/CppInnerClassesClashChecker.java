package zserio.extension.cpp;

import java.util.Arrays;
import java.util.List;

import zserio.ast.BitmaskType;
import zserio.ast.SqlTableType;
import zserio.extension.common.DefaultTreeWalker;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ZserioToolPrinter;

/**
 * Clash checker for clashing of classes generated for zserio types with theirs inner classes.
 *
 * Checks that C++ code generator will not produce any clashes between generated classes and inner classes.
 * Note that C++ doesn't allow an inner class to have same name as its outer class.
 */
final class CppInnerClassesClashChecker extends DefaultTreeWalker
{
    @Override
    public boolean traverseTemplateInstantiations()
    {
        // we need to check class names with their inner classes names
        return true;
    }

    @Override
    public void beginBitmask(BitmaskType bitmaskType) throws ZserioExtensionException
    {
        final String className = bitmaskType.getName();
        if (BITMASK_INNER_CLASS_NAME.equals(className))
        {
            ZserioToolPrinter.printError(bitmaskType.getLocation(),
                    "Class name '" + className + "' generated for bitmask clashes with "
                            + "its inner class '" + className + "' generated in C++ code.");
            throw new ZserioExtensionException("Class name clash detected!");
        }
    }

    @Override
    public void beginSqlTable(SqlTableType sqlTableType) throws ZserioExtensionException
    {
        final String className = sqlTableType.getName();
        if (SQL_TABLE_INNER_CLASSES_NAMES.contains(className))
        {
            ZserioToolPrinter.printError(sqlTableType.getLocation(),
                    "Class name '" + className + "' generated for SQL table clashes with "
                            + "its inner class '" + className + "' generated in C++ code.");
            throw new ZserioExtensionException("Class name clash detected!");
        }
    }

    private static final String BITMASK_INNER_CLASS_NAME = "Values";
    private static final List<String> SQL_TABLE_INNER_CLASSES_NAMES =
            Arrays.asList("IParameterProvider", "Reader", "Row");
}