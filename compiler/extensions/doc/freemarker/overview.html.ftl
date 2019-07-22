<html>
  <head>
    <title>Zserio Class List</title>
    <link rel="stylesheet" type="text/css" href="webStyles.css">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  </head>

  <body>
    <ul class="classlist">
<#list types as type>
      <li id="${type.packageNameAsID}"><#rt>
      <#t><a class="${type.style}" href="content/${type.packageName}/${type.hyperlinkName}.html" title="Type: ${type.category}" target="detailedDocu" >${type.name}</a>
      <#lt></li>
</#list>
    </ul>
  </body>

<#list packageNames as pkg>
  <style id="style_${pkg}" type="text/css">
    li#${pkg} { display: list-item }
  </style>
</#list>

  <script language="JavaScript">
    var allPackageNameListStyles = new Object();
<#list packageNames as pkg>
    allPackageNameListStyles.style_${pkg} = getElementStyleFromID("style_${pkg}");
</#list>

<#--

    /*
     * returns an array of CSS rules
     */
    function getCSS(docToChange, index)
    {
        if (!docToChange.styleSheets)
            return null;

        var theRules = new Array();
        var styleSheet = docToChange.styleSheets[index];
        if (styleSheet.cssRules)
            theRules = styleSheet.cssRules
        else if (styleSheet.rules)
            theRules = styleSheet.rules
        else
            return null;
        return theRules;
    }

-->

    function getElementStyleFromID(styleItemId)
    {
<#--
        theRules = getCSS(document, 0);
        if (theRules)
        {
            return theRules[theRules.length-1].style;
        }
        return null;
-->
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
            styleElementStyle.display = (event.data === "all" || styleItemId === event.data)? "list-item" : "none";
        }
    }

    window.addEventListener("message", receiveMessage, false);
  </script>
</html>
