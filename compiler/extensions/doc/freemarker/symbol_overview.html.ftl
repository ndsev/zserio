<#ftl output_format="HTML">
<html>
  <head>
    <title>Package Symbols Overview</title>

    <link rel="stylesheet" type="text/css" href="webStyles.css">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  </head>

  <body>
    <h2>Symbols</h2>

    <ul class="classlist">
<#list linkedTypes as linkedType>
      <li id="${linkedType.packageNameAsID}"><#rt>
        <a class="${linkedType.style}" href="content/${linkedType.packageName}.html#${linkedType.hyperlinkName}" <#t>
          title="Symbol: ${linkedType.category}" target="detailedDocu" >${linkedType.name}</a><#t>
      <#lt></li>
</#list>
    </ul>
  </body>

<#list packageNames as packageName>
  <style id="style_${packageName}" type="text/css">
    li#${packageName} { display: list-item }
  </style>
</#list>

  <script language="JavaScript">
    var allPackageNameListStyles = new Object();
<#list packageNames as packageName>
    allPackageNameListStyles.style_${packageName} = getElementStyleFromID("style_${packageName}");
</#list>

    function getElementStyleFromID(styleItemId)
    {
        var styleElement = document.getElementById(styleItemId);

        var styleElementSheet =
            (styleElement.sheet)? styleElement.sheet : styleElement.styleSheet;
        var styleElementRules =
            (styleElementSheet.rules)? styleElementSheet.rules : styleElementSheet.cssRules;
        return styleElementRules[0].style;
    }

    function receiveMessage(event)
    {
        for (var styleItemId in allPackageNameListStyles)
        {
            var styleElementStyle = allPackageNameListStyles[styleItemId];
            styleElementStyle.display = (event.data === ".all" || styleItemId === event.data) ? "list-item" : "none";
        }
    }

    window.addEventListener("message", receiveMessage, false);
  </script>
</html>
