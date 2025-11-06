#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_info() {
    echo -e "${BLUE}ℹ${NC} $1"
}

print_success() {
    echo -e "${GREEN}✓${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

# Function to show usage
show_usage() {
    echo "Usage: $0 [version]"
    echo ""
    echo "Examples:"
    echo "  $0              # Interactive mode - choose version bump type"
    echo "  $0 1.2.3        # Create release v1.2.3"
    echo "  $0 v1.2.3       # Create release v1.2.3 (v prefix optional)"
    echo ""
    exit 1
}

# Function to get the latest tag
get_latest_tag() {
    git tag -l | grep -E '^v?[0-9]+\.[0-9]+\.[0-9]+$' | sed 's/^v//' | sort -V | tail -1
}

# Function to increment version
increment_version() {
    local version=$1
    local type=$2
    
    if [[ $version =~ ^([0-9]+)\.([0-9]+)\.([0-9]+)$ ]]; then
        local major=${BASH_REMATCH[1]}
        local minor=${BASH_REMATCH[2]}
        local patch=${BASH_REMATCH[3]}
        
        case $type in
            major)
                echo "$((major + 1)).0.0"
                ;;
            minor)
                echo "${major}.$((minor + 1)).0"
                ;;
            patch)
                echo "${major}.${minor}.$((patch + 1))"
                ;;
            *)
                echo "$version"
                ;;
        esac
    else
        echo "1.0.0"
    fi
}

# Parse command line arguments
if [[ "$1" == "-h" ]] || [[ "$1" == "--help" ]]; then
    show_usage
fi

# Check if version is provided as argument
VERSION_ARG=""
if [[ -n "$1" ]]; then
    VERSION_ARG="$1"
    # Remove 'v' prefix if present
    VERSION_ARG="${VERSION_ARG#v}"
    
    # Validate version format
    if [[ ! $VERSION_ARG =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
        print_error "Invalid version format: $VERSION_ARG"
        echo "Version must be in format X.Y.Z (e.g., 1.2.3)"
        exit 1
    fi
fi

# Check if gh CLI is installed
if ! command -v gh &> /dev/null; then
    print_error "GitHub CLI (gh) is not installed."
    echo ""
    echo "Please install it from: https://cli.github.com/"
    echo ""
    echo "Installation:"
    echo "  macOS:   brew install gh"
    echo "  Linux:   See https://github.com/cli/cli/blob/trunk/docs/install_linux.md"
    echo "  Windows: See https://github.com/cli/cli#windows"
    exit 1
fi

# Check if user is authenticated
if ! gh auth status &> /dev/null; then
    print_error "You are not authenticated with GitHub CLI."
    echo ""
    echo "Please run: gh auth login"
    exit 1
fi

# Check if we're in a git repository
if ! git rev-parse --git-dir > /dev/null 2>&1; then
    print_error "Not in a git repository!"
    exit 1
fi

# Check if there are uncommitted changes
if [[ -n $(git status -s) ]]; then
    print_warning "You have uncommitted changes:"
    git status -s
    echo ""
    read -p "Do you want to continue anyway? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        print_info "Aborted."
        exit 0
    fi
fi

# Get current branch
CURRENT_BRANCH=$(git branch --show-current)
print_info "Current branch: $CURRENT_BRANCH"

# Get latest tag
LATEST_TAG=$(get_latest_tag)
if [[ -z "$LATEST_TAG" ]]; then
    LATEST_TAG="0.0.0"
    print_warning "No existing tags found. Starting from v0.0.0"
else
    print_info "Latest tag: v$LATEST_TAG"
fi

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "  Create New Release"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# Use provided version or ask for version bump type
if [[ -n "$VERSION_ARG" ]]; then
    NEW_VERSION="$VERSION_ARG"
    print_info "Using provided version: $NEW_VERSION"
else
    # Ask for version bump type
    echo "Select version bump type:"
    echo "  1) Patch (${LATEST_TAG} → $(increment_version $LATEST_TAG patch))"
    echo "  2) Minor (${LATEST_TAG} → $(increment_version $LATEST_TAG minor))"
    echo "  3) Major (${LATEST_TAG} → $(increment_version $LATEST_TAG major))"
    echo "  4) Custom version"
    echo ""
    read -p "Enter choice (1-4): " choice

    case $choice in
        1)
            NEW_VERSION=$(increment_version $LATEST_TAG patch)
            ;;
        2)
            NEW_VERSION=$(increment_version $LATEST_TAG minor)
            ;;
        3)
            NEW_VERSION=$(increment_version $LATEST_TAG major)
            ;;
        4)
            read -p "Enter custom version (e.g., 1.2.3): " NEW_VERSION
            # Validate version format
            if [[ ! $NEW_VERSION =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
                print_error "Invalid version format. Must be X.Y.Z (e.g., 1.2.3)"
                exit 1
            fi
            ;;
        *)
            print_error "Invalid choice"
            exit 1
            ;;
    esac
fi

TAG_NAME="v${NEW_VERSION}"

echo ""
print_info "New version: $TAG_NAME"
echo ""

# Ask for release notes
echo "Enter release notes (press Ctrl+D when done, or Ctrl+C to cancel):"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
RELEASE_NOTES=$(cat)
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# If no release notes provided, use a default message
if [[ -z "$RELEASE_NOTES" ]]; then
    RELEASE_NOTES="Release $TAG_NAME"
fi

echo ""
print_info "Release notes:"
echo "$RELEASE_NOTES"
echo ""

# Confirm
read -p "Create release $TAG_NAME? (y/N): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    print_info "Aborted."
    exit 0
fi

echo ""
print_info "Creating release..."

# Create the release using GitHub CLI
if gh release create "$TAG_NAME" \
    --title "$TAG_NAME" \
    --notes "$RELEASE_NOTES" \
    --target "$CURRENT_BRANCH"; then
    
    echo ""
    print_success "Release $TAG_NAME created successfully!"
    echo ""
    print_info "GitHub Actions will now:"
    echo "  1. Run tests"
    echo "  2. Build release AARs"
    echo "  3. Publish to GitHub Packages"
    echo ""
    print_info "View the workflow run at:"
    REPO_URL=$(git config --get remote.origin.url | sed 's/\.git$//' | sed 's/git@github.com:/https:\/\/github.com\//')
    echo "  ${REPO_URL}/actions"
    echo ""
else
    echo ""
    print_error "Failed to create release!"
    exit 1
fi


