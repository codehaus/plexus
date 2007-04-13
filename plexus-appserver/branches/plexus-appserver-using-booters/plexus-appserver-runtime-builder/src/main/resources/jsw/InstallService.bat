@echo off

set _WRAPPER_CONF="%~f1"
if not %_WRAPPER_CONF%=="" goto conf
set _WRAPPER_CONF="wrapper.conf"

:conf
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

:startup
"Wrapper.exe" -i %_WRAPPER_CONF% set.plexushome="%PLEXUS_HOME%" set.plexusbase="%PLEXUS_BASE%" set.TOOLS_JAR="%TOOLS_JAR%"
if not errorlevel 1 goto end
pause

:end
set _WRAPPER_CONF=
set TOOLS_JAR=
set PLEXUS_HOME=
set PLEXUS_BASE=
