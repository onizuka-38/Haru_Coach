import logging
import random
import sys
from typing import List, Dict, Optional

from fastapi import APIRouter, Depends, Query, HTTPException
from sqlalchemy.orm import Session
from sqlalchemy import func
from datetime import datetime

from ..models import CognitiveSession, CognitiveQuestion, CognitiveAnswer
from ..database_oracle import get_db
from ..schemas_cognitive import (
    Question, StartResponse, AnswerItem, SubmitRequest, Result
)
from ..services.quiz_scoring import choose_scoring  #  AI 채점 함수

LOG_FORMAT = "%(levelname)s: [%(asctime)s] - [%(module)s] => %(message)s"
logging.basicConfig(level=logging.INFO, format=LOG_FORMAT, stream=sys.stdout)
logger = logging.getLogger(__name__)

# Router 선언 (이 파일의 API 주소는 모두 /cognitive 로 시작)
router = APIRouter(prefix="/cognitive", tags=["cognitive"])


def _norm(s: Optional[str]) -> str:
    """문자열을 비교하기 전에 앞뒤 공백 제거하고 모두 소문자로 바꿔요"""
    return (s or "").strip().casefold()


def _grade_from(score_pct: float) -> str:
    """평균 점수로 등급을 간단히 판단해요"""
    if score_pct >= 80:
        return "정상"
    if score_pct >= 60:
        return "주의"
    return "위험"


