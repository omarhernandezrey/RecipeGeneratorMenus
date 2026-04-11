package com.example.recipe_generator.data.remote

import com.google.gson.annotations.SerializedName

data class YouTubeSearchResponse(
    @SerializedName("kind")
    val kind: String? = null,
    @SerializedName("etag")
    val etag: String? = null,
    @SerializedName("nextPageToken")
    val nextPageToken: String? = null,
    @SerializedName("prevPageToken")
    val prevPageToken: String? = null,
    @SerializedName("regionCode")
    val regionCode: String? = null,
    @SerializedName("pageInfo")
    val pageInfo: YouTubePageInfo? = null,
    @SerializedName("items")
    val items: List<YouTubeSearchItem>? = null
)

data class YouTubePageInfo(
    @SerializedName("totalResults")
    val totalResults: Int? = null,
    @SerializedName("resultsPerPage")
    val resultsPerPage: Int? = null
)

data class YouTubeSearchItem(
    @SerializedName("kind")
    val kind: String? = null,
    @SerializedName("etag")
    val etag: String? = null,
    @SerializedName("id")
    val id: YouTubeVideoId? = null,
    @SerializedName("snippet")
    val snippet: YouTubeSnippet? = null
)

data class YouTubeVideoId(
    @SerializedName("kind")
    val kind: String? = null,
    @SerializedName("videoId")
    val videoId: String? = null,
    @SerializedName("channelId")
    val channelId: String? = null,
    @SerializedName("playlistId")
    val playlistId: String? = null
)

data class YouTubeSnippet(
    @SerializedName("publishedAt")
    val publishedAt: String? = null,
    @SerializedName("channelId")
    val channelId: String? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("thumbnails")
    val thumbnails: YouTubeThumbnails? = null,
    @SerializedName("channelTitle")
    val channelTitle: String? = null,
    @SerializedName("liveBroadcastContent")
    val liveBroadcastContent: String? = null,
    @SerializedName("publishTime")
    val publishTime: String? = null
)

data class YouTubeThumbnails(
    @SerializedName("default")
    val default: YouTubeThumbnail? = null,
    @SerializedName("medium")
    val medium: YouTubeThumbnail? = null,
    @SerializedName("high")
    val high: YouTubeThumbnail? = null,
    @SerializedName("standard")
    val standard: YouTubeThumbnail? = null,
    @SerializedName("maxres")
    val maxres: YouTubeThumbnail? = null
)

data class YouTubeThumbnail(
    @SerializedName("url")
    val url: String? = null,
    @SerializedName("width")
    val width: Int? = null,
    @SerializedName("height")
    val height: Int? = null
)
