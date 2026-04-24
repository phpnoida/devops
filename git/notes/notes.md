# Git Workflow — Sequential Steps (CLI + PR Flow)

> Designed for teams with protected `main`/`staging`/`dev`. Use PRs for shared branches. Commands show inline comments.

---

### Step 0 — Verify Git & Configure Identity (one‑time)

```bash
git --version                                              # check installed version
git config --global user.name "Your Name"                 # set author name
git config --global user.email "you@example.com"          # set email for commits
git config --global --list                                 # verify settings
# Tip: use SSH keys or Git Credential Manager; avoid storing raw tokens.
```

### Step 0A — Store Credentials Securely (one-time setup)

> **Rule**: never type your password or paste a raw token into the terminal on every push.
> Store credentials once in the OS keychain — Git reads from there silently from that point on.
>
> Way 1 and Way 2 are both HTTPS-based. Way 2 is just the automated version of Way 1.
> **If your company uses HTTPS → go with Way 2. If SSH → go with Way 3.**

---

#### Way 1 — OS Keychain manually *(HTTPS — understand what is happening under the hood)*

> The OS keychain is a secure encrypted vault built into your OS.
> Git's credential helper is a plugin that saves your token there once,
> then reads it silently on every future push/pull. You never type credentials again.

**Before you start: generate a PAT on GitHub (needed for both macOS and Linux)**

```
1. Open browser → go to github.com → log in
2. Top-right corner → click your profile photo → Settings
3. Left sidebar → scroll to the bottom → Developer settings
4. Left sidebar → Personal access tokens → Tokens (classic)
5. Click "Generate new token" → "Generate new token (classic)"
6. Fill in:
     Note:       my-laptop-git                (any label you want)
     Expiration: 90 days  (or No expiration for personal use)
     Scopes:     ✓ repo   (check the top-level repo checkbox — selects all sub-items)
                 ✓ workflow
7. Scroll to bottom → click "Generate token"
8. COPY the token immediately — it starts with ghp_
   Example: ghp_ABcDeFgHiJkLmNoPqRsTuVwXyZ123456
   GitHub will NEVER show it again. Save it in a notepad temporarily.
```

---

**macOS — configure keychain helper and trigger first-time save**

```bash
# Step 1: Tell git to use macOS keychain
git config --global credential.helper osxkeychain

# Step 2: Verify it is set
git config --global credential.helper
# Expected output: osxkeychain

# Step 3: Trigger the first-time credential prompt by doing any push/pull
# Go to any repo you cloned via HTTPS and push, OR run this test command:
git ls-remote https://github.com/your-username/your-repo.git
# Git will prompt:
#   Username for 'https://github.com': your-github-username     ← type your GitHub username
#   Password for 'https://...':        ghp_ABcDeFgHi...         ← paste the PAT here
#
# macOS saves it to Keychain automatically. Next time: no prompt at all.

# Step 4: Verify it is stored in macOS Keychain
# Option A — GUI: Spotlight (Cmd+Space) → type "Keychain Access" → search "github.com"
#            You will see an entry: "github.com" with your username
# Option B — Terminal:
security find-internet-password -s github.com
# Output shows: acct (your username), svce (github.com), stored password = your PAT

# To delete/replace a stored token (e.g. PAT expired, generate a new one):
git credential-osxkeychain erase <<EOF
protocol=https
host=github.com
EOF
# Then push again — Git will prompt for new credentials → paste new PAT
```

---

**Linux — configure libsecret helper and trigger first-time save**

```bash
# Step 1: Install libsecret (the keychain backend)
sudo apt install libsecret-1-0 libsecret-1-dev          # Ubuntu/Debian
sudo dnf install libsecret-devel                         # Fedora/RHEL

# Step 2: Check if the git-credential-libsecret helper already exists
ls /usr/lib/git-core/git-credential-libsecret
# If file exists → go to Step 4
# If not found  → build it in Step 3

# Step 3: Build the helper (only if Step 2 showed file not found)
sudo apt install gcc make                                # build tools
sudo make --directory=/usr/share/doc/git/contrib/credential/gnome-keyring
# This compiles the helper binary

# Step 4: Configure git to use libsecret
git config --global credential.helper /usr/lib/git-core/git-credential-libsecret

# Step 5: Verify
git config --global credential.helper
# Expected: /usr/lib/git-core/git-credential-libsecret

# Step 6: Trigger first-time credential save
git ls-remote https://github.com/your-username/your-repo.git
# Git prompts:
#   Username for 'https://github.com': your-github-username
#   Password for 'https://...':        ghp_ABcDeFgHi...    ← paste PAT here
# Linux saves it to GNOME Keyring. All future git operations: no prompt.

# Step 7: Verify it is stored
secret-tool lookup server github.com
# Shows the stored token

# To delete (e.g. PAT expired):
secret-tool clear server github.com
# Then trigger Step 6 again with new PAT
```

