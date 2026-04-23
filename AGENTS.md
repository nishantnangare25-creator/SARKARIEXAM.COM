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
