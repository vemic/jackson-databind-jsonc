import java.time.Duration

plugins {
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}

// Maven Central Portal Configuration
// ================================
// This project uses Maven Central Portal for publishing.
// 
// Required environment variables for automated publishing:
// - CENTRAL_PORTAL_USERNAME: Central Portal username 
// - CENTRAL_PORTAL_PASSWORD: Central Portal password/token
// 
// Setup guide: https://central.sonatype.org/publish/generate-portal-token/

nexusPublishing {
    repositories {
        sonatype {
            // Central Portal configuration via nexus-publish-plugin
            nexusUrl.set(uri("https://central.sonatype.com/api/v1/publisher/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/api/v1/publisher/"))
            
            username.set(System.getenv("CENTRAL_PORTAL_USERNAME") ?: "")
            password.set(System.getenv("CENTRAL_PORTAL_PASSWORD") ?: "")
        }
    }
    
    // Configure timeouts for publishing
    connectTimeout.set(Duration.ofMinutes(3))
    clientTimeout.set(Duration.ofMinutes(3))
    
    // Transition check settings
    transitionCheckOptions {
        maxRetries.set(60)
        delayBetween.set(Duration.ofSeconds(10))
    }
}

// Add publishing tasks for validation
tasks.register("checkCentralPortalCredentials") {
    group = "verification"
    description = "Validates Central Portal publishing configuration"
    
    doLast {
        val username = System.getenv("CENTRAL_PORTAL_USERNAME")
        val password = System.getenv("CENTRAL_PORTAL_PASSWORD")
        
        println("=== Central Portal Configuration ===")
        println("Username configured: ${if (!username.isNullOrEmpty()) "✓" else "✗"}")
        println("Password configured: ${if (!password.isNullOrEmpty()) "✓" else "✗"}")
        println("")
        
        if (username.isNullOrEmpty() || password.isNullOrEmpty()) {
            println("⚠️  Missing Central Portal credentials")
            println("Required environment variables:")
            println("• CENTRAL_PORTAL_USERNAME")
            println("• CENTRAL_PORTAL_PASSWORD")
            println("")
            println("📚 Setup guide: https://central.sonatype.org/publish/generate-portal-token/")
        } else {
            println("✅ Central Portal credentials configured")
            println("Ready for automated publishing!")
        }
    }
}

// Add custom tasks for debugging and validation
tasks.register("validateCredentials") {
    group = "verification"
    description = "Validates publishing configuration"
    dependsOn("checkCentralPortalCredentials")
}

tasks.register("publishLocalOnly") {
    group = "publishing"
    description = "Publishes only to local Maven repository (no remote publishing)"
    dependsOn(":lib:publishToMavenLocal")
}

