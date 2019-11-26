package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.ZserioType;
import zserio.ast.ZserioTypeUtil;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.emit.common.ZserioEmitException;

/**
 * The database structure overview data used for FreeMarker template during DOT generation.
 */
public class DbOverviewDotTemplateData
{
    /**
     * The constructor.
     *
     * @param databaseTypeList The list of all SQL database zserio types.
     * @param docRootPath      The root path of the generated documentation for links or null if links are
     *                         not required.
     *
     * @throws ZserioEmitException Throws in case of any internal error.
     */
    public DbOverviewDotTemplateData(List<SqlDatabaseType> databaseTypeList, String docRootPath)
            throws ZserioEmitException
    {
        databaseList = new ArrayList<Database>();
        int databaseIndex = 0;
        for (SqlDatabaseType databaseType : databaseTypeList)
        {
            databaseList.add(new Database(databaseType, DocEmitterTools.getDatabaseColor(databaseIndex),
                                          docRootPath));
            databaseIndex++;
        }
    }

    /**
     * Returns the list of the databases.
     */
    public List<Database> getDatabaseList()
    {
        return databaseList;
    }

    /**
     * Helper class to model the database used for FreeMarker template.
     */
    public static class Database
    {
        public Database(SqlDatabaseType databaseType, String colorName, String docRootPath)
                throws ZserioEmitException
        {
            this.name = databaseType.getName();
            this.colorName = colorName;
            docUrl = DocEmitterTools.getDocUrlFromType(docRootPath, databaseType);

            final List<zserio.ast.Field> databaseFieldTypeList = databaseType.getFields();
            tableList = new ArrayList<Table>();
            for (zserio.ast.Field field : databaseFieldTypeList)
            {
                final ZserioType fieldType = field.getTypeInstantiation().getType();
                tableList.add(new Table((SqlTableType) fieldType, databaseType.getName(),
                              field.getName(), docRootPath));
            }
        }

        public String getName()
        {
            return name;
        }

        public String getColorName()
        {
            return colorName;
        }

        public String getDocUrl()
        {
            return docUrl;
        }

        public List<Table> getTableList()
        {
            return tableList;
        }

        private final String        name;
        private final String        colorName;
        private final String        docUrl;
        private final List<Table>   tableList;
    }

    /**
     * Helper class to model the table stored in database used for FreeMarker template.
     */
    public static class Table
    {
        public Table(SqlTableType tableType, String databaseName, String tableName, String docRootPath)
                throws ZserioEmitException
        {
            fullName = ZserioTypeUtil.getFullName(tableType);
            name = tableName;
            docUrl = DocEmitterTools.getDocUrlFromType(docRootPath, tableType);
        }

        public String getFullTypeName()
        {
            return fullName;
        }

        public String getName()
        {
            return name;
        }

        public String getDocUrl()
        {
            return docUrl;
        }

        private final String                            fullName;
        private final String                            name;
        private final String                            docUrl;
    }

    private final List<Database>        databaseList;
}
