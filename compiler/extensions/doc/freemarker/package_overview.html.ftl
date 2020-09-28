<#ftl output_format="HTML">
<html>
  <head>
    <title>Zserio Package Overview</title>

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

        function showAllPackages(clickedElement)
        {
            hiliteElement(clickedElement);

            parent.postMessage(".all", "*");
        }
    </script>
  </head>

  <body>
    <h2>Packages</h2>

    <ul class="packagelist" id="all_packages" title="All packages in one" onclick="showAllPackages(this);"><#rt>
      <li><#t>
        all packages<#t>
      </li><#t>
    <#lt></ul>
<#list packageNames as packageName>
    <ul class="packagelist"><#rt>
      <li><#t>
        <a href="content/${packageName}.html" title="Package: ${packageName}" target="detailedDocu" <#t>
          onclick="showPackage(this);">${packageName}</a><#t>
      </li><#t>
    <#lt></ul>
</#list>

    <script language="JavaScript">
      showAllPackages(document.getElementById("all_packages"));
    </script>
  </body>
</html>
