from pydantic import BaseModel, Field
from typing import Optional, Any, List
from datetime import date, datetime

# ---------- Users ----------
class UserCreate(BaseModel):
    phone: str
    name: str
    display_name: str

class UserOut(BaseModel):
    user_id: int
    phone: str
    name: str
    display_name: str
    created_at: datetime
    class Config: from_attributes = True

# ---------- Today ----------
class TodayCreate(BaseModel):
    entry_date: date
    mood_code: Optional[str] = None
    content: str

class TodayOut(BaseModel):
    entry_id: int
    user_id: int
    entry_date: date
    mood_code: Optional[str]
    content: str
    created_at: datetime
    class Config: from_attributes = True

# ---------- Questions ----------
class QuestionCreate(BaseModel):
    text: str
    category: Optional[str] = None
    answer: str

class QuestionOut(BaseModel):
    question_id: int
    text: str
    category: Optional[str]
    class Config: from_attributes = True

# ---------- Sessions / Answers ----------
class StartSessionOut(BaseModel):
    session_id: int

class AnswerIn(BaseModel):
    question_no: int
    question_id: int | None = None
    stt_text: Optional[str] = None
    typed_text: Optional[str] = None
    score: Optional[float] = None
    latency_ms: Optional[int] = None
    voice_vector: Optional[List[float]] = None  # 23ai VECTOR면 서버에서 변환 없이 그대로 바인딩됨(드라이버 지원 필요)

class SubmitAnswersIn(BaseModel):
    answers: List[AnswerIn]

class FinishOut(BaseModel):
    session_id: int
    total_score: Optional[float] = None
    status: str

class SessionOut(BaseModel):
    session_id: int
    user_id: int
    started_at: datetime
    finished_at: Optional[datetime]
    total_score: Optional[float]
    status: str
    class Config: from_attributes = True
    
class DiaryAIRequest(BaseModel):
    """
    오늘의 일기 AI 코칭 요청 바디
    - DB 저장은 /diary/create 에서 따로 처리
    - 여기서는 content(본문)만 받아서 AI 요약/감정/추천을 만든다.
    """
    content: str = Field(..., description="일기 본문 (필수)")
    entry_date: Optional[date] = Field(
        None,
        description="선택: 일기 날짜 (없어도 됨)",
    )
    display_name: Optional[str] = Field(
        None,
        description="선택: 사용자 표시 이름/닉네임 (누네림, 캡틴 등)",
    )


class EmotionReport(BaseModel):
    """
    감정 리포트
    1) emotion  : 짧은 감정 + 이모지 1개 (예: '기쁨😊')
    2) empathy  : 사용자의 일기와 감정에 공감하는 답변
    3) life_tip : 한 줄짜리 짧은 인생 조언
    """
    emotion: str = Field(
        ...,
        description="짧은 감정 + 이모지 1개 (예: '기쁨😊')",
    )
    empathy: str = Field(
        ...,
        description="사용자의 일기와 감정에 공감하는 답변",
    )
    life_tip: str = Field(
        ...,
        description="한 줄짜리 짧은 인생 조언",
    )


class YoutubeRecommendation(BaseModel):
    """
    유튜브 영상 추천
    - URL은 UI에 텍스트로 노출하지 않고,
      안드로이드에서 버튼 클릭 시 이동하는 용도로만 사용
    """
    title: str = Field(..., description="영상 제목")
    url: str = Field(..., description="유튜브 링크(URL)")
    reason: str = Field(..., description="이 영상을 추천하는 이유 (간단 설명)")
    category: str = Field(
        ...,
        description="추천 타입 (예: '운동', '스트레칭', '힐링', '동물', '명상' 등)",
    )


class DiaryAIResponse(BaseModel):
    """
    오늘의 일기 AI 코칭 응답
    1. summary        : 사용자의 당일 일기 요약
    2. emotion_report : 감정 리포트 (감정/공감/인생 조언)
    3. youtube        : 유튜브 영상 추천
    """
    summary: str = Field(..., description="사용자의 당일 일기 요약 (2~3문장)")
    emotion_report: EmotionReport
    youtube: YoutubeRecommendation