package ar.com.develup.tateti.servicios

// TODO-07-NOTIFICATION
/*
class ServicioNotificaciones : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.data != null) {
            mostrarNotificacion(remoteMessage.data)
        }
    }

    private fun mostrarNotificacion(notificationData: Map<String, String>) {
        val intent = Intent(this, ActividadInicial::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val titulo = notificationData["title"]
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(titulo)
                .setContentText(notificationData["body"])
                .setStyle(NotificationCompat.BigTextStyle().bigText(notificationData["body"]))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }
}

 */