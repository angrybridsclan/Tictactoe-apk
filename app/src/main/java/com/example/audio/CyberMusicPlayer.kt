package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

/**
 * Background Cyber Music Player Engine.
 * Streams and loops the background music track from the provided URL:
 * https://files.catbox.moe/oeqfg8.mp3
 *
 * Includes automatic error-handling, offline synthesizer fallback,
 * background pause/resume, and volume muting.
 */
object CyberMusicPlayer {
    private const val TAG = "CyberMusicPlayer"
    const val BGM_STREAM_URL = "https://files.catbox.moe/oeqfg8.mp3"

    private val scope = CoroutineScope(Dispatchers.Default)
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var isMuted = false
    private var isPrepared = false
    private var isUsingFallback = false
    private var synthJob: Job? = null
    private var synthTrack: AudioTrack? = null

    // Fallback chord frequencies (Bm7 -> Gmaj7 -> Dmaj7 -> A)
    private val chordRoots = listOf(61.74f, 49.00f, 73.42f, 55.00f)
    private val chordVoices = listOf(
        listOf(123.47f, 246.94f, 293.66f, 369.99f, 440.00f), // Bm7
        listOf(98.00f,  196.00f, 246.94f, 293.66f, 369.99f), // Gmaj7
        listOf(146.83f, 220.00f, 277.18f, 369.99f, 440.00f), // Dmaj7
        listOf(110.00f, 220.00f, 277.18f, 329.63f, 392.00f)  // A7
    )
    private val melodyMotifs = listOf(
        listOf(739.99f, 659.25f, 587.33f, 493.88f, 587.33f, 739.99f, 880.00f, 739.99f),
        listOf(587.33f, 659.25f, 739.99f, 783.99f, 739.99f, 587.33f, 493.88f, 587.33f),
        listOf(880.00f, 739.99f, 659.25f, 587.33f, 739.99f, 880.00f, 1108.73f, 880.00f),
        listOf(659.25f, 739.99f, 659.25f, 587.33f, 554.37f, 493.88f, 554.37f, 659.25f)
    )

    fun startMusic(enabled: Boolean) {
        isMuted = !enabled
        if (isPlaying && enabled) {
            setMuted(false)
            return
        }
        isPlaying = enabled
        if (!enabled) return

        initializeMediaPlayer()
    }

