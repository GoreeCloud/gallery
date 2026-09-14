package com.goreecloud.gallery

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RecycleBinMutationBatchingTest {
    @Test
    fun plansTwoHundredFiftyItemsAsBoundedOrderedBatches() {
        val uris = (1..250).map { "content://media/external/images/media/$it" }

        val batches = RecycleBinMutationBatching.plan(uris, maxBatchSize = 100)

        assertEquals(listOf(100, 100, 50), batches.map(List<String>::size))
        assertEquals(uris, batches.flatten())
    }

    @Test
    fun keepsSingleRequestSizedScopeInOneBatch() {
        val uris = (1..100).map { "content://media/external/images/media/$it" }

        val batches = RecycleBinMutationBatching.plan(uris, maxBatchSize = 100)

        assertEquals(1, batches.size)
        assertEquals(uris, batches.single())
    }

    @Test
    fun rejectsDuplicateScope() {
        assertFailsWith<IllegalArgumentException> {
            RecycleBinMutationBatching.plan(
                listOf(
                    "content://media/external/images/media/1",
                    "content://media/external/images/media/1",
                ),
                maxBatchSize = 100,
            )
        }
    }
}
