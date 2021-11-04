<#ftl output_format="HTML">
<#include "html_common.inc.ftl">
<#include "doc_comment.inc.ftl">
<#include "symbol.inc.ftl">
<#macro rules_anchor packageSymbol symbol>
    ${packageSymbol.htmlLink.htmlAnchor}-${symbol.htmlLink.htmlAnchor}<#t>
</#macro>
<!doctype html>
<html lang="en">
  <head>
    <@html_meta_tags/>

    <@html_stylesheets cssDirectory stylesheetName/>

    <title>Rules overview</title>
  </head>
  <body>
    <header id="header" class="navbar navbar-dark bg-dark sticky-top">
      <@html_navbar_navigation headerNavigation/>

      <@html_navbar_zserio_brand/>
    </header>

    <div class="container-fluid">
      <div class="row">
        <div id="left_panel" class="col-2 order-1">
          <form id="search_form" role="search">
            <input id="search" class="form-control" type="text" autocomplete="off" spellcheck="false"
              placeholder="Search...">
          </form>
          <nav id="rules_overview" class="nav flex-column">
<#list packagesRuleGroups as packageRuleGroups>
            <nav class="nav-package">
              <a class="nav-link nav-link-package" href="#${packageRuleGroups.packageSymbol.htmlLink.htmlAnchor}" title="${packageRuleGroups.packageSymbol.htmlTitle}">
                ${packageRuleGroups.packageSymbol.name}
              </a>
              <nav class="nav nav-rule-groups flex-column">
    <#list packageRuleGroups.ruleGroups as ruleGroup>
                <a class="nav-link nav-link-rule-group" href="#<@rules_anchor packageRuleGroups.packageSymbol, ruleGroup.symbol/>" title="${ruleGroup.symbol.htmlTitle}">
                  ${ruleGroup.symbol.name}
                </a>
    </#list>
              </nav>
            </nav>
</#list>
          </nav>
        </div>

        <main id="main" class="col-10 pl-3 order-2">
<#list packagesRuleGroups as packageRuleGroups>
          <h1 class="anchor" id="${packageRuleGroups.packageSymbol.htmlLink.htmlAnchor}">${packageRuleGroups.packageSymbol.name}</h1>
    <#list packageRuleGroups.ruleGroups as ruleGroup>
          <h2 class="anchor" id="<@rules_anchor packageRuleGroups.packageSymbol ruleGroup.symbol/>">${ruleGroup.symbol.name}</h2>
          <div class="rule_group table table-responsive"><table><tbody>
        <#list ruleGroup.rules as rule>
            <tr>
              <th scope="row">
                <@symbol_reference rule.symbol/>
              </th>
              <td>
                <@doc_comments_all rule.docComments, 6, false/>
              </td>
            </tr>
        </#list>
          </tbody></table></div>
    </#list>
</#list>
        </main>

      </div>
    </div>

    <script src="<@html_path jsDirectory/>/jquery-3.5.1.slim.min.js"></script>
    <script src="<@html_path jsDirectory/>/bootstrap.bundle.min.js"></script>
    <script src="<@html_path jsDirectory/>/anchor.min.js"></script>
    <script>
      // anchorjs setup
      anchors.add(".anchor");
      anchors.add(".anchor-md");

      <@html_js_storage_functions/>
      <@html_js_search_functions "rules_overview", "package", "rule-group"/>

      // custom hooks
      $(document).ready(function() {
        <@html_js_search_setup "rulesOverviewSearchValue"/>
      });
    </script>
  </body>
</html>
