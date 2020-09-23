<html>
  <head>
    <title>Zserio Package-List</title>
    <link rel="stylesheet" type="text/css" href="webStyles.css">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <script language="JavaScript">
        var oldClickedElement = null;


        function hiliteElement(clickedElement)
        {
            clickedElement.className =
                (clickedElement.className == "packagelist")? "selectedpackagelist" : "packagelist";

            if (oldClickedElement)
            {
                oldClickedElement.className =
                    (oldClickedElement.className == "packagelist")? "selectedpackagelist" : "packagelist";
            }
            oldClickedElement = clickedElement;
        }


        function showPackage(clickedElement)
        {
            hiliteElement(clickedElement.parentElement.parentElement);

            var clickedStyleItemId = "style_" +
                clickedElement.text.replace(/\./g, '_');
            parent.postMessage(clickedStyleItemId, "*");
        }

        function showDeprecated(clickedElement)
        {
            hiliteElement(clickedElement.parentElement.parentElement);
            parent.postMessage(".deprecated", "*");
        }

        function showAllPackages(clickedElement)
        {
            hiliteElement(clickedElement);

            parent.postMessage(".all", "*");
        }
    </script>
  </head>

  <body>
    <h2>Packages</h2>
    <ul id="all_packages" class="packagelist" onclick="showAllPackages(this);"><li>all packages</li></ul>
<#list packageList as pkg>
    <ul class="packagelist"><li><#rt>
    <#t><a href="content/${pkg}.html" title="Package: ${pkg}" target="detailedDocu" onclick="showPackage(this);">${pkg}</a>
    <#lt></li></ul>
</#list>

  <script language="JavaScript">
    showAllPackages(document.getElementById("all_packages"));
  </script>
  </body>
</html>

