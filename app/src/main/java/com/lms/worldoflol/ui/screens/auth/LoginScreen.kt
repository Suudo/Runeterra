package com.lms.worldoflol.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lms.worldoflol.R
import com.lms.worldoflol.common.NoInternetConnectionScreen
import com.lms.worldoflol.common.ErrorType
import com.lms.worldoflol.common.RuneterraBottomSheet
import com.lms.worldoflol.common.RuneterraContent
import com.lms.worldoflol.domain.model.remote.Summoner
import com.lms.worldoflol.ui.screens.auth.components.*
import com.lms.worldoflol.ui.screens.auth.components.SelectRegionButton
import com.lms.worldoflol.ui.theme.textStyle
import com.lms.worldoflol.ui.theme.textStyle24
import com.lms.worldoflol.utils.backgroundWithBorder
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalLifecycleComposeApi::class, ExperimentalAnimationApi::class
)
@Composable
fun LoginScreen(
    navigateMainScreen: (summoner: Summoner) -> Unit
) {
    val viewModel = hiltViewModel<LoginViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LoginContent(
        state = state,
        onEvent = viewModel::onEvent,
        onContinue = { navigateMainScreen(it) }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LoginContent(
    state: LoginState,
    onEvent: (LoginEvent) -> Unit,
    onContinue: (Summoner) -> Unit
) {

    val coroutineScope = rememberCoroutineScope()
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.Expanded },
        skipHalfExpanded = true
    )

    var selectedRegion by remember {
        mutableStateOf("")
    }

    var summonerName by remember {
        mutableStateOf("")
    }

    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp),
        sheetContent = {
            RegionsBottomSheet(
                onCloseClick = { coroutineScope.launch { modalSheetState.hide() } },
                onSelectRegion = {
                    coroutineScope.launch { modalSheetState.hide() }
                    selectedRegion = it
                }
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentHeight(),
                contentScale = ContentScale.FillBounds,
                painter = painterResource(id = R.drawable.ic_login_header_background),
                contentDescription = "login_header_background"
            )
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color(0x00242731),
                                Color(0xFF242731)
                            ),
                            endY = with(LocalDensity.current) {
                                188.dp.toPx()
                            }
                        )
                    )
            )
        }
    }
}

@Composable
fun WelcomeContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f)
            .padding(top = 92.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WelcomeText()
        Spacer(Modifier.height(50.dp))
//        WelcomeUserInputs()
    }
}



@Composable
fun RegionsBottomSheet(
    onCloseClick: () -> Unit,
    onSelectRegion: (regionName: String) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .backgroundWithBorder(
                borderGradientColors = arrayListOf(0xFFCA9D4B, 0x80242731),
                borderWidth = 0.5.dp
            )
    ) {
        RegionsBottomSheetHeader(
            Modifier
                .fillMaxWidth()
                .height(75.dp),
            onCloseClick = { onCloseClick.invoke() }
        )
        RegionsBottomSheetList(onRegionClick = {
            onSelectRegion.invoke(it.name)
        })

        Spacer(modifier = Modifier.height(35.dp))
    }
}

@Composable
fun RegionsBottomSheetHeader(modifier: Modifier, onCloseClick: () -> Unit) {
    Box(modifier = modifier) {
        Image(
            painter = painterResource(id = R.drawable.ic_close_button),
            contentDescription = "close",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clip(RoundedCornerShape(10.dp))
                .clickable { onCloseClick.invoke() }
                .padding(20.dp)
        )
        Text(
            text = "Select Region",
            style = textStyle24(color = 0xFFF6C97F),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
                .align(Alignment.BottomCenter)
        )
    }
    Divider(modifier = Modifier.fillMaxWidth(), thickness = 0.5.dp, color = Color(0x33CA9D4B))
}

@Composable
fun RegionsBottomSheetList(onRegionClick: (region: Regions) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        userScrollEnabled = false
    ) {
        items(Regions.size, key = { it }) {
            RegionItem(item = Regions.list[it]) { region ->
                onRegionClick.invoke(region)
            }
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginContent(
        state = LoginState(),
        onEvent = {},
        onContinue = {}
    )
}