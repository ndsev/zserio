<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">

    <script language="JavaScript">
      function receiveMessage(event)
      {
        // forward to symbol overview iframe
        var f = document.getElementsByName("symbol_overview")[0]
        f.contentWindow.postMessage(event.data, "*")
      }

      window.addEventListener("message", receiveMessage, false);
    </script>
  </head>

  <frameset cols="20%,*">
    <frameset rows="30%,70%">
      <frame name="package_overview" src="package_overview.html" scrolling="auto" frameborder="0" />
      <frame name="symbol_overview" src="symbol_overview.html" scrolling="auto" frameborder="1" />
    </frameset>
    <frame name="detailedDocu" src="" class="detailedDocu" scrolling="auto" frameborder="0" />

    <noframes>
      <body>
        <p>Your browser software can not handle framesets!</p>
      </body>
    </noframes>
  </frameset>
</html>
