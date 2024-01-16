package com.ta.ittpizen.data.remote.service

import com.haroldadmin.cnradapter.NetworkResponse
import com.ta.ittpizen.data.remote.request.auth.LoginRequest
import com.ta.ittpizen.data.remote.request.auth.RegisterRequest
import com.ta.ittpizen.data.remote.response.CommonErrorResponse
import com.ta.ittpizen.data.remote.response.CommonResponse
import com.ta.ittpizen.data.remote.response.PagedCommonResponse
import com.ta.ittpizen.data.remote.response.auth.LoginResponse
import com.ta.ittpizen.data.remote.response.auth.RegisterResponse
import com.ta.ittpizen.data.remote.response.post.CreatePostCommentResponse
import com.ta.ittpizen.data.remote.response.post.PostCommentResponse
import com.ta.ittpizen.data.remote.response.post.PostResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface IttpizenService {

    @POST("user/login")
    suspend fun login(
        @Body request: LoginRequest
    ): NetworkResponse<CommonResponse<LoginResponse>, CommonErrorResponse>

    @POST("user/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): NetworkResponse<CommonResponse<RegisterResponse>, CommonErrorResponse>

    @GET("post")
    suspend fun getAllPost(
        @Header("Authorization")
        authorization: String,
        @Query("type")
        type: String,
        @Query("page")
        page: Int,
        @Query("size")
        size: Int
    ): NetworkResponse<PagedCommonResponse<List<PostResponse>>, CommonErrorResponse>

    @GET("post/user/{userId}")
    suspend fun getPostByUser(
        @Header("Authorization")
        authorization: String,
        @Path("userId")
        userId: String
    ): NetworkResponse<PagedCommonResponse<List<PostResponse>>, CommonErrorResponse>

    @GET("post/{postId}")
    suspend fun getPostById(
        @Header("Authorization")
        authorization: String,
        @Path("postId")
        postId: String
    ): NetworkResponse<CommonResponse<PostResponse>, CommonErrorResponse>

    @GET("post/comment/{postId}")
    suspend fun getPostComment(
        @Header("Authorization")
        authorization: String,
        @Path("postId")
        postId: String
    ): NetworkResponse<CommonResponse<List<PostCommentResponse>>, CommonErrorResponse>

    @FormUrlEncoded
    @POST("post/comment/{postId}")
    suspend fun createPostComment(
        @Header("Authorization")
        authorization: String,
        @Path("postId")
        postId: String,
        @Field("comment")
        comment: String
    ): NetworkResponse<CommonResponse<CreatePostCommentResponse>, CommonErrorResponse>

    @POST("post/like/{postId}")
    suspend fun createPostLike(
        @Header("Authorization")
        authorization: String,
        @Path("postId")
        postId: String,
    ): NetworkResponse<CommonResponse<String>, CommonErrorResponse>

    @DELETE("post/like/{postId}")
    suspend fun deletePostLike(
        @Header("Authorization")
        authorization: String,
        @Path("postId")
        postId: String,
    ): NetworkResponse<CommonResponse<String>, CommonErrorResponse>

}