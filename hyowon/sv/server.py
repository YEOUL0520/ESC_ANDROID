from fastapi import FastAPI
from pydantic import BaseModel
from datetime import datetime

app = FastAPI()

class AnalyzeRequest(BaseModel):
    text: str

@app.get("/")
def root():
    return {
        "message": "Hello! This is a simple FastAPI server."
    }
    
@app.get("/health")
def health_check():
    return {
        "status": "OK",
        "server_time": datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    }

@app.post("/analyze")
def analyze_text(request: AnalyzeRequest):
    text = request.text
    
    keywords = ["계좌", "송금", "인증번호", "비밀번호", "대출", "검찰", "경찰"]
    detected_keywords = []
    
    for keyword in keywords:
        if keyword in text:
            detected_keywords.append(keyword)
            
    risk_score = min(len(detected_keywords) * 25, 100)
    
    if risk_score >= 70:
        result = "danger"
        message = "위험한 문장으로 보입니다."
    elif risk_score >= 30:
        result = "warning"
        message = "주의가 필요한 문장으로 보입니다."
    else:
        result = "safe"
        message = "특별한 위험 키워드는 발견되지 않았습니다."

    return {
        "input_text": text,
        "result": result,
        "risk_score": risk_score,
        "detected_keywords": detected_keywords,
        "message": message
    }
