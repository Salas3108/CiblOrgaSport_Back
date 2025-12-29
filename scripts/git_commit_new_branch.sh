#!/usr/bin/env bash
set -euo pipefail

# Usage: ./scripts/git_commit_new_branch.sh <branch-name> "<commit-message>"
# Example: ./scripts/git_commit_new_branch.sh feature/compose-update "Update docker-compose for healthchecks"

if [ $# -lt 2 ]; then
  echo "Usage: $0 <branch-name> \"<commit-message>\""
  exit 1
fi

BRANCH_NAME="$1"
COMMIT_MSG="$2"

# Ensure we're in repo root
cd "$(dirname "$0")/.."

# Check for git repo
if [ ! -d .git ]; then
  echo "Error: Not a git repository. Initialize with 'git init' and add remote first."
  exit 1
fi

# Show status
git status

# Create and switch to new branch (or switch if it exists)
if git rev-parse --verify "$BRANCH_NAME" >/dev/null 2>&1; then
  git checkout "$BRANCH_NAME"
else
  git checkout -b "$BRANCH_NAME"
fi

# Stage all changes
git add -A

# Commit
git commit -m "$COMMIT_MSG"

# Push and set upstream
git push -u origin "$BRANCH_NAME"

echo "Pushed branch '$BRANCH_NAME' with commit: $COMMIT_MSG"
