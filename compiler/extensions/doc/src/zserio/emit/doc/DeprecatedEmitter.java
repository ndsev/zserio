package zserio.emit.doc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.ConstType;
import zserio.ast.DocComment;
import zserio.ast.EnumItem;
import zserio.ast.EnumType;
import zserio.ast.Field;
import zserio.ast.Root;
import zserio.ast.StructureType;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.Subtype;
import zserio.ast.ZserioType;
import zserio.ast.UnionType;
import zserio.emit.common.ZserioEmitException;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class DeprecatedEmitter extends DefaultHtmlEmitter
{
    private final List<Item> items = new ArrayList<Item>();

    public DeprecatedEmitter(String outputPath)
    {
        super(outputPath);
    }

    /**
     *  Represents a deprecated Zserio-element
     */
    public static class Item
    {
        private Field         field;
        private CompoundType  fieldOwner;
        private ZserioType type;

        private EnumType      enumType;
        private EnumItem      enumItem;

        /**
         *  Constructors
         */

        public Item( Field field, CompoundType fieldOwner )
        {
            this.field = field;
            this.fieldOwner = fieldOwner;
        }

        public Item( ZserioType type )
        {
            this.type = type;
        }

        public Item( EnumItem enumItem,
                     EnumType enumType )
        {
            this.enumItem = enumItem;
            this.enumType = enumType;
        }


        /**
         *  What kind of Zserio-element the item represents
         */

        public boolean getIsField()
        {
            return field!=null;
        }

        public boolean getIsEnumItem()
        {
            return (enumItem!=null) && (enumType!=null);
        }

        /**
         *  IS FIELD
         */

        public Field getField()
        {
            return field;
        }

        public CompoundType getFieldOwner()
        {
            return fieldOwner;
        }

        public CompoundEmitter.FieldLinkedType getFieldLinkedType()
        {
            return new CompoundEmitter.FieldLinkedType( field );
        }

        public LinkedType getFieldCompoundLinkedType()
        {
            return new LinkedType(fieldOwner);
        }

        /**
         *  IS ENUMITEM
         */

        public EnumItem getEnumItem()
        {
            return enumItem;
        }

        public EnumType getEnumType()
        {
            return enumType;
        }

        public LinkedType getEnumLinkedType()
        {
            return new LinkedType( enumType );
        }

        /**
         *  FIELD NOR ENUMITEM
         */

        public ZserioType getType()
        {
            return type;
        }

        public LinkedType getLinkedType()
        {
            return new LinkedType( getType() );
        }
    }; // class Item

    public List<Item> getItems()
    {
        return items;
    }

    @Override
    public void beginConst(ConstType constType) throws ZserioEmitException
    {
        if (getIsDeprecated(constType.getDocComment()))
        {
            Item item = new Item(constType);
            items.add(item);
        }
    }

    @Override
    public void beginStructure(StructureType structureType) throws ZserioEmitException
    {
        if (getIsDeprecated(structureType.getDocComment()))
        {
            Item item = new Item(structureType);
            items.add(item);
        }

        handleFields(structureType);
    }

    @Override
    public void beginChoice(ChoiceType choiceType) throws ZserioEmitException
    {
        if (getIsDeprecated(choiceType.getDocComment()))
        {
            Item item = new Item(choiceType);
            items.add(item);
        }

        handleFields(choiceType);
    }


    @Override
    public void beginUnion(UnionType unionType) throws ZserioEmitException
    {
        if (getIsDeprecated(unionType.getDocComment()))
        {
            Item item = new Item(unionType);
            items.add(item);
        }

        handleFields(unionType);
    }

    public void handleFields( CompoundType ct ) throws ZserioEmitException
    {
        for( Field f : ct.getFields() )
        {
            if( getIsDeprecated( f.getDocComment() ) )
            {
                Item item = new Item( f, ct );
                items.add( item );
            }
        }
    }

    @Override
    public void beginEnumeration(EnumType enumType) throws ZserioEmitException
    {
        if (getIsDeprecated(enumType.getDocComment()))
        {
            Item item = new Item(enumType);
            items.add(item);
        }

        // handleEnumItems( EnumType et )
        for (EnumItem ei : enumType.getItems())
        {
            if (getIsDeprecated(ei.getDocComment()))
            {
                Item item = new Item(ei, enumType);
                items.add(item);
            }
        }
    }

    @Override
    public void beginSubtype(Subtype subtype) throws ZserioEmitException
    {
        if (getIsDeprecated(subtype.getDocComment()))
        {
            Item item = new Item(subtype);
            items.add(item);
        }
    }

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType) throws ZserioEmitException
    {
        if (getIsDeprecated(sqlDatabaseType.getDocComment()))
        {
            Item item = new Item(sqlDatabaseType);
            items.add(item);
        }

        handleFields(sqlDatabaseType);
    }

    @Override
    public void beginSqlTable(SqlTableType sqlTableType) throws ZserioEmitException
    {
        if (getIsDeprecated(sqlTableType.getDocComment()))
        {
            Item item = new Item(sqlTableType);
            items.add(item);
        }

        handleFields(sqlTableType);
    }

    public boolean getIsDeprecated(DocComment docComment) throws ZserioEmitException
    {
        boolean isDeprecated = false;
        if (docComment != null)
            isDeprecated = new DocCommentTemplateData(docComment).getIsDeprecated();

        return isDeprecated;
    };

    @Override
    public void endRoot(Root root) throws ZserioEmitException
    {
        try
        {
            Template tpl = cfg.getTemplate("doc/deprecated.html.ftl");
            openOutputFile(directory, "deprecated" + HTML_EXT);
            tpl.process( this, writer );
        }
        catch (IOException exception)
        {
            throw new ZserioEmitException(exception.getMessage());
        }
        catch (TemplateException exception)
        {
            throw new ZserioEmitException(exception.getMessage());
        }
        finally
        {
            if (writer != null)
                writer.close();
        }
    }
}; // class DreprecatedEmitter