@router.get("/start", response_model=StartResponse)
def start_cognitive(
        user_id: int = Query(1, ge=1, description="임시 유저 ID (로그인 연동 전)"),
        count: int = Query(10, description="총 문제 수"),
        category: Optional[str] = Query(None, description="특정 카테고리만 지정 (보통 None)"),
        db: Session = Depends(get_db)
):
    """
    인지 능력 검사 시작
    :param user_id:
    :param count:
    :param category:
    :param db:
    :return:
    """
    logger.info(
        f"REQ => /start: API 시작. [user_id={user_id}, count={count}, category={category}]"
    )
    session_id: int = 0

    try:
        logger.info(f"[세션생성] user_id={user_id}의 새 CognitiveSession 생성 시도...")
        new_session = CognitiveSession(
            user_id=user_id,
            status="IN_PROGRESS",
            started_at=datetime.utcnow()
        )
        db.add(new_session)
        db.flush()

        session_id = int(new_session.session_id)
        db.commit()
        logger.info(f"[세션 생성] user_id={user_id} 생성 성공! [session_id={session_id}]")

    except Exception as e:
        logger.error(f"[세션 생성] user_id={user_id} 생성 실패! [Error: {e}]", exc_info=True)
        db.rollback()
        raise HTTPException(status_code=500, detail=f"세션 생성 오류: {e}")

    try:
        logger.info(f"[질문 조회] session_id={session_id}의 질문 {count}개 뽑기 시작...")

        question_objects: List[CognitiveQuestion] = []

        if category:
            logger.info(f"특정 카테고리 '{category}'만 {count}개 무작위로 뽑습니다.")
            question_objects = (
                db.query(CognitiveQuestion)
                .filter(CognitiveQuestion.category == category)
                .order_by(func.dbms_random.value())
                .limit(count)
                .all()
            )
        else:
            logger.info(f"[질문 조회] user_id={user_id} 검사 생성 START!")

            set_id = random.randint(1, 5)
            logger.info(f"[질문 조회] user_id={user_id} 지남력 '{set_id}번'")

            mem_q_id = 5 + set_id
            rec_q_id = 20 + set_id

            pair_questions = (
                db.query(CognitiveQuestion)
                .filter(CognitiveQuestion.question_id.in_([mem_q_id, rec_q_id]))
                .all()
            )
            question_objects.extend(pair_questions)
            logger.info(f"[질문 조회] user_id={user_id} 기억력, 회상력 생성 (Q_ID: {mem_q_id}, {rec_q_id})")

            remaining_count = count - 2
            if remaining_count > 0:
                other_cats = ['지남력', '주의력', '언어능력']

                n_per_cat = remaining_count // len(other_cats)
                remainder = remaining_count % len(other_cats)

                needs_map = {cat: n_per_cat for cat in other_cats}
                for i in range(remainder):
                    needs_map[other_cats[i]] += 1

                logger.info(f"[질문 조회] user_id={user_id} 나머지 {remaining_count}개 배분: {needs_map}")

                for cat_name, needed_count in needs_map.items():
                    cat_questions = (
                        db.query(CognitiveQuestion)
                        .filter(CognitiveQuestion.category == cat_name)
                        .order_by(func.dbms_random.value())
                        .limit(needed_count)
                        .all()
                    )
                    question_objects.extend(cat_questions)
                    logger.info(f"'{cat_name}' 카테고리 {len(cat_questions)}개 확보.")

            random.shuffle(question_objects)
            logger.info(f"[질문 조회] user_id={user_id}총 {len(question_objects)}개 문제 확보 및 순서 섞기 완료.")

        questions_for_response: List[Question] = []

        logger.info(f"[Step 3] session_id={session_id}에 대한 '빈 답안지' {len(question_objects)}개 생성 시작...")

        for i, q_obj in enumerate(question_objects, start=1):
            questions_for_response.append(
                Question(
                    questionNo=i,
                    questionId=int(q_obj.question_id),
                    text=str(q_obj.text or ""),
                    category=str(q_obj.category) if q_obj.category is not None else None
                )
            )

            new_blank_answer = CognitiveAnswer(
                session_id=session_id,
                question_no=i,
                question_id=int(q_obj.question_id),
                created_at=datetime.utcnow(),
                voice_vector=None
            )
            db.add(new_blank_answer)

        if not question_objects:
            logger.warning(f"[질문 백업] user_id={user_id} DB에 질문이 없음! [session_id={session_id}] 더미 질문 반환.")
            demo = [("올해는 몇 년인가요?", "지남력"), ("오늘은 무슨 요일인가요?", "지남력")]
            for i, (q_text, cat) in enumerate(demo, start=1):
                questions_for_response.append(
                    Question(questionNo=i, questionId=0, text=q_text, category=cat)
                )
        else:
            db.commit()
            logger.info(f"[질문 백업] user_id={user_id} {len(question_objects)}개 DB 저장 완료.")

        logger.info(
            f"RES => user_id={user_id} /start: 성공! [session_id={session_id}] "
            f"질문 {len(questions_for_response)}개 반환."
        )
        return StartResponse(sessionId=session_id, questions=questions_for_response)

    except Exception as e:
        logger.error(f"[질문 백업] user_id={user_id} 질문 조회/백업 생성 실패! [Error: {e}]", exc_info=True)
        db.rollback()
        raise HTTPException(status_code=500, detail=f"질문 조회/빈 답안 생성 오류: {e}")


