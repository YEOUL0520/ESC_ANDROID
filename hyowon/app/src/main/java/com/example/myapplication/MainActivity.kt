package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth // Firebase 추가
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.foundation.background

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyScreen()
                }
            }
        }
    }
}

@Composable
fun MyScreen() {
    // Firebase 인스턴스 가져오기
    val auth = FirebaseAuth.getInstance()
    // 현재 로그인된 사용자가 있으면 바로 메인으로, 없으면 로그인 화면으로
    var currentScreen by remember {
        mutableStateOf(if (auth.currentUser != null) "menu" else "login")
    }
    // 선택된 이미지 정보를 저장할 상태(상세 보기를 위해)
    var selectedImageRes by remember { mutableStateOf<Int?>(null) }

    when (currentScreen) {
        "login" -> {
            LoginScreen(onAuthSuccess = { currentScreen = "menu" })
        }
        "menu" -> {
            GalleryMenuScreen(
                onImageClick = { resId ->
                    selectedImageRes = resId
                    currentScreen = "detail"
                },
                onLogout = {
                    auth.signOut()
                    currentScreen = "login"
                }
            )
        }
        "detail" -> {
            // 그리드에서 사진을 눌렀을 때 보여줄 상세 화면
            ImageDetailScreen(
                initialImage = selectedImageRes ?: R.drawable.gemine1,
                onBack = { currentScreen = "menu" }
            )
        }
        "settings" -> {
            // 여기에 설정 화면 등 다른 화면을 추가할 수 있습니다.
            SettingsScreen(onBack = {currentScreen = "menu"})
        }
    }
}

@Composable
fun ImageDetailScreen(initialImage: Int, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Image(
            painter = painterResource(id = initialImage),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().align(Alignment.Center),
            contentScale = ContentScale.Fit // 사진 전체가 보이도록 설정
        )

        // 상단 뒤로가기 버튼
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_revert), // 기본 뒤로가기 아이콘
                contentDescription = "뒤로가기",
                tint = Color.White
            )
        }
    }
}
@Composable
fun GalleryMenuScreen(onImageClick: (Int) -> Unit, onLogout: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Place, contentDescription = null) }, // 사진 아이콘 대체
                    label = { Text("사진") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Menu, contentDescription = null) }, // 앨범 아이콘 대체
                    label = { Text("앨범") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onLogout,
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = null) },
                    label = { Text("로그아웃") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // 삼성 One UI 스타일의 큰 타이틀 공간
            Spacer(modifier = Modifier.height(50.dp))
            Text(
                text = if(selectedTab == 0) "사진" else "앨범",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            if (selectedTab == 0) {
                // 실제 사진 그리드 레이아웃
                val images = listOf(R.drawable.gemine1, R.drawable.gemine2, R.drawable.gemine1)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(images) { resId ->
                        Image(
                            painter = painterResource(id = resId),
                            contentDescription = null,
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clickable { onImageClick(resId) },
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            } else {
                // 앨범 탭 클릭 시 보여줄 내용
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("준비 중인 앨범 기능입니다.")
                }
            }
        }
    }
}

@Composable
fun LoginScreen(onAuthSuccess: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var pw by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("로그인해주세요.", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("이메일 주소") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = pw,
            onValueChange = { pw = it },
            label = { Text("비밀번호 (6자 이상)") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            // 로그인 버튼
            Button(
                onClick = {
                    if (email.isEmpty() || pw.isEmpty()) {
                        Toast.makeText(context, "정보를 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    isLoading = true
                    auth.signInWithEmailAndPassword(email, pw)
                        .addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) onAuthSuccess()  // 성공 시 메인으로 이동
                            else Toast.makeText(
                                context,
                                "로그인 실패: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show() // 실패 시 팝업 알림
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("로그인하기")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 회원가입 버튼
            OutlinedButton(
                onClick = {
                    if (email.isEmpty() || pw.isEmpty()) {
                        Toast.makeText(context, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                        return@OutlinedButton
                    }
                    isLoading = true
                    auth.createUserWithEmailAndPassword(email, pw)
                        .addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                Toast.makeText(context, "회원가입 성공!", Toast.LENGTH_SHORT).show()
                                onAuthSuccess()
                            } else {
                                Toast.makeText(
                                    context,
                                    "가입 실패: ${task.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("이 정보로 새로 가입하기")
            }
        }
    }
}

// 예시로 만든 설정 화면
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("설정 화면입니다.")
        Button(onClick = onBack) { Text("뒤로가기") }
    }
}