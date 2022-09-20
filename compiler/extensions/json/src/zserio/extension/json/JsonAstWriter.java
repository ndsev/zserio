package zserio.extension.json;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import zserio.ast.ArrayType;

import zserio.ast.BitmaskType;
import zserio.ast.BitmaskValue;
import zserio.ast.BooleanType;
import zserio.ast.ChoiceCase;
import zserio.ast.ChoiceCaseExpression;
import zserio.ast.ChoiceDefault;
import zserio.ast.ChoiceType;
import zserio.ast.CompatibilityVersion;
import zserio.ast.Constant;
import zserio.ast.DocElement;
import zserio.ast.DocLine;
import zserio.ast.DocLineElement;
import zserio.ast.DocMultiline;
import zserio.ast.DocTagDeprecated;
import zserio.ast.DynamicBitFieldType;
import zserio.ast.EnumItem;
import zserio.ast.EnumType;
import zserio.ast.Expression;
import zserio.ast.ExternType;
import zserio.ast.Field;
import zserio.ast.FixedBitFieldType;
import zserio.ast.FloatType;
import zserio.ast.Function;
import zserio.ast.Import;
import zserio.ast.InstantiateType;
import zserio.ast.Parameter;
import zserio.ast.PubsubMessage;
import zserio.ast.PubsubType;
import zserio.ast.Root;
import zserio.ast.Rule;
import zserio.ast.RuleGroup;
import zserio.ast.Package;
import zserio.ast.ServiceMethod;
import zserio.ast.ServiceType;
import zserio.ast.SqlConstraint;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.StdIntegerType;
import zserio.ast.StringType;
import zserio.ast.StructureType;
import zserio.ast.Subtype;
import zserio.ast.TemplateArgument;
import zserio.ast.TemplateParameter;
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeReference;
import zserio.ast.UnionType;
import zserio.ast.VarIntegerType;
import zserio.ast.ZserioAstVisitor;
import zserio.extension.common.FileUtil;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.StringJoinUtil;
import zserio.ast.DocCommentClassic;
import zserio.ast.DocCommentMarkdown;
import zserio.ast.DocParagraph;
import zserio.ast.DocTagParam;
import zserio.ast.DocTagSee;
import zserio.ast.DocTagTodo;
import zserio.ast.DocText;

/**
 * Abstract Syntax Tree (AST) visitor for JSON writer.
 *
 * This visitor converts whole AST into JSON format and offers possibility to
 * save created JSON to the given file.
 */
public class JsonAstWriter implements ZserioAstVisitor {
    /** Current package name */
    // private String currentPackageName = null;

    /** Current element */
    private JsonElement currentJsonElement = null;

    /** Collection of all types */
    private final Map<String, JsonElement> jsonElementMap = new HashMap<String, JsonElement>();

    /**
     * Constructor.
     */
    public JsonAstWriter() {
    }

    /**
     * Saves AST in the JSON format to the file.
     *
     * @param outputDir JSON file outputDir where to write whole AST.
     * @throws ZserioExtensionException
     * @throws IOException
     *
     */
    public void save(OutputFileManager outputFileManager, String outputDir) throws ZserioExtensionException {
        // TODO:
        List<JsonElement> root = new ArrayList<JsonElement>();

        final File outputFile = new File(outputDir, "ndslive-layer.json");
        FileUtil.createOutputDirectory(outputFile);

        for (String key : jsonElementMap.keySet()) {
            if (key.endsWith("Layer")) {
                JsonElement element = jsonElementMap.get(key);
                fillGrandson(element);
                root.add(element);

                String text = JSON.toJSONString(element, SerializerFeature.DisableCircularReferenceDetect);
                final File jsonFile = new File(outputDir, element.getName() + ".json");
                PrintWriter writer = FileUtil.createWriter(jsonFile);
                writer.write(text);
                writer.close();
                outputFileManager.registerOutputFile(jsonFile);
            }
        }

        //
        String text = JSON.toJSONString(root, SerializerFeature.DisableCircularReferenceDetect);
        PrintWriter writer = FileUtil.createWriter(outputFile);
        writer.write(text);
        writer.close();
        outputFileManager.registerOutputFile(outputFile);
    }

