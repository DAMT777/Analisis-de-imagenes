@echo off
REM Iniciar el script de Python en segundo plano
start "" /B C:/Users/jesus/tf-env/Scripts/python.exe C:/Users/jesus/IdeaProjects/Analisis-de-imagenes/src/main/python/app/api.py

REM Esperar 3 segundos para asegurar que el proceso inicie
timeout /T 3 >nul

REM Obtener el PID del proceso python.exe más reciente
for /f "tokens=2" %%a in ('tasklist /FI "IMAGENAME eq python.exe" /FO LIST ^| findstr "PID"') do (
    echo %%a
    goto :eof
)
