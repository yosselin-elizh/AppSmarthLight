package com.yosselin.appsmarthlight

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yosselin.appsmarthlight.navigation.AppNavigation
import com.yosselin.appsmarthlight.ui.theme.AppSmarthLightTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class NavigationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
        composeTestRule.setContent {
            AppSmarthLightTheme {
                AppNavigation()
            }
        }
    }

    @Test
    fun splashToWelcomeToLoginToHomeNavigation() {
        // Splash screen navigates to Welcome screen after a delay
        composeTestRule.waitUntil(3000) { true }

        // On WelcomeScreen, click "Comenzar" to navigate to LoginScreen
        composeTestRule.onNodeWithText("Comenzar").performClick()

        // On LoginScreen, enter credentials and click "Ingresar"
        composeTestRule.onNodeWithText("Usuario").performClick()
        composeTestRule.onNodeWithText("Usuario").performTextInput("testuser")
        composeTestRule.onNodeWithText("Contraseña").performClick()
        composeTestRule.onNodeWithText("Contraseña").performTextInput("password")
        composeTestRule.onNodeWithText("Registrar").performClick() // Register first
        composeTestRule.waitUntil(3000) { true } // Wait for registration

        composeTestRule.onNodeWithText("Ingresar").performClick()

        // Verify we are on the HomeScreen
        composeTestRule.onNodeWithText("SmartLight").assertExists()
    }
}