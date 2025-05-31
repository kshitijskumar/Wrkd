package org.example.project.wrkd.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import org.example.project.wrkd.core.navigation.scenes.AppScenes
import org.example.project.wrkd.di.core.inject

@Composable
fun HomeScreen(
    vm: HomeViewModel
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                vm.processIntent(HomeIntent.DummyClickIntent)
            },
            content = {
                Text("Dummy Click")
            }
        )
    }
}