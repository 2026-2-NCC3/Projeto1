@echo off
chcp 65001 >nul
cd /d "%~dp0"
where node >nul 2>nul
if errorlevel 1 (
  echo Node.js nao foi encontrado. Instale Node.js 22 ou superior.
  echo https://nodejs.org/
  pause
  exit /b 1
)
if not exist ".env" (
  echo Preparando login LOCAL de testes...
  call npm run setup
  echo.
  echo Anote o email e a senha que apareceram acima.
  echo.
)
echo Abra no navegador: http://127.0.0.1:3000
echo Para encerrar o painel, pressione Ctrl+C.
call npm start
pause
