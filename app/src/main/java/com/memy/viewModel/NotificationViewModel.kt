package com.memy.viewModel

import com.memy.retrofit.NotificationRepository

class NotificationViewModel: AppBaseViewModel() {
    val notificationRepository : NotificationRepository = NotificationRepository()

}