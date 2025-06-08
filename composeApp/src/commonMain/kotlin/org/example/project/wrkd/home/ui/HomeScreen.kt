package org.example.project.wrkd.home.ui

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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.example.project.wrkd.core.models.app.DayPlanAppModel
import org.example.project.wrkd.core.models.app.ExercisePlanInfoAppModel
import org.example.project.wrkd.core.ui.compose.AppTheme
import org.example.project.wrkd.home.domain.WorkoutListSection
import org.example.project.wrkd.home.domain.WorkoutSessionInfo
import org.example.project.wrkd.utils.rememberTimeUtils
import org.jetbrains.compose.resources.painterResource
import wrkd.composeapp.generated.resources.Res
import wrkd.composeapp.generated.resources.ic_cross_in_circle

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

        if (state.workoutInfoSections != null) {
            workoutInfoSections(
                sections = state.workoutInfoSections,
                horizontalPadding = horizontalPadding,
                sendIntent = sendIntent
            )
        }

        item {
            Spacer(Modifier.height(AppTheme.dimens.baseScreenTabScreenBottomSpace))
        }
    }
}

fun LazyListScope.workoutInfoSections(
    sections: List<WorkoutListSection>,
    horizontalPadding: Dp,
    sendIntent: (HomeIntent) -> Unit
) {
    if (sections.isEmpty()) {
        item {
            WorkoutSectionEmptyComponent(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppTheme.dimens.workoutSectionEmptyHeight)
                    .padding(
                        horizontal = horizontalPadding,
                        vertical = AppTheme.dimens.medium1
                    )
                    .clickable {
                        sendIntent.invoke(HomeIntent.AddWorkoutClickedIntent)
                    }
            )
        }
    } else {
        sections.forEach {
            item {
                Text(
                    text = it.title,
                    color = AppTheme.color.black87,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = horizontalPadding,
                            vertical = AppTheme.dimens.medium1
                        ),
                    fontWeight = FontWeight.ExtraBold,
                    style = AppTheme.typography.h5
                )
            }

            itemsIndexed(it.workoutSessionsList) { index, sessionInfo ->
                WorkoutSectionSessionCard(
                    data = sessionInfo,
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = horizontalPadding
                        )
                        .padding(
                            top = if (index != 0) AppTheme.dimens.medium1 else AppTheme.dimens.small0
                        )
                )
            }
        }
    }
}

@Composable
fun WorkoutSectionSessionCard(
    data: WorkoutSessionInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(AppTheme.corners.homeInfoCard),
        elevation = AppTheme.dimens.small2
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(
                    horizontal = AppTheme.dimens.medium1,
                    vertical = AppTheme.dimens.medium1
                )
        ) {
            WorkoutSectionCardHeader(
                title = data.workoutName,
                startedAt = data.startedAt
            )

            Spacer(Modifier.height(AppTheme.dimens.small3))

            WorkoutSectionExercisesList(
                list = data.exercises,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun WorkoutSectionExercisesList(
    list: List<ExercisePlanInfoAppModel>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        val style = AppTheme.typography.body2
        val textColor = AppTheme.color.black60

        list.forEachIndexed { index, exercise ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = AppTheme.dimens.small1
                    )
                    .padding(
                        top = if (index != 0) AppTheme.dimens.small1 else AppTheme.dimens.small0
                    )
            ) {
                Text(
                    text = exercise.name,
                    style = style,
                    color = textColor,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = exercise.setSummary(),
                    style = style,
                    color = textColor,
                    modifier = Modifier,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun ExercisePlanInfoAppModel.setSummary(): String {
    val setCount = sets.size
    val minReps = sets.minBy { it.repsCount }.repsCount
    val maxReps = sets.maxBy { it.repsCount }.repsCount

    return if (minReps == maxReps) {
        "$setCount x $minReps reps"
    } else {
        "$setCount x ($minReps - $maxReps) reps"
    }
}

@Composable
fun WorkoutSectionCardHeader(
    title: String,
    startedAt: Long,
    modifier: Modifier = Modifier
) {
    val timeUtils = rememberTimeUtils()
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            color = AppTheme.color.black87,
            style = AppTheme.typography.h6,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Box(
            modifier = Modifier
                .background(
                    color = AppTheme.color.lightGrey,
                    shape = CircleShape
                )
                .border(
                    width = AppTheme.dimens.small1,
                    color = AppTheme.color.black10,
                    shape = CircleShape
                )
                .padding(
                    vertical = AppTheme.dimens.small2,
                    horizontal = AppTheme.dimens.medium1
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = timeUtils.dateAndTime(startedAt),
                color = AppTheme.color.black60,
                style = AppTheme.typography.subtitle2,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun WorkoutSectionEmptyComponent(
    title: String = "No workout sessions logged",
    subtitle: String = "Add the workout to get the week started",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = AppTheme.color.emptySectionBackground,
                shape = RoundedCornerShape(AppTheme.corners.homeInfoCard)
            )
            .padding(AppTheme.dimens.medium1),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            style = AppTheme.typography.h6,
            color = AppTheme.color.black87,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(AppTheme.dimens.small3))

        Text(
            text = subtitle,
            textAlign = TextAlign.Center,
            style = AppTheme.typography.subtitle1,
            color = AppTheme.color.black60,
            fontWeight = FontWeight.Normal
        )
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
                    onAddClick = { sendIntent.invoke(HomeIntent.AddWorkoutClickedIntent) },
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
                .fillMaxWidth(0.25f)
                .aspectRatio(1f),
            tint = AppTheme.color.white,
            contentDescription = null,
        )

        Spacer(Modifier.height(AppTheme.dimens.small3))

        Text(
            text = "No workout logged yet",
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