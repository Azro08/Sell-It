package com.azrosk.domain.usecase

import com.azrosk.domain.repository.AuthRepository
import com.azrosk.domain.repository.UsersRepository

class DeleteAccountUseCase(
    private val usersRepository: UsersRepository,
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(uid: String): String {
        authRepository.deleteAccount(uid).let {
            return if (it == "Done") {
                usersRepository.deleteUser(uid)
                usersRepository.deleteUsersProducts(uid)
                "Done"
            } else "Error"
        }
    }


}