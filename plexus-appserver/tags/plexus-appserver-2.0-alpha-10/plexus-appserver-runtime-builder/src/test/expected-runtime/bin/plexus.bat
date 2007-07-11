@REM ----------------------------------------------------------------------------
@REM Plexus Start Up Batch script
@REM
@REM Required ENV vars:
@REM JAVA_HOME - location of a JDK home dir
@REM PLEXUS_HOME - location of Plexus installed home dir
@REM
@REM Optional ENV vars
@REM PLEXUS_BATCH_ECHO - set to 'on' to enable the echoing of the batch commands
@REM PLEXUS_BATCH_PAUSE - set to 'on' to wait for a key stroke before ending
@REM PLEXUS_OPTS - parameters passed to the Java VM when running Plexus
@REM     e.g. to debug Plexus itself, use
@REM set PLEXUS_OPTS=-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
@REM
@REM Utility rquirements:
@REM Windows' find.exe
@REM Windows' cmd.exe (NT) OR command.com ( 98 or ME )
@REM ----------------------------------------------------------------------------
set PLEXUS_HOME=
set CLASSWORLDS_VERSION=1.1-SNAPSHOT
set MAIN_CLASS=org.codehaus.classworlds.Launcher
set PLEXUS_OPTS="-Xmx128m"

@REM Begin all REM lines with '@' in case PLEXUS_BATCH_ECHO is 'on'
@REM echo on
@REM enable echoing my setting PLEXUS_BATCH_ECHO to 'on'
if "%PLEXUS_BATCH_ECHO%" == "on"  echo %PLEXUS_BATCH_ECHO%

@REM Execute a user defined script before this one
if exist "%HOME%\plexusrc_pre.bat" call "%HOME%\plexusrc_pre.bat"

@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" @setlocal

@REM For Windows NT, use cmd.exe to execute the "CD" later
@REM For Win 98ME, use comand.com - if neither is found , default to use
@REM cmd.exe in the path and hope we'll hit a Win version of it
@REM FYI: Using command.com on Win NT causes "Parameter format not correct" error
set PLEXUS_COMMAND_COM="cmd.exe"
if exist "%SystemRoot%\system32\cmd.exe" set PLEXUS_COMMAND_COM="%SystemRoot%\system32\cmd.exe"
if exist "%SystemRoot%\command.com" set PLEXUS_COMMAND_COM="%SystemRoot%\command.com"

@REM Use explicit find.exe to prevent cygwin and others find.exe from being
@REM used instead - we use this to test dir existance in a cross-win-platform way
set PLEXUS_FIND_EXE="find.exe"
if exist "%SystemRoot%\system32\find.exe" set PLEXUS_FIND_EXE="%SystemRoot%\system32\find.exe"
if exist "%SystemRoot%\command\find.exe" set PLEXUS_FIND_EXE="%SystemRoot%\command\find.exe"

@REM ==== START VALIDATION ====
if not "%JAVA_HOME%" == "" goto OkJHome

echo.
echo ERROR: JAVA_HOME not found in your environment.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation
echo.
goto end

:OkJHome
%PLEXUS_COMMAND_COM% /C DIR "%JAVA_HOME%" 2>&1 | %PLEXUS_FIND_EXE% /I /C "%JAVA_HOME%" >nul
if not errorlevel 1 goto chkMHome

echo.
echo ERROR: JAVA_HOME is set to an invalid directory.
echo JAVA_HOME = %JAVA_HOME%
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation
echo.
goto end

:chkMHome
if not "%PLEXUS_HOME%"=="" goto valMHome

echo.
echo ERROR: PLEXUS_HOME not found in your environment.
echo Please set the PLEXUS_HOME variable in your environment to match the
echo location of the Plexus installation
echo.
goto end

:valMHome
%PLEXUS_COMMAND_COM% /C DIR "%PLEXUS_HOME%" 2>&1 | %PLEXUS_FIND_EXE% /I /C "%PLEXUS_HOME%" >nul
if not errorlevel 1 goto init

echo.
echo ERROR: PLEXUS_HOME is set to an invalid directory.
echo PLEXUS_HOME = %PLEXUS_HOME%
echo Please set the PLEXUS_HOME variable in your environment to match the
echo location of the Plexus installation
echo.
goto end
@REM ==== END VALIDATION ====

:init
@REM Decide how to startup depending on the version of windows

@REM -- Win98ME
if NOT "%OS%"=="Windows_NT" goto Win9xArg

@REM -- 4NT shell
if "%eval[2+2]" == "4" goto 4NTArgs

@REM -- Regular WinNT shell
set PLEXUS_CMD_LINE_ARGS=%*
goto endInit

@REM The 4NT Shell from jp software
:4NTArgs
set PLEXUS_CMD_LINE_ARGS=%$
goto endInit

:Win9xArg
@REM Slurp the command line arguments.  This loop allows for an unlimited number
@REM of agruments (up to the command line limit, anyway).
set PLEXUS_CMD_LINE_ARGS=
:Win9xApp
if %1a==a goto endInit
set PLEXUS_CMD_LINE_ARGS=%PLEXUS_CMD_LINE_ARGS% %1
shift
goto Win9xApp

@REM Reaching here means variables are defined and arguments have been captured
:endInit
SET PLEXUS_DEFAULT_OPTS="-Xmx160m"
SET PLEXUS_JAVA_EXE="%JAVA_HOME%\bin\java.exe"
SET PLEXUS_ENDORSED="-Djava.endorsed.dirs=%JAVA_HOME%\lib\endorsed;%PLEXUS_HOME%\lib\endorsed"

@REM Start PLEXUS
%PLEXUS_JAVA_EXE% "-Dplexus.home=%PLEXUS_HOME%" "-Dtools.jar=%JAVA_HOME%\lib\tools.jar" %PLEXUS_DEFAULT_OPTS% %PLEXUS_OPTS% -classpath %PLEXUS_CLASSPATH% %PLEXUS_MAIN_CLASS% %PLEXUS_CMD_LINE_ARGS% $PLEXUS_OPTS -classpath %PLEXUS_HOME%\lib\classworlds-%CLASSWORLDS_VERSION%.jar -Dclassworlds.conf=%PLEXUS_HOME%\conf\classworlds.conf  -Dplexus.core=%PLEXUS_HOME%\core -Dtools.jar=$TOOLS_JAR -Dplexus.home=%PLEXUS_HOME% org.codehaus.classworlds.Launcher %PLEXUS_HOME%\conf\plexus.conf
  

:end
@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" goto endNT

@REM For old DOS remove the set variables from ENV - we assume they were not set
@REM before we started - at least we don't leave any baggage around
set PLEXUS_COMMAND_COM=
set PLEXUS_FIND_EXE=
set PLEXUS_DEFAULT_OPTS=
set PLEXUS_JAVA_EXE=
set PLEXUS_CLASSPATH=
set PLEXUS_MAIN_CLASS=
set PLEXUS_CMD_LINE_ARGS=
SET PLEXUS_ENDORSED=
goto postExec

:endNT
@endlocal

:postExec
@REM if exist "%HOME%\plexusrc_post.bat" call "%HOME%\plexusrc_post.bat"
@REM pause the batch file if PLEXUS_BATCH_PAUSE is set to 'on'
@REM if "%PLEXUS_BATCH_PAUSE%" == "on" pause
