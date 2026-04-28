package com.example.mungi0901

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Mainscreen()
                }
            }
        }


@Composable
fun Mainscreen(){
    var message by remember { mutableStateOf("안녕하세요")}
    var count by remember { mutableIntStateOf(0) }

    // 76번 눌렀을 때의 색상을 결정하는 변수
    val buttonColor = if (count >= 76) ButtonDefaults.buttonColors(containerColor = Color.Red) else ButtonDefaults.buttonColors()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment =
            Alignment.CenterHorizontally
    ){
        Text(
            text = message,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "클릭 횟수 : $count",
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
                val nextCount = count + 1

                if (nextCount >= 100) {
                    // 100이 되면 초기화 및 문구 변경
                    count = 0
                    message = "너무 많이 누르지 마세요!"
                } else {
                    count = nextCount
                    message = "버튼을 눌렀어요!"
                }
            },
                    colors = buttonColor
        ) {
            Text("클릭하기")
        }
    }

}