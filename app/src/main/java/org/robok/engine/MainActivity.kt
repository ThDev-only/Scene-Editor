package org.robok.engine

/*
 *  This file is part of Robok © 2024.
 *
 *  Robok is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Robok is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with Robok.  If not, see <https://www.gnu.org/licenses/>.
 */

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.badlogic.gdx.math.Vector2
import org.robok.engine.compose.components.animation.ExpandAndShrink
import org.robok.engine.compose.components.gdx.GDXState
import org.robok.engine.compose.components.gdx.GDXWidget
import org.robok.engine.compose.components.gdx.rememberGDXState
import org.robok.engine.compose.components.options.OptionsGrid
import org.robok.engine.compose.components.options.getOptions
import org.robok.engine.compose.theme.AppTheme
import org.robok.engine.feature.scene.editor.interfaces.EmptyObjectActionListener
import org.robok.engine.feature.scene.editor.interfaces.ObjectListener
import org.robok.engine.feature.scene.editor.objects.SceneObject
import org.robok.engine.viewmodel.GDXViewModel

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.fadeIn
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import kotlinx.coroutines.delay


class MainActivity : AppCompatActivity(), AndroidFragmentApplication.Callbacks {

  private val gdxViewModel: GDXViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent { AppTheme { Screen(savedInstanceState) } }
  }

  /** Hide phone ui to better experience */
  private fun hideSystemUI() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      window.setDecorFitsSystemWindows(false)
      window.insetsController?.let { controller ->
        controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
      }
    } else {
      @Suppress("DEPRECATION")
      window.decorView.systemUiVisibility =
        (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }
  }

  @Composable
  fun Screen(savedInstanceState: Bundle?) {
    SideEffect { hideSystemUI() }
    val state = rememberGDXState()
    GDXScreen(state = state)
    FadingTitle("Robok Editor")

    state.objectListener =
      object : ObjectListener {
        override fun onTap(x: Float, y: Float, count: Int, button: Int) {}

        override fun onObjectClick(sceneObject: SceneObject, x: Float, y: Float) {}

        override fun onZoom(initialDistance: Float, distance: Float) {}

        override fun onPinch(
          initialPointer1: Vector2,
          initialPointer2: Vector2,
          pointer1: Vector2,
          pointer2: Vector2,
        ) {}

        override fun onTouchDown(x: Float, y: Float, count: Int, button: Int) {
          gdxViewModel.setOptionsOpen(false)
        }
      }
    state.objectActionListener = state.fragment?.sceneEditorView ?: EmptyObjectActionListener()
  }

  @Composable
  fun GDXScreen(state: GDXState) {
    Box(modifier = Modifier.fillMaxSize()) {
      GDXWidget(modifier = Modifier.fillMaxSize(), state = state)

      ExpandAndShrink(
        modifier = Modifier.align(Alignment.CenterEnd),
        visible = gdxViewModel.isOptionsOpen,
        vertically = false,
      ) {
        OptionsBox(modifier = Modifier.padding(10.dp))
      }

      ExpandAndShrink(
        modifier = Modifier.align(Alignment.TopEnd),
        visible = !gdxViewModel.isOptionsOpen,
        vertically = false,
      ) {
        IconButton(
          onClick = { gdxViewModel.setOptionsOpen(!gdxViewModel.isOptionsOpen) },
          modifier = Modifier.size(80.dp).align(Alignment.TopEnd),
        ) {
          Image(
            imageVector = Icons.Rounded.MoreVert,
            contentDescription = stringResource(R.string.common_word_more),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
          )
        }
      }
    }
  }

  @Composable
  fun OptionsBox(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val options = getOptions(context)
    Surface(
      modifier = modifier,
      shape = RoundedCornerShape(20.dp),
      color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
      OptionsGrid(
        options = options,
        modifier = Modifier.padding(start = 5.dp, end = 5.dp),
        onOptionClick = { optionModel ->
          // do something
        }
      )
    }
  }

@Composable
fun FadingTitle(title: String) {
    var isVisible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(2500)
        isVisible = false
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(durationMillis = 1000)),
        exit = fadeOut(animationSpec = tween(durationMillis = 1000))
    ) {
        Text(
            text = title,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(20.dp)
        )
    }
}

  @Composable
  fun ObjectModelsBox(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val options = getOptions(context)
    Surface(
      modifier = Modifier.width(300.dp).height(300.dp),
      shape = RoundedCornerShape(20.dp),
      color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
      OptionsGrid(
        options = options,
        modifier = Modifier.padding(start = 5.dp, end = 5.dp),
        onOptionClick = { optionModel ->
          // do something
        }
      )
    }
  }

  override fun exit() {
    finish()
  }
}
