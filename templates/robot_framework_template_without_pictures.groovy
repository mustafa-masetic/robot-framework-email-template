<%
import java.text.DateFormat
import java.text.SimpleDateFormat
%>

<STYLE>
  BODY, TABLE, TD, TH, P {
    font-family: Calibri, Verdana, Helvetica, sans serif;
    font-size: 12px;
    color: black;
  }
  .console {
    font-family: Courier New;
  }
  .filesChanged {
    width: 10%;
    padding-left: 10px;
  }
  .section {
    width: 100%;
    border: thin black dotted;
  }
  .td-title-main {
    color: white;
    font-size: 200%;
    padding-left: 5px;
    font-weight: bold;
  }
  .td-title {
    color: white;
    font-size: 120%;
    font-weight: bold;
    padding-left: 5px;
    text-transform: uppercase;
  }
  .td-title-tests {
    font-weight: bold;
    font-size: 120%;
  }
  .td-header-maven-module {
    font-weight: bold;
    font-size: 120%;    
  }
  .td-maven-artifact {
    padding-left: 5px;
  }
  .tr-title {
    background-color: <%= (build.result == null || build.result.toString() == 'SUCCESS') ? '#27AE60' : build.result.toString() == 'FAILURE' ? '#E74C3C' : '#f4e242' %>;
  }
  .test {
    padding-left: 20px;
  }
  .test-fixed {
    color: #27AE60;
  }
  .test-failed {
    color: #E74C3C;
  }
