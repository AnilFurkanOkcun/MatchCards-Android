package com.anilokcun.matchcards.data.repository

import com.anilokcun.matchcards.data.model.dto.LeaderDTO
import com.anilokcun.matchcards.data.model.dto.UserDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class ApiRepository {

	private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
	private val firebaseDatabase by lazy { FirebaseDatabase.getInstance() }
	private val firebaseStorage by lazy { FirebaseStorage.getInstance() }

	private fun String.getEmail() = "$this@matchcards.com"

	private fun getAvatarPath(currentUserUid: String) = "avatars/$currentUserUid.jpg"

	private val dbUsersFolderName = "users"

	private val dbUsersLastScoreKey = "lastScore"

	private val dbUsersHighScoreKey = "highScore"

	private val dbLeadersFolderName = "leaders"

	/* Register */
	fun createUserWithNicknameAndPassword(nickname: String, password: String) =
		firebaseAuth.createUserWithEmailAndPassword(nickname.getEmail(), password)

	fun putBytesToCurrentUserAvatarImage(currentUserUid: String, data: ByteArray) =
		firebaseStorage.reference.child(getAvatarPath(currentUserUid)).putBytes(data)

	/* Login */
	fun signInWithNicknameAndPassword(nickname: String, password: String) =
		firebaseAuth.signInWithEmailAndPassword(nickname.getEmail(), password)

	/* Sign out */
	fun signOut() = firebaseAuth.signOut()

	/* User */
	fun getCurrentUser() = firebaseAuth.currentUser

	fun getUser(uid: String) = firebaseDatabase.reference.child(dbUsersFolderName).child(uid)

	fun saveUser(userDto: UserDTO) =
		firebaseDatabase.reference.child(dbUsersFolderName).child(userDto.id).setValue(userDto)

	/* Score */
	fun saveScoreToUserLastScore(currentUserUid: String, score: Int) =
		firebaseDatabase.reference
			.child(dbUsersFolderName)
			.child(currentUserUid)
			.child(dbUsersLastScoreKey)
			.setValue(score)

	fun saveScoreToUserHighScore(currentUserUid: String, score: Int) = firebaseDatabase.reference
		.child(dbUsersFolderName)
		.child(currentUserUid)
		.child(dbUsersHighScoreKey)
		.setValue(score)

	fun saveLeader(currentUserUid: String, leaderDTO: LeaderDTO) = firebaseDatabase.reference
		.child(dbLeadersFolderName)
		.child(currentUserUid)
		.setValue(leaderDTO)

	/* Leaders */
	fun getLeaders(leadersCountLimit: Int) =
		firebaseDatabase.reference.child(dbLeadersFolderName).orderByChild(dbUsersHighScoreKey).limitToLast(
			leadersCountLimit
		)

}