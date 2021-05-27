$JAVA_HOME = 'C:\Apps\jdk'
$GIT_HOME = 'C:\Apps\git\cmd'
$MAVEN_HOME = 'C:\Apps\ideaIU\plugins\maven\lib\maven3\bin'


#-----------全局变量-------------
#项目名称
$PROJECT_NAME = '-'
#项目版本
$PROJECT_VERSION = '-'
#构建成品输出位置 默认为当前目录下的 built 文件夹
$OUTPUT_URL = Join-Path (Get-Location) 'built'

#设置标题
function SetTitle($title)
{
    $host.ui.RawUI.WindowTitle = $title
}

function SetOutputUrl()
{
    while ($true)
    {
        $tempOutputUrl = Read-Host '请输入打包文件的输出目录 留空为当前目录下的 built'
        if ($tempOutputUrl -ne '')
        {
            $script:OUTPUT_URL = $tempOutputUrl
        }
        $urlCanUse = Test-Path $OUTPUT_URL
        if (-not$urlCanUse)
        {
            Write-Host "$OUTPUT_URL 不存在,已自动创建!!!" -ForegroundColor Yellow
            $null = mkdir $OUTPUT_URL
            break
        }
        else
        {
            $isNotEmptyFolder = Test-Path (Join-Path $OUTPUT_URL '*')
            if ($isNotEmptyFolder)
            {
                $choiceNumber = Read-Host "输出目录文件夹不为空 , 强制覆盖(yes) | 重新输入(AnyKey) "
                if ($choiceNumber -eq 'yes')
                {
                    $Files = Get-ChildItem $OUTPUT_URL -Force
                    foreach ($File in $Files)
                    {
                        $FilePath = $File.FullName
                        Remove-Item -Path $FilePath -Recurse -Force
                    }
                    break
                }
            }
            else
            {
                break
            }
        }
    }
    Write-Host "打包后的文件将存储在 $OUTPUT_URL" -ForegroundColor Green
}

#显示 选项
function DisplayInfo()
{
    SetTitle "$PROJECT_NAME 构建脚本"
    Write-Host "此 Build 脚本,用于 $PROJECT_NAME 项目" -ForegroundColor Cyan
    Write-Host '建议事先请在脚本中配置一下 基本变量 !!!' -ForegroundColor Cyan
    Write-Host '如果卡住请按一下回车 !!!' -ForegroundColor Cyan
    Write-Host '  1. 运行项目' -ForegroundColor Green
    Write-Host '  2. 构建项目 (不包括依赖项)' -ForegroundColor Magenta
    Write-Host '  3. 构建项目 (包括依赖项)' -ForegroundColor Magenta
    Write-Host '  0. 退出' -ForegroundColor Yellow
}


#设置
function SetPageCode()
{
    $null = chcp.com(65001)
}
#设置 临时环境变量
function SetTempEnvironmentVariables()
{
    $PathVariables = $JAVA_HOME + '\bin;' + $GIT_HOME + ';' + $MAVEN_HOME
    $env:Path = $env:Path + $PathVariables
    $env:JAVA_HOME = $JAVA_HOME
}

function ToZip($from, $to)
{
    Compress-Archive -Path $from  -DestinationPath $to -Force
}

# 输出带颜色的文字 colorCode 参照表如下
#
function aaa($string, $colorCode)
{
    #    $color=36m
    Write-Output  $string "[36m此 Build 脚本,用于 $PROJECT_NAME 项目[0m"
    Write-Host "Red on white text." -ForegroundColor red -BackgroundColor white
    Write-Output  $string "[36m此 Build 脚本,用于 $PROJECT_NAME 项目[0m"
}


#运行项目
function RunProject()
{
    SetTitle "运行 $PROJECT_NAME 项目中..."
    Clear-Host
    mvn compile exec:java
}

#构建项目并复制 lib
function BuildProjectWithLib()
{

}

#构建项目但不复制 lib
function BuildProjectWithoutLib()
{
    SetOutputUrl
    Write-Host "开始打包 $PROJECT_NAME 版本为: $PROJECT_VERSION" -ForegroundColor Green
    call mvn clean package
    Move-Item "target\$PROJECT_NAME-$PROJECT_VERSION.jar" $OUTPUT_URL
    Move-Item  "target\lib" "$OUTPUT_URL\lib"
    Copy-Item "src\main\resources\c\*" $OUTPUT_URL
    Copy-Item "src\main\resources\scx-config.json" $OUTPUT_URL
    Write-Output "@echo off" > "$OUTPUT_URL\startup.bat"
    Write-Output "chcp 65001" >> "$OUTPUT_URL\startup.bat"
    Write-Output "set JAVA_TOOL_OPTIONS = -Dfile.encoding = UTF-8 -Duser.language = zh" >> "$OUTPUT_URL\startup.bat"
    Write-Output "java -jar scx-%scxVersion%.jar --supportAnsiColor = false" >> "$OUTPUT_URL\startup.bat"
    Write-Host '清理残余文件'
    call mvn clean
    Write-Host '打包成功'
    Write-Host "后台项目是$OUTPUT_URL\$PROJECT_NAME-$PROJECT_VERSION.jar"
    Write-Host "启动脚本是$OUTPUT_URL\startup.bat"
    pause
    explorer $OUTPUT_URL
}
#分析 项目版本 版本
function GetProjectVersion()
{

}

function aaaa()
{
    foreach ($color1 in (0..15))
    {

        Write-Host -BackgroundColor ([ConsoleColor]$color1) -Object ([ConsoleColor]$color1) -NoNewline
        Write-Host
    }

}
#检查项目 并设置基本变量
function CheckProject()
{
    [XML]$xmlfile = Get-Content .\pom.xml
    $script:PROJECT_NAME = $xmlfile.project.artifactId
    $script:PROJECT_VERSION = $xmlfile.project.version
}

function Select-FolderDialog
{
    param([string]$Directory, [string]$Description, [boolean]$ShowNewFolderButton)
    [System.Reflection.Assembly]::LoadWithPartialName("System.Windows.Forms") | Out-Null
    $objForm = New-Object System.Windows.Forms.FolderBrowserDialog
    $objForm.RootFolder = $Directory
    $objForm.Description = $Description
    $objForm.ShowNewFolderButton = $ShowNewFolderButton
    $Show = $objForm.ShowDialog()
    if ($Show -eq "OK")
    {
        return $objForm.SelectedPath
    }
    else
    {
        #需要输出错误信息的话可以取消下一行的注释
        #Write-Error "error information here"
    }
}



function DisplayChoice()
{
    $choiceNumber = Read-Host '请选择要进行的操作数字 , 然后按回车'
    if ($choiceNumber -eq '1')
    {
        RunProject
    }
    elseif ($choiceNumber -eq '2')
    {
        BuildProjectWithoutLib
    }
    elseif ($choiceNumber -eq '3')
    {
        BuildProjectWithLib
    }
    elseif ($choiceNumber -eq '0')
    {
        Write-Host '已退出脚本!!!' -ForegroundColor red
        exit
    }
    else
    {
        Write-Host  "输入的操作数字 $choiceNumber 有误 , 请重新输入 !!!" -ForegroundColor red
        DisplayChoice
    }
}



#主函数
function Main()
{
    SetPageCode
    CheckProject
    SetTempEnvironmentVariables
    DisplayInfo
    DisplayChoice
}

#执行主函数
Main
