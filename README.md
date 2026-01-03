# 🧠 Haru Coach  
**AI Agent 기반 고령층 대상 인지·정서 케어 플랫폼**

Haru Coach는 음성·감정·행동 데이터를 기반으로  
고령층의 **인지 변화와 정서 상태를 일상 속에서 조기에 감지**하는  
AI Cognitive Caregiver 서비스입니다.

---

## 📄 Documents

- 📕 [Haru Coach 프로젝트 발표자료 (PDF)](docs/HaruCoach.pdf)

---

## 📌 Background

- 국내 치매 유병률 약 **10.4%**, 경도인지장애 **22.7%**
- 병원 중심의 인지검사는 **발견이 늦고**, 반복 추적이 어려움
- 고령층은 복잡한 UI, 대면 검사에 큰 부담

👉 **Haru Coach는 “진단”이 아닌  
일상 속 “변화 감지”에 초점을 둡니다.**

---

## 🎯 Core Features

### 1️⃣ 음성 기반 인지 평가
- AI 음성 질문 → STT → 자동 채점
- 지남력 / 기억력 / 주의력 / 언어능력 등 카테고리별 점수 제공
- LangChain + OpenAI 기반 **부분 정답·표현 차이 허용 채점**
- Oracle SELECT AI 기반 문제 선택 자동화

### 2️⃣ 음성 기반 오늘의 일기 (AI 코치)
- 음성 일기 → AI 요약
- 감정 분석 + 공감 메시지 + 짧은 코칭
- 감정 상태에 맞는 **유튜브 힐링 콘텐츠 추천**
- CoT + AI Agent 기반 **일관된 톤·구조 유지**

### 3️⃣ 오늘의 학습 (인지 훈련)
- 숫자 계산, 숫자 기억하기
- 초성 맞추기, 색깔 맞추기
- 반복 훈련을 통한 **인지 처리 속도·억제력 향상**

---

## 🧩 Service Flow

1. 로그인
2. 인지 검사 시작 (AI 음성 질문)
3. 음성 답변 → STT → AI 채점
4. 결과 리포트 제공
5. 일기 작성 및 감정 분석
6. Dashboard에서 변화 추적

---

## 🛠 Tech Stack

### 📱 Frontend (Android)
- Kotlin
- Jetpack Compose
- StateFlow / ViewModel
- Retrofit / OkHttp
- Google STT / TTS
- Hilt DI

### 🧠 Backend
- FastAPI (Python)
- LangChain + OpenAI API
- RapidFuzz (유사도 기반 채점 보조)
- Oracle 23ai Cloud DB
- SQLAlchemy + Raw SQL 혼합
- Oracle SELECT AI

### 🗄 Database
- Cognitive Session / Question / Answer
- Diary / Emotion / Log
- Vector(768) 기반 음성 임베딩 저장

---

## 🧠 AI Agent Architecture

- Chain-of-Thought (내부 사고 비공개)
- Structured JSON Output
- Few-shot Prompting
- 안전성 규칙 (진단/응급 발언 제한)

> 안정적 톤 + 일관된 출력 + 사용자 친화 UX

---

## 🌍 Vision

> **AI가 사람의 일상 속 인지 변화를  
> 누구보다 먼저 이해하는 세상**

- 실버케어 시장 연 14% 성장
- 병원·보험사·지자체 연계 가능
- B2B / B2G 확장 가능

---
