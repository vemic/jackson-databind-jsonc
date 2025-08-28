@echo off
echo "=== Testing Release Workflow (Post-OSSRH Migration) ==="
echo.

echo "⚠️  OSSRH Migration Notice:"
echo "Sonatype OSSRH is discontinued as of June 30, 2025"
echo "This project now uses Maven Central Portal for publishing"
echo.

echo "Step 1: Validate build configuration"
call .\gradlew.bat validateCredentials
if %ERRORLEVEL% neq 0 (
    echo "❌ Configuration validation failed"
    exit /b 1
)

echo.
echo "Step 2: Test artifact building"
call .\gradlew.bat clean build fatJar
if %ERRORLEVEL% neq 0 (
    echo "❌ Artifact building failed"
    exit /b 1
)

echo.
echo "Step 3: Test local publishing"
call .\gradlew.bat publishLocalOnly
if %ERRORLEVEL% neq 0 (
    echo "❌ Local publishing failed"
    exit /b 1
)

echo.
echo "✅ Release workflow test - PASSED"
echo "Migration complete - GitHub releases will provide artifacts for Central Portal upload"
