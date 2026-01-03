from sqlalchemy import (
    Column, Integer, String, Date, DateTime, Text, ForeignKey, Identity,
    Numeric, UniqueConstraint
)
from sqlalchemy.orm import declarative_base, relationship, deferred
from datetime import datetime
import json

Base = declarative_base()

# ---------- (선택) Oracle 23ai VECTOR 타입 ----------
# 23ai가 아니면 사용하지 않거나 주석 처리하세요.
from sqlalchemy.types import UserDefinedType


class Vector768(UserDefinedType):
    """
    Oracle 23ai의 VECTOR(768) 타입을 Python에서 처리하기 위한 클래스
    """

    def get_col_spec(self, **kw):
        """DB에 생성될 때 컬럼 타입 이름"""
        return "VECTOR(768)"

    def bind_parameter(self, value, dialect):
        """
        Python → DB: 데이터를 저장할 때
        Python 리스트 [0.1, 0.2, ...] → DB의 VECTOR 형식
        """
        if value is None:
            return None
        # 리스트를 JSON 문자열로 변환 (Oracle이 이해할 수 있는 형태)
        if isinstance(value, (list, tuple)):
            return json.dumps(value)
        return value

    def result_processor(self, dialect, coltype):
        """
        DB → Python: 데이터를 읽어올 때
        DB의 VECTOR → Python 리스트 [0.1, 0.2, ...]
        """

        def process(value):
            if value is None:
                return None  # ⭐ NULL은 NULL로 반환!

            # VECTOR 타입을 Python에서 사용 가능한 형태로 변환
            # (보통 문자열이나 bytes로 넘어옴)
            try:
                if isinstance(value, str):
                    return json.loads(value)
                elif isinstance(value, bytes):
                    return json.loads(value.decode('utf-8'))
                else:
                    # 그냥 반환 (이미 리스트 형태일 수도)
                    return value
            except:
                # 변환 실패시 None 반환 (에러 대신)
                return None

        return process


# ========== APP_USER ==========
class AppUser(Base):
    __tablename__ = "APP_USER"
    user_id      = Column("USER_ID", Integer, Identity(start=1, increment=1), primary_key=True)
    phone        = Column("PHONE", String(50), nullable=False, unique=True)
    name         = Column("NAME", String(200), nullable=False)
    display_name = Column("DISPLAY_NAME", String(200), nullable=False)
    created_at   = Column("CREATED_AT", DateTime, nullable=False, default=datetime.utcnow)

    todays   = relationship("Today", back_populates="user", cascade="all, delete-orphan")
    sessions = relationship("CognitiveSession", back_populates="user", cascade="all, delete-orphan")


# ========== TODAY ==========
class Today(Base):
    __tablename__ = "TODAY"
    entry_id   = Column("ENTRY_ID",Integer, Identity(start=1, increment=1), primary_key=True)
    user_id    = Column("USER_ID",Integer, ForeignKey("APP_USER.USER_ID"), nullable=False)
    entry_date = Column("ENTRY_DATE",Date, nullable=False)
    mood_code  = Column("MOOD_CODE",String(16))
    content    = Column("CONTENT",Text, nullable=False)  # CLOB
    created_at = Column("CREATED_AT",DateTime, nullable=False, default=datetime.utcnow)

    __table_args__ = (
        UniqueConstraint("USER_ID", "ENTRY_DATE", name="UQ_TODAY_USER_DATE"),
    )

    user = relationship("AppUser", back_populates="todays")


# ========== COGNITIVE_SESSION ==========
class CognitiveSession(Base):
    __tablename__ = "COGNITIVE_SESSION"
    session_id  = Column("SESSION_ID", Integer, Identity(start=1, increment=1), primary_key=True)
    user_id     = Column("USER_ID", Integer, ForeignKey("APP_USER.USER_ID"), nullable=False)
    started_at  = Column("STARTED_AT", DateTime, nullable=False, default=datetime.utcnow)
    finished_at = Column("FINISHED_AT", DateTime)
    total_score = Column("TOTAL_SCORE", Numeric(5, 2))
    status      = Column("STATUS", String(30), nullable=False, default="IN_PROGRESS")

    user    = relationship("AppUser", back_populates="sessions")
    answers = relationship("CognitiveAnswer", back_populates="session", cascade="all, delete-orphan")


# ========== COGNITIVE_QUESTION  → 기존 QUESTIONS 테이블에 매핑 ==========
class CognitiveQuestion(Base):
    """
    기존 문제은행 테이블 구조에 맞춰 매핑:
      - __tablename__ = "QUESTIONS"
      - QUESTION_ID NUMBER (PK)
      - TEXT        CLOB   (NOT NULL)
      - CATEGORY    VARCHAR2(30)
      - ANSWER      CLOB
    주의: 이미 존재하는 테이블이므로 Identity/Sequence를 새로 걸지 않습니다.
    """
    __tablename__ = "COGNITIVE_QUESTION"

    # 이미 존재하는 PK라서 autoincrement는 DB가 관리합니다.
    question_id = Column("QUESTION_ID", Integer, primary_key=True)  # autoincrement=False 의미
    text        = Column("TEXT",      Text,    nullable=False)       # CLOB
    category    = Column("CATEGORY",  String(30))
    answer      = Column("ANSWER",    Text)                          # CLOB

    answers = relationship("CognitiveAnswer", back_populates="question")


# ========== COGNITIVE_ANSWER ==========
class CognitiveAnswer(Base):
    __tablename__ = "COGNITIVE_ANSWER"
    answer_id   = Column("ANSWER_ID", Integer, Identity(start=1, increment=1), primary_key=True)
    session_id  = Column("SESSION_ID", Integer, ForeignKey("COGNITIVE_SESSION.SESSION_ID"), nullable=False)
    question_no = Column("QUESTION_NO", Integer, nullable=False)
    # 기존 QUESTIONS 테이블을 참조하도록 FK 수정
    question_id = Column("QUESTION_ID", Integer, ForeignKey("COGNITIVE_QUESTION.QUESTION_ID"))

    stt_text   = Column("STT_TEXT", Text)    # CLOB
    typed_text = Column("TYPED_TEXT", Text)    # CLOB
    score      = Column("SCORE", Numeric(5, 2))
    latency_ms = Column("LATENCY_MS", Integer)

    # 23ai라면:
    # deferred 추가
    voice_vector = deferred(Column("VOICE_VECTOR", Vector768))  # nullable 기본 True
    # 23ai가 아니면, 예: JSON 컬럼을 쓰거나(별도) 생략하세요.

    created_at = Column("CREATED_AT", DateTime, nullable=False, default=datetime.utcnow)

    __table_args__ = (
        UniqueConstraint("SESSION_ID", "QUESTION_NO", name="UQ_ANSWER_SESSION_QNO"),
    )

    session  = relationship("CognitiveSession", back_populates="answers")
    question = relationship("CognitiveQuestion", back_populates="answers")
