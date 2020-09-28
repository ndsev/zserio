package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.ZserioType;
import zserio.ast.ZserioTypeUtil;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.emit.common.ZserioEmitException;

public class DbOverviewDotTemplateData
{
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

    public List<Database> getDatabaseList()
    {
        return databaseList;
    }

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

        private final String name;
        private final String colorName;
        private final String docUrl;
        private final List<Table> tableList;
    }

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

        private final String fullName;
        private final String name;
        private final String docUrl;
    }

    private final List<Database> databaseList;
}
