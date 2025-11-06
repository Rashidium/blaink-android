# Publishing to Public GitHub Packages

## Overview

You can publish Android packages to GitHub Packages from a **public repository** and they will be **publicly accessible** without authentication.

## ✅ Advantages of Public GitHub Packages

- ✅ **No setup required** - No Sonatype account, no GPG keys
- ✅ **Publicly accessible** - Anyone can download without authentication
- ✅ **Free for public repos** - No cost
- ✅ **Unified repository** - Can host both iOS (XCFramework) and Android (AAR) in the same repo
- ✅ **Simple workflow** - Just create a release, packages publish automatically

## 📦 How It Works

### Public Repository = Public Packages

When your repository is **public**, packages published to GitHub Packages are automatically **public**.

```
Public Repo → Public Packages → No Authentication Needed
```

## 🔧 Setup Options

### Option 1: Use Your iOS Repository (Recommended)

If you already have a public iOS XCFramework repository, you can publish Android packages from the same repo.

#### Structure:
```
your-ios-repo/
├── ios/                    # iOS XCFramework code
├── android/                # Android SDK code
│   ├── blaink/
│   ├── blaink-core/
│   └── blaink-push/
├── .github/
│   └── workflows/
│       ├── publish-ios.yml      # iOS XCFramework workflow
│       └── publish-android.yml  # Android AAR workflow
└── README.md
```

#### Update build.gradle.kts:
```kotlin
publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            // Use your iOS repo URL
            url = uri("https://maven.pkg.github.com/YourUsername/your-ios-repo")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
```

### Option 2: Keep Separate Repositories

Keep Android in its own public repository.

#### Make Repository Public:
1. Go to repository **Settings**
2. Scroll to **Danger Zone**
3. Click **Change visibility**
4. Select **Make public**
5. Confirm

## 📱 Usage (Public Packages)

Once published from a **public repository**, anyone can use your packages:

### For Android Users:

```gradle
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/Rashidium/blaink-android")
        // No credentials needed for public packages!
    }
}

dependencies {
    implementation("com.blaink:blaink:1.0.7")
    implementation("com.blaink:blaink-core:1.0.7")
    implementation("com.blaink:blaink-push:1.0.7")
}
```

### For iOS Users (if using same repo):

```swift
// Package.swift
dependencies: [
    .package(url: "https://github.com/Rashidium/your-repo", from: "1.0.7")
]
```

## 🚀 Publishing Workflow

### Current Setup:

The workflow is already configured to publish to GitHub Packages:

```yaml
- name: Publish to GitHub Packages (Public)
  run: ./gradlew publish
  env:
    GITHUB_ACTOR: ${{ github.actor }}
    GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

### To Publish:

```bash
./create-release.sh
```

That's it! No additional secrets or configuration needed.

## 🔄 Migrating to iOS Repository

If you want to publish Android packages from your iOS repository:

### Step 1: Update Repository URL

In `build.gradle.kts`:

```kotlin
publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            // Change to your iOS repo
            url = uri("https://maven.pkg.github.com/YourUsername/your-ios-repo")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
```

### Step 2: Copy Android Code to iOS Repo

```bash
# In your iOS repo
mkdir -p android
cp -r /path/to/blaink-android/* android/
```

### Step 3: Copy Workflow

Copy `.github/workflows/publish.yml` to your iOS repo:

```bash
cp .github/workflows/publish.yml /path/to/ios-repo/.github/workflows/publish-android.yml
```

### Step 4: Update Workflow Paths (if needed)

If Android code is in a subdirectory, update workflow:

```yaml
- name: Checkout code
  uses: actions/checkout@v4

- name: Set up Android build
  working-directory: ./android  # Add this if code is in subdirectory
  run: chmod +x gradlew
```

## 📊 Comparison: GitHub Packages vs Maven Central

| Feature | GitHub Packages (Public) | Maven Central |
|---------|-------------------------|---------------|
| **Setup Complexity** | ✅ Simple | ❌ Complex |
| **Account Required** | ❌ No | ✅ Yes (Sonatype) |
| **GPG Signing** | ❌ Not required | ✅ Required |
| **Approval Process** | ❌ No | ✅ 1-2 days |
| **Public Access** | ✅ Yes (if repo public) | ✅ Yes |
| **Authentication** | ❌ Not needed (public) | ❌ Not needed |
| **Discovery** | ⚠️ Less discoverable | ✅ Highly discoverable |
| **Cost** | ✅ Free | ✅ Free |
| **Multi-platform** | ✅ Yes (iOS + Android) | ❌ No (JVM only) |

## 🎯 Recommendation

### Use GitHub Packages (Public) If:
- ✅ You want simple setup
- ✅ You have a public repository
- ✅ You want to host iOS and Android in same repo
- ✅ Your users are okay with adding GitHub Packages repository

### Use Maven Central If:
- ✅ You want maximum discoverability
- ✅ You want to be on search.maven.org
- ✅ You're okay with complex setup
- ✅ Android-only SDK

## 🔐 Security Note

Even though packages are public, your **source code remains protected** because:
- ✅ Only compiled binaries (AAR) are published
- ✅ No source JARs included
- ✅ Code is obfuscated in bytecode

## 📝 Current Configuration

Your project is now configured to publish to **GitHub Packages (Public)**:

1. ✅ No GPG signing required
2. ✅ No Sonatype account needed
3. ✅ No additional secrets required
4. ✅ Works with built-in `GITHUB_TOKEN`
5. ✅ Ready to use with public repository

## 🚀 Next Steps

### To Make Packages Public:

1. **Make repository public** (if not already)
   - Settings → Danger Zone → Change visibility → Make public

2. **Create a release**
   ```bash
   ./create-release.sh
   ```

3. **Packages are now public!**
   - Anyone can download without authentication
   - Available at: `https://maven.pkg.github.com/Rashidium/blaink-android`

### To Use Your iOS Repo:

1. Update `build.gradle.kts` with iOS repo URL
2. Copy Android code to iOS repo
3. Copy workflow file
4. Create release from iOS repo

That's it! Your packages will be publicly available on GitHub Packages. 🎉

