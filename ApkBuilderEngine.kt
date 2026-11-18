package com.lucas.apkbuilder

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ApkBuilderEngine {
    fun buildUnsignedApk(projectDir: File, outputApk: File): Boolean {
        return try {
            val fos = FileOutputStream(outputApk)
            val zos = ZipOutputStream(fos)
            
            projectDir.walkTopDown().forEach { file ->
                if (file.isFile) {
                    val relativePath = file.toURI().toASCIIString()
                        .substring(projectDir.toURI().toASCIIString().length)
                    zos.putNextEntry(ZipEntry(relativePath))
                    file.inputStream().copyTo(zos)
                    zos.closeEntry()
                }
            }
            zos.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
