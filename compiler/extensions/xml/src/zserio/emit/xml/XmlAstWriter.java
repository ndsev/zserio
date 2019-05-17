package zserio.emit.xml;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import zserio.ast.ArrayType;
import zserio.ast.AstNode;
import zserio.ast.BitFieldType;
import zserio.ast.BooleanType;
import zserio.ast.ChoiceCase;
import zserio.ast.ChoiceCaseExpression;
import zserio.ast.ChoiceDefault;
import zserio.ast.ChoiceType;
import zserio.ast.ConstType;
import zserio.ast.DocElement;
import zserio.ast.DocLine;
import zserio.ast.DocLineElement;
import zserio.ast.DocMultiline;
import zserio.ast.DocTagDeprecated;
import zserio.ast.EnumItem;
import zserio.ast.EnumType;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.FloatType;
import zserio.ast.FunctionType;
import zserio.ast.Import;
import zserio.ast.Parameter;
import zserio.ast.Root;
import zserio.ast.Package;
import zserio.ast.Rpc;
import zserio.ast.ServiceType;
import zserio.ast.SqlConstraint;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.StdIntegerType;
import zserio.ast.StringType;
import zserio.ast.StructureType;
import zserio.ast.Subtype;
import zserio.ast.SymbolReference;
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeReference;
import zserio.ast.UnionType;
import zserio.ast.VarIntegerType;
import zserio.ast.ZserioAstVisitor;
import zserio.ast.DocComment;
import zserio.ast.DocParagraph;
import zserio.ast.DocTagParam;
import zserio.ast.DocTagSee;
import zserio.ast.DocTagTodo;
import zserio.ast.DocText;
import zserio.ast.ZserioType;
import zserio.emit.common.ZserioEmitException;

/**
 * Abstract Syntax Tree (AST) visitor for XML writer.
 *
 * This visitor converts whole AST into XML format and offers possibility to save created XML to the given file.
 */
public class XmlAstWriter implements ZserioAstVisitor
{
    /**
     * Constructor.
     *
     * @throws ZserioEmitException Throws in case of XML parser configuration error.
     */
    public XmlAstWriter() throws ZserioEmitException
    {
        try
        {
            // create XML doc builder
            final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            xmlDoc = docBuilder.newDocument();
        }
        catch (ParserConfigurationException excpt)
        {
            throw new ZserioEmitException(excpt.getMessage());
        }
    }

    /**
     * Saves AST in the XML format to the file.
     *
     * @param xmlFile XML file where to write whole AST.
     *
     * @throws ZserioEmitException Throws in case of XML transformer error.
     */
    public void save(File xmlFile) throws ZserioEmitException
    {
        try
        {
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            final DOMSource source = new DOMSource(xmlDoc);
            final StreamResult result = new StreamResult(xmlFile);
            transformer.transform(source, result);
        }
        catch (TransformerConfigurationException excpt)
        {
            throw new ZserioEmitException(excpt.getMessage());
        }
        catch (TransformerException excpt)
        {
            throw new ZserioEmitException(excpt.getMessage());
        }
    }

    @Override
    public void visitRoot(Root root)
    {
        currentXmlElement = xmlDoc.createElement("ROOT");
        xmlDoc.appendChild(currentXmlElement);
        root.visitChildren(this);
    }

    @Override
    public void visitPackage(Package unitPackage)
    {
        final Element xmlElement = xmlDoc.createElement("PACKAGE");
        xmlElement.setAttribute("packageName", unitPackage.getPackageName().toString());
        visitAstNode(unitPackage, xmlElement);
    }

    @Override
    public void visitImport(Import unitImport)
    {
        final Element xmlElement = xmlDoc.createElement("IMPORT");
        xmlElement.setAttribute("importedPackageName", unitImport.getImportedPackageName().toString());

        final String importedTypeName = unitImport.getImportedTypeName();
        if (importedTypeName != null)
            xmlElement.setAttribute("importedTypeName", importedTypeName);
        visitAstNode(unitImport, xmlElement);
    }

    @Override
    public void visitConstType(ConstType constType)
    {
        visitZserioType(constType, "CONST");
    }

    @Override
    public void visitSubtype(Subtype subtype)
    {
        visitZserioType(subtype, "SUBTYPE");
    }

    @Override
    public void visitStructureType(StructureType structureType)
    {
        visitZserioType(structureType, "STRUCTURE");
    }

    @Override
    public void visitChoiceType(ChoiceType choiceType)
    {
        visitZserioType(choiceType, "CHOICE");
    }

    @Override
    public void visitUnionType(UnionType unionType)
    {
        visitZserioType(unionType, "UNION");
    }

