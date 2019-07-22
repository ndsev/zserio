<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">

    <script language="JavaScript">
      function receiveMessage(event) {
        // forward to overview iframe
        var f = document.getElementsByName("overview")[0]
        f.contentWindow.postMessage(event.data, "*")
      }

      window.addEventListener("message", receiveMessage, false);
    </script>
  </head>

  <frameset cols="20%,*">
    <frameset rows="30%,70%">
      <frame name="packages" src="packages.html" scrolling="auto" frameborder="0" />
      <frame name="overview" src="overview.html" scrolling="auto" frameborder="1" />
    </frameset>
    <frame name="detailedDocu" src="" class="detailedDocu" scrolling="auto" frameborder="0" />

    <noframes>
      <body>
        <p>Ihr Browser kann leider keine Frames anzeigen!<br>
        Your browser software can not handle framesets!</p>
      </body>
    </noframes>
  </frameset>

</html>
