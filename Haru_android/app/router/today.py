from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from sqlalchemy import select
from ..database_oracle import get_db
from ..models import Today, AppUser
from ..schemas import TodayCreate, TodayOut

router = APIRouter(prefix="/today", tags=["Today"])

@router.post("", response_model=TodayOut, status_code=201)
def create_today(payload: TodayCreate, db: Session = Depends(get_db)):
    if not db.get(AppUser, payload.user_id):
        raise HTTPException(404, "User not found")
    # UNIQUE(user_id, entry_date)
    exists = db.execute(
        select(Today).where(Today.user_id == payload.user_id, Today.entry_date == payload.entry_date)
    ).scalar_one_or_none()
    if exists:
        raise HTTPException(409, "Entry already exists for this date")

    t = Today(user_id=payload.user_id, entry_date=payload.entry_date,
              mood_code=payload.mood_code, content=payload.content)
    db.add(t); db.commit(); db.refresh(t)
    return t
