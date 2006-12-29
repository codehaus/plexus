@echo off
rem
rem Find the application home.
rem
if "%OS%"=="Windows_NT" goto nt

echo This is not NT, so please edit this script and set _APP_HOME manually
set _APP_HOME=..

goto conf

:nt
rem %~dp0 is name of current script under NT
set _APP_HOME=%~dp0
rem : operator works similar to make : operator
rem set _APP_HOME=%_APP_HOME:\bin\=%


rem
rem Find the wrapper.conf
rem
:conf
set _WRAPPER_CONF=wrapper.conf
SET TOOLS_JAR=%JAVA_HOME%\lib\tools.jar

:chkMHome
if not "%PLEXUS_HOME%"=="" goto valMHome

if "%OS%"=="Windows_NT" SET PLEXUS_HOME=%~dp0..\..
if not "%PLEXUS_HOME%"=="" goto valMHome

echo.
echo ERROR: PLEXUS_HOME not found in your environment.
echo Please set the PLEXUS_HOME variable in your environment to match the
echo location of the PLEXUS installation
echo.
goto end

:valMHome
if exist "%PLEXUS_HOME%\bin\plexus.bat" goto chkMBase

echo.
echo ERROR: PLEXUS_HOME is set to an invalid directory.
echo PLEXUS_HOME = %PLEXUS_HOME%
echo Please set the PLEXUS_HOME variable in your environment to match the
echo location of the PLEXUS installation
echo.
goto end

:chkMBase
if not "%PLEXUS_BASE%"=="" goto valMBase

if "%OS%"=="Windows_NT" SET PLEXUS_BASE=%PLEXUS_HOME%
if not "%PLEXUS_BASE%"=="" goto valMBase

echo.
echo ERROR: PLEXUS_BASE not found in your environment.
echo Please set the PLEXUS_BASE variable in your environment to match the
echo location of the PLEXUS installation
echo.
goto end

:valMBase
if exist "%PLEXUS_BASE%\bin\plexus.bat" goto startup

echo.
echo ERROR: PLEXUS_BASE is set to an invalid directory.
echo PLEXUS_BASE = %PLEXUS_BASE%
echo Please set the PLEXUS_BASE variable in your environment to match the
echo location of the PLEXUS installation
echo.
goto end
@REM ==== END VALIDATION ====

rem
rem Run the application.
rem At runtime, the current directory will be that of Wrapper.exe
rem
:startup
"%_APP_HOME%wrapper.exe" -c %_WRAPPER_CONF%
if not errorlevel 1 goto end
pause

:end
set _APP_HOME=
set _WRAPPER_CONF=
set PLEXUS_HOME=
set PLEXUS_BASE=
set TOOLS_JAR=
