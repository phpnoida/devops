# Git Office Simulation — Hands-On Practice

> **How to use this file**
> Each module simulates a real day/situation at a company.
> Read the scenario, follow the instructions step by step, and do it in your terminal.
> Do NOT copy-paste blindly — type commands to build muscle memory.
> Expected output is shown after each command so you can verify you're on track.

---

## Setup — Before You Begin

You need two things:
1. A GitHub account
2. Git installed locally

Create a fresh repo on GitHub named `evnt-platform` (make it public or private, your choice).
This will be your fake company project throughout all modules.

---

## Module 1 — First Day at the Company (Setup & Clone)

**Scenario**: You just joined a company. Your lead has given you access to the repo.
Your first task is to set up your machine and clone the project.

**Step 1: Configure your identity (one-time)**
```bash
git config --global user.name "Your Name"
git config --global user.email "you@example.com"
git config --global core.editor "nano"          # or vim/code
git config --global --list
```
Expected: your name, email, and editor printed back.

**Step 2: Set up SSH key so you never type password again**
```bash
ssh-keygen -t ed25519 -C "you@example.com"     # press Enter for all prompts
cat ~/.ssh/id_ed25519.pub                       # copy this output
```
Go to GitHub → Settings → SSH and GPG keys → New SSH key → paste it.

Test it:
```bash
ssh -T git@github.com
# Expected: Hi <username>! You've successfully authenticated...
```

**Step 3: Clone and explore**
```bash
cd ~/projects
git clone git@github.com:<your-username>/evnt-platform.git
cd evnt-platform
git log --oneline                               # see commit history
git branch -a                                   # see all branches
git remote -v                                   # see remote URL
```

**Step 4: Create the base branch structure (simulating what DevOps already set up)**
```bash
# create dev and staging branches
git switch -c dev
git push -u origin dev

git switch -c staging
git push -u origin staging

git switch main
```

> Checkpoint: You should now have `main`, `dev`, `staging` on GitHub.

---

## Module 2 — Storing Credentials Securely (Never store raw passwords)

**Scenario**: Your lead says "never hardcode tokens or passwords in code".

**Step 1: See what Git Credential Manager offers**
```bash
git config --global credential.helper
# if empty, set it:
git config --global credential.helper store        # stores in ~/.git-credentials (plain text, ok for personal)
# OR (better on Linux):
git config --global credential.helper cache        # keeps in memory for 15 min
git config --global credential.helper 'cache --timeout=3600'   # 1 hour
```

**Step 2: Using a Personal Access Token (PAT) instead of password**

On GitHub → Settings → Developer Settings → Personal Access Tokens → Tokens (classic) → Generate new token.
Select scopes: `repo`, `workflow`. Copy the token.

```bash
# when git asks for password, paste the token — it gets cached
git clone https://github.com/<your-username>/evnt-platform.git evnt-platform-https
# Username: your-github-username
# Password: <paste token here>
```

**Step 3: Store token in environment variable (the RIGHT way for scripts)**
```bash
export GITHUB_TOKEN="ghp_xxxxxxxxxxxxxxxxxxxx"    # paste your token
echo $GITHUB_TOKEN                                 # verify it's set

# Use in curl/API calls:
curl -H "Authorization: token $GITHUB_TOKEN" https://api.github.com/user
# Expected: JSON with your GitHub profile
```

> **Rule**: Never commit a `.env` file with real tokens. Add `.env` to `.gitignore` always.

```bash
echo ".env" >> .gitignore
echo "*.pem" >> .gitignore
echo "*.key" >> .gitignore
git add .gitignore
git commit -m "chore: add sensitive files to gitignore"
git push origin main
```

---

## Module 3 — Daily Feature Work (The Most Common Workflow)

**Scenario**: You got a Jira ticket: `EVNT-101 — Add README with project overview`.
This is your first real task.

**Step 1: Always start from latest dev**
```bash
git switch dev
git pull origin dev
git log --oneline -5                              # confirm you're up to date
```

**Step 2: Create your feature branch**
```bash
git switch -c feature/EVNT-101-add-readme
git branch                                        # confirm you're on the new branch
```

**Step 3: Do your work**
```bash
cat > README.md << 'EOF'
# EVNT Platform

Event management platform for enterprise teams.

## Tech Stack
- Backend: Node.js
- Database: PostgreSQL
- Infrastructure: AWS + Kubernetes

## Getting Started
Clone the repo and run `npm install`.
EOF
```

**Step 4: Stage, review, commit**
```bash
git status                                        # see what changed
git diff                                          # review changes before staging
git add README.md
git diff --staged                                 # review what is staged
git commit -m "EVNT-101: Add README with project overview"
git log --oneline -3                              # confirm commit is there
```

**Step 5: Push branch to remote**
```bash
git push -u origin feature/EVNT-101-add-readme
```

