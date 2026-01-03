from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from sqlalchemy import select
from ..database_oracle import get_db
from ..models import CognitiveQuestion
from ..schemas import QuestionCreate, QuestionOut

router = APIRouter(prefix="/questions", tags=["Questions"])

@router.post("", response_model=QuestionOut, status_code=201)
def create_question(payload: QuestionCreate, db: Session = Depends(get_db)):
    q = CognitiveQuestion(text=payload.text, category=payload.category, answer=payload.answer)
    db.add(q); db.commit(); db.refresh(q)
    return q

@router.get("", response_model=list[QuestionOut])
def list_questions(db: Session = Depends(get_db)):
    rows = db.execute(select(CognitiveQuestion)).scalars().all()
    return rows
