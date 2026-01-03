from __future__ import annotations
from typing import List, Tuple
from datetime import datetime
from rapidfuzz import fuzz
import re

def resolve_answer_tokens(raw: str) -> str:
    """
    DB에 {current.year}, {today.kor_date} 같은 토큰이 들어있을 때
    현재 시각 기준 실제 문자열로 치환합니다.
    """
    if not raw:
        return raw
    now = datetime.now()
    mapping = {
        "{current.year}": str(now.year),
        "{today.kor_date}": f"{now.year}년 {now.month}월 {now.day}일",
        "{today.month}": str(now.month),
        "{today.day}": str(now.day),
        "{today.weekday_kor}": [
            "월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일"
        ][now.weekday()],
        "{xmas.kor}": "12월 25일",
        "{this.month.kor}": f"{now.month}월",
        "{this.year.kor}": f"{now.year}년",
    }
    out = raw
    for k, v in mapping.items():
        out = out.replace(k, v)
    return out


def normalize_kor(s: str) -> str:
    """
    한국어/숫자 비교 시 대략적으로만 동일 여부를 보기 위한 정규화 함수.
    - 소문자
    - 공백/구두점 제거
    - 년/월/일 제거
    """
    s = (s or "")
    return (
        s.lower()
        .replace(" ", "")
        .replace(",", "")
        .replace("·", "")
        .replace("，", "")
        .replace(".", "")
        .replace("년", "")
        .replace("월", "")
        .replace("일", "")
        .strip()
    )


def split_items(s: str) -> List[str]:
    parts = re.split(r"[,\\s/·\\-]+", (s or "").strip())
    return [p for p in parts if p]


def score_exact_or_fuzzy(user: str, correct: str) -> float:
    """
    완전 동일하면 1.0, 아니면 rapidfuzz 기반 유사도 점수(0~1).
    (현재는 AI 채점이 기본이지만, 백업용으로 남겨 둠)
    """
    u = normalize_kor(user)
    c = normalize_kor(correct)
    if not u and not c:
        return 1.0
    if u == c:
        return 1.0
    return fuzz.ratio(u, c) / 100.0


from pydantic import BaseModel, Field
from langchain_openai import ChatOpenAI
from langchain_core.prompts import ChatPromptTemplate


class AIScoringResult(BaseModel):
    score: float = Field(..., ge=0.0, le=1.0)
    reasoning: str
    is_correct: bool


_llm = ChatOpenAI(
    model="gpt-4.1-mini",
    temperature=0.0,
)

_PROMPT = ChatPromptTemplate.from_template(
    """
당신은 한국어 인지능력 검사 채점 도우미입니다.

규칙:
- 점수는 0.0 ~ 1.0 사이의 실수로 주세요.
- 1.0은 완전 정답, 0.0은 완전 오답, 부분 정답은 그 사이 값입니다.
- 단순한 표현 차이, 조사/어미, 띄어쓰기는 틀린 것으로 보지 않습니다.
- 질문 범위를 벗어난 내용은 무시하고, 핵심 정답 요소가 맞는지만 확인합니다.
- 기억력 문제(단어/숫자 나열)는 기준 정답에 있는 핵심 단어/숫자가 몇 개 맞는지 비율을 반영해서 점수를 주십시오.

[문항 카테고리]
{category}

[문항 텍스트]
{text}

[기준 정답]
{correct}

[피검자 답변]
{user}

위 정보를 바탕으로 채점 결과를 JSON 형식으로 생성하세요.
"""
)

_chain = _PROMPT | _llm.with_structured_output(AIScoringResult)


def _score_with_ai(
    category: str,
    text: str,
    resolved_correct: str,
    user_answer: str,
) -> Tuple[float, str]:
    """
    LangChain 체인을 이용해 AI 채점을 수행합니다.
    """
    result: AIScoringResult = _chain.invoke(
        {
            "category": category or "",
            "text": text or "",
            "correct": resolved_correct or "",
            "user": user_answer or "",
        }
    )
    score = float(result.score)
    feedback = (
        f"AI 채점 ({category}): 점수={score:.2f}, "
        f"정답여부={result.is_correct}, 이유={result.reasoning}"
    )
    return score, feedback


def choose_scoring(
    category: str,
    text: str,
    correct_answer_raw: str,
    user_answer: str,
) -> Tuple[float, str, str]:
    """
    최종 채점 함수.

    1) DB 정답과 사용자 답변이 "정규화 기준으로 완전히 일치"하면
       - 규칙 기반으로 1.0 점 (100점) 처리 (AI 호출 없음)

    2) 그 외의 경우
       - LangChain + OpenAI 기반 AI 채점을 수행하여 0.0 ~ 1.0 점수를 받는다.

    반환:
        score (0.0~1.0), feedback(str), resolved_correct(str)
    """

    resolved_correct = resolve_answer_tokens(correct_answer_raw or "")

    user_norm = normalize_kor(user_answer)
    correct_norm = normalize_kor(resolved_correct)

    if correct_norm and user_norm and user_norm == correct_norm:
        score = 1.0
        fb = "정답과 완전히 일치 (규칙 기반 통과, AI 호출 안 함)"
        return score, fb, resolved_correct

    score, fb = _score_with_ai(category or "", text or "", resolved_correct, user_answer)
    score = max(0.0, min(1.0, score))
    return score, fb, resolved_correct
