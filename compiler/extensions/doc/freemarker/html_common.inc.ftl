<#ftl output_format="HTML">
<#-- converts native path to HTML path - i.e. convert backslashes to slashes ('\' -> '/') -->
<#macro html_path path>
    ${path?replace('\\', '/')}<#t>
</#macro>

<#macro html_meta_tags>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
</#macro>

<#macro html_stylesheets cssDirectory stylesheetName>
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="<@html_path cssDirectory/>/bootstrap.min.css">

    <!-- Zserio Documentation CSS -->
    <link rel="stylesheet" href="<@html_path cssDirectory/>/${stylesheetName}">
</#macro>

<#macro html_navbar_navigation headerNavigation>
      <div class="navbar-nav">
        <ul class="navbar-nav flex-row">
<#list headerNavigation.navigationItems as item>
          <li class="nav-item"><a class="nav-link<#if item?index == headerNavigation.activeItem> active</#if>"<#rt>
            <#lt> href="<@html_path item.htmlLink/>">${item.name}</a></li>
</#list>
        </ul>
      </div>
</#macro>

<#macro html_navbar_zserio_brand>
      <div class="navbar-brand navbar-center d-none d-sm-block">
        <a href="https://zserio.org/" target="_blank">
          <svg id="zserio-logo" version="1.1" viewBox="0 0 60 15.328" xmlns="http://www.w3.org/2000/svg">
            <g id="zserio-logo-serio" fill="currentColor">
              <path d="m21.733 11.887q0 1.5082-1.2519 2.4743-1.2421 0.96605-3.4009 0.96605-1.2224 0-2.2476-0.28587-1.0154-0.29572-1.7053-0.64075v-2.08h0.09857q0.87732 0.66046 1.9518 1.0548 1.0744 0.38445 2.0603 0.38445 1.2224 0 1.9123-0.39431 0.68996-0.39431 0.69003-1.2421 0-0.6506-0.37459-0.98577t-1.4392-0.57174q-0.39431-0.08872-1.0351-0.20701-0.63089-0.11829-1.1533-0.2563-1.4491-0.38445-2.0603-1.1238-0.60132-0.74918-0.60132-1.8336 0-0.68017 0.27602-1.2815 0.28587-0.60132 0.85761-1.0744 0.55203-0.46331 1.3997-0.72946 0.85761-0.27602 1.9123-0.27602 0.98576 0 1.9912 0.24644 1.0154 0.23659 1.6856 0.5816v1.9814h-0.09857q-0.70975-0.52245-1.7251-0.87732-1.0154-0.36473-1.9912-0.36473-1.0154 0-1.7152 0.39431-0.69989 0.38445-0.69989 1.1533 0 0.68017 0.42388 1.0252 0.41402 0.34501 1.3406 0.56188 0.51259 0.11829 1.1435 0.23659 0.64074 0.11829 1.0646 0.21687 1.2913 0.29572 1.9912 1.0154 0.69989 0.72946 0.69989 1.9321z"/>
              <path d="m33.611 9.7579h-8.1128q0 1.0154 0.30558 1.7744 0.30558 0.74918 0.8379 1.2322 0.51259 0.47316 1.2125 0.70975 0.70975 0.23659 1.5575 0.23659 1.1238 0 2.2574-0.4436 1.1435-0.45344 1.6265-0.88718h0.09857v2.0208q-0.93647 0.39431-1.9123 0.66046-0.97591 0.26616-2.0504 0.26616-2.7404 0-4.2782-1.4786-1.5378-1.4885-1.5378-4.2191 0-2.701 1.4688-4.288 1.4786-1.5871 3.8839-1.5871 2.2278 0 3.4305 1.3013 1.2125 1.3013 1.2125 3.6967zm-1.8039-1.4195q-0.00981-1.4589-0.73932-2.2574-0.7196-0.79847-2.1982-0.79847-1.4885 0-2.3757 0.87732-0.87732 0.87732-0.99566 2.1785z"/>
              <path d="m43.291 5.9437h-0.09857q-0.41402-0.1029-0.80833-0.14405-0.38445-0.051448-0.91676-0.051448-0.85762 0-1.6561 0.40128-0.79847 0.39099-1.5378 1.0186v8.1593h-1.8533v-11.493h1.8533v1.6977q1.1041-0.92603 1.942-1.3067 0.84776-0.39099 1.7251-0.39099 0.48302 0 0.69989 0.030867 0.21687 0.020581 0.6506 0.0926z"/>
              <path d="m47.048 1.9939h-2.0898v-1.9939h2.0898zm-0.11829 13.334h-1.8533v-11.421h1.8533z"/>
              <path d="m60 9.5213q0 2.6911-1.38 4.2487-1.38 1.5575-3.6967 1.5575-2.3362 0-3.7163-1.5575-1.3702-1.5575-1.3702-4.2487 0-2.6911 1.3702-4.2486 1.38-1.5674 3.7163-1.5674 2.3165 0 3.6967 1.5674 1.38 1.5575 1.38 4.2486zm-1.9123 0q0-2.1391-0.8379-3.1742-0.8379-1.0449-2.3264-1.0449-1.5082 0-2.3462 1.0449-0.82804 1.0351-0.82804 3.1742 0 2.0701 0.8379 3.1446 0.8379 1.0646 2.3362 1.0646 1.4786 0 2.3165-1.0548 0.84776-1.0646 0.84776-3.1544z"/>
              <path d="m50.913 11.329 6.8258-5.1858 1.1848 1.5596-6.8258 5.1858z"/>
            </g>
            <path id="zserio-logo-z" d="m10.399 15.327h-10.399v-2.2771l5.8357-6.2005h-5.5991v-2.5926h10.055v2.2377l-5.747 6.1905h5.8554z" fill="#f00"/>
          </svg>
        </a>
      </div>
