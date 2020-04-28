$download_url = (Invoke-WebRequest "https://api.github.com/repos/trilogy-group/arch-as-code/releases/latest" | ConvertFrom-Json).assets.browser_download_url | select -first 1

New-Item -ItemType Directory -Force -Path $HOME\arch-as-code

(New-Object System.Net.WebClient).DownloadFile($download_url, "$HOME\arch-as-code\arch-as-code.tar.gz")

tar -xzv --strip-components 1 -f $HOME\arch-as-code\arch-as-code.tar.gz -C $HOME\arch-as-code

$Env:Path += ";$HOME\arch-as-code\bin"

$path = [Environment]::GetEnvironmentVariable('Path', 'Machine')
$newpath = $path + ';$HOME\arch-as-code\bin'
[Environment]::SetEnvironmentVariable("Path", $newpath, 'Machine')


arch-as-code --version



