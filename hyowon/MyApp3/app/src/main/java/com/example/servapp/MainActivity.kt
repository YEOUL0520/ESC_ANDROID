package com.example.servapp;

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class MainActivity : ComponentActivity() {

    private val client = OkHttpClient()

    /*
     * Android 에뮬레이터에서 내 컴퓨터의 localhost 서버에 접속할 때는
     * 127.0.0.1 또는 localhost가 아니라 10.0.2.2를 사용해야 합니다.
     *
     * 서버 실행 주소:
     * http://127.0.0.1:8000
     *
     * 앱에서 접근하는 주소:
     * http://10.0.2.2:8000
     */
    private val baseUrl = "https://esc-android.onrender.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface {
                    ServerConnectScreen()
                }
            }
        }
    }

    @Composable
    fun ServerConnectScreen() {
        var inputText by remember {
            mutableStateOf("검찰입니다. 계좌 확인을 위해 인증번호를 알려주세요.")
        }

        var resultText by remember {
            mutableStateOf("아직 서버 요청 전입니다.")
        }

        val scope = rememberCoroutineScope()
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp)
        ) {
            Text(
                text = "Python 서버 연결 실습",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "1. 서버 상태 확인",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    scope.launch {
                        resultText = "서버 상태 확인 중..."
                        resultText = checkServerHealth()
                    }
                }
            ) {
                Text(text = "GET /health 요청")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "2. 문장 분석 요청",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("서버로 보낼 문장") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    scope.launch {
                        resultText = "서버에 분석 요청 중..."
                        resultText = sendAnalyzeRequest(inputText)
                    }
                }
            ) {
                Text(text = "POST /analyze 요청")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "서버 응답",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = resultText)
        }
    }

    private suspend fun checkServerHealth(): String {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("$baseUrl/health")
                    .get()
                    .build()

                client.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string()

                    if (!response.isSuccessful) {
                        "요청 실패\ncode: ${response.code}\nbody: $responseBody"
                    } else {
                        responseBody ?: "응답이 비어 있습니다."
                    }
                }
            } catch (e: Exception) {
                "에러 발생: ${e.message}"
            }
        }
    }

    private suspend fun sendAnalyzeRequest(text: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val jsonObject = JSONObject()
                jsonObject.put("text", text)

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = jsonObject.toString().toRequestBody(mediaType)

                val request = Request.Builder()
                    .url("$baseUrl/analyze")
                    .post(requestBody)
                    .build()

                client.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string()

                    if (!response.isSuccessful) {
                        return@use "요청 실패\ncode: ${response.code}\nbody: $responseBody"
                    }

                    if (responseBody == null) {
                        return@use "응답이 비어 있습니다."
                    }

                    val responseJson = JSONObject(responseBody)

                    val inputText = responseJson.getString("input_text")
                    val result = responseJson.getString("result")
                    val riskScore = responseJson.getInt("risk_score")
                    val keywords = responseJson.getJSONArray("detected_keywords")
                    val message = responseJson.getString("message")

                    """
                    분석 결과
                    
                    입력 문장:
                    $inputText
                    
                    result:
                    $result
                    
                    risk_score:
                    $riskScore
                    
                    detected_keywords:
                    $keywords
                    
                    message:
                    $message
                    """.trimIndent()
                }
            } catch (e: Exception) {
                "에러 발생: ${e.message}"
            }
        }
    }
}