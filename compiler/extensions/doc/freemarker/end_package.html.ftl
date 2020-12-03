<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#if hasDocComments(docComments)>

          <@doc_comments_all docComments, 5, false/>
</#if>
        </main>
        <script>
          if (isTocHidden()) {
            let main = document.getElementById("main");
            main.classList.remove("col-8");
            main.classList.add("col-10");
          }
        </script>

      </div><!-- row -->
    </div><!-- container -->

    <script src="<@html_path jsDirectory/>/jquery-3.5.1.slim.min.js"></script>
    <script src="<@html_path jsDirectory/>/bootstrap.bundle.min.js"></script>
    <script src="<@html_path jsDirectory/>/anchor.min.js"></script>
    <script>
      // anchorjs setup
      anchors.add(".anchor");
      anchors.add(".anchor-md");

      // search logic
      function escapeRegex(string) {
        return string.replace(/[-\/\\^$*+?.()|[\]{}]/g, '\\$&');
      }

      function buildRegEx(terms) {
        try {
          return new RegExp(terms.join(".*"), "i");
        } catch (e) {
          return null;
        }
      }

      function buildSearchPattern(value) {
        let terms = value.split(/\s/);
        let pattern = buildRegEx(terms);
        if (!pattern) {
          let escapedTerms = terms.map(function(term) {
            return escapeRegex(term);
          });
          pattern = buildRegEx(escapedTerms);
        }
        return pattern;
      }

      function searchFilter(value) {
        let symbolOverview = $("#symbol_overview");

        if (value == "") {
          // restore to default state
          symbolOverview.find(".nav-symbols").css("display", "");
          symbolOverview.find(".nav-link").css("display", "");
        } else {
          // show also symbols nav-s which are not in the active package
          symbolOverview.find(".nav-symbols").show();
          symbolOverview.find(".nav-link").hide(); // hide all links

          let searchPattern = buildSearchPattern(value);
          if (!searchPattern) {
            console.warn("Couldn't build a search pattern!")
            return;
          }

          symbolOverview.find(".nav-package").each(function(index, element) {
            let navPackage = $(element);
            let navLinkPackage = navPackage.find(".nav-link-package");
            let packageName = navLinkPackage.text();
            let showPackage = packageName.match(searchPattern);
            navPackage.find(".nav-link-symbol").each(function(index, element) {
              let navLinkSymbol = $(element);
              let symbolName = navLinkSymbol.text();
              let fullSymbolName = packageName + "." + symbolName;

              if (fullSymbolName.match(searchPattern)) {
                navLinkSymbol.show();
                showPackage = true;
              }
            });
            if (showPackage) {
              navLinkPackage.show();
            }
          });
        }
      }

      function scrollParentToChild(parent, child) {
        let parentRect = parent.getBoundingClientRect();
        let parentViewableArea = {
          height: parent.clientHeight,
          width: parent.clientWidth
        };

        let childRect = child.getBoundingClientRect();
        let isViewable = (childRect.top >= parentRect.top) &&
                         (childRect.top <= parentRect.top + parentViewableArea.height);

        if (!isViewable) {
          parent.scrollTop = (childRect.top + parent.scrollTop) - parentRect.top
        }
      }

      function toggleToc() {
        let toc = $("#toc");
        toc.toggle();
        $("#toc_collapsed_icon").toggle();
        $("#toc_active_icon").toggle();
        $("main").toggleClass("col-8");
        $("main").toggleClass("col-10");

        let hidden = toc.is(":hidden");
        setItemToStorage(localStorage, "tocHidden", hidden);
      }

      function toggleDoc(button) {
        // find all doc rows within the button's table
        $(button).parents("table").find("tbody tr.doc").toggle();
        let icon = $(button).find("use").attr("xlink:href");
        icon = (icon == "#chat-left") ? "#chat-left-text" : "#chat-left";
        $(button).find("use").attr("xlink:href", icon);
      }

      // custom hooks
      $(document).ready(function() {
        // re-apply current search value
        let searchValue = getItemFromStorage(sessionStorage, "searchValue");
        if (searchValue) {
          $("#search").val(searchValue);
          searchFilter(searchValue);
        }

        // re-apply current left panel scroll
        let symbolOverviewScrollTop = getItemFromStorage(sessionStorage, "symbolOverviewScrollTop");
        if (symbolOverviewScrollTop) {
          $('#symbol_overview').scrollTop(symbolOverviewScrollTop);
        }

        // toc toggleable by the button
        $("#toc_button").click(toggleToc);

        // bootstrap's scrollspy setup
        var scrollMarginTop = parseInt($('h1.anchor').css("scroll-margin-top"));
        $('body').scrollspy({
          target: '#toc',
          offset: scrollMarginTop
        });

        $("#search").on("keyup", function(e) {
          if (e.key == "Escape")
            $(this).val("");

          let value = $(this).val();
          setItemToStorage(sessionStorage, "searchValue", value);
          searchFilter(value);
        });

        // remember current symbol overview scroll position
        $(".nav-link").on("click", function() {
          setItemToStorage(sessionStorage, "symbolOverviewScrollTop", $('#symbol_overview').scrollTop());
        });

        // allow to collapse active package in the overview
        $(".nav-link-package.active").on("click", function() {
          $(this).siblings(".nav-symbols").toggleClass("collapsed");
        })

        // catch scrollspy's event that the active item has changed
        $(window).on("activate.bs.scrollspy", function() {
          scrollParentToChild($('#toc')[0], $('#toc .active')[0]);
        })
      });
    </script>
  </body>
</html>
