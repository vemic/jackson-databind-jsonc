# Publishing Guide

This document explains how to publish releases to Maven Central using the automated Central Portal workflow and manage the CI/CD process.

> **✅ SUCCESS**: This project now supports fully automated publishing to Maven Central using Central Portal API integration.

## Release Process Overview

The release process is now split into separate workflows:

1. **Build Workflow** (`.github/workflows/build.yml`) - Runs on every push/PR
2. **Release Workflow** (`.github/workflows/release.yml`) - Runs on tags and publishes to Maven Central

### 1. Automated Release Process

**Trigger a Release:**
- **Git Tag**: Push a tag like `v1.0.5` to automatically trigger release and Maven Central publishing
- **Manual**: Use GitHub Actions "workflow_dispatch" with version input and optional skip options

**What Happens Automatically:**
1. **Build Artifacts**: Creates slim JAR, fat JAR, sources JAR, and javadoc JAR
2. **GitHub Release**: Creates release with JAR attachments and release notes
3. **Maven Central Publishing**: Automatically uploads and publishes to Maven Central Portal
4. **Validation**: Verifies artifacts are available on Maven Central

### 2. Prerequisites for Automated Publishing

**Required GitHub Secrets:**
- `CENTRAL_PORTAL_USERNAME`: Your Central Portal username
- `CENTRAL_PORTAL_PASSWORD`: Your Central Portal password/token
- `GPG_PRIVATE_KEY`: GPG private key for signing artifacts (optional)
- `GPG_PASSPHRASE`: GPG key passphrase (optional)

**Setup Central Portal Credentials:**
1. Visit https://central.sonatype.com/
2. Sign in with your Sonatype account
3. Generate Publisher API credentials
4. Add credentials as GitHub repository secrets

### 3. Manual Release (Fallback)

If automated publishing fails, you can skip Maven Central and upload manually:

1. **Trigger with skip option**: Use workflow_dispatch with `skip_maven_central: true`
2. **Download from GitHub**: Get artifacts from the created GitHub release
3. **Manual upload**: Upload to https://central.sonatype.com/

### 4. Local Development and Testing

```bash
# Validate Central Portal credentials  
./gradlew validateCredentials

# Publish to local Maven repository only
./gradlew publishLocalOnly

# Build all release artifacts
./gradlew clean build fatJar

# Test Central Portal publishing (requires credentials)
./gradlew publishToCentralPortal
```

## Troubleshooting

### Migration-Related Issues

**Symptoms:**
- Build references to deprecated OSSRH endpoints
- Missing Sonatype publishing tasks

**Solutions:**
1. **Use new workflow**: Current release workflow creates GitHub releases with artifacts
2. **Manual upload**: Download artifacts from GitHub releases and upload to Central Portal
3. **Clear cache**: `./gradlew clean build --refresh-dependencies`

### Local Development

```bash
# Validate current configuration  
./gradlew validateCredentials

# Publish to local Maven repository only
./gradlew publishLocalOnly

# Build release artifacts
./gradlew clean build fatJar
```

## Security Notes

- **Never commit credentials** to the repository
- **Use GitHub secrets** for storing sensitive information  
- **Rotate credentials** regularly
- **Use API tokens** instead of passwords when possible

## Maven Central Portal Access

1. **Create Sonatype account**: Visit https://central.sonatype.com/
2. **Request namespace**: Request access to jp.vemi group through Central Portal
3. **Verify domain ownership**: You may need to prove ownership of vemi.jp domain
4. **Wait for approval**: Can take 1-2 business days
5. **Set up GPG signing**: Required for Maven Central (can be done during upload)

## References

- [Maven Central Portal](https://central.sonatype.com/)
- [OSSRH Sunset Information](https://central.sonatype.org/pages/ossrh-eol/)
- [Central Portal Migration Guide](https://www.endoflineblog.com/migrate-maven-central-publishing-to-central-portal-for-a-gradle-project)
- [Maven Central Requirements](https://central.sonatype.org/publish/requirements/)
- [GPG Signing Guide](https://central.sonatype.org/publish/requirements/gpg/)