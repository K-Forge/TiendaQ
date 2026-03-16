# Security Policy

## Reporting a Vulnerability

If you discover a security vulnerability in TiendaQ, we ask that you report it responsibly. **Do not open a public GitHub issue for security vulnerabilities.**

Instead, please send a detailed report to:

**Email**: kforge.dev@gmail.com

Include the following information in your report:

- A description of the vulnerability and its potential impact
- Steps to reproduce the issue
- The affected component (backend API, frontend, database, authentication, etc.)
- Any relevant logs, screenshots, or proof-of-concept code
- Your suggested fix, if you have one

## What Constitutes a Security Issue

The following are considered security issues and should be reported through the process described above:

- Authentication or authorization bypass
- SQL injection, XSS, CSRF, or other injection attacks
- Exposure of sensitive data (user credentials, personal information, payment data)
- Insecure direct object references (IDOR)
- Server-side request forgery (SSRF)
- Misconfigured Spring Security rules or OAuth2 flows
- Exposed API endpoints that should require authentication
- Hardcoded credentials or secrets in the codebase
- Dependency vulnerabilities with known exploits

The following are **not** considered security issues:

- Bugs that do not have a security impact
- Feature requests or usability issues
- Issues in third-party dependencies without a demonstrated exploit path against TiendaQ
- Denial of service via excessive API calls (unless exploitable with minimal resources)

## Responsible Disclosure Policy

- **Do not** publicly disclose the vulnerability before it has been addressed.
- **Do not** access, modify, or delete data belonging to other users during your research.
- **Do not** perform actions that could degrade the service for other users.
- Act in good faith to avoid privacy violations, data destruction, and service interruption.

## Response Expectations

- **Acknowledgment**: We will acknowledge receipt of your report within 72 hours.
- **Assessment**: We will provide an initial assessment of the vulnerability within 7 days.
- **Resolution**: We aim to resolve confirmed vulnerabilities within 30 days, depending on severity and complexity.
- **Credit**: If you wish, we will credit you in the fix commit or release notes.

## Supported Versions

Security updates are applied to the latest version on the `main` branch. We do not maintain security patches for older versions.

| Branch     | Supported |
| ---------- | --------- |
| `main`     | Yes       |
| All others | No        |

## Scope

This policy applies to the TiendaQ application and all code hosted in the [K-Forge/TiendaK](https://github.com/K-Forge/TiendaQ) repository, including:

- Spring Boot backend API (`app/backend/`)
- Angular frontend application (`app/frontend/`)
- Database schemas and migration scripts (`app/database/`)
- Build and deployment scripts (`scripts/`)
