package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import zserio.ast.Field;
import zserio.ast.SqlConstraint;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;

public class DbStructureDotTemplateData
{
    public DbStructureDotTemplateData(TemplateDataContext context, SqlDatabaseType databaseType,
            int databaseIndex)
    {
        // create database with proper color
        final String databaseColor = DocEmitterTools.getDatabaseColor(databaseIndex);
        database = new Database(context, databaseType, databaseColor);

        final Iterable<Field> databaseFields = databaseType.getFields();
        for (Field databaseField : databaseFields)
            database.addTable(context, databaseField.getName());
    }

    public Database getDatabase()
    {
        return database;
    }

    public static class Database
    {
        public Database(TemplateDataContext context, SqlDatabaseType databaseType, String colorName)
        {
            symbol = SymbolTemplateDataCreator.createData(context, databaseType);
            this.colorName = colorName;

            nameToSqlTableTypeMap = new TreeMap<String, SqlTableType>();
            final Iterable<Field> databaseFields = databaseType.getFields();
            for (Field databaseField : databaseFields)
            {
                final SqlTableType tableType = (SqlTableType)databaseField.getTypeInstantiation().getType();
                final String tableName = databaseField.getName();
                nameToSqlTableTypeMap.put(tableName, tableType);
            }

            nameToTableMap = new TreeMap<String, Table>();
        }

        public Table addTable(TemplateDataContext context, String tableName)
        {
            Table table = nameToTableMap.get(tableName);
            if (table == null)
            {
                final SqlTableType tableType = nameToSqlTableTypeMap.get(tableName);
                table = new Table(context, tableType, tableName);
                nameToTableMap.put(tableName, table);
            }

            return table;
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
            return nameToTableMap.values();
        }

        private final SymbolTemplateData symbol;
        private final String colorName;
        private final Map<String, SqlTableType> nameToSqlTableTypeMap;
        private final Map<String, Table> nameToTableMap;
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
                fields.add(new TableFieldTemplateData(tableField, tableType.isFieldPrimaryKey(tableField)));
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
        public TableFieldTemplateData(Field fieldType, boolean isPrimaryKey)
        {
            name = StringHtmlUtil.escapeForHtml(fieldType.getName());
            typeName = StringHtmlUtil.escapeForHtml(fieldType.getTypeInstantiation().getType().getName());
            this.isPrimaryKey = isPrimaryKey;
            isNullAllowed = SqlConstraint.isNullAllowed(fieldType.getSqlConstraint());
        }

        public String getName()
        {
            return name;
        }

        public String getTypeName()
        {
            return typeName;
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
        private final String typeName;
        private final boolean isPrimaryKey;
        private final boolean isNullAllowed;
    }

    private final Database database;
}
