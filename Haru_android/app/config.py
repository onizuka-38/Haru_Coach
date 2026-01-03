# app/config.py
from pydantic_settings import BaseSettings
from pydantic import Field

class Settings(BaseSettings):
    oracle_user: str = Field(..., alias="ORACLE_USER")
    oracle_password: str = Field(..., alias="ORACLE_PASSWORD")
    oracle_host: str = Field(..., alias="ORACLE_HOST")
    oracle_port: int = Field(1521, alias="ORACLE_PORT")
    # service_name 우선, sid는 대안
    oracle_service: str | None = Field(None, alias="ORACLE_SERVICE")
    oracle_sid: str | None = Field(None, alias="ORACLE_SID")

    class Config:
        env_file = ".env"
        extra = "ignore"

settings = Settings()
