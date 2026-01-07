package com.ora.app.core.di

import com.ora.app.presentation.features.auth.AuthViewModel
import com.ora.app.presentation.features.chat.ChatViewModel
import com.ora.app.presentation.features.profile.UserProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { AuthViewModel(get(), get()) }
    viewModel { ChatViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { UserProfileViewModel(get(), get(), get(), get()) }
}