---

#### Way 2 — `gh auth login` *(HTTPS, automated — use this day-to-day)*

> `gh` is GitHub's official CLI tool. `gh auth login` does everything in Way 1 automatically —
> it creates the PAT for you, configures the credential helper, and stores the token
> in the OS keychain. You do not need to touch github.com settings manually.
> **This is what most developers at companies actually use.**

**Step 1: Install gh CLI**
```bash
# macOS
brew install gh

# Ubuntu/Debian
sudo apt install gh

# Fedora
sudo dnf install gh

# Verify install
gh --version
# Expected: gh version 2.x.x
```

**Step 2: Run `gh auth login` and answer the prompts**
```bash
gh auth login
```
```
? Where do you use GitHub?
  ▸ GitHub.com          ← select this (not GitHub Enterprise unless your company uses it)
    GitHub Enterprise Server

? What is your preferred protocol for Git operations on this host?
  ▸ HTTPS               ← select HTTPS (Way 2 is HTTPS-based)
    SSH

? Authenticate Git with your GitHub credentials?
  ▸ Yes                 ← select Yes (this is the keychain config step from Way 1, done automatically)

? How would you like to authenticate GitHub CLI?
  ▸ Login with a web browser    ← easiest — opens browser, you log in, done
    Paste an authentication token

# If you chose "Login with a web browser":
# Terminal prints: First copy your one-time code: ABCD-1234
# Browser opens automatically → paste the code → authorize → done

# If you chose "Paste an authentication token":
# Go to github.com → Settings → Developer settings → Personal access tokens
# Generate a new token with scopes: repo, workflow, read:org
# Paste it here → Enter
```

**Step 3: Verify everything is set up**
```bash
gh auth status
# Expected output:
# github.com
#   ✓ Logged in to github.com account your-username (keyring)
#   ✓ Active account: true
#   ✓ Git operations for github.com configured to use https protocol
#   ✓ Token: gho_xxxxxxxxxxxx (stored in system keychain)
#   ✓ Token scopes: gist, read:org, repo, workflow
```

**Step 4: Test — clone a repo to confirm credentials work**
```bash
gh repo clone your-username/your-repo
# OR
git clone https://github.com/your-username/your-repo.git
# Neither should ask for username or password — credentials come from keychain silently
```

**Token management**
```bash
gh auth refresh                    # generate a fresh token and update keychain (use when token expires)
gh auth logout                     # remove all stored credentials for github.com
gh auth token                      # print the currently stored token (useful for scripts)
```

---

#### Way 3 — SSH Keys *(no token at all — key-based authentication)*

> SSH uses a key pair: a private key (stays on your machine, never shared) and a public key
> (uploaded to GitHub). No username, no token, no password — Git just uses the key silently.
> Companies with strict security policies often require SSH.

**Step 1: Generate an SSH key pair**
```bash
ssh-keygen -t ed25519 -C "you@example.com"
# Prompts:
# Enter file to save key: press Enter  (saves to ~/.ssh/id_ed25519)
# Enter passphrase: press Enter        (no passphrase = no prompt on every use)
#                                       (set one if your org requires it)

ls ~/.ssh/
# id_ed25519        ← private key — NEVER share or commit this
# id_ed25519.pub    ← public key  — this goes to GitHub
```

**Step 2: Add public key to GitHub**
```bash
# Print your public key
cat ~/.ssh/id_ed25519.pub
# Output: ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAA... you@example.com
# Select and copy the entire line (from ssh-ed25519 to your email)
```
```
1. Open browser → github.com → log in
2. Top-right profile photo → Settings
3. Left sidebar → SSH and GPG keys
4. Click "New SSH key"
5. Fill in:
     Title: my-laptop   (any label — helps identify which machine this key belongs to)
     Key type: Authentication Key  (default, leave as is)
     Key: paste the copied line here
6. Click "Add SSH key" → confirm with your GitHub password if prompted
```

**Step 3: Make key loading automatic (the pro way — do this once)**

> Without this step, you must run `ssh-add` every time you open a new terminal.
> The `~/.ssh/config` file tells SSH to load your key automatically — no manual steps ever again.

