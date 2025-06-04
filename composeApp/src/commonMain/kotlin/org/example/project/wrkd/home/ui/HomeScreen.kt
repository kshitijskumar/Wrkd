package org.example.project.wrkd.home.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import kotlinx.coroutines.flow.Flow
import org.example.project.wrkd.core.navigation.scenes.AppScenes
import org.example.project.wrkd.core.ui.compose.AppTheme
import org.example.project.wrkd.di.core.inject
import org.jetbrains.compose.resources.painterResource
import wrkd.composeapp.generated.resources.Res
import wrkd.composeapp.generated.resources.*

@Composable
fun HomeScreen(
    vm: HomeViewModel
) {
    val state by vm.state.collectAsStateWithLifecycle(LocalLifecycleOwner.current)

    HomeScreenContent(
        state = state,
        sendIntent = vm::processIntent
    )
}

@Composable
private fun HomeScreenContent(
    state: HomeState,
    sendIntent: (HomeIntent) -> Unit
) {
    val horizontalPadding = AppTheme.dimens.medium1
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = state.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = AppTheme.dimens.small3,
                        bottom = AppTheme.dimens.medium2,
                        start = horizontalPadding,
                        end = horizontalPadding
                    ),
                color = AppTheme.color.black87,
                style = AppTheme.typography.h4,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            HomeInfoCardSection(
                sectionItem = state.homeInfoCards,
                sendIntent = sendIntent,
                modifier = Modifier
                    .padding(horizontal = horizontalPadding)
            )
        }

        item {
            Button(
                onClick = {
                    sendIntent.invoke(HomeIntent.DummyClickIntent)
                },
                content = {
                    Text("Add")
                }
            )
        }
    }
}

@Composable
fun HomeInfoCardSection(
    sectionItem: List<HomeInfoCard>,
    sendIntent: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val gridColumnsCount = 2
    val aspectRatioCalculator: (chunkSize: Int) -> Float = { chunkSize ->
        if (chunkSize == gridColumnsCount) {
            1f
        } else {
            0.5f
        }
    }
    Column(
        modifier = modifier
    ) {
        val sectionsGrid by derivedStateOf {
            sectionItem.chunked(gridColumnsCount)
        }

        sectionsGrid.forEach { chunk ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                chunk.forEachIndexed { index, cardData ->
                    HomeInfoCardComponent(
                        data = cardData,
                        sendIntent = sendIntent,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(aspectRatioCalculator.invoke(chunk.size))
                    )
                    if (index != chunk.size - 1) {
                        Spacer(Modifier.width(AppTheme.dimens.medium1))
                    }
                }
            }
        }
    }
}

@Composable
fun HomeInfoCardComponent(
    data: HomeInfoCard,
    sendIntent: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    when(data) {
        is HomeInfoCard.CurrentDayWorkoutDetails -> {
            Card(
                modifier = modifier,
                shape = RoundedCornerShape(AppTheme.corners.homeInfoCard)
            ) {
                CurrentDayWorkoutComponent(
                    data = data,
                    onAddClick = { sendIntent.invoke(HomeIntent.DummyClickIntent) },
                    onWorkoutClick = {  }
                )
            }
        }
        is HomeInfoCard.WeeklyWorkoutSummary -> {
            Card(
                modifier = modifier,
                shape = RoundedCornerShape(AppTheme.corners.homeInfoCard)
            ) {
                WeeklyWorkoutSummaryComponent(
                    data = data
                )
            }
        }
    }
}

@Composable
fun WeeklyWorkoutSummaryComponent(
    data: HomeInfoCard.WeeklyWorkoutSummary,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.color.secondaryCardColor)
            .padding(AppTheme.dimens.small3),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = data.numberOfDaysWorkedOut.toFloat() / data.totalPlannedDays.toFloat(),
                color = AppTheme.color.white,
                modifier = Modifier
                    .fillMaxSize(),
                backgroundColor = AppTheme.color.white.copy(alpha = 0.3f),
                strokeWidth = AppTheme.dimens.medium1,
                strokeCap = StrokeCap.Round
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${data.numberOfDaysWorkedOut} / ${data.totalPlannedDays}",
                    modifier = Modifier,
                    color = AppTheme.color.white,
                    style = AppTheme.typography.h4,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Total\n${data.formattedDuration}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = AppTheme.dimens.small1
                        ),
                    style = AppTheme.typography.subtitle2.copy(letterSpacing = 0.sp),
                    textAlign = TextAlign.Center,
                    color = AppTheme.color.white
                )
            }
        }
    }
}

