# Git Notes

## What/Why Version Control System?

### Solves the Following:
- Code Sharing
- Versioning

### Examples of Version Control System:
- SVN (Centralized Version Control System)
- CVS (Centralized Version Control System)
- GIT (Distributed Version Control System)

### Why Git Became the Most Popular One?
- **Distributed Version Control System**: In distributed systems, everyone has a copy of the project, while in centralized systems, the code is only stored in one place (i.e., the server).

## GIT vs GitHub/Bitbucket/GitLab

- **Git**: A version control system/software that manages communication between the local machine and the repository in the cloud.
- **GitHub/Bitbucket/GitLab**: Companies that use Git to store source code in private/public repositories, with additional features like code reviews.

## Branching Strategy
- **Main**
- **Dev**
- **Feature/event**
- **Feature/videos**
- **Release/1.0**: For testing/staging. Once testing is complete, merge into the main branch.
- **Hotfix/auth-error**
- **Bugfix/auth-error**

## Git Commands

### Check if Git is Installed
```bash
git --version
```

### Configure Git User
```bash
git config --global user.name

git config --global user.email

git config --global user.name phpnoida
git config --global user.email amit@incred.io
```

## Case 1: From Remote to Local Machine

### Clone Repository
```bash
cd location_where_u_want_project_to_be_cloned
git clone url_of_repo  # By default, the master branch code will be copied
ls -a  # Verify the .git file
```

### List Branches
```bash
git branch -a  # List all branches
git branch -r  # List remote branches
git branch     # Show the current branch
```

### Switch Branch
```bash
git switch branch_name
```

### Create Your Own Working Branch
```bash
git branch feature/events  # Code copied from dev
git branch bugfix/events
```

### Check Status and Stage Changes
```bash
git status
git add .  # Move code from working directory to staging
```

### Commit Changes Locally
```bash
git commit -m "proper_message"
```

### Push to Remote
```bash
git push origin feature/events  # Push changes to the remote branch
```

### Merge Changes Once Feature is Complete
```bash
git switch development
git pull origin development  # Get the latest code
git switch feature/events
git rebase development  # Ensure code is working fine with development branch code
git push origin feature/events --force  # Push changes after rebase and raise PR
```

### Difference Between Git Merge and Git Rebase
- **Git Rebase Keeps a Clean History**: Rebasing creates a linear commit history, making it easier to follow what changes were made.

## Case 2: From Local Machine to Remote

### Add Remote
```bash
git remote show origin
git remote add origin url_of_repo
git remote show origin
```

### Initialize Local Git Repository
```bash
git init  # Creates a local git repo
```

### Follow Normal Steps
- Add, commit, and push changes as described above.

## Logs
```bash
git log
git log feature/events
```

## Undo Commit

### Undo Commit on Remote
```bash
git log
git revert commitId
git revert -m 1 commitId  # For merge commits
git push origin main
```

### Undo Commit on Local
```bash
git reset --hard commitId
git push origin feature/events --force

### Difference
git diff                # View unstaged changes
git diff commit1 commit2  # Compare two commits

### Stash
git stash
git switch main
git switch feature/events
git pop

### Deleting branch
git branch -d branch_name         # Delete a local branch (safe delete)
git branch -D branch_name         # Force delete a local branch
git push origin --delete branch_name  # Delete a remote branch