    @Override
    public void visitEnumType(EnumType enumType)
    {
        visitZserioType(enumType, "ENUM");
    }

    @Override
    public void visitSqlTableType(SqlTableType sqlTableType)
    {
        visitZserioType(sqlTableType, "SQL_TABLE");
    }

    @Override
    public void visitSqlDatabaseType(SqlDatabaseType sqlDatabaseType)
    {
        visitZserioType(sqlDatabaseType, "SQL_DATABASE");
    }

    @Override
    public void visitServiceType(ServiceType serviceType)
    {
        visitZserioType(serviceType, "SERVICE");
    }

    @Override
    public void visitField(Field field)
    {
        final Element fieldXmlElement = xmlDoc.createElement("FIELD");
        fieldXmlElement.setAttribute("name", field.getName());
        if (field.getIsOptional())
            fieldXmlElement.setAttribute("isOptional", "true");

        currentXmlElement.appendChild(fieldXmlElement);
        final Element oldCurrentXmlElement = currentXmlElement;
        currentXmlElement = fieldXmlElement;

        final ZserioType fieldType = field.getFieldType();
        fieldType.accept(this);

        final Expression alignmentExpr = field.getAlignmentExpr();
        if (alignmentExpr != null)
        {
            rootExprElementName = "ALIGN_EXPRESSION";
            alignmentExpr.accept(this);
        }

        final Expression offsetExpr = field.getOffsetExpr();
        if (offsetExpr != null)
        {
            rootExprElementName = "OFFSET_EXPRESSION";
            offsetExpr.accept(this);
        }

        final Expression initializerExpr = field.getInitializerExpr();
        if (initializerExpr != null)
        {
            rootExprElementName = "INITIALIZER_EXPRESSION";
            initializerExpr.accept(this);
        }

        final Expression optionalClauseExpr = field.getOptionalClauseExpr();
        if (optionalClauseExpr != null)
        {
            rootExprElementName = "OPTIONAL_EXPRESSION";
            optionalClauseExpr.accept(this);
        }

        final Expression constraintExpr = field.getConstraintExpr();
        if (constraintExpr != null)
        {
            rootExprElementName = "CONSTRAINT_EXPRESSION";
            constraintExpr.accept(this);
        }

        final SqlConstraint sqlConstraint = field.getSqlConstraint();
        if (sqlConstraint != null)
            sqlConstraint.accept(this);

        currentXmlElement = oldCurrentXmlElement;
    }

    @Override
    public void visitChoiceCase(ChoiceCase choiceCase)
    {
        visitAstNode(choiceCase, "CASE");
    }

    @Override
    public void visitChoiceCaseExpression(ChoiceCaseExpression choiceCaseExpression)
    {
        visitAstNode(choiceCaseExpression, "CASE_EXPRESSION");
    }

    @Override
    public void visitChoiceDefault(ChoiceDefault choiceDefault)
    {
        visitAstNode(choiceDefault, "DEFAULT");
    }

    @Override
    public void visitEnumItem(EnumItem enumItem)
    {
        final Element xmlElement = xmlDoc.createElement("ITEM");
        xmlElement.setAttribute("name", enumItem.getName());
        visitAstNode(enumItem, xmlElement);
    }

    @Override
    public void visitSqlConstraint(SqlConstraint sqlConstraint)
    {
        visitAstNode(sqlConstraint, "SQL_CONSTRAINT");
    }

    @Override
    public void visitRpc(Rpc rpc)
    {
        visitAstNode(rpc, "RPC");
    }

    @Override
    public void visitFunctionType(FunctionType functionType)
    {
        visitZserioType(functionType, "FUNCTION");
    }

    @Override
    public void visitParameter(Parameter parameter)
    {
        final Element xmlElement = xmlDoc.createElement("PARAMETER");
        xmlElement.setAttribute("name", parameter.getName());
        visitAstNode(parameter, xmlElement);
    }

    @Override
    public void visitExpression(Expression expression)
    {
        final String xmlElementName = (rootExprElementName == null) ? "EXPRESSION" : rootExprElementName;
        final Element xmlElement = xmlDoc.createElement(xmlElementName);
        xmlElement.setAttribute("text", expression.getText());
        rootExprElementName = null;
        visitAstNode(expression, xmlElement);
    }

    @Override
    public void visitArrayType(ArrayType arrayType)
    {
        visitZserioType(arrayType, "ARRAY");
    }

    @Override
    public void visitTypeInstantiation(TypeInstantiation typeInstantiation)
    {
        visitZserioType(typeInstantiation, "TYPE_INSTANTIATION");
    }

    @Override
    public void visitTypeReference(TypeReference typeReference)
    {
        visitZserioType(typeReference, "TYPE_REFERENCE");
    }

