package com.azrosk.data.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth =
        FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFireStore(): FirebaseFirestore =
        FirebaseFirestore.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseStorage(): FirebaseStorage =
        FirebaseStorage.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseDatabase() : FirebaseDatabase =
        FirebaseDatabase.getInstance("https://sell-it-a3a71-default-rtdb.europe-west1.firebasedatabase.app")

    @Singleton
    @Provides
    fun provideFirebaseDatabaseRef(database: FirebaseDatabase) : DatabaseReference =
        database.reference

}