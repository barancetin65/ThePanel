package com.thepanel.data.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.provider.Settings
import com.thepanel.data.model.MediaState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AndroidMediaControlService(
    private val context: Context
) : MediaControlService {
    private val mediaSessionManager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
    private val stateFlow = MutableStateFlow(currentState())
    private var lastController: MediaController? = null

    private val sessionCallback = object : MediaController.Callback() {
        override fun onPlaybackStateChanged(state: android.media.session.PlaybackState?) {
            stateFlow.value = currentState()
        }
        override fun onMetadataChanged(metadata: android.media.MediaMetadata?) {
            stateFlow.value = currentState()
        }
    }

    private val listener = MediaSessionManager.OnActiveSessionsChangedListener {
        updateController()
    }

    init {
        runCatching {
            mediaSessionManager.addOnActiveSessionsChangedListener(
                listener,
                ComponentName(context, PanelNotificationListenerService::class.java)
            )
            updateController()
        }
    }

    private fun updateController() {
        val controller = currentController()
        if (controller != lastController) {
            lastController?.unregisterCallback(sessionCallback)
            lastController = controller
            lastController?.registerCallback(sessionCallback)
            stateFlow.value = currentState()
        }
    }

    override fun activeMedia(): Flow<MediaState> = stateFlow.asStateFlow()

    override suspend fun playPause() {
        val controller = currentController()
        val state = controller?.playbackState?.state
        if (state == android.media.session.PlaybackState.STATE_PLAYING) {
            controller.transportControls.pause()
        } else {
            controller?.transportControls?.play()
        }
        stateFlow.value = currentState()
    }

    override suspend fun skipNext() {
        currentController()?.transportControls?.skipToNext()
        stateFlow.value = currentState()
    }

    override suspend fun skipPrevious() {
        currentController()?.transportControls?.skipToPrevious()
        stateFlow.value = currentState()
    }

    override suspend fun launchAssistant() {
        runCatching {
            val intent = Intent(Intent.ACTION_ASSIST).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    private fun currentController(): MediaController? {
        if (!notificationAccessEnabled()) return null
        val component = ComponentName(context, PanelNotificationListenerService::class.java)
        return runCatching { mediaSessionManager.getActiveSessions(component).firstOrNull() }.getOrNull()
    }

    private fun currentState(): MediaState {
        if (!notificationAccessEnabled()) {
            return MediaState(
                permissionRequired = true,
                title = "Bildirim eriÅŸimi gerekli",
                subtitle = "Spotify / YouTube kontrolÃ¼ iÃ§in eriÅŸim verin"
            )
        }
        val controller = currentController() ?: return MediaState(title = "Etkin medya yok")
        val metadata = controller.metadata
        val playbackState = controller.playbackState
        return MediaState(
            available = true,
            title = metadata?.description?.title?.toString().orEmpty().ifBlank { "Bilinmeyen parÃ§a" },
            subtitle = metadata?.description?.subtitle?.toString().orEmpty(),
            source = controller.packageName,
            playing = playbackState?.state == android.media.session.PlaybackState.STATE_PLAYING,
            progressLabel = "",
            artworkUrl = null,
            canSkip = true
        )
    }

    private fun notificationAccessEnabled(): Boolean {
        val enabled = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners").orEmpty()
        return enabled.contains(ComponentName(context, PanelNotificationListenerService::class.java).flattenToString())
    }
}