**Step 6: Open Pull Request on GitHub**

Go to GitHub → your repo → you'll see "Compare & pull request" banner.
- Base: `dev`
- Compare: `feature/EVNT-101-add-readme`
- Title: `EVNT-101: Add README with project overview`
- Description: what you did and why

Click "Create pull request".

**Step 7: Merge and clean up**
```bash
# after PR is merged on GitHub:
git switch dev
git pull origin dev                               # get the merged changes
git branch -d feature/EVNT-101-add-readme         # delete local branch
git fetch -p                                      # prune deleted remote branch
git branch -a                                     # confirm it's gone
```

---

## Module 4 — Handling Review Feedback (PR revision cycle)

**Scenario**: Your PR for `EVNT-102` got a review comment:
*"Please also add a CONTRIBUTING.md file explaining the branch naming convention."*

```bash
git switch feature/EVNT-102-add-docs             # go back to your PR branch
# (if you deleted it, recreate from the PR branch on origin)
# git switch -c feature/EVNT-102-add-docs origin/feature/EVNT-102-add-docs

cat > CONTRIBUTING.md << 'EOF'
# Contributing Guide

## Branch Naming
- feature/<TICKET>-<short-desc>    e.g. feature/EVNT-123-add-login
- bugfix/<TICKET>-<short-desc>     e.g. bugfix/EVNT-456-fix-crash
- hotfix/<TICKET>-<short-desc>     e.g. hotfix/EVNT-789-payment-null

## Commit Messages
Follow Conventional Commits: feat:, fix:, chore:, docs:, refactor:

## PRs
All PRs go into `dev`. Never push directly to main or staging.
EOF

git add CONTRIBUTING.md
git commit -m "EVNT-102: Add CONTRIBUTING.md per review feedback"
git push origin feature/EVNT-102-add-docs         # push update — PR auto-updates
```

---

## Module 5 — Squashing WIP Commits Before PR

**Scenario**: You worked on `EVNT-103` across 2 days and made a mess of commits.
Before raising the PR, clean up history.

**Step 1: Simulate messy commits**
```bash
git switch dev && git pull origin dev
git switch -c feature/EVNT-103-user-auth

echo "# auth module" > auth.js && git add . && git commit -m "wip"
echo "import jwt from 'jwt'" >> auth.js && git add . && git commit -m "added import"
echo "function login() {}" >> auth.js && git add . && git commit -m "fix typo"
echo "function logout() {}" >> auth.js && git add . && git commit -m "almost done"
echo "// done" >> auth.js && git add . && git commit -m "EVNT-103: Add auth module"

git log --oneline                                 # you'll see 5 ugly commits
```

**Step 2: Squash into one clean commit**
```bash
git rebase -i HEAD~5
# Your editor opens. You'll see 5 lines starting with "pick".
# Change lines 2,3,4,5 from "pick" to "s" (squash):
#
# pick abc1234 wip
# s    def5678 added import
# s    ghi9012 fix typo
# s    jkl3456 almost done
# s    mno7890 EVNT-103: Add auth module
#
# Save and close. Another editor opens for the combined commit message.
# Delete all the WIP messages, keep only:
# EVNT-103: Add authentication module with login and logout
# Save and close.

git log --oneline                                 # now only 1 clean commit
git push -u origin feature/EVNT-103-user-auth     # push clean branch
```

---

## Module 6 — Merge Conflict (The Most Common Pain Point)

**Scenario**: You and a colleague both edited `config.js`. Their PR merged first.
Now your branch has a conflict with `dev`.

**Step 1: Simulate the conflict**
```bash
# Simulate what your colleague pushed to dev
git switch dev
echo "const DB_HOST = 'prod-db.company.com';" > config.js
git add . && git commit -m "EVNT-200: Set production DB host"
git push origin dev

# Now simulate what you did on your branch (same file, different line)
git switch -c feature/EVNT-201-add-api-key
echo "const API_KEY = 'key-abc-123';" >> config.js
echo "const DB_HOST = 'localhost';" >> config.js   # CONFLICT — you both touched this
git add . && git commit -m "EVNT-201: Add API key config"
```

**Step 2: Rebase onto latest dev (conflict appears)**
```bash
git rebase dev
# Git stops and says: CONFLICT (content): Merge conflict in config.js
```

**Step 3: Open and understand the conflict markers**
```bash
cat config.js
# You'll see:
# <<<<<<< HEAD  (this is dev's version)
# const DB_HOST = 'prod-db.company.com';
# =======
# const API_KEY = 'key-abc-123';
# const DB_HOST = 'localhost';
# >>>>>>> EVNT-201: Add API key config  (this is your version)
```

