package zserio.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import zserio.antlr.util.ParserException;

/**
 * This class holds all created Zserio types.
 *
 * TODO:
 * It is called from ZserioTool.java only for now. However for the near future, it would be nice to have
 *
 * - tree and not container
 * - instance and not static
 * - usage from all emitters not only from ZserioTool.java only
 */
public class ZserioTypeContainer
{
    public static void addSqlDatabase(SqlDatabaseType databaseType)
    {
        sqlDatabaseList.add(databaseType);
    }

    public static void addSqlTable(SqlTableType tableType)
    {
        sqlTableList.add(tableType);
    }

    public static void add(ZserioType type)
    {
        container.add(type);
    }

    public static void walk(ZserioTypeVisitor visitor)
    {
        for (ZserioType type : container)
        {
            type.callVisitor(visitor);
        }
    }

    public static void check() throws ParserException
    {
        checkUniqueTypeNames(sqlDatabaseList);
        checkUniqueTypeNames(sqlTableList);
    }

    public static Iterable<SqlDatabaseType> getSqlDatabaseList()
    {
        return sqlDatabaseList;
    }

    private static <T extends TokenAST & ZserioType> void checkUniqueTypeNames(Iterable<T> typeList)
            throws ParserException
    {
        final Set<String> typeNameSet = new HashSet<String>();
        for (T type : typeList)
        {
            final String typeName = type.getName();
            if (!typeNameSet.add(typeName))
            {
                throw new ParserException(type, "The multiple definition of " + type.getText() + " " +
                        typeName + " is not allowed!");
            }
        }
    }

    private static List<ZserioType>  container = new ArrayList<ZserioType>();
    private static List<SqlDatabaseType> sqlDatabaseList = new ArrayList<SqlDatabaseType>();
    private static List<SqlTableType>    sqlTableList = new ArrayList<SqlTableType>();
}
