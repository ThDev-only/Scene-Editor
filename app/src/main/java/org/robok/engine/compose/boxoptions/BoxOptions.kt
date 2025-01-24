package org.robok.engine.compose.boxoptions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.robok.engine.R
import org.robok.engine.model.Option

val options = remember {
  listOf(
    Option(icon = painterResource(id = R.drawable.ic_launcher_foreground), text = "Option 1"),
    Option(icon = painterResource(id = R.drawable.ic_launcher_foreground), text = "Option 2"),
    Option(icon = painterResource(id = R.drawable.ic_launcher_foreground), text = "Option 3"),
    Option(icon = painterResource(id = R.drawable.ic_launcher_foreground), text = "Option 4"),
    Option(icon = painterResource(id = R.drawable.ic_launcher_foreground), text = "Option 5"),
    Option(icon = painterResource(id = R.drawable.ic_launcher_foreground), text = "Option 6"),
  )
}

@Composable
fun OptionContainer(option: Option) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    modifier = Modifier.padding(8.dp).width(100.dp).height(100.dp),
  ) {
    Icon(painter = option.icon, contentDescription = option.text, modifier = Modifier.size(40.dp))
    Spacer(modifier = Modifier.height(8.dp))
    Text(text = option.text, color = Color.Black)
  }
}

@Composable
fun OptionGrid(options: List<Option>) {
  val rows = options.chunked(2) // Divide a lista em sublistas com 2 elementos (duas colunas)

  Column {
    rows.forEach { row ->
      Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
        row.forEach { option -> OptionContainer(option = option) }
      }
    }
  }
}