**Step 4: Resolve — keep BOTH changes correctly**
```bash
nano config.js
# Edit the file to be the correct final version:
# const DB_HOST = 'prod-db.company.com';   ← keep colleague's correct value
# const API_KEY = 'key-abc-123';           ← keep your addition
# (remove ALL <<<, ===, >>> markers completely)
```

**Step 5: Mark resolved and continue**
```bash
git add config.js
git rebase --continue
# Git may open editor for commit message — keep it or update it, then save

git log --oneline                               # confirm clean history
git push -u origin feature/EVNT-201-add-api-key
```

> **Rule**: always resolve conflicts carefully. Read both sides before deciding what to keep.
> When unsure, talk to the colleague who wrote the conflicting code.

---

## Module 7 — Stashing (Context Switch Mid-Work)

**Scenario**: You're in the middle of coding `EVNT-205` when your lead messages:
*"Can you quickly check something on the staging branch? Don't commit yet."*

```bash
# You have uncommitted changes
echo "half written code here" >> service.js
git status                                        # shows modified file

# You can't switch branches with dirty working tree — stash it
git stash                                         # saves changes, cleans working tree
git status                                        # now clean

# Do your lead's task on staging
git switch staging
git log --oneline -5                              # look around

# Come back and restore your work
git switch feature/EVNT-205-add-service
git stash pop                                     # restores your changes
git status                                        # back to where you were
cat service.js                                    # your code is back
```

**Managing multiple stashes**
```bash
git stash list                                    # see all stashes
# stash@{0}: WIP on feature/EVNT-205: half done service
# stash@{1}: WIP on feature/EVNT-190: old stash

git stash pop stash@{1}                           # restore a specific stash
git stash drop stash@{0}                          # delete a stash you don't need
git stash clear                                   # delete ALL stashes
```

---

## Module 8 — Reverting a Bad Commit (Safe Undo in Production)

**Scenario**: A commit was merged to `main` that broke the login page.
You need to undo it without rewriting history (since others may have pulled).

**Step 1: Find the bad commit**
```bash
git switch main && git pull origin main
git log --oneline
# a3f9c12 EVNT-301: Update login page styling   ← this one broke it
# b71d004 EVNT-298: Add footer links
# c92ee11 EVNT-295: Release v2.1.0
```

**Step 2: Revert (creates a new commit that undoes it)**
```bash
git revert a3f9c12
# Editor opens with a default message like:
# Revert "EVNT-301: Update login page styling"
# Keep it or add more context, then save.

git log --oneline
# d44cc90 Revert "EVNT-301: Update login page styling"   ← new commit
# a3f9c12 EVNT-301: Update login page styling
# b71d004 EVNT-298: Add footer links
```

**Step 3: Push (raise PR if main is protected)**
```bash
git push origin main
# If main is protected: push to a branch and raise PR
git switch -c revert/EVNT-301-undo-login-styling
git push -u origin revert/EVNT-301-undo-login-styling
# Open PR: base=main, compare=revert/EVNT-301-undo-login-styling
```

> **Why revert and not reset?**
> `git reset --hard` rewrites history. If someone already pulled `a3f9c12`, their history
> diverges and you create chaos. `git revert` is always safe on shared branches.

---

## Module 9 — Hotfix (Production is Down, Fix NOW)

**Scenario**: 2am. Payments are failing in production. The bug was in the last release.
You must fix `main` directly and deploy within 20 minutes.

```bash
# 1. Start from production (main), not dev or staging
git switch main && git pull origin main

# 2. Create hotfix branch
git switch -c hotfix/EVNT-999-fix-payment-null-crash

# 3. Implement the fix
nano payment.js
# Fix the null check that caused the crash

git add payment.js
git commit -m "hotfix: EVNT-999 fix null pointer in payment processor"

# 4. Push and raise emergency PR to main
git push -u origin hotfix/EVNT-999-fix-payment-null-crash
# Open PR: base=main (get emergency approval from on-call lead)

# 5. After merge: tag the hotfix release
git switch main && git pull origin main
git tag -a v2.1.1 -m "Hotfix v2.1.1: fix payment null crash"
git push origin v2.1.1

# 6. Cherry-pick the fix into dev so it's not lost in next release
git switch dev && git pull origin dev
git log --oneline main -3                         # find the hotfix commit hash
git cherry-pick <hotfix-commit-hash>
git push origin dev
```

---

## Module 10 — Cherry-pick (Apply One Commit to Another Branch)

**Scenario**: A fix on `dev` needs to go to `staging` without merging all of `dev`.

```bash
# Find the specific commit on dev
git switch dev && git pull origin dev
git log --oneline -10
# 7f3a1bc EVNT-412: Fix email validation regex   ← only this one needed on staging

# Apply it to staging
git switch staging && git pull origin staging
git cherry-pick 7f3a1bc

# If conflict:
# nano <conflicted-file>   → resolve
# git add <file>
# git cherry-pick --continue

git push origin staging
git log --oneline staging -3                      # confirm the commit is there
```