```bash
# Create or open the SSH config file
nano ~/.ssh/config
```

Paste this inside (read the comments — one line is macOS only):
```
Host github.com
  AddKeysToAgent yes
  UseKeychain yes
  IdentityFile ~/.ssh/id_ed25519
```

> **Line by line:**
> `AddKeysToAgent yes` — automatically adds the key to ssh-agent when first used (macOS + Linux)
> `UseKeychain yes`    — stores the passphrase in macOS Keychain so you never type it again
>                        **remove this line if you are on Linux** — it is macOS only
> `IdentityFile`       — tells SSH exactly which private key to use for github.com

```bash
# Set correct permissions on the config file (SSH refuses to work if permissions are wrong)
chmod 600 ~/.ssh/config

# Verify the file looks correct
cat ~/.ssh/config
```

**Linux only — start ssh-agent on login (one-time)**
```bash
# On Linux, ssh-agent does not start automatically on terminal open.
# Add this to the END of your ~/.bashrc (bash) or ~/.zshrc (zsh):
nano ~/.bashrc

# Paste at the bottom:
if [ -z "$SSH_AUTH_SOCK" ]; then
  eval "$(ssh-agent -s)"
fi

# Reload the file
source ~/.bashrc

# Now add your key to the running agent once:
ssh-add ~/.ssh/id_ed25519
# After this, the ~/.ssh/config handles everything automatically on next terminal open
```

**Step 4: Test the connection**
```bash
ssh -T git@github.com
# Expected: Hi your-username! You've successfully authenticated, but GitHub does not provide shell access.
# If you see "Permission denied (publickey)": public key was not added to GitHub correctly (redo Step 2)
# If you see "Connection refused": SSH port 22 is blocked — see tip below

# If port 22 is blocked by corporate firewall, use port 443 instead:
# Add this to ~/.ssh/config:
# Host github.com
#   Hostname ssh.github.com
#   Port 443
#   AddKeysToAgent yes
#   IdentityFile ~/.ssh/id_ed25519
```

**Step 5: Clone using SSH URL**
```bash
git clone git@github.com:your-username/repo-name.git
# HTTPS URL format: https://github.com/your-username/repo-name.git
# SSH URL format:   git@github.com:your-username/repo-name.git
#                   ^^^                          ^^^
#                   git user on github.com       colon not slash before username
```

**Switch an existing repo from HTTPS to SSH**
```bash
git remote -v                                          # check current remote URL
# origin  https://github.com/user/repo.git  (currently HTTPS)

git remote set-url origin git@github.com:user/repo.git # switch to SSH URL
git remote -v                                          # verify
# origin  git@github.com:user/repo.git      (now SSH)
```

---

#### SSH vs HTTPS — quick comparison

| | HTTPS + Keychain (Way 1/2) | SSH (Way 3) |
|---|---|---|
| Setup effort | Low — `gh auth login` done | Medium — keygen + GitHub setup |
| Token needed | Yes (PAT stored in keychain) | No |
| Works behind corporate proxy | Yes | Sometimes blocked |
| Used for CI/CD pipelines | Rarely | Yes (deploy keys) |
| Used by developers day-to-day | Most common | Common in security-focused orgs |
| Token expiry | PATs expire, need refresh | SSH keys don't expire by default |
| Multiple GitHub accounts | Easy (gh profiles) | Requires SSH config file tricks |

---

### Step 1 — CD to workspace & clone the repo

```bash
cd ~/projects                                              # go to your working folder
git clone <repo-url>                                       # clone repository
cd <repo>                                                  # enter project directory
```

### Step 2 — Inspect branches & switch to `dev`

```bash
git branch -r                                              # list remote branches
git branch -a                                              # all branches (local+remote)
git branch                                                 # local branches

git switch dev                                             # move to integration branch
# if missing locally:
# git switch -c dev origin/dev
```

### Step 3 — Create your feature branch from `dev`

```bash
git pull origin dev                                        # ensure latest dev first
git switch -c feature/EVNT-123-add-event-module            # feature/<TICKET>-<short-desc>
# naming notes: use ticket id, lower-case, hyphenated summary; e.g. feature/EVNT-456-add-payment-api
```

### Step 4 — Daily work: status → add → commit → push

