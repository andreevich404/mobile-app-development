package com.example.mycity.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mycity.R
import com.example.mycity.data.PlaceCategory
import com.example.mycity.ui.utils.MyCityContentType
import com.example.mycity.ui.utils.MyCityNavigationType

enum class MyCityScreen { Places, Detail }

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MyCityApp(
    windowSize: WindowWidthSizeClass,
    modifier: Modifier = Modifier,
    viewModel: MyCityViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val uiState by viewModel.uiState.collectAsState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = backStackEntry?.destination?.route ?: MyCityScreen.Places.name
    val isDetailScreen = currentScreen == MyCityScreen.Detail.name

    val navigationType: MyCityNavigationType
    val contentType: MyCityContentType
    when (windowSize) {
        WindowWidthSizeClass.Compact -> {
            navigationType = MyCityNavigationType.BOTTOM_NAVIGATION
            contentType = MyCityContentType.LIST_ONLY
        }
        WindowWidthSizeClass.Medium -> {
            navigationType = MyCityNavigationType.NAVIGATION_RAIL
            contentType = MyCityContentType.LIST_ONLY
        }
        WindowWidthSizeClass.Expanded -> {
            navigationType = MyCityNavigationType.PERMANENT_NAVIGATION_DRAWER
            contentType = MyCityContentType.LIST_AND_DETAIL
        }
        else -> {
            navigationType = MyCityNavigationType.BOTTOM_NAVIGATION
            contentType = MyCityContentType.LIST_ONLY
        }
    }

    // On wide screens the detail pane lives next to the list, so a separate detail screen is not needed.
    LaunchedEffect(contentType, isDetailScreen) {
        if (contentType == MyCityContentType.LIST_AND_DETAIL && isDetailScreen) {
            navController.popBackStack(MyCityScreen.Places.name, inclusive = false)
        }
    }

    val onCategorySelected = { category: PlaceCategory ->
        viewModel.selectCategory(category)
        navController.popBackStack(MyCityScreen.Places.name, inclusive = false)
        Unit
    }

    val appContent: @Composable () -> Unit = {
        Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            if (isDetailScreen) {
                                stringResource(uiState.selectedPlace.name)
                            } else {
                                stringResource(uiState.currentCategory.title) + " · " +
                                    stringResource(R.string.city_name)
                            }
                        )
                    },
                    navigationIcon = {
                        if (isDetailScreen) {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_arrow_back),
                                    contentDescription = stringResource(R.string.back_button)
                                )
                            }
                        }
                    }
                )
            },
            bottomBar = {
                if (navigationType == MyCityNavigationType.BOTTOM_NAVIGATION) {
                    NavigationBar {
                        PlaceCategory.entries.forEach { category ->
                            NavigationBarItem(
                                selected = uiState.currentCategory == category,
                                onClick = { onCategorySelected(category) },
                                icon = {
                                    Icon(
                                        painter = painterResource(category.icon),
                                        contentDescription = stringResource(category.title)
                                    )
                                },
                                label = { Text(stringResource(category.shortTitle), maxLines = 1) }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(modifier = Modifier.padding(innerPadding)) {
                if (navigationType == MyCityNavigationType.NAVIGATION_RAIL) {
                    NavigationRail {
                        PlaceCategory.entries.forEach { category ->
                            NavigationRailItem(
                                selected = uiState.currentCategory == category,
                                onClick = { onCategorySelected(category) },
                                icon = {
                                    Icon(
                                        painter = painterResource(category.icon),
                                        contentDescription = stringResource(category.title)
                                    )
                                },
                                label = { Text(stringResource(category.shortTitle), maxLines = 1) }
                            )
                        }
                    }
                }
                NavHost(
                    navController = navController,
                    startDestination = MyCityScreen.Places.name
                ) {
                    composable(route = MyCityScreen.Places.name) {
                        if (contentType == MyCityContentType.LIST_AND_DETAIL) {
                            PlacesListAndDetail(
                                places = uiState.places,
                                selectedPlace = uiState.selectedPlace,
                                onPlaceClick = viewModel::selectPlace
                            )
                        } else {
                            PlacesListOnly(
                                places = uiState.places,
                                onPlaceClick = { place ->
                                    viewModel.selectPlace(place)
                                    navController.navigate(MyCityScreen.Detail.name)
                                }
                            )
                        }
                    }
                    composable(route = MyCityScreen.Detail.name) {
                        PlaceDetail(place = uiState.selectedPlace)
                    }
                }
            }
        }
    }

    if (navigationType == MyCityNavigationType.PERMANENT_NAVIGATION_DRAWER) {
        PermanentNavigationDrawer(
            drawerContent = {
                PermanentDrawerSheet(Modifier.width(240.dp)) {
                    PlaceCategory.entries.forEach { category ->
                        NavigationDrawerItem(
                            selected = uiState.currentCategory == category,
                            onClick = { onCategorySelected(category) },
                            icon = {
                                Icon(
                                    painter = painterResource(category.icon),
                                    contentDescription = stringResource(category.title)
                                )
                            },
                            label = { Text(stringResource(category.title)) },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxHeight()
        ) {
            appContent()
        }
    } else {
        appContent()
    }
}