# ---------------- SUBMIT 엔드포인트 (AI 채점 + 기존 답안 UPDATE) ----------------
@router.post("/submit", response_model=Result)
def submit_cognitive(payload: SubmitRequest, db: Session = Depends(get_db)):
    """
    클라이언트에서 보낸 답안을 저장하고 채점한 뒤 결과를 반환합니다.
    - /start에서 이미 COGNITIVE_ANSWER에 (SESSION_ID, QUESTION_NO) 빈 레코드를 만들어둔 상태이므로,
      여기서는 INSERT가 아니라 해당 레코드를 UPDATE하는 방식으로 동작합니다.
    - 정답이 완전히 일치하면 AI 호출 없이 100점,
      그렇지 않으면 LangChain + OpenAI를 활용해 AI 채점을 수행합니다.
    """

    # 0) 유효성 검사: answers가 비어있으면 오류
    if not payload.answers:
        raise HTTPException(status_code=400, detail="answers가 비어 있습니다.")

    # 1) 이 세션에서 사용된 문항 정보 로드: {questionId: CognitiveQuestion}
    qids = [a.questionId for a in payload.answers if a.questionId]
    question_map: Dict[int, CognitiveQuestion] = {}

    if qids:
        try:
            rows: List[CognitiveQuestion] = (
                db.query(CognitiveQuestion)
                .filter(CognitiveQuestion.question_id.in_(qids))
                .all()
            )
            question_map = {int(r.question_id): r for r in rows}
        except Exception as e:
            logger.error(f"[정답 조회] question 정보 조회 실패: {e}", exc_info=True)
            question_map = {}

    # 2) 각 문항 점수 계산 및 CognitiveAnswer UPDATE
    per_q_score: Dict[int, float] = {}
    total = 0.0

    try:
        for item in payload.answers:
            user_text = item.sttText or item.typedText or ""
            qinfo = question_map.get(item.questionId) if item.questionId else None

            if qinfo:
                # ★ 정답 완전 일치면 AI 안 부르고 1.0점,
                #   다르면 LangChain + OpenAI 기반 AI 채점
                score_0_1, fb, resolved_correct = choose_scoring(
                    qinfo.category or "",
                    qinfo.text or "",
                    qinfo.answer or "",
                    user_text,
                )
            else:
                score_0_1 = 0.0
                fb = "DB에 문항 정보가 없어 0점 처리"
                resolved_correct = ""

            score_pct = score_0_1 * 100.0

            # ★ 여기서 새로 INSERT 하지 말고,
            #    /start에서 생성한 기존 빈 답안 레코드를 찾아서 UPDATE
            existing = (
                db.query(CognitiveAnswer)
                .filter(
                    CognitiveAnswer.session_id == payload.sessionId,
                    CognitiveAnswer.question_no == item.questionNo,
                )
                .one_or_none()
            )

            if existing:
                # 기존 레코드에 값 채워넣기
                existing.question_id = item.questionId if item.questionId else existing.question_id
                existing.stt_text = item.sttText
                existing.typed_text = item.typedText
                existing.score = score_pct
                existing.latency_ms = item.latencyMs
                existing.created_at = existing.created_at or datetime.utcnow()
                # voice_vector 등은 나중에 음성 임베딩 추가할 때 채우면 됨
            else:
                # 혹시라도 placeholder가 없으면 안전하게 새로 INSERT (예외 케이스용)
                ans = CognitiveAnswer(
                    session_id=payload.sessionId,
                    question_no=item.questionNo,
                    question_id=item.questionId if item.questionId else None,
                    stt_text=item.sttText,
                    typed_text=item.typedText,
                    score=score_pct,
                    latency_ms=item.latencyMs,
                    created_at=datetime.utcnow(),
                    voice_vector=None,
                )
                db.add(ans)

            per_q_score[item.questionNo] = score_pct
            total += score_pct

        db.commit()

    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=f"답안 저장 오류: {e}")

    # 3) 세션 종료 및 총점 업데이트
    try:
        n = len(payload.answers)
        avg = total / n if n else 0.0

        session_obj = (
            db.query(CognitiveSession)
            .filter(CognitiveSession.session_id == payload.sessionId)
            .one_or_none()
        )
        if not session_obj:
            raise HTTPException(status_code=400, detail="유효하지 않은 sessionId 입니다.")

        session_obj.finished_at = datetime.utcnow()
        session_obj.total_score = avg
        session_obj.status = "COMPLETED"

        db.commit()

    except HTTPException:
        raise
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=f"세션 업데이트 오류: {e}")

    grade = _grade_from(avg)
    summary = f"총 {len(payload.answers)}문항 평균 {avg:.1f}점 → 등급 {grade}"

    return Result(
        totalScore=round(avg, 1),
        perQuestion=per_q_score,
        summary=summary,
        grade=grade
    )
