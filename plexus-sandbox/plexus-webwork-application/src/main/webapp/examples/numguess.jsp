<%@ taglib uri="webwork" prefix="webwork" %>
<%@ taglib uri="webwork" prefix="ui" %> <html>
<head><title>Number Guess</title></head>
<body bgcolor="white">
<font size=4>
<webwork:if test="guessBean/success==true">
  Congratulations!  You got it.
  And after just <webwork:property value="guessBean/numGuesses"/> tries.<p>
  Not sure what to do here?
  Care to <a href="<webwork:url value="numguess.action?begin=true"/>">try again</a>?
</webwork:if>
<webwork:elseIf test="guessBean/numGuesses == 0">
  Welcome to the Number Guess game.<p>
  I'm thinking of a number between 1 and 100.<p>

  <form action="<webwork:url value="numguess.action"/>" method="POST">
   <ui:textfield label="'What\'s your guess?'" name="'guess'"/>
   <input type=submit value="Submit">
  </form>
</webwork:elseIf>
<webwork:else>
  Good guess, but nope.  Try <b><webwork:property value="guessBean/hint"/></b>.
  You have made <webwork:property value="guessBean/numGuesses"/> guesses.<p>
  I'm thinking of a number between 1 and 100.<p>

  <form action="<webwork:url value="numguess.action"/>" method="POST">
   <ui:textfield label="'What\'s your guess?'" name="'guess'"/>
   <input type=submit value="Submit">
  </form>
</webwork:else>
</font>
</body>
</html>