    @Override
    public void visitStdIntegerType(StdIntegerType stdIntegerType)
    {
        visitZserioType(stdIntegerType, "STD_INTEGER");
    }

    @Override
    public void visitVarIntegerType(VarIntegerType varIntegerType)
    {
        visitZserioType(varIntegerType, "VAR_INTEGER");
    }

    @Override
    public void visitBitFieldType(BitFieldType bitFieldType)
    {
        visitZserioType(bitFieldType, "BIT_FIELD");
    }

    @Override
    public void visitBooleanType(BooleanType booleanType)
    {
        visitZserioType(booleanType, "BOOLEAN");
    }

    @Override
    public void visitStringType(StringType stringType)
    {
        visitZserioType(stringType, "STRING");
    }

    @Override
    public void visitFloatType(FloatType floatType)
    {
        visitZserioType(floatType, "FLOAT");
    }

    @Override
    public void visitDocComment(DocComment docComment)
    {
        visitAstNode(docComment, "DOC_COMMENT");
    }

    @Override
    public void visitDocParagraph(DocParagraph docParagraph)
    {
        visitAstNode(docParagraph, "DOC_PARAGRAPH");
    }

    @Override
    public void visitDocElement(DocElement docElement)
    {
        visitAstNode(docElement, "DOC_ELEMENT");
    }

    @Override
    public void visitDocMultiline(DocMultiline docMultiline)
    {
        visitAstNode(docMultiline, "DOC_MULTILINE");
    }

    @Override
    public void visitDocTagSee(DocTagSee docTagSee)
    {
        final Element xmlElement = xmlDoc.createElement("DOC_TAG_SEE");
        final String linkAlias = docTagSee.getLinkAlias();
        if (linkAlias != null)
            xmlElement.setAttribute("link_alias", linkAlias);

        final SymbolReference linkReference = docTagSee.getLinkSymbolReference();
        final ZserioType linkReferencedType = linkReference.getReferencedType();
        if (linkReferencedType != null)
            xmlElement.setAttribute("link_referenced_type", linkReferencedType.getName());

        final String linkReferencedSymbolName = linkReference.getReferencedSymbolName();
        if (linkReferencedSymbolName != null)
            xmlElement.setAttribute("link_referenced_symbol", linkReferencedSymbolName);

        visitAstNode(docTagSee, xmlElement);
    }

    @Override
    public void visitDocTagTodo(DocTagTodo docTagTodo)
    {
        visitAstNode(docTagTodo, "DOC_TAG_TODO");
    }

    @Override
    public void visitDocTagParam(DocTagParam docTagParam)
    {
        final Element xmlElement = xmlDoc.createElement("DOC_TAG_PARAM");
        xmlElement.setAttribute("name", docTagParam.getParamName());
        visitAstNode(docTagParam, xmlElement);
    }

    @Override
    public void visitDocTagDeprecated(DocTagDeprecated docTagDeprecated)
    {
        visitAstNode(docTagDeprecated, "DOC_TAG_DEPRECATED");
    }

    @Override
    public void visitDocLine(DocLine docLine)
    {
        visitAstNode(docLine, "DOC_LINE");
    }

    @Override
    public void visitDocLineElement(DocLineElement docLineElement)
    {
        visitAstNode(docLineElement, "DOC_LINE_ELEMENT");
    }

    @Override
    public void visitDocText(DocText docText)
    {
        final Element xmlElement = xmlDoc.createElement("DOC_TEXT");
        xmlElement.setAttribute("text", docText.getText());
        visitAstNode(docText, xmlElement);
    }

    private void visitZserioType(ZserioType zserioType, String xmlElementName)
    {
        final Element xmlElement = xmlDoc.createElement(xmlElementName);
        xmlElement.setAttribute("name", zserioType.getName());
        visitAstNode(zserioType, xmlElement);
    }

    private void visitAstNode(AstNode node, Element xmlElement)
    {
        currentXmlElement.appendChild(xmlElement);

        final Element oldCurrentXmlElement = currentXmlElement;
        currentXmlElement = xmlElement;
        node.visitChildren(this);
        currentXmlElement = oldCurrentXmlElement;
    }

    private void visitAstNode(AstNode node, String xmlElementName)
    {
        final Element packageElement = xmlDoc.createElement(xmlElementName);
        currentXmlElement.appendChild(packageElement);

        final Element oldCurrentXmlElement = currentXmlElement;
        currentXmlElement = packageElement;
        node.visitChildren(this);
        currentXmlElement = oldCurrentXmlElement;
    }

    private Document xmlDoc = null;
    private Element currentXmlElement = null;
    private String rootExprElementName = null;
}
