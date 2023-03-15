package com.realityexpander.composeimageslider

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.realityexpander.composeimageslider.ui.theme.AUTO_SLIDE_DURATION
import com.realityexpander.composeimageslider.ui.theme.ComposeImageSliderTheme
import com.realityexpander.composeimageslider.ui.theme.IndicatorSelectedColor
import com.realityexpander.composeimageslider.ui.theme.IndicatorUnselectedColor
import kotlinx.coroutines.launch
import kotlin.concurrent.fixedRateTimer

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeImageSliderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //Greeting("Android")

                    val images = listOf(
                        "https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__340.jpg",
                        "https://cdn.pixabay.com/photo/2015/12/01/20/28/road-1072823__340.jpg",
                        "https://cdn.pixabay.com/photo/2022/12/04/16/17/leaves-7634894_1280.jpg",
                        "https://cdn.pixabay.com/photo/2023/03/08/21/03/duck-7838724_1280.jpg"
                    )

                    Card(
                        modifier = Modifier.padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        AutoSlidingCarousel(
                            itemsCount = images.size,
                            itemContent = { index ->
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(images[index])
                                        .build(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.height(200.dp)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeImageSliderTheme {
        Greeting("Android")
    }
}

@Composable
fun IndicatorDot(
    modifier: Modifier = Modifier,
    size: Dp,
    color: Color
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
fun DotsIndicator(
    modifier: Modifier = Modifier,
    totalDots: Int,
    selectedIndex: Int,
    selectedColor: Color = IndicatorSelectedColor /* Color.Yellow */,
    unSelectedColor: Color = IndicatorUnselectedColor /* Color.Gray */,
    dotSize: Dp
) {
    LazyRow(
        modifier = modifier
            .wrapContentWidth()
            .wrapContentHeight()
    ) {
        items(totalDots) { index ->
            IndicatorDot(
                color = if (index == selectedIndex) selectedColor else unSelectedColor,
                size = dotSize
            )

            if (index != totalDots - 1) {
                Spacer(modifier = Modifier.padding(horizontal = 2.dp))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AutoSlidingCarousel(
    modifier: Modifier = Modifier,
    autoSlideDuration: Long = AUTO_SLIDE_DURATION,
    pagerState: PagerState = remember { PagerState() },
    itemsCount: Int,
    itemContent: @Composable (index: Int) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val timer = remember {
        fixedRateTimer(
            initialDelay = autoSlideDuration,
            period = autoSlideDuration
        ) {
            coroutineScope.launch {
                pagerState.animateScrollToPage((pagerState.currentPage + 1) % itemsCount)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { timer.cancel() }
    }

    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        HorizontalPager(pageCount = itemsCount, state = pagerState) { page ->
            itemContent(page)
        }

        // you can remove the surface in case you don't want
        // the transparant bacground
        Surface(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .align(Alignment.BottomCenter),
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.5f)
        ) {
            DotsIndicator(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                totalDots = itemsCount,
                selectedIndex = pagerState.targetPage,
                dotSize = 8.dp
            )
        }
    }
}
