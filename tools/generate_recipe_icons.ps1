Add-Type -AssemblyName System.Drawing

$ErrorActionPreference = "Stop"

$repo = Split-Path -Parent $PSScriptRoot
$res = Join-Path $repo "app/src/main/res"
$drawableNoDpi = Join-Path $res "drawable-nodpi"

New-Item -ItemType Directory -Force -Path $drawableNoDpi | Out-Null

function New-Brush([string]$hex) {
    return [System.Drawing.SolidBrush]::new([System.Drawing.ColorTranslator]::FromHtml($hex))
}

function New-Pen([string]$hex, [float]$width) {
    $pen = [System.Drawing.Pen]::new([System.Drawing.ColorTranslator]::FromHtml($hex), $width)
    $pen.StartCap = [System.Drawing.Drawing2D.LineCap]::Round
    $pen.EndCap = [System.Drawing.Drawing2D.LineCap]::Round
    $pen.LineJoin = [System.Drawing.Drawing2D.LineJoin]::Round
    return $pen
}

function Set-Quality($graphics) {
    $graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
    $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $graphics.CompositingQuality = [System.Drawing.Drawing2D.CompositingQuality]::HighQuality
}

function Add-FlamePath($path, [float]$x, [float]$y, [float]$w, [float]$h) {
    $path.StartFigure()
    $path.AddBezier($x + 0.48 * $w, $y + 1.00 * $h, $x + 0.24 * $w, $y + 0.68 * $h, $x + 0.06 * $w, $y + 0.40 * $h, $x + 0.28 * $w, $y + 0.12 * $h)
    $path.AddBezier($x + 0.28 * $w, $y + 0.12 * $h, $x + 0.52 * $w, $y - 0.04 * $h, $x + 0.92 * $w, $y + 0.18 * $h, $x + 0.76 * $w, $y + 0.50 * $h)
    $path.AddBezier($x + 0.76 * $w, $y + 0.50 * $h, $x + 0.66 * $w, $y + 0.72 * $h, $x + 0.54 * $w, $y + 0.84 * $h, $x + 0.48 * $w, $y + 1.00 * $h)
    $path.CloseFigure()
}

function Add-BowlPath($path, [float]$x, [float]$y, [float]$w, [float]$h) {
    $path.StartFigure()
    $path.AddLine($x + 0.03 * $w, $y + 0.19 * $h, $x + 0.97 * $w, $y + 0.19 * $h)
    $path.AddBezier($x + 0.97 * $w, $y + 0.19 * $h, $x + 0.98 * $w, $y + 0.47 * $h, $x + 0.82 * $w, $y + 0.74 * $h, $x + 0.63 * $w, $y + 0.84 * $h)
    $path.AddLine($x + 0.63 * $w, $y + 0.84 * $h, $x + 0.69 * $w, $y + 1.00 * $h)
    $path.AddLine($x + 0.69 * $w, $y + 1.00 * $h, $x + 0.31 * $w, $y + 1.00 * $h)
    $path.AddLine($x + 0.31 * $w, $y + 1.00 * $h, $x + 0.37 * $w, $y + 0.84 * $h)
    $path.AddBezier($x + 0.37 * $w, $y + 0.84 * $h, $x + 0.18 * $w, $y + 0.74 * $h, $x + 0.02 * $w, $y + 0.47 * $h, $x + 0.03 * $w, $y + 0.19 * $h)
    $path.CloseFigure()
}

function Draw-RecipeMark($graphics, [float]$size, [float]$offsetX, [float]$offsetY, [bool]$monochrome) {
    $dark = if ($monochrome) { "#1D1D1D" } else { "#2B2B2B" }
    $flame = if ($monochrome) { "#1D1D1D" } else { "#FFB000" }

    $bowlBrush = New-Brush $dark
    $flameBrush = New-Brush $flame
    $spoonPen = New-Pen $dark (0.075 * $size)

    $bowlPath = New-Object System.Drawing.Drawing2D.GraphicsPath
    Add-BowlPath $bowlPath $offsetX $offsetY $size $size
    $graphics.FillPath($bowlBrush, $bowlPath)

    $graphics.DrawLine(
        $spoonPen,
        $offsetX + 0.68 * $size,
        $offsetY + 0.40 * $size,
        $offsetX + 0.94 * $size,
        $offsetY + 0.16 * $size
    )

    $flameLeft = New-Object System.Drawing.Drawing2D.GraphicsPath
    Add-FlamePath $flameLeft ($offsetX + 0.33 * $size) ($offsetY + 0.03 * $size) (0.15 * $size) (0.34 * $size)
    $graphics.FillPath($flameBrush, $flameLeft)

    $flameRight = New-Object System.Drawing.Drawing2D.GraphicsPath
    Add-FlamePath $flameRight ($offsetX + 0.47 * $size) ($offsetY + 0.14 * $size) (0.11 * $size) (0.24 * $size)
    $graphics.FillPath($flameBrush, $flameRight)

    $bowlPath.Dispose()
    $flameLeft.Dispose()
    $flameRight.Dispose()
    $bowlBrush.Dispose()
    $flameBrush.Dispose()
    $spoonPen.Dispose()
}

function Save-ScaledIcon([string]$path, [int]$size, [scriptblock]$draw) {
    $bmp = New-Object System.Drawing.Bitmap $size, $size, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $graphics = [System.Drawing.Graphics]::FromImage($bmp)
    Set-Quality $graphics
    & $draw $graphics $size
    $bmp.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
    $graphics.Dispose()
    $bmp.Dispose()
}

$bgBrush = New-Brush "#FFF8F0"

Save-ScaledIcon (Join-Path $drawableNoDpi "ic_launcher_foreground_art.png") 432 {
    param($g, $size)
    Draw-RecipeMark $g ($size * 0.74) ($size * 0.13) ($size * 0.15) $false
}

Save-ScaledIcon (Join-Path $drawableNoDpi "ic_launcher_monochrome_art.png") 432 {
    param($g, $size)
    Draw-RecipeMark $g ($size * 0.74) ($size * 0.13) ($size * 0.15) $true
}

$legacySizes = @{
    "mipmap-mdpi" = 48
    "mipmap-hdpi" = 72
    "mipmap-xhdpi" = 96
    "mipmap-xxhdpi" = 144
    "mipmap-xxxhdpi" = 192
}

foreach ($entry in $legacySizes.GetEnumerator()) {
    $folder = Join-Path $res $entry.Key
    New-Item -ItemType Directory -Force -Path $folder | Out-Null

    Save-ScaledIcon (Join-Path $folder "ic_launcher.png") $entry.Value {
        param($g, $size)
        $g.Clear([System.Drawing.ColorTranslator]::FromHtml("#FFF8F0"))
        Draw-RecipeMark $g ($size * 0.72) ($size * 0.14) ($size * 0.16) $false
    }

    Save-ScaledIcon (Join-Path $folder "ic_launcher_round.png") $entry.Value {
        param($g, $size)
        $g.Clear([System.Drawing.Color]::Transparent)
        $g.FillEllipse($bgBrush, 0, 0, $size - 1, $size - 1)
        Draw-RecipeMark $g ($size * 0.64) ($size * 0.18) ($size * 0.20) $false
    }
}

$bgBrush.Dispose()
