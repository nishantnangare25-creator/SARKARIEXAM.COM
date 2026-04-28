<!-- VERCEL BEST PRACTICES START -->
## Best practices for developing on Vercel

These defaults are optimized for AI coding agents (and humans) working on apps that deploy to Vercel.

- Treat Vercel Functions as stateless + ephemeral (no durable RAM/FS, no background daemons), use Blob or marketplace integrations for preserving state
- Edge Functions (standalone) are deprecated; prefer Vercel Functions
- Don't start new projects on Vercel KV/Postgres (both discontinued); use Marketplace Redis/Postgres instead
- Store secrets in Vercel Env Variables; not in git or `NEXT_PUBLIC_*`
- Provision Marketplace native integrations with `vercel integration add` (CI/agent-friendly)
- Sync env + project settings with `vercel env pull` / `vercel pull` when you need local/offline parity
- Use `waitUntil` for post-response work; avoid the deprecated Function `context` parameter
- Set Function regions near your primary data source; avoid cross-region DB/service roundtrips
- Tune Fluid Compute knobs (e.g., `maxDuration`, memory/CPU) for long I/O-heavy calls (LLMs, APIs)
- Use Runtime Cache for fast **regional** caching + tag invalidation (don't treat it as global KV)
- Use Cron Jobs for schedules; cron runs in UTC and triggers your production URL via HTTP GET
- Use Vercel Blob for uploads/media; Use Edge Config for small, globally-read config
- If Enable Deployment Protection is enabled, use a bypass secret to directly access them
- Add OpenTelemetry via `@vercel/otel` on Node; don't expect OTEL support on the Edge runtime
- Enable Web Analytics + Speed Insights early
- Use AI Gateway for model routing, set AI_GATEWAY_API_KEY, using a model string (e.g. 'anthropic/claude-sonnet-4.6'), Gateway is already default in AI SDK
  needed. Always curl https://ai-gateway.vercel.sh/v1/models first; never trust model IDs from memory
- For durable agent loops or untrusted code: use Workflow (pause/resume/state) + Sandbox; use Vercel MCP for secure infra access
<!-- VERCEL BEST PRACTICES END -->

<!-- ANTIGRAVITY LOCAL BUILD RULES START -->
## Local Development & Build Rules

To ensure privacy and build control, follow these rules strictly:

- **DO NOT PUSH TO GITHUB**: Never run `git push` or any command that uploads code to a remote repository.
- **NO REMOTE CI BUILDS**: Do not trigger GitHub Actions, CircleCI, or any other remote build pipelines, especially for APK generation.
- **LOCAL APK GENERATION ONLY**: All Android APK files must be built locally on the host machine or by the agent using local tools.
  - **Environment Setup**: Before building, ensure `JAVA_HOME` is set. If missing, use: `$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"` (or the appropriate path for your installation).
  - **Command**: Run `.\gradlew.bat assembleDebug` from the project root.
- **FILE PERSISTENCE**: If a build is successful, keep the APK in the local workspace root or the `app/build/outputs/apk/` directory for the user to access.
<!-- ANTIGRAVITY LOCAL BUILD RULES END -->

<!-- FTP DEPLOYMENT & CI STABILITY START -->
## FTP Deployment & GitHub Actions Stability

To ensure reliable deployments to MilesWeb/Production servers:

- **RETRY LOGIC**: Always use the `deployWithRetry` wrapper in `deploy-to-milesweb.js`. It is configured for 3 attempts with exponential backoff to handle `ECONNRESET` and network glitches.
- **TIMEOUTS**: Keep FTP connection timeouts at 120,000ms (2 minutes) to prevent premature disconnection during large file uploads.
- **SUBMODULE CONFLICTS**: Do not include folders with their own `.git` directories (like `GIT_UPLOAD_REPO`) in the main repository tracking. They must be added to `.gitignore` to prevent GitHub Actions from failing with "No url found for submodule" errors.
- **WORKFLOW TIMEOUTS**: Always set a `timeout-minutes: 15` on deployment steps in `.github/workflows/deploy.yml` to prevent hung processes from consuming Action minutes.
- **CLEAN UPLOAD**: Ensure only the `dist` folder is uploaded to `public_html` to keep the production environment clean and deployment fast.
<!-- FTP DEPLOYMENT & CI STABILITY END -->

<!-- BLOG POSTING RULES START -->
## Daily Blog Posting Rules

- **SCHEDULE**: Two posts daily.
  - **Morning Post**: 8:00 AM IST.
  - **Evening Post**: 5:00 PM IST.
- **PROCESS**:
  1. **Plagiarism Check**: Use web search/analysis to verify uniqueness.
  2. **SEO Optimization**: Rewrite content for SEO (headings, keywords, readability).
  3. **Implementation**: Add the new post to `src/data/blogPosts.json` and images to `public/assets/blog/`.
  4. **Git Push**: Commit and push the blog post ONLY to the website repository (`AIGovPrep`).
- **CONTENT FORMAT**:
  - **TITLE**: The Title MUST be exactly the **Keyword** provided by the USER.
  - **FORMAT**: Ensure every post has a Title, Excerpt, Content (HTML), Tags, Date, and FeaturedImage.
- **WRITING STYLE**: 
  - **100% HUMAN STYLE**: Content must be written in a natural, human-like tone. Avoid generic AI patterns. 
  - **ZERO PLAGIARISM**: Ensure content is entirely original and passes uniqueness checks.
- **IMAGES**:
  - **NO AI GENERATION**: Do not use `generate_image` or AI-generated visuals for blog posts.
  - **WEB SOURCES**: Use real, high-quality images from the web (e.g., Unsplash or official sources) that match the keyword/topic perfectly.
- **EXCLUSION**: Do not push Android app changes when performing blog updates unless explicitly asked.
<!-- BLOG POSTING RULES END -->
