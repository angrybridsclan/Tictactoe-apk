package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

class NeonSoundManager(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Default)

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    fun playMoveSound(isO: Boolean, enabled: Boolean) {
        if (!enabled) return
        scope.launch {
            if (isO) {
                // High futuristic neon blip (880Hz -> 1200Hz)
                playFrequencySweep(startFreq = 880f, endFreq = 1320f, durationMs = 80, volume = 0.45f)
            } else {
                // Punchy futuristic laser pop (580Hz -> 320Hz)
                playFrequencySweep(startFreq = 660f, endFreq = 440f, durationMs = 80, volume = 0.45f)
            }
        }
    }

    fun playWinSound(enabled: Boolean) {
        if (!enabled) return
        scope.launch {
            // Neon victorious triad arpeggio
            val notes = listOf(523.25f, 659.25f, 783.99f, 1046.50f)
            for (freq in notes) {
                playTone(freq, 90, 0.5f)
                kotlinx.coroutines.delay(65)
            }
        }
    }

    fun playDrawSound(enabled: Boolean) {
        if (!enabled) return
        scope.launch {
            playTone(330f, 120, 0.4f)
            kotlinx.coroutines.delay(100)
            playTone(260f, 180, 0.4f)
        }
    }

    fun playButtonClick(enabled: Boolean) {
        if (!enabled) return
        scope.launch {
            playTone(980f, 35, 0.35f)
        }
    }

    fun triggerVibrate(view: View? = null, durationMs: Long = 25, enabled: Boolean = true) {
        if (!enabled) return
        try {
            if (view != null) {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(durationMs)
                }
            }
        } catch (_: Exception) {}
    }

    fun triggerWinVibrate(enabled: Boolean = true) {
        if (!enabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 80, 50, 80, 50, 150)
                val amplitudes = intArrayOf(0, 180, 0, 220, 0, 255)
                vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(250)
            }
        } catch (_: Exception) {}
    }

    private fun playTone(frequency: Float, durationMs: Int, volume: Float) {
        val sampleRate = 22050
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val generatedSnd = ByteArray(2 * numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            // Envelope to avoid click
            val envelope = when {
                i < numSamples * 0.1 -> (i / (numSamples * 0.1))
                i > numSamples * 0.7 -> ((numSamples - i) / (numSamples * 0.3))
                else -> 1.0
            }
            val sample = (sin(2.0 * Math.PI * frequency * t) * envelope * volume * 32767).toInt().toShort()
            val idx = 2 * i
            generatedSnd[idx] = (sample.toInt() and 0x00ff).toByte()
            generatedSnd[idx + 1] = ((sample.toInt() and 0xff00) ushr 8).toByte()
        }

        playPcmData(generatedSnd, sampleRate)
    }

    private fun playFrequencySweep(startFreq: Float, endFreq: Float, durationMs: Int, volume: Float) {
        val sampleRate = 22050
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val generatedSnd = ByteArray(2 * numSamples)

        var phase = 0.0
        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val currentFreq = startFreq + (endFreq - startFreq) * progress
            phase += 2.0 * Math.PI * currentFreq / sampleRate
            val envelope = when {
                i < numSamples * 0.1 -> (i / (numSamples * 0.1))
                i > numSamples * 0.7 -> ((numSamples - i) / (numSamples * 0.3))
                else -> 1.0
            }
            val sample = (sin(phase) * envelope * volume * 32767).toInt().toShort()
            val idx = 2 * i
            generatedSnd[idx] = (sample.toInt() and 0x00ff).toByte()
            generatedSnd[idx + 1] = ((sample.toInt() and 0xff00) ushr 8).toByte()
        }

        playPcmData(generatedSnd, sampleRate)
    }

    private fun playPcmData(pcmData: ByteArray, sampleRate: Int) {
        try {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val audioFormat = AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()

            val track = AudioTrack(
                audioAttributes,
                audioFormat,
                pcmData.size,
                AudioTrack.MODE_STATIC,
                android.media.AudioManager.AUDIO_SESSION_ID_GENERATE
            )

            track.write(pcmData, 0, pcmData.size)
            track.play()
            // Release track after playback
            scope.launch {
                kotlinx.coroutines.delay(400)
                try {
                    track.stop()
                    track.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {}
    }
}