    private void fillGrandson(JsonElement element) {
        if (element != null) {
            if (element.getChildren().isEmpty() && jsonElementMap.containsKey(element.getType())) {
                String type = element.getType() + " (" + jsonElementMap.get(element.getType()).getType() + ")";
                element.setType(type);
            } else {
                for (JsonElement children : element.getChildren()) {
                    if (jsonElementMap.containsKey(children.getType())) {
                        // add grandson
                        JsonElement realChildren = jsonElementMap.get(children.getType());
                        children.setChildren(realChildren.getChildren());
                        fillGrandson(children);
                    }
                }
            }
        }
    }

    /**
     * Visits root node.
     *
     * @param root Root node of zserio AST.
     */
    public void visitRoot(Root root) {
        root.visitChildren(this);
    }

    /**
     * Visits a single package.
     *
     * @param packageNode Package AST node.
     */
    public void visitPackage(Package packageNode) {
        // currentPackageName = packageNode.getPackageName().toString();
        packageNode.visitChildren(this);
    }

    /**
     * Visits compatibility version.
     *
     * @param compatibilityVersion Compatibility version AST node.
     */
    public void visitCompatibilityVersion(CompatibilityVersion compatibilityVersion) {
    }

    /**
     * Visits a single import.
     *
     * @param importNode Import AST node.
     */
    public void visitImport(Import importNode) {
    }

    /**
     * Visits constant definition.
     *
     * @param constant Constant AST node.
     */
    public void visitConstant(Constant constant) {

    }

    /**
     * Visits rule group definition.
     *
     * @param ruleGroup Rule group AST node.
     */
    public void visitRuleGroup(RuleGroup ruleGroup) {

    }

    /**
     * Visits subtype declaration.
     *
     * @param subtype Subtype AST node.
     */
    public void visitSubtype(Subtype subtype) {
        // name
        String name = subtype.getName();

        // Add element
        JsonElement element = new JsonElement(name);
        jsonElementMap.put(name, element);
        currentJsonElement = element;

        subtype.visitChildren(this);
        currentJsonElement = null;
    }

    /**
     * Visits structure type declaration.
     *
     * @param structureType Structure AST node.
     */
    public void visitStructureType(StructureType structureType) {
        // name
        String name = structureType.getName();

        // Add element
        JsonElement element = new JsonElement(name, name);
        jsonElementMap.put(name, element);
        currentJsonElement = element;

        structureType.visitChildren(this);
        currentJsonElement = null;
    }

    /**
     * Visits choice type declaration.
     *
     * @param choiceType Choice AST node.
     */
    public void visitChoiceType(ChoiceType choiceType) {
        // name
        String name = choiceType.getName();

        // Add element
        JsonElement element = new JsonElement(name, name);
        jsonElementMap.put(name, element);
        currentJsonElement = element;

        choiceType.visitChildren(this);
        currentJsonElement = null;
    }

    /**
     * Visits union type declaration.
     *
     * @param unionType Union AST node.
     */
    public void visitUnionType(UnionType unionType) {

    }

    /**
     * Visits enum type declaration.
     *
     * @param enumType Enum AST node.
     */
    public void visitEnumType(EnumType enumType) {
        // name
        String name = enumType.getName();

        // Add element
        JsonElement element = new JsonElement(name);
        jsonElementMap.put(name, element);
        currentJsonElement = element;

        enumType.visitChildren(this);
        currentJsonElement = null;
    }

    /**
     * Visits bitmask type declaration.
     *
     * @param bitmaskType Bitmask AST node.
     */
    public void visitBitmaskType(BitmaskType bitmaskType) {

    }

    /**
     * Visits SQL table declaration.
     *
     * @param sqlTableType SQL table AST node.
     */
    public void visitSqlTableType(SqlTableType sqlTableType) {

    }

    /**
     * Visits SQL database definition.
     *
     * @param sqlDatabaseType SQL database AST node.
     */
    public void visitSqlDatabaseType(SqlDatabaseType sqlDatabaseType) {

    }

    /**
     * Visits service definition.
     *
     * @param serviceType Service AST node.
     */
    public void visitServiceType(ServiceType serviceType) {

    }

    /**
     * Visits Pub/Sub definition.
     *
     * @param pubsubType Pub/Sub AST node.
     */
    public void visitPubsubType(PubsubType pubsubType) {

    }

    /**
     * Visits field definition.
     *
     * @param field Field AST node.
     */
    public void visitField(Field field) {
        if (currentJsonElement != null) {
            JsonElement fatherElement = currentJsonElement;
            JsonElement fieldElement = new JsonElement(field.getName());
            // isOptional
            fieldElement.setOptional(field.isOptional());
            // isPackable
            fieldElement.setPackable(field.isPackable());

            currentJsonElement = fieldElement;
            fatherElement.addChildren(fieldElement);

            field.visitChildren(this);
            currentJsonElement = fatherElement;
        }
    }

