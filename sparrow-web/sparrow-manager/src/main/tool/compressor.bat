@echo off
rem author thinkgem@163.com
echo Compressor JS and CSS?
pause
cd %~dp0

rem call compressor\compressor.bat css
call compressor\compressor.bat json2.js
rem call compressor\compressor.bat common\crud-grid.js
rem call compressor\compressor.bat common\crud-tool.js
rem call compressor\compressor.bat common\event.js
rem call compressor\compressor.bat common\fush.js
rem call compressor\compressor.bat common\page.js
rem call compressor\compressor.bat common\toolbar.js

echo.
echo Compressor Success
pause
echo on