/*
 * SPDX-FileCopyrightText: 2023-2026 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package com.dot.gallery.core.logging

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LogExporterTest {

    @Test
    fun `issue url contains template labels and encoded title`() {
        val url = LogExporter.issueUrl("[BUG] A crash in Library")
        assertTrue(url.startsWith(LogExporter.ISSUES_NEW_URL))
        assertTrue(url.contains("template=bug_report.md"))
        assertTrue(url.contains("labels=bug"))
        assertTrue(url.contains("title=%5BBUG%5D%20A%20crash%20in%20Library"))
        assertFalse(url.contains("body="))
    }

    @Test
    fun `issue url never sends diagnostics or device identifiers in query`() {
        val url = LogExporter.issueUrl("[BUG] GoreeCloud Gallery crash report")
        assertFalse(url.contains("body="))
        assertFalse(url.contains("logs.jsonl"))
        assertFalse(url.contains("device-info"))
        assertFalse(url.contains("error%20trace"))
    }

    @Test
    fun `entry text wraps the entry as fenced json`() {
        val entry = LogEntry(1, "ERROR", "tag", "message")
        val text = LogExporter.entryText(entry)
        assertTrue(text.startsWith("```json"))
        assertTrue(text.contains("\"tag\":\"tag\""))
        assertTrue(text.trim().endsWith("```"))
    }
}
