#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

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

show_usage() {
    echo "Usage: $0 <version> [release-notes]"
    echo ""
    echo "Arguments:"
    echo "  version        Version number (e.g., 1.0.0 or v1.0.0)"
    echo "  release-notes  Optional release notes (if not provided, will prompt)"
    echo ""
    echo "Examples:"
    echo "  $0 1.0.0"
    echo "  $0 v1.0.0"
    echo "  $0 1.0.0 \"Bug fixes and improvements\""
    echo ""
    exit 1
}

# Check arguments
if [[ "$1" == "-h" ]] || [[ "$1" == "--help" ]] || [[ -z "$1" ]]; then
    show_usage
fi

# Get version from argument
VERSION_ARG="$1"
# Remove 'v' prefix if present
VERSION_ARG="${VERSION_ARG#v}"

# Validate version format
if [[ ! $VERSION_ARG =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    print_error "Invalid version format: $VERSION_ARG"
    echo "Version must be in format X.Y.Z (e.g., 1.2.3)"
    exit 1
fi

NEW_VERSION="$VERSION_ARG"
TAG_NAME="v${NEW_VERSION}"

# Check if gh CLI is installed
if ! command -v gh &> /dev/null; then
    print_error "GitHub CLI (gh) is not installed."
    echo ""
    echo "Install it from: https://cli.github.com/"
    echo "  macOS:   brew install gh"
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
print_info "Creating release: $TAG_NAME"
echo ""

# Get release notes
if [[ -n "$2" ]]; then
    # Use provided release notes
    RELEASE_NOTES="$2"
    print_info "Using provided release notes"
else
    # Prompt for release notes
    echo "Enter release notes (press Ctrl+D when done, or Ctrl+C to cancel):"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    RELEASE_NOTES=$(cat)
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
fi

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
    echo "  1. Build release AAR files"
    echo "  2. Run tests"
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
