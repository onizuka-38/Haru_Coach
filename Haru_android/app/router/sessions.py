from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from sqlalchemy import select, func
from datetime import datetime
from ..database_oracle import get_db
from ..models import CognitiveSession, CognitiveAnswer, AppUser, CognitiveQuestion
from ..schemas import StartSessionOut, SubmitAnswersIn, FinishOut, SessionOut

router = APIRouter(prefix="/sessions", tags=["Cognitive Sessions"])

@router.post("/start", response_model=StartSessionOut, status_code=201)
def start_session(user_id: int, db: Session = Depends(get_db)):
    if not db.get(AppUser, user_id):
        raise HTTPException(404, "User not found")
    s = CognitiveSession(user_id=user_id, status="IN_PROGRESS")
    db.add(s); db.commit(); db.refresh(s)
    return {"session_id": s.session_id}

@router.post("/{sid}/answers")
def submit_answers(sid: int, payload: SubmitAnswersIn, db: Session = Depends(get_db)):
    s = db.get(CognitiveSession, sid)
    if not s: raise HTTPException(404, "Session not found")
    if s.status != "IN_PROGRESS": raise HTTPException(400, "Session already finished")

    for a in payload.answers:
        # question_no unique check is enforced by UNIQUE(session_id, question_no)
        if a.question_id and not db.get(CognitiveQuestion, a.question_id):
            raise HTTPException(400, f"Question {a.question_id} not found")
        db.add(CognitiveAnswer(
            session_id=sid,
            question_no=a.question_no,
            question_id=a.question_id,
            stt_text=a.stt_text,
            typed_text=a.typed_text,
            score=a.score,
            latency_ms=a.latency_ms,
            voice_vector=a.voice_vector  # 23ai VECTOR: 드라이버가 리스트 바인딩을 지원해야 함
        ))
    db.commit()
    return {"ok": True}

@router.post("/{sid}/finish", response_model=FinishOut)
def finish_session(sid: int, db: Session = Depends(get_db)):
    s = db.get(CognitiveSession, sid)
    if not s: raise HTTPException(404, "Session not found")
    if s.status != "IN_PROGRESS": raise HTTPException(400, "Already finished")

    total = db.execute(
        select(func.coalesce(func.sum(CognitiveAnswer.score), 0)).where(CognitiveAnswer.session_id == sid)
    ).scalar_one()

    s.total_score = float(total) if total is not None else None
    s.status = "SCORED"
    s.finished_at = datetime.utcnow()
    db.commit()
    return {"session_id": sid, "total_score": s.total_score, "status": s.status}

@router.get("/{sid}", response_model=SessionOut)
def get_session(sid: int, db: Session = Depends(get_db)):
    s = db.get(CognitiveSession, sid)
    if not s: raise HTTPException(404, "Session not found")
    return s