    /**
     * Visits choice case definition. Note that a single case can have multiple
     * choice case expressions.
     *
     * @param choiceCase Choice case AST node.
     */
    public void visitChoiceCase(ChoiceCase choiceCase) {
        choiceCase.visitChildren(this);
    }

    /**
     * Visits choice case expression.
     *
     * @param choiceCaseExpression Choice case expression AST node.
     */
    public void visitChoiceCaseExpression(ChoiceCaseExpression choiceCaseExpression) {

    }

    /**
     * Visits choice default statement.
     *
     * @param choiceDefault Choice default statement AST node.
     */
    public void visitChoiceDefault(ChoiceDefault choiceDefault) {

    }

    /**
     * Visits enum item definition.
     *
     * @param enumItem Enum item AST node.
     */
    public void visitEnumItem(EnumItem enumItem) {
        if (currentJsonElement != null) {
            JsonElement fatherElement = currentJsonElement;
            String text = enumItem.getName() + " (" + enumItem.getValue().toString() + ")";
            JsonElement fieldElement = new JsonElement(text, fatherElement.getType());
            currentJsonElement = fieldElement;
            fatherElement.addChildren(fieldElement);

            enumItem.visitChildren(this);
            currentJsonElement = fatherElement;
        }
    }

    /**
     * Visits bitmask named value definition.
     *
     * @param bitmaskValue Bitmask named value AST node.
     */
    public void visitBitmaskValue(BitmaskValue bitmaskValue) {

    }

    /**
     * Visits SQL constraint definition.
     *
     * @param sqlConstraint SQL constraint AST node.
     */
    public void visitSqlConstraint(SqlConstraint sqlConstraint) {

    }

    /**
     * Visits service method definition.
     *
     * @param serviceMethod Service method AST node.
     */
    public void visitServiceMethod(ServiceMethod serviceMethod) {

    }

    /**
     * Visits Pub/Sub message definition.
     *
     * @param pubsubMessage Pub/Sub message AST node.
     */
    public void visitPubsubMessage(PubsubMessage pubsubMessage) {

    }

    /**
     * Visits a single rule.
     *
     * @param rule Rule AST node.
     */
    public void visitRule(Rule rule) {

    }

    /**
     * Visits function definition.
     *
     * @param function Function AST node.
     */
    public void visitFunction(Function function) {

    }

    /**
     * Visits parameter definition.
     *
     * @param parameter Parameter AST node.
     */
    public void visitParameter(Parameter parameter) {

    }

    /**
     * Visits expression.
     *
     * @param expresssion Expression AST node.
     */
    public void visitExpression(Expression expresssion) {

    }

    /**
     * Visits type reference.
     *
     * @param typeReference Type reference AST node.
     */
    public void visitTypeReference(TypeReference typeReference) {
        if (currentJsonElement != null) {
            String type = typeReference.getReferencedTypeName();
            currentJsonElement.setType(type);
        }
    }

    /**
     * Visits type instantiation.
     *
     * @param typeInstantiation Type instantiation AST node.
     */
    public void visitTypeInstantiation(TypeInstantiation typeInstantiation) {
        typeInstantiation.visitChildren(this);
    }

    /**
     * Visits array type.
     *
     * @param arrayType Array type AST node.
     */
    public void visitArrayType(ArrayType arrayType) {

    }

    /**
     * Visits reference to built-in standard integer type.
     *
     * @param stdIntegerType Standard integer type AST node.
     */
    public void visitStdIntegerType(StdIntegerType stdIntegerType) {

    }

    /**
     * Visits reference to built-in variable length integer type.
     *
     * @param varIntegerType Variable length integer type AST node.
     */
    public void visitVarIntegerType(VarIntegerType varIntegerType) {

    }

    /**
     * Visits reference to built-in fixed bit field type.
     *
     * @param fixedBitFieldType Fixed bit field type AST node.
     */
    public void visitFixedBitFieldType(FixedBitFieldType fixedBitFieldType) {

    }

    /**
     * Visits reference to built-in dynamic bit field type.
     *
     * @param dynamicBitFieldType Dynamic bit field type AST node.
     */
    public void visitDynamicBitFieldType(DynamicBitFieldType dynamicBitFieldType) {

    }

