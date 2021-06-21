#define   Name       "Personal Department"
#define   Version    "0.0.1"
#define   Publisher  ""
#define   URL        ""
#define   ExeName    "PersonnelDepartment.exe"

[Setup]

PrivilegesRequired=admin

AppId={{D171215D-9B45-465C-8521-5CBDA67EB8F3}
AppName={#Name}
AppVersion={#Version}
AppPublisher={#Publisher}
AppPublisherURL={#URL}
AppSupportURL={#URL}
AppUpdatesURL={#URL}

DefaultDirName={pf}\{#Name}
DefaultGroupName={#Name}

OutputDir=installer\
OutputBaseFileName=installer

SetupIconFile=classes\icon.ico

Compression=lzma
SolidCompression=yes

UsePreviousAppDir=no

[Languages]
Name: "ukrainian"; MessagesFile: "compiler:Languages\Ukrainian.isl";

[Tasks]

Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}";

[Files]

Source: "README.html"; DestDir: "{app}"; Flags: isreadme 
Source: "PersonnelDepartment.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "resources\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]

Name: "{group}\Uninstall"; Filename: "{app}\unins000.exe"
Name: "{group}\Â³הה³כ ךאהנ³ג"; Filename: "{app}\{#ExeName}"

Name: "{commondesktop}\Â³הה³כ ךאהנ³ג"; Filename: "{app}\{#ExeName}"; Tasks: desktopicon

[Run]

Filename: {app}\{#ExeName}; Flags: runascurrentuser nowait postinstall skipifsilent;