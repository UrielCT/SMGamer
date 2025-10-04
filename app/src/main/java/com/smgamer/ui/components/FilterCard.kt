package com.smgamer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.smgamer.R
import com.smgamer.ui.theme.CommonFontSizeXLarge
import com.smgamer.ui.theme.CommonPaddingDefault
import com.smgamer.ui.theme.CommonPaddingMicroMin
import com.smgamer.ui.theme.CommonPaddingMin
import com.smgamer.ui.theme.FilterImageSize

@Composable
fun FilterCard(
    modifier: Modifier = Modifier,
    name: String,
    imageRes: Int = R.drawable.icon_pc,
    navToFilteredPosts: () -> Unit
) {
    val context = LocalContext.current

    Card(
        onClick = { navToFilteredPosts() },
        modifier = modifier
            .padding(horizontal = CommonPaddingDefault, vertical = CommonPaddingMin),
        shape = RoundedCornerShape(CommonPaddingDefault),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = CommonPaddingMicroMin)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(CommonPaddingDefault)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageRes)
                    .crossfade(true)
                    .build(),
                contentDescription = name,
                modifier = Modifier
                    .size(FilterImageSize)
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = CircleShape
                    )
                    .padding(CommonPaddingMin)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(CommonPaddingDefault))

            Text(
                text = name,
                fontWeight = FontWeight.Bold,
                fontSize = CommonFontSizeXLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}