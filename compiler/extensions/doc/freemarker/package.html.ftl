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
            hiliteElement(clickedElement);

            var clickedStyleItemId = "style_" +
                clickedElement.firstChild.firstChild.data.replace(/\./g, '_');
            parent.postMessage(clickedStyleItemId, "*");
        }


        function showAllPackages(clickedElement)
        {
            hiliteElement(clickedElement);

            parent.postMessage("all", "*");
        }
    </script>
  </head>

  <body>
    <h2>Zserio Package: ${rootPackageName}</h2>
    <ul id="all_packages" class="packagelist" onclick="showAllPackages(this);"><li>all packages</li></ul>
    <ul class="packagelist"><li><a href="deprecated.html" title="Deprecated elements" target="detailedDocu">deprecated</a></li></ul>
<#list packages as pkg>
    <ul class="packagelist" onclick="showPackage(this);"><li>${pkg}</li></ul>
</#list>

  <script language="JavaScript">
    showAllPackages(document.getElementById("all_packages"));
  </script>
  </body>
</html>

