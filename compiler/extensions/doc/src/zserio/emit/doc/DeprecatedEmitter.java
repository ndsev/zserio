package zserio.emit.doc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import antlr.collections.AST;
import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.ConstType;
import zserio.ast.EnumItem;
import zserio.ast.EnumType;
import zserio.ast.Field;
import zserio.ast.StructureType;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.Subtype;
import zserio.ast.ZserioType;
import zserio.ast.UnionType;
import zserio.ast.doc.DocCommentToken;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class DeprecatedEmitter extends DefaultHtmlEmitter
{
    private List<Item> items = new ArrayList<Item>();

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
    public void endConst(AST c)
    {
        if (!(c instanceof ConstType))
            throw new ZserioEmitHtmlException("Unexpected token type in endConst!");
        ConstType ct = (ConstType)c;

        if( getIsDeprecated(ct.getDocComment()) )
        {
            Item item = new Item( ct );
            items.add( item );
        }
    }

    @Override
    public void endStructure(AST s)
    {
        if (!(s instanceof StructureType))
            throw new ZserioEmitHtmlException("Unexpected token type in endStructure!");
        StructureType st = (StructureType)s;

        if( getIsDeprecated(st.getDocComment()) )
        {
            Item item = new Item( st );
            items.add( item );
        }

        handleFields( st );
    }

    @Override
    public void endChoice(AST c)
    {
        if (!(c instanceof ChoiceType))
            throw new ZserioEmitHtmlException("Unexpected token type in endChoice!");
        ChoiceType ct = (ChoiceType)c;

        if( getIsDeprecated(ct.getDocComment()) )
        {
            Item item = new Item( ct );
            items.add( item );
        }

        handleFields( ct );
    }


    @Override
    public void endUnion(AST u)
    {
        if (!(u instanceof UnionType))
            throw new ZserioEmitHtmlException("Unexpected token type in endUnion!");
        UnionType ut = (UnionType)u;

        if( getIsDeprecated(ut.getDocComment()) )
        {
            Item item = new Item( ut );
            items.add( item );
        }

        handleFields( ut );
    }

    public void handleFields( CompoundType ct )
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
    public void endEnumeration(AST e)
    {
        if (!(e instanceof EnumType))
            throw new ZserioEmitHtmlException("Unexpected token type in endEnumeration!");
        EnumType et = (EnumType)e;

        if( getIsDeprecated(et.getDocComment()) )
        {
            Item item = new Item( et );
            items.add( item );
        }

        // handleEnumItems( EnumType et )
        for( EnumItem ei : et.getItems() )
        {
            ei.setEnumType( et );
            if( getIsDeprecated( ei.getDocComment()) )
            {
                Item item = new Item( ei, et );
                items.add( item );
            }
        }
    }

    @Override
    public void endSubtype(AST s)
    {
        if (!(s instanceof Subtype))
            throw new ZserioEmitHtmlException("Unexpected token type in endSubtype!");
        Subtype st = (Subtype)s;

        if( getIsDeprecated(st.getDocComment()) )
        {
            Item item = new Item( st );
            items.add( item );
        }
    }

    @Override
    public void endSqlDatabase(AST s)
    {
        if (!(s instanceof SqlDatabaseType))
            throw new ZserioEmitHtmlException("Unexpected token type in endSqlDatabase!");
        SqlDatabaseType sd = (SqlDatabaseType)s;

        if( getIsDeprecated(sd.getDocComment()) )
        {
            Item item = new Item( sd );
            items.add( item );
        }

        handleFields( sd );
    }

    @Override
    public void endSqlTable(AST s)
    {
        if (!(s instanceof SqlTableType))
            throw new ZserioEmitHtmlException("Unexpected token type in endSqlTable!");
        SqlTableType st = (SqlTableType)s;

        if( getIsDeprecated(st.getDocComment()) )
        {
            Item item = new Item( st );
            items.add( item );
        }

        handleFields( st );
    }

    public boolean getIsDeprecated(DocCommentToken docCommentToken)
    {
        boolean isDeprecated = false;
        if (docCommentToken != null)
            isDeprecated = docCommentToken.isDeprecated();

        return isDeprecated;
    };

    @Override
    public void endRoot()
    {
        try
        {
            Template tpl = cfg.getTemplate("doc/deprecated.html.ftl");
            openOutputFile(directory, "deprecated" + HTML_EXT);
            tpl.process( this, writer );
        }
        catch (IOException exc)
        {
            throw new ZserioEmitHtmlException(exc);
        }
        catch (TemplateException exc)
        {
            throw new ZserioEmitHtmlException(exc);
        }
        finally
        {
            if (writer != null)
                writer.close();
        }
    }
}; // class DreprecatedEmitter
