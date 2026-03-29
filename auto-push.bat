@echo off
echo Preparing to automatically sync your latest code to GitHub...
echo.

:: Add all changed files
git add .

:: Commit with the current date and time
git commit -m "Auto-sync from local PC: %date% %time%"

:: Push to the main branch on GitHub
echo.
echo Pushing changes to GitHub Repository...
git push origin main

echo.
echo ==========================================================
echo SUCCESS! Your files are safely pushed to your GitHub.
echo.
echo This has also triggered the GitHub Actions workflow in 
echo the background to automatically upload your new changes
echo live to the MilesWeb server (sarkariexamai.com).
echo ==========================================================
pause
