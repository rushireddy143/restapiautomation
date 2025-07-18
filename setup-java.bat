@echo off
echo Setting up JAVA_HOME environment variable...
echo.

REM Check if Java is installed
echo Checking for Java installations...
if exist "C:\Program Files\Amazon Corretto" (
    for /d %%i in ("C:\Program Files\Amazon Corretto\jdk*") do (
        set JAVA_PATH=%%i
        echo Found Java at: %%i
    )
) else if exist "C:\Program Files\Java" (
    for /d %%i in ("C:\Program Files\Java\jdk*") do (
        set JAVA_PATH=%%i
        echo Found Java at: %%i
    )
) else if exist "C:\Program Files (x86)\Java" (
    for /d %%i in ("C:\Program Files (x86)\Java\jdk*") do (
        set JAVA_PATH=%%i
        echo Found Java at: %%i
    )
) else (
    echo Java not found! Please install Java first.
    echo Download from: https://adoptium.net/ or https://aws.amazon.com/corretto/
    pause
    exit /b 1
)

if defined JAVA_PATH (
    echo.
    echo Setting JAVA_HOME to: %JAVA_PATH%
    setx JAVA_HOME "%JAVA_PATH%" /M
    echo.
    echo Adding Java to PATH...
    setx PATH "%PATH%;%JAVA_PATH%\bin" /M
    echo.
    echo JAVA_HOME setup complete!
    echo Please restart your command prompt or IDE for changes to take effect.
    echo.
    echo Verifying installation...
    "%JAVA_PATH%\bin\java" -version
) else (
    echo Could not determine Java installation path.
    echo Please set JAVA_HOME manually.
)

pause
