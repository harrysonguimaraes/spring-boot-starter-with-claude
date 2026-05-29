## Spring Boot Testing
- When writing `@WebMvcTest` slices, always explicitly import the project's `SecurityConfig` (e.g., `@Import(SecurityConfig.class)`) instead of relying on Spring's default security auto-configuration.
- For JWT-protected endpoints, verify the test security context matches production config before asserting status codes.

## Learning Mode
When implementing Spring/Java features, briefly explain the key concepts (annotations, lifecycle, why this approach) alongside the code — not just the diff.

## Deploy
Before deploying, consult and update [`docs/deploy-checklist.md`](docs/deploy-checklist.md). Run `/deploy-check` to verify each item interactively.
