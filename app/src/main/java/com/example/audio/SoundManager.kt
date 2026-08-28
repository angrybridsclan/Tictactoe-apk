package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlin.concurrent.thread
import kotlin.math.sin

class SoundManager(private val context: Context) {

    var isSoundEnabled: Boolean = true
    var isVibrationEnabled: Boolean = true

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    /**
     * Synthesizes a clean cyber beep/tone using AudioTrack.
     */
    private fun playTone(frequencyHz: Double, durationMs: Int, volume: Float = 0.8f) {
        if (!isSoundEnabled) return
        thread(start = true, isDaemon = true) {
            try {
                val sampleRate = 44100
                val numSamples = (durationMs * sampleRate / 1000)
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    // Sine wave with subtle decay envelope
                    val envelope = 1.0 - (i.toDouble() / numSamples)
                    val sample = (sin(2.0 * Math.PI * frequencyHz * time) * envelope * Short.MAX_VALUE * volume).toInt()
                    buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()

                val audioFormat = AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()

                val audioTrack = AudioTrack(
                    audioAttributes,
                    audioFormat,
                    buffer.size * 2,
                    AudioTrack.MODE_STATIC,
                    0
                )

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()
                Thread.sleep(durationMs.toLong() + 50)
                audioTrack.release()
            } catch (e: Exception) {
                // Ignore audio errors gracefully
            }
        }
    }

    fun playMoveSound(isX: Boolean) {
        if (isX) {
            playTone(frequencyHz = 880.0, durationMs = 60, volume = 0.6f) // High tech A5
        } else {
            playTone(frequencyHz = 660.0, durationMs = 60, volume = 0.6f) // Warm E5
        }
        vibrate(30)
    }

    fun playWinSound() {
        thread(start = true, isDaemon = true) {
            playTone(frequencyHz = 523.25, durationMs = 80, volume = 0.7f) // C5
            Thread.sleep(70)
            playTone(frequencyHz = 659.25, durationMs = 80, volume = 0.7f) // E5
            Thread.sleep(70)
            playTone(frequencyHz = 783.99, durationMs = 80, volume = 0.7f) // G5
            Thread.sleep(70)
            playTone(frequencyHz = 1046.50, durationMs = 200, volume = 0.9f) // C6
        }
        vibrate(120)
    }

    fun playDrawSound() {
        thread(start = true, isDaemon = true) {
            playTone(frequencyHz = 400.0, durationMs = 100, volume = 0.5f)
            Thread.sleep(90)
            playTone(frequencyHz = 300.0, durationMs = 150, volume = 0.5f)
        }
        vibrate(60)
    }

    fun playButtonClick() {
        playTone(frequencyHz = 1200.0, durationMs = 35, volume = 0.4f)
        vibrate(20)
    }

    private fun vibrate(milliseconds: Long) {
        if (!isVibrationEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(milliseconds)
            }
        } catch (e: Exception) {
            // Ignore vibration errors gracefully
        }
    }
}
