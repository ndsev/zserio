package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.Field;
import zserio.ast.SqlConstraint;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.TypeInstantiation;

public class DbStructureDotTemplateData
{
    public DbStructureDotTemplateData(TemplateDataContext context, SqlDatabaseType databaseType)
    {
        symbol = SymbolTemplateDataCreator.createData(context, databaseType);

        final Iterable<Field> databaseFields = databaseType.getFields();
        tables = new ArrayList<Table>();
        for (Field databaseField : databaseFields)
        {
            final TypeInstantiation tableTypeInstantiation = databaseField.getTypeInstantiation();
            tables.add(new Table(context, tableTypeInstantiation, databaseField.getName()));
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
        public Table(TemplateDataContext context, TypeInstantiation tableTypeInstantiation, String name)
        {
            this.name = name;

            packageName = AstNodePackageNameMapper.getPackageName(
                    tableTypeInstantiation, context.getPackageMapper()).toString();
            typeSymbol = SymbolTemplateDataCreator.createData(context, tableTypeInstantiation);

            SqlTableType tableType = (SqlTableType)tableTypeInstantiation.getBaseType();

            fields = new ArrayList<TableFieldTemplateData>();
            final List<Field> tableFieldList = tableType.getFields();
            for (Field tableField : tableFieldList)
                fields.add(new TableFieldTemplateData(context, tableType, tableField,
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
        public TableFieldTemplateData(TemplateDataContext context, SqlTableType tableType, Field fieldType, boolean isPrimaryKey)
        {
            // TODO[Mi-L@]: We wan't to make link to the template's field, but we use field from the template's
            //              instantiation to get the field's name. Is it correct?
            if (tableType.getTemplate() != null)
                tableType = (SqlTableType)tableType.getTemplate();
            symbol = SymbolTemplateDataCreator.createData(context, tableType, fieldType);
            typeSymbol = SymbolTemplateDataCreator.createData(context, fieldType.getTypeInstantiation());
            this.isPrimaryKey = isPrimaryKey;
            isNullAllowed = SqlConstraint.isNullAllowed(fieldType.getSqlConstraint());
        }

        public SymbolTemplateData getSymbol()
        {
            return symbol;
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

        private final SymbolTemplateData symbol;
        private final SymbolTemplateData typeSymbol;
        private final boolean isPrimaryKey;
        private final boolean isNullAllowed;
    }

    private final SymbolTemplateData symbol;
    private final List<Table> tables;
}
