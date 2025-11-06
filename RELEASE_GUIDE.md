# Release Guide

## Quick Release with Helper Script

### Prerequisites

1. **Install GitHub CLI**:
   ```bash
   # macOS
   brew install gh
   
   # Linux (Debian/Ubuntu)
   sudo apt install gh
   
   # Or download from: https://cli.github.com/
   ```

2. **Authenticate with GitHub**:
   ```bash
   gh auth login
   ```

### Usage

Simply run the helper script:

```bash
./create-release.sh
```

### What the Script Does

1. ✅ Checks for uncommitted changes
2. ✅ Shows the latest version
3. ✅ Offers version bump options:
   - **Patch**: Bug fixes (1.0.0 → 1.0.1)
   - **Minor**: New features (1.0.0 → 1.1.0)
   - **Major**: Breaking changes (1.0.0 → 2.0.0)
   - **Custom**: Specify your own version
4. ✅ Prompts for release notes
5. ✅ Creates the release on GitHub
6. ✅ Automatically triggers the publish workflow

### Example Session

```bash
$ ./create-release.sh

ℹ Current branch: main
ℹ Latest tag: v1.0.6

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Create New Release
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Select version bump type:
  1) Patch (1.0.6 → 1.0.7)
  2) Minor (1.0.6 → 1.1.0)
  3) Major (1.0.6 → 2.0.0)
  4) Custom version

Enter choice (1-4): 1

ℹ New version: v1.0.7

Enter release notes (press Ctrl+D when done):
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
## What's New
- Fixed notification handling
- Improved error messages
- Updated dependencies
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Create release v1.0.7? (y/N): y

ℹ Creating release...

✓ Release v1.0.7 created successfully!

ℹ GitHub Actions will now:
  1. Run tests
  2. Build release AARs
  3. Publish to GitHub Packages

ℹ View the workflow run at:
  https://github.com/Rashidium/blaink-android/actions
```

---

## Manual Release (Without Script)

If you prefer to create releases manually through GitHub UI:

### Steps:

1. **Go to your repository on GitHub**

2. **Click on "Releases"** (right sidebar)

3. **Click "Create a new release"**

4. **Choose or create a tag**:
   - Click "Choose a tag"
   - Type a new tag name (e.g., `v1.0.7`)
   - Click "Create new tag: v1.0.7 on publish"

5. **Fill in release details**:
   - **Release title**: Same as tag (e.g., `v1.0.7`)
   - **Description**: Add release notes

6. **Click "Publish release"**

7. **Monitor the workflow**:
   - Go to "Actions" tab
   - Watch the "Build, Test, and Publish" workflow run

---

## What Happens After Release

Once you create a release (via script or GitHub UI), the workflow automatically:

1. ✅ **Validates** the Gradle wrapper
2. ✅ **Extracts** version from the tag
3. ✅ **Runs** unit tests
4. ✅ **Builds** release AAR files (binary only, no source code)
5. ✅ **Checks** if version already exists
6. ✅ **Deletes** existing packages if needed
7. ✅ **Publishes** to GitHub Packages:
   - `com.blaink:blaink:X.Y.Z`
   - `com.blaink:blaink-core:X.Y.Z`
   - `com.blaink:blaink-push:X.Y.Z`
8. ✅ **Uploads** build reports as artifacts

---

## Published Artifacts

All published artifacts are **compressed binaries only** (no source code):

```gradle
dependencies {
    implementation("com.blaink:blaink:1.0.7")
}
```

### Maven Repository Configuration

Users need to add your GitHub Packages repository:

```gradle
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/Rashidium/blaink-android")
        credentials {
            username = project.findProperty("gpr.user") ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") ?: System.getenv("TOKEN")
        }
    }
}
```

---

## Troubleshooting

### Script Issues

**Problem**: `gh: command not found`
```bash
# Install GitHub CLI
brew install gh  # macOS
```

**Problem**: `You are not authenticated`
```bash
# Authenticate with GitHub
gh auth login
```

**Problem**: Permission denied
```bash
# Make script executable
chmod +x create-release.sh
```

### Workflow Issues

**Problem**: Workflow doesn't trigger
- Make sure you created a **release**, not just a tag
- Check the "Actions" tab for any errors

**Problem**: 409 Conflict error
- The version already exists
- The workflow will automatically delete and republish

**Problem**: Tests failing
- Check the build reports in the workflow artifacts
- Fix the tests and create a new release

---

## Version Naming Convention

Follow semantic versioning (SemVer):

- **v1.0.0** - Initial release
- **v1.0.1** - Patch (bug fixes)
- **v1.1.0** - Minor (new features, backward compatible)
- **v2.0.0** - Major (breaking changes)

Always prefix versions with `v` (e.g., `v1.0.7`).



