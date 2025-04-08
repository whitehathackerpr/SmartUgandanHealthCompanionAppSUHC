package com.the4codexlabs.smartugandanhealthcompanionappsuhc.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.CommunityGroup
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.CommunityPost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*
import com.google.firebase.Timestamp

class CommunityRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    
    private val userId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
        
    private val userName: String
        get() = auth.currentUser?.displayName ?: "Anonymous User"
    
    private val postsCollection
        get() = firestore.collection("posts")
    
    private val groupsCollection
        get() = firestore.collection("groups")
    
    private val userJoinedGroupsCollection
        get() = firestore.collection("users").document(userId).collection("joinedGroups")
    
    private val userLikedPostsCollection
        get() = firestore.collection("users").document(userId).collection("likedPosts")
    
    // Get all posts
    fun getPosts(): Flow<List<CommunityPost>> = callbackFlow {
        val subscription = postsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val posts = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(CommunityPost::class.java)?.copy(id = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    
                    // Get liked posts for current user - using a coroutine scope
                    CoroutineScope(Dispatchers.IO).launch {
                        val likedPostIds = try {
                            getLikedPostIds()
                        } catch (e: Exception) {
                            emptyList()
                        }
                        
                        val postsWithLikeStatus = posts.map { post ->
                            post.copy(isLiked = likedPostIds.contains(post.id))
                        }
                        
                        trySend(postsWithLikeStatus)
                    }
                }
            }
        
        awaitClose { subscription.remove() }
    }
    
    // Create a new post
    suspend fun createPost(content: String, tags: List<String>, imageUrls: List<String> = emptyList()): String {
        val postId = UUID.randomUUID().toString()
        val newPost = CommunityPost(
            id = postId,
            authorId = userId,
            authorName = userName,
            authorAvatar = auth.currentUser?.photoUrl?.toString(),
            content = content,
            timestamp = Date(),
            likes = 0,
            comments = 0,
            isLiked = false,
            tags = tags,
            images = imageUrls
        )
        
        postsCollection.document(postId).set(newPost).await()
        return postId
    }
    
    // Like/unlike a post
    suspend fun togglePostLike(postId: String, isLiked: Boolean) {
        // Update like status in user's liked posts collection
        if (isLiked) {
            userLikedPostsCollection.document(postId).set(mapOf(
                "postId" to postId,
                "timestamp" to Date()
            )).await()
            
            // Increment likes count in the post document
            firestore.runTransaction { transaction ->
                val postRef = postsCollection.document(postId)
                val post = transaction.get(postRef).toObject(CommunityPost::class.java)
                if (post != null) {
                    transaction.update(postRef, "likes", post.likes + 1)
                }
            }.await()
        } else {
            userLikedPostsCollection.document(postId).delete().await()
            
            // Decrement likes count in the post document
            firestore.runTransaction { transaction ->
                val postRef = postsCollection.document(postId)
                val post = transaction.get(postRef).toObject(CommunityPost::class.java)
                if (post != null && post.likes > 0) {
                    transaction.update(postRef, "likes", post.likes - 1)
                }
            }.await()
        }
    }
    
    // Get liked post IDs for current user
    private suspend fun getLikedPostIds(): List<String> {
        return try {
            val snapshot = userLikedPostsCollection.get().await()
            snapshot.documents.map { it.id }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    // Get all community groups
    fun getGroups(): Flow<List<CommunityGroup>> = callbackFlow {
        val subscription = groupsCollection
            .orderBy("name")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val groups = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(CommunityGroup::class.java)?.copy(id = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    
                    // Get joined groups for current user - using a coroutine scope
                    CoroutineScope(Dispatchers.IO).launch {
                        val joinedGroupIds = try {
                            getJoinedGroupIds()
                        } catch (e: Exception) {
                            emptyList()
                        }
                        
                        val groupsWithJoinStatus = groups.map { group ->
                            group.copy(isJoined = joinedGroupIds.contains(group.id))
                        }
                        
                        trySend(groupsWithJoinStatus)
                    }
                }
            }
        
        awaitClose { subscription.remove() }
    }
    
    // Create a new community group
    suspend fun createGroup(name: String, description: String, imageUrl: String? = null): String {
        val groupId = UUID.randomUUID().toString()
        val newGroup = CommunityGroup(
            id = groupId,
            name = name,
            description = description,
            memberCount = 1, // Creator is automatically a member
            imageUrl = imageUrl,
            isJoined = true,
            createdBy = userId,
            createdAt = Date()
        )
        
        // Save the group to Firestore
        groupsCollection.document(groupId).set(newGroup).await()
        
        // Add the creator as a member
        userJoinedGroupsCollection.document(groupId).set(mapOf(
            "groupId" to groupId,
            "joinedAt" to Date()
        )).await()
        
        return groupId
    }
    
    // Search groups by name
    fun searchGroups(query: String): Flow<List<CommunityGroup>> = callbackFlow {
        val subscription = groupsCollection
            .orderBy("name")
            .startAt(query)
            .endAt(query + '\uf8ff')
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val groups = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(CommunityGroup::class.java)?.copy(id = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }.filter { group ->
                        group.name.contains(query, ignoreCase = true) ||
                        group.description.contains(query, ignoreCase = true)
                    }
                    
                    // Get joined groups for current user - using a coroutine scope
                    CoroutineScope(Dispatchers.IO).launch {
                        val joinedGroupIds = try {
                            getJoinedGroupIds()
                        } catch (e: Exception) {
                            emptyList()
                        }
                        
                        val groupsWithJoinStatus = groups.map { group ->
                            group.copy(isJoined = joinedGroupIds.contains(group.id))
                        }
                        
                        trySend(groupsWithJoinStatus)
                    }
                }
            }
        
        awaitClose { subscription.remove() }
    }
    
    // Join/leave a group
    suspend fun toggleGroupJoin(groupId: String, isJoining: Boolean) {
        if (isJoining) {
            // Add to user's joined groups
            userJoinedGroupsCollection.document(groupId).set(mapOf(
                "groupId" to groupId,
                "joinedAt" to Date()
            )).await()
            
            // Increment member count in the group document
            firestore.runTransaction { transaction ->
                val groupRef = groupsCollection.document(groupId)
                val group = transaction.get(groupRef).toObject(CommunityGroup::class.java)
                if (group != null) {
                    transaction.update(groupRef, "memberCount", group.memberCount + 1)
                }
            }.await()
        } else {
            // Remove from user's joined groups
            userJoinedGroupsCollection.document(groupId).delete().await()
            
            // Decrement member count in the group document
            firestore.runTransaction { transaction ->
                val groupRef = groupsCollection.document(groupId)
                val group = transaction.get(groupRef).toObject(CommunityGroup::class.java)
                if (group != null && group.memberCount > 0) {
                    transaction.update(groupRef, "memberCount", group.memberCount - 1)
                }
            }.await()
        }
    }
    
    // Get joined group IDs for current user
    private suspend fun getJoinedGroupIds(): List<String> {
        return try {
            val snapshot = userJoinedGroupsCollection.get().await()
            snapshot.documents.map { it.id }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    // Get posts for a specific group
    fun getGroupPosts(groupId: String): Flow<List<CommunityPost>> = callbackFlow {
        val subscription = postsCollection
            .whereEqualTo("groupId", groupId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val posts = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(CommunityPost::class.java)?.copy(id = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    
                    // Get liked posts for current user - using a coroutine scope
                    CoroutineScope(Dispatchers.IO).launch {
                        val likedPostIds = try {
                            getLikedPostIds()
                        } catch (e: Exception) {
                            emptyList()
                        }
                        
                        val postsWithLikeStatus = posts.map { post ->
                            post.copy(isLiked = likedPostIds.contains(post.id))
                        }
                        
                        trySend(postsWithLikeStatus)
                    }
                }
            }
        
        awaitClose { subscription.remove() }
    }
    
    // Create a post in a specific group
    suspend fun createGroupPost(groupId: String, content: String, tags: List<String>, imageUrls: List<String> = emptyList()): String {
        val postId = UUID.randomUUID().toString()
        val newPost = CommunityPost(
            id = postId,
            authorId = userId,
            authorName = userName,
            authorAvatar = auth.currentUser?.photoUrl?.toString(),
            content = content,
            timestamp = Date(),
            likes = 0,
            comments = 0,
            isLiked = false,
            tags = tags,
            images = imageUrls,
            groupId = groupId
        )
        
        postsCollection.document(postId).set(newPost).await()
        return postId
    }
} 