# PowerShell script to extract an icon from an executable
param(
    [String]$exePath,
    [String]$iconPath = "icon.ico"
)

Add-Type -AssemblyName System.Drawing
$icon = [System.Drawing.Icon]::ExtractAssociatedIcon($exePath)
$icon.ToBitmap().Save($iconPath)