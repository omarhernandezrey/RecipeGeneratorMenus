@echo off
set "PATH=C:\Program Files\nodejs;%PATH%"
echo Instalando firebase-tools...
"C:\Program Files\nodejs\npm.cmd" install -g firebase-tools
echo.
echo Estado de instalacion: %ERRORLEVEL%
