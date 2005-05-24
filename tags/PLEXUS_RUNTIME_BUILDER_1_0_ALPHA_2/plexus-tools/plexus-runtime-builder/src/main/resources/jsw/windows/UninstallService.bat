@echo off

set _WRAPPER_CONF="%~f1"
if not %_WRAPPER_CONF%=="" goto startup
set _WRAPPER_CONF="..\..\conf\wrapper.conf"

:startup
"Wrapper.exe" -r %_WRAPPER_CONF%
if not errorlevel 1 goto end
pause

:end
set _WRAPPER_CONF=