package com.harucoach.harucoachfront.viewmodel

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.*
import java.util.Locale

class VoiceRecognitionViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    var recordedText = mutableStateOf("")
    var errorMessage = mutableStateOf("")
    var isListening = mutableStateOf(false)
    var btnState = mutableStateOf(1) // 1=말하기, 2=대기, 3=종료

    private var speechRecognizer: SpeechRecognizer? = null
    private var tts: TextToSpeech? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val speechRecognizerIntent: Intent by lazy {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toLanguageTag())
            //putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 3000)
            //putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 3000)
            //putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 10000)
        }
    }

    init {
        setupSpeechRecognizer()
        setupTTS()
    }

    private fun setupSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    Log.d("STT", "🎤 onReadyForSpeech")
                    errorMessage.value = ""
                    isListening.value = true
                }

                override fun onBeginningOfSpeech() {
                    Log.d("STT", "🎙️ 사용자가 말하기 시작")
                }

                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {
                    Log.d("STT", "🛑 onEndOfSpeech")
                }

                override fun onError(error: Int) {
                    isListening.value = false
                    val msg = when (error) {
                        SpeechRecognizer.ERROR_AUDIO -> "오디오 오류"
                        SpeechRecognizer.ERROR_CLIENT -> "클라이언트 오류"
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "권한 부족"
                        SpeechRecognizer.ERROR_NETWORK -> "네트워크 오류"
                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "네트워크 시간 초과"
                        SpeechRecognizer.ERROR_NO_MATCH -> "일치하는 결과 없음"
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "음성 인식기 사용 중"
                        SpeechRecognizer.ERROR_SERVER -> "서버 오류"
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "입력 시간 초과"
                        else -> "알 수 없는 오류: $error"
                    }
                    errorMessage.value = msg
                    Log.e("STT", "❌ $msg")
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        recordedText.value = matches[0]
                        Log.d("STT", "✅ 인식 결과: ${matches[0]}")

                        // 👇 0.3초 지연 후 btnState 변경 (UI 안전하게 리컴포즈)
                        coroutineScope.launch {
                            delay(300)
                            btnState.value = 3
                            isListening.value = false
                        }
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        recordedText.value = matches[0]
                    }
                }

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
    }

    private fun setupTTS() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.KOREAN
                Log.d("TTS", "✅ 초기화 완료")
            } else {
                Log.e("TTS", "❌ 초기화 실패")
            }
        }
    }

    fun startListening() {
        Log.d("STT", "🎤 startListening 호출됨")
        recordedText.value = ""
        isListening.value = true
        errorMessage.value = ""
        speechRecognizer?.startListening(speechRecognizerIntent)
    }

    fun stopListening() {
        Log.d("STT", "🛑 stopListening 호출됨")
        isListening.value = false
        speechRecognizer?.stopListening()
    }

    fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts_id")
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizer?.destroy()
        tts?.stop()
        tts?.shutdown()
    }
}