</#macro>

<#macro html_js_storage_functions>
      function setItemToStorage(storage, key, value) {
        try { // handle eventual SecurityError
          storage.setItem(key, value);
        } catch (e) {
          console.warn("Failed to set an item to the storage!", storage);
        }
      }

      function getItemFromStorage(storage, key) {
        try { // handle eventual SecurityError
          return storage.getItem(key);
        } catch (e) {
          console.warn("Failed to get an item from the storage!", storage);
          return null;
        }
      }
</#macro>

<#macro html_js_search_functions navId firstLevelName secondLevelName>
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
        let navElement = $("#${navId}");

        if (value == "") {
          // restore to default state
          navElement.find(".nav-${secondLevelName}s").css("display", "");
          navElement.find(".nav-link").css("display", "");
        } else {
          // show also second level nav-s which are not in the active first level nav
          navElement.find(".nav-${secondLevelName}s").show();
          navElement.find(".nav-link").hide(); // hide all links

          let searchPattern = buildSearchPattern(value);
          if (!searchPattern) {
            console.warn("Couldn't build a search pattern!")
            return;
          }

          navElement.find(".nav-${firstLevelName}").each(function(index, element) {
            let firstLevelNav = $(element);
            let firstLevelLink = firstLevelNav.find(".nav-link-${firstLevelName}");
            let firstLevelName = firstLevelLink.text();
            let showFirstLevelLink = firstLevelName.match(searchPattern);
            firstLevelNav.find(".nav-link-${secondLevelName}").each(function(index, element) {
              let secondLevelLink = $(element);
              let secondLevelName = secondLevelLink.text();
              let fullName = firstLevelName + "." + secondLevelName;

              if (fullName.match(searchPattern)) {
                secondLevelLink.show();
                showFirstLevelLink = true;
              }
            });
            if (showFirstLevelLink) {
              firstLevelLink.show();
            }
          });
        }
      }
</#macro>

<#macro html_js_search_setup storedSearchValueName>
        // re-apply current search value
        let searchValue = getItemFromStorage(sessionStorage, "${storedSearchValueName}");
        if (searchValue) {
          $("#search").val(searchValue);
          searchFilter(searchValue);
        }
        $("#search").on("keyup", function(e) {
          if (e.key == "Escape")
            $(this).val("");

          let value = $(this).val();
          setItemToStorage(sessionStorage, "${storedSearchValueName}", value);
          searchFilter(value);
        });
</#macro>
