# 📱 HaruCoach Android App

AI 기반 라이프코칭 앱 HaruCoach의 Android 클라이언트 프로젝트입니다.

이 프로젝트는 Kotlin, JDK 17, Android SDK 35를 기반으로 하며 MVVM 아키텍처 + Hilt(DI) + Jetpack Compose / ViewBinding 구조를 따릅니다.

## 🧩 기술 스택

| 분류 | 기술 |
|------|------|
| **Language** | Kotlin |
| **JDK** | 17 |
| **Android SDK** | 35 |
| **Architecture** | MVVM |
| **Dependency Injection** | Hilt |
| **Data Storage** | DataStore Preferences |
| **Networking** | Retrofit2 + OkHttp3 |
| **Asynchronous** | Kotlin Coroutines / Flow |
| **UI Framework** | Android View + Compose (병행 사용 가능) |

## ⚙️ 실행 방법 (Run Instructions)

### 1. 프로젝트 클론
```bash
git clone https://github.com/OracleHealthCareTeam01/HaruCoachFront.git
```

### 2. Android Studio에서 열기

- JDK 17 환경 설정 확인
    - `Preferences` → `Build, Execution, Deployment` → `Build Tools` → `Gradle` → `Gradle JDK = 17`

### 3. SDK 확인

- Android SDK 35 설치 확인

### 4. 빌드 & 실행

- Gradle Sync 완료 후 ▶️ Run

## 📁 디렉토리 구조 (Project Structure)
```
HaruCoach/
├── app/
│   ├── build.gradle                          # App 모듈의 Gradle 설정
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/harucoach/harucoachfront/
│   │   │   │   ├── HaruCoachApplication.kt   # 앱 초기화 (DI, 전역 설정)
│   │   │   │   ├── di/                       # Hilt 모듈 (Repository, DataSource 주입)
│   │   │   │   ├── data/
│   │   │   │   │   ├── repository/           # Repository 계층 (DataStore, Network 접근)
│   │   │   │   │   ├── models/                # data class
│   │   │   │   │   ├── remote/               # Retrofit API 정의
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screens/               # UI 화면 단위 (Activity / Fragment)
│   │   │   │   │   
│   │   │   │   ├── viewmodel/            # ViewModel 계층
│   │   │   ├── res/                          # 리소스 (layout, drawable, values 등)
│   │   │   
│   │   │   
│
├── gradle/
│   ├── wrapper/                              # Gradle wrapper 실행 환경
│
├── .gitignore                                # Git 추적 제외 설정 (build, .idea 등)
├── build.gradle                              # 프로젝트 전체 설정
├── settings.gradle                           # 모듈 등록
└── README.md                                 # 현재 문서
```

## 🧠 주요 컴포넌트 설명

| 구성요소 | 역할 |
|---------|------|
| **DataStore** | 사용자 설정값, 로그인 토큰 등 로컬 key-value 저장 |
| **Repository** | DataStore + Network + DB를 통합하여 ViewModel에 전달 |
| **ViewModel** | UI 상태(State) 관리 및 로직 처리 |
| **HaruCoachApplication** | Hilt 초기화, 전역 Context, Analytics 등 초기 세팅 담당 |
| **DI (Hilt)** | Repository, DataSource, PreferenceManager 등 의존성 관리 |
| **UI (Screen)** | 사용자 인터페이스 및 이벤트 처리 담당 |