```bash
git status                                                 # see changes
git add .                                                  # stage (prefer narrower paths when possible)

git commit -m "EVNT-123: Add validation for event form"   # <TICKET>: <short, imperative summary>
# optional details using multi-line commit (git commit then write body): what/why, not how

git push -u origin feature/EVNT-123-add-event-module       # push branch (first time with -u)
```

### Step 5 — Before finishing: sync with latest `dev` (rebase preferred)

```bash
# Option A (recommended for feature branches): REBASE to keep history clean
git switch dev && git pull origin dev                      # update dev
git switch feature/EVNT-123-add-event-module               # back to your branch
git rebase dev                                             # replay your commits on latest dev
# if conflicts: edit files to resolve →
git add <file>                                             # mark each resolved file
git rebase --continue                                      # continue rebase
# if you must abandon the rebase:
# git rebase --abort

# Option B (acceptable alternative): MERGE dev into feature
git switch feature/EVNT-123-add-event-module
git merge dev                                              # fast-forward or merge commit
# if conflicts: resolve, then
git add <file>
git merge --continue                                                 # completes the merge commit
```

### Step 5A — Squashing commits: GitHub UI (company standard) vs local rebase

> During development you often make commits like "wip", "fix typo", "trying again".
> The end goal is one clean commit per feature in the main history.
> There are two ways to achieve this — know both, but use the right one for your company.

---

#### Method 1 — Squash via GitHub UI on PR merge *(most common in companies)*

This is what most companies do. The developer does **nothing special** locally.
Just push your commits as-is and raise the PR. When the PR is approved, the person
merging selects **"Squash and merge"** on GitHub instead of the plain "Merge" button.

```
Developer's branch has:
  a3f1c2e wip
  b91d04a fix typo
  c72aa10 trying again
  d88e001 EVNT-123: Add validation for event form

After "Squash and merge" on GitHub UI, dev branch gets:
  f9a3b21 EVNT-123: Add validation for event form   ← one clean commit, all changes combined
```

GitHub lets the merger edit the final combined commit message before confirming.

**Why companies prefer this:**
- No local force push required
- Developer doesn't need to know `git rebase -i`
- Consistent — enforced at repo level (admins can disable plain merge so everyone squashes)
- Audit trail: the PR still shows all original commits for review history

> **How to check if your repo enforces this:**
> GitHub → repo → Settings → Pull Requests → look for "Allow squash merging" being the only enabled option.

---

#### Method 2 — Local interactive rebase before PR *(less common, useful to know)*

Used when the team wants the branch itself to be clean before review,
or when you need to reorganise/reword commits (not just squash).

```bash
git log --oneline                                          # see your commits on this branch
# a3f1c2e wip
# b91d04a fix typo
# c72aa10 trying again
# d88e001 EVNT-123: Add validation for event form

git rebase -i HEAD~4                                       # interactively edit last 4 commits
# Editor opens — change "pick" to "s" (squash) on all except the first:
#
# pick d88e001 EVNT-123: Add validation for event form
# s    c72aa10 trying again
# s    b91d04a fix typo
# s    a3f1c2e wip
#
# Save → another editor opens for the final commit message
# Write: "EVNT-123: Add validation for event form" → save

git push -f origin feature/EVNT-123-add-event-module       # force push needed — history was rewritten
# ONLY safe on your own feature branch. Never force push shared/protected branches.
```

### Step 6 — Sync dev into your feature branch FIRST, then push and raise PR

> **Never raise a PR on a branch that is behind `dev`.**
> Always merge latest `dev` into your feature branch before pushing for review.
> This ensures your branch has everyone else's latest work and any conflicts are
> resolved by you (the author) — not left for the reviewer or CI to discover.

```bash
# 1. Sync your feature branch with latest dev (do this before EVERY PR)
git switch dev && git pull origin dev                      # get latest dev
git switch feature/EVNT-123-add-event-module               # back to your branch
git merge dev                                              # merge dev into your feature branch
# if conflicts: resolve → git add <file> → git commit

# 2. Push the updated branch
git push origin feature/EVNT-123-add-event-module

# 3. Open PR in GitHub UI
# base=dev, compare=feature/EVNT-123-add-event-module
# Checklist before submitting:
#   ✓ dev is merged into your branch (just done above)
#   ✓ CI is green
#   ✓ no unresolved conflicts
#   ✓ PR description explains what and why
```

### Step 7 — Promote `dev` → `staging` for QA (via PR)

```bash
# Done in Git UI: create PR with base=staging, compare=dev  # protected branch, use PR only
# After merge, CI deploys staging environment automatically (if configured)
```

