package com.the4codexlabs.smartugandanhealthcompanionappsuhc.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.ArticleCategory
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.HealthArticle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class HealthEducationRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    
    private val userId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
    
    private val articlesCollection
        get() = firestore.collection("articles")
    
    private val bookmarksCollection
        get() = firestore.collection("users").document(userId).collection("bookmarks")
    
    // Get all articles
    fun getArticles(category: ArticleCategory? = null): Flow<List<HealthArticle>> = callbackFlow {
        val query = if (category != null) {
            articlesCollection.whereEqualTo("category", category.name)
        } else {
            articlesCollection
        }
        
        val subscription = query
            .orderBy("publishDate", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val articles = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(HealthArticle::class.java)?.copy(id = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    
                    // Get bookmarked status for each article - using a coroutine scope
                    CoroutineScope(Dispatchers.IO).launch {
                        val bookmarkedArticles = try {
                            getBookmarkedArticleIds()
                        } catch (e: Exception) {
                            emptyList()
                        }
                        
                        val articlesWithBookmarkStatus = articles.map { article ->
                            article.copy(isBookmarked = bookmarkedArticles.contains(article.id))
                        }
                        trySend(articlesWithBookmarkStatus)
                    }
                }
            }
        
        awaitClose { subscription.remove() }
    }
    
    // Get featured articles
    fun getFeaturedArticles(): Flow<List<HealthArticle>> = callbackFlow {
        val subscription = articlesCollection
            .whereEqualTo("isFeatured", true)
            .orderBy("publishDate", Query.Direction.DESCENDING)
            .limit(5)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val articles = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(HealthArticle::class.java)?.copy(id = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    
                    // Get bookmarked status for each article - using a coroutine scope
                    CoroutineScope(Dispatchers.IO).launch {
                        val bookmarkedArticles = try {
                            getBookmarkedArticleIds()
                        } catch (e: Exception) {
                            emptyList()
                        }
                        
                        val articlesWithBookmarkStatus = articles.map { article ->
                            article.copy(isBookmarked = bookmarkedArticles.contains(article.id))
                        }
                        trySend(articlesWithBookmarkStatus)
                    }
                }
            }
        
        awaitClose { subscription.remove() }
    }
    
    // Get article by ID
    suspend fun getArticleById(articleId: String): HealthArticle? {
        return try {
            val doc = articlesCollection.document(articleId).get().await()
            doc.toObject(HealthArticle::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }
    
    // Search articles
    fun searchArticles(query: String): Flow<List<HealthArticle>> = callbackFlow {
        val subscription = articlesCollection
            .orderBy("title")
            .startAt(query)
            .endAt(query + '\uf8ff')
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val articles = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(HealthArticle::class.java)?.copy(id = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }.filter { article ->
                        article.title.contains(query, ignoreCase = true) ||
                        article.summary.contains(query, ignoreCase = true)
                    }
                    
                    // Get bookmarked status for each article - using a coroutine scope
                    CoroutineScope(Dispatchers.IO).launch {
                        val bookmarkedArticles = try {
                            getBookmarkedArticleIds()
                        } catch (e: Exception) {
                            emptyList()
                        }
                        
                        val articlesWithBookmarkStatus = articles.map { article ->
                            article.copy(isBookmarked = bookmarkedArticles.contains(article.id))
                        }
                        trySend(articlesWithBookmarkStatus)
                    }
                }
            }
        
        awaitClose { subscription.remove() }
    }
    
    // Toggle bookmark status
    suspend fun toggleBookmark(articleId: String, isBookmarked: Boolean) {
        if (isBookmarked) {
            // Add bookmark
            bookmarksCollection.document(articleId).set(mapOf(
                "articleId" to articleId,
                "timestamp" to Date()
            )).await()
        } else {
            // Remove bookmark
            bookmarksCollection.document(articleId).delete().await()
        }
    }
    
    // Get bookmarked article IDs
    private suspend fun getBookmarkedArticleIds(): List<String> {
        return try {
            val snapshot = bookmarksCollection.get().await()
            snapshot.documents.map { it.id }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    // Get bookmarked articles
    fun getBookmarkedArticles(): Flow<List<HealthArticle>> = callbackFlow {
        val subscription = bookmarksCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val articleIds = snapshot.documents.map { it.id }
                    
                    if (articleIds.isEmpty()) {
                        trySend(emptyList())
                        return@addSnapshotListener
                    }
                    
                    // Get articles for these IDs - using a coroutine scope
                    CoroutineScope(Dispatchers.IO).launch {
                        val articles = mutableListOf<HealthArticle>()
                        for (id in articleIds) {
                            val article = try {
                                getArticleById(id)
                            } catch (e: Exception) {
                                null
                            }
                            if (article != null) {
                                articles.add(article.copy(isBookmarked = true))
                            }
                        }
                        trySend(articles)
                    }
                }
            }
        
        awaitClose { subscription.remove() }
    }
} 