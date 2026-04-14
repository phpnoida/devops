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

### Step 6 — Push updated feature & raise PR → `dev`

```bash
git push origin feature/EVNT-123-add-event-module          # push after rebase/merge
# Open PR in Git UI: base=dev, compare=feature/EVNT-123-...
# Ensure: CI green, approvals obtained, branch up-to-date
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

# 4. Create annotated tag on this HEAD
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

```bash
# sync main → dev so dev contains the released code
git switch dev && git pull origin dev
git merge origin/main                                       # bring prod changes to dev
git push origin dev

# (optional) also ensure staging matches dev for next cycle via PR if policy requires
# Typically: PR base=staging, compare=dev (or merge dev → staging before next QA cycle)
```

### Step 11 — Revert / emergency commands (when something goes wrong)

```bash
git log --oneline --graph --decorate --all                 # inspect history

git revert <commit-id>                                     # safe undo: creates a new commit that reverts
# push and raise PR for protected branches

# use with caution on shared branches (avoid on protected):
# git reset --hard <commit-id>

# temporarily shelve work
git stash                                                   # save uncommitted changes
git stash pop                                               # reapply latest stash

# if .gitignore changed late and files are already tracked
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

### Step 12 — Cleanup

```bash
git push origin -d <branchName> #delete remote branch
git branch -d feature/EVNT-123-add-event-module            # delete merged local branch
git fetch -p                                               # prune deleted remote branches locally
```

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