### Step 8 — QA bugfix flow (branch from `staging`, PR back to `staging`)

```bash
git switch staging && git pull origin staging              # start from latest staging
git switch -c bugfix/EVNT-221-login-issue                  # bugfix/<TICKET>-<short-desc>
# implement fix

git add . && git commit -m "EVNT-221: Fix login race condition"   # concise, ticket-first
git push -u origin bugfix/EVNT-221-login-issue
# Open PR in Git UI: base=staging, compare=bugfix/EVNT-221-login-issue
# After approval & green CI, merge PR to staging
```

### Step 9 — Release: `staging` → `main` (protected, PR-only) + tag

```bash
# 1. In GitHub UI: create PR base=main, compare=staging
#    Ensure required approvals + all production CI checks pass
#    Get sign-off from Release Manager / Tech Lead
#    Merge the PR (plain merge commit, not squash — you want the full staging history on main)
```

**After the PR is merged — tagging the release (done locally)**

> You must pull latest `main` first before tagging.
> If you tag without pulling, your local `main` is stale and the tag points to the wrong commit
> (the one before the merge). The tag must point to the exact merge commit that is now on `main`.

```bash
# 2. Switch to main and pull the just-merged commit
git switch main
git pull origin main                               # now your local main = what GitHub just merged

# 3. Verify you're on the right commit
git log --oneline -3
# expected output:
# f3a91bc Merge pull request #47 from staging      ← this is the release merge commit
# a72de10 EVNT-410: Fix edge case in checkout
# b33cc01 EVNT-405: Add promo code support

# 4. Check the previous tag so you know what to increment
git tag --sort=-version:refname | head -5
# output (latest first):
# v2.0.3
# v2.0.2
# v2.0.1
# v2.0.0
# v1.9.0

# or: get just the single most recent tag
git describe --tags --abbrev=0
# output: v2.0.3

# 5. Decide the new tag based on semantic versioning:
#
#    Format: v<MAJOR>.<MINOR>.<PATCH>
#
#    PATCH +1  →  only bug fixes merged         v2.0.3 → v2.0.4
#    MINOR +1  →  new features added            v2.0.3 → v2.1.0  (PATCH resets to 0)
#    MAJOR +1  →  breaking change (API changed, v2.0.3 → v3.0.0  (MINOR+PATCH reset to 0)
#                 DB schema changed, etc.)
#
#    In this example: new features were added → bump MINOR
#    Previous: v2.0.3  →  New tag: v2.1.0

# 6. Create annotated tag on this HEAD
git tag -a v2.1.0 -m "Release v2.1.0 - Event module and checkout improvements"
#         ^^^^^^^   ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
#         tag name  message shown in GitHub Releases and git log

# 5. Verify the tag was created correctly
git show v2.1.0
# shows: tag name, tagger, date, message, and the commit it points to

# 6. Push the tag to remote
git push origin v2.1.0

# The tag is now visible on GitHub under Releases / Tags
```

> **Why annotated tag (`-a`) and not lightweight tag?**
> Annotated tags store author, date, and message — they show up properly in GitHub Releases
> and `git describe`. Lightweight tags are just a pointer with no metadata, used for temp markers.
> Always use `-a` for release tags in a company.

### Step 9A — Hotfix flow (production emergency — branch directly from `main`)

> A bug is found in production **after** a release. You cannot wait for the normal dev → staging → main cycle.
> Hotfixes branch from `main` (live production code), not from `dev` or `staging`.

```bash
# 1. Start from latest main (what is actually running in production)
git switch main && git pull origin main

# 2. Create a hotfix branch
git switch -c hotfix/EVNT-999-payment-crash                # hotfix/<TICKET>-<short-desc>

# 3. Implement the fix
# ... edit files ...
git add .
git commit -m "EVNT-999: Fix null pointer crash in payment processor"

# 4. Push and raise PR → main (get emergency approval)
git push -u origin hotfix/EVNT-999-payment-crash
# Open PR: base=main, compare=hotfix/EVNT-999-payment-crash
# After approval + CI green → merge to main

# 5. Tag the hotfix release immediately after merge
git fetch --tags
git tag -a v2.1.1 -m "Hotfix 2.1.1 - Fix payment crash"
git push origin v2.1.1

# 6. Bring the fix back to dev and staging so they don't regress
#    Option A: cherry-pick the commit into dev (see Step 11 for cherry-pick)
git switch dev && git pull origin dev
git cherry-pick <hotfix-commit-hash>                       # apply just this commit to dev
git push origin dev

#    Option B: raise a PR from hotfix branch → dev (safer if there are conflicts)
# Open PR in Git UI: base=dev, compare=hotfix/EVNT-999-payment-crash
```

