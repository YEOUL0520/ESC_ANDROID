package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
        mutableStateOf(if (auth.currentUser != null) "main" else "login")
    }

    if (currentScreen == "login") {
        LoginScreen(
            onAuthSuccess = { currentScreen = "main" }
        )
    } else {
        ImagePagerScreen(
            onLogout = {
                auth.signOut()
                currentScreen = "login"
            }
        )
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
        modifier = Modifier.fillMaxSize().padding(24.dp),
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
                            else Toast.makeText(context, "로그인 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show() // 실패 시 팝업 알림
                        }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
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
                                Toast.makeText(context, "가입 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("이 정보로 새로 가입하기")
            }
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun ImagePagerScreen(onLogout: () -> Unit) {
    val images = listOf(R.drawable.gemine1, R.drawable.gemine2)
    val pagerState = rememberPagerState(pageCount = { images.size })

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            Image(
                painter = painterResource(id = images[page]),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // 로그아웃 버튼 추가
        Button(
            onClick = onLogout,
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.5f))
        ) {
            Text("로그아웃", color = Color.White)
        }

        Text(
            text = "드래그해서 넘겨보세요",
            color = Color.White,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 50.dp)
        )
    }
}