---

## Module 11 — Git Bisect (Find Which Commit Broke the Build)

**Scenario**: Tests started failing sometime this week. 40 commits since last green build.
You need to find the exact bad commit.

```bash
git switch dev && git pull origin dev

# Start bisect
git bisect start
git bisect bad                                    # current HEAD is broken
git bisect good v2.0.0                            # this tag was known-good

# Git checks out a middle commit — test your app
npm test
# If tests PASS:
git bisect good
# If tests FAIL:
git bisect bad

# Repeat until Git prints:
# "abc1234 is the first bad commit"
# commit abc1234
# Author: ...
#     EVNT-388: Refactor database connection pool

# Exit bisect
git bisect reset                                  # returns to your original branch

# Now you know the culprit commit — revert it or fix the root cause
git revert abc1234
```

**Automated bisect (when you have a test script)**
```bash
git bisect start
git bisect bad
git bisect good v2.0.0
git bisect run npm test                           # runs automatically — no manual testing needed
git bisect reset
```

---

## Module 12 — Syncing Branches After Release (Back-merge)

**Scenario**: `main` was released. Now `dev` is behind — it doesn't have the hotfix
and release commits that went directly to `main`. This must be synced.

```bash
git switch dev && git pull origin dev
git log --oneline dev..main                       # see commits on main that dev doesn't have

git merge origin/main                             # bring them in
# if conflicts: resolve, then git add + git commit

git push origin dev

# Verify
git log --oneline dev..main                       # should be empty now
```

---

## Module 13 — Working with Tags (Releases)

```bash
# List all tags
git tag

# Create annotated tag (always use annotated in orgs — has author + message)
git tag -a v2.2.0 -m "Release v2.2.0 — User auth module"

# Tag a specific old commit (useful if you forgot to tag at release time)
git log --oneline
git tag -a v2.1.5 b71d004 -m "Backfill tag for v2.1.5 release"

# Push a single tag
git push origin v2.2.0

# Push all tags at once
git push origin --tags

# Delete a tag (locally and remotely)
git tag -d v2.2.0
git push origin --delete v2.2.0

# Checkout code at a specific tag (read-only look)
git checkout v2.1.0
git switch -                                      # go back to where you were
```

---

## Module 14 — Cleaning Up a Tracked File from History

**Scenario**: Someone accidentally committed a `.env` file with real credentials.
It must be removed from ALL history, not just the latest commit.

> **Warning**: this rewrites history. Coordinate with your team before doing this.
> Everyone must re-clone after this operation.

```bash
# Step 1: Add to .gitignore FIRST so it never happens again
echo ".env" >> .gitignore
git add .gitignore && git commit -m "chore: add .env to gitignore"

# Step 2: Remove from git tracking (file stays on disk)
git rm --cached .env
git commit -m "chore: remove .env from tracking"
git push origin main

# Step 3: If you need to purge from ALL history (nuclear option):
# Use git-filter-repo (modern, recommended over filter-branch)
pip install git-filter-repo
git filter-repo --path .env --invert-paths

# Force push all branches
git push origin --force --all
git push origin --force --tags

# Immediately rotate ALL credentials that were in that file
```

---

## Module 15 — Full Sprint Simulation (All Modules Together)

This is your final challenge. Do this from scratch without looking at other modules.

**Story**: It's Monday morning. Sprint starts today.

1. Pull latest `dev`
2. Pick up ticket `EVNT-500: Add health check endpoint`
3. Create proper feature branch
4. Write a file `healthcheck.js` with content `module.exports = () => 'ok';`
5. Make 3 messy WIP commits
6. Squash them into 1 clean commit
7. Rebase onto latest `dev` (no conflicts expected this time)
8. Push and open PR to `dev`
9. Simulate review: add a comment to the file and push another commit
10. Merge the PR on GitHub
11. Back on terminal: switch to `dev`, pull, delete local branch, prune
12. Tag `dev` as `sprint-23-complete`
13. Push the tag

If you can do all 15 steps from memory, you are ready for a real company environment.

---

## Quick Reference Card

| Situation | Command |
|-----------|---------|
| Start new feature | `git switch dev && git pull && git switch -c feature/TICKET-desc` |
| Save work temporarily | `git stash` / `git stash pop` |
| Clean up WIP commits | `git rebase -i HEAD~N` |
| Sync with latest dev | `git rebase dev` |
| Undo a commit safely | `git revert <hash>` |
| Apply one commit elsewhere | `git cherry-pick <hash>` |
| Find the bad commit | `git bisect start/bad/good/reset` |
| Emergency production fix | branch from `main` → fix → PR to `main` → tag → cherry-pick to dev |
| See pretty history | `git log --oneline --graph --decorate --all` |
| Clean up after merge | `git branch -d <branch> && git fetch -p` |
