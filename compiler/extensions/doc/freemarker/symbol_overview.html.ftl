<#ftl output_format="HTML">
<#include "symbol.inc.ftl">
<html>
  <head>
    <title>Package Symbols Overview</title>

    <link rel="stylesheet" type="text/css" href="web_styles.css">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  </head>

  <body>
    <h2>Symbols</h2>

    <ul class="classlist">
<#list packageSymbols as packageSymbol>
      <li id="${packageSymbol.packageName?replace(".", "_")}"><@symbol_reference packageSymbol.symbol/></li>
</#list>
    </ul>
  </body>

<#list packageNames as packageName>
  <style id="style_${packageName?replace(".", "_")}" type="text/css">
    li#${packageName?replace(".", "_")} { display: list-item }
  </style>
</#list>

  <script language="JavaScript">
    var allPackageNameListStyles = new Object();
<#list packageNames as packageName>
    allPackageNameListStyles.style_${packageName?replace(".", "_")} = <#rt>
        <#lt>getElementStyleFromID("style_${packageName?replace(".", "_")}");
</#list>

    function getElementStyleFromID(styleItemId) {
      var styleElement = document.getElementById(styleItemId);

      var styleElementSheet = (styleElement.sheet)? styleElement.sheet : styleElement.styleSheet;
      var styleElementRules = (styleElementSheet.rules)? styleElementSheet.rules : styleElementSheet.cssRules;
      return styleElementRules[0].style;
    }

    function receiveMessage(event) {
      for (var styleItemId in allPackageNameListStyles) {
        var styleElementStyle = allPackageNameListStyles[styleItemId];
        styleElementStyle.display =
            (event.data === ".all" || styleItemId === event.data) ? "list-item" : "none";
      }
    }

    window.addEventListener("message", receiveMessage, false);
  </script>
</html>
