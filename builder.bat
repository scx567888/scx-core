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
set JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8 -Duser.language=zh
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
echo [32m开始读取 SCX 版本[0m
echo off
for /f "tokens=3 delims=><" %%i in ('findstr /i /c:"<version>" "pom.xml"') do (set "scxVersion=%%i")&goto endGetVersion
:endGetVersion
echo [32m SCX 版本为: %scxVersion%[0m
echo [36m开始打包 SCX [0m
md %outPutUrl%
call mvn clean package
move target\scx-%scxVersion%.jar %outPutUrl%
move target\lib %outPutUrl%\lib
xcopy src\main\resources\c %outPutUrl%\c\ /E /Y
copy src\main\resources\scx-config.json %outPutUrl%
echo @echo off > %outPutUrl%\startup.bat
echo chcp 65001 >> %outPutUrl%\startup.bat
echo set JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8 -Duser.language=zh >> %outPutUrl%\startup.bat
echo java -jar scx-%scxVersion%.jar --supportAnsiColor=false >> %outPutUrl%\startup.bat
echo [31m清理残余文件[0m
call mvn clean
echo [32m打包成功[0m
echo [35m后台项目是%outPutUrl%\scx-%scxVersion%.jar[0m
echo [36m启动脚本是%outPutUrl%\startup.bat[0m
pause
start %outPutUrl%
