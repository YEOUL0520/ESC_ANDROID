package com.example.myapplication

import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        enableEdgeToEdge()
        setContent {
            MainScreen(this)
        }
    }
}



@Composable
fun MainScreen(mainActivity: MainActivity) {
    var message by remember{ mutableStateOf("안녕하세요")}
    var count by remember { mutableStateOf(0)}
    var buttonColor by remember { mutableStateOf(Color.Blue)}

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "클릭 횟수: $count", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            // 설정 조건
            onClick = {
                count++
                if (count == 100) {
                    buttonColor = Color.Blue
                    count = 0
                    message = "너무 많이 누르지 마세요"
                }
                if (count == 46) {
                    buttonColor = Color.Red
                    message = "내 학번 끝 2자리:46"
                } else {
                    message = "버튼 클릭됨"
                }
            },
            colors=buttonColors(containerColor=buttonColor)
        ) {
            Text(text = "클릭")
        }
    }
}
