@echo off
chcp 65001
echo [36m此 Build 脚本,用于 SCX 项目[0m
echo [36m事先请配置一下 环境变量 !!![0m
echo [36m如果卡住请按一下回车 !!![0m
set /p outPutUrl=[32m请输入打包文件的输出目录 留空为当前目录:[0m
if  "%outPutUrl%"  ==  "" (set "outPutUrl=%cd%\built")
echo [33m打包后的文件将存储在 %outPutUrl%[0m
echo [36m正在设置临时环境变量[0m
@echo on
set Path=C:\Windows\system32;C:\Windows;C:\Apps\jdk\bin;C:\Apps\git\cmd;C:\Apps\ideaIU\plugins\maven\lib\maven3\bin;C:\Apps\node;C:\Apps\7z\;
set JAVA_HOME=C:\Apps\jdk
@echo off
echo [32m临时环境变量设置成功[0m
echo [32mJava 环境变量设置成功[0m
call java -version
echo [33mMvn 环境变量设置成功[0m
call mvn -version
echo [34mGit 环境变量设置成功[0m
call git --version
echo [35mNode 环境变量设置成功[0m
call node -v
echo [36mnpm 环境变量设置成功[0m
call npm -v
echo 清理无用文件
call git clean -df
call mvn clean
echo [35m开始获取服务端代码更新[0m
call git pull
echo [32m开始获取软件版本[0m
echo off
for /f "tokens=3 delims=><" %%i in ('findstr /i /c:"<version>" "pom.xml"') do (set "scxVersion=%%i")&goto endGetVersion
:endGetVersion
echo [32m服务端版本为: %scxVersion%[0m
echo [36m开始打包 服务端[0m
md %outPutUrl%
call mvn package -pl "!scx-attend,!scx-cms,!scx-ims,!scx-pile,!scx-reson,!scx-study,!scx-workflow"
move target\scx-%scxVersion%.jar %outPutUrl%
move target\lib %outPutUrl%\lib
xcopy src\main\resources\c %outPutUrl%\c\ /E /Y
copy src\main\resources\scx.json %outPutUrl%
echo java -jar scx-%scxVersion%.jar > %outPutUrl%\startup.bat
echo [31m清理残余文件[0m
call mvn clean
cd ..
cd scx-ui
echo [31m清理无用文件[0m
call git clean -df
echo [35m开始获取UI端代码更新[0m
call git pull
call npm run build
call 7z.exe a scx-ui.zip  .\dist\*
move scx-ui.zip   %outPutUrl%
del /f/s/q dist
rmdir /s/q dist
echo [32m打包成功[0m
echo [31m后台项目是%outPutUrl%\scx-%scxVersion%.jar[0m
echo [35m前台项目是%outPutUrl%\scx-ui.zip[0m
echo [36m启动脚本是%outPutUrl%\startup.bat[0m
pause
start %outPutUrl%
