from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from sqlalchemy import select
from ..database_oracle import get_db
from ..models import AppUser
from ..schemas import UserCreate, UserOut

router = APIRouter(prefix="/users", tags=["Users"])

@router.post("", response_model=UserOut, status_code=201)
def create_user(payload: UserCreate, db: Session = Depends(get_db)):
    exists = db.execute(select(AppUser).where(AppUser.phone == payload.phone)).scalar_one_or_none()
    if exists:
        raise HTTPException(409, "Phone already registered")
    u = AppUser(phone=payload.phone, name=payload.name, display_name=payload.display_name)
    db.add(u); db.commit(); db.refresh(u)
    return u

@router.get("/{user_id}", response_model=UserOut)
def get_user(user_id: int, db: Session = Depends(get_db)):
    u = db.get(AppUser, user_id)
    if not u:
        raise HTTPException(404, "User not found")
    return u
