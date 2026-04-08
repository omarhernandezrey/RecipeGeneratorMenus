@echo off
set "PATH=C:\Program Files\nodejs;C:\Users\herna\AppData\Roaming\npm;%PATH%"
echo Firebase CLI version:
firebase --version
echo.
echo Desplegando reglas de Firestore...
cd /d "C:\Users\herna\OneDrive\Documents\RecipeGeneratorMenus"
firebase deploy --only firestore:rules --project recipe-generator-app-417d0