    private fun initializeMediaPlayer() {
        scope.launch(Dispatchers.IO) {
            try {
                releaseMediaPlayer()
                stopFallbackSynth()

                val player = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    setDataSource(BGM_STREAM_URL)
                    isLooping = true

                    val vol = if (isMuted) 0.0f else 0.75f
                    setVolume(vol, vol)

                    setOnPreparedListener { mp ->
                        isPrepared = true
                        if (isPlaying && !isMuted) {
                            try {
                                mp.start()
                                isUsingFallback = false
                                Log.d(TAG, "Catbox MP3 stream started playing successfully")
                            } catch (e: Exception) {
                                Log.e(TAG, "Error starting MediaPlayer onPrepared", e)
                                startFallbackSynth()
                            }
                        }
                    }

                    setOnErrorListener { _, what, extra ->
                        Log.w(TAG, "MediaPlayer error ($what, $extra), switching to fallback synth")
                        isPrepared = false
                        startFallbackSynth()
                        true // Handled error
                    }

                    setOnCompletionListener {
                        // Loop restart safeguard
                        if (isPlaying && !isMuted) {
                            try {
                                start()
                            } catch (_: Exception) {}
                        }
                    }

                    prepareAsync()
                }

                mediaPlayer = player
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize MediaPlayer for URL $BGM_STREAM_URL", e)
                startFallbackSynth()
            }
        }
    }

    fun setMuted(muted: Boolean) {
        isMuted = muted
        scope.launch(Dispatchers.IO) {
            try {
                val player = mediaPlayer
                if (player != null && isPrepared) {
                    val vol = if (muted) 0.0f else 0.75f
                    player.setVolume(vol, vol)
                    if (muted) {
                        if (player.isPlaying) player.pause()
                    } else {
                        if (!player.isPlaying) player.start()
                    }
                } else if (!muted && isPlaying && !isPrepared) {
                    initializeMediaPlayer()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error setting mute state on MediaPlayer", e)
            }
        }
    }

    fun pauseMusic() {
        try {
            val player = mediaPlayer
            if (player != null && isPrepared && player.isPlaying) {
                player.pause()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error pausing MediaPlayer", e)
        }
    }

    fun resumeMusic(enabled: Boolean) {
        if (!enabled) {
            setMuted(true)
            return
        }
        isMuted = false
        isPlaying = true
        scope.launch(Dispatchers.IO) {
            try {
                val player = mediaPlayer
                if (player != null && isPrepared) {
                    val vol = 0.75f
                    player.setVolume(vol, vol)
                    if (!player.isPlaying) {
                        player.start()
                    }
                } else {
                    initializeMediaPlayer()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error resuming MediaPlayer", e)
                startFallbackSynth()
            }
        }
    }

    fun stopMusic() {
        isPlaying = false
        isPrepared = false
        releaseMediaPlayer()
        stopFallbackSynth()
    }

    private fun releaseMediaPlayer() {
        try {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    player.stop()
                }
                player.reset()
                player.release()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing MediaPlayer", e)
        } finally {
            mediaPlayer = null
            isPrepared = false
        }
    }

    // Offline / Network Error Fallback Synthesizer
    private fun startFallbackSynth() {
        if (isUsingFallback || !isPlaying || isMuted) return
        isUsingFallback = true

        synthJob?.cancel()
        synthJob = scope.launch(Dispatchers.Default) {
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
                    AudioManager.AUDIO_SESSION_ID_GENERATE
                )
                synthTrack = track
                track.play()

                var barIndex = 0
                val bpm = 85.0
                val barDurationSec = (60.0 / bpm) * 4.0
                val numSamples = (sampleRate * barDurationSec).toInt()
                val pcmBuffer = ByteArray(numSamples * 2)
                val noiseRandom = Random(12345)

                while (isActive && isPlaying && isUsingFallback) {
                    if (isMuted) {
                        delay(200)
                        continue
                    }

                    val sectionIndex = barIndex % chordRoots.size
                    val rootFreq = chordRoots[sectionIndex]
                    val chordNotes = chordVoices[sectionIndex]
                    val leadNotes = melodyMotifs[sectionIndex]
                    barIndex++

                    for (i in 0 until numSamples) {
                        val t = i.toDouble() / sampleRate
                        val beatTime = (t / barDurationSec) * 4.0
                        val beatFrac = beatTime % 1.0
                        val eighthIndex = (beatTime * 2.0).toInt().coerceIn(0, 7)
                        val eighthFrac = (beatTime * 2.0) % 1.0

                        var sampleSum = 0.0

                        // 1. Deep 808 Sub-Bass
                        val isKickTime = (beatTime < 0.9) || (beatTime in 2.5..3.4)
                        if (isKickTime) {
                            val kickTimeOffset = if (beatTime < 0.9) beatTime else (beatTime - 2.5)
                            val bassEnv = exp(-kickTimeOffset * 2.2).coerceIn(0.0, 1.0)
                            val bassWave = sin(2.0 * PI * rootFreq * t) +
                                    0.45 * sin(2.0 * PI * (rootFreq * 2.0) * t) +
                                    0.20 * sin(2.0 * PI * (rootFreq * 3.0) * t)
                            sampleSum += bassWave * bassEnv * 0.32
                        }

                        // 2. Chill Snare on Beats 2 and 4
                        val isSnareBeat = (beatTime.toInt() == 1 || beatTime.toInt() == 3)
                        if (isSnareBeat) {
                            val snareEnv = exp(-beatFrac * 6.5).coerceIn(0.0, 1.0)
                            if (snareEnv > 0.001) {
                                val noise = (noiseRandom.nextDouble() * 2.0 - 1.0)
                                val tone = sin(2.0 * PI * 220.0 * beatFrac)
                                sampleSum += (noise * 0.65 + tone * 0.35) * snareEnv * 0.22
                            }
                        }

                        // 3. Relaxing Hi-Hat
                        val hatEnv = exp(-eighthFrac * 14.0).coerceIn(0.0, 1.0)
                        if (hatEnv > 0.001) {
                            val hatNoise = (noiseRandom.nextDouble() * 2.0 - 1.0)
                            val hatVol = if (eighthIndex % 2 == 0) 0.10 else 0.06
                            sampleSum += hatNoise * hatEnv * hatVol
                        }

                        // 4. Rhodes Chords
                        val tremolo = 0.88 + 0.12 * sin(2.0 * PI * 3.5 * t)
                        val chordEnv = exp(-beatTime * 0.25).coerceIn(0.3, 1.0)
                        for (freq in chordNotes) {
                            val voicePhase = 2.0 * PI * freq * t
                            val rhodes = sin(voicePhase) +
                                    0.35 * sin(voicePhase * 2.0) +
                                    0.15 * sin(voicePhase * 3.0)
                            sampleSum += rhodes * 0.075 * tremolo * chordEnv
                        }

                        // 5. Plucked Melody Lead
                        val leadFreq = leadNotes[eighthIndex]
                        val leadEnv = exp(-eighthFrac * 3.8).coerceIn(0.0, 1.0)
                        val leadPhase = 2.0 * PI * leadFreq * t
                        val leadWave = sin(leadPhase) + 0.25 * sin(leadPhase * 2.0)
                        sampleSum += leadWave * leadEnv * 0.24

                        val masterVol = if (isMuted) 0.0 else 0.32
                        val preSample = sampleSum * masterVol
                        val clipped = when {
                            preSample > 0.92 -> 0.92 + (preSample - 0.92) * 0.1
                            preSample < -0.92 -> -0.92 + (preSample + 0.92) * 0.1
                            else -> preSample
                        }

                        val finalSample = (clipped * 32767.0).toInt().coerceIn(-32768, 32767).toShort()
                        val idx = i * 2
                        pcmBuffer[idx] = (finalSample.toInt() and 0x00ff).toByte()
                        pcmBuffer[idx + 1] = ((finalSample.toInt() and 0xff00) ushr 8).toByte()
                    }

                    if (track.playState == AudioTrack.PLAYSTATE_PLAYING) {
                        track.write(pcmBuffer, 0, pcmBuffer.size)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in fallback synth", e)
            } finally {
                stopFallbackSynth()
            }
        }
    }

    private fun stopFallbackSynth() {
        isUsingFallback = false
        synthJob?.cancel()
        synthJob = null
        try {
            synthTrack?.stop()
            synthTrack?.release()
        } catch (_: Exception) {}
        synthTrack = null
    }
}


