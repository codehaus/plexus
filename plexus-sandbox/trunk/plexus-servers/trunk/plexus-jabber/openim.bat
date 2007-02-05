@echo off

rem Slurp the command line arguments. This loop allows for an unlimited number
rem of arguments (up to the command line limit, anyway).
set OPENIM_CMD_LINE_ARGS=%1
if ""%1""=="""" goto doneStart
shift
:setupArgs
if ""%1""=="""" goto doneStart
set OPENIM_CMD_LINE_ARGS=%OPENIM_CMD_LINE_ARGS% %1
shift
goto setupArgs

rem This label provides a place for the argument list loop to break out 
rem and for NT handling to skip to.
:doneStart
set _JAVACMD=%JAVACMD%
set LOCALCLASSPATH=%CLASSPATH%

if "%JAVA_HOME%" == "" goto noJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=%JAVA_HOME%\bin\java.exe
if exist "%JAVA_HOME%\lib\tools.jar" set LOCALCLASSPATH=%JAVA_HOME%\lib\tools.jar;%LOCALCLASSPATH%
if exist "%JAVA_HOME%\lib\classes.zip" set LOCALCLASSPATH=%JAVA_HOME%\lib\classes.zip;%LOCALCLASSPATH%
goto checkMerlin

:noJavaHome
echo ERROR: JAVA_HOME not found in your environment.
echo
echo Please, set the JAVA_HOME variable in your environment to match the
echo location of the Java Virtual Machine you want to use.
goto mainEnd

:checkMerlin
set _MERLINCMD=%MERLINCMD%

if "%MERLIN_HOME%" == "" goto noMerlinHome
if not exist "%MERLIN_HOME%\bin\merlin.bat" goto noMerlinHome
if "%_MERLINCMD%" == "" set _MERLINCMD=%MERLIN_HOME%\bin\merlin.bat
goto checkOpenIM

:noMerlinHome
echo ERROR: MERLIN_HOME not found in your environment.
echo
echo Please, set the MERLIN_HOME variable in your environment to match the
echo location of Merlin distribution.
goto mainEnd

:checkOpenIM
if "%OPENIM_HOME%" == "" goto runOpenIMNoHome
goto runOpenIM

:runOpenIM
set RUN_CMD=%MERLIN_HOME%\bin\merlin.bat %OPENIM_HOME%\repository\openim\jars\openim-server-impl-1.2.1.jar -config file:/%OPENIM_HOME%\conf\config.xml -context %OPENIM_HOME% -kernel file:/%OPENIM_HOME%\conf\kernel.xml  %OPENIM_CMD_LINE_ARGS%
echo RUN CMD IS: %RUN_CMD%
call %RUN_CMD%
goto mainEnd

:runOpenIMNoHome
set OPENIM_HOME=c:/works/openim-1.2.1-bin
echo Setting OPENIM_HOME: %OPENIM_HOME%
set RUN_CMD=%MERLIN_HOME%\bin\merlin.bat %OPENIM_HOME%\repository\openim\jars\openim-server-impl-1.2.1.jar -config file:/%OPENIM_HOME%\conf\config.xml -context %OPENIM_HOME% -kernel file:/%OPENIM_HOME%\conf\kernel.xml  %OPENIM_CMD_LINE_ARGS%
echo RUN CMD IS: %RUN_CMD%
call %RUN_CMD%
set OPENIM_HOME=
goto mainEnd

:mainEnd
set LOCALCLASSPATH=
set _JAVACMD=
set _MERLINCMD=
set OPENIM_CMD_LINE_ARGS=
