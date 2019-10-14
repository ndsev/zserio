package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import zserio.ast.Field;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.emit.common.ZserioEmitException;

/**
 * The database structure data used for FreeMarker template during DOT generation.
 */
public class DbStructureDotTemplateData
{
    /**
     * Constructor.
     *
     * @param databaseType           The type of the database for which to generated structure diagram.
     * @param databaseIndex          The index of the database in list of all SQL database zserio types.
     * @param docRootPath            The root path of the generated documentation for links or null if links
     *                               are not required.
     *
     * @throws ZserioEmitException Throws in case of any internal error.
     */
    public DbStructureDotTemplateData(SqlDatabaseType databaseType, String databaseColor, String docRootPath)
            throws ZserioEmitException
    {
        // create database with proper color
        database = new Database(databaseType, docRootPath, databaseColor);

        final Iterable<Field> databaseFieldList = databaseType.getFields();
        for (Field databaseField : databaseFieldList)
            database.addTable(databaseField.getName());
    }

    /**
     * Returns the name of the database for which the structure diagram is generated.
     */
    public String getDatabaseName()
    {
        return database.getName();
    }

    /**
     * Returns the databases.
     */
    public Database getDatabase()
    {
        return database;
    }

    /**
     * Helper class to model the database used for FreeMarker template.
     */
    public static class Database
    {
        public Database(SqlDatabaseType databaseType, String docRootPath, String colorName)
                throws ZserioEmitException
        {
            name = databaseType.getName();
            docUrl = DocEmitterTools.getDocUrlFromType(docRootPath, databaseType);
            this.colorName = colorName;

            nameToSqlTableTypeMap = new TreeMap<String, SqlTableType>();
            final Iterable<Field> databaseFieldList = databaseType.getFields();
            for (Field databaseField : databaseFieldList)
            {
                final SqlTableType tableType =
                        (SqlTableType)databaseField.getTypeInstantiation().getTypeReference().getType();
                final String tableName = databaseField.getName();
                nameToSqlTableTypeMap.put(tableName, tableType);
            }

            nameToTableMap = new TreeMap<String, Table>();
            this.docRootPath = docRootPath;
        }

        public Table addTable(String tableName) throws ZserioEmitException
        {
            Table table = nameToTableMap.get(tableName);
            if (table == null)
            {
                final SqlTableType tableType = nameToSqlTableTypeMap.get(tableName);
                final String docUrl = DocEmitterTools.getDocUrlFromType(docRootPath, tableType);
                table = new Table(tableType, tableName, docUrl);
                nameToTableMap.put(tableName, table);
            }

            return table;
        }

        public String getName()
        {
            return name;
        }

        public String getDocUrl()
        {
            return docUrl;
        }

        public String getColorName()
        {
            return colorName;
        }

        public Iterable<Table> getTableList()
        {
            return nameToTableMap.values();
        }

        private final String                    name;
        private final String                    docUrl;
        private final String                    colorName;
        private final Map<String, SqlTableType> nameToSqlTableTypeMap;
        private final Map<String, Table>        nameToTableMap;
        private final String                    docRootPath;
    }

    /**
     * Helper class to model the table stored in database used for FreeMarker template.
     */
    public static class Table
    {
        public Table(SqlTableType tableType, String name, String docUrl)
        {
            this.name = name;
            typeName = tableType.getName();
            packageName = tableType.getPackage().getPackageName().toString();
            this.docUrl = docUrl;

            fieldList = new ArrayList<TableFieldTemplateData>();
            final List<Field> tableFieldList = tableType.getFields();
            for (Field tableField : tableFieldList)
                fieldList.add(new TableFieldTemplateData(tableField, tableType.isFieldPrimaryKey(tableField)));
        }

        public String getName()
        {
            return name;
        }

        public String getTypeName()
        {
            return typeName;
        }

        public String getPackageName()
        {
            return packageName;
        }

        public String getDocUrl()
        {
            return docUrl;
        }

        public Iterable<TableFieldTemplateData> getFieldList()
        {
            return fieldList;
        }

        private final String    name;
        private final String    typeName;
        private final String    packageName;
        private final String    docUrl;

        private final List<TableFieldTemplateData>      fieldList;
    }

    /**
     * Helper class to model the table field used for FreeMarker template.
     */
    public static class TableFieldTemplateData
    {
        public TableFieldTemplateData(Field fieldType, boolean isPrimaryKey)
        {
            name = StringHtmlUtil.escapeForHtml(fieldType.getName());
            typeName = StringHtmlUtil.escapeForHtml(
                    fieldType.getTypeInstantiation().getTypeReference().getType().getName());
            this.isPrimaryKey = isPrimaryKey;
            isNullAllowed = fieldType.getSqlConstraint().isNullAllowed();
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

        private final String    name;
        private final String    typeName;
        private final boolean   isPrimaryKey;
        private final boolean   isNullAllowed;
    }

    private final Database            database;
}
