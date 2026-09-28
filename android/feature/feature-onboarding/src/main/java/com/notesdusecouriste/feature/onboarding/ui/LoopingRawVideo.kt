package com.notesdusecouriste.feature.onboarding.ui

import android.content.Context
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.view.Surface
import android.view.TextureView
import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Vidéo muette en boucle. TextureView (et non SurfaceView) pour suivre le clip
 * et les transformations Compose (coins arrondis, rotation).
 */
@Composable
internal fun LoopingRawVideo(
    @RawRes videoRes: Int,
    playing: Boolean,
    modifier: Modifier = Modifier,
) {
    val holder = remember { LoopingPlayerHolder() }
    val currentPlaying by rememberUpdatedState(playing)

    LaunchedEffect(playing) { holder.setPlaying(playing) }
    DisposableEffect(Unit) { onDispose { holder.release() } }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextureView(context).apply {
                surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                    override fun onSurfaceTextureAvailable(
                        surface: SurfaceTexture,
                        width: Int,
                        height: Int,
                    ) {
                        holder.attach(context, videoRes, Surface(surface), currentPlaying)
                    }

                    override fun onSurfaceTextureSizeChanged(
                        surface: SurfaceTexture,
                        width: Int,
                        height: Int,
                    ) = Unit

                    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                        holder.release()
                        return true
                    }

                    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) = Unit
                }
            }
        },
    )
}

private class LoopingPlayerHolder {
    private var player: MediaPlayer? = null
    private var surface: Surface? = null

    fun attach(context: Context, @RawRes videoRes: Int, surface: Surface, playing: Boolean) {
        release()
        val created = MediaPlayer.create(context, videoRes) ?: run {
            surface.release()
            return
        }
        created.setSurface(surface)
        created.isLooping = true
        created.setVolume(0f, 0f)
        player = created
        this.surface = surface
        if (playing) created.start()
    }

    fun setPlaying(playing: Boolean) {
        val current = player ?: return
        if (playing) {
            current.seekTo(0)
            current.start()
        } else if (current.isPlaying) {
            current.pause()
        }
    }

    fun release() {
        player?.release()
        player = null
        surface?.release()
        surface = null
    }
}