</STYLE>
<BODY>
  <!-- BUILD RESULT -->
  <table class="section">
    <tr class="tr-title">
      <td class="td-title-main" colspan=2>
        BUILD ${build.result ?: 'COMPLETED'}
      </td>
    </tr>
    <tr>
      <td>URL:</td>
      <td><A href="${rooturl}${build.url}">${rooturl}${build.url}</A></td>
    </tr>
    <tr>
      <td>Project:</td>
      <td>${project.name}</td>
    </tr>
    <tr>
      <td>Date:</td>
      <td>${it.timestampString}</td>
    </tr>
    <tr>
      <td>Duration:</td>
      <td>${build.durationString}</td>
    </tr>
    <tr>
      <td>Cause:</td>
      <td><% build.causes.each() { cause -> %> ${cause.shortDescription} <%  } %></td>
    </tr>
  </table>
  <br/>

  <!-- ROBOT FRAMEWORK RESULTS -->
    <%
        def robotResults = false
        def actions = build.actions // List<hudson.model.Action>
        actions.each() { action ->
        if( action instanceof hudson.plugins.robot.RobotBuildAction ) { //hudson.plugins.robot.RobotBuildAction
        robotResults = true %>

  <table class="section">
    <tr class="tr-title">
      <td class="td-title-main" colspan=2>
        ROBOT FRAMEWORK RESULTS
      </td>
    </tr>
    
    <tr>
        <td style="font-size: 20px; padding-left: 5px">Test summary</td>
    </tr>

    <tr>
    <td>
        <table id="robot-summary-table" style="border-collapse:collapse;text-align:center;">
        <tr>
            <th style="font-weight:normal;padding:0 10px;">Type </th>
            <th style="font-weight:normal;padding:0 10px;">Total</th>
            <th style="font-weight:normal;padding:0 10px;">Failed</th>
            <th style="font-weight:normal;padding:0 10px;">Passed</th>
            <th style="font-weight:normal;padding:0 10px;">Pass %</th>
        </tr>
        <tr style="font-weight:bold">
            <th style="font-weight:normal">Critical tests</th>
            <td style="border-right:1px solid #000;border-bottom:1px solid #000;">${action.result.criticalTotal}</td>
            <td style="border-right:1px solid #000;border-bottom:1px solid #000;color:red">${action.result.criticalFailed}</td>
            <td style="border-right:1px solid #000;border-bottom:1px solid #000;color:green">${action.result.criticalPassed}</td>
            <td style="border-bottom:1px solid #000;">${action.criticalPassPercentage}</td>
        </tr>
        <tr style="font-weight:bold">
            <th style="font-weight:normal">All tests</th>
            <td style="border-right:1px solid #000;">${action.result.overallTotal}</td>
            <td style="border-right:1px solid #000;color:red">${action.result.overallFailed}</td>
            <td style="border-right:1px solid #000;color:green">${action.result.overallPassed}</td>
            <td>${action.overallPassPercentage}</td>
        </tr>
            <tr style="font-weight:bold">
            <th style="font-weight:normal">Duration</hd>
            <td colspan=4 align="right">${action.result.humanReadableDuration}</td>
        </tr>
        <tr><td><br></td></tr>
        </table>
    </td>
    </tr>

    <tr>
        <td style="font-size: 20px; padding-left: 5px">Statistics by Suite</td>
    </tr>

    <tr>
    <td>
    <table cellspacing="1" cellpadding="4" border="2" align="left">
        <thead>
            <tr bgcolor="#F3F3F3">
                <td><b>Name      </b></td>
                <td><b>Failed tests     </b></td>
               <td><b>Passed tests     </b></td>
                <td><b>Duration</b></td>
            </tr>
        </thead>
        <tbody>
        <% 
        def suites = action.result.allSuites
        suites.each() { suite ->
            def currSuite = suite
            def suiteName = currSuite.displayName
        // ignore top 2 elements in the structure as they are placeholders
        while (currSuite.parent != null && currSuite.parent.parent != null) {
        currSuite = currSuite.parent
        suiteName = currSuite.displayName + "." + suiteName
        } %>
            <tr>
                <td>
                    <b><%= suiteName %></b>
                </td>
                <td style="border-right:1px solid #000;border-bottom:1px solid #000;color:red">${suite.failed}</td>
                <td style="border-right:1px solid #000;border-bottom:1px solid #000;color:green">${suite.passed}</td>
                <td>${suite.humanReadableDuration}</td>
            </tr>
        <%  DateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SS")
        def execDateTcPairs = []
        suite.caseResults.each() { tc ->
        Date execDate = format.parse(tc.starttime)
        execDateTcPairs << [execDate, tc]
        }
        // primary sort execDate, secondary displayName
        execDateTcPairs = execDateTcPairs.sort{ a,b -> a[1].displayName <=> b[1].displayName }
        execDateTcPairs = execDateTcPairs.sort{ a,b -> a[0] <=> b[0] }
         // tests
        } %>
        </tbody>
    </table>
    </td>
    </tr>

    <tr>
        <td style="font-size: 20px; padding-left: 5px">Test Execution Results</td>
    </tr>

    <tr>

    <td>
    <table cellspacing="0" cellpadding="4" border="1" align="left">
        <thead>
            <tr bgcolor="#F3F3F3">
                <td><b> Test Name</b></td>
                <td><b>Status</b></td>
               <td><b>Message</b></td>
                <td><b>Execution</b></td>
                <td><b>Duration</b></td>
            </tr>
        </thead>
        <tbody>
        <% def suites1 = action.result.allSuites
        suites1.each() { suite ->
        def currSuite = suite
        def suiteName = currSuite.displayName
        // ignore top 2 elements in the structure as they are placeholders
        while (currSuite.parent != null && currSuite.parent.parent != null) {
        currSuite = currSuite.parent
        suiteName = currSuite.displayName + "." + suiteName
        } %>
            <tr>
                <td colspan="4">
                    <b><%= suiteName %></b>
                </td>
                <td>${suite.humanReadableDuration}</td>
            </tr>
        <%  DateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SS")
        def execDateTcPairs = []
        suite.caseResults.each() { tc ->
        Date execDate = format.parse(tc.starttime)
        execDateTcPairs << [execDate, tc]
        }
        // primary sort execDate, secondary displayName
        execDateTcPairs = execDateTcPairs.sort{ a,b -> a[1].displayName <=> b[1].displayName }
        execDateTcPairs = execDateTcPairs.sort{ a,b -> a[0] <=> b[0] }
        def i = 1
        execDateTcPairs.each() {
        def execDate = it[0]
        def tc = it[1]  %>
            <tr>
                <td><a href="${rooturl}${build.url}robot/report/log.html#s1-s1-t<%= i%>"><%= tc.displayName %></a></td>
                <% i = i + 1 %>

                <% if(tc.isPassed()){ %>
                <td style="color: #66CC00">PASS</td>
                <% if(tc.isFailed()){ %>
                <td style="color: #FF3333">FAIL</td>
                <% if(tc.isSkipped()){ %>
                <td style="color: #FFFF01">SKIP</td>
                <% } %>
                <td><%      if ( tc.errorMsg == null ) {
                        tc.errorMsg=""
    } else {
         tc.errorMsg
}%>
<%= tc.errorMsg %>
</td>
                <td><%= execDate %></td>
                <td>${tc.humanReadableDuration}</td>
            </tr>
        <%  } // tests
        } // suites %>
        </tbody>
    </table>
    </td>
    </tr>

  </table>
    <%
    } // robot results
    }
    %>

  <br/>

  <!-- CHANGE SET -->
  <%
  def changeSets = build.changeSets
  if(changeSets != null) {
    def hadChanges = false %>
  <table class="section">
    <tr class="tr-title">
      <td class="td-title" colspan="2">CHANGES</td>
    </tr>
    <% changeSets.each() { 
      cs_list -> cs_list.each() { 
        cs -> hadChanges = true %>
    <tr>
      <td>
        Revision
        <%= cs.metaClass.hasProperty('commitId') ? cs.commitId : cs.metaClass.hasProperty('revision') ? cs.revision : cs.metaClass.hasProperty('changeNumber') ? cs.changeNumber : "" %>
        by <B><%= cs.author %></B>
      </td>
      <td>${cs.msgAnnotated}</td>
    </tr>
        <% cs.affectedFiles.each() {
          p -> %>
    <tr>
      <td class="filesChanged">${p.editType.name}</td>
      <td>${p.path}</td>
    </tr>
        <% }
      }
    }
    if ( !hadChanges ) { %>
    <tr>
      <td colspan="2">No Changes</td>
    </tr>
    <% } %>
  </table>
  <br/>
  <% } %>

<!-- ARTIFACTS -->
  <% 
  def artifacts = build.artifacts
  if ( artifacts != null && artifacts.size() > 0 ) { %>
  <table class="section">
    <tr class="tr-title">
      <td class="td-title">BUILD ARTIFACTS</td>
    </tr>
    <% artifacts.each() {
      f -> %>
      <tr>
        <td>
          <a href="${rooturl}${build.url}artifact/${f}">${f}</a>
      </td>
    </tr>
    <% } %>
  </table>
  <br/>
  <% } %>

<!-- MAVEN ARTIFACTS -->
  <%
  try {
    def mbuilds = build.moduleBuilds
    if ( mbuilds != null ) { %>
  <table class="section">
    <tr class="tr-title">
      <td class="td-title">BUILD ARTIFACTS</td>
    </tr>
      <%
      try {
        mbuilds.each() {
          m -> %>
    <tr>
      <td class="td-header-maven-module">${m.key.displayName}</td>
    </tr>
          <%
          m.value.each() { 
            mvnbld -> def artifactz = mvnbld.artifacts
            if ( artifactz != null && artifactz.size() > 0) { %>
    <tr>
      <td class="td-maven-artifact">
              <% artifactz.each() {
                f -> %>
        <a href="${rooturl}${mvnbld.url}artifact/${f}">${f}</a><br/>
              <% } %>
      </td>
    </tr>
            <% }
          }
        }
      } catch(e) {
        // we don't do anything
      } %>
  </table>
  <br/>
    <% }
  } catch(e) {
    // we don't do anything
  } %>

<!-- JUnit TEMPLATE -->

  <%
  def junitResultList = it.JUnitTestResult
  try {
    def cucumberTestResultAction = it.getAction("org.jenkinsci.plugins.cucumber.jsontestsupport.CucumberTestResultAction")
    junitResultList.add( cucumberTestResultAction.getResult() )
  } catch(e) {
    //cucumberTestResultAction not exist in this build
  }
  if ( junitResultList.size() > 0 ) { %>
  <table class="section">
    <tr class="tr-title">
      <td class="td-title" colspan="5">${junitResultList.first().displayName}</td>
    </tr>
    <tr>
        <td class="td-title-tests">Name</td>
        <td class="td-title-tests">Failed</td>
        <td class="td-title-tests">Passed</td>
        <td class="td-title-tests">Skipped</td>
        <td class="td-title-tests">Total</td>
      </tr>
    <% junitResultList.each {
      junitResult -> junitResult.getChildren().each {
        packageResult -> %>
    <tr>
      <td>${packageResult.getName()}</td>
      <td>${packageResult.getFailCount()}</td>
      <td>${packageResult.getPassCount()}</td>
      <td>${packageResult.getSkipCount()}</td>
      <td>${packageResult.getPassCount() + packageResult.getFailCount() + packageResult.getSkipCount()}</td>
    </tr>
    <% packageResult.getPassedTests().findAll({it.getStatus().toString() == "FIXED";}).each{
        test -> %>
            <tr>
              <td class="test test-fixed" colspan="5">
                ${test.getFullName()} ${test.getStatus()}
              </td>
            </tr>
        <% } %>
        <% packageResult.getFailedTests().sort({a,b -> a.getAge() <=> b.getAge()}).each{
          failed_test -> %>
    <tr>
      <td class="test test-failed" colspan="5">
        ${failed_test.getFullName()} (Age: ${failed_test.getAge()})
      </td>
    </tr>
        <% }
      }
    } %>
  </table>
  <br/>
  <% } %>

<!-- CONSOLE OUTPUT -->
  <%
  if ( build.result == hudson.model.Result.FAILURE ) { %>
  <table class="section" cellpadding="0" cellspacing="0">
    <tr class="tr-title">
      <td class="td-title">CONSOLE OUTPUT</td>
    </tr>
    <% 	build.getLog(100).each() {
      line -> %>
	  <tr>
      <td class="console">${org.apache.commons.lang.StringEscapeUtils.escapeHtml(line)}</td>
    </tr>
    <% } %>
  </table>
  <br/>
  <% } %>
</BODY>