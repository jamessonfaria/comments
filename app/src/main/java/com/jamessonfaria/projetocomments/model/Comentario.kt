package com.jamessonfaria.projetocomments.model

import com.google.gson.annotations.SerializedName

data class Comentario(@SerializedName("id") var id: Int,
                      @SerializedName("user") var user: String,
                      @SerializedName("content") var content: String,
                      @SerializedName("created_at") var created_at: String,
                      @SerializedName("image") var image: String,
                      @SerializedName("uploaded_image") var uploaded_image: String,
                      @SerializedName("lat") var lat: String,
                      @SerializedName("lng") var lng: String) {

}