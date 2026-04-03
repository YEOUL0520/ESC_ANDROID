package com.example.myapplication

import android.os.Bundle    // 액티비티를 만들기 위해 필요
// 앱의 기본 구성요소를 가져옵니다
import androidx.activity.ComponentActivity // 액티비티를 만들기 위해 필요
import androidx.activity.compose.setContent // 액티비티의 레이아웃을 설정하는데 사용
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image // 이미지 표시용
import androidx.compose.ui.res.painterResource // 리소스 불러오기용
import androidx.compose.foundation.layout.Box // 겹치기 위한 컨테이너
import androidx.compose.ui.layout.ContentScale // 이미지 채우기 방식 설정
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 앱의 테마를 설정합니다 (기본 설정)
            MaterialTheme {
                // 배경색을 채운 화면 컨테이너
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyScreen() // 우리가 만든 화면 함수 호출
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyScreen() {
    val images = listOf(
        R.drawable.img1,
        R.drawable.img2,
        R.drawable.img3,
        R.drawable.img4,
        R.drawable.img5,
        R.drawable.img6,
        R.drawable.img7,
        R.drawable.img8,
        R.drawable.img9,
        R.drawable.img10
    )

    // 2. 페이지 상태 관리 (몇 번째 장인지 기억함)
    val pagerState = rememberPagerState(pageCount = { images.size })

    Box(modifier = Modifier.fillMaxSize()) {
        // 3. 가로로 드래그해서 넘기는 페이저
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            // 각 페이지에 보여줄 이미지
            Image(
                painter = painterResource(id = images[page]),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop // 화면 꽉 차게
            )
        }

        // 4. 화면 하단에 현재 몇 번째 페이지인지 표시 (선택 사항)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 50.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${pagerState.currentPage + 1} / ${images.size}",
                color = Color.White,
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
    }

}