    /**
     * Visits reference to built-in boolean type.
     *
     * @param booleanType Boolean type AST node.
     */
    public void visitBooleanType(BooleanType booleanType) {

    }

    /**
     * Visits reference to built-in string type.
     *
     * @param stringType String type AST node.
     */
    public void visitStringType(StringType stringType) {

    }

    /**
     * Visits reference to built-in float type.
     *
     * @param floatType Float type AST node.
     */
    public void visitFloatType(FloatType floatType) {

    }

    /**
     * Visits reference to built-in extern type.
     *
     * @param externType Extern type AST node.
     */
    public void visitExternType(ExternType externType) {

    }

    /**
     * Visits template parameter.
     *
     * @param templateParameter Template parameter AST node.
     */
    public void visitTemplateParameter(TemplateParameter templateParameter) {

    }

    /**
     * Visits template argument.
     *
     * @param templateArgument Template argument AST node.
     */
    public void visitTemplateArgument(TemplateArgument templateArgument) {

    }

    /**
     * Visits template instantiation.
     *
     * @param templateInstantiation Template instantiation AST node.
     */
    public void visitInstantiateType(InstantiateType templateInstantiation) {
        // name
        String name = templateInstantiation.getName();
        // Add element
        JsonElement element = new JsonElement(name);
        jsonElementMap.put(name, element);
        currentJsonElement = element;
        templateInstantiation.visitChildren(this);

        JsonElement fieldElement = new JsonElement(currentJsonElement.getType(), currentJsonElement.getType());
        currentJsonElement.addChildren(fieldElement);

        currentJsonElement = null;
    }

    /**
     * Visits a classic-style documentation comment.
     *
     * @param docComment Classic-style documentation comment AST node.
     */
    public void visitDocCommentClassic(DocCommentClassic docComment) {
        docComment.visitChildren(this);
    }

    /**
     * Visits a markdown-style documentation comment.
     *
     * @param docComment Markdown-style documentation comment AST node.
     */
    public void visitDocCommentMarkdown(DocCommentMarkdown docComment) {
        docComment.visitChildren(this);
    }

    /**
     * Visits documentation paragraph.
     *
     * @param docParagraph Documentation paragraph AST node.
     */
    public void visitDocParagraph(DocParagraph docParagraph) {
        docParagraph.visitChildren(this);
    }

    /**
     * Visits documentation element.
     *
     * @param docElement Documentation element AST node.
     */
    public void visitDocElement(DocElement docElement) {
        docElement.visitChildren(this);
    }

    /**
     * Visits documentation multiline.
     *
     * @param docMultiline Documentation multiline AST node.
     */
    public void visitDocMultiline(DocMultiline docMultiline) {
        docMultiline.visitChildren(this);
    }

    /**
     * Visits a see tag within a documentation comment.
     *
     * @param docTagSee See tag AST node.
     */
    public void visitDocTagSee(DocTagSee docTagSee) {

    }

    /**
     * Visits a todo tag within a documentation comment.
     *
     * @param docTagTodo Todo tag AST node.
     */
    public void visitDocTagTodo(DocTagTodo docTagTodo) {

    }

    /**
     * Visits a param tag within a documentation comment.
     *
     * @param docTagParam Param tag AST node.
     */
    public void visitDocTagParam(DocTagParam docTagParam) {

    }

    /**
     * Visits a deprecated tag within a documentation comment.
     *
     * @param docTagDeprecated Deprecated tag AST node.
     */
    public void visitDocTagDeprecated(DocTagDeprecated docTagDeprecated) {

    }

    /**
     * Visits a single line of documentation.
     *
     * @param docLine Documentation line AST node.
     */
    public void visitDocLine(DocLine docLine) {
        docLine.visitChildren(this);
    }

    /**
     * Visits documentation text wrapper.
     *
     * DocLineElement can be either a text or a see tag.
     *
     * @param docLineElement Documentation line element AST node.
     */
    public void visitDocLineElement(DocLineElement docLineElement) {
        docLineElement.visitChildren(this);
    }

    /**
     * Visits documentation text.
     *
     * @param docText Documentation text AST node.
     */
    public void visitDocText(DocText docText) {
        if (currentJsonElement != null) {
            String text = currentJsonElement.getComment();
            if (text != null) {
                text = StringJoinUtil.joinStrings(text, docText.getText(), "\n");
            } else {
                text = docText.getText();
            }
            currentJsonElement.setComment(text);
        }
    }
}
