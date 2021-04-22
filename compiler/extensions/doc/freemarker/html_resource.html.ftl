<#ftl output_format="HTML">
<#include "html_common.inc.ftl">
<!doctype html>
<html lang="en">
  <head>
    <@html_meta_tags/>

    <@html_stylesheets cssDirectory stylesheetName/>

    <title>${title}</title>
  </head>
  <body>
    <header id="header" class="navbar navbar-dark bg-dark sticky-top">
      <@html_navbar_navigation headerNavigation/>

      <@html_navbar_zserio_brand/>
    </header>

    <main id="main" class="px-3">
<#list bodyContent?split("\r?\n", "rm") as htmlLine>
      ${htmlLine?no_esc}
</#list>
    </main>
  </body>
</html>
