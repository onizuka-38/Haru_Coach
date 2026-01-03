# test/app/router/auth_android.py
from fastapi import APIRouter, Form
import uuid

router = APIRouter(prefix="/auth", tags=["Auth(Android)"])

@router.post("/login")
def login_android(username: str = Form(...), password: str = Form(...)):
    """
    안드로이드 Retrofit 사양(폼 전송)에 맞춘 로그인.
    - 실제 인증/DB검증이 아직 없다면, 임시 토큰 발급만 해도 Android는 정상 동작.
    - 운영시에는 사용자 테이블과 패스워드 해시를 붙이면 된다.
    """
    token = str(uuid.uuid4())
    return {"access_token": token, "token_type": "bearer"}
