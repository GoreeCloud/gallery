package com.dot.gallery.feature_node.presentation.storycards

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.dot.gallery.R
import com.dot.gallery.feature_node.domain.model.StoryCard
import com.dot.gallery.feature_node.domain.model.StoryCardType
import com.dot.gallery.feature_node.presentation.storycards.components.StoryCardsRow
import com.dot.gallery.feature_node.presentation.storycards.components.storyCardTag
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class StoryCardsRowTest {

    @get:Rule
    val rule = createComposeRule(StandardTestDispatcher())

    @Test
    fun card_exposesLocalizedDescriptionAndDeliversClick() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val card = StoryCard(
            id = 42L,
            type = StoryCardType.ALBUMS,
            title = "Camera",
            subtitle = "3 items",
        )
        var clickedCardId: Long? = null

        rule.setContent {
            MaterialTheme {
                StoryCardsRow(cards = listOf(card), onCardClick = { clickedCardId = it.id })
            }
        }

        rule.onNodeWithTag(storyCardTag(card.id))
            .assertIsDisplayed()
            .assertHasClickAction()
            .assertContentDescriptionEquals(
                listOf(
                    context.getString(R.string.story_type_albums),
                    card.title,
                    card.subtitle,
                ).joinToString(". ")
            )
            .performClick()

        rule.runOnIdle { assertEquals(card.id, clickedCardId) }
    }
}
