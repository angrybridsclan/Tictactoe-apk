package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

object CyberMusicPlayer {
    private const val TAG = "CyberMusicPlayer"
    private val scope = CoroutineScope(Dispatchers.Default)
    private var musicJob: Job? = null
    private var isPlaying = false
    private var isMuted = false
    private var audioTrack: AudioTrack? = null

    // Ambient Synth Chord Progression (A minor -> F maj -> C maj -> G maj)
    // Frequencies in Hz:
    // Chord 1 (Am): A3 (220.0), C4 (261.63), E4 (329.63), A2 Bass (110.0)
    // Chord 2 (F):  F3 (174.61), A3 (220.0), C4 (261.63), F2 Bass (87.31)
    // Chord 3 (C):  C3 (130.81), E3 (164.81), G3 (196.00), C2 Bass (65.41)
    // Chord 4 (G):  G3 (196.00), B3 (246.94), D4 (293.66), G2 Bass (98.00)

    private val chords = listOf(
        listOf(110.0f, 220.00f, 261.63f, 329.63f, 523.25f), // Am9
        listOf(87.31f, 174.61f, 220.00f, 261.63f, 440.00f), // Fmaj7
        listOf(65.41f, 130.81f, 164.81f, 196.00f, 392.00f), // Cmaj9
        listOf(98.00f, 196.00f, 246.94f, 293.66f, 587.33f)  // Gsus4
    )

    fun startMusic(enabled: Boolean) {
        isMuted = !enabled
        if (isPlaying || !enabled) return
        isPlaying = true

        musicJob?.cancel()
        musicJob = scope.launch {
            try {
                val sampleRate = 22050
                val minBufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )

                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()

                val format = AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()

                val track = AudioTrack(
                    audioAttributes,
                    format,
                    minBufferSize * 2,
                    AudioTrack.MODE_STREAM,
                    android.media.AudioManager.AUDIO_SESSION_ID_GENERATE
                )
                audioTrack = track
                track.play()

                var chordIndex = 0
                val chordDurationSec = 3.2
                val numSamples = (sampleRate * chordDurationSec).toInt()
                val pcmBuffer = ByteArray(numSamples * 2)

                while (isActive && isPlaying) {
                    if (isMuted) {
                        delay(200)
                        continue
                    }

                    val chordFreqs = chords[chordIndex % chords.size]
                    chordIndex++

                    // Synthesize warm analog synth pad with low-pass style envelope
                    for (i in 0 until numSamples) {
                        val t = i.toDouble() / sampleRate
                        val progress = i.toDouble() / numSamples

                        // Soft fade-in and fade-out envelope
                        val envelope = when {
                            progress < 0.20 -> (progress / 0.20)
                            progress > 0.80 -> ((1.0 - progress) / 0.20)
                            else -> 1.0
                        }

                        // Combine chord voices + subtle sub-bass warmth + slow LFO tremolo
                        val lfo = 0.85 + 0.15 * sin(2.0 * Math.PI * 1.5 * t)
                        var sampleSum = 0.0

                        // Bass voice
                        sampleSum += sin(2.0 * Math.PI * chordFreqs[0] * t) * 0.35
                        // Chord pads
                        for (v in 1 until chordFreqs.size) {
                            sampleSum += sin(2.0 * Math.PI * chordFreqs[v] * t) * 0.18
                            // Gentle detuned unison for rich chorus effect
                            sampleSum += sin(2.0 * Math.PI * (chordFreqs[v] * 1.004) * t) * 0.10
                        }

                        // Soft 16th note ambient cyber arp ping
                        val arpStep = ((t * 4.0).toInt()) % (chordFreqs.size - 1) + 1
                        val arpFlt = chordFreqs[arpStep] * 2.0
                        val arpEnv = (1.0 - ((t * 4.0) % 1.0)).coerceIn(0.0, 1.0)
                        sampleSum += sin(2.0 * Math.PI * arpFlt * t) * (arpEnv * 0.12)

                        val masterVol = if (isMuted) 0.0 else 0.24
                        val finalSample = (sampleSum * envelope * lfo * masterVol * 32767.0).toInt().coerceIn(-32768, 32767).toShort()

                        val idx = i * 2
                        pcmBuffer[idx] = (finalSample.toInt() and 0x00ff).toByte()
                        pcmBuffer[idx + 1] = ((finalSample.toInt() and 0xff00) ushr 8).toByte()
                    }

                    if (track.playState == AudioTrack.PLAYSTATE_PLAYING) {
                        track.write(pcmBuffer, 0, pcmBuffer.size)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error playing background music", e)
            } finally {
                try {
                    audioTrack?.stop()
                    audioTrack?.release()
                } catch (_: Exception) {}
                audioTrack = null
            }
        }
    }

    fun setMuted(muted: Boolean) {
        isMuted = muted
        if (!muted && !isPlaying) {
            startMusic(true)
        }
    }

    fun pauseMusic() {
        isMuted = true
    }

    fun resumeMusic(enabled: Boolean) {
        if (enabled) {
            isMuted = false
            if (!isPlaying) {
                startMusic(true)
            }
        }
    }

    fun stopMusic() {
        isPlaying = false
        musicJob?.cancel()
        musicJob = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (_: Exception) {}
        audioTrack = null
    }
}
