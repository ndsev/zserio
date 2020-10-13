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
                    DbIndexToColorConverter.convert(databaseIndex)));
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
                final SqlTableType tableType = (SqlTableType)field.getTypeInstantiation().getBaseType();
                tables.add(new Table(context, tableType, field.getName()));
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
        public Table(TemplateDataContext context, SqlTableType tableType, String name)
        {
            this.name = name;
            fullTypeName = ZserioTypeUtil.getFullName(tableType);
            typeSymbol = SymbolTemplateDataCreator.createData(context, tableType);
        }

        public String getName()
        {
            return name;
        }

        public String getFullTypeName()
        {
            return fullTypeName;
        }

        public SymbolTemplateData getTypeSymbol()
        {
            return typeSymbol;
        }

        private final String name;
        private final String fullTypeName;
        private final SymbolTemplateData typeSymbol;
    }

    private final List<Database> databases;
}
