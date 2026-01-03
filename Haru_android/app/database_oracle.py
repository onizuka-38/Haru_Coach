import os
from sqlalchemy import create_engine, text
from sqlalchemy.orm import sessionmaker

try:
    from dotenv import load_dotenv
    load_dotenv()
except Exception:
    pass

ORACLE_USER = os.getenv("ORACLE_USER", "EDUORA001")
ORACLE_PASSWORD = os.getenv("ORACLE_PASSWORD", "")
ORACLE_HOST = os.getenv("ORACLE_HOST", "localhost")
ORACLE_PORT = os.getenv("ORACLE_PORT", "1521")

ORACLE_SERVICE = os.getenv("ORACLE_SERVICE", os.getenv("ORACLE_SID", "free"))

DB_URL = (
    f"oracle+oracledb://{ORACLE_USER}:{ORACLE_PASSWORD}"
    f"@{ORACLE_HOST}:{ORACLE_PORT}/?service_name={ORACLE_SERVICE}"
)

_safe = DB_URL.replace(ORACLE_PASSWORD, "****") if ORACLE_PASSWORD else DB_URL
print(f"[DB URL(check)] { _safe }")

engine = create_engine(
    DB_URL,
    thick_mode=False,
    pool_pre_ping=True,
    pool_recycle=1800,
    pool_size=5,
    max_overflow=5,
)

SessionLocal = sessionmaker(bind=engine, autocommit=False, autoflush=False)

def get_db():
    db = SessionLocal()
    try:
        yield db
        db.commit()
    except Exception:
        db.rollback()
        raise
    finally:
        db.close()

def ping_db() -> dict:
    try:
        with engine.connect() as conn:
            conn.execute(text("SELECT 1 FROM DUAL"))
        return {"db": "ok"}
    except Exception as e:
        return {"db": "fail", "error": str(e)}
