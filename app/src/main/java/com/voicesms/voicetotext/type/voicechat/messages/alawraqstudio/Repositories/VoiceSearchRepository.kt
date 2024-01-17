package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Repositories

import android.content.Context
import android.util.Log
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ModelClasses.CategoryModel
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.R

class VoiceSearchRepository(val context: Context) {


    fun getListofCategories(categoryName: String?, callback:(ArrayList<CategoryModel>)->Unit) {

        var listOfCategories = ArrayList<CategoryModel>()

        val categoryMap = mapOf(
            context.getString(R.string.communication_title) to listOf(
                CategoryModel(context.getString(R.string.reddit), R.drawable.reddit_icon),
                CategoryModel(context.getString(R.string.quora), R.drawable.quora_icon),
                CategoryModel(context.getString(R.string.flipboard), R.drawable.flipboard_icon),
                CategoryModel(context.getString(R.string.msn), R.drawable.msn_icon)
            ),
            context.getString(R.string.shopping_title) to listOf(
                CategoryModel(context.getString(R.string.alibaba), R.drawable.alibaba_icon),
                CategoryModel(context.getString(R.string.amazon), R.drawable.amazon_icon),
                CategoryModel(context.getString(R.string.daraz), R.drawable.daraz_icon),
                CategoryModel(context.getString(R.string.olx), R.drawable.olx_icon),
                CategoryModel(context.getString(R.string.ebay), R.drawable.ebay_icon),
                CategoryModel(context.getString(R.string.aliexpress), R.drawable.aliexpress_icon)
            ),
            context.getString(R.string.social_title) to listOf(
                CategoryModel(context.getString(R.string.youtube), R.drawable.youtube_icon),
                CategoryModel(context.getString(R.string.insta), R.drawable.insta_icon),
                CategoryModel(context.getString(R.string.pintrest), R.drawable.pintrest_icon),
                CategoryModel(context.getString(R.string.facebook), R.drawable.facebook_icon),
                CategoryModel(context.getString(R.string.twitter), R.drawable.twitter_icon),
                CategoryModel(context.getString(R.string.tiktok), R.drawable.tiktok_icon)
            ),
            context.getString(R.string.searchEngine_title) to listOf(
                CategoryModel(context.getString(R.string.google), R.drawable.google_icon),
                CategoryModel(context.getString(R.string.bing), R.drawable.bing_icon),
                CategoryModel(context.getString(R.string.duckgo), R.drawable.duckgo_icon),
                CategoryModel(context.getString(R.string.yahoo), R.drawable.yahoo_icon),
                CategoryModel(context.getString(R.string.wikipedia), R.drawable.wikipedia_icon),
            ),
            context.getString(R.string.more_title) to listOf(
                CategoryModel(context.getString(R.string.playstore), R.drawable.playstore_icon),
                CategoryModel(context.getString(R.string.weather), R.drawable.weather_icon),
                CategoryModel(context.getString(R.string.location), R.drawable.location_icon),
                CategoryModel(context.getString(R.string.imdb), R.drawable.imdb_icon),
            )

        )

        categoryName?.let {
            if (categoryMap.containsKey(it)) {
                listOfCategories.addAll(categoryMap[it]!!)
            }
        }

        callback(listOfCategories)
    }


}