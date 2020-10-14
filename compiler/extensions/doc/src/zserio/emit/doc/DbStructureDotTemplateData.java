package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.Field;
import zserio.ast.SqlConstraint;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;

public class DbStructureDotTemplateData
{
    public DbStructureDotTemplateData(TemplateDataContext context, SqlDatabaseType databaseType)
    {
        symbol = SymbolTemplateDataCreator.createData(context, databaseType);

        final Iterable<Field> databaseFields = databaseType.getFields();
        tables = new ArrayList<Table>();
        for (Field databaseField : databaseFields)
        {
            final SqlTableType tableType = (SqlTableType)databaseField.getTypeInstantiation().getBaseType();
            final String tableName = databaseField.getName();
            tables.add(new Table(context, tableType, tableName));
        }
    }

    public SymbolTemplateData getSymbol()
    {
        return symbol;
    }

    public Iterable<Table> getTables()
    {
        return tables;
    }

    public static class Table
    {
        public Table(TemplateDataContext context, SqlTableType tableType, String name)
        {
            this.name = name;

            packageName = tableType.getPackage().getPackageName().toString();
            typeSymbol = SymbolTemplateDataCreator.createData(context, tableType);

            fields = new ArrayList<TableFieldTemplateData>();
            final List<Field> tableFieldList = tableType.getFields();
            for (Field tableField : tableFieldList)
                fields.add(new TableFieldTemplateData(context, tableField,
                        tableType.isFieldPrimaryKey(tableField)));
        }

        public String getName()
        {
            return name;
        }

        public String getPackageName()
        {
            return packageName;
        }

        public SymbolTemplateData getTypeSymbol()
        {
            return typeSymbol;
        }

        public Iterable<TableFieldTemplateData> getFields()
        {
            return fields;
        }

        private final String name;
        private final String packageName;
        private final SymbolTemplateData typeSymbol;
        private final List<TableFieldTemplateData> fields;
    }

    public static class TableFieldTemplateData
    {
        public TableFieldTemplateData(TemplateDataContext context, Field fieldType, boolean isPrimaryKey)
        {
            name = fieldType.getName();
            typeSymbol = SymbolTemplateDataCreator.createData(context,
                    fieldType.getTypeInstantiation().getType());
            this.isPrimaryKey = isPrimaryKey;
            isNullAllowed = SqlConstraint.isNullAllowed(fieldType.getSqlConstraint());
        }

        public String getName()
        {
            return name;
        }

        public SymbolTemplateData getTypeSymbol()
        {
            return typeSymbol;
        }

        public boolean getIsPrimaryKey()
        {
            return isPrimaryKey;
        }

        public boolean getIsNullAllowed()
        {
            return isNullAllowed;
        }

        private final String name;
        private final SymbolTemplateData typeSymbol;
        private final boolean isPrimaryKey;
        private final boolean isNullAllowed;
    }

    private final SymbolTemplateData symbol;
    private final List<Table> tables;
}
