package com.azrosk.data.di

import com.azrosk.data.api.UserService
import com.azrosk.data.repository.AuthRepositoryImpl
import com.azrosk.domain.repository.AuthRepository
import com.azrosk.domain.usecase.LoginUseCase
import com.azrosk.domain.usecase.RegisterUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object DomainModule {

    @Provides
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore,
        api: UserService
    ): AuthRepository = AuthRepositoryImpl(
        firebaseAuth, firestore, api
    )

    @Provides
    fun provideRegisterUseCase(repository: AuthRepository): RegisterUseCase =
        RegisterUseCase(repository)

    @Provides
    fun provideLoginUseCase(repository: AuthRepository): LoginUseCase =
        LoginUseCase(repository)

}