> **Why not branch from `staging`?**
> `staging` may have unreleased features from `dev`. Branching from it would accidentally carry
> those into production when you merge back to `main`.

---

### Step 10 — Back-merge after release (keep branches in sync)

> **Do this immediately after every release. Do not wait for the next sprint.**
> After `staging → main` merges, `main` and `staging` are identical.
> But `dev` may have new commits added during the sprint that were not part of this release.
> Both back-merges below are mandatory — skipping either causes branches to drift apart
> and future releases risk losing hotfixes or carrying stale code into QA.

```bash
# --- Back-merge 1: main → dev (MANDATORY) ---
# Reason: any hotfix that went directly to main must come back to dev,
#         otherwise the next release will overwrite it and the fix is lost in production.

git switch dev && git pull origin dev
git merge origin/main                                      # bring released + hotfix commits to dev
# if conflict: resolve → git add <file> → git commit
git push origin dev

git log --oneline dev..main                                # verify: should be empty (dev is ahead or equal)

# --- Back-merge 2: dev → staging (MANDATORY before next QA cycle) ---
# Reason: staging must have the new sprint's dev commits before QA starts testing.
#         Best practice is to do this right after the release, not at the start of next sprint,
#         so all three branches (main, dev, staging) are in sync and the team starts fresh.

# Via PR (recommended if staging is a protected branch):
# Open PR in GitHub UI: base=staging, compare=dev
# Get approval → merge

# Via direct merge (if your policy allows it):
git switch staging && git pull origin staging
git merge origin/dev
git push origin staging

# Final verification — all three should point to the same or expected commits:
git log --oneline -1 main
git log --oneline -1 staging
git log --oneline -1 dev
```

> **State after both merges:**
> `main` = released code
> `staging` = released code + new sprint dev commits (ready for next QA round)
> `dev` = same as staging (hotfixes from main are now in dev too)

### Step 11 — Revert a bad commit (safe undo on a protected branch)

> `git revert` is always safe on shared/protected branches — it creates a NEW commit
> that undoes the changes. It does NOT rewrite history, so no one else is affected.
> Never use `git reset --hard` on a shared branch — it rewrites history and breaks
> everyone who has already pulled that commit.

**Step 1: Find the bad commit**
```bash
git switch main && git pull origin main
git log --oneline -5
# f3a91bc Merge pull request #47 from feature/EVNT-301-login-styling   ← bad one
# b71d004 EVNT-298: Add footer links
# c92ee11 Release v2.1.0
```

**Step 2: Check if it is a merge commit or a regular commit**
```bash
git cat-file -p f3a91bc | grep parent
# parent b71d004          ← ONE parent  = regular (squash merged) commit
# parent b71d004
# parent c92ee11          ← TWO parents = merge commit (standard merge)
```

**Step 3: Create the revert branch FIRST (never revert directly on main)**
```bash
git switch -c revert/EVNT-301-undo-login-styling           # create branch off main
```

**Step 4: Revert — choose the right command based on commit type**
```bash
# If it is a REGULAR commit (one parent — squash merged PR):
git revert f3a91bc                                         # no extra flag needed
# editor opens with default message → save it

# If it is a MERGE COMMIT (two parents — standard merge PR):
git revert -m 1 f3a91bc                                    # -m 1 means "keep parent 1 (main), undo parent 2 (feature)"
# editor opens with default message → save it

# What -m 1 means:
# A merge commit has two parents: parent 1 = main before merge, parent 2 = the feature branch
# -m 1 tells git: treat parent 1 (main) as the mainline → revert everything the feature branch added
```

**Step 5: Push the branch and raise PR**
```bash
git push -u origin revert/EVNT-301-undo-login-styling

# Open PR in GitHub UI:
# base=main, compare=revert/EVNT-301-undo-login-styling
# Get approval → merge

# After merge, verify the bad changes are gone:
git switch main && git pull origin main
git log --oneline -5
```

**Other emergency commands**
```bash
# Temporarily shelve uncommitted work
git stash                                                   # save uncommitted changes
git stash pop                                               # reapply latest stash

# If .gitignore was added late and files are already tracked
git rm -r --cached . && git add . && git commit -m "chore: apply .gitignore"
```

### Step 11A — Cherry-pick (apply a specific commit to another branch)

