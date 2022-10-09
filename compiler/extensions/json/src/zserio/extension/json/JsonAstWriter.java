package zserio.extension.json;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

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
import zserio.ast.ZserioTemplatableType;
import zserio.ast.ZserioType;
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

    private final OutputFileManager outputFileManager;

    private final String outputDir;

    /** Current element */
    private JsonElement currentJsonElement = null;

    /** Collection of all types */
    private final Map<String, JsonElement> jsonElementMap = new HashMap<String, JsonElement>();

    /** The number of times the element is referenced */
    private final Map<String, Integer> referenceMap = new HashMap<String, Integer>();

    /**
     * Constructor.
     */
    public JsonAstWriter(OutputFileManager outputFileManager, String outputDir) {
        this.outputFileManager = outputFileManager;
        this.outputDir = outputDir;
    }

    /**
     * Saves AST in the JSON format to the file.
     *
     * @throws ZserioExtensionException
     *
     */
    public void save() throws ZserioExtensionException {
        // Create output directory
        final File outputFile = new File(outputDir, "0");
        FileUtil.createOutputDirectory(outputFile);

        TreeSet<String> rootSet = new TreeSet<String>();
        for (String key : jsonElementMap.keySet()) {
            // Filter elements not referenced by other structures.
            // key.endsWith("Layer") && 
            if (referenceMap.containsKey(key) && 0 == referenceMap.get(key)) {
                JsonElement element = jsonElementMap.get(key);
                fillGrandson(element);
                writeFile(element, element.getName());
                rootSet.add(key);
            } else {
                // remove it ?
            }
        }
        // Root node element as menu list.
        writeFile(rootSet, "Menus");
    }

    /**
     * Fill in the real grandchild node information.
     * 
     * @param element
     */
    private void fillGrandson(JsonElement element) {
        if (element != null) {
            if (element.getChildren() == null) {
                // leaf node
                if (jsonElementMap.containsKey(element.getType())) {
                    String type = element.getType() + " (" + jsonElementMap.get(element.getType()).getType() + ")";
                    element.setType(type);
                }
            } else {
                // intermediate node
                for (JsonElement children : element.getChildren()) {
                    if (jsonElementMap.containsKey(children.getType())) {
                        // add grandson
                        JsonElement realChildren = jsonElementMap.get(children.getType());
                        children.setChildren(realChildren.getChildren());
                    }
                    // recursive fill
                    fillGrandson(children);
                }
            }
        }
    }

    /**
     * Export objects to a file.
     *
     * @param object object.
     * @param name file name.
     */
    private void writeFile(Object object, String name) throws ZserioExtensionException {
        String text = JSON.toJSONString(object, SerializerFeature.DisableCircularReferenceDetect);
        final File jsonFile = new File(outputDir, name + ".json");
        PrintWriter writer = FileUtil.createWriter(jsonFile);
        writer.write(text);
        writer.close();
        outputFileManager.registerOutputFile(jsonFile);
    }

    /**
     * Add element.
     *
     * @param name Name of the element.
     * @param type Type of the element.
     */
    private void addElement(String name, String type) {
        // Add element
        currentJsonElement = new JsonElement(name, type);
        jsonElementMap.put(name, currentJsonElement);
        addReference(type);
    }

    /**
     * Collect all types and record the number of times the type is referenced.
     * 
     * @param type Name of the type.
     */
    private void addReference(String type) {
        // Number of references to the type
        if (type != null) {
            if (referenceMap.containsKey(type)) {
                referenceMap.compute(type, (key, value) -> value + 1);
            } else {
                referenceMap.put(type, 0);
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
        addElement(name, name);
        subtype.visitChildren(this);
    }

    /**
     * Visits structure type declaration.
     *
     * @param structureType Structure AST node.
     */
    public void visitStructureType(StructureType structureType) {
        // instantiations
        List<ZserioTemplatableType> instantiations = structureType.getInstantiations();
        // Is it a template struct?
        if (0 < instantiations.size()) {
            for (ZserioTemplatableType instantiation : instantiations) {
                // Add element
                String name = instantiation.getInstantiationName();
                addElement(name, name);
                instantiation.visitChildren(this);
            }
        } else {
            // Add element
            String name = structureType.getName();
            addElement(name, name);
            structureType.visitChildren(this);
        }
    }

    /**
     * Visits choice type declaration.
     *
     * @param choiceType Choice AST node.
     */
    public void visitChoiceType(ChoiceType choiceType) {
        // name
        String name = choiceType.getName();
        addElement(name, name);
        choiceType.visitChildren(this);

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
        addElement(name, name);
        enumType.visitChildren(this);
    }

    /**
     * Visits bitmask type declaration.
     *
     * @param bitmaskType Bitmask AST node.
     */
    public void visitBitmaskType(BitmaskType bitmaskType) {
        // name
        String name = bitmaskType.getName();
        addElement(name, name);
        bitmaskType.visitChildren(this);
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
        // name
        String name = serviceType.getName();
        addElement(name, name);
        serviceType.visitChildren(this);
    }

    /**
     * Visits Pub/Sub definition.
     *
     * @param pubsubType Pub/Sub AST node.
     */
    public void visitPubsubType(PubsubType pubsubType) {
        // name
        String name = pubsubType.getName();
        addElement(name, name);
        pubsubType.visitChildren(this);
    }

    /**
     * Visits field definition.
     *
     * @param field Field AST node.
     */
    public void visitField(Field field) {
        if (currentJsonElement != null) {
            JsonElement fatherElement = currentJsonElement;
            addElement(field.getName(), null);
            // isOptional
            currentJsonElement.setOptional(field.isOptional());
            // isPackable
            currentJsonElement.setPackable(field.isPackable());
            field.visitChildren(this);
            //
            addReference(currentJsonElement.getType());

            // add children
            fatherElement.addChildren(currentJsonElement);
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
            String name = enumItem.getName() + " (" + enumItem.getValue().toString() + ")";
            addElement(name, null);
            enumItem.visitChildren(this);
            // add children
            fatherElement.addChildren(currentJsonElement);
            currentJsonElement = fatherElement;
        }
    }

    /**
     * Visits bitmask named value definition.
     *
     * @param bitmaskValue Bitmask named value AST node.
     */
    public void visitBitmaskValue(BitmaskValue bitmaskValue) {
        if (currentJsonElement != null) {
            JsonElement fatherElement = currentJsonElement;
            String name = bitmaskValue.getName() + " (" + bitmaskValue.getValue().toString() + ")";
            addElement(name, null);
            bitmaskValue.visitChildren(this);
            // add children
            fatherElement.addChildren(currentJsonElement);
            currentJsonElement = fatherElement;
        }
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
        if (currentJsonElement != null) {
            JsonElement fatherElement = currentJsonElement;
            addElement(serviceMethod.getName(), null);
            JsonElement sonElement = currentJsonElement;

            // request
            String request = serviceMethod.getRequestTypeReference().getReferencedTypeName();
            addElement("request", request);
            sonElement.addChildren(currentJsonElement);
            // response
            String response = serviceMethod.getResponseTypeReference().getReferencedTypeName();
            addElement("response", response);
            sonElement.addChildren(currentJsonElement);

            // add children
            fatherElement.addChildren(sonElement);
            currentJsonElement = fatherElement;
        }
    }

    /**
     * Visits Pub/Sub message definition.
     *
     * @param pubsubMessage Pub/Sub message AST node.
     */
    public void visitPubsubMessage(PubsubMessage pubsubMessage) {
        if (currentJsonElement != null) {
            JsonElement fatherElement = currentJsonElement;
            addElement(pubsubMessage.getName(), null);
            pubsubMessage.visitChildren(this);
            //
            addReference(currentJsonElement.getType());

            // add children
            fatherElement.addChildren(currentJsonElement);
            currentJsonElement = fatherElement;
        }

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
            String type = null;
            ZserioType zserioType = typeReference.getType();
            if (zserioType != null) {
                type = zserioType.getName();
            } else {
                type = typeReference.getReferencedTypeName();
            }
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
