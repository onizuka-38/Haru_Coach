from typing import List, Optional, Dict
from pydantic import BaseModel, Field

class Question(BaseModel):
    questionNo: int = Field(..., description="세션 내 문항 순번 (1..N)")
    questionId: int = Field(..., description="COGNITIVE_QUESTION.QUESTION_ID")
    text: str = Field(..., description="문항 텍스트")
    category: Optional[str] = Field(None, description="문항 카테고리")

class StartResponse(BaseModel):
    sessionId: int
    questions: List[Question]

class AnswerItem(BaseModel):
    questionNo: int
    questionId: int
    sttText: Optional[str] = None
    typedText: Optional[str] = None
    latencyMs: Optional[int] = None

class SubmitRequest(BaseModel):
    sessionId: int
    answers: List[AnswerItem]


class Result(BaseModel):
    totalScore: float
    perQuestion: Dict[int, float]
    summary: str
    grade: str