> Used when you want ONE commit from a branch — not the entire branch.
> Most common use: applying a hotfix commit to `dev`/`staging` after it was merged to `main`.

```bash
# Scenario: commit a3f9c12 fixed a bug on main, now you need it on dev too

git log --oneline main                                     # find the commit hash
# a3f9c12 EVNT-999: Fix null pointer crash in payment processor

git switch dev && git pull origin dev                      # start from latest dev
git cherry-pick a3f9c12                                    # apply just that one commit

# if conflict during cherry-pick:
# resolve the file, then:
git add <file>
git cherry-pick --continue

# to abort:
# git cherry-pick --abort

git push origin dev                                        # push the result
```

> **Rule**: cherry-pick creates a NEW commit with the same change but a different hash.
> The original commit on `main` is untouched.

---

### Step 11B — Git bisect (find which commit introduced a bug)

> Production is broken. You have 200 commits since the last known good release.
> `git bisect` does a **binary search** through commits — you only need to test ~8 times instead of 200.

```bash
git bisect start                                           # begin bisect session

git bisect bad                                             # tell git: current state is broken
git bisect good v2.0.0                                     # tell git: this tag/commit was working

# Git automatically checks out a commit in the middle.
# Test your app → does the bug exist?

git bisect bad                                             # yes, still broken → bug is in earlier half
# OR
git bisect good                                            # no, works here → bug is in later half

# Git keeps halving the range. After ~8 rounds it will output:
# "a3f9c12 is the first bad commit"
# commit a3f9c12
# Author: John Dev <john@company.com>
# Date:   Mon Apr 7 10:22:00 2026
#     EVNT-188: Refactor payment processor

git bisect reset                                           # exit bisect, return to original branch

# Now you know exactly which commit broke things → revert it or fix it
git revert a3f9c12
```

> **Real-world tip**: you can automate bisect with a test script:
> ```bash
> git bisect run ./run-tests.sh                            # runs script on each step; 0=good, non-zero=bad
> ```

### Step 12 — Cleanup (delete local branch, then remote branch)

> **Rule: you cannot delete the branch you are currently on.**
> Git will throw an error: `error: Cannot delete branch 'feature/...' checked out`.
> Always switch to another branch first (usually `dev`), then delete.

```bash
# 1. Switch away from the feature branch first
git switch dev                                             # move to dev (or main — any other branch)

# 2. Delete the LOCAL branch
git branch -d feature/EVNT-123-add-event-module            # -d = safe delete (only if fully merged)
# if git refuses because it thinks it's not merged, and you're sure it is:
# git branch -D feature/EVNT-123-add-event-module          # -D = force delete

# 3. Delete the REMOTE branch on GitHub
git push origin --delete feature/EVNT-123-add-event-module
# or shorthand:
# git push origin -d feature/EVNT-123-add-event-module

# 4. Prune stale remote-tracking references from your local git
git fetch -p                                               # removes origin/<branch> entries that no longer exist

# 5. Verify it's gone
git branch -a                                              # should not see the branch anywhere
```

> **Note**: GitHub also offers "Delete branch" button on the merged PR page.
> That only deletes the remote branch. You still need step 2 to delete your local copy.

---

## Who does what (typical)

- Developer: feature → PR into `dev`; addresses review; keeps branch rebased.
- QA/Release/DevOps: PR `dev` → `staging`; oversee deployments and checks.
- Release Manager/Approver: PR `staging` → `main`; tags release.
- After release: back-merge `main` → `dev` to keep history consistent.

**End of sequential SOP.**

---

## Conventional Commits (commit message standard used in orgs)

> Many companies enforce this format via CI hooks. It also auto-generates changelogs.

**Format:**
```
<type>(<scope>): <short summary>

[optional body]
[optional footer: BREAKING CHANGE or issue refs]
```

| Type | When to use | Example |
|------|-------------|---------|
| `feat` | New feature | `feat(auth): add OAuth2 login` |
| `fix` | Bug fix | `fix(payment): handle null card object` |
| `chore` | Tooling, deps, config (no prod code) | `chore: upgrade node to 20` |
| `docs` | Documentation only | `docs(api): update endpoint examples` |
| `refactor` | Code restructure, no feature/fix | `refactor(cart): extract price calculator` |
| `test` | Adding or fixing tests | `test(auth): add unit tests for token refresh` |
| `ci` | CI/CD pipeline changes | `ci: add staging deploy step to workflow` |
| `perf` | Performance improvement | `perf(db): add index on orders.user_id` |

