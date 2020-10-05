package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.ZserioType;
import zserio.ast.ZserioTypeUtil;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;

public class DbOverviewDotTemplateData
{
    public DbOverviewDotTemplateData(TemplateDataContext context, List<SqlDatabaseType> databaseTypes)
    {
        databases = new ArrayList<Database>();
        int databaseIndex = 0;
        for (SqlDatabaseType databaseType : databaseTypes)
        {
            databases.add(new Database(context, databaseType,
                    DocEmitterTools.getDatabaseColor(databaseIndex)));
            databaseIndex++;
        }
    }

    public Iterable<Database> getDatabases()
    {
        return databases;
    }

    public static class Database
    {
        public Database(TemplateDataContext context, SqlDatabaseType databaseType, String colorName)
        {
            symbol = SymbolTemplateDataCreator.createData(context, databaseType);
            this.colorName = colorName;

            final List<zserio.ast.Field> databaseFieldTypeList = databaseType.getFields();
            tables = new ArrayList<Table>();
            for (zserio.ast.Field field : databaseFieldTypeList)
            {
                final ZserioType fieldType = field.getTypeInstantiation().getType();
                tables.add(new Table(context, (SqlTableType) fieldType));
            }
        }

        public SymbolTemplateData getSymbol()
        {
            return symbol;
        }

        public String getColorName()
        {
            return colorName;
        }

        public Iterable<Table> getTables()
        {
            return tables;
        }

        private final SymbolTemplateData symbol;
        private final String colorName;
        private final List<Table> tables;
    }

    public static class Table
    {
        public Table(TemplateDataContext context, SqlTableType tableType)
        {
            fullName = ZserioTypeUtil.getFullName(tableType);
            symbol = SymbolTemplateDataCreator.createData(context, tableType);
        }

        public String getFullTypeName()
        {
            return fullName;
        }

        public SymbolTemplateData getSymbol()
        {
            return symbol;
        }

        private final String fullName;
        private final SymbolTemplateData symbol;
    }

    private final List<Database> databases;
}
