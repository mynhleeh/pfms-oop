param (
    [Parameter(Mandatory = $true)]
    [string]$FilePath
)

$scriptPath = Join-Path $PSScriptRoot "r08.py"
python -I $scriptPath $FilePath
exit $LASTEXITCODE

