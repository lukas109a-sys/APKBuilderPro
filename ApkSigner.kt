package com.lucas.apkbuilder

import java.io.File
import java.io.FileOutputStream
import java.util.jar.Attributes
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.jar.Manifest
import java.security.MessageDigest
import java.util.Base64

object ApkSigner {
    fun sign(unsignedApk: File, signedApk: File): Boolean {
        return try {
            val jarFile = JarFile(unsignedApk)
            val manifest = Manifest()
            val mainAttrs = manifest.mainAttributes
            mainAttrs.putValue(Attributes.Name.MANIFEST_VERSION.toString(), "1.0")
            mainAttrs.putValue("Created-By", "APKBuilderPro")

            val sha256 = MessageDigest.getInstance("SHA-256")
            val entries = jarFile.entries()

            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                if (!entry.isDirectory && !entry.name.startsWith("META-INF/")) {
                    val bytes = jarFile.getInputStream(entry).readBytes()
                    val digest = Base64.getEncoder().encodeToString(sha256.digest(bytes))
                    val attrs = Attributes()
                    attrs.putValue("SHA-256-Digest", digest)
                    manifest.entries[entry.name] = attrs
                }
            }

            JarOutputStream(FileOutputStream(signedApk), manifest).use { jos ->
                val copyEntries = jarFile.entries()
                while (copyEntries.hasMoreElements()) {
                    val entry = copyEntries.nextElement()
                    if (!entry.name.startsWith("META-INF/")) {
                        jos.putNextEntry(JarEntry(entry.name))
                        jarFile.getInputStream(entry).copyTo(jos)
                        jos.closeEntry()
                    }
                }
            }
            jarFile.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
