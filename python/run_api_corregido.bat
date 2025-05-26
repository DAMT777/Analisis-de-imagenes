@echo off
echo Activando entorno virtual...
call ..\tf-env\Scripts\activate.bat

echo Ejecutando API...
python api.py

pause
