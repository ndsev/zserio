package zserio.extension.doc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import zserio.ast.Package;
import zserio.ast.Root;
import zserio.ast.RuleGroup;
import zserio.extension.common.DefaultTreeWalker;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.doc.RulesOverviewTemplateData.PackageRulesTemplateData;
import zserio.tools.StringJoinUtil;

/**
 * Rules overview emitter.
 *
 * The rules overview emitter creates an HTML overview of all rules defined in schema.
 */
final class RulesOverviewEmitter extends DefaultTreeWalker
{
    public RulesOverviewEmitter(OutputFileManager outputFileManager, DocExtensionParameters docParameters,
            DocResourceManager docResourceManager) throws ZserioExtensionException
    {
        this.outputFileManager = outputFileManager;

        final String outputDir = docParameters.getOutputDir();
        htmlContentDirectory =
                StringJoinUtil.joinStrings(outputDir, DocDirectories.CONTENT_DIRECTORY, File.separator);
        docResourceManager.setCurrentOutputDir(htmlContentDirectory);

        final String htmlRootDirectory = "..";
        context = new ContentTemplateDataContext(docParameters, htmlRootDirectory, docResourceManager);
    }

    @Override
    public boolean traverseTemplateInstantiations()
    {
        return false;
    }

    @Override
    public void endRoot(Root root) throws ZserioExtensionException
    {
        final File outputFile = new File(getRulesOverviewHtmlLink(htmlContentDirectory));
        final RulesOverviewTemplateData templateData =
                new RulesOverviewTemplateData(context, root.getRootPackage(), packagesRuleGroups);
        DocFreeMarkerUtil.processTemplate(RULES_OVERVIEW_TEMPLATE_SOURCE_NAME, templateData, outputFile);
        outputFileManager.registerOutputFile(outputFile);
    }

    @Override
    public void endPackage(Package pkg) throws ZserioExtensionException
    {
        if (!currentPackageRuleGroups.isEmpty())
        {
            packagesRuleGroups.add(new PackageRulesTemplateData(context, pkg, currentPackageRuleGroups));
            currentPackageRuleGroups.clear();
        }
    }

    @Override
    public void beginRuleGroup(RuleGroup ruleGroup) throws ZserioExtensionException
    {
        currentPackageRuleGroups.add(new RuleGroupTemplateData(context, ruleGroup));
    }

    public static String getRulesOverviewHtmlLink(String htmlContentDirectory)
    {
        return StringJoinUtil.joinStrings(htmlContentDirectory, RULES_OVERVIEW_FILE_NAME, File.separator);
    }

    static final String RULES_OVERVIEW_FILE_NAME = "rules_overview.html";
    private static final String RULES_OVERVIEW_TEMPLATE_SOURCE_NAME = "rules_overview.html.ftl";

    private final String htmlContentDirectory;
    private final OutputFileManager outputFileManager;
    private final ContentTemplateDataContext context;
    // we want to have sorted packages in the left pane
    private final Set<PackageRulesTemplateData> packagesRuleGroups = new TreeSet<PackageRulesTemplateData>();
    private final List<RuleGroupTemplateData> currentPackageRuleGroups = new ArrayList<RuleGroupTemplateData>();
}
