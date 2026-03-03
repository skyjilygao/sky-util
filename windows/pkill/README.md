# Windows Port Process Killer (pkill)

## Overview

A Windows command-line tool to simplify finding and terminating processes using specific ports. It wraps `netstat` and `taskkill` commands with an interactive interface for easy process management.

## Usage

### Basic Usage
```
pkill [port_number] [taskkill_parameters]
```

### Examples
```
# Terminate process using port 8080 (force kill by default)
pkill 8080

# Force terminate process using port 3000 with process tree
pkill 3000 /t

# Terminate process and show verbose output
pkill 5432 /v

# Standard force kill (redundant since /f is default)
pkill 3000 /f
```

### Example Output - Single Process
```
Found process for port 8080:
TCP    0.0.0.0:8080    0.0.0.0:0    LISTENING    12345

Terminate this process? (yes/y to confirm, anything else to cancel): y

Terminating process...
SUCCESS: The process with PID 12345 has been terminated.
Process terminated successfully
```

### Example Output - Multiple Processes
```
Found 2 processes for port 3000:

[1] TCP    0.0.0.0:3000    0.0.0.0:0    LISTENING    54321
[2] TCP    127.0.0.1:3000  127.0.0.1:45678 ESTABLISHED  98765

Select process to terminate (1-2): 1

Selected process:
TCP    0.0.0.0:3000    0.0.0.0:0    LISTENING    54321

Terminate this process? (yes/y to confirm, anything else to cancel): y

Terminating process...
Process terminated successfully
```

### Parameters
- **First parameter**: Port number (required)
- **Additional parameters**: Passed directly to `taskkill` command:
  - `/f` - Force termination (used by default if no other parameters specified)
  - `/t` - Terminate process and its child processes
  - `/v` - Show detailed termination information
  - See `taskkill /?` for more options

## Installation

### Method 1: Add to System PATH (Recommended)

1. Copy `pkill.bat` to a fixed location, e.g: `C:\Tools\`
2. Right-click "This PC" or "My Computer", select "Properties"
3. Click "Advanced system settings"
4. Click "Environment Variables"
5. In "System variables" section, find and select "Path" variable, click "Edit"
6. Click "New", then enter the folder path containing `pkill.bat` (e.g: `C:\Tools\`)
7. Click "OK" to save all changes
8. Restart all open command prompt windows

### Method 2: Copy to System Directory

1. Copy `pkill.bat` to `C:\Windows\System32\` directory (requires administrator privileges)

## Workflow

1. Enter `pkill [port_number]` in command prompt
2. The tool searches and displays all matching processes with full network information:
   - **Single result**: Shows detailed connection info automatically
   - **Multiple results**: Lists numbered options for user selection
3. **For multiple results**: User selects process by entering corresponding number
4. **Confirmation prompt**: "Terminate this process? (yes/y to confirm, anything else to cancel)"
5. If confirmed, the tool executes termination and shows results

### Default Behavior
- **Force termination (`/f`)** is automatically added if no additional parameters are provided
- **User-specified parameters** take priority over defaults
- **Smart selection**: Handles single vs multiple process scenarios intelligently

## Notes

- Administrator privileges may be required for some system processes
- Force termination may cause data loss - use carefully
- If multiple processes use the same port, only the first found process will be terminated
- The tool only displays the PID, not the process name, to avoid potential encoding issues

## Technical Details

- Uses `C:\Windows\System32\netstat.exe` to find processes
- Uses full system paths to ensure compatibility
- Handles parameter passing robustly to avoid common batch script issues