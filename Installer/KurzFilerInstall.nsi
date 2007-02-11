;Installer für KurzFiler
; $Id$

  !include "MUI.nsh"


; The name of the installer
Name "KurzFiler"

; The file to write
OutFile "KurzFiler1.0-setup.exe"

; The default installation directory
InstallDir "$PROGRAMFILES\KurzFiler"

; Registry key to check for directory (so if you install again, it will 
; overwrite the old one automatically)
InstallDirRegKey HKLM SOFTWARE\MARCHALBRUEGGE\KurzFiler "Install_Dir"

;--------------------------------
;Interface Settings

  !define MUI_ABORTWARNING

;--------------------------------
;Language Selection Dialog Settings

  ;Remember the installer language
  !define MUI_LANGDLL_REGISTRY_ROOT "HKCU" 
  !define MUI_LANGDLL_REGISTRY_KEY "SOFTWARE\MARCHALBRUEGGE\KurzFiler" 
  !define MUI_LANGDLL_REGISTRY_VALUENAME "Installer Language"

;--------------------------------
;Pages

  !insertmacro MUI_PAGE_LICENSE "license.txt"
;  !insertmacro MUI_PAGE_COMPONENTS
  !insertmacro MUI_PAGE_DIRECTORY
  !insertmacro MUI_PAGE_INSTFILES
  
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES
  
;--------------------------------
;Languages
 
  !insertmacro MUI_LANGUAGE "English" # first language is the default language
  !insertmacro MUI_LANGUAGE "German"
  !insertmacro MUI_LANGUAGE "French"
  !insertmacro MUI_LANGUAGE "Spanish"
  !insertmacro MUI_LANGUAGE "SimpChinese"
  !insertmacro MUI_LANGUAGE "TradChinese"
  !insertmacro MUI_LANGUAGE "Japanese"
  !insertmacro MUI_LANGUAGE "Korean"
  !insertmacro MUI_LANGUAGE "Italian"
  !insertmacro MUI_LANGUAGE "Dutch"
  !insertmacro MUI_LANGUAGE "Danish"
  !insertmacro MUI_LANGUAGE "Swedish"
  !insertmacro MUI_LANGUAGE "Norwegian"
  !insertmacro MUI_LANGUAGE "Finnish"
  !insertmacro MUI_LANGUAGE "Greek"
  !insertmacro MUI_LANGUAGE "Russian"
  !insertmacro MUI_LANGUAGE "Portuguese"
  !insertmacro MUI_LANGUAGE "PortugueseBR"
  !insertmacro MUI_LANGUAGE "Polish"
  !insertmacro MUI_LANGUAGE "Ukrainian"
  !insertmacro MUI_LANGUAGE "Czech"
  !insertmacro MUI_LANGUAGE "Slovak"
  !insertmacro MUI_LANGUAGE "Croatian"
  !insertmacro MUI_LANGUAGE "Bulgarian"
  !insertmacro MUI_LANGUAGE "Hungarian"
  !insertmacro MUI_LANGUAGE "Thai"
  !insertmacro MUI_LANGUAGE "Romanian"
  !insertmacro MUI_LANGUAGE "Latvian"
  !insertmacro MUI_LANGUAGE "Macedonian"
  !insertmacro MUI_LANGUAGE "Estonian"
  !insertmacro MUI_LANGUAGE "Turkish"
  !insertmacro MUI_LANGUAGE "Lithuanian"
  !insertmacro MUI_LANGUAGE "Catalan"
  !insertmacro MUI_LANGUAGE "Slovenian"
  !insertmacro MUI_LANGUAGE "Serbian"
  !insertmacro MUI_LANGUAGE "SerbianLatin"
  !insertmacro MUI_LANGUAGE "Arabic"
  !insertmacro MUI_LANGUAGE "Farsi"
  !insertmacro MUI_LANGUAGE "Hebrew"
  !insertmacro MUI_LANGUAGE "Indonesian"
  !insertmacro MUI_LANGUAGE "Mongolian"
  !insertmacro MUI_LANGUAGE "Luxembourgish"
  !insertmacro MUI_LANGUAGE "Albanian"
  !insertmacro MUI_LANGUAGE "Breton"
  !insertmacro MUI_LANGUAGE "Belarusian"
  !insertmacro MUI_LANGUAGE "Icelandic"
  !insertmacro MUI_LANGUAGE "Malay"
  !insertmacro MUI_LANGUAGE "Bosnian"
  !insertmacro MUI_LANGUAGE "Kurdish"

