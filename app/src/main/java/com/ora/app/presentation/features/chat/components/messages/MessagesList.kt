package com.ora.app.presentation.features.chat.components.messages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ora.app.domain.model.FeedbackState
import com.ora.app.domain.model.Interaction
import com.ora.app.presentation.theme.Dimensions

@Composable
fun MessagesList(
    interactions: List<Interaction>,
    listState: LazyListState,
    onThumbsUp: (Int) -> Unit,
    onThumbsDown: (Int) -> Unit,
    onCopy: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(Dimensions.paddingScreen),
        verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMd)
    ) {
        itemsIndexed(interactions, key = { index, _ -> index }) { index, interaction ->
            MessagePair(
                interaction = interaction,
                onThumbsUp = { onThumbsUp(index) },
                onThumbsDown = { onThumbsDown(index) },
                onCopy = { onCopy(interaction.assistantResponse) }
            )
        }
    }
}
