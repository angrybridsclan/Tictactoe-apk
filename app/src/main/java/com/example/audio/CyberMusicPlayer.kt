package com.example.audio

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * High-performance Background Music Player.
 * Streams and loops the custom anime/cyber soundtrack:
 * URL: https://files.catbox.moe/oeqfg8.mp3
 *
 * Fully supports lifecycle pause/resume, mute toggles, and seamless looping.
 */
object CyberMusicPlayer {
    private const val TAG = "CyberMusicPlayer"
    private const val MUSIC_STREAM_URL = "https://files.catbox.moe/oeqfg8.mp3"

    private val scope = CoroutineScope(Dispatchers.IO)
    private var mediaPlayer: MediaPlayer? = null
    private var isPlayingRequested = false
    private var isMuted = false
    private var isPrepared = false
    private var retryJob: Job? = null

    fun startMusic(enabled: Boolean) {
        isPlayingRequested = enabled
        isMuted = !enabled
        if (!enabled) {
            pauseMusic()
            return
        }
        initAndPlay()
    }

    fun setMuted(muted: Boolean) {
        isMuted = muted
        if (muted) {
            pauseMusic()
        } else {
            resumeMusic(true)
        }
    }

    fun pauseMusic() {
        isPlayingRequested = false
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error pausing MediaPlayer", e)
        }
    }

    fun resumeMusic(enabled: Boolean) {
        if (!enabled) {
            isMuted = true
            pauseMusic()
            return
        }
        isMuted = false
        isPlayingRequested = true

        if (mediaPlayer == null) {
            initAndPlay()
        } else if (isPrepared) {
            try {
                if (mediaPlayer?.isPlaying == false) {
                    mediaPlayer?.setVolume(0.85f, 0.85f)
                    mediaPlayer?.start()
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error resuming MediaPlayer", e)
                initAndPlay()
            }
        }
    }

    fun stopMusic() {
        isPlayingRequested = false
        retryJob?.cancel()
        retryJob = null
        try {
            mediaPlayer?.stop()
            mediaPlayer?.reset()
            mediaPlayer?.release()
        } catch (e: Exception) {
            Log.w(TAG, "Error stopping MediaPlayer", e)
        } finally {
            mediaPlayer = null
            isPrepared = false
        }
    }

    private fun initAndPlay() {
        retryJob?.cancel()
        scope.launch {
            try {
                // Clean up previous instance if any
                mediaPlayer?.let {
                    try {
                        it.reset()
                        it.release()
                    } catch (_: Exception) {}
                }
                isPrepared = false

                val player = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .build()
                    )
                    setDataSource(MUSIC_STREAM_URL)
                    isLooping = true
                    setVolume(0.85f, 0.85f)

                    setOnPreparedListener { mp ->
                        isPrepared = true
                        Log.i(TAG, "Background music prepared from $MUSIC_STREAM_URL")
                        if (isPlayingRequested && !isMuted) {
                            try {
                                mp.start()
                            } catch (e: Exception) {
                                Log.e(TAG, "Failed to start playback after prepared", e)
                            }
                        }
                    }

                    setOnErrorListener { _, what, extra ->
                        Log.w(TAG, "MediaPlayer error (what=$what, extra=$extra)")
                        isPrepared = false
                        scheduleRetry()
                        true
                    }

                    setOnCompletionListener { mp ->
                        if (isPlayingRequested && !isMuted) {
                            try {
                                mp.start()
                            } catch (_: Exception) {}
                        }
                    }
                }

                mediaPlayer = player
                player.prepareAsync()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize MediaPlayer for $MUSIC_STREAM_URL", e)
                scheduleRetry()
            }
        }
    }

    private fun scheduleRetry() {
        if (!isPlayingRequested || isMuted) return
        retryJob?.cancel()
        retryJob = scope.launch {
            delay(5000)
            if (isPlayingRequested && !isMuted) {
                Log.i(TAG, "Retrying background music stream...")
                initAndPlay()
            }
        }
    }
}