;--------------------------------
;Reserve Files
  
  ;These files should be inserted before other files in the data block
  ;Keep these lines before any File command
  ;Only for solid compression (by default, solid compression is enabled for BZIP2 and LZMA)
  
  !insertmacro MUI_RESERVEFILE_LANGDLL
  
Section "-KurzFiler"
  ; Set output path to the installation directory.
  SetOutPath $INSTDIR

  CreateDirectory "$INSTDIR"

  ; Put file there
  File "JarRunner.exe"
  File "..\KurzFiler.jar" 
  File "k2x00.ico"
  
  ; Write the installation path into the registry
  WriteRegStr HKLM SOFTWARE\MARCHALBRUEGGE\KurzFiler "Install_Dir" "$INSTDIR"

  ; Write the uninstall keys for Windows
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\KurzFiler" "DisplayName" "KurzFiler"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\KurzFiler" "UninstallString" '"$INSTDIR\kf-uninstall.exe"'

  WriteUninstaller "$INSTDIR\kf-uninstall.exe"

  ; Shell-Kram
  WriteRegStr HKCR ".krz" "" "KurzweilK2x00File"
  WriteRegStr HKCR ".k25" "" "KurzweilK2x00File"
  WriteRegStr HKCR ".k26" "" "KurzweilK2x00File"
  WriteRegStr HKCR ".kvx" "" "KurzweilK2x00File"

  WriteRegStr HKCR "KurzweilK2x00File" "" "Kurzweil K2x00 File"

  WriteRegStr HKCR "KurzweilK2x00File\DefaultIcon" "" "$INSTDIR\k2x00.ico"

  WriteRegStr HKCR "KurzweilK2x00File\shell" "" "open"
  WriteRegStr HKCR "KurzweilK2x00File\shell\open\command" "" '"$INSTDIR\JarRunner" "$INSTDIR\KurzFiler.jar" "%1"'

  ; Startmenü
  CreateDirectory $SMPROGRAMS\KurzFiler
  CreateShortCut $SMPROGRAMS\KurzFiler\KurzFiler.lnk $INSTDIR\KurzFiler.jar "" "$INSTDIR\k2x00.ico"

SectionEnd

;--------------------------------
;Installer Functions

Function .onInit

  !insertmacro MUI_LANGDLL_DISPLAY

FunctionEnd


; ------------------------------------------------------------------------------------
; uninstall stuff


; special uninstall section.
Section "Uninstall"

  ReadRegStr $INSTDIR HKLM "SOFTWARE\MARCHALBRUEGGE\KurzFiler" "Install_Dir"

  Delete $INSTDIR\JarRunner.exe
  Delete $INSTDIR\KurzFiler.jar
  Delete $INSTDIR\.kf-mrulist.dat

  ; remove registry keys
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\KurzFiler"
  ; remove files


  ; MUST REMOVE UNINSTALLER, too

  Delete $INSTDIR\kf-uninstall.exe
  RMdir "$INSTDIR"
  
  ; Registry
  
  DeleteRegKey HKCR ".krz" 
  DeleteRegKey HKCR ".k25" 
  DeleteRegKey HKCR ".k26" 

  DeleteRegKey HKCR "KurzweilK2x00File" 

  ; Startmenü
  
  Delete $SMPROGRAMS\KurzFiler\KurzFiler.lnk
  RMdir $SMPROGRAMS\KurzFiler
  
SectionEnd


;--------------------------------
;Uninstaller Functions

Function un.onInit

  !insertmacro MUI_UNGETLANGUAGE
  
FunctionEnd


; eof