**Breaking change** (major version bump):
```
feat(api): change response shape for /users endpoint

BREAKING CHANGE: `data` field renamed to `users` in response body
```

> Combine with ticket reference: `fix(payment): EVNT-999 handle null card object`

---

## Pre-commit Hooks (automated checks before every commit)

> Orgs use hooks to enforce code quality gates **before** code even reaches CI.
> The most common tool is `pre-commit` (Python) or `husky` (Node.js).

### Setting up `pre-commit` (language-agnostic)

```bash
pip install pre-commit                                     # install tool

# create .pre-commit-config.yaml at repo root:
```

```yaml
# .pre-commit-config.yaml
repos:
  - repo: https://github.com/pre-commit/pre-commit-hooks
    rev: v4.5.0
    hooks:
      - id: trailing-whitespace          # remove trailing spaces
      - id: end-of-file-fixer            # ensure files end with newline
      - id: check-merge-conflict         # block accidental merge conflict markers
      - id: check-yaml                   # validate YAML syntax
      - id: detect-private-key           # block committing private keys

  - repo: https://github.com/psf/black   # Python formatter
    rev: 24.3.0
    hooks:
      - id: black
```

```bash
pre-commit install                                         # installs the git hook into .git/hooks/pre-commit
# Now every `git commit` runs these checks automatically

git commit -m "feat: add user service"                     # hooks run first, commit blocked if any fail

# bypass hooks in emergency (use sparingly, logs this action):
git commit --no-verify -m "emergency: hotfix deploy"
```

### Setting up `husky` (Node.js projects)

```bash
npm install --save-dev husky
npx husky init                                             # creates .husky/ folder

# .husky/pre-commit
npm run lint                                               # runs eslint before every commit
npm run test -- --passWithNoTests

# .husky/commit-msg (enforce conventional commits format)
npx --no -- commitlint --edit $1
```

---

## CODEOWNERS (auto-assign reviewers by file path)

> GitHub/GitLab reads this file to **automatically add reviewers** when a PR touches certain paths.
> Placed at repo root, `docs/`, or `.github/`.

```
# .github/CODEOWNERS

# Global fallback — any file not matched below
*                           @org/core-team

# Backend services
/backend/                   @org/backend-team
/backend/auth/              @john-dev @sarah-dev       # specific people for sensitive area

# Infrastructure — DevOps must review all infra changes
/infra/                     @org/devops-team
/terraform/                 @org/devops-team
/.github/workflows/         @org/devops-team           # CI pipeline changes

# Frontend
/frontend/                  @org/frontend-team

# Docs — tech writers
/docs/                      @org/tech-writers

# Security-sensitive files — security team must approve
**/secrets*                 @org/security-team
**/.env*                    @org/security-team
```

> **How it works**: When a PR modifies `/infra/k8s/deployment.yaml`, GitHub automatically
> adds `@org/devops-team` as required reviewers. PR cannot merge without their approval
> (when branch protection + CODEOWNERS is enabled).

---

## PR Merge Strategies (GitHub/GitLab repo settings)

> The merge button on a PR has 3 options. Orgs configure which ones are allowed.
> Understanding these is important — the wrong strategy creates messy history.

### 1. Merge commit (default)
```
main:    A --- B ----------- F (merge commit)
                \           /
feature:         C --- D --- E
```
- Creates a merge commit `F` joining both histories
- **Preserves** full context of when feature was developed
- **Downside**: clutters `git log` with merge commits
- **Use when**: you want full history, release branches

### 2. Squash and merge *(most common in product teams)*
```
main:    A --- B --- F' (one squashed commit with all C+D+E changes)
feature:       C --- D --- E  (branch deleted after)
```
- All commits from the PR are squashed into **one commit** on `main`
- Clean, linear history — one commit per feature/fix
- **Downside**: you lose individual commit history from the branch
- **Use when**: feature branches, most product repos

### 3. Rebase and merge
```
main:    A --- B --- C' --- D' --- E'  (replayed commits, no merge commit)
```
- Each commit from the PR is **replayed** on top of `main` individually
- Linear history, keeps individual commits
- **Downside**: rewrites commit hashes; can confuse `git blame`
- **Use when**: teams that want linear history AND individual commits

### How to check which strategy your repo uses
```bash
# GitHub repo → Settings → Pull Requests section
# Look for: "Allow merge commits" / "Allow squash merging" / "Allow rebase merging"
# Org policy usually enables only one to enforce consistency
```