@Composable
fun CurrentDayWorkoutComponent(
    data: HomeInfoCard.CurrentDayWorkoutDetails,
    onAddClick: () -> Unit,
    onWorkoutClick: (HomeInfoCard.CurrentDayWorkoutDetails.WorkoutInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.color.primaryCardColor)
            .padding(AppTheme.dimens.small3),
    ) {
        Text(
            text = data.today,
            modifier = Modifier.fillMaxWidth(),
            color = AppTheme.color.white,
            style = AppTheme.typography.h6,
            fontWeight = FontWeight.SemiBold
        )


        if (data.info.isNotEmpty()) {
            CurrentDayWorkoutPager(
                items = data.info,
                itemClick = { onWorkoutClick.invoke(it) },
                modifier = Modifier
                    .weight(1f)
                    .padding(top = AppTheme.dimens.small3)
            )
        } else {
            CurrentDayEmptyWorkoutState(
                modifier = Modifier.fillMaxSize(),
                onClick = onAddClick
            )
        }
    }
}

@Composable
fun CurrentDayEmptyWorkoutState(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clickable(
                interactionSource = null,
                indication = null,
                onClick = onClick
            )
            .padding(AppTheme.dimens.medium1),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_cross_in_circle),
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .aspectRatio(1f),
            tint = AppTheme.color.white,
            contentDescription = null,
        )

        Spacer(Modifier.height(AppTheme.dimens.small3))

        Text(
            text = "Click to log workout",
            modifier = Modifier.fillMaxWidth(),
            color = AppTheme.color.white,
            textAlign = TextAlign.Center,
            style = AppTheme.typography.subtitle1
        )
    }
}

@Composable
fun CurrentDayWorkoutPager(
    items: List<HomeInfoCard.CurrentDayWorkoutDetails.WorkoutInfo>,
    itemClick: (HomeInfoCard.CurrentDayWorkoutDetails.WorkoutInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(
        pageCount = { items.size }
    )
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) {
            val item = items[it]
            CurrentDayWorkoutContent(
                info = item,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        onClick = { itemClick.invoke(item) },
                        indication = null,
                        interactionSource = null
                    )
            )
        }

        CurrentDayWorkoutPagerIndicatorSection(
            total = pagerState.pageCount,
            currentPage = pagerState.currentPage,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = AppTheme.dimens.small1)
        )
    }
}

@Composable
fun CurrentDayWorkoutPagerIndicatorSection(
    total: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    val isFirstSelected = currentPage == 0
    val isLastSelected = currentPage == (total - 1)
    val isMiddleSelected = !isFirstSelected && !isLastSelected
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // first circle, show this always
        CurrentDayWorkoutPagerIndicator(
            isSelected = isFirstSelected
        )
        // if there are more than 2 items, start showing middle
        // the reason this is >2 and not >1, coz if there are only 2 items, then second item will be the last,
        // so we dont need middle selection
        if (total > 2) {
            Spacer(Modifier.width(AppTheme.dimens.small2))
            CurrentDayWorkoutPagerIndicator(
                isSelected = isMiddleSelected
            )
        }
        // if there are more than 1 items, show the last circle
        if (total > 1) {
            Spacer(Modifier.width(AppTheme.dimens.small2))
            CurrentDayWorkoutPagerIndicator(
                isSelected = isLastSelected
            )
        }
    }
}

@Composable
fun CurrentDayWorkoutPagerIndicator(
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(AppTheme.dimens.small3)
            .background(
                color = if (isSelected)
                    AppTheme.color.indicatorSelectedColor
                else
                    AppTheme.color.indicatorUnselectedColor,
                shape = CircleShape
            )
    )
}

@Composable
fun CurrentDayWorkoutContent(
    info: HomeInfoCard.CurrentDayWorkoutDetails.WorkoutInfo,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = info.workoutName,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = AppTheme.typography.subtitle1,
            color = AppTheme.color.white,
            fontWeight = FontWeight.SemiBold
        )

        Column {
            val exerciseText = if (info.totalExercises > 1) "Exercises" else "Exercise"
            Text(
                text = "${info.totalExercises} $exerciseText",
                modifier = Modifier
                    .fillMaxWidth(),
                color = AppTheme.color.white,
                style = AppTheme.typography.subtitle2,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(AppTheme.dimens.small1))

            Text(
                text = "For ${info.formattedDuration}",
                modifier = Modifier
                    .fillMaxWidth(),
                color = AppTheme.color.white,
                style = AppTheme.typography.subtitle2,
                fontWeight = FontWeight.Normal
            